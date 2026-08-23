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

package org.catrobat.catroid.test.mqtt

import org.catrobat.catroid.devices.mqtt.MqttClientFactory
import org.catrobat.catroid.devices.mqtt.MqttClientInterface
import org.catrobat.catroid.devices.mqtt.MqttConnectionConfig
import org.catrobat.catroid.devices.mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

internal val TEST_CONFIG = MqttConnectionConfig("localhost", 1883, "client-1", "", "", false)

internal class FakeMqttClient : MqttClientInterface {
    var connected = false
    var connectCalled = false
    var disconnectCalled = false
    var closeCalled = false
    var callbackSet = false
    var throwOnConnect = false
    var publishCalled = false
    var throwOnPublish = false
    var lastPublishTopic: String? = null
    var lastPublishMessage: MqttMessage? = null
    val publishedTopics = mutableListOf<String>()
    var subscribeCalled = false
    var throwOnSubscribe = false
    var lastSubscribeQos = -1
    var unsubscribeCalled = false
    var throwOnUnsubscribe = false
    var onDisconnect: (() -> Unit)? = null
    val subscribedTopics = mutableListOf<String>()
    private var storedCallback: MqttCallback? = null

    override val isConnected get() = connected

    override fun connect(options: MqttConnectOptions) {
        if (throwOnConnect) throw MqttException(0)
        connectCalled = true
        connected = true
    }

    override fun publish(topic: String, message: MqttMessage) {
        requireValidTopicName(topic)
        if (throwOnPublish) throw MqttException(0)
        publishCalled = true
        lastPublishTopic = topic
        lastPublishMessage = message
        publishedTopics.add(topic)
    }

    override fun subscribe(topic: String, qos: Int) {
        requireValidFilter(topic)
        if (throwOnSubscribe) throw MqttException(0)
        subscribeCalled = true
        subscribedTopics.add(topic)
        lastSubscribeQos = qos
    }

    override fun unsubscribe(topic: String) {
        requireValidFilter(topic)
        if (throwOnUnsubscribe) throw MqttException(0)
        unsubscribeCalled = true
        subscribedTopics.remove(topic)
    }

    override fun disconnect() {
        onDisconnect?.invoke()
        disconnectCalled = true
        connected = false
    }

    override fun close() {
        closeCalled = true
    }

    override fun setCallback(callback: MqttCallback) {
        callbackSet = true
        storedCallback = callback
    }

    fun deliver(topic: String, payload: String) =
        deliverBytes(topic, payload.toByteArray(Charsets.UTF_8))

    fun deliverBytes(topic: String, payload: ByteArray) {
        storedCallback?.messageArrived(topic, MqttMessage(payload))
    }

    fun dropConnection(cause: Throwable = MqttException(0)) {
        connected = false
        storedCallback?.connectionLost(cause)
    }

    // Paho rejects malformed topics with IllegalArgumentException before it checks
    // whether it is connected; a fake that accepts anything hides that crash.
    private fun requireValidFilter(filter: String) {
        val levels = filter.split('/')
        val valid = filter.isNotEmpty() && levels.withIndex().all { (index, level) ->
            (!level.contains('+') || level == "+") &&
                (!level.contains('#') || (level == "#" && index == levels.lastIndex))
        }
        require(valid) { "Invalid usage of wildcard in topic string: $filter" }
    }

    private fun requireValidTopicName(topic: String) {
        require(topic.isNotEmpty() && !topic.contains('+') && !topic.contains('#')) {
            "Invalid usage of wildcard in topic string: $topic"
        }
    }
}

internal class FakeMqttClientFactory(private val client: FakeMqttClient) : MqttClientFactory {
    var createCalled = false
    var lastBrokerUrl: String? = null
    var lastClientId: String? = null

    override fun create(brokerUrl: String, clientId: String): FakeMqttClient {
        createCalled = true
        lastBrokerUrl = brokerUrl
        lastClientId = clientId
        return client
    }
}

internal class FakeListener : MqttListener {
    val received = mutableListOf<Pair<String, String>>()
    override fun onMessageReceived(topic: String, payload: String) {
        received.add(topic to payload)
    }
}
