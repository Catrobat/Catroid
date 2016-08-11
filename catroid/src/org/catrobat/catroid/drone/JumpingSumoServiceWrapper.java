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

public final class JumpingSumoServiceWrapper {
	private static final String TAG = JumpingSumoServiceWrapper.class.getSimpleName();

	private static JumpingSumoServiceWrapper instance = null;
	private static DroneControlService droneControlService = null;

	private JumpingSumoServiceWrapper() {
	}

	public static JumpingSumoServiceWrapper getInstance() {
		if (instance == null) {
			instance = new JumpingSumoServiceWrapper();
		}

		return instance;
	}

	//TODO: whatever TGr
	public void setDroneService(DroneControlService service) {
		droneControlService = service;
	}

	//TODO: whatever TGr
	public DroneControlService getDroneService() {
		return droneControlService;
	}

	public static boolean checkJumpingSumoAvailability() {
		int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		boolean isJSAvailable = (((requiredResources & Brick.JUMPING_SUMO) > 0) && BuildConfig.FEATURE_PARROT_JS_DRONE_ENABLED);
		//TODO: TGr: Brick.JUMPING_SUMO in JumpingSumoBasicBrick.java aktivieren und Vebindung (vermutlich) nur einmal aufbauen!!!
		Log.d(TAG, "Jumping Sumo checked");
		return true;
		//return isJSAvailable; // isDroneSharedPreferenceEnabled()

	}

	public static boolean isJSSharedPreferenceEnabled() {
		return SettingsActivity.isJSSharedPreferenceEnabled(CatroidApplication.getAppContext());
	}
}
