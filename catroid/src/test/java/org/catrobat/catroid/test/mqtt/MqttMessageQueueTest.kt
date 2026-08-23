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

import org.catrobat.catroid.devices.mqtt.MqttMessageQueue
import org.catrobat.catroid.devices.mqtt.ReceivedMqttMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MqttMessageQueueTest {

    private lateinit var queue: MqttMessageQueue

    @Before
    fun setUp() {
        queue = MqttMessageQueue()
    }

    @Test
    fun testQueueIsEmptyInitially() {
        assertTrue(queue.isEmpty())
        assertEquals(0, queue.size)
    }

    @Test
    fun testDequeueOnEmptyQueueReturnsNull() {
        assertNull(queue.dequeue())
    }

    @Test
    fun testDrainOnEmptyQueueReturnsEmptyList() {
        assertTrue(queue.drain().isEmpty())
    }

    @Test
    fun testEnqueueMakesQueueNonEmpty() {
        queue.enqueue(ReceivedMqttMessage("home/temp", "22.5"))
        assertFalse(queue.isEmpty())
        assertEquals(1, queue.size)
    }

    @Test
    fun testDequeueReturnsEnqueuedMessage() {
        queue.enqueue(ReceivedMqttMessage("home/temp", "22.5"))
        assertEquals(ReceivedMqttMessage("home/temp", "22.5"), queue.dequeue())
    }

    @Test
    fun testDequeueRemovesMessage() {
        queue.enqueue(ReceivedMqttMessage("home/temp", "22.5"))
        queue.dequeue()
        assertTrue(queue.isEmpty())
    }

    @Test
    fun testDequeuePreservesFifoOrder() {
        queue.enqueue(ReceivedMqttMessage("t", "first"))
        queue.enqueue(ReceivedMqttMessage("t", "second"))
        queue.enqueue(ReceivedMqttMessage("t", "third"))
        assertEquals("first", queue.dequeue()?.payload)
        assertEquals("second", queue.dequeue()?.payload)
        assertEquals("third", queue.dequeue()?.payload)
    }

    @Test
    fun testDrainPreservesFifoOrder() {
        queue.enqueue(ReceivedMqttMessage("t", "first"))
        queue.enqueue(ReceivedMqttMessage("t", "second"))
        assertEquals(listOf("first", "second"), queue.drain().map { it.payload })
    }

    @Test
    fun testDrainEmptiesQueue() {
        queue.enqueue(ReceivedMqttMessage("t", "first"))
        queue.drain()
        assertTrue(queue.isEmpty())
    }

    @Test
    fun testSamePayloadOnDifferentTopicsAreBothQueued() {
        queue.enqueue(ReceivedMqttMessage("home/a", "ON"))
        queue.enqueue(ReceivedMqttMessage("home/b", "ON"))
        assertEquals(listOf("home/a", "home/b"), queue.drain().map { it.topic })
    }

    @Test
    fun testDuplicateMessagesAreNotCollapsed() {
        queue.enqueue(ReceivedMqttMessage("t", "same"))
        queue.enqueue(ReceivedMqttMessage("t", "same"))
        assertEquals(2, queue.size)
    }

    @Test
    fun testClearEmptiesQueue() {
        queue.enqueue(ReceivedMqttMessage("t", "first"))
        queue.clear()
        assertTrue(queue.isEmpty())
    }

    @Test
    fun testQueueIsBoundedAtCapacity() {
        val bounded = MqttMessageQueue(capacity = 3)
        repeat(5) { bounded.enqueue(ReceivedMqttMessage("t", "message-$it")) }
        assertEquals(3, bounded.size)
    }

    @Test
    fun testOldestMessageIsDroppedWhenCapacityExceeded() {
        val bounded = MqttMessageQueue(capacity = 3)
        repeat(5) { bounded.enqueue(ReceivedMqttMessage("t", "message-$it")) }
        assertEquals(
            listOf("message-2", "message-3", "message-4"),
            bounded.drain().map { it.payload }
        )
    }

    @Test
    fun testEmptyPayloadIsQueued() {
        queue.enqueue(ReceivedMqttMessage("t", ""))
        assertEquals("", queue.dequeue()?.payload)
    }
}
