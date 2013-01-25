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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckTokenTask;
import org.catrobat.catroid.transfers.CheckTokenTask.OnCheckTokenCompleteListener;
import org.catrobat.catroid.transfers.ProjectDownloadService;
import org.catrobat.catroid.ui.dialogs.AboutDialogFragment;
import org.catrobat.catroid.ui.dialogs.LoginRegisterDialog;
import org.catrobat.catroid.ui.dialogs.NewProjectDialog;
import org.catrobat.catroid.ui.dialogs.UploadProjectDialog;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainMenuActivity extends SherlockFragmentActivity implements OnCheckTokenCompleteListener,
		ErrorListenerInterface {

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

				StatusBarNotificationManager.INSTANCE.updateNotification(notificationId, notificationMessage,
						Constants.DOWNLOAD_NOTIFICATION, endOfFileReached);
			}
		}
	}

	private static final String TAG = "MainMenuActivity";
	private static final String PROJECTNAME_TAG = "fname=";

	private ActionBar actionBar;

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

		Utils.loadProjectIfNeeded(this, this);
		findViewById(R.id.main_menu_button_continue).setEnabled(true);
		StatusBarNotificationManager.INSTANCE.displayDialogs(this);
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
		if (ProjectManager.INSTANCE.getCurrentProject() != null) {
			ProjectManager.INSTANCE.saveProject();
			Utils.saveToPreferences(this, Constants.PREF_PROJECTNAME_KEY, ProjectManager.INSTANCE.getCurrentProject()
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
		if (ProjectManager.INSTANCE.getCurrentProject() != null) {
			Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
			startActivity(intent);
		}
	}

	public void handleNewButton(View v) {
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.show(getSupportFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public void handleProgramsButton(View v) {
		Intent intent = new Intent(MainMenuActivity.this, MyProjectsActivity.class);
		startActivity(intent);
	}

	public void handleForumButton(View v) {
		Intent browerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.catrobat_forum).toString()));
		startActivity(browerIntent);
	}

	public void handleWebButton(View v) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.catroid_website).toString()));
		startActivity(browserIntent);
	}

	public void handleUploadButton(View v) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String token = preferences.getString(Constants.TOKEN, null);

		if (token == null || token.length() == 0 || token.equals("0")) {
			showLoginRegisterDialog();
		} else {
			CheckTokenTask checkTokenTask = new CheckTokenTask(this, token);
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

	@Override
	public void showErrorDialog(String errorMessage) {
		Utils.displayErrorMessageFragment(getSupportFragmentManager(), errorMessage);
	}

	public int createNotification(String downloadName) {
		StatusBarNotificationManager manager = StatusBarNotificationManager.INSTANCE;
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
		if (loadExternalProjectUri.getScheme().equals("http")) {
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

		} else if (loadExternalProjectUri.getScheme().equals("file")) {

			String path = loadExternalProjectUri.getPath();
			int a = path.lastIndexOf('/') + 1;
			int b = path.lastIndexOf('.');
			String projectName = path.substring(a, b);
			if (!UtilZip.unZipFile(path, Utils.buildProjectPath(projectName))) {
				Utils.displayErrorMessageFragment(getSupportFragmentManager(),
						getResources().getString(R.string.error_load_project));
			}
		}
	}

}
