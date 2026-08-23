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

import org.catrobat.catroid.devices.mqtt.MqttListener
import org.catrobat.catroid.devices.mqtt.MqttManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MqttManagerRoutingTest {

    private lateinit var fakeClient: FakeMqttClient
    private lateinit var fakeFactory: FakeMqttClientFactory
    private lateinit var manager: MqttManager

    @Before
    fun setUp() {
        fakeClient = FakeMqttClient()
        fakeFactory = FakeMqttClientFactory(fakeClient)
        manager = MqttManager(fakeFactory, { it.run() }) { it.run() }
    }

    private val defaultConfig = TEST_CONFIG

    private fun connected(): MqttManager = manager.also { it.connect(defaultConfig) }

    private fun deliverFromBroker(topic: String, payload: String) {
        connected()
        fakeClient.deliver(topic, payload)
    }

    @Test
    fun testDispatchDeliversTopicAndPayloadToRegisteredListener() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        manager.dispatchMessage("home/temp", "22.5")
        assertEquals(listOf("home/temp" to "22.5"), listener.received)
    }

    @Test
    fun testDispatchDeliversToAllListenersOfSameTopic() {
        val first = FakeListener()
        val second = FakeListener()
        manager.register("home/temp", first)
        manager.register("home/temp", second)
        manager.dispatchMessage("home/temp", "22.5")
        assertEquals(1, first.received.size)
        assertEquals(1, second.received.size)
    }

    @Test
    fun testDispatchDoesNotDeliverToListenerOfDifferentTopic() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        manager.dispatchMessage("home/light", "ON")
        assertTrue(listener.received.isEmpty())
    }

    @Test
    fun testDispatchIgnoresUnknownTopic() {
        manager.dispatchMessage("home/unknown", "payload")
    }

    @Test
    fun testUnregisteredListenerStopsReceivingMessages() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        manager.unregister("home/temp", listener)
        manager.dispatchMessage("home/temp", "22.5")
        assertTrue(listener.received.isEmpty())
    }

    @Test
    fun testUnregisteringOneListenerKeepsOthersOnSameTopic() {
        val removed = FakeListener()
        val kept = FakeListener()
        manager.register("home/temp", removed)
        manager.register("home/temp", kept)
        manager.unregister("home/temp", removed)
        manager.dispatchMessage("home/temp", "22.5")
        assertTrue(removed.received.isEmpty())
        assertEquals(1, kept.received.size)
    }

    @Test
    fun testUnregisteringLastListenerDropsTopic() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        manager.unregister("home/temp", listener)
        assertTrue(manager.registeredTopics.isEmpty())
    }

    @Test
    fun testListenerCanBeReRegisteredAfterUnregister() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        manager.unregister("home/temp", listener)
        manager.register("home/temp", listener)
        manager.dispatchMessage("home/temp", "22.5")
        assertEquals(1, listener.received.size)
    }

    @Test
    fun testRegisterRejectsBlankTopic() {
        manager.register("  ", FakeListener())
        assertTrue(manager.registeredTopics.isEmpty())
    }

    @Test
    fun testUnregisteringFromUnknownTopicDoesNotCrash() {
        manager.unregister("home/nothing", FakeListener())
    }

    @Test
    fun testThrowingListenerDoesNotPreventOtherListeners() {
        val throwing = MqttListener { _, _ -> throw IllegalStateException("boom") }
        val healthy = FakeListener()
        manager.register("home/temp", throwing)
        manager.register("home/temp", healthy)
        manager.dispatchMessage("home/temp", "22.5")
        assertEquals(1, healthy.received.size)
    }

    @Test
    fun testSingleLevelWildcardListenerReceivesMatchingTopic() {
        val listener = FakeListener()
        manager.register("home/+/state", listener)
        manager.dispatchMessage("home/light/state", "ON")
        assertEquals(listOf("home/light/state" to "ON"), listener.received)
    }

    @Test
    fun testMultiLevelWildcardListenerReceivesNestedTopic() {
        val listener = FakeListener()
        manager.register("home/#", listener)
        manager.dispatchMessage("home/kitchen/light/state", "ON")
        assertEquals(1, listener.received.size)
    }

    @Test
    fun testWildcardListenerReceivesActualTopicNotFilter() {
        val listener = FakeListener()
        manager.register("home/#", listener)
        manager.dispatchMessage("home/kitchen/light", "ON")
        assertEquals("home/kitchen/light", listener.received.single().first)
    }

    @Test
    fun testWildcardListenerDoesNotReceiveNonMatchingTopic() {
        val listener = FakeListener()
        manager.register("home/+/state", listener)
        manager.dispatchMessage("garden/light/state", "ON")
        assertTrue(listener.received.isEmpty())
    }

    @Test
    fun testExactAndWildcardListenersBothReceiveMessage() {
        val exact = FakeListener()
        val wildcard = FakeListener()
        manager.register("home/light", exact)
        manager.register("home/#", wildcard)
        manager.dispatchMessage("home/light", "ON")
        assertEquals(1, exact.received.size)
        assertEquals(1, wildcard.received.size)
    }

    @Test
    fun testUnregisteringWildcardStopsDelivery() {
        val listener = FakeListener()
        manager.register("home/#", listener)
        manager.unregister("home/#", listener)
        manager.dispatchMessage("home/light", "ON")
        assertTrue(listener.received.isEmpty())
    }

    @Test
    fun testWildcardSubscriptionReachesListenerEndToEnd() {
        val listener = FakeListener()
        manager.register("home/+/state", listener)
        connected().subscribe(defaultConfig, "home/+/state")
        fakeClient.deliver("home/light/state", "ON")
        manager.dispatchPendingMessages()
        assertEquals(listOf("home/light/state" to "ON"), listener.received)
    }

    @Test
    fun testBrokerMessageIsQueuedNotRoutedImmediately() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        deliverFromBroker("home/temp", "22.5")
        assertTrue(listener.received.isEmpty())
        assertEquals(1, manager.pendingMessageCount)
    }

    @Test
    fun testDispatchPendingMessagesRoutesQueuedMessage() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        deliverFromBroker("home/temp", "22.5")
        manager.dispatchPendingMessages()
        assertEquals(listOf("home/temp" to "22.5"), listener.received)
    }

    @Test
    fun testDispatchPendingMessagesEmptiesQueue() {
        manager.register("home/temp", FakeListener())
        deliverFromBroker("home/temp", "22.5")
        manager.dispatchPendingMessages()
        assertEquals(0, manager.pendingMessageCount)
    }

    @Test
    fun testDispatchPendingMessagesPreservesArrivalOrder() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        connected()
        fakeClient.deliver("home/temp", "first")
        fakeClient.deliver("home/temp", "second")
        manager.dispatchPendingMessages()
        assertEquals(listOf("first", "second"), listener.received.map { it.second })
    }

    @Test
    fun testDispatchPendingMessagesWithEmptyQueueDoesNotCrash() {
        manager.dispatchPendingMessages()
    }

    @Test
    fun testBrokerPayloadIsDecodedAsUtf8() {
        val listener = FakeListener()
        manager.register("home/text", listener)
        connected()
        fakeClient.deliverBytes("home/text", "grüße✓".toByteArray(Charsets.UTF_8))
        manager.dispatchPendingMessages()
        assertEquals("grüße✓", listener.received.single().second)
    }

    @Test
    fun testDisconnectDiscardsQueuedMessages() {
        manager.register("home/temp", FakeListener())
        deliverFromBroker("home/temp", "22.5")
        manager.disconnect()
        assertEquals(0, manager.pendingMessageCount)
    }

    private class FakeListener : MqttListener {
        val received = mutableListOf<Pair<String, String>>()
        override fun onMessageReceived(topic: String, payload: String) {
            received.add(topic to payload)
        }
    }

    @Test
    fun testDisconnectDropsRegisteredListeners() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        connected()
        manager.disconnect()
        manager.dispatchMessage("home/temp", "22.5")
        assertTrue(listener.received.isEmpty())
    }

    @Test
    fun testDisconnectDropsWildcardListeners() {
        val listener = FakeListener()
        manager.register("home/#", listener)
        connected()
        manager.disconnect()
        manager.dispatchMessage("home/kitchen/temp", "22.5")
        assertTrue(listener.received.isEmpty())
    }

    @Test
    fun testDisconnectDropsListenersEvenWhenConnectNeverSucceeded() {
        val listener = FakeListener()
        manager.register("home/temp", listener)
        manager.disconnect()
        manager.dispatchMessage("home/temp", "22.5")
        assertTrue(listener.received.isEmpty())
    }

    @Test
    fun testListenersOfEarlierRunsStopReceiving() {
        val earlierRuns = (1..3).map { FakeListener() }
        earlierRuns.forEach { stale ->
            manager.register("home/temp", stale)
            connected()
            manager.disconnect()
        }
        val currentRun = FakeListener()
        manager.register("home/temp", currentRun)
        connected()

        manager.dispatchMessage("home/temp", "22.5")

        assertEquals(1, currentRun.received.size)
        assertEquals(0, earlierRuns.sumOf { it.received.size })
    }
}
