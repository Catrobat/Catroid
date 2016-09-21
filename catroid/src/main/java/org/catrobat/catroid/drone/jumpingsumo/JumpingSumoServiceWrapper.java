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
package org.catrobat.catroid.drone.jumpingsumo;

import android.os.Bundle;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.dialogs.TermsOfUseJSDialogFragment;

public final class JumpingSumoServiceWrapper {

	private static JumpingSumoServiceWrapper instance = null;
	private static JumpingSumoInitializer jumpingSumoInitializer = null;
	private static final String TAG = JumpingSumoServiceWrapper.class.getSimpleName();

	private JumpingSumoServiceWrapper() {
	}

	public static JumpingSumoServiceWrapper getInstance() {
		if (instance == null) {
			instance = new JumpingSumoServiceWrapper();
		}

		return instance;
	}

	public static boolean checkJumpingSumoAvailability() {
		int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		boolean isJSAvailable = (((requiredResources & Brick.JUMPING_SUMO) > 0) && BuildConfig.FEATURE_PARROT_JUMPING_SUMO_ENABLED);
		return isJSAvailable;
	}

	public static boolean isJumpingSumoSharedPreferenceEnabled() {
		return SettingsActivity.isJSSharedPreferenceEnabled(CatroidApplication.getAppContext());
	}

	public static void showTermsOfUseDialog(PreStageActivity preStageActivity) {
		Bundle args = new Bundle();
		args.putBoolean(TermsOfUseJSDialogFragment.DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT, true);

		TermsOfUseJSDialogFragment termsOfUseDialog = new TermsOfUseJSDialogFragment();
		termsOfUseDialog.setPrestageStageActivity(preStageActivity);
		termsOfUseDialog.setArguments(args);

		termsOfUseDialog.show(preStageActivity.getFragmentManager(),
				TermsOfUseJSDialogFragment.DIALOG_FRAGMENT_TAG);
	}

	public static void initJumpingSumo(PreStageActivity prestageStageActivity) {
		if (SettingsActivity.areTermsOfServiceJSAgreedPermanently(prestageStageActivity.getApplicationContext())) {
			jumpingSumoInitializer = getJumpingSumoInitialiser(prestageStageActivity);
			jumpingSumoInitializer.initialise();
			jumpingSumoInitializer.checkJumpingSumoAvailability(prestageStageActivity);
		} else {
			showTermsOfUseDialog(prestageStageActivity);
		}
	}

	public static JumpingSumoInitializer getJumpingSumoInitialiser(PreStageActivity prestageStageActivity) {
		if (jumpingSumoInitializer == null) {
			jumpingSumoInitializer = JumpingSumoInitializer.getInstance();
			jumpingSumoInitializer.setPreStageActivity(prestageStageActivity);
		}
		return jumpingSumoInitializer;
	}
}
