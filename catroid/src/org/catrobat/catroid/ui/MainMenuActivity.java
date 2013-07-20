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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.locks.Lock;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.transfers.CheckTokenTask;
import org.catrobat.catroid.transfers.CheckTokenTask.OnCheckTokenCompleteListener;
import org.catrobat.catroid.transfers.ProjectDownloadService;
import org.catrobat.catroid.ui.dialogs.AboutDialogFragment;
import org.catrobat.catroid.ui.dialogs.LoginRegisterDialog;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.UploadProjectDialog;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainMenuActivity extends SherlockFragmentActivity implements OnCheckTokenCompleteListener {

	private String TYPE_FILE = "file";
	private String TYPE_HTTP = "http";

	private class DownloadReceiver extends ResultReceiver {

		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == Constants.UPDATE_DOWNLOAD_PROGRESS) {
				long progress = resultData.getLong("currentDownloadProgress");
				boolean endOfFileReached = resultData.getBoolean("endOfFileReached");
				Integer notificationId = resultData.getInt("notificationId");
				String projectName = resultData.getString("projectName");
				if (endOfFileReached) {
					progress = 100;
				}
				String notificationMessage = "Download " + progress + "% "
						+ getString(R.string.notification_percent_completed) + ":" + projectName;

				StatusBarNotificationManager.getInstance().updateNotification(notificationId, notificationMessage,
						Constants.DOWNLOAD_NOTIFICATION, endOfFileReached);
			}
		}
	}

	private static final String TAG = "MainMenuActivity";
	private static final String PROJECTNAME_TAG = "fname=";

	private ActionBar actionBar;
	private Lock viewSwitchLock = new ViewSwitchLock();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(this)) {
			return;
		}
		Utils.updateScreenWidthAndHeight(this);

		setContentView(R.layout.activity_main_menu);

		actionBar = getSupportActionBar();
		actionBar.setDisplayUseLogoEnabled(true);
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

		PreStageActivity.shutdownPersistentResources();
		Utils.loadProjectIfNeeded(this);
		setMainMenuButtonContinueText();
		findViewById(R.id.main_menu_button_continue).setEnabled(true);
		StatusBarNotificationManager.getInstance().displayDialogs(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!Utils.externalStorageAvailable()) {
			return;
		}

		// onPause is sufficient --> gets called before "process_killed",
		// onStop(), onDestroy(), onRestart()
		// also when you switch activities
		if (ProjectManager.getInstance().getCurrentProject() != null) {
			ProjectManager.getInstance().saveProject();
			Utils.saveToPreferences(this, Constants.PREF_PROJECTNAME_KEY, ProjectManager.getInstance().getCurrentProject()
					.getName());
		}
	}

	// Code from Stackoverflow to reduce memory problems
	// onDestroy() and unbindDrawables() methods taken from
	// http://stackoverflow.com/a/6779067
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!Utils.externalStorageAvailable()) {
			return;
		}
		unbindDrawables(findViewById(R.id.main_menu));
		System.gc();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings: {
				Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
				startActivity(intent);
				return true;
			}
			case R.id.menu_about: {
				AboutDialogFragment aboutDialog = new AboutDialogFragment();
				aboutDialog.show(getSupportFragmentManager(), AboutDialogFragment.DIALOG_FRAGMENT_TAG);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void handleContinueButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		if (ProjectManager.getInstance().getCurrentProject() != null) {
			Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
			startActivity(intent);
		}
	}

	public void handleNewButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.show(getSupportFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public void handleProgramsButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Intent intent = new Intent(MainMenuActivity.this, MyProjectsActivity.class);
		startActivity(intent);
	}

	public void handleForumButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.catrobat_forum).toString()));
		startActivity(browserIntent);
	}

	public void handleWebButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(getText(R.string.pocketcode_website).toString()));
		startActivity(browserIntent);
	}

	public void handleUploadButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String token = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = preferences.getString(Constants.USERNAME, Constants.NO_USERNAME);

		if (token == Constants.NO_TOKEN || token.length() != ServerCalls.TOKEN_LENGTH
				|| token.equals(ServerCalls.TOKEN_CODE_INVALID)) {
			showLoginRegisterDialog();
		} else {
			CheckTokenTask checkTokenTask = new CheckTokenTask(this, token, username);
			checkTokenTask.setOnCheckTokenCompleteListener(this);
			checkTokenTask.execute();
		}
	}

	@Override
	public void onTokenNotValid() {
		showLoginRegisterDialog();
	}

	@Override
	public void onCheckTokenSuccess() {
		UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
		uploadProjectDialog.show(getSupportFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public int createNotification(String downloadName) {
		StatusBarNotificationManager manager = StatusBarNotificationManager.getInstance();
		int notificationId = manager.createNotification(downloadName, this, Constants.DOWNLOAD_NOTIFICATION);
		return notificationId;
	}

	private void showLoginRegisterDialog() {
		LoginRegisterDialog loginRegisterDialog = new LoginRegisterDialog();
		loginRegisterDialog.show(getSupportFragmentManager(), LoginRegisterDialog.DIALOG_FRAGMENT_TAG);
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	private void loadProgramFromExternalSource(Uri loadExternalProjectUri) {
		String scheme = loadExternalProjectUri.getScheme();
		if (scheme.startsWith((TYPE_HTTP))) {
			String url = loadExternalProjectUri.toString();
			int projectNameIndex = url.lastIndexOf(PROJECTNAME_TAG) + PROJECTNAME_TAG.length();
			String projectName = url.substring(projectNameIndex);
			try {
				projectName = URLDecoder.decode(projectName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Could not decode project name: " + projectName, e);
			}

			Intent downloadIntent = new Intent(this, ProjectDownloadService.class);
			downloadIntent.putExtra("receiver", new DownloadReceiver(new Handler()));
			downloadIntent.putExtra("downloadName", projectName);
			downloadIntent.putExtra("url", url);
			int notificationId = createNotification(projectName);
			downloadIntent.putExtra("notificationId", notificationId);
			startService(downloadIntent);

		} else if (scheme.equals(TYPE_FILE)) {

			String path = loadExternalProjectUri.getPath();
			int a = path.lastIndexOf('/') + 1;
			int b = path.lastIndexOf('.');
			String projectName = path.substring(a, b);
			if (!UtilZip.unZipFile(path, Utils.buildProjectPath(projectName))) {
				Utils.showErrorDialog(this, getResources().getString(R.string.error_load_project));
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
		spannableStringBuilder.append(ProjectManager.getInstance().getCurrentProject().getName());

		spannableStringBuilder.setSpan(textAppearanceSpan, mainMenuContinue.length() + 1,
				spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		mainMenuButtonContinue.setText(spannableStringBuilder);
	}
}
