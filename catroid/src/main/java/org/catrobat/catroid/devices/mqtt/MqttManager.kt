/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.devices.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class MqttManager(
    private val clientFactory: MqttClientFactory = DefaultMqttClientFactory,
    private val publishExecutor: Executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "MqttPublish").apply { isDaemon = true }
    },
    private val teardownExecutor: Executor = Executor { runnable ->
        Thread(runnable, "MqttDisconnect").apply { isDaemon = true }.start()
    }
) {

    private val clientRef = AtomicReference<MqttClientInterface?>(null)

    private var mqttClient: MqttClientInterface?
        get() = clientRef.get()
        set(value) = clientRef.set(value)

    // Cleared by disconnect() so a connect still in flight cannot revive a stage
    // the user has already left.
    @Volatile
    private var sessionOpen = true

    private val subscriptions = ConcurrentHashMap<String, Int>()

    // Survives dropped connections, unlike subscriptions, so a failed reconnect
    // still knows what to restore.
    private val desiredSubscriptions = ConcurrentHashMap<String, Int>()

    internal val activeSubscriptions: Set<String> get() = subscriptions.keys.toSet()

    private val incomingMessages = MqttMessageQueue()

    private val outgoingMessages = MqttPublishQueue()

    @Volatile
    private var lastConfig: MqttConnectionConfig? = null

    @Volatile
    private var reconnectAttempt = 0

    @Volatile
    private var autoReconnectEnabled = true

    private val reconnectExecutor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "MqttReconnect").apply { isDaemon = true }
        }

    internal val pendingMessageCount: Int get() = incomingMessages.size

    internal val pendingPublishCount: Int get() = outgoingMessages.size

    // Must run on the render thread: this is where scripts and variables are touched.
    fun dispatchPendingMessages() {
        incomingMessages.drain().forEach { dispatchMessage(it.topic, it.payload) }
    }

    private val listeners = ConcurrentHashMap<String, CopyOnWriteArraySet<MqttListener>>()

    private val wildcardFilters = CopyOnWriteArraySet<String>()

    internal val registeredTopics: Set<String> get() = listeners.keys.toSet()

    fun register(topic: String, listener: MqttListener) {
        if (topic.isBlank()) {
            Log.e(TAG, "Cannot register listener: topic is blank")
            return
        }
        val added = listeners.computeIfAbsent(topic) { CopyOnWriteArraySet() }.add(listener)
        if (MqttTopicMatcher.containsWildcard(topic)) {
            wildcardFilters.add(topic)
        }
        if (added) {
            Log.d(TAG, "Listener registered for '$topic'")
        }
    }

    fun unregister(topic: String, listener: MqttListener) {
        val topicListeners = listeners[topic] ?: return
        if (topicListeners.remove(listener)) {
            Log.d(TAG, "Listener unregistered from '$topic'")
        }
        if (topicListeners.isEmpty()) {
            listeners.remove(topic, topicListeners)
            wildcardFilters.remove(topic)
        }
    }

    internal fun dispatchMessage(topic: String, payload: String) {
        var delivered = false
        listeners[topic]?.forEach { listener ->
            notifyListener(listener, topic, payload)
            delivered = true
        }
        wildcardFilters.forEach { filter ->
            if (MqttTopicMatcher.matches(filter, topic)) {
                listeners[filter]?.forEach { listener ->
                    notifyListener(listener, topic, payload)
                    delivered = true
                }
            }
        }
        if (!delivered) {
            Log.d(TAG, "No listener registered for '$topic', ignoring message")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun notifyListener(listener: MqttListener, topic: String, payload: String) {
        try {
            listener.onMessageReceived(topic, payload)
        } catch (e: Exception) {
            Log.e(TAG, "Listener failed handling message on '$topic'", e)
        }
    }

    val isConnected: Boolean
        get() = mqttClient?.isConnected == true

    companion object {
        private val TAG = MqttManager::class.java.simpleName
        private const val TCP_SCHEME = "tcp"
        private const val SSL_SCHEME = "ssl"
        private const val CONNECTION_TIMEOUT = 5
        private const val MIN_QOS = 0
        private const val MAX_QOS = 2
        const val DEFAULT_QOS = 0
        private const val MULTI_LEVEL_WILDCARD = '#'
        private const val SINGLE_LEVEL_WILDCARD = '+'
        private const val INITIAL_BACKOFF_MILLIS = 1000L
        private const val MAX_BACKOFF_MILLIS = 60_000L
        private const val MAX_BACKOFF_EXPONENT = 6
        private const val MAX_RECONNECT_ATTEMPTS = 10
    }

    fun connectFromContext(context: Context) = connect(MqttConnectionConfig.fromContext(context))

    fun connect(config: MqttConnectionConfig): Boolean {
        sessionOpen = true
        autoReconnectEnabled = true
        return establishConnection(config)
    }

    @Suppress("TooGenericExceptionCaught", "ReturnCount")
    internal fun establishConnection(config: MqttConnectionConfig): Boolean = synchronized(this) {
        if (isConnected) return true
        if (!sessionOpen) {
            Log.d(TAG, "Session already ended, not connecting")
            return false
        }
        if (config.host.isBlank()) {
            Log.e(TAG, "Cannot connect: host is blank")
            return false
        }
        closeStaleClientIfNeeded()
        return tryConnect(config)
    }

    private fun closeStaleClientIfNeeded() {
        val currentClient = mqttClient
        if (currentClient != null && !currentClient.isConnected) {
            try {
                currentClient.close()
            } catch (e: MqttException) {
                Log.e(TAG, "Failed to close stale client before reconnect", e)
            }
            mqttClient = null
            subscriptions.clear()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun tryConnect(config: MqttConnectionConfig): Boolean {
        val brokerUrl = buildServerUri(config.host, config.port, config.useTls)
        val resolvedClientId = config.clientId.ifBlank { MqttClient.generateClientId() }
        return try {
            val client = clientFactory.create(brokerUrl, resolvedClientId).also { mqttClient = it }
            client.setCallback(callback)
            client.connect(buildConnectOptions(config.username, config.password))
            val connected = client.isConnected
            Log.d(TAG, "Connect result for clientId=$resolvedClientId at $brokerUrl: connected=$connected")
            if (connected && !sessionOpen) {
                Log.d(TAG, "Session ended during connect, discarding the new client")
                closeQuietly(client)
                mqttClient = null
                return false
            }
            if (connected) {
                lastConfig = config
                reconnectAttempt = 0
                restoreSubscriptions(desiredSubscriptions.toMap())
                flushOutgoingMessages()
            }
            connected
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to connect to ${config.host}:${config.port}", e)
            closeQuietly(mqttClient)
            mqttClient = null
            false
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Broker address '${config.host}:${config.port}' is not usable", e)
            closeQuietly(mqttClient)
            mqttClient = null
            false
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun closeQuietly(client: MqttClientInterface?) {
        try {
            client?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to close client", e)
        }
    }

    fun publishFromContext(
        context: Context,
        topic: String,
        payload: String,
        qos: Int = DEFAULT_QOS,
        retained: Boolean = false
    ) = publish(MqttConnectionConfig.fromContext(context), topic, payload, qos, retained)

    fun publish(
        config: MqttConnectionConfig,
        topic: String,
        payload: String,
        qos: Int = DEFAULT_QOS,
        retained: Boolean = false
    ): Boolean {
        if (topic.isBlank()) {
            Log.e(TAG, "Cannot publish: topic is blank")
            return false
        }
        if (!MqttTopicMatcher.isValidTopicName(topic)) {
            Log.e(TAG, "Cannot publish: '$topic' is not a valid topic name")
            return false
        }
        if (qos !in MIN_QOS..MAX_QOS) {
            Log.e(TAG, "Cannot publish: invalid QoS value $qos")
            return false
        }
        val pending = PendingPublish(topic, payload, qos, retained)
        val client = mqttClient
        if (!isConnected || client == null) {
            Log.w(TAG, "Broker unreachable, queueing message for '$topic'")
            outgoingMessages.enqueue(pending)
            return true
        }
        // Off the caller's thread: a QoS 1 or 2 publish waits for the broker, and the
        // publish brick runs on the render thread.
        publishExecutor.execute { sendOrQueue(client, pending) }
        return true
    }

    private fun sendOrQueue(client: MqttClientInterface, pending: PendingPublish) {
        try {
            client.publish(pending.topic, buildMessage(pending.payload, pending.qos, pending.retained))
            Log.d(TAG, "Published message to '${pending.topic}'")
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to publish to '${pending.topic}', queueing for retry", e)
            outgoingMessages.enqueue(pending)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Broker rejected the topic '${pending.topic}', dropping message", e)
        }
    }

    private fun restoreSubscriptions(topics: Map<String, Int>) {
        if (topics.isEmpty()) {
            return
        }
        val client = mqttClient ?: return
        Log.d(TAG, "Restoring ${topics.size} subscription(s)")
        topics.forEach { (topic, qos) ->
            try {
                client.subscribe(topic, qos)
                subscriptions[topic] = qos
            } catch (e: MqttException) {
                Log.e(TAG, "Failed to restore subscription '$topic'", e)
            }
        }
    }

    private fun flushOutgoingMessages() {
        if (outgoingMessages.isEmpty()) {
            return
        }
        val client = mqttClient ?: return
        val pending = outgoingMessages.drain()
        Log.d(TAG, "Flushing ${pending.size} queued message(s)")
        pending.forEachIndexed { index, message ->
            try {
                client.publish(message.topic, buildMessage(message.payload, message.qos, message.retained))
            } catch (e: MqttException) {
                Log.e(TAG, "Flush interrupted at '${message.topic}', requeueing remainder", e)
                outgoingMessages.requeueAll(pending.drop(index))
                return
            }
        }
    }

    fun subscribeFromContext(context: Context, topic: String, qos: Int = DEFAULT_QOS) =
        subscribe(MqttConnectionConfig.fromContext(context), topic, qos)

    fun subscribe(config: MqttConnectionConfig, topic: String, qos: Int = DEFAULT_QOS): Boolean {
        if (topic.isBlank()) {
            Log.e(TAG, "Cannot subscribe: topic is blank")
            return false
        }
        if (!MqttTopicMatcher.isValidFilter(topic)) {
            Log.e(TAG, "Cannot subscribe: '$topic' is not a valid topic filter")
            return false
        }
        if (qos !in MIN_QOS..MAX_QOS) {
            Log.e(TAG, "Cannot subscribe: invalid QoS value $qos")
            return false
        }
        subscriptions[topic]?.let { existingQos ->
            if (existingQos >= qos) {
                Log.d(TAG, "Already subscribed to '$topic' with QoS $existingQos, ignoring duplicate")
                return true
            }
            Log.d(TAG, "Upgrading '$topic' from QoS $existingQos to $qos")
        }
        if (!isConnected && !establishConnection(config)) {
            Log.e(TAG, "Cannot subscribe: connection failed")
            return false
        }
        val client = mqttClient ?: run {
            Log.e(TAG, "Cannot subscribe: client is null")
            return false
        }
        return try {
            client.subscribe(topic, qos)
            subscriptions[topic] = qos
            desiredSubscriptions[topic] = qos
            Log.d(TAG, "Subscribed to '$topic' with QoS $qos")
            true
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to subscribe to '$topic'", e)
            false
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Broker rejected the topic filter '$topic'", e)
            false
        }
    }

    fun unsubscribe(topic: String): Boolean {
        if (topic.isBlank()) {
            Log.e(TAG, "Cannot unsubscribe: topic is blank")
            return false
        }
        if (!subscriptions.containsKey(topic)) {
            Log.d(TAG, "Not subscribed to '$topic', ignoring")
            return true
        }
        val client = mqttClient ?: run {
            Log.e(TAG, "Cannot unsubscribe: client is null")
            return false
        }
        return try {
            client.unsubscribe(topic)
            desiredSubscriptions.remove(topic)
            val qos = subscriptions.remove(topic)
            Log.d(TAG, "Unsubscribed from '$topic' (was QoS $qos)")
            true
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to unsubscribe from '$topic'", e)
            false
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Broker rejected the topic filter '$topic'", e)
            false
        }
    }

    internal fun buildMessage(payload: String, qos: Int, retained: Boolean) =
        MqttMessage(payload.toByteArray(Charsets.UTF_8)).apply {
            this.qos = qos
            isRetained = retained
        }

    fun disconnect() {
        sessionOpen = false
        autoReconnectEnabled = false
        reconnectAttempt = 0
        listeners.clear()
        wildcardFilters.clear()
        subscriptions.clear()
        desiredSubscriptions.clear()
        incomingMessages.clear()
        outgoingMessages.clear()

        val client = clientRef.getAndSet(null) ?: return

        teardownExecutor.execute {
            try {
                client.disconnect()
                client.close()
                Log.d(TAG, "Disconnected and closed client")
            } catch (e: MqttException) {
                Log.e(TAG, "Error during disconnect", e)
            }
        }
    }

    internal fun buildServerUri(host: String, port: Int, useTls: Boolean): String {
        val scheme = if (useTls) SSL_SCHEME else TCP_SCHEME
        return "$scheme://$host:$port"
    }

    internal fun buildConnectOptions(username: String, password: String) = MqttConnectOptions().apply {
        isCleanSession = true
        connectionTimeout = CONNECTION_TIMEOUT
        if (username.isNotBlank()) {
            this.userName = username
            this.password = password.toCharArray()
        }
    }

    internal fun backoffDelayMillis(attempt: Int): Long {
        val exponent = attempt.coerceAtLeast(0).coerceAtMost(MAX_BACKOFF_EXPONENT)
        val delay = INITIAL_BACKOFF_MILLIS shl exponent
        return delay.coerceAtMost(MAX_BACKOFF_MILLIS)
    }

    internal fun reconnectNow(): Boolean {
        if (!autoReconnectEnabled) {
            Log.d(TAG, "Auto reconnect disabled, skipping reconnect")
            return false
        }
        val config = lastConfig ?: run {
            Log.d(TAG, "No previous configuration, skipping reconnect")
            return false
        }
        return establishConnection(config)
    }

    private fun scheduleReconnect() {
        if (!autoReconnectEnabled) {
            return
        }
        val attempt = reconnectAttempt++
        if (attempt >= MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Giving up reconnecting after $attempt attempts")
            return
        }
        val delay = backoffDelayMillis(attempt)
        Log.d(TAG, "Scheduling reconnect attempt ${attempt + 1} in ${delay}ms")
        reconnectExecutor.schedule({
            if (autoReconnectEnabled && !isConnected && reconnectNow().not()) {
                scheduleReconnect()
            }
        }, delay, TimeUnit.MILLISECONDS)
    }

    private val callback = object : MqttCallback {
        override fun connectionLost(cause: Throwable?) {
            Log.e(TAG, "Connection lost: ${cause?.message}")
            scheduleReconnect()
        }
        override fun messageArrived(topic: String, message: MqttMessage) {
            incomingMessages.enqueue(
                ReceivedMqttMessage(topic, String(message.payload, Charsets.UTF_8))
            )
        }
        override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
    }
}
