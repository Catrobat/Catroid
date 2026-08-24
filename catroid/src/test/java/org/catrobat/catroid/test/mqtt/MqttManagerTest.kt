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
    }
}
