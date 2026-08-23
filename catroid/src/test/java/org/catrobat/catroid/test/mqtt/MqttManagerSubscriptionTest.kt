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

import org.catrobat.catroid.devices.mqtt.MqttManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MqttManagerSubscriptionTest {

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

    @Test
    fun testSubscribeReturnsTrueOnSuccess() {
        assertTrue(connected().subscribe(defaultConfig, "home/light"))
        assertTrue(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeSendsTopicAndQos() {
        connected().subscribe(defaultConfig, "home/light", qos = 2)
        assertEquals(listOf("home/light"), fakeClient.subscribedTopics)
        assertEquals(2, fakeClient.lastSubscribeQos)
    }

    @Test
    fun testSubscribeAcceptsMultiLevelWildcard() {
        assertTrue(connected().subscribe(defaultConfig, "home/#"))
        assertEquals(listOf("home/#"), fakeClient.subscribedTopics)
    }

    @Test
    fun testSubscribeAcceptsSingleLevelWildcard() {
        assertTrue(connected().subscribe(defaultConfig, "home/+/state"))
        assertEquals(listOf("home/+/state"), fakeClient.subscribedTopics)
    }

    @Test
    fun testSubscribeRejectsBlankTopic() {
        assertFalse(connected().subscribe(defaultConfig, "  "))
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeRejectsInvalidQos() {
        assertFalse(connected().subscribe(defaultConfig, "home/light", qos = 3))
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeTracksActiveSubscription() {
        connected().subscribe(defaultConfig, "home/light")
        assertEquals(setOf("home/light"), manager.activeSubscriptions)
    }

    @Test
    fun testDuplicateSubscribeDoesNotCallClientAgain() {
        connected().subscribe(defaultConfig, "home/light")
        fakeClient.subscribeCalled = false
        assertTrue(manager.subscribe(defaultConfig, "home/light"))
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeLazilyConnectsWhenDisconnected() {
        assertTrue(manager.subscribe(defaultConfig, "home/light"))
        assertTrue(fakeClient.connectCalled)
        assertTrue(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeReturnsFalseWhenLazyConnectFails() {
        fakeClient.throwOnConnect = true
        assertFalse(manager.subscribe(defaultConfig, "home/light"))
        assertFalse(fakeClient.subscribeCalled)
    }

    @Test
    fun testSubscribeReturnsFalseWhenClientThrows() {
        connected()
        fakeClient.throwOnSubscribe = true
        assertFalse(manager.subscribe(defaultConfig, "home/light"))
        assertTrue(manager.activeSubscriptions.isEmpty())
    }

    @Test
    fun testUnsubscribeReturnsTrueOnSuccess() {
        connected().subscribe(defaultConfig, "home/light")
        assertTrue(manager.unsubscribe("home/light"))
        assertTrue(fakeClient.unsubscribeCalled)
    }

    @Test
    fun testUnsubscribeStopsTrackingTopic() {
        connected().subscribe(defaultConfig, "home/light")
        manager.unsubscribe("home/light")
        assertTrue(manager.activeSubscriptions.isEmpty())
    }

    @Test
    fun testUnsubscribeWhenNotSubscribedDoesNotCallClient() {
        connected()
        assertTrue(manager.unsubscribe("home/light"))
        assertFalse(fakeClient.unsubscribeCalled)
    }

    @Test
    fun testUnsubscribeRejectsBlankTopic() {
        connected()
        assertFalse(manager.unsubscribe("  "))
    }

    @Test
    fun testUnsubscribeReturnsFalseWhenClientThrows() {
        connected().subscribe(defaultConfig, "home/light")
        fakeClient.throwOnUnsubscribe = true
        assertFalse(manager.unsubscribe("home/light"))
        assertEquals(setOf("home/light"), manager.activeSubscriptions)
    }

    @Test
    fun testDisconnectClearsSubscriptions() {
        connected().subscribe(defaultConfig, "home/light")
        manager.disconnect()
        assertTrue(manager.activeSubscriptions.isEmpty())
    }

    @Test
    fun testSubscriptionIsRestoredAfterReconnect() {
        connected().subscribe(defaultConfig, "home/light")
        fakeClient.connected = false
        manager.connect(defaultConfig)
        assertEquals(setOf("home/light"), manager.activeSubscriptions)
    }

    @Test
    fun testRestoredSubscriptionIsReissuedToTheNewSession() {
        connected().subscribe(defaultConfig, "home/light")
        fakeClient.connected = false
        fakeClient.subscribedTopics.clear()
        manager.connect(defaultConfig)
        assertEquals(listOf("home/light"), fakeClient.subscribedTopics)
    }

    @Test
    fun testRestoredSubscriptionKeepsItsQos() {
        connected().subscribe(defaultConfig, "home/light", qos = 2)
        fakeClient.connected = false
        fakeClient.lastSubscribeQos = -1
        manager.connect(defaultConfig)
        assertEquals(2, fakeClient.lastSubscribeQos)
    }

    @Test
    fun testAllSubscriptionsAreRestoredAfterReconnect() {
        connected().subscribe(defaultConfig, "home/light")
        manager.subscribe(defaultConfig, "home/+/state")
        fakeClient.connected = false
        manager.connect(defaultConfig)
        assertEquals(setOf("home/light", "home/+/state"), manager.activeSubscriptions)
    }

    @Test
    fun testMessagesReachListenersAgainAfterReconnect() {
        val listener = FakeListener()
        manager.register("home/light", listener)
        connected().subscribe(defaultConfig, "home/light")
        fakeClient.connected = false
        manager.connect(defaultConfig)
        fakeClient.deliver("home/light", "ON")
        manager.dispatchPendingMessages()
        assertEquals(listOf("home/light" to "ON"), listener.received)
    }

    @Test
    fun testSubscriptionsAreNotRestoredAfterIntentionalDisconnect() {
        connected().subscribe(defaultConfig, "home/light")
        manager.disconnect()
        manager.connect(defaultConfig)
        assertTrue(manager.activeSubscriptions.isEmpty())
    }

    @Test
    fun testBackoffStartsAtOneSecond() {
        assertEquals(1000L, manager.backoffDelayMillis(0))
    }

    @Test
    fun testBackoffDoublesPerAttempt() {
        assertEquals(2000L, manager.backoffDelayMillis(1))
        assertEquals(4000L, manager.backoffDelayMillis(2))
        assertEquals(8000L, manager.backoffDelayMillis(3))
    }

    @Test
    fun testBackoffIsCappedAtOneMinute() {
        assertEquals(60_000L, manager.backoffDelayMillis(20))
    }

    @Test
    fun testBackoffTreatsNegativeAttemptAsFirst() {
        assertEquals(1000L, manager.backoffDelayMillis(-1))
    }

    @Test
    fun testReconnectWithoutPreviousConfigDoesNothing() {
        assertFalse(manager.reconnectNow())
        assertFalse(fakeClient.connectCalled)
    }

    @Test
    fun testReconnectUsesLastSuccessfulConfiguration() {
        connected()
        fakeClient.connected = false
        fakeClient.connectCalled = false
        assertTrue(manager.reconnectNow())
        assertTrue(fakeClient.connectCalled)
    }

    @Test
    fun testReconnectRestoresSubscriptionsAndFlushesQueue() {
        connected().subscribe(defaultConfig, "home/light")
        fakeClient.connected = false
        fakeClient.throwOnConnect = true
        manager.publish(defaultConfig, "home/light", "ON")
        assertEquals(1, manager.pendingPublishCount)

        fakeClient.throwOnConnect = false
        fakeClient.subscribedTopics.clear()
        assertTrue(manager.reconnectNow())

        assertEquals(listOf("home/light"), fakeClient.subscribedTopics)
        assertEquals(0, manager.pendingPublishCount)
    }

    @Test
    fun testSubscriptionSurvivesFailedReconnectAttempts() {
        connected().subscribe(defaultConfig, "home/light")
        fakeClient.connected = false
        fakeClient.throwOnConnect = true
        manager.connect(defaultConfig)
        manager.connect(defaultConfig)

        fakeClient.throwOnConnect = false
        fakeClient.subscribedTopics.clear()
        manager.connect(defaultConfig)

        assertEquals(listOf("home/light"), fakeClient.subscribedTopics)
    }

    @Test
    fun testReconnectAfterDisconnectIsRefused() {
        connected()
        manager.disconnect()
        fakeClient.connectCalled = false

        assertFalse(manager.reconnectNow())
        assertFalse("a queued retry reconnected after the stage ended", fakeClient.connectCalled)
    }

    @Test
    fun testRefusedReconnectDoesNotReEnableRetrying() {
        connected()
        manager.disconnect()
        manager.reconnectNow()
        assertFalse(manager.reconnectNow())
    }

    @Test
    fun testExplicitConnectReEnablesRetryingAfterDisconnect() {
        connected()
        manager.disconnect()
        fakeClient.connectCalled = false

        assertTrue(manager.connect(defaultConfig))
        fakeClient.connected = false
        assertTrue(manager.reconnectNow())
    }
    @Test
    fun testSubscribeRejectsFiltersTheBrokerWouldRefuse() {
        connected()
        assertFalse(manager.subscribe(defaultConfig, "a/#/b"))
        assertFalse(manager.subscribe(defaultConfig, "temp+"))
        assertFalse(manager.subscribe(defaultConfig, "a#b"))
        assertEquals(0, manager.activeSubscriptions.size)
    }

    @Test
    fun testSubscribeStillAcceptsValidWildcardFilters() {
        connected()
        assertTrue(manager.subscribe(defaultConfig, "home/#"))
        assertTrue(manager.subscribe(defaultConfig, "home/+/state"))
    }

    @Test
    fun testResubscribingWithHigherQosUpgradesTheSubscription() {
        connected()
        manager.subscribe(defaultConfig, "home/temp", 0)
        manager.subscribe(defaultConfig, "home/temp", 2)
        assertEquals(2, fakeClient.lastSubscribeQos)
    }
}
