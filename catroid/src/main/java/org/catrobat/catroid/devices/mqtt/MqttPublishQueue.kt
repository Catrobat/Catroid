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

data class PendingPublish(
    val topic: String,
    val payload: String,
    val qos: Int,
    val retained: Boolean
)

class MqttPublishQueue(private val capacity: Int = DEFAULT_CAPACITY) {

    private val queue = LinkedBlockingQueue<PendingPublish>(capacity)

    val size: Int get() = queue.size

    fun isEmpty(): Boolean = queue.isEmpty()

    fun enqueue(message: PendingPublish) {
        if (!queue.offer(message)) {
            val dropped = queue.poll()
            Log.w(TAG, "Publish queue full ($capacity), dropped message for '${dropped?.topic}'")
            queue.offer(message)
        }
    }

    fun drain(): List<PendingPublish> {
        val drained = mutableListOf<PendingPublish>()
        queue.drainTo(drained)
        return drained
    }

    fun requeueAll(messages: List<PendingPublish>) {
        val remaining = drain()
        // Head first: these are the messages that never reached the broker, so
        // enqueue's drop-oldest rule must not discard them.
        (messages + remaining).take(capacity).forEach { enqueue(it) }
    }

    fun clear() = queue.clear()

    companion object {
        private val TAG = MqttPublishQueue::class.java.simpleName
        const val DEFAULT_CAPACITY = 100
    }
}
