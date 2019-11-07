/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.common;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.Sensors;

import java.util.Set;

public final class FormulaElementResources {
	private FormulaElementResources() {
	}

	public static void addSensorsResources(Set<Integer> resources, Sensors sensor) {
		switch (sensor) {
			case X_ACCELERATION:
			case Y_ACCELERATION:
			case Z_ACCELERATION:
				resources.add(Brick.SENSOR_ACCELERATION);
				break;
			case X_INCLINATION:
			case Y_INCLINATION:
				resources.add(Brick.SENSOR_INCLINATION);
				break;
			case COMPASS_DIRECTION:
				resources.add(Brick.SENSOR_COMPASS);
				break;
			case LATITUDE:
			case LONGITUDE:
			case LOCATION_ACCURACY:
			case ALTITUDE:
				resources.add(Brick.SENSOR_GPS);
				break;
			case FACE_DETECTED:
			case FACE_SIZE:
			case FACE_X_POSITION:
			case FACE_Y_POSITION:
				resources.add(Brick.FACE_DETECTION);
				break;
			case NXT_SENSOR_1:
			case NXT_SENSOR_2:
			case NXT_SENSOR_3:
			case NXT_SENSOR_4:
				resources.add(Brick.BLUETOOTH_LEGO_NXT);
				break;
			case EV3_SENSOR_1:
			case EV3_SENSOR_2:
			case EV3_SENSOR_3:
			case EV3_SENSOR_4:
				resources.add(Brick.BLUETOOTH_LEGO_EV3);
				break;
			case PHIRO_FRONT_LEFT:
			case PHIRO_FRONT_RIGHT:
			case PHIRO_SIDE_LEFT:
			case PHIRO_SIDE_RIGHT:
			case PHIRO_BOTTOM_LEFT:
			case PHIRO_BOTTOM_RIGHT:
				resources.add(Brick.BLUETOOTH_PHIRO);
				break;
			case DRONE_BATTERY_STATUS:
			case DRONE_CAMERA_READY:
			case DRONE_EMERGENCY_STATE:
			case DRONE_FLYING:
			case DRONE_INITIALIZED:
			case DRONE_NUM_FRAMES:
			case DRONE_RECORD_READY:
			case DRONE_RECORDING:
			case DRONE_USB_ACTIVE:
			case DRONE_USB_REMAINING_TIME:
				resources.add(Brick.ARDRONE_SUPPORT);
				break;
			case NFC_TAG_MESSAGE:
			case NFC_TAG_ID:
				resources.add(Brick.NFC_ADAPTER);
				break;
			case COLLIDES_WITH_EDGE:
			case COLLIDES_WITH_FINGER:
				resources.add(Brick.COLLISION);
				break;
			case GAMEPAD_A_PRESSED:
			case GAMEPAD_B_PRESSED:
			case GAMEPAD_DOWN_PRESSED:
			case GAMEPAD_UP_PRESSED:
			case GAMEPAD_LEFT_PRESSED:
			case GAMEPAD_RIGHT_PRESSED:
				resources.add(Brick.CAST_REQUIRED);
				break;
			case LOUDNESS:
				resources.add(Brick.MICROPHONE);
				break;
			default:
		}
	}

	public static void addFunctionResources(Set<Integer> resources, Functions functions) {
		switch (functions) {
			case ARDUINOANALOG:
			case ARDUINODIGITAL:
				resources.add(Brick.BLUETOOTH_SENSORS_ARDUINO);
				break;
			case RASPIDIGITAL:
				resources.add(Brick.SOCKET_RASPI);
				break;
			default:
		}
	}
}
