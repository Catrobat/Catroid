/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.recyclerview.dialog.AboutDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.PrivacyPolicyDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInDialog;
import org.catrobat.catroid.ui.recyclerview.fragment.MainMenuFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

public class MainMenuActivity extends BaseCastActivity implements
		ProjectLoaderTask.ProjectLoaderListener,
		SignInDialog.SignInCompleteListener {

	public static final String TAG = MainMenuActivity.class.getSimpleName();
	public static final int REQUEST_CODE_GOOGLE_PLUS_SIGNIN = 100;

	private static final int ACCESS_STORAGE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SettingsFragment.setToChosenLanguage(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		UtilUi.updateScreenWidthAndHeight(this);

		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			setContentView(R.layout.activity_main_menu_splashscreen);
		} else {
			FacebookSdk.sdkInitialize(getApplicationContext());
			setContentView(R.layout.activity_main_menu);
			setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
			getSupportActionBar().setTitle(R.string.app_name);
		}

		@PermissionChecker.PermissionResult
		int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (permissionResult == PackageManager.PERMISSION_GRANTED) {
			onPermissionsGranted();
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE);
		}
	}

	private void onPermissionsGranted() {
		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			prepareStandaloneProject();
			return;
		}

		getFragmentManager().beginTransaction()
				.replace(R.id.main_menu_buttons_container, new MainMenuFragment(), MainMenuFragment.TAG)
				.commit();

		if (SettingsFragment.isCastSharedPreferenceEnabled(this)) {
			CastManager.getInstance().initializeCast(this);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case ACCESS_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					onPermissionsGranted();
				}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject != null) {
			ProjectManager.getInstance().saveProject(getApplicationContext());
			Utils.saveToPreferences(this, Constants.PREF_PROJECTNAME_KEY, currentProject.getName());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_login).setVisible(!Utils.isUserLoggedIn(this));
		menu.findItem(R.id.menu_logout).setVisible(Utils.isUserLoggedIn(this));
		if (!BuildConfig.FEATURE_SCRATCH_CONVERTER_ENABLED) {
			menu.removeItem(R.id.menu_scratch_converter);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_rate_app:
				launchMarket();
				break;
			case R.id.menu_terms_of_use:
				new TermsOfUseDialogFragment().show(getFragmentManager(), TermsOfUseDialogFragment.TAG);
				break;
			case R.id.menu_privacy_policy:
				new PrivacyPolicyDialogFragment().show(getFragmentManager(), PrivacyPolicyDialogFragment.TAG);
				break;
			case R.id.menu_about:
				new AboutDialogFragment().show(getFragmentManager(), AboutDialogFragment.TAG);
				break;
			case R.id.menu_scratch_converter:
				if (Utils.isNetworkAvailable(this)) {
					startActivity(new Intent(this, ScratchConverterActivity.class));
				} else {
					ToastUtil.showError(this, R.string.error_internet_connection);
				}
				break;
			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.menu_login:
				SignInDialog dialog = new SignInDialog();
				dialog.setSignInCompleteListener(this);
				dialog.show(getFragmentManager(), SignInDialog.TAG);
				break;
			case R.id.menu_logout:
				Utils.logoutUser(this);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void launchMarket() {
		if (Utils.isNetworkAvailable(this)) {
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
			} catch (ActivityNotFoundException e) {
				ToastUtil.showError(this, R.string.main_menu_play_store_not_installed);
			}
		} else {
			ToastUtil.showError(this, R.string.error_internet_connection);
		}
	}

	private void prepareStandaloneProject() {
		try {
			InputStream inputStream = getAssets().open(BuildConfig.START_PROJECT + ".zip");
			StorageHandler.copyAndUnzip(inputStream, Utils.buildProjectPath(BuildConfig.PROJECT_NAME));
			new ProjectLoaderTask(this, this).execute(BuildConfig.PROJECT_NAME);
		} catch (IOException e) {
			Log.e("STANDALONE", "Could not unpack Standalone Program: ", e);
		}
	}

	@Override
	public void onLoadFinished(boolean success, String message) {
		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			startActivityForResult(new Intent(this, PreStageActivity.class), PreStageActivity.REQUEST_RESOURCES_INIT);
		}
	}

	@Override
	public void onLoginSuccessful(Bundle bundle) {
		//maybe do something? or nah?
	}

	@Override
	public void onLoginCancel() {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
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
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
