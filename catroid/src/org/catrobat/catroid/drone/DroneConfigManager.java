/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.drone;

import android.util.Log;

import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.DroneConfigPreference;

public final class DroneConfigManager {

	private static DroneConfigManager instance;
	private static DroneControlService droneControlService;
	private final String first = "first";
	private final String second = "second";
	private final String third = "third";
	private final String fourth = "fourth";
	private final String fifth = "fifth";

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
			Log.d("onDroneReady config", "Drone = " + preference);
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
				setDefaultConfig();
				break;
			case second:
				setOutdoorConfig();
				break;
			case third:
				setIndoorConfig();
				break;
		}
	}

	public void setDefaultConfig() {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			droneControlService.resetConfigToDefaults();
		}
	}

	//TODO: find the perfect settings for each profile
	public void setOutdoorConfig() {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			droneControlService.getDroneConfig().setOutdoorFlight(true);
			setVerticalSpeed(BrickValues.DRONE_VERTICAL_OUTDOOR);
			setRotationSpeed(BrickValues.DRONE_ROTATION_OUTDOOR);
			setTiltAngle(BrickValues.DRONE_TILT_OUTDOOR);
			Log.d("DroneConfigManager", "Set Config = outdoor");
		}

		//TODO: set other config params for outdoor flight
	}

	public void setIndoorConfig() {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			setDefaultConfig();
			droneControlService.getDroneConfig().setOutdoorFlight(false);
			Log.d("DroneConfigManager", "Set Config = indoor");
		}

		//TODO: set other config params for indoor flight
	}

	private void setAltitude(String preference) {
		int altitudeValue = BrickValues.DRONE_ALTITUDE_DEFAULT;

		switch (preference) {
			case first:
				altitudeValue = BrickValues.DRONE_ALTITUDE_MIN;
				break;
			case second:
				altitudeValue = 5;
				break;
			case third:
				altitudeValue = 10;
				break;
			case fourth:
				altitudeValue = 50;
				break;
			case fifth:
				altitudeValue = BrickValues.DRONE_ALTITUDE_MAX;
				break;
		}
		setAltitude(altitudeValue);
	}

	public void setAltitude(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d("DroneConfigManager", String.format("old altitude = %d", droneControlService.getDroneConfig().getAltitudeLimit()));
			if (BrickValues.DRONE_ALTITUDE_MIN <= value && value <= BrickValues.DRONE_ALTITUDE_MAX) {
				droneControlService.getDroneConfig().setAltitudeLimit(value);
			} else {
				droneControlService.getDroneConfig().setAltitudeLimit(BrickValues.DRONE_ALTITUDE_DEFAULT);
			}
			Log.d("DroneConfigManager", String.format("new altitude = %d", droneControlService.getDroneConfig().getAltitudeLimit()));
		}
	}

	private void setVerticalSpeed(String preference) {
		int verticalValue = BrickValues.DRONE_VERTICAL_DEFAULT;

		switch (preference) {
			case first:
				verticalValue = BrickValues.DRONE_VERTICAL_MIN;
				break;
			case second:
				verticalValue = 660;
				break;
			case third:
				verticalValue = 1100;
				break;
			case fourth:
				verticalValue = 1540;
				break;
			case fifth:
				verticalValue = BrickValues.DRONE_VERTICAL_MAX;
				break;
		}
		setVerticalSpeed(verticalValue);
	}

	public void setVerticalSpeed(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d("DroneConfigManager", String.format("old vertical = %d", droneControlService.getDroneConfig().getVertSpeedMax()));
			if (BrickValues.DRONE_VERTICAL_MIN <= value && value <= BrickValues.DRONE_VERTICAL_MAX) {
				droneControlService.getDroneConfig().setVertSpeedMax(value);
			} else {
				droneControlService.getDroneConfig().setVertSpeedMax(BrickValues.DRONE_VERTICAL_DEFAULT);
			}
			Log.d("DroneConfigManager", String.format("new vertical = %d", droneControlService.getDroneConfig().getVertSpeedMax()));
		}
	}

	private void setRotationSpeed(String preference) {
		int rotationValue = BrickValues.DRONE_ROTATION_DEFAULT;

		switch (preference) {
			case first:
				rotationValue = BrickValues.DRONE_ROTATION_MIN;
				break;
			case second:
				rotationValue = 117;
				break;
			case third:
				rotationValue = 195;
				break;
			case fourth:
				rotationValue = 273;
				break;
			case fifth:
				rotationValue = BrickValues.DRONE_ROTATION_MAX;
				break;
		}
		setRotationSpeed(rotationValue);
	}

	public void setRotationSpeed(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d("DroneConfigManager", String.format("old rotation = %d", droneControlService.getDroneConfig().getYawSpeedMax()));
			if (BrickValues.DRONE_ROTATION_MIN <= value && value <= BrickValues.DRONE_ROTATION_MAX) {
				droneControlService.getDroneConfig().setYawSpeedMax(value);
			} else {
				droneControlService.getDroneConfig().setYawSpeedMax(BrickValues.DRONE_ALTITUDE_DEFAULT);
			}
			Log.d("DroneConfigManager", String.format("new rotation = %d", droneControlService.getDroneConfig().getYawSpeedMax()));
		}
	}

	private void setTiltAngle(String preference) {
		int tiltValue = BrickValues.DRONE_TILT_DEFAULT;

		switch (preference) {
			case first:
				tiltValue = BrickValues.DRONE_TILT_MIN;
				break;
			case second:
				tiltValue = 10;
				break;
			case third:
				tiltValue = 17;
				break;
			case fourth:
				tiltValue = 24;
				break;
			case fifth:
				tiltValue = BrickValues.DRONE_TILT_MAX;
				break;
		}
		setTiltAngle(tiltValue);
	}

	public void setTiltAngle(int value) {
		droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			Log.d("DroneConfigManager", String.format("old tilt = %d", droneControlService.getDroneConfig().getDeviceTiltMax()));
			if (BrickValues.DRONE_TILT_MIN <= value && value <= BrickValues.DRONE_TILT_MAX) {
				droneControlService.getDroneConfig().setDeviceTiltMax(value);
			} else {
				droneControlService.getDroneConfig().setDeviceTiltMax(BrickValues.DRONE_TILT_DEFAULT);
			}
			Log.d("DroneConfigManager", String.format("new tilt = %d", droneControlService.getDroneConfig().getDeviceTiltMax()));
		}
	}
}
