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
package org.catrobat.catroid.bluetooth.ble

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import org.catrobat.catroid.R
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.bluetooth.ble.operations.CharacteristicReadOperation
import org.catrobat.catroid.bluetooth.ble.operations.CharacteristicSetOperation
import org.catrobat.catroid.bluetooth.ble.operations.DescriptorReadOperation
import org.catrobat.catroid.bluetooth.ble.operations.DescriptorWriteOperation
import org.catrobat.catroid.bluetooth.ble.operations.Operation
import org.catrobat.catroid.bluetooth.ble.operations.ServicesDiscoverOperation
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.devices.arduino.ArduinoImpl
import org.catrobat.catroid.devices.mindstorms.MindstormsException
import org.catrobat.catroid.utils.ToastUtil
import java.util.UUID

class BleManager(private val context: Context, val btDevice: BluetoothDevice) {
    var bluetoothGatt: BluetoothGatt? = null
    val bleQueueManager: BleQueueManager = BleQueueManager()

    fun connect(address: String) {
        bluetoothGatt = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address).connectGatt(context, false, bluetoothGattCallback)
        (btDevice as ArduinoImpl).bleManager = this
    }

    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            var result = Activity.RESULT_CANCELED

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e(TAG, "GATT connected.")

                requestOperation(ServicesDiscoverOperation(gatt, btDevice))

                val btDeviceService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
                if (btDeviceService != null) {
                    try {
                        btDeviceService.deviceConnected(btDevice)
                    } catch (e: MindstormsException) {
                        ToastUtil.showError(context, R.string.bt_connection_failed)
                    }
                }
                result = Activity.RESULT_OK
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "GATT disconnected.")
                bluetoothGatt = null
                bleQueueManager.clear()
                gatt.close()
                ToastUtil.showError(context, R.string.bt_connection_failed)
            }
            (context as Activity).setResult(result)
            context.finish()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "Services discovered successfully.")

                bleQueueManager.operationCompleted()

                gatt.printGattTable()

                for (service in gatt.services) {
                    if (service.uuid == UUID_BLE_SHIELD_SERVICE) {
                        requestOperation(CharacteristicSetOperation(gatt, btDevice, UUID_BLE_SHIELD_SERVICE, UUID_BLE_SHIELD_RX, true))
                        val characteristic = service.getCharacteristic(UUID_BLE_SHIELD_RX)
                        for (descriptor in characteristic.descriptors) {
                            requestOperation(DescriptorWriteOperation(gatt, btDevice, UUID_BLE_SHIELD_SERVICE, UUID_BLE_SHIELD_RX,
                                                                      UUID_BLE_SHIELD_DESCRIPTOR, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE))
                        }
                        //requestOperation(CharacteristicSetOperation(gatt, btDevice, UUID_BLE_SHIELD_SERVICE, UUID_BLE_SHIELD_RX, true))
                    }
                }
            } else {
                Log.e(TAG, "Services discovery failed with status: $status.")
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            Log.e(TAG, "Characteristic ${characteristic.uuid} was read.")
            (bleQueueManager.currentOperation as? CharacteristicReadOperation)?.onRead(characteristic.value)
            bleQueueManager.operationCompleted()
            //(btDevice as ArduinoImpl).firmata.onDataReceived(characteristic.value)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.e(TAG, "Characteristic ${characteristic.uuid} was written.")
            bleQueueManager.operationCompleted()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            Log.e(TAG, "Characteristic ${characteristic.uuid} was changed.")
            if (characteristic.uuid == UUID_BLE_SHIELD_RX) {
                (btDevice as ArduinoImpl).firmata.onDataReceived(characteristic.value)
            }
            bleQueueManager.operationCompleted()
        }

        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
            Log.e(TAG, "Descriptor ${descriptor.uuid} was read.")
            (bleQueueManager.currentOperation as? DescriptorReadOperation)?.onRead(descriptor.value)
            bleQueueManager.operationCompleted()
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            Log.e(TAG, "Descriptor ${descriptor.uuid} was written.")
            bleQueueManager.operationCompleted()
        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            Log.e(TAG, "Reliable Write.")
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Log.e(TAG, "Read Remote Rssi.")
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.e(TAG, "On Mtu Changed.")
        }
    }

    fun requestOperation(operation: Operation) {
        bleQueueManager.request(operation)
    }

    companion object {
        val TAG: String = BleManager::class.java.simpleName
        val UUID_BLE_SHIELD_SERVICE: UUID = UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e")
        val UUID_BLE_SHIELD_TX: UUID = UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e")
        val UUID_BLE_SHIELD_RX: UUID = UUID.fromString("713d0002-503e-4c75-ba94-3148f18d941e")
        val UUID_BLE_SHIELD_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
