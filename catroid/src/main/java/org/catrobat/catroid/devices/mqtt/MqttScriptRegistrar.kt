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
import org.catrobat.catroid.content.EventWrapper
import org.catrobat.catroid.content.MqttScript
import org.catrobat.catroid.content.Project

class MqttScriptRegistrar(
    private val mqttManager: MqttManager,
    private val config: MqttConnectionConfig
) {

    // Subscribes before the stage starts: MQTT delivers nothing published before the
    // subscription existed.
    fun registerScriptsOf(project: Project) {
        var registeredCount = 0
        project.mqttScripts().forEach { script ->
            val topic = script.topic
            if (topic.isNullOrBlank()) {
                Log.w(TAG, "Skipping MQTT script with no topic")
                return@forEach
            }
            val dispatcher = MqttEventDispatcher(topic) { eventId, message ->
                bindMessageToVariables(script, message)
                project.fireToAllSprites(EventWrapper(eventId, false))
            }
            mqttManager.register(topic, dispatcher)
            if (!mqttManager.subscribe(config, topic)) {
                Log.e(TAG, "Failed to subscribe MQTT script topic '$topic'")
            }
            registeredCount++
        }
        Log.d(TAG, "Registered $registeredCount MQTT script(s)")
    }

    companion object {
        private val TAG = MqttScriptRegistrar::class.java.simpleName

        @JvmStatic
        fun bindMessageToVariables(script: MqttScript, message: ReceivedMqttMessage) {
            script.payloadVariable?.value = message.payload
            script.topicVariable?.value = message.topic
        }

        private fun Project.mqttScripts(): List<MqttScript> =
            sceneList.orEmpty()
                .flatMap { scene -> scene.spriteList.orEmpty() }
                .flatMap { sprite -> sprite.scriptList.orEmpty() }
                .filterIsInstance<MqttScript>()
    }
}
