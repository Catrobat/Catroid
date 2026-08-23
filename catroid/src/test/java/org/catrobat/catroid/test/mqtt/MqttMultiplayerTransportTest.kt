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
import org.catrobat.catroid.devices.mqtt.MqttMultiplayerTransport
import org.catrobat.catroid.devices.mqtt.MqttMultiplayerTransport.Companion.roomIdFor
import org.catrobat.catroid.formulaeditor.UserVariable
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MqttMultiplayerTransportTest {

    private val config = MqttConnectionConfig("localhost", 1883, "client-1", "", "", false)
    private lateinit var client: RecordingClient
    private lateinit var manager: MqttManager
    private lateinit var transport: MqttMultiplayerTransport
    private val received = mutableListOf<Pair<String, String>>()

    @Before
    fun setUp() {
        client = RecordingClient()
        manager = MqttManager(
            clientFactory = { _, _ -> client },
            publishExecutor = { it.run() },
            teardownExecutor = { it.run() }
        )
        transport = MqttMultiplayerTransport(manager)
    }

    private fun start(sender: String = "player1") =
        transport.start(config, "room1", sender) { name, value -> received.add(name to value) }

    private fun deliver(topic: String, payload: String) {
        client.storedCallback?.messageArrived(topic, MqttMessage(payload.toByteArray()))
        manager.dispatchPendingMessages()
    }

    @Test
    fun testStartSubscribesToTheWholeRoom() {
        start()
        assertEquals(listOf("catrobat/multiplayer/room1/#"), client.subscribedTopics)
    }

    @Test
    fun testTransportIsNotStartedInitially() {
        assertFalse(transport.isStarted)
    }

    @Test
    fun testTransportIsStartedAfterStart() {
        start()
        assertTrue(transport.isStarted)
    }

    @Test
    fun testStartRejectsBlankRoom() {
        assertFalse(transport.start(config, "  ", "player1") { _, _ -> })
    }

    @Test
    fun testStartRejectsBlankSender() {
        assertFalse(transport.start(config, "room1", "  ") { _, _ -> })
    }

    @Test
    fun testStopEndsDelivery() {
        start()
        transport.stop()
        deliver("catrobat/multiplayer/room1/player2/score", "10")
        assertTrue(received.isEmpty())
    }

    @Test
    fun testSendPublishesUnderRoomAndSender() {
        start()
        transport.sendVariable(UserVariable("score", 10))
        assertEquals("catrobat/multiplayer/room1/player1/score", client.lastPublishedTopic)
    }

    @Test
    fun testSendPublishesVariableValue() {
        start()
        transport.sendVariable(UserVariable("score", 10))
        assertEquals("10", client.lastPublishedPayload)
    }

    @Test
    fun testSendBeforeStartIsIgnored() {
        transport.sendVariable(UserVariable("score", 10))
        assertEquals(null, client.lastPublishedTopic)
    }

    @Test
    fun testSendIgnoresVariableWithoutName() {
        start()
        transport.sendVariable(null)
        assertEquals(null, client.lastPublishedTopic)
    }

    @Test
    fun testMessageFromOtherPlayerUpdatesVariable() {
        start()
        deliver("catrobat/multiplayer/room1/player2/score", "42")
        assertEquals(listOf("score" to "42"), received)
    }

    @Test
    fun testOwnEchoIsIgnored() {
        start(sender = "player1")
        deliver("catrobat/multiplayer/room1/player1/score", "42")
        assertTrue(received.isEmpty())
    }

    @Test
    fun testMessageFromAnotherRoomIsNotDelivered() {
        start()
        deliver("catrobat/multiplayer/room2/player2/score", "42")
        assertTrue(received.isEmpty())
    }

    @Test
    fun testMalformedTopicIsIgnored() {
        start()
        deliver("catrobat/multiplayer/room1/player2", "42")
        assertTrue(received.isEmpty())
    }

    @Test
    fun testEmptyPayloadIsDelivered() {
        start()
        deliver("catrobat/multiplayer/room1/player2/name", "")
        assertEquals(listOf("name" to ""), received)
    }

    @Test
    fun testMultiplePlayersAreAllReceived() {
        start()
        deliver("catrobat/multiplayer/room1/player2/score", "1")
        deliver("catrobat/multiplayer/room1/player3/score", "2")
        assertEquals(listOf("score" to "1", "score" to "2"), received)
    }

    private class RecordingClient : MqttClientInterface, MqttClientFactory {
        var connected = false
        var storedCallback: MqttCallback? = null
        val subscribedTopics = mutableListOf<String>()
        var lastPublishedTopic: String? = null
        var lastPublishedPayload: String? = null

        override fun create(brokerUrl: String, clientId: String) = this
        override val isConnected get() = connected
        override fun connect(options: MqttConnectOptions) {
            connected = true
        }

        override fun disconnect() {
            connected = false
        }

        override fun close() = Unit
        override fun setCallback(callback: MqttCallback) {
            storedCallback = callback
        }

        override fun publish(topic: String, message: MqttMessage) {
            lastPublishedTopic = topic
            lastPublishedPayload = String(message.payload, Charsets.UTF_8)
        }

        override fun subscribe(topic: String, qos: Int) {
            subscribedTopics.add(topic)
        }

        override fun unsubscribe(topic: String) {
            subscribedTopics.remove(topic)
        }
    }

    @Test
    fun testPlainProjectNameIsUsedAsRoom() {
        assertEquals("MyGame", roomIdFor("MyGame"))
    }

    @Test
    fun testSpacesAreKeptBecauseTheyAreValidInTopics() {
        assertEquals("My Game", roomIdFor("My Game"))
    }

    @Test
    fun testMultiLevelWildcardInProjectNameIsReplaced() {
        assertEquals("Game _1", roomIdFor("Game #1"))
    }

    @Test
    fun testSingleLevelWildcardInProjectNameIsReplaced() {
        assertEquals("Game_1", roomIdFor("Game+1"))
    }

    @Test
    fun testSlashInProjectNameIsReplacedSoSegmentsStayAligned() {
        assertEquals("a_b", roomIdFor("a/b"))
    }

    @Test
    fun testBlankProjectNameFallsBackToADefaultRoom() {
        assertEquals("room", roomIdFor("   "))
    }

    @Test
    fun testSanitisedRoomProducesAValidFilter() {
        val filter = MqttMultiplayerTransport.roomFilter(roomIdFor("Game #1"))
        assertEquals("catrobat/multiplayer/Game _1/#", filter)
        assertEquals(1, filter.count { it == '#' })
    }
}
