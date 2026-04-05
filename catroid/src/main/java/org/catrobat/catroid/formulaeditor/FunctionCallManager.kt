/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class FunctionCallManager private constructor() {

    private val callIdCounter = AtomicLong(0)
    private val pendingCalls = ConcurrentHashMap<Long, CallResult>()

    fun createCall(): Long {
        val id = callIdCounter.incrementAndGet()
        pendingCalls[id] = CallResult()
        return id
    }

    fun waitForResult(callId: Long): Any {
        val result = pendingCalls[callId] ?: return DEFAULT_VALUE

        return try {
            if (result.latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                result.value ?: DEFAULT_VALUE
            } else {
                DEFAULT_VALUE
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            DEFAULT_VALUE
        } finally {
            pendingCalls.remove(callId)
        }
    }

    fun setResult(callId: Long, value: Any?) {
        val result = pendingCalls[callId]
        if (result != null) {
            result.value = value
            result.latch.countDown()
        }
    }

    private class CallResult {
        val latch = CountDownLatch(1)

        @Volatile
        var value: Any? = null
    }

    companion object {
        private const val TIMEOUT_SECONDS = 30L
        private const val DEFAULT_VALUE = 0.0

        @JvmStatic
        val instance = FunctionCallManager()
    }
}
