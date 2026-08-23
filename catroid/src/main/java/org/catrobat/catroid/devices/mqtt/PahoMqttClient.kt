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
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class PahoMqttClient(brokerUrl: String, clientId: String) : MqttClientInterface {
    private val client = MqttClient(brokerUrl, clientId, MemoryPersistence())

    init {
        // Paho waits forever by default, so an unacknowledged publish never returns.
        client.timeToWait = MAX_WAIT_MILLIS
    }

    override val isConnected get() = client.isConnected
    override fun connect(options: MqttConnectOptions) = client.connect(options)
    override fun disconnect() {
        try {
            client.disconnect(DISCONNECT_QUIESCE_MILLIS)
        } catch (e: MqttException) {
            Log.w(TAG, "Graceful disconnect failed, forcing", e)
            forceDisconnect()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun forceDisconnect() {
        try {
            client.disconnectForcibly(DISCONNECT_QUIESCE_MILLIS, DISCONNECT_QUIESCE_MILLIS)
        } catch (e: Exception) {
            Log.w(TAG, "Forced disconnect failed", e)
        }
    }
    override fun close() = client.close()
    override fun setCallback(callback: MqttCallback) = client.setCallback(callback)
    override fun publish(topic: String, message: MqttMessage) = client.publish(topic, message)
    override fun subscribe(topic: String, qos: Int) = client.subscribe(topic, qos)
    override fun unsubscribe(topic: String) = client.unsubscribe(topic)

    companion object {
        private val TAG = PahoMqttClient::class.java.simpleName
        private const val DISCONNECT_QUIESCE_MILLIS = 1000L

        private const val MAX_WAIT_MILLIS = 5000L
    }
}
