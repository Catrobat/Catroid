/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.util.EnumSet;

public enum Sensors {
	X_ACCELERATION_("X_ACCELERATION_"), Y_ACCELERATION_("Y_ACCELERATION_"), Z_ACCELERATION_("Z_ACCELERATION_"), Z_ORIENTATION_(
			"Z_ORIENTATION_"), X_ORIENTATION_("X_ORIENTATION_"), Y_ORIENTATION_("Y_ORIENTATION_"), LOOK_X_("LOOK_X_"), LOOK_Y_(
			"LOOK_Y_"), LOOK_GHOSTEFFECT_("LOOK_GHOSTEFFECT_"), LOOK_BRIGHTNESS_("LOOK_BRIGHTNESS_"), LOOK_SIZE_(
			"LOOK_SIZE_"), LOOK_ROTATION_("LOOK_ROTATION_"), LOOK_LAYER_("LOOK_LAYER_");
	public final String sensorName;

	Sensors(String value) {
		this.sensorName = value;
	}

	public static boolean isSensor(String value) {
		for (Sensors fct : EnumSet.allOf(Sensors.class)) {
			if (value.startsWith(fct.sensorName)) {
				return true;
			}
		}
		return false;
	}

}
