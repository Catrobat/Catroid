/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.drone.ardrone;

import android.util.Log;

import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.DroneConfigPreference;

public final class DroneConfigManager {
	private static final String TAG = DroneConfigManager.class.getSimpleName();

	private static DroneConfigManager instance;
	private static DroneControlService droneControlService;
	private final String first = "FIRST";
	private final String second = "SECOND";
	private final String third = "THIRD";
	private final String fourth = "FOURTH";

	private DroneConfigManager() {
	}

	public static DroneConfigManager getInstance() {
		if (DroneConfigManager.instance == null) {
			DroneConfigManager.instance = new DroneConfigManager();
		}
		return DroneConfigManager.instance;
	}

	public void setDroneConfig(DroneConfigPreference.Preferences[] preferences) {
		for (DroneConfigPreference.Preferences preference : preferences) {
			Log.d(TAG, "Drone = " + preference);
		}

		setBasicConfig(preferences[0].getPreferenceCode());
		setAltitude(preferences[1].getPreferenceCode());
		setVerticalSpeed(preferences[2].getPreferenceCode());
		setRotationSpeed(preferences[3].getPreferenceCode());
		setTiltAngle(preferences[4].getPreferenceCode());
	}

	private void setBasicConfig(String preference) {
		switch (preference) {
			case first:
				setIndoorConfigWithHull();
				break;
			case second:
				setIndoorConfigWithoutHull();
				break;
			case third:
				setOutdoorConfigWithHull();
				break;
			case fourth:
				setOutdoorConfigWithoutHull();
				break;
		}
	}

	public void setIndoorConfigWithHull() {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			droneControlService.getDroneConfig().setOutdoorFlight(false);
			droneControlService.getDroneConfig().setOutdoorHull(true);
			Log.d(TAG, "Set Config = indoor with hull");
		}
	}

	public void setIndoorConfigWithoutHull() {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			droneControlService.getDroneConfig().setOutdoorFlight(false);
			droneControlService.getDroneConfig().setOutdoorHull(false);
			Log.d(TAG, "Set Config = indoor without hull");
		}
	}

	public void setOutdoorConfigWithHull() {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			droneControlService.getDroneConfig().setOutdoorFlight(true);
			droneControlService.getDroneConfig().setOutdoorHull(true);
			Log.d(TAG, "Set Config = outdoor with hull");
		}
	}

	public void setOutdoorConfigWithoutHull() {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			droneControlService.getDroneConfig().setOutdoorFlight(true);
			droneControlService.getDroneConfig().setOutdoorHull(false);
			Log.d(TAG, "Set Config = outdoor without hull");
		}
	}

	private void setAltitude(String preference) {
		int altitudeValue = BrickValues.DRONE_ALTITUDE_MIN;

		switch (preference) {
			case first:
				altitudeValue = BrickValues.DRONE_ALTITUDE_MIN;
				break;
			case second:
				altitudeValue = BrickValues.DRONE_ALTITUDE_INDOOR;
				break;
			case third:
				altitudeValue = BrickValues.DRONE_ALTITUDE_OUTDOOR;
				break;
			case fourth:
				altitudeValue = BrickValues.DRONE_ALTITUDE_MAX;
				break;
		}
		setAltitude(altitudeValue);
	}

	public void setAltitude(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d(TAG, String.format("old altitude = %d", droneControlService.getDroneConfig().getAltitudeLimit()));
			if (BrickValues.DRONE_ALTITUDE_MIN <= value && value <= BrickValues.DRONE_ALTITUDE_MAX) {
				droneControlService.getDroneConfig().setAltitudeLimit(value);
			} else {
				droneControlService.getDroneConfig().setAltitudeLimit(BrickValues.DRONE_ALTITUDE_MIN);
			}
			Log.d(TAG, String.format("new altitude = %d", droneControlService.getDroneConfig().getAltitudeLimit()));
		}
	}

	private void setVerticalSpeed(String preference) {
		int verticalValue = BrickValues.DRONE_VERTICAL_INDOOR;

		switch (preference) {
			case first:
				verticalValue = BrickValues.DRONE_VERTICAL_MIN;
				break;
			case second:
				verticalValue = BrickValues.DRONE_VERTICAL_INDOOR;
				break;
			case third:
				verticalValue = BrickValues.DRONE_VERTICAL_OUTDOOR;
				break;
			case fourth:
				verticalValue = BrickValues.DRONE_VERTICAL_MAX;
				break;
		}
		setVerticalSpeed(verticalValue);
	}

	public void setVerticalSpeed(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d(TAG, String.format("old vertical = %d", droneControlService.getDroneConfig().getVertSpeedMax()));
			if (BrickValues.DRONE_VERTICAL_MIN <= value && value <= BrickValues.DRONE_VERTICAL_MAX) {
				droneControlService.getDroneConfig().setVertSpeedMax(value);
			} else {
				droneControlService.getDroneConfig().setVertSpeedMax(BrickValues.DRONE_VERTICAL_INDOOR);
			}
			Log.d(TAG, String.format("new vertical = %d", droneControlService.getDroneConfig().getVertSpeedMax()));
		}
	}

	private void setRotationSpeed(String preference) {
		int rotationValue = BrickValues.DRONE_ROTATION_INDOOR;

		switch (preference) {
			case first:
				rotationValue = BrickValues.DRONE_ROTATION_MIN;
				break;
			case second:
				rotationValue = BrickValues.DRONE_ROTATION_INDOOR;
				break;
			case third:
				rotationValue = BrickValues.DRONE_ROTATION_OUTDOOR;
				break;
			case fourth:
				rotationValue = BrickValues.DRONE_ROTATION_MAX;
				break;
		}
		setRotationSpeed(rotationValue);
	}

	public void setRotationSpeed(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d(TAG, String.format("old rotation = %d", droneControlService.getDroneConfig().getYawSpeedMax()));
			if (BrickValues.DRONE_ROTATION_MIN <= value && value <= BrickValues.DRONE_ROTATION_MAX) {
				droneControlService.getDroneConfig().setYawSpeedMax(value);
			} else {
				droneControlService.getDroneConfig().setYawSpeedMax(BrickValues.DRONE_ROTATION_INDOOR);
			}
			Log.d(TAG, String.format("new rotation = %d", droneControlService.getDroneConfig().getYawSpeedMax()));
		}
	}

	private void setTiltAngle(String preference) {
		int tiltValue = BrickValues.DRONE_TILT_INDOOR;

		switch (preference) {
			case first:
				tiltValue = BrickValues.DRONE_TILT_MIN;
				break;
			case second:
				tiltValue = BrickValues.DRONE_TILT_INDOOR;
				break;
			case third:
				tiltValue = BrickValues.DRONE_TILT_OUTDOOR;
				break;
			case fourth:
				tiltValue = BrickValues.DRONE_TILT_MAX;
				break;
		}
		setTiltAngle(tiltValue);
	}

	public void setTiltAngle(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d(TAG, String.format("old tilt = %d", droneControlService.getDroneConfig().getDeviceTiltMax()));
			if (BrickValues.DRONE_TILT_MIN <= value && value <= BrickValues.DRONE_TILT_MAX) {
				droneControlService.getDroneConfig().setDeviceTiltMax(value);
			} else {
				droneControlService.getDroneConfig().setDeviceTiltMax(BrickValues.DRONE_TILT_INDOOR);
			}
			Log.d(TAG, String.format("new tilt = %d", droneControlService.getDroneConfig().getDeviceTiltMax()));
		}
	}
}
