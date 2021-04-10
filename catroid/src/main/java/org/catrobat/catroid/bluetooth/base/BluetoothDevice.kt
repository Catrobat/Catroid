/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.bluetooth.base

import org.catrobat.catroid.devices.arduino.Arduino
import org.catrobat.catroid.devices.arduino.phiro.Phiro
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT
import org.catrobat.catroid.stage.StageResourceInterface
import java.util.UUID

interface BluetoothDevice : StageResourceInterface {
    val name: String?
    val deviceType: Class<out BluetoothDevice?>?
    fun setConnection(connection: BluetoothConnection?)
    fun disconnect()
    val isAlive: Boolean
    val bluetoothDeviceUUID: UUID?

    companion object {
        @JvmField
		val LEGO_NXT = LegoNXT::class.java
        @JvmField
		val LEGO_EV3 = LegoEV3::class.java
        @JvmField
		val PHIRO = Phiro::class.java
        @JvmField
		val ARDUINO = Arduino::class.java
    }
}