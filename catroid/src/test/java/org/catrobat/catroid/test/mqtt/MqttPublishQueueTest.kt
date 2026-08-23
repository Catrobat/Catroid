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

import org.catrobat.catroid.devices.mqtt.MqttPublishQueue
import org.catrobat.catroid.devices.mqtt.PendingPublish
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MqttPublishQueueTest {

    private lateinit var queue: MqttPublishQueue

    @Before
    fun setUp() {
        queue = MqttPublishQueue()
    }

    private fun message(topic: String) = PendingPublish(topic, "payload", 0, false)

    @Test
    fun testQueueIsEmptyInitially() {
        assertTrue(queue.isEmpty())
        assertEquals(0, queue.size)
    }

    @Test
    fun testDrainReturnsMessagesOldestFirst() {
        queue.enqueue(message("a"))
        queue.enqueue(message("b"))
        queue.enqueue(message("c"))

        assertEquals(listOf("a", "b", "c"), queue.drain().map { it.topic })
        assertTrue(queue.isEmpty())
    }

    @Test
    fun testRequeueAllPutsMessagesBackAtTheHead() {
        queue.enqueue(message("published-later"))
        queue.requeueAll(listOf(message("failed-earlier")))

        assertEquals(listOf("failed-earlier", "published-later"), queue.drain().map { it.topic })
    }

    @Test
    fun testOldestIsDroppedWhenCapacityIsReached() {
        val small = MqttPublishQueue(capacity = 2)
        small.enqueue(message("a"))
        small.enqueue(message("b"))
        small.enqueue(message("c"))

        assertEquals(2, small.size)
        assertEquals(listOf("b", "c"), small.drain().map { it.topic })
    }

    @Test
    fun testRequeueAtCapacityKeepsTheMessagesThatStillNeedSending() {
        val small = MqttPublishQueue(capacity = 2)
        small.enqueue(message("already-queued-1"))
        small.enqueue(message("already-queued-2"))

        // The restored messages are the ones that never reached the broker.
        small.requeueAll(listOf(message("failed-1"), message("failed-2")))

        assertEquals(listOf("failed-1", "failed-2"), small.drain().map { it.topic })
    }
}
