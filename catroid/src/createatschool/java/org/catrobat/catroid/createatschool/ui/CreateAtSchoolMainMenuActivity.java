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

package org.catrobat.catroid.createatschool.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zed.bdsclient.controller.BDSClientController;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.json.JSONObject;
import org.json.JSONException;

public class CreateAtSchoolMainMenuActivity extends MainMenuActivity {

	private static final String TAG = CreateAtSchoolMainMenuActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.edit().putBoolean(Constants.FORCE_SIGNIN, true).commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean forceSignIn = sharedPreferences.getBoolean(Constants.FORCE_SIGNIN, false);

		if (!Utils.isUserLoggedIn(this) && forceSignIn) {
			ProjectManager.getInstance().showSignInDialog(this, false);
			sharedPreferences.edit().putBoolean(Constants.FORCE_SIGNIN, false).commit();
		}
	}
/*
	@Override
	public void handleContinueButton() {
		BDSClientController.getInstance().generateCustomEvent("ContinueButton", ProjectManager.getInstance().getUserID(),
				System.currentTimeMillis(), null);
		BDSClientController.getInstance().setDebugMode(true);
		ToastUtil.showSuccess(this, "generateCustomEvent!");
		Log.e(TAG, "generateCustomEvent!");
		super.handleContinueButton();
	}*/
}
