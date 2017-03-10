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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.io.LoadProjectTask.OnLoadProjectCompleteListener;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.transfers.GetFacebookUserInfoTask;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.locks.Lock;

public class MainMenuActivity extends BaseActivity implements OnLoadProjectCompleteListener {

	private static final String TAG = MainMenuActivity.class.getSimpleName();

	private static final String START_PROJECT = BuildConfig.START_PROJECT;
	private static final Boolean STANDALONE_MODE = BuildConfig.FEATURE_APK_GENERATOR_ENABLED;
	private static final String ZIP_FILE_NAME = START_PROJECT + ".zip";
	private static final String STANDALONE_PROJECT_NAME = BuildConfig.PROJECT_NAME;

	public static final String SHARED_PREFERENCES_SHOW_BROWSER_WARNING = "shared_preferences_browser_warning";
	public static final int REQUEST_CODE_GOOGLE_PLUS_SIGNIN = 100;

	private static final String TYPE_FILE = "file";
	private static final String TYPE_HTTP = "http";

	private Lock viewSwitchLock = new ViewSwitchLock();
	private CallbackManager callbackManager;
	private SignInDialog signInDialog;
	private Menu mainMenu;

	CountingIdlingResource idlingResource = new CountingIdlingResource(TAG);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}
		initializeFacebookSdk();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		UtilUi.updateScreenWidthAndHeight(this);

		if (STANDALONE_MODE) {
			/*requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
			getActionBar().hide();
			setContentView(R.layout.activity_main_menu_splashscreen);
			unzipProgram();
		} else {
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
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}
		AppEventsLogger.activateApp(this);

		SettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(this, false);
		SettingsActivity.setLegoMindstormsEV3SensorChooserEnabled(this, false);

		SettingsActivity.setDroneChooserEnabled(this, false);

		findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
		final Activity activity = this;
		idlingResource.increment();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				UtilFile.createStandardProjectIfRootDirectoryIsEmpty(activity);
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						finishOnCreateAfterRunnable();
					}
				});
			}
		};
		(new Thread(r)).start();
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
		mainMenu = menu;

		final MenuItem scratchConverterMenuItem = menu.findItem(R.id.menu_scratch_converter);
		if (scratchConverterMenuItem != null) {
			final String title = getString(R.string.main_menu_scratch_converter);
			final String beta = getString(R.string.beta).toUpperCase(Locale.getDefault());
			final SpannableString spanTitle = new SpannableString(title + " " + beta);
			final int begin = title.length() + 1;
			final int end = begin + beta.length();
			final int betaLabelColor = ContextCompat.getColor(this, R.color.beta_label_color);
			spanTitle.setSpan(new ForegroundColorSpan(betaLabelColor), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			scratchConverterMenuItem.setTitle(spanTitle);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem logout = mainMenu.findItem(R.id.menu_logout);
		MenuItem login = mainMenu.findItem(R.id.menu_login);
		logout.setVisible(Utils.isUserLoggedIn(this));
		login.setVisible(!Utils.isUserLoggedIn(this));

		if (!BuildConfig.FEATURE_SCRATCH_CONVERTER_ENABLED) {
			mainMenu.removeItem(R.id.menu_scratch_converter);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void finishOnCreateAfterRunnable() {
		if (!STANDALONE_MODE) {
			findViewById(R.id.progress_circle).setVisibility(View.GONE);
			findViewById(R.id.main_menu_buttons_container).setVisibility(View.VISIBLE);

			PreStageActivity.shutdownPersistentResources();

			setMainMenuButtonContinueText();
			findViewById(R.id.main_menu_button_continue).setEnabled(true);
		}

		String projectName = getIntent().getStringExtra(StatusBarNotificationManager.EXTRA_PROJECT_NAME);
		if (projectName != null) {
			loadProjectInBackground(projectName);
		}
		getIntent().removeExtra(StatusBarNotificationManager.EXTRA_PROJECT_NAME);

		if (ProjectManager.getInstance().getHandleNewSceneFromScriptActivity()) {
			Intent intent = new Intent(this, ProjectActivity.class);
			intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
		}
		idlingResource.decrement();
	}

	// needed because of android:onClick in activity_main_menu.xml
	public void handleContinueButton(View view) {
		handleContinueButton();
	}

	public void handleContinueButton() {
		LoadProjectTask loadProjectTask = new LoadProjectTask(this, Utils.getCurrentProjectName(this), true, true);
		loadProjectTask.setOnLoadProjectCompleteListener(this);
		findViewById(R.id.main_menu_buttons_container).setVisibility(View.GONE);
		loadProjectTask.execute();
	}

	private void loadProjectInBackground(String projectName) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		LoadProjectTask loadProjectTask = new LoadProjectTask(this, projectName, true, true);
		loadProjectTask.setOnLoadProjectCompleteListener(this);
		findViewById(R.id.main_menu_buttons_container).setVisibility(View.GONE);
		loadProjectTask.execute();
	}

	@Override
	public void onLoadProjectSuccess(boolean startProjectActivity) {
		if (STANDALONE_MODE) {
			Log.d("STANDALONE", "onLoadProjectSucess -> startStage");
			startStageProject();
		} else if (ProjectManager.getInstance().getCurrentProject() != null && startProjectActivity) {
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
		findViewById(R.id.main_menu_buttons_container).setVisibility(View.VISIBLE);
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
		if (!STANDALONE_MODE) {
			super.onActivityResult(requestCode, resultCode, data);
			callbackManager.onActivityResult(requestCode, resultCode, data);
		} else {
			if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
				SensorHandler.startSensorListener(this);

				Intent intent = new Intent(MainMenuActivity.this, StageActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
				}
				startActivityForResult(intent, StageActivity.STAGE_ACTIVITY_FINISH);
			}
			if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
				finish();
			}
		}
	}

	public void setSignInDialog(SignInDialog signInDialog) {
		this.signInDialog = signInDialog;
	}

	private void unzipProgram() {
		StorageHandler.getInstance();
		String zipFileString = Constants.DEFAULT_ROOT + "/" + ZIP_FILE_NAME;
		copyProgramZip();
		Log.d("STANDALONE", "default root " + Constants.DEFAULT_ROOT);
		Log.d("STANDALONE", "zip file name:" + ZIP_FILE_NAME);
		Archiver archiver = ArchiverFactory.createArchiver("zip");
		File unpackedDirectory = new File(Constants.DEFAULT_ROOT + "/" + START_PROJECT);
		try {
			archiver.extract(new File(zipFileString), unpackedDirectory);
		} catch (IOException e) {
			Log.d("STANDALONE", "Can't extract program", e);
		}

		File destination = new File(Constants.DEFAULT_ROOT + "/" + STANDALONE_PROJECT_NAME);
		if (unpackedDirectory.isDirectory()) {
			unpackedDirectory.renameTo(destination);
		}

		loadStageProject(STANDALONE_PROJECT_NAME);

		File zipFile = new File(zipFileString);
		if (zipFile.exists()) {
			zipFile.delete();
		}
	}

	private void copyProgramZip() {
		AssetManager assetManager = getResources().getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("STANDALONE", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			if (filename.contains(ZIP_FILE_NAME)) {
				InputStream in;
				OutputStream out;
				try {
					in = assetManager.open(filename);
					File outFile = new File(Constants.DEFAULT_ROOT, filename);
					out = new FileOutputStream(outFile);
					copyFile(in, out);
					out.flush();
					out.close();
					in.close();
				} catch (IOException e) {
					Log.e("STANDALONE", "Failed to copy asset file: " + filename, e);
				}
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private void loadStageProject(String projectName) {
		LoadProjectTask loadProjectTask = new LoadProjectTask(this, projectName, false, false);
		loadProjectTask.setOnLoadProjectCompleteListener(this);
		Log.e("STANDALONE", "going to execute standalone project");
		loadProjectTask.execute();
	}

	private void startStageProject() {
		//ProjectManager.getInstance().getCurrentProject().getUserVariables().resetAllUserVariables();
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@VisibleForTesting
	@NonNull
	public IdlingResource getIdlingResource() {
		return idlingResource;
	}
}
