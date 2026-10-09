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
import org.catrobat.catroid.devices.mqtt.MqttManager
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MqttManagerTest {

    private lateinit var fakeClient: FakeMqttClient
    private lateinit var fakeFactory: FakeMqttClientFactory
    private lateinit var manager: MqttManager

    @Before
    fun setUp() {
        fakeClient = FakeMqttClient()
        fakeFactory = FakeMqttClientFactory(fakeClient)
        manager = MqttManager(fakeFactory)
    }

    private val defaultConfig = MqttConnectionConfig("localhost", 1883, "client-1", "", "", false)

    // --- Singleton ---

    @Test
    fun testMqttManagerInstanceIsNotNull() {
        assertNotNull(MqttManager.instance)
    }

    @Test
    fun testMqttManagerIsSingleton() {
        assertSame(MqttManager.instance, MqttManager.instance)
    }

    // --- Initial state ---

    @Test
    fun testIsNotConnectedInitially() {
        fakeClient.connected = false
        assertFalse(manager.isConnected)
    }

    // --- connect() ---

    @Test
    fun testConnectReturnsTrueOnSuccess() {
        assertTrue(manager.connect(defaultConfig))
        assertTrue(fakeClient.connectCalled)
    }

    @Test
    fun testConnectSetsCallbackOnClient() {
        manager.connect(defaultConfig)
        assertTrue(fakeClient.callbackSet)
    }

    @Test
    fun testConnectReturnsFalseWhenClientThrows() {
        fakeClient.throwOnConnect = true
        assertFalse(manager.connect(defaultConfig))
    }

    @Test
    fun testIsNotConnectedAfterConnectFailure() {
        fakeClient.throwOnConnect = true
        manager.connect(defaultConfig)
        assertFalse(manager.isConnected)
    }

    @Test
    fun testCloseIsCalledOnClientWhenConnectThrows() {
        fakeClient.throwOnConnect = true
        manager.connect(defaultConfig)
        assertTrue(fakeClient.closeCalled)
    }

    @Test
    fun testConnectWhenAlreadyConnectedDoesNotReconnect() {
        manager.connect(defaultConfig)
        fakeClient.connectCalled = false
        manager.connect(defaultConfig)
        assertFalse(fakeClient.connectCalled)
    }

    @Test
    fun testConnectWhenAlreadyConnectedReturnsTrue() {
        manager.connect(defaultConfig)
        assertTrue(manager.connect(defaultConfig))
    }

    @Test
    fun testConnectWithBlankHostReturnsFalse() {
        assertFalse(manager.connect(MqttConnectionConfig("   ", 1883, "client-1", "", "", false)))
    }

    @Test
    fun testConnectWithBlankHostDoesNotCallClient() {
        manager.connect(MqttConnectionConfig("   ", 1883, "client-1", "", "", false))
        assertFalse(fakeClient.connectCalled)
    }

    @Test
    fun testConnectSucceedsWithEmptyClientId() {
        manager.connect(MqttConnectionConfig("localhost", 1883, "", "", "", false))
        assertTrue(fakeClient.connectCalled)
    }

    @Test
    fun testConnectClosesStaleClientBeforeReconnecting() {
        manager.connect(defaultConfig)
        fakeClient.connected = false
        fakeFactory.createCalled = false
        manager.connect(defaultConfig)
        assertTrue(fakeClient.closeCalled)
        assertTrue(fakeFactory.createCalled)
    }

    @Test
    fun testConnectDoesNotCloseAlreadyConnectedClient() {
        manager.connect(defaultConfig)
        fakeClient.closeCalled = false
        manager.connect(defaultConfig)
        assertFalse(fakeClient.closeCalled)
    }

    // --- URI building ---

    @Test
    fun testBuildServerUriWithoutTlsUsesTcpScheme() {
        assertTrue(manager.buildServerUri("localhost", 1883, false).startsWith("tcp://"))
    }

    @Test
    fun testBuildServerUriWithTlsUsesSslScheme() {
        assertTrue(manager.buildServerUri("localhost", 8883, true).startsWith("ssl://"))
    }

    @Test
    fun testBuildServerUriTcpFullUri() {
        assertEquals("tcp://broker.test.com:1883", manager.buildServerUri("broker.test.com", 1883, false))
    }

    @Test
    fun testBuildServerUriSslFullUri() {
        assertEquals("ssl://broker.test.com:8883", manager.buildServerUri("broker.test.com", 8883, true))
    }

    // --- ConnectOptions building ---

    @Test
    fun testBuildConnectOptionsUsesCleanSession() {
        assertTrue(manager.buildConnectOptions("", "").isCleanSession)
    }

    @Test
    fun testBuildConnectOptionsSetsUsernameAndPasswordWhenProvided() {
        val options = manager.buildConnectOptions("user", "pass")
        assertEquals("user", options.userName)
        assertEquals("pass", String(options.password ?: charArrayOf()))
    }

    @Test
    fun testBuildConnectOptionsDoesNotSetUsernameWhenEmpty() {
        assertEquals(null, manager.buildConnectOptions("", "").userName)
    }

    @Test
    fun testBuildConnectOptionsUsernameOnlyWithEmptyPasswordStillSets() {
        val options = manager.buildConnectOptions("user", "")
        assertEquals("user", options.userName)
        assertEquals("", String(options.password ?: charArrayOf()))
    }

    @Test
    fun testBuildConnectOptionsDoesNotSetUsernameWhenBlank() {
        assertEquals(null, manager.buildConnectOptions("   ", "").userName)
    }

    // --- disconnect() ---

    @Test
    fun testDisconnectCallsClientDisconnect() {
        manager.connect(defaultConfig)
        manager.disconnect()
        assertTrue(fakeClient.disconnectCalled)
    }

    @Test
    fun testDisconnectCallsClientClose() {
        manager.connect(defaultConfig)
        manager.disconnect()
        assertTrue(fakeClient.closeCalled)
    }

    @Test
    fun testDisconnectWhenNoClientDoesNotCallClient() {
        manager = MqttManager(FakeMqttClientFactory(fakeClient))
        manager.disconnect()
        assertFalse(fakeClient.disconnectCalled)
        assertFalse(fakeClient.closeCalled)
    }

    @Test
    fun testDisconnectCleansUpDroppedConnection() {
        manager.connect(defaultConfig)
        fakeClient.connected = false
        manager.disconnect()
        assertTrue(fakeClient.disconnectCalled)
        assertTrue(fakeClient.closeCalled)
    }

    @Test
    fun testIsNotConnectedAfterDisconnect() {
        manager.connect(defaultConfig)
        manager.disconnect()
        assertFalse(manager.isConnected)
    }

    @Test
    fun testDisconnectTwiceDoesNotCrash() {
        manager.connect(defaultConfig)
        manager.disconnect()
        manager.disconnect()
        // no exception = pass
    }

    // --- publish() ---

    @Test
    fun testPublishReturnsTrueWhenConnectedAndTopicValid() {
        fakeClient.connected = true
        assertTrue(manager.publish(defaultConfig, "home/temp", "22"))
    }

    @Test
    fun testPublishCallsClientPublish() {
        fakeClient.connected = true
        manager.publish(defaultConfig, "home/temp", "22")
        assertTrue(fakeClient.publishCalled)
    }

    @Test
    fun testPublishSendsCorrectTopicAndPayload() {
        fakeClient.connected = true
        manager.publish(defaultConfig, "home/temp", "42")
        assertEquals("home/temp", fakeClient.lastTopic)
        assertEquals("42", fakeClient.lastPayload)
    }

    @Test
    fun testPublishSetsQosAndRetained() {
        fakeClient.connected = true
        manager.publish(defaultConfig, "home/temp", "on", qos = 2, retained = true)
        assertEquals(2, fakeClient.lastQos)
        assertTrue(fakeClient.lastRetained)
    }

    @Test
    fun testPublishTriggersLazyConnectWhenDisconnected() {
        fakeClient.connected = false
        manager.publish(defaultConfig, "home/temp", "22")
        assertTrue(fakeClient.connectCalled)
    }

    @Test
    fun testPublishReturnsTrueAfterLazyConnect() {
        fakeClient.connected = false
        assertTrue(manager.publish(defaultConfig, "home/temp", "22"))
    }

    @Test
    fun testPublishReturnsFalseWhenLazyConnectFails() {
        fakeClient.connected = false
        fakeClient.throwOnConnect = true
        assertFalse(manager.publish(defaultConfig, "home/temp", "22"))
    }

    @Test
    fun testPublishReturnsFalseForBlankTopic() {
        fakeClient.connected = true
        assertFalse(manager.publish(defaultConfig, "   ", "22"))
    }

    @Test
    fun testPublishDoesNotCallClientForBlankTopic() {
        fakeClient.connected = true
        manager.publish(defaultConfig, "   ", "22")
        assertFalse(fakeClient.publishCalled)
    }

    @Test
    fun testPublishReturnsFalseForTopicWithHashWildcard() {
        fakeClient.connected = true
        assertFalse(manager.publish(defaultConfig, "home/#", "22"))
    }

    @Test
    fun testPublishReturnsFalseForTopicWithPlusWildcard() {
        fakeClient.connected = true
        assertFalse(manager.publish(defaultConfig, "home/+/temp", "22"))
    }

    @Test
    fun testPublishReturnsFalseForInvalidQos() {
        fakeClient.connected = true
        assertFalse(manager.publish(defaultConfig, "home/temp", "22", qos = 3))
    }

    @Test
    fun testPublishReturnsFalseWhenClientThrows() {
        fakeClient.connected = true
        fakeClient.throwOnPublish = true
        assertFalse(manager.publish(defaultConfig, "home/temp", "22"))
    }

    @Test
    fun testPublishDoesNotCrashWhenClientThrows() {
        fakeClient.connected = true
        fakeClient.throwOnPublish = true
        manager.publish(defaultConfig, "home/temp", "22")
        // no exception = pass
    }

    @Test
    fun testPublishWithEmptyPayloadReturnsTrue() {
        fakeClient.connected = true
        assertTrue(manager.publish(defaultConfig, "home/temp", ""))
    }

    @Test
    fun testPublishWithQosZeroReturnsTrue() {
        fakeClient.connected = true
        assertTrue(manager.publish(defaultConfig, "home/temp", "22", qos = 0))
    }

    @Test
    fun testPublishWithQosOneReturnsTrue() {
        fakeClient.connected = true
        assertTrue(manager.publish(defaultConfig, "home/temp", "22", qos = 1))
    }

    @Test
    fun testPublishWithQosTwoReturnsTrue() {
        fakeClient.connected = true
        assertTrue(manager.publish(defaultConfig, "home/temp", "22", qos = 2))
    }

    @Test
    fun testPublishWithRetainedFalseSetsRetainedFalse() {
        fakeClient.connected = true
        manager.publish(defaultConfig, "home/temp", "22", retained = false)
        assertFalse(fakeClient.lastRetained)
    }

    @Test
    fun testPublishWhenAlreadyConnectedDoesNotReconnect() {
        manager.connect(defaultConfig)
        fakeClient.connectCalled = false
        manager.publish(defaultConfig, "home/temp", "22")
        assertFalse(fakeClient.connectCalled)
    }

    @Test
    fun testPublishDoesNotCallClientWhenLazyConnectFails() {
        fakeClient.connected = false
        fakeClient.throwOnConnect = true
        manager.publish(defaultConfig, "home/temp", "22")
        assertFalse(fakeClient.publishCalled)
    }

    // --- subscribe() ---

    @Test
    fun testSubscribeReturnsTrueWhenConnectedAndTopicValid() {
        fakeClient.connected = true
        assertTrue(manager.subscribe(defaultConfig, "home/temp"))
    }

    @Test
    fun testSubscribeCallsClientSubscribe() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        assertTrue(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeSendsCorrectTopicAndQos() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp", qos = 1)
        assertEquals("home/temp", fakeClient.lastSubscribedTopic)
        assertEquals(1, fakeClient.lastSubscribedQos)
    }

    @Test
    fun testSubscribeAcceptsWildcardHashTopic() {
        fakeClient.connected = true
        assertTrue(manager.subscribe(defaultConfig, "home/#"))
    }

    @Test
    fun testSubscribeAcceptsWildcardPlusTopic() {
        fakeClient.connected = true
        assertTrue(manager.subscribe(defaultConfig, "home/+/temp"))
    }

    @Test
    fun testSubscribeReturnsFalseForBlankTopic() {
        fakeClient.connected = true
        assertFalse(manager.subscribe(defaultConfig, "   "))
    }

    @Test
    fun testSubscribeDoesNotCallClientForBlankTopic() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "   ")
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeReturnsFalseForInvalidQos() {
        fakeClient.connected = true
        assertFalse(manager.subscribe(defaultConfig, "home/temp", qos = 3))
    }

    @Test
    fun testDuplicateSubscribeReturnsTrueWithoutCallingClientAgain() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        fakeClient.subscribeCalled = false
        assertTrue(manager.subscribe(defaultConfig, "home/temp"))
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeTriggersLazyConnectWhenDisconnected() {
        fakeClient.connected = false
        manager.subscribe(defaultConfig, "home/temp")
        assertTrue(fakeClient.connectCalled)
    }

    @Test
    fun testSubscribeReturnsFalseWhenLazyConnectFails() {
        fakeClient.connected = false
        fakeClient.throwOnConnect = true
        assertFalse(manager.subscribe(defaultConfig, "home/temp"))
    }

    @Test
    fun testSubscribeDoesNotCallClientWhenLazyConnectFails() {
        fakeClient.connected = false
        fakeClient.throwOnConnect = true
        manager.subscribe(defaultConfig, "home/temp")
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeReturnsFalseWhenClientThrows() {
        fakeClient.connected = true
        fakeClient.throwOnSubscribe = true
        assertFalse(manager.subscribe(defaultConfig, "home/temp"))
    }

    @Test
    fun testSubscribeDoesNotCrashWhenClientThrows() {
        fakeClient.connected = true
        fakeClient.throwOnSubscribe = true
        manager.subscribe(defaultConfig, "home/temp")
        // no exception = pass
    }

    @Test
    fun testSubscribeWithQosZeroReturnsTrue() {
        fakeClient.connected = true
        assertTrue(manager.subscribe(defaultConfig, "home/temp", qos = 0))
    }

    @Test
    fun testSubscribeWithQosTwoReturnsTrue() {
        fakeClient.connected = true
        assertTrue(manager.subscribe(defaultConfig, "home/temp", qos = 2))
    }

    // --- unsubscribe() ---

    @Test
    fun testUnsubscribeReturnsTrueAfterSubscribe() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        assertTrue(manager.unsubscribe("home/temp"))
    }

    @Test
    fun testUnsubscribeCallsClientUnsubscribe() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        manager.unsubscribe("home/temp")
        assertTrue(fakeClient.unsubscribeCalled)
    }

    @Test
    fun testUnsubscribeSendsCorrectTopic() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        manager.unsubscribe("home/temp")
        assertEquals("home/temp", fakeClient.lastUnsubscribedTopic)
    }

    @Test
    fun testUnsubscribeWhenNotSubscribedReturnsTrueWithoutCallingClient() {
        fakeClient.connected = true
        assertTrue(manager.unsubscribe("home/temp"))
        assertFalse(fakeClient.unsubscribeCalled)
    }

    @Test
    fun testUnsubscribeRemovesTopicFromActiveSubscriptions() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        manager.unsubscribe("home/temp")
        assertFalse(manager.activeSubscriptions.contains("home/temp"))
    }

    @Test
    fun testUnsubscribeReturnsFalseForBlankTopic() {
        assertFalse(manager.unsubscribe("   "))
    }

    @Test
    fun testUnsubscribeDoesNotCallClientForBlankTopic() {
        manager.unsubscribe("   ")
        assertFalse(fakeClient.unsubscribeCalled)
    }

    @Test
    fun testUnsubscribeReturnsFalseWhenClientThrows() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        fakeClient.throwOnUnsubscribe = true
        assertFalse(manager.unsubscribe("home/temp"))
    }

    @Test
    fun testUnsubscribeDoesNotCrashWhenClientThrows() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        fakeClient.throwOnUnsubscribe = true
        manager.unsubscribe("home/temp")
        // no exception = pass
    }

    @Test
    fun testSubscriptionsAreClearedOnDisconnect() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        manager.disconnect()
        assertTrue(manager.activeSubscriptions.isEmpty())
    }

    @Test
    fun testSubscribeAfterDisconnectSucceedsAgain() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp")
        manager.disconnect()
        // Re-create manager with same fakeClient to simulate reconnection.
        manager = MqttManager(FakeMqttClientFactory(fakeClient))
        fakeClient.connected = true
        fakeClient.subscribeCalled = false
        manager.subscribe(defaultConfig, "home/temp")
        assertTrue(fakeClient.subscribeCalled)
    }

    @Test
    fun testDuplicateSubscribeWithDifferentQosDoesNotResubscribe() {
        fakeClient.connected = true
        manager.subscribe(defaultConfig, "home/temp", qos = 0)
        fakeClient.subscribeCalled = false
        manager.subscribe(defaultConfig, "home/temp", qos = 2)
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testFailedSubscribeDoesNotAddToActiveSubscriptions() {
        fakeClient.connected = true
        fakeClient.throwOnSubscribe = true
        manager.subscribe(defaultConfig, "home/temp")
        assertTrue(manager.activeSubscriptions.isEmpty())
    }

    // --- buildMessage() ---

    @Test
    fun testBuildMessageSetsPayload() {
        val msg = manager.buildMessage("hello", 1, false)
        assertEquals("hello", String(msg.payload))
    }

    @Test
    fun testBuildMessageSetsQos() {
        assertEquals(1, manager.buildMessage("hello", 1, false).qos)
    }

    @Test
    fun testBuildMessageSetsRetained() {
        assertTrue(manager.buildMessage("hello", 0, true).isRetained)
    }

    // --- FakeMqttClientFactory ---

    private class FakeMqttClientFactory(private val client: FakeMqttClient) : MqttClientFactory {
        var createCalled = false
        override fun create(brokerUrl: String, clientId: String): FakeMqttClient {
            createCalled = true
            return client
        }
    }

    // --- FakeMqttClient ---

    private inner class FakeMqttClient : MqttClientInterface {
        var connected = false
        var connectCalled = false
        var disconnectCalled = false
        var closeCalled = false
        var callbackSet = false
        var throwOnConnect = false
        var throwOnPublish = false
        var publishCalled = false
        var lastTopic: String? = null
        var lastPayload: String? = null
        var lastQos: Int = -1
        var lastRetained: Boolean = false
        var lastConnectOptions: MqttConnectOptions? = null

        override val isConnected get() = connected

        override fun connect(options: MqttConnectOptions) {
            if (throwOnConnect) throw org.eclipse.paho.client.mqttv3.MqttException(0)
            connectCalled = true
            connected = true
            lastConnectOptions = options
        }

        override fun disconnect() {
            disconnectCalled = true
            connected = false
        }

        override fun close() {
            closeCalled = true
        }

        override fun setCallback(callback: MqttCallback) {
            callbackSet = true
        }

        override fun publish(topic: String, message: MqttMessage) {
            if (throwOnPublish) throw org.eclipse.paho.client.mqttv3.MqttException(0)
            publishCalled = true
            lastTopic = topic
            lastPayload = String(message.payload)
            lastQos = message.qos
            lastRetained = message.isRetained
        }

        var throwOnSubscribe = false
        var throwOnUnsubscribe = false
        var subscribeCalled = false
        var unsubscribeCalled = false
        var lastSubscribedTopic: String? = null
        var lastSubscribedQos: Int = -1
        var lastUnsubscribedTopic: String? = null

        override fun subscribe(topic: String, qos: Int) {
            if (throwOnSubscribe) throw org.eclipse.paho.client.mqttv3.MqttException(0)
            subscribeCalled = true
            lastSubscribedTopic = topic
            lastSubscribedQos = qos
        }

        override fun unsubscribe(topic: String) {
            if (throwOnUnsubscribe) throw org.eclipse.paho.client.mqttv3.MqttException(0)
            unsubscribeCalled = true
            lastUnsubscribedTopic = topic
        }
    }
}
