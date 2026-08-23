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

class MqttManagerPublishTest {

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
    fun testPublishReturnsTrueOnSuccess() {
        assertTrue(connected().publish(defaultConfig, "home/light", "ON"))
        assertTrue(fakeClient.publishCalled)
    }

    @Test
    fun testPublishSendsTopicAndPayload() {
        connected().publish(defaultConfig, "home/light", "ON")
        assertEquals("home/light", fakeClient.lastPublishTopic)
        assertEquals("ON", String(fakeClient.lastPublishMessage!!.payload, Charsets.UTF_8))
    }

    @Test
    fun testPublishEncodesPayloadAsUtf8() {
        connected().publish(defaultConfig, "home/text", "grüße✓")
        assertEquals("grüße✓", String(fakeClient.lastPublishMessage!!.payload, Charsets.UTF_8))
    }

    @Test
    fun testPublishAllowsEmptyPayload() {
        assertTrue(connected().publish(defaultConfig, "home/light", ""))
        assertEquals(0, fakeClient.lastPublishMessage!!.payload.size)
    }

    @Test
    fun testPublishAppliesQosAndRetainedFlag() {
        connected().publish(defaultConfig, "home/light", "ON", qos = 2, retained = true)
        assertEquals(2, fakeClient.lastPublishMessage!!.qos)
        assertTrue(fakeClient.lastPublishMessage!!.isRetained)
    }

    @Test
    fun testPublishDefaultsToQosZeroNotRetained() {
        connected().publish(defaultConfig, "home/light", "ON")
        assertEquals(0, fakeClient.lastPublishMessage!!.qos)
        assertFalse(fakeClient.lastPublishMessage!!.isRetained)
    }

    @Test
    fun testPublishRejectsBlankTopic() {
        assertFalse(connected().publish(defaultConfig, "   ", "ON"))
        assertFalse(fakeClient.publishCalled)
    }

    @Test
    fun testPublishRejectsMultiLevelWildcardTopic() {
        assertFalse(connected().publish(defaultConfig, "home/#", "ON"))
        assertFalse(fakeClient.publishCalled)
    }

    @Test
    fun testPublishRejectsSingleLevelWildcardTopic() {
        assertFalse(connected().publish(defaultConfig, "home/+/state", "ON"))
        assertFalse(fakeClient.publishCalled)
    }

    @Test
    fun testPublishRejectsQosAboveRange() {
        assertFalse(connected().publish(defaultConfig, "home/light", "ON", qos = 3))
        assertFalse(fakeClient.publishCalled)
    }

    @Test
    fun testPublishRejectsNegativeQos() {
        assertFalse(connected().publish(defaultConfig, "home/light", "ON", qos = -1))
        assertFalse(fakeClient.publishCalled)
    }

    @Test
    fun testPublishQueuesRatherThanConnectingWhenDisconnected() {
        assertTrue(manager.publish(defaultConfig, "home/light", "ON"))
        assertFalse(fakeClient.connectCalled)
        assertFalse(fakeClient.publishCalled)
        assertEquals(1, manager.pendingPublishCount)
    }

    @Test
    fun testPublishDoesNotReconnectWhenAlreadyConnected() {
        connected()
        fakeClient.connectCalled = false
        manager.publish(defaultConfig, "home/light", "ON")
        assertFalse(fakeClient.connectCalled)
    }

    @Test
    fun testPublishIsRequeuedWhenClientThrows() {
        connected()
        fakeClient.throwOnPublish = true
        assertTrue(manager.publish(defaultConfig, "home/light", "ON"))
        assertEquals(1, manager.pendingPublishCount)
    }

    @Test
    fun testPublishIsQueuedWhenBrokerUnreachable() {
        fakeClient.throwOnConnect = true
        assertTrue(manager.publish(defaultConfig, "home/light", "ON"))
        assertFalse(fakeClient.publishCalled)
        assertEquals(1, manager.pendingPublishCount)
    }

    @Test
    fun testQueuedMessageIsSentOnceConnected() {
        fakeClient.throwOnConnect = true
        manager.publish(defaultConfig, "home/light", "ON")
        fakeClient.throwOnConnect = false
        manager.connect(defaultConfig)
        assertTrue(fakeClient.publishCalled)
        assertEquals("home/light", fakeClient.lastPublishTopic)
        assertEquals(0, manager.pendingPublishCount)
    }

    @Test
    fun testQueuedMessagesAreFlushedInPublishOrder() {
        fakeClient.throwOnConnect = true
        manager.publish(defaultConfig, "home/a", "first")
        manager.publish(defaultConfig, "home/b", "second")
        fakeClient.throwOnConnect = false
        manager.connect(defaultConfig)
        assertEquals(listOf("home/a", "home/b"), fakeClient.publishedTopics)
    }

    @Test
    fun testQueuedMessageKeepsQosAndRetainedFlag() {
        fakeClient.throwOnConnect = true
        manager.publish(defaultConfig, "home/light", "ON", qos = 2, retained = true)
        fakeClient.throwOnConnect = false
        manager.connect(defaultConfig)
        assertEquals(2, fakeClient.lastPublishMessage!!.qos)
        assertTrue(fakeClient.lastPublishMessage!!.isRetained)
    }

    @Test
    fun testFailedPublishIsQueuedForRetry() {
        connected()
        fakeClient.throwOnPublish = true
        manager.publish(defaultConfig, "home/light", "ON")
        assertEquals(1, manager.pendingPublishCount)
    }

    @Test
    fun testInvalidTopicIsNotQueued() {
        fakeClient.throwOnConnect = true
        manager.publish(defaultConfig, "home/#", "ON")
        assertEquals(0, manager.pendingPublishCount)
    }

    @Test
    fun testDisconnectDropsQueuedMessages() {
        connected()
        fakeClient.throwOnPublish = true
        manager.publish(defaultConfig, "home/light", "ON")
        manager.disconnect()
        assertEquals(0, manager.pendingPublishCount)
    }

    @Test
    fun testFlushStopsAndRequeuesWhenConnectionDropsMidway() {
        fakeClient.throwOnConnect = true
        manager.publish(defaultConfig, "home/a", "first")
        manager.publish(defaultConfig, "home/b", "second")
        fakeClient.throwOnConnect = false
        fakeClient.throwOnPublish = true
        manager.connect(defaultConfig)
        assertEquals(2, manager.pendingPublishCount)
    }
    @Test
    fun testPublishRejectsTopicsTheBrokerWouldRefuse() {
        connected()
        assertFalse(manager.publish(defaultConfig, "home/+/light", "ON"))
        assertFalse(manager.publish(defaultConfig, "home/#", "ON"))
        assertFalse(fakeClient.publishCalled)
    }
}
