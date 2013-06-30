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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainMenuActivityFragment_Shruti extends SherlockFragment implements OnCheckTokenCompleteListener,
		OnClickListener {

	private static final int DIALOG_ALERT = 10;

	private String TYPE_FILE = "file";
	private String TYPE_HTTP = "http";

	//private MainMenuActivityFragment_Shruti parentFragment = ;

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

	private android.app.ActionBar actionBar;
	private Lock viewSwitchLock = new ViewSwitchLock();

	public void onAttach(MainMenuActivity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v("reached", "till here");
		rootView = inflater.inflate(R.layout.activity_main_menu, container, false);
		Button main_menu_button_new = (Button) rootView.findViewById(R.id.main_menu_button_new);
		main_menu_button_new.setOnClickListener(this);
		Button main_menu_button_continue = (Button) rootView.findViewById(R.id.main_menu_button_continue);
		main_menu_button_continue.setOnClickListener(this);
		Button main_menu_button_programs = (Button) rootView.findViewById(R.id.main_menu_button_programs);
		main_menu_button_programs.setOnClickListener(this);
		Button main_menu_button_forum = (Button) rootView.findViewById(R.id.main_menu_button_forum);
		main_menu_button_forum.setOnClickListener(this);
		Button main_menu_button_web = (Button) rootView.findViewById(R.id.main_menu_button_web);
		main_menu_button_web.setOnClickListener(this);
		Button main_menu_button_upload = (Button) rootView.findViewById(R.id.main_menu_button_upload);
		main_menu_button_upload.setOnClickListener(this);

		return rootView;
	}

	private void showOrientationDialog() {
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("What orientation of the project do you want?");
		builder.setCancelable(true);
		builder.setPositiveButton("Landscape", new LandscapeOnClickListener());
		builder.setNegativeButton("Portrait", new PortraitOnClickListener());
		AlertDialog dialog1 = builder.create();
		dialog1.show();

	}

	public void onFinishEditDialog(String inputText) {
		Toast.makeText(getActivity(), "Hi, " + inputText, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//registerForContextMenu(getListView());
		//if (savedInstanceState != null) {
		//projectToEdit = (ProjectData) savedInstanceState.getSerializable(BUNDLE_ARGUMENTS_PROJECT_DATA);
		//}
		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}
		Utils.updateScreenWidthAndHeight(getActivity());

		//getActivity().setContentView(R.layout.activity_main_menu);

		actionBar = getActivity().getActionBar();
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setTitle(R.string.app_name);

		getActivity().findViewById(R.id.main_menu_button_continue).setEnabled(false);

		// Load external project from URL or local file system.
		Uri loadExternalProjectUri = getActivity().getIntent().getData();
		getActivity().getIntent().setData(null);

		if (loadExternalProjectUri != null) {
			loadProgramFromExternalSource(loadExternalProjectUri);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.main_menu_button_new:
				//Toast.makeText(getActivity().getApplicationContext(), "Shruti", Toast.LENGTH_LONG).show();

				handleNewButton(rootView);
				break;

			case R.id.main_menu_button_continue:
				//Toast.makeText(getActivity().getApplicationContext(), "Shruti", Toast.LENGTH_LONG).show();

				handleContinueButton(rootView);
				break;
			case R.id.main_menu_button_programs:
				//Toast.makeText(getActivity().getApplicationContext(), "Shruti", Toast.LENGTH_LONG).show();

				handleProgramsButton(rootView);
				break;
			case R.id.main_menu_button_forum:
				//Toast.makeText(getActivity().getApplicationContext(), "Shruti", Toast.LENGTH_LONG).show();

				handleForumButton(rootView);
				break;

			case R.id.main_menu_button_web:
				//Toast.makeText(getActivity().getApplicationContext(), "Shruti", Toast.LENGTH_LONG).show();

				handleWebButton(rootView);
				break;
			case R.id.main_menu_button_upload:
				//Toast.makeText(getActivity().getApplicationContext(), "Shruti", Toast.LENGTH_LONG).show();

				handleUploadButton(rootView);
				break;

			default:
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!Utils.checkForExternalStorageAvailableAndDisplayErrorIfNot(getActivity())) {
			return;
		}

		PreStageActivity.shutdownPersistentResources();
		Utils.loadProjectIfNeeded(getActivity());
		setMainMenuButtonContinueText();
		getActivity().findViewById(R.id.main_menu_button_continue).setEnabled(true);
		StatusBarNotificationManager.INSTANCE.displayDialogs(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		if (!Utils.externalStorageAvailable()) {
			return;
		}

		if (ProjectManager.INSTANCE.getCurrentProject() != null) {
			ProjectManager.INSTANCE.saveProject();
			Utils.saveToPreferences(getActivity(), Constants.PREF_PROJECTNAME_KEY, ProjectManager.INSTANCE
					.getCurrentProject().getName());
		}
	}

	// Code from Stackoverflow to reduce memory problems
	// onDestroy() and unbindDrawables() methods taken from
	// http://stackoverflow.com/a/6779067

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!Utils.externalStorageAvailable()) {
			return;
		}
		unbindDrawables(getView().findViewById(R.id.main_menu));
		System.gc();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.menu_main_menu, menu);
		return onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings: {
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivity(intent);
				return true;
			}
			case R.id.menu_about: {
				AboutDialogFragment aboutDialog = new AboutDialogFragment();
				aboutDialog.show(getActivity().getSupportFragmentManager(), AboutDialogFragment.DIALOG_FRAGMENT_TAG);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void handleContinueButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		if (ProjectManager.INSTANCE.getCurrentProject() != null) {
			Intent intent = new Intent(getActivity(), ProjectActivity.class);
			startActivity(intent);
		}
	}

	public void handleNewButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		Toast.makeText(getActivity().getApplicationContext(), "Shruti", Toast.LENGTH_LONG).show();
		showOrientationDialog();
		//getActivity().showDialog(DIALOG_ALERT);

	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_ALERT:
				// Create out AlterDialog
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("What orientation of the project do you want?");
				builder.setCancelable(true);
				builder.setPositiveButton("Landscape", new LandscapeOnClickListener());
				builder.setNegativeButton("Portrait", new PortraitOnClickListener());
				AlertDialog dialog1 = builder.create();
				dialog1.show();
		}
		return onCreateDialog(id);
	}

	private final class PortraitOnClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getActivity().getApplicationContext(), "Project in portrait mode", Toast.LENGTH_LONG).show();
			NewProjectDialog dialog2 = new NewProjectDialog();
			dialog2.show(getActivity().getSupportFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);

		}
	}

	private final class LandscapeOnClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getActivity().getApplicationContext(), "To be continued", Toast.LENGTH_LONG).show();
			//MainMenuActivity.this.finish();
			Intent intent = new Intent(getActivity(), MainMenuActivity_Shruti.class);
			startActivity(intent);
		}
	}

	public void handleProgramsButton(View v) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		Intent intent = new Intent(getActivity(), MyProjectsActivity.class);
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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String token = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = preferences.getString(Constants.USERNAME, Constants.NO_USERNAME);

		if (token == Constants.NO_TOKEN || token.length() != ServerCalls.TOKEN_LENGTH
				|| token.equals(ServerCalls.TOKEN_CODE_INVALID)) {
			showLoginRegisterDialog();
		} else {
			CheckTokenTask checkTokenTask = new CheckTokenTask(getActivity(), token, username);
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
		uploadProjectDialog.show(getActivity().getSupportFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public int createNotification(String downloadName) {
		StatusBarNotificationManager manager = StatusBarNotificationManager.INSTANCE;
		int notificationId = manager.createNotification(downloadName, getActivity(), Constants.DOWNLOAD_NOTIFICATION);
		return notificationId;
	}

	private void showLoginRegisterDialog() {
		LoginRegisterDialog loginRegisterDialog = new LoginRegisterDialog();
		loginRegisterDialog.show(getActivity().getSupportFragmentManager(), LoginRegisterDialog.DIALOG_FRAGMENT_TAG);
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

			Intent downloadIntent = new Intent(getActivity(), ProjectDownloadService.class);
			downloadIntent.putExtra("receiver", new DownloadReceiver(new Handler()));
			downloadIntent.putExtra("downloadName", projectName);
			downloadIntent.putExtra("url", url);
			int notificationId = createNotification(projectName);
			downloadIntent.putExtra("notificationId", notificationId);
			getActivity().startService(downloadIntent);

		} else if (scheme.equals(TYPE_FILE)) {

			String path = loadExternalProjectUri.getPath();
			int a = path.lastIndexOf('/') + 1;
			int b = path.lastIndexOf('.');
			String projectName = path.substring(a, b);
			if (!UtilZip.unZipFile(path, Utils.buildProjectPath(projectName))) {
				Utils.showErrorDialog(getActivity(), getResources().getString(R.string.error_load_project));
			}
		}
	}

	private void setMainMenuButtonContinueText() {
		Button mainMenuButtonContinue = (Button) getActivity().findViewById(R.id.main_menu_button_continue);
		TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(getActivity(),
				R.style.MainMenuButtonTextSecondLine);
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		String mainMenuContinue = this.getString(R.string.main_menu_continue);

		spannableStringBuilder.append(mainMenuContinue);
		spannableStringBuilder.append("\n");
		spannableStringBuilder.append(ProjectManager.INSTANCE.getCurrentProject().getName());

		spannableStringBuilder.setSpan(textAppearanceSpan, mainMenuContinue.length() + 1,
				spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		mainMenuButtonContinue.setText(spannableStringBuilder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

}
