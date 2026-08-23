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

import org.catrobat.catroid.content.eventids.MqttEventId
import org.catrobat.catroid.devices.mqtt.MqttEventDispatcher
import org.catrobat.catroid.devices.mqtt.ReceivedMqttMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MqttEventDispatcherTest {

    private val firedEvents = mutableListOf<Pair<MqttEventId, ReceivedMqttMessage>>()

    private fun dispatcherFor(filter: String) =
        MqttEventDispatcher(filter) { eventId, message -> firedEvents.add(eventId to message) }

    @Test
    fun testMessageFiresEvent() {
        dispatcherFor("home/temp").onMessageReceived("home/temp", "22.5")
        assertEquals(1, firedEvents.size)
    }

    @Test
    fun testEventCarriesTheRegisteredFilter() {
        dispatcherFor("home/#").onMessageReceived("home/kitchen/temp", "22.5")
        assertEquals("home/#", firedEvents.single().first.topicFilter)
    }

    @Test
    fun testEventMetadataPreservesConcreteTopicAndPayload() {
        dispatcherFor("home/#").onMessageReceived("home/kitchen/temp", "22.5")
        assertEquals(ReceivedMqttMessage("home/kitchen/temp", "22.5"), firedEvents.single().second)
    }

    @Test
    fun testEachMessageFiresItsOwnEvent() {
        val dispatcher = dispatcherFor("home/temp")
        dispatcher.onMessageReceived("home/temp", "22.5")
        dispatcher.onMessageReceived("home/temp", "23.0")
        assertEquals(listOf("22.5", "23.0"), firedEvents.map { it.second.payload })
    }

    @Test
    fun testEmptyPayloadStillFiresEvent() {
        dispatcherFor("home/temp").onMessageReceived("home/temp", "")
        assertEquals("", firedEvents.single().second.payload)
    }

    @Test
    fun testLastMessageIsNullBeforeAnyMessage() {
        assertNull(dispatcherFor("home/temp").lastMessage)
    }

    @Test
    fun testLastMessageHoldsMostRecentMessage() {
        val dispatcher = dispatcherFor("home/#")
        dispatcher.onMessageReceived("home/a", "first")
        dispatcher.onMessageReceived("home/b", "second")
        assertEquals(ReceivedMqttMessage("home/b", "second"), dispatcher.lastMessage)
    }

    @Test
    fun testEventIdsForSameFilterAreEqual() {
        assertEquals(MqttEventId("home/temp"), MqttEventId("home/temp"))
    }

    @Test
    fun testEventIdsForSameFilterShareHashCode() {
        assertEquals(MqttEventId("home/temp").hashCode(), MqttEventId("home/temp").hashCode())
    }

    @Test
    fun testEventIdsForDifferentFiltersAreNotEqual() {
        assertNotEquals(MqttEventId("home/temp"), MqttEventId("home/light"))
    }

    @Test
    fun testWildcardFilterIsNotEqualToMatchingConcreteTopic() {
        assertNotEquals(MqttEventId("home/#"), MqttEventId("home/temp"))
    }

    @Test
    fun testEventIdIsNotEqualToOtherEventTypes() {
        assertTrue(MqttEventId("home/temp") != Any())
    }

    @Test
    fun testEventIdLookupWorksAcrossInstances() {
        val map = mapOf(MqttEventId("home/#") to "script")
        assertEquals("script", map[MqttEventId("home/#")])
    }
}
