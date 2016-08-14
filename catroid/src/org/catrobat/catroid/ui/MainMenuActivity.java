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
package org.catrobat.catroid.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.io.LoadProjectTask.OnLoadProjectCompleteListener;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.transfers.GetFacebookUserInfoTask;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import java.util.concurrent.locks.Lock;

public class MainMenuActivity extends BaseActivity implements OnLoadProjectCompleteListener {

	private static final String TAG = ProjectActivity.class.getSimpleName();

	public static final String SHARED_PREFERENCES_SHOW_BROWSER_WARNING = "shared_preferences_browser_warning";
	public static final int REQUEST_CODE_GOOGLE_PLUS_SIGNIN = 100;

	private static final String TYPE_FILE = "file";
	private static final String TYPE_HTTP = "http";

	private Lock viewSwitchLock = new ViewSwitchLock();
	private CallbackManager callbackManager;
	private SignInDialog signInDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}
		initializeFacebookSdk();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		Utils.updateScreenWidthAndHeight(this);

		setContentView(R.layout.activity_main_menu);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setTitle(R.string.app_name);

		findViewById(R.id.main_menu_button_continue).setEnabled(false);

		// Load external project from URL or local file system.
		Uri loadExternalProjectUri = getIntent().getData();
		getIntent().setData(null);

		if (loadExternalProjectUri != null) {
			loadProgramFromExternalSource(loadExternalProjectUri);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}

		AppEventsLogger.activateApp(this);

		SettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(this, false);

		SettingsActivity.setDroneChooserEnabled(this, false);

		findViewById(R.id.progress_circle).setVisibility(View.GONE);

		UtilFile.createStandardProjectIfRootDirectoryIsEmpty(this);

		PreStageActivity.shutdownPersistentResources();
		setMainMenuButtonContinueText();
		findViewById(R.id.main_menu_button_continue).setEnabled(true);
		String projectName = getIntent().getStringExtra(StatusBarNotificationManager.EXTRA_PROJECT_NAME);
		if (projectName != null) {
			loadProjectInBackground(projectName);
		}
		getIntent().removeExtra(StatusBarNotificationManager.EXTRA_PROJECT_NAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!Utils.externalStorageAvailable()) {
			return;
		}

		AppEventsLogger.deactivateApp(this);

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject != null) {
			ProjectManager.getInstance().saveProject(getApplicationContext());
			Utils.saveToPreferences(this, Constants.PREF_PROJECTNAME_KEY, currentProject.getName());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// needed because of android:onClick in activity_main_menu.xml
	public void handleContinueButton(View view) {
		handleContinueButton();
	}

	public void handleContinueButton() {
		Intent intent = new Intent(this, ProjectActivity.class);
		intent.putExtra(Constants.PROJECTNAME_TO_LOAD, Utils.getCurrentProjectName(this));
		startActivity(intent);
	}

	private void loadProjectInBackground(String projectName) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		LoadProjectTask loadProjectTask = new LoadProjectTask(this, projectName, true, true);
		loadProjectTask.setOnLoadProjectCompleteListener(this);
		loadProjectTask.execute();
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity) {
		if (ProjectManager.getInstance().getCurrentProject() != null && startProjectActivity) {
			Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
			startActivity(intent);
		}
	}

	public void handleNewButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.show(getFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public void handleProgramsButton(View view) {
		findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Intent intent = new Intent(MainMenuActivity.this, MyProjectsActivity.class);
		startActivity(intent);
	}

	public void handleHelpButton(View view) {
		if (!Utils.isNetworkAvailable(view.getContext(), true)) {
			return;
		}

		if (!viewSwitchLock.tryLock()) {
			return;
		}

		startWebViewActivity(Constants.CATROBAT_HELP_URL);
	}

	public void handleWebButton(View view) {
		if (!Utils.isNetworkAvailable(view.getContext(), true)) {
			return;
		}

		if (!viewSwitchLock.tryLock()) {
			return;
		}

		if (Utils.isUserLoggedIn(this)) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
			String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);

			String url = Constants.CATROBAT_TOKEN_LOGIN_URL + username + Constants.CATROBAT_TOKEN_LOGIN_AMP_TOKEN + token;
			startWebViewActivity(url);
		} else {
			startWebViewActivity(Constants.BASE_URL_HTTPS);
		}
	}

	public void startWebViewActivity(String url) {
		Intent intent = new Intent(MainMenuActivity.this, WebViewActivity.class);
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		startActivity(intent);
	}

	public void handleUploadButton(View view) {
		if (!Utils.isNetworkAvailable(view.getContext(), true)) {
			return;
		}

		if (!viewSwitchLock.tryLock()) {
			return;
		}
		ProjectManager.getInstance().uploadProject(Utils.getCurrentProjectName(this), this);
	}

	private void loadProgramFromExternalSource(Uri loadExternalProjectUri) {
		String scheme = loadExternalProjectUri.getScheme();
		if (scheme.startsWith(TYPE_HTTP)) {
			String url = loadExternalProjectUri.toString();
			DownloadUtil.getInstance().prepareDownloadAndStartIfPossible(this, url);
		} else if (scheme.equals(TYPE_FILE)) {

			String path = loadExternalProjectUri.getPath();
			int a = path.lastIndexOf('/') + 1;
			int b = path.lastIndexOf('.');
			String projectName = path.substring(a, b);
			if (!UtilZip.unZipFile(path, Utils.buildProjectPath(projectName))) {
				Utils.showErrorDialog(this, R.string.error_load_project);
			}
		}
	}

	private void setMainMenuButtonContinueText() {
		Button mainMenuButtonContinue = (Button) this.findViewById(R.id.main_menu_button_continue);
		TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(this, R.style.MainMenuButtonTextSecondLine);
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		String mainMenuContinue = this.getString(R.string.main_menu_continue);

		spannableStringBuilder.append(mainMenuContinue);
		spannableStringBuilder.append("\n");
		spannableStringBuilder.append(Utils.getCurrentProjectName(this));

		spannableStringBuilder.setSpan(textAppearanceSpan, mainMenuContinue.length() + 1,
				spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		mainMenuButtonContinue.setText(spannableStringBuilder);
	}

	@Override
	public void onLoadProjectFailure() {
	}

	public void initializeFacebookSdk() {
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Log.d(TAG, loginResult.toString());
						AccessToken accessToken = loginResult.getAccessToken();
						GetFacebookUserInfoTask getFacebookUserInfoTask = new GetFacebookUserInfoTask(MainMenuActivity.this,
								accessToken.getToken(), accessToken.getUserId());
						getFacebookUserInfoTask.setOnGetFacebookUserInfoTaskCompleteListener(signInDialog);
						getFacebookUserInfoTask.execute();
					}

					@Override
					public void onCancel() {
						Log.d(TAG, "cancel");
					}

					@Override
					public void onError(FacebookException exception) {
						ToastUtil.showError(MainMenuActivity.this, exception.getMessage());
						Log.d(TAG, exception.getMessage());
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	public void setSignInDialog(SignInDialog signInDialog) {
		this.signInDialog = signInDialog;
	}
}
