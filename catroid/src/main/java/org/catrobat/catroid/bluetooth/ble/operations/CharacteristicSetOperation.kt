/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.bluetooth.ble.operations

import android.bluetooth.BluetoothGatt
import android.util.Log
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import java.util.UUID

class CharacteristicSetOperation(private val gatt: BluetoothGatt, btDevice: BluetoothDevice,
    private val service: UUID, private val characteristic: UUID, private val enable: Boolean) : Operation
    (btDevice) {

    override fun execute() {
        Log.d(TAG, "Setting to $characteristic.")
        val characteristic = gatt.getService(service).getCharacteristic(characteristic)
        gatt.setCharacteristicNotification(characteristic, enable)
    }

    override fun hasAvailableCompletionCallback(): Boolean = false

    companion object {
        private val TAG = CharacteristicSetOperation::class.java.simpleName
    }
}
