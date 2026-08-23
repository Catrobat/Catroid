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

import org.catrobat.catroid.devices.mqtt.MqttConnectionConfig
import org.catrobat.catroid.devices.mqtt.MqttManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MqttManagerRecoveryTest {

    private lateinit var fakeClient: FakeMqttClient
    private lateinit var fakeFactory: FakeMqttClientFactory
    private lateinit var manager: MqttManager

    private val defaultConfig = MqttConnectionConfig("broker.local", 1883, "client", "", "", false)

    @Before
    fun setUp() {
        fakeClient = FakeMqttClient()
        fakeFactory = FakeMqttClientFactory(fakeClient)
        manager = MqttManager(fakeFactory, { it.run() }) { it.run() }
    }

    private fun connected() = manager.connect(defaultConfig)

    @Test
    fun testConnectPassesTheResolvedBrokerUrlAndClientId() {
        connected()
        assertEquals("tcp://broker.local:1883", fakeFactory.lastBrokerUrl)
        assertEquals("client", fakeFactory.lastClientId)
    }

    @Test
    fun testTlsConnectUsesTheSecureScheme() {
        manager.connect(defaultConfig.copy(useTls = true, port = 8883))
        assertEquals("ssl://broker.local:8883", fakeFactory.lastBrokerUrl)
    }

    @Test
    fun testBlankClientIdIsReplacedWithAGeneratedOne() {
        manager.connect(defaultConfig.copy(clientId = "   "))
        assertTrue(fakeFactory.lastClientId!!.isNotBlank())
        assertFalse(fakeFactory.lastClientId == "   ")
    }

    @Test
    fun testConnectionLostTriggersRecoveryThatRestoresSubscriptions() {
        connected()
        manager.subscribe(defaultConfig, "home/#")
        fakeClient.subscribedTopics.clear()

        // Reported by Paho on its own thread; without the callback wired to the retry
        // machinery nothing would ever recover.
        fakeClient.dropConnection()
        assertFalse(manager.isConnected)

        assertTrue(manager.reconnectNow())
        assertTrue(manager.isConnected)
        assertTrue(fakeClient.subscribedTopics.contains("home/#"))
    }

    @Test
    fun testReconnectIsRefusedAfterAnIntentionalDisconnect() {
        connected()
        manager.disconnect()

        assertFalse(manager.reconnectNow())
        assertFalse(manager.isConnected)
    }

    @Test
    fun testConnectFinishingAfterDisconnectDoesNotLeaveALiveSession() {
        manager.disconnect()
        assertFalse(manager.establishConnection(defaultConfig))
        assertFalse(manager.isConnected)
    }

    @Test
    fun testDisconnectDoesNotWaitForTheClientBeforeClearingState() {
        connected()
        manager.subscribe(defaultConfig, "home/#")
        manager.disconnect()

        assertEquals(0, manager.activeSubscriptions.size)
        assertEquals(0, manager.registeredTopics.size)
        assertFalse(manager.isConnected)
    }
}
