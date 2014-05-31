/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.LoadProjectTask;
import org.catrobat.catroid.io.LoadProjectTask.OnLoadProjectCompleteListener;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.dialogs.AboutDialogFragment;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import java.util.concurrent.locks.Lock;

public class MainMenuActivity extends BaseActivity implements OnLoadProjectCompleteListener {

	public static final String SHARED_PREFERENCES_SHOW_BROWSER_WARNING = "shared_preferences_browser_warning";

	private static final String TYPE_FILE = "file";
	private static final String TYPE_HTTP = "http";

	private Lock viewSwitchLock = new ViewSwitchLock();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}
		Utils.updateScreenWidthAndHeight(this);

		setContentView(R.layout.activity_main_menu);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setTitle(R.string.app_name);

		findViewById(R.id.main_menu_button_continue).setEnabled(false);

		// Load external project from URL or local file system.
		Uri loadExternalProjectUri = getIntent().getData();
		getIntent().setData(null);

		if (loadExternalProjectUri != null) {
			loadProgramFromExternalSource(loadExternalProjectUri);
		}

		if (!BackPackListManager.isBackpackFlag()) {
			BackPackListManager.getInstance().setSoundInfoArrayListEmpty();
		}
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}
		// modified
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (sharedPreferences.getBoolean(Constants.FIRST_TIME_INSTALL, true)) {
			Log.d("MainMenuActivity", "firsttime");
			UtilFile.createStandardProjectIfRootDirectoryIsEmpty(this);
		}

		Editor edit = sharedPreferences.edit();
		edit.putBoolean(Constants.FIRST_TIME_INSTALL, false);
		edit.commit();

		//TODO Drone dont create project for now
		//if (BuildConfig.DEBUG && DroneUtils.isDroneSharedPreferenceEnabled(getApplication(), false)) {
		//	UtilFile.loadExistingOrCreateStandardDroneProject(this);
		//}
		//SettingsActivity.setTermsOfSerivceAgreedPermanently(this, false);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}

		findViewById(R.id.progress_circle).setVisibility(View.GONE);

		PreStageActivity.shutdownPersistentResources();

		String projectName = getIntent().getStringExtra(StatusBarNotificationManager.EXTRA_PROJECT_NAME);

		if (projectName != null) {
			Log.d("MainMenu", "projectName not null");
			loadProjectInBackground(projectName);
		} else {
			Log.d("MainMenu", "projectName null");
			Utils.loadProjectIfNeeded(this);
		}

		//		setMainMenuButtonContinueText();
		//		if (ProjectManager.getInstance().getCurrentProject() != null) {
		//			Log.d("MainMenuActivity", "projectname not null");
		//			findViewById(R.id.main_menu_button_continue).setEnabled(true);
		//		} else {
		//			Log.d("MainMenuActivity", "projectname null");
		//			findViewById(R.id.main_menu_button_continue).setEnabled(false);
		//		}

		getIntent().removeExtra(StatusBarNotificationManager.EXTRA_PROJECT_NAME);

		updateContinueButton();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!Utils.externalStorageAvailable()) {
			return;
		}

		//<<<<<<< HEAD
		//		if (ProjectManager.getInstance().getCurrentProject() != null) {
		//			Log.d("MainMenuActivity", "current project != null "
		//					+ ProjectManager.getInstance().getCurrentProject().getName());
		//=======
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject != null) {
			//>>>>>>> master

			ProjectManager.getInstance().saveProject();
			Utils.saveToPreferences(this, Constants.PREF_PROJECTNAME_KEY, currentProject.getName());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_rate_app:
				launchMarket();
				return true;
			case R.id.menu_terms_of_use:
				TermsOfUseDialogFragment termsOfUseDialog = new TermsOfUseDialogFragment();
				termsOfUseDialog.show(getSupportFragmentManager(), TermsOfUseDialogFragment.DIALOG_FRAGMENT_TAG);
				return true;
			case R.id.menu_about:
				AboutDialogFragment aboutDialog = new AboutDialogFragment();
				aboutDialog.show(getSupportFragmentManager(), AboutDialogFragment.DIALOG_FRAGMENT_TAG);
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	// Taken from http://stackoverflow.com/a/11270668
	private void launchMarket() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.main_menu_play_store_not_installed, Toast.LENGTH_SHORT).show();
		}
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
		LoadProjectTask loadProjectTask = new LoadProjectTask(this, projectName, false, true);
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
		dialog.show(getSupportFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
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
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		startWebViewActivity(Constants.CATROBAT_HELP_URL);
	}

	public void handleWebButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		startWebViewActivity(Constants.BASE_URL_HTTPS);

	}

	private void startWebViewActivity(String url) {
		// TODO just a quick fix for not properly working webview on old devices
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			boolean showBrowserWarning = preferences.getBoolean(SHARED_PREFERENCES_SHOW_BROWSER_WARNING, true);
			if (showBrowserWarning) {
				showWebWarningDialog();
			} else {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASE_URL_HTTPS));
				startActivity(browserIntent);
			}
		} else {
			Intent intent = new Intent(MainMenuActivity.this, WebViewActivity.class);
			intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
			startActivity(intent);
		}

	}

	private void showWebWarningDialog() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final View checkboxView = View.inflate(this, R.layout.dialog_web_warning, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getText(R.string.main_menu_web_dialog_title));
		builder.setView(checkboxView);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				CheckBox dontShowAgainCheckBox = (CheckBox) checkboxView
						.findViewById(R.id.main_menu_web_dialog_dont_show_checkbox);
				if (dontShowAgainCheckBox != null && dontShowAgainCheckBox.isChecked()) {
					preferences.edit().putBoolean(SHARED_PREFERENCES_SHOW_BROWSER_WARNING, false).commit();
				}

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASE_URL_HTTPS));
				startActivity(browserIntent);
			}
		});
		builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
	}

	public void handleUploadButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		ProjectManager.getInstance().uploadProject(Utils.getCurrentProjectName(this), this);
	}

	private void loadProgramFromExternalSource(Uri loadExternalProjectUri) {
		String scheme = loadExternalProjectUri.getScheme();
		if (scheme.startsWith((TYPE_HTTP))) {
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

	//	private void setMainMenuButtonContinueText() {
	//		Button mainMenuButtonContinue = (Button) this.findViewById(R.id.main_menu_button_continue);
	//		TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(this, R.style.MainMenuButtonTextSecondLine);
	//		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
	//		String mainMenuContinue = this.getString(R.string.main_menu_continue);
	//
	//		spannableStringBuilder.append(mainMenuContinue);
	//		spannableStringBuilder.append("\n");
	//		spannableStringBuilder.append(Utils.getCurrentProjectName(this));
	//
	//		spannableStringBuilder.setSpan(textAppearanceSpan, mainMenuContinue.length() + 1,
	//				spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	//
	//		mainMenuButtonContinue.setText(spannableStringBuilder);
	//	}

	private void updateContinueButton() {
		Button mainMenuButtonContinue = (Button) this.findViewById(R.id.main_menu_button_continue);
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		String mainMenuContinue = this.getString(R.string.main_menu_continue);
		spannableStringBuilder.append(mainMenuContinue);
		spannableStringBuilder.append("\n");

		if (ProjectManager.getInstance().getCurrentProject() != null) {
			spannableStringBuilder.append(Utils.getCurrentProjectName(this));
			TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(this, R.style.MainMenuButtonTextSecondLine);
			spannableStringBuilder.setSpan(textAppearanceSpan, mainMenuContinue.length() + 1,
					spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			mainMenuButtonContinue.setTextColor(getResources().getColor(R.color.main_menu_button_text_color));

			mainMenuButtonContinue.setText(spannableStringBuilder);
			mainMenuButtonContinue.getCompoundDrawables()[0].setAlpha(255);
			mainMenuButtonContinue.getBackground().setAlpha(255);
			findViewById(R.id.main_menu_button_continue).setEnabled(true);
		} else {
			spannableStringBuilder.append("---");
			TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(this,
					R.style.MainMenuButtonTextSecondLineDisabled);
			spannableStringBuilder.setSpan(textAppearanceSpan, mainMenuContinue.length() + 1,
					spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			mainMenuButtonContinue.setTextColor(getResources().getColor(R.color.main_menu_button_text_color_disabled));

			mainMenuButtonContinue.setText(spannableStringBuilder);
			mainMenuButtonContinue.getCompoundDrawables()[0].setAlpha(127);
			mainMenuButtonContinue.getBackground().setAlpha(178);
			findViewById(R.id.main_menu_button_continue).setEnabled(false);
		}

	}

	@Override
	public void onLoadProjectFailure() {

	}
}
