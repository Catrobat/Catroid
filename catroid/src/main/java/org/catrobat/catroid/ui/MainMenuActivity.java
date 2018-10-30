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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.recyclerview.dialog.AboutDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.PrivacyPolicyDialogFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.MainMenuFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.ImportProjectsFromExternalStorage;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.catrobat.catroid.common.Constants.PREF_PROJECTNAME_KEY;
import static org.catrobat.catroid.common.FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY;

public class MainMenuActivity extends BaseCastActivity implements ProjectLoaderTask.ProjectLoaderListener {

	public static final String TAG = MainMenuActivity.class.getSimpleName();

	private static final int ACCESS_STORAGE = 0;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({PROGRESS_BAR, FRAGMENT, ERROR})
	@interface Content {}
	protected static final int PROGRESS_BAR = 0;
	protected static final int FRAGMENT = 1;
	protected static final int ERROR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SettingsFragment.setToChosenLanguage(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		ScreenValueHandler.updateScreenWidthAndHeight(this);

		boolean hasUserAgreedToPrivacyPolicy = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, false);

		if (hasUserAgreedToPrivacyPolicy) {
			loadContent();
		} else {
			setContentView(R.layout.privacy_policy_view);
		}
	}

	public void handleAgreedToPrivacyPolicyButton(View view) {
		PreferenceManager.getDefaultSharedPreferences(this)
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, true)
				.commit();
		loadContent();
	}

	public void handleDeclinedPrivacyPolicyButton(View view) {
		View dialogView = View.inflate(this, R.layout.declined_privacy_agreement_alert_view, null);

		String linkString = getString(R.string.about_link_template,
				Constants.CATROBAT_ABOUT_URL,
				getString(R.string.share_website_text));

		((TextView) dialogView.findViewById(R.id.share_website_view)).setText(Html.fromHtml(linkString));

		new AlertDialog.Builder(this)
				.setView(dialogView)
				.setNeutralButton(R.string.ok, null)
				.create()
				.show();
	}

	private void loadContent() {
		setContentView(R.layout.activity_main_menu);
		showContentView(PROGRESS_BAR);

		if (!BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			FacebookSdk.sdkInitialize(getApplicationContext());
			setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
			getSupportActionBar().setIcon(R.drawable.pc_toolbar_icon);
			getSupportActionBar().setTitle(R.string.app_name);
		}

		@PermissionChecker.PermissionResult
		int permissionResult = ContextCompat
				.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (permissionResult == PackageManager.PERMISSION_GRANTED) {
			onPermissionsGranted();
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE);
		}
	}

	private void showContentView(@Content int content) {
		View progressBar = findViewById(R.id.progress_bar);
		View fragment = findViewById(R.id.fragment_container);
		View errorView = findViewById(R.id.runtime_permission_error_view);

		switch (content) {
			case PROGRESS_BAR:
				fragment.setVisibility(View.GONE);
				errorView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				break;
			case FRAGMENT:
				fragment.setVisibility(View.VISIBLE);
				errorView.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				break;
			case ERROR:
				fragment.setVisibility(View.GONE);
				errorView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				break;
		}
	}

	private void onPermissionsGranted() {
		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			setContentView(R.layout.activity_main_menu_splashscreen);
			prepareStandaloneProject();
			return;
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, new MainMenuFragment(), MainMenuFragment.TAG)
				.commit();
		showContentView(FRAGMENT);

		if (SettingsFragment.isCastSharedPreferenceEnabled(this)) {
			CastManager.getInstance().initializeCast(this);
		}

		if (EXTERNAL_STORAGE_ROOT_DIRECTORY.exists()) {
			new ImportProjectsFromExternalStorage(this).showImportProjectsDialog();
		}
	}

	private void onPermissionDenied(int requestCode) {
		switch (requestCode) {
			case ACCESS_STORAGE:
				((TextView) findViewById(R.id.runtime_permission_error_view)).setText(R.string.error_no_write_access);
				showContentView(ERROR);
				break;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case ACCESS_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					onPermissionsGranted();
				} else {
					onPermissionDenied(requestCode);
				}
				break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		if (currentProject != null) {
			ProjectManager.getInstance().saveProject(getApplicationContext());
			PreferenceManager.getDefaultSharedPreferences(this)
					.edit()
					.putString(PREF_PROJECTNAME_KEY, currentProject.getName())
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main_menu, menu);

		String scratchConverter = getString(R.string.main_menu_scratch_converter);
		SpannableString scratchConverterBeta = new SpannableString(scratchConverter
				+ " "
				+ getString(R.string.beta));
		scratchConverterBeta.setSpan(
				new ForegroundColorSpan(getResources().getColor(R.color.beta_label_color)),
				scratchConverter.length(), scratchConverterBeta.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		menu.findItem(R.id.menu_scratch_converter).setTitle(scratchConverterBeta);
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
				if (Utils.isNetworkAvailable(this)) {
					try {
						startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse("market://details?id=" + getPackageName())));
					} catch (ActivityNotFoundException e) {
						ToastUtil.showError(this, R.string.main_menu_play_store_not_installed);
					}
				} else {
					ToastUtil.showError(this, R.string.error_internet_connection);
				}
				break;
			case R.id.menu_terms_of_use:
				new TermsOfUseDialogFragment().show(getSupportFragmentManager(), TermsOfUseDialogFragment.TAG);
				break;
			case R.id.menu_privacy_policy:
				new PrivacyPolicyDialogFragment().show(getSupportFragmentManager(), PrivacyPolicyDialogFragment.TAG);
				break;
			case R.id.menu_about:
				new AboutDialogFragment().show(getSupportFragmentManager(), AboutDialogFragment.TAG);
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
				startActivity(new Intent(this, SignInActivity.class));
				break;
			case R.id.menu_logout:
				Utils.logoutUser(this);
				ToastUtil.showSuccess(this, R.string.logout_successful);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
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
		if (BuildConfig.FEATURE_APK_GENERATOR_ENABLED) {
			startActivityForResult(
					new Intent(this, PreStageActivity.class), PreStageActivity.REQUEST_RESOURCES_INIT);
		}
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
