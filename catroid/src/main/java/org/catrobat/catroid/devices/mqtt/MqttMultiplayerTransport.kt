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
import org.catrobat.catroid.formulaeditor.UserVariable
import java.net.URLDecoder
import java.net.URLEncoder
import org.koin.core.context.KoinContextHandler

class MqttMultiplayerTransport(private val mqttManager: MqttManager) {

    private var config: MqttConnectionConfig? = null
    private var roomId: String? = null
    private var senderId: String? = null
    private var listener: MqttListener? = null

    val isStarted: Boolean get() = listener != null

    fun start(
        config: MqttConnectionConfig,
        roomId: String,
        senderId: String,
        onVariableReceived: (String, String) -> Unit
    ): Boolean {
        if (roomId.isBlank() || senderId.isBlank()) {
            Log.e(TAG, "Cannot start multiplayer transport: room or sender id is blank")
            return false
        }
        stop()
        val safeSender = sanitiseSegment(senderId)
        this.config = config
        this.roomId = sanitiseSegment(roomId)
        this.senderId = safeSender

        val filter = roomFilter(this.roomId!!)
        val roomListener = MqttListener { topic, payload ->
            val message = parse(topic) ?: return@MqttListener
            if (message.sender == safeSender) {
                return@MqttListener
            }
            onVariableReceived(message.variable, payload)
        }
        mqttManager.register(filter, roomListener)
        listener = roomListener
        return mqttManager.subscribe(config, filter)
    }

    fun stop() {
        val filter = roomId?.let { roomFilter(it) }
        listener?.let { current ->
            filter?.let { mqttManager.unregister(it, current) }
        }
        listener = null
        roomId = null
        senderId = null
        config = null
    }

    fun sendVariable(variable: UserVariable?) {
        val name = variable?.name
        if (name.isNullOrBlank()) {
            return
        }
        val currentConfig = config
        val currentRoom = roomId
        val currentSender = senderId
        if (currentConfig == null || currentRoom == null || currentSender == null) {
            return
        }
        mqttManager.publish(
            currentConfig,
            "$TOPIC_ROOT/$currentRoom/$currentSender/${encodeSegment(name)}",
            variable.value?.toString().orEmpty()
        )
    }

    private data class RoomMessage(val sender: String, val variable: String)

    private fun parse(topic: String): RoomMessage? {
        val segments = topic.split('/')
        if (segments.size != EXPECTED_SEGMENTS) {
            Log.d(TAG, "Ignoring multiplayer topic with unexpected shape: '$topic'")
            return null
        }
        return RoomMessage(segments[SENDER_INDEX], decodeSegment(segments[VARIABLE_INDEX]))
    }

    companion object {
        private val TAG = MqttMultiplayerTransport::class.java.simpleName

        @JvmStatic
        fun activeOrNull(): MqttMultiplayerTransport? =
            KoinContextHandler.getOrNull()?.getOrNull(MqttMultiplayerTransport::class)

        const val TOPIC_ROOT = "catrobat/multiplayer"
        private const val EXPECTED_SEGMENTS = 5
        private const val SENDER_INDEX = 3
        private const val VARIABLE_INDEX = 4

        fun roomFilter(roomId: String) = "$TOPIC_ROOT/$roomId/#"

        private fun sanitiseSegment(value: String): String =
            value.trim().replace(Regex("[/+#]"), "_").ifEmpty { DEFAULT_ROOM }

        // Percent-encoded rather than stripped, because the receiver looks the
        // variable up by name. URLEncoder writes a space as '+', itself a wildcard.
        private fun encodeSegment(value: String): String =
            URLEncoder.encode(value, Charsets.UTF_8.name()).replace("+", "%20")

        private fun decodeSegment(value: String): String =
            try {
                URLDecoder.decode(value, Charsets.UTF_8.name())
            } catch (e: IllegalArgumentException) {
                Log.d(TAG, "Undecodable variable segment '$value', using it as sent", e)
                value
            }

        @JvmStatic
        fun roomIdFor(projectName: String): String =
            projectName.trim()
                .replace(Regex("[/+#]"), "_")
                .ifEmpty { DEFAULT_ROOM }

        private const val DEFAULT_ROOM = "room"
    }
}
