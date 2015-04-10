/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

public class DroneConfigManager {

	private static DroneConfigManager instance;
	private static DroneControlService droneControlService = null;

	private DroneConfigManager () {}

	public static DroneConfigManager getInstance () {
		if (DroneConfigManager.instance == null) {
			DroneConfigManager.instance = new DroneConfigManager ();
			instance.droneControlService = DroneInitializer.droneControlService;
		}
		return DroneConfigManager.instance;
	}

	public void setDefaultConfig(){
		droneControlService.resetConfigToDefaults();
		Log.d("DroneConfigManager", "Set Config = default" );
	}

	//TODO: find the perfect settings for each profile
	public void setOutdoorConfig(){
		droneControlService.getDroneConfig().setOutdoorFlight(true);
		Log.d("DroneConfigManager", "Set Config = outdoor" );

		//TODO: set other config params for outdoor flight
	}

	public void setIndoorConfig(){
		droneControlService.getDroneConfig().setOutdoorFlight(false);
		Log.d("DroneConfigManager", "Set Config = indoor" );

		//TODO: set other config params for indoor flight
	}

}
