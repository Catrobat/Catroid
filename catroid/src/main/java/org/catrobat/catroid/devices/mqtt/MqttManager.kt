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

class MqttManager(private val clientFactory: MqttClientFactory = DefaultMqttClientFactory) {

    @Volatile
    private var mqttClient: MqttClientInterface? = null

    val isConnected: Boolean
        get() = mqttClient?.isConnected == true

    private val subscriptions = mutableMapOf<String, Int>()
    internal val activeSubscriptions: Set<String> get() = subscriptions.keys.toSet()

    companion object {
        private val TAG = MqttManager::class.simpleName
        private const val TCP_SCHEME = "tcp"
        private const val SSL_SCHEME = "ssl"
        private const val CONNECTION_TIMEOUT = 5

        @JvmStatic
        val instance: MqttManager by lazy { MqttManager() }
    }

    fun connectFromContext(context: Context) = connect(MqttConnectionConfig.fromContext(context))

    fun connect(config: MqttConnectionConfig): Boolean = synchronized(this) {
        if (isConnected) return true
        if (config.host.isBlank()) {
            Log.e(TAG, "Cannot connect: host is blank")
            return false
        }
        val currentClient = mqttClient
        if (currentClient != null && !currentClient.isConnected) {
            try {
                currentClient.close()
            } catch (e: MqttException) {
                Log.e(TAG, "Failed to close stale client before reconnect", e)
            }
            mqttClient = null
        }
        return try {
            val brokerUrl = buildServerUri(config.host, config.port, config.useTls)
            val resolvedClientId = config.clientId.ifEmpty { MqttClient.generateClientId() }
            val client = mqttClient ?: clientFactory.create(brokerUrl, resolvedClientId).also { mqttClient = it }
            client.setCallback(callback)
            client.connect(buildConnectOptions(config.username, config.password))
            val connected = client.isConnected
            Log.d(TAG, "Connect result for clientId=$resolvedClientId at $brokerUrl: connected=$connected")
            connected
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to connect to ${config.host}:${config.port}", e)
            try {
                mqttClient?.close()
            } catch (closeEx: MqttException) {
                Log.e(TAG, "Failed to close client after connect error", closeEx)
            }
            mqttClient = null
            false
        }
    }

    fun publishFromContext(context: Context, topic: String, payload: String, qos: Int = 0, retained: Boolean = false) =
        publish(MqttConnectionConfig.fromContext(context), topic, payload, qos, retained)

    fun publish(config: MqttConnectionConfig, topic: String, payload: String, qos: Int = 0, retained: Boolean = false): Boolean {
        if (topic.isBlank()) {
            Log.e(TAG, "Cannot publish: topic is blank")
            return false
        }
        if (topic.contains('#') || topic.contains('+')) {
            Log.e(TAG, "Cannot publish: topic contains wildcard characters")
            return false
        }
        if (qos !in 0..2) {
            Log.e(TAG, "Cannot publish: invalid QoS value $qos")
            return false
        }
        if (!isConnected && !connect(config)) {
            Log.e(TAG, "Cannot publish: connection failed")
            return false
        }
        val client = mqttClient ?: return false
        return try {
            client.publish(topic, buildMessage(payload, qos, retained))
            Log.d(TAG, "Published message to '$topic'")
            true
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to publish to '$topic'", e)
            false
        }
    }

    fun subscribeFromContext(context: Context, topic: String, qos: Int = 0) =
        subscribe(MqttConnectionConfig.fromContext(context), topic, qos)

    fun subscribe(config: MqttConnectionConfig, topic: String, qos: Int = 0): Boolean {
        if (topic.isBlank()) {
            Log.e(TAG, "Cannot subscribe: topic is blank")
            return false
        }
        if (qos !in 0..2) {
            Log.e(TAG, "Cannot subscribe: invalid QoS value $qos")
            return false
        }
        val existingQos = subscriptions[topic]
        if (existingQos != null) {
            Log.d(TAG, "Already subscribed to '$topic' with QoS $existingQos, ignoring duplicate")
            return true
        }
        // Wildcards (#, +) are valid for MQTT subscriptions unlike publish.
        if (!isConnected && !connect(config)) {
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
            Log.d(TAG, "Subscribed to '$topic' with QoS $qos")
            true
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to subscribe to '$topic'", e)
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
            val qos = subscriptions.remove(topic)
            Log.d(TAG, "Unsubscribed from '$topic' (was QoS $qos)")
            true
        } catch (e: MqttException) {
            Log.e(TAG, "Failed to unsubscribe from '$topic'", e)
            false
        }
    }

    fun disconnect() {
        synchronized(this) {
            if (mqttClient == null) return
            try {
                mqttClient?.disconnect()
                mqttClient?.close()
                Log.d(TAG, "Disconnected and closed client")
            } catch (e: MqttException) {
                Log.e(TAG, "Error during disconnect", e)
            } finally {
                subscriptions.clear()
                mqttClient = null
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

    internal fun buildMessage(payload: String, qos: Int, retained: Boolean) = MqttMessage(payload.toByteArray()).apply {
        this.qos = qos
        isRetained = retained
    }

    private val callback = object : MqttCallback {
        override fun connectionLost(cause: Throwable?) {
            Log.e(TAG, "Connection lost", cause)
        }
        override fun messageArrived(topic: String, message: MqttMessage) = Unit
        override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
    }
}
