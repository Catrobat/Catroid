/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import android.util.Log;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.devices.arduino.Arduino;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;


public class ArduinoIrCommandAction extends TemporalAction {

	private String commandToSend;
	private Sprite sprite;


	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	protected void update(float percent) {
		Log.d("Arduino IR Command", "BT command" + commandToSend);
		// here the magic happens :-)

		Arduino arduino = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).getDevice(BluetoothDevice.ARDUINO);
		if(arduino != null)
			arduino.sendArduinoMessage(commandToSend);
	}

	public void setCommand(String command) {
		commandToSend = command;
	}
}
