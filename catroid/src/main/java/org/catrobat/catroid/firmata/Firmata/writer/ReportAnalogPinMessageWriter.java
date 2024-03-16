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

package org.catrobat.catroid.firmata.Firmata.writer;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.ble.BleManager;
import org.catrobat.catroid.bluetooth.ble.operations.CharacteristicWriteOperation;
import org.catrobat.catroid.devices.arduino.ArduinoImpl;
import org.catrobat.catroid.firmata.Firmata.message.ReportAnalogPinMessage;
import org.catrobat.catroid.firmata.Serial.ISerial;
import org.catrobat.catroid.firmata.Serial.SerialException;

import static org.catrobat.catroid.firmata.Firmata.BytesHelper.ENCODE_CHANNEL;

/**
 * MessageWriter for ReportAnalogPinMessage
 */
public class ReportAnalogPinMessageWriter implements IMessageWriter<ReportAnalogPinMessage> {

    public static final int COMMAND = 0xC0;

    public void write(ReportAnalogPinMessage message, ISerial serial) throws SerialException {
        serial.write(COMMAND | ENCODE_CHANNEL(message.getPin()));
        serial.write(message.isEnable() ? 1 : 0);
    }

    public void write(ReportAnalogPinMessage message, BluetoothDevice btDevice) {
        byte[] buffer = new byte[2];

        buffer[0] = (byte) (COMMAND | ENCODE_CHANNEL(message.getPin()));
        buffer[1] = (byte) (message.isEnable() ? 1 : 0);

        ((ArduinoImpl) btDevice).getBleManager().requestOperation(new CharacteristicWriteOperation(((ArduinoImpl) btDevice).getBleManager().getBluetoothGatt(),
                btDevice,  BleManager.Companion.getUUID_BLE_SHIELD_SERVICE(),
                BleManager.Companion.getUUID_BLE_SHIELD_TX(), buffer));

    }
}
