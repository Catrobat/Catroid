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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.content.ContextCompat;
import android.support.v7.media.MediaRouter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.TrackingConstants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.io.LoadProjectTask.OnLoadProjectCompleteListener;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.transfers.GetFacebookUserInfoTask;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.catrobat.catroid.utils.DividerUtil;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import java.util.Locale;
import java.util.concurrent.locks.Lock;

public class BaseMainMenuActivity extends BaseCastActivity implements OnLoadProjectCompleteListener {

	private static final String TAG = BaseMainMenuActivity.class.getSimpleName();

	private static final Boolean STANDALONE_MODE = BuildConfig.FEATURE_APK_GENERATOR_ENABLED;

	public static final String SHARED_PREFERENCES_SHOW_BROWSER_WARNING = "shared_preferences_browser_warning";
	public static final int REQUEST_CODE_GOOGLE_PLUS_SIGNIN = 100;

	public static final String RESTART_INTENT = "restart";

	private static final String TYPE_FILE = "file";
	private static final String TYPE_HTTP = "http";

	protected Lock viewSwitchLock = new ViewSwitchLock();
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

		if (getIntent().getBooleanExtra(RESTART_INTENT, false)) {
			finish();
			Intent intent = getIntent();
			intent.removeExtra(RESTART_INTENT);
			startActivity(intent);
		}

		if (!BuildConfig.RESTRICTED_LOGIN) {
			initializeFacebookSdk();
		}

		for (int preference : BaseSettingsActivity.preferences) {
			PreferenceManager.setDefaultValues(this, preference, true);
		}

		UtilUi.updateScreenWidthAndHeight(this);

		if (STANDALONE_MODE) {
			/*requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
			getActionBar().hide();
			setContentView(R.layout.activity_main_menu_splashscreen);
			UtilZip.unzipProgram(this);
			loadStageProject(Constants.STANDALONE_PROJECT_NAME);
		} else {
			if (!DividerUtil.isElementSpacing()) {
				setContentView(R.layout.activity_main_menu);
			} else {
				setContentView(R.layout.activity_main_menu_with_dividers);
			}

			IconsUtil.setMenuIconSize(this.findViewById(android.R.id.content), IconsUtil.largeIconSizeMainMenu);

			final ActionBar actionBar = getActionBar();
			actionBar.setDisplayUseLogoEnabled(true);
			actionBar.setHomeButtonEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setTitle(R.string.app_name);

			findViewById(R.id.main_menu_button_continue).setEnabled(false);

			if (!BuildConfig.FEATURE_TEMPLATES) {
				findViewById(R.id.main_menu_button_templates).setVisibility(View.GONE);
			}

			// Load external project from URL or local file system.
			Uri loadExternalProjectUri = getIntent().getData();
			getIntent().setData(null);

			if (loadExternalProjectUri != null) {
				loadProgramFromExternalSource(loadExternalProjectUri);
			}

			if (BaseSettingsActivity.isCastSharedPreferenceEnabled(this)) {
				CastManager.getInstance().initializeCast(this);
			}
		}
		TextSizeUtil.enlargeViewGroup((ViewGroup) getWindow().getDecorView().getRootView());
		DividerUtil.setDivider(this, (LinearLayout) findViewById(R.id.main_menu_buttons_container));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}

		BaseSettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(this, false);
		BaseSettingsActivity.setLegoMindstormsEV3SensorChooserEnabled(this, false);

		BaseSettingsActivity.setDroneChooserEnabled(this, false);

		if (BaseSettingsActivity.isCastSharedPreferenceEnabled(this)) {
			CastManager.getInstance().initializeCast(this);
		} else if (CastManager.getInstance().isConnected()) {
			CastManager.getInstance().getMediaRouter().unselect(MediaRouter.UNSELECT_REASON_STOPPED);
		}

		findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
		final Activity activity = this;
		idlingResource.increment();

		Utils.getTrackingUtilProxy().trackStopWebSessionTutorial();
		Utils.getTrackingUtilProxy().trackStopWebSessionExplore();

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
		TextSizeUtil.enlargeOptionsMenu(menu);

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

	public void handleContinueButton(View view) {
		Project project = ProjectManager.getInstance().getCurrentProject();
		String projectName = project != null ? project.getName() : TrackingConstants.NO_PROGRAM;
		Utils.getTrackingUtilProxy().trackMenuButtonProject(projectName, TrackingConstants.MAIN_MENU_CONTINUE);
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
			Intent intent = new Intent(this, ProjectActivity.class);
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
		Intent intent = new Intent(this, MyProjectsActivity.class);
		startActivity(intent);
	}

	public void handleTemplatesButton(View view) {
		findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Intent intent = new Intent(this, TemplatesActivity.class);
		startActivity(intent);
	}

	public void handleHelpButton(View view) {
		if (!Utils.isNetworkAvailable(view.getContext(), true)) {
			return;
		}

		if (!viewSwitchLock.tryLock()) {
			return;
		}

		Utils.getTrackingUtilProxy().trackStartWebSessionTutorial();
		Intent helpUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.CATROBAT_HELP_URL));
		startActivity(helpUrlIntent);
	}

	public void handleWebButton(View view) {
		if (!Utils.isNetworkAvailable(view.getContext(), true)) {
			return;
		}

		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Utils.getTrackingUtilProxy().trackStartWebSessionExplore();

		String url = Constants.FLAVORED_BASE_URL_HTTPS;
		url = Utils.addUsernameAndTokenInfoToUrl(url, this);
		startWebViewActivity(url);
	}

	public void startWebViewActivity(String url) {
		Intent intent = new Intent(this, WebViewActivity.class);
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

		if (BuildConfig.RESTRICTED_LOGIN && !Utils.isCreateAtSchoolUser(this)) {
			SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			sharedPrefEditor.putString(Constants.TOKEN, Constants.NO_TOKEN);
			sharedPrefEditor.putString(Constants.USERNAME, Constants.NO_USERNAME);
			sharedPrefEditor.apply();
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
		TextAppearanceSpan textAppearanceSpan = TextSizeUtil.getTextAppearanceSpanForMainMenu(this);
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
						GetFacebookUserInfoTask getFacebookUserInfoTask = new GetFacebookUserInfoTask(BaseMainMenuActivity.this, accessToken.getToken(), accessToken.getUserId());
						getFacebookUserInfoTask.setOnGetFacebookUserInfoTaskCompleteListener(signInDialog);
						getFacebookUserInfoTask.execute();
					}

					@Override
					public void onCancel() {
						Log.d(TAG, "cancel");
					}

					@Override
					public void onError(FacebookException exception) {
						ToastUtil.showError(BaseMainMenuActivity.this, exception.getMessage());
						Log.d(TAG, exception.getMessage());
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!STANDALONE_MODE) {
			super.onActivityResult(requestCode, resultCode, data);
			if (!BuildConfig.RESTRICTED_LOGIN) {
				callbackManager.onActivityResult(requestCode, resultCode, data);
			}
		} else {
			if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
				SensorHandler.startSensorListener(this);

				Intent intent = new Intent(this, StageActivity.class);
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
