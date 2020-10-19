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
package org.catrobat.catroid.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.ui.runtimepermissions.PermissionHandlingActivity;
import org.catrobat.catroid.ui.runtimepermissions.PermissionRequestActivityExtension;
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask;
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements PermissionHandlingActivity {
	private static boolean savedInstanceStateExpected;

	public static final String RECOVERED_FROM_CRASH = "RECOVERED_FROM_CRASH";
	private PermissionRequestActivityExtension permissionRequestActivityExtension = new PermissionRequestActivityExtension();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SettingsFragment.setToChosenLanguage(this);
		applyAccessibilityStyles();

		Thread.setDefaultUncaughtExceptionHandler(new BaseExceptionHandler(this));
		checkIfCrashRecoveryAndFinishActivity(this);
		checkIfProcessRecreatedAndFinishActivity(savedInstanceState);

		if (SettingsFragment.isCastSharedPreferenceEnabled(this)) {
			CastManager.getInstance().initializeCast(this);
		}
	}

	private void checkIfProcessRecreatedAndFinishActivity(Bundle savedInstanceState) {
		if (savedInstanceStateExpected || savedInstanceState == null || this instanceof MainMenuActivity) {
			savedInstanceStateExpected = true;
		} else {
			String activityName = getClass().getSimpleName();
			Log.e(activityName, activityName + " does not support recovery from process "
					+ "recreation, finishing activity.");
			finish();
		}
	}

	private void applyAccessibilityStyles() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		AccessibilityProfile profile = AccessibilityProfile.fromCurrentPreferences(sharedPreferences);
		profile.applyAccessibilityStyles(getTheme());
	}

	@Override
	protected void onResume() {
		super.onResume();
		SettingsFragment.setToChosenLanguage(this);

		if (SettingsFragment.isCastSharedPreferenceEnabled(this)) {
			CastManager.getInstance().initializeCast(this);
		}

		invalidateOptionsMenu();
		googleAnalyticsTrackScreenResume();
	}

	protected void googleAnalyticsTrackScreenResume() {
		Tracker googleTracker = ((CatroidApplication) getApplication()).getDefaultTracker();
		googleTracker.setScreenName(this.getClass().getName());
		googleTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void checkIfCrashRecoveryAndFinishActivity(final Activity activity) {
		if (isRecoveringFromCrash()) {
			if (activity instanceof MainMenuActivity) {
				PreferenceManager.getDefaultSharedPreferences(this).edit()
						.putBoolean(RECOVERED_FROM_CRASH, false)
						.apply();
			} else {
				activity.finish();
			}
		}
	}

	private boolean isRecoveringFromCrash() {
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(RECOVERED_FROM_CRASH, false);
	}

	@Override
	public void addToRequiresPermissionTaskList(RequiresPermissionTask task) {
		permissionRequestActivityExtension.addToRequiresPermissionTaskList(task);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		permissionRequestActivityExtension.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}
}
