/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.LegoImageLookData;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.devices.mindstorms.ev3.LegoEV3;

public class LegoEv3ShowImageAction extends SetLookAction {

	private BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

	@Override
	protected void doLookUpdate() {
		LegoEV3 ev3 = btService.getDevice(BluetoothDevice.LEGO_EV3);
		if (ev3 != null && look != null && sprite != null && sprite.getLookDataList().contains(look)) {
			String fileName = look.getAbsolutePath();
			if (fileName != null) {
				String programName = ev3.downloadFileToEv3(ev3.EV3_SHOW_IMAGE_PROGRAM_NAME,
						ev3.getLegoEv3ShowImageProgram());
				ev3.downloadFileToEv3(ev3.EV3_IMAGE_NAME, ((LegoImageLookData) look).getLookRgf());
				ev3.startProgram(programName);
			}
			setLookDone = true;
		}
	}
}
