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

package org.catrobat.catroid.formulaeditor.function;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.devices.arduino.Arduino;
import org.catrobat.catroid.formulaeditor.Functions;

import java.util.Map;

public class ArduinoFunctionProvider implements FunctionProvider {
	@Override
	public void addFunctionsToMap(Map<Functions, FormulaFunction> formulaFunctions) {
		formulaFunctions.put(Functions.ARDUINODIGITAL, new UnaryFunction(this::interpretFunctionArduinoDigital));
		formulaFunctions.put(Functions.ARDUINOANALOG, new UnaryFunction(this::interpretFunctionArduinoAnalog));
	}

	private double interpretFunctionArduinoAnalog(double argument) {
		Arduino arduino = getArduino();
		if (arduino == null || argument < 0 || argument > 5) {
			return 0;
		}
		return arduino.getAnalogArduinoPin((int) argument);
	}

	private double interpretFunctionArduinoDigital(double argument) {
		Arduino arduino = getArduino();
		if (arduino == null || argument < 0 || argument > 13) {
			return 0;
		}
		return arduino.getDigitalArduinoPin((int) argument);
	}

	private Arduino getArduino() {
		return ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).getDevice(BluetoothDevice.ARDUINO);
	}
}
