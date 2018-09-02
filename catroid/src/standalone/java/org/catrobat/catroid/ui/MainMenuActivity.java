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

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.ScreenValueHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY;

public class MainMenuActivity extends BaseCastActivity implements ProjectLoaderTask.ProjectLoaderListener {

	public static final String TAG = MainMenuActivity.class.getSimpleName();

	private static final int ACCESS_STORAGE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SettingsFragment.setToChosenLanguage(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		ScreenValueHandler.updateScreenWidthAndHeight(this);

		boolean hasUserAgreedToPrivacyPolicy = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, false);

		if (hasUserAgreedToPrivacyPolicy) {
			askForPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE);
		} else {
			setContentView(R.layout.privacy_policy_view);
		}
	}

	public void handleAgreedToPrivacyPolicyButton(View view) {
		PreferenceManager.getDefaultSharedPreferences(this)
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, true)
				.commit();

		askForPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE);
	}

	public void handleDeclinedPrivacyPolicyButton(View view) {
		View dialogView = View.inflate(this, R.layout.declined_privacy_agreement_alert_view, null);

		String linkString = getString(
				R.string.about_link_template, Constants.CATROBAT_ABOUT_URL, getString(R.string.share_website_text));

		((TextView) dialogView.findViewById(R.id.share_website_view)).setText(Html.fromHtml(linkString));

		new AlertDialog.Builder(this)
				.setView(dialogView)
				.setNeutralButton(R.string.ok, null)
				.create()
				.show();
	}

	@Override
	public void onAskForPermissionsResult(int requestCode, @NonNull String[] permissions, boolean permissionsGranted) {
		super.onAskForPermissionsResult(requestCode, permissions, permissionsGranted);
		if (requestCode == ACCESS_STORAGE) {
			if (permissionsGranted) {
				setContentView(R.layout.activity_main_menu_splashscreen);
				prepareStandaloneProject();
			} else {
				showPermissionDeniedDialog(requestCode, permissions, R.string.error_no_write_access);
			}
		}
	}

	private void prepareStandaloneProject() {
		try {
			InputStream inputStream = getAssets().open(BuildConfig.START_PROJECT + ".zip");
			new ZipArchiver().unzip(inputStream, new File(PathBuilder.buildProjectPath(BuildConfig.PROJECT_NAME)));
			new ProjectLoaderTask(this, this).execute(BuildConfig.PROJECT_NAME);
		} catch (IOException e) {
			Log.e("STANDALONE", "Could not unpack Standalone Program: ", e);
		}
	}

	@Override
	public void onLoadFinished(boolean success, String message) {
		startActivityForResult(new Intent(this, PreStageActivity.class), PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			SensorHandler.startSensorListener(this);
			Intent intent = new Intent(this, StageActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(intent, StageActivity.STAGE_ACTIVITY_FINISH);
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			finish();
		}
	}
}
