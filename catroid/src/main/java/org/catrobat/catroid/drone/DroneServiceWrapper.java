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
package org.catrobat.catroid.drone;

import android.util.Log;

import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.SettingsActivity;

public final class DroneServiceWrapper {
	private static final String TAG = DroneServiceWrapper.class.getSimpleName();

	private static DroneServiceWrapper instance = null;
	private static DroneControlService droneControlService = null;

	private DroneServiceWrapper() {
	}

	public static DroneServiceWrapper getInstance() {
		if (instance == null) {
			instance = new DroneServiceWrapper();
		}

		return instance;
	}

	public void setDroneService(DroneControlService service) {
		droneControlService = service;
	}

	public DroneControlService getDroneService() {
		return droneControlService;
	}

	public static boolean checkARDroneAvailability() {
		int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		boolean isDroneAvailable = (((requiredResources & Brick.ARDRONE_SUPPORT) > 0) && BuildConfig.FEATURE_PARROT_AR_DRONE_ENABLED);
		Log.d(TAG, "drone pref enabled? " + isDroneSharedPreferenceEnabled());
		return isDroneAvailable; // isDroneSharedPreferenceEnabled()
	}

	public static boolean isDroneSharedPreferenceEnabled() {
		return SettingsActivity.isDroneSharedPreferenceEnabled(CatroidApplication.getAppContext());
	}
}
