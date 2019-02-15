/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.drone.jumpingsumo;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

public final class JumpingSumoServiceWrapper {

	private static JumpingSumoServiceWrapper instance = null;
	private static JumpingSumoInitializer jumpingSumoInitializer = null;

	private JumpingSumoServiceWrapper() {
	}

	public static JumpingSumoServiceWrapper getInstance() {
		if (instance == null) {
			instance = new JumpingSumoServiceWrapper();
		}

		return instance;
	}

	public static boolean isJumpingSumoSharedPreferenceEnabled() {
		return SettingsFragment.isJSSharedPreferenceEnabled(CatroidApplication.getAppContext());
	}

	public static void initJumpingSumo(StageActivity prestageStageActivity) {
		jumpingSumoInitializer = getJumpingSumoInitialiser(prestageStageActivity);
		jumpingSumoInitializer.initialise();
		jumpingSumoInitializer.checkJumpingSumoAvailability(prestageStageActivity);
	}

	public static JumpingSumoInitializer getJumpingSumoInitialiser(StageActivity stageStageActivity) {
		if (jumpingSumoInitializer == null) {
			jumpingSumoInitializer = JumpingSumoInitializer.getInstance();
			jumpingSumoInitializer.setStageActivity(stageStageActivity);
		}
		return jumpingSumoInitializer;
	}
}
