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

import android.util.Log;

import org.catrobat.catroid.devices.raspberrypi.RPiSocketConnection;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.formulaeditor.Functions;

import java.util.Map;

public class RaspiFunctionProvider implements FunctionProvider {
	@Override
	public void addFunctionsToMap(Map<Functions, FormulaFunction> formulaFunctions) {
		formulaFunctions.put(Functions.RASPIDIGITAL, new UnaryFunction(this::interpretFunctionRaspiDigital));
	}

	private double interpretFunctionRaspiDigital(Double argument) {
		RPiSocketConnection connection = RaspberryPiService.getInstance().connection;
		if (argument == null) {
			return 0d;
		}
		try {
			int pin = argument.intValue();
			return booleanToDouble(connection.getPin(pin));
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "RPi: exception during getPin: " + e);
		}
		return 0d;
	}

	private double booleanToDouble(boolean value) {
		return value ? 1 : 0;
	}
}
