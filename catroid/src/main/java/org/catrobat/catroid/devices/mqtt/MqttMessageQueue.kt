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

package org.catrobat.catroid.devices.mqtt

import android.util.Log
import java.util.concurrent.LinkedBlockingQueue

data class ReceivedMqttMessage(val topic: String, val payload: String)

class MqttMessageQueue(private val capacity: Int = DEFAULT_CAPACITY) {

    private val queue = LinkedBlockingQueue<ReceivedMqttMessage>(capacity)

    val size: Int get() = queue.size

    fun isEmpty(): Boolean = queue.isEmpty()

    fun enqueue(message: ReceivedMqttMessage) {
        if (!queue.offer(message)) {
            val dropped = queue.poll()
            Log.w(TAG, "Incoming queue full ($capacity), dropped message on '${dropped?.topic}'")
            queue.offer(message)
        }
    }

    fun dequeue(): ReceivedMqttMessage? = queue.poll()

    fun drain(): List<ReceivedMqttMessage> {
        val drained = mutableListOf<ReceivedMqttMessage>()
        queue.drainTo(drained)
        return drained
    }

    fun clear() = queue.clear()

    companion object {
        private val TAG = MqttMessageQueue::class.java.simpleName
        const val DEFAULT_CAPACITY = 100
    }
}
