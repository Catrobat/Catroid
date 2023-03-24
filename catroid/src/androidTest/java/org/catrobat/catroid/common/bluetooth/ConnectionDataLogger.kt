/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.common.bluetooth

import androidx.annotation.VisibleForTesting
import com.google.common.base.Stopwatch
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils.getSubArray
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils.hookInConnection
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils.hookInConnectionFactoryWithBluetoothConnectionProxy
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils.resetConnectionHooks
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils
import org.catrobat.catroid.bluetooth.base.BluetoothConnection
import org.catrobat.catroid.common.bluetooth.BluetoothLogger
import org.catrobat.catroid.common.bluetooth.LocalConnectionProxy
import java.util.ArrayList
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class ConnectionDataLogger private constructor(local: Boolean) {
    private val sentMessages: BlockingQueue<ByteArray?> = LinkedBlockingQueue()
    private val receivedMessages: BlockingQueue<ByteArray?> = LinkedBlockingQueue()
    private var timeoutMilliSeconds = 15000
    @VisibleForTesting
    fun setTimeoutMilliSeconds(timeoutMilliSeconds: Int) {
        this.timeoutMilliSeconds = timeoutMilliSeconds
    }

    val nextSentMessage: ByteArray?
        get() = getNextSentMessage(0, 0)

    fun getNextSentMessage(messageOffset: Int, messageByteOffset: Int): ByteArray? {
        return getNextMessage(sentMessages, messageOffset, messageByteOffset)
    }

    fun getSentMessages(messageCountToWaitFor: Int): ArrayList<ByteArray?> {
        return getSentMessages(0, messageCountToWaitFor)
    }

    fun getSentMessages(messageByteOffset: Int, messageCountToWaitFor: Int): ArrayList<ByteArray?> {
        return getMessages(sentMessages, messageByteOffset, messageCountToWaitFor)
    }

    private fun getNextMessage(
        messages: BlockingQueue<ByteArray?>,
        messageOffset: Int,
        messageByteOffset: Int
    ): ByteArray? {
        val stopWatch = Stopwatch.createStarted()
        for (i in 0 until messageOffset) {
            val message = pollMessage(
                messages,
                timeoutMilliSeconds - stopWatch.elapsed(TimeUnit.MILLISECONDS)
                    .toInt()
            )
                ?: return null
        }
        val message = pollMessage(
            messages,
            timeoutMilliSeconds - stopWatch.elapsed(TimeUnit.MILLISECONDS)
                .toInt()
        )
            ?: return null
        return getSubArray(message, messageByteOffset)
    }

    private fun getMessages(
        messages: BlockingQueue<ByteArray?>,
        messageByteOffset: Int,
        messageCountToWaitFor: Int
    ): ArrayList<ByteArray?> {
        return if (messageCountToWaitFor == 0) {
            getMessages(messages, messageByteOffset)
        } else waitForMessages(messages, messageByteOffset, messageCountToWaitFor)
    }

    private fun waitForMessages(
        messages: BlockingQueue<ByteArray?>,
        messageByteOffset: Int,
        messageCountToWaitFor: Int
    ): ArrayList<ByteArray?> {
        val m = ArrayList<ByteArray?>()
        val stopWatch = Stopwatch.createStarted()
        do {
            val message = pollMessage(
                messages,
                timeoutMilliSeconds - stopWatch.elapsed(TimeUnit.MILLISECONDS)
                    .toInt()
            )
                ?: return m
            m.add(getSubArray(message, messageByteOffset))
        } while (m.size < messageCountToWaitFor && stopWatch.elapsed(TimeUnit.MILLISECONDS) < timeoutMilliSeconds)
        return m
    }

    var connectionProxy: BluetoothConnection? = null
        private set
    val logger: BluetoothLogger = object : BluetoothLogger {
        override fun logSentData(b: ByteArray?) {
            sentMessages.add(b)
        }

        override fun logReceivedData(b: ByteArray?) {
            receivedMessages.add(b)
        }

        override fun loggerAttached(proxy: BluetoothConnection?) {
            connectionProxy = proxy
        }
    }

    init {
        if (local) {
            connectionProxy = LocalConnectionProxy(logger)
            hookInConnection(connectionProxy as LocalConnectionProxy)
        } else {
            hookInConnectionFactoryWithBluetoothConnectionProxy(logger)
        }
    }

    fun disconnectAndDestroy() {
        if (connectionProxy != null) {
            connectionProxy!!.disconnect()
        }
        resetConnectionHooks()
    }

    companion object {
        private fun getMessages(
            messages: BlockingQueue<ByteArray?>,
            messageByteOffset: Int
        ): ArrayList<ByteArray?> {
            val m = ArrayList<ByteArray?>()
            var message: ByteArray? = null
            while (messages.poll().also { message = it } != null) {
                m.add(getSubArray(message, messageByteOffset))
            }
            return m
        }

        private fun pollMessage(
            messages: BlockingQueue<ByteArray?>,
            timeoutMilliSeconds: Int
        ): ByteArray? {
            return try {
                messages.poll(timeoutMilliSeconds.toLong(), TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                null
            }
        }

        @JvmStatic
		fun createLocalConnectionLogger(): ConnectionDataLogger {
            return ConnectionDataLogger(true)
        }
    }
}