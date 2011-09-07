/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.transfers.CheckTokenTask;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoadProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoginRegisterDialog;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.UploadProjectDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class MainMenuActivity extends Activity {
	private static final String PREF_PROJECTNAME_KEY = "projectName";
	private ProjectManager projectManager;
	private ActivityHelper activityHelper = new ActivityHelper(this);
	private static final int DIALOG_NEW_PROJECT = 0;
	private static final int DIALOG_LOAD_PROJECT = 1;
	private static final int DIALOG_UPLOAD_PROJECT = 2;
	private static final int DIALOG_ABOUT = 3;
	private static final int DIALOG_LOGIN_REGISTER = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.updateScreenWidthAndHeight(this);

		setContentView(R.layout.activity_main_menu);
		projectManager = ProjectManager.getInstance();

		// Try to load sharedPreferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String projectName = prefs.getString(PREF_PROJECTNAME_KEY, null);

		if (projectName != null) {
			projectManager.loadProject(projectName, this, false);
		} else {
			projectManager.initializeDefaultProject(this);
		}

		if (projectManager.getCurrentProject() == null) {
			Button currentProjectButton = (Button) findViewById(R.id.current_project_button);
			currentProjectButton.setEnabled(false);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		activityHelper.setupActionBar(true, null);
		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				if (projectManager.getCurrentProject() != null) {
					Intent intent = new Intent(MainMenuActivity.this, StageActivity.class);
					startActivity(intent);
				}
			}
		}, false);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		if (projectManager.getCurrentProject() != null
				&& StorageHandler.getInstance().projectExists(projectManager.getCurrentProject().getName())) {
			projectManager.saveProject();
		}

		switch (id) {
			case DIALOG_NEW_PROJECT:
				dialog = new NewProjectDialog(this);
				break;
			case DIALOG_LOAD_PROJECT:
				dialog = new LoadProjectDialog(this);
				break;
			case DIALOG_ABOUT:
				dialog = new AboutDialog(this);
				break;
			case DIALOG_UPLOAD_PROJECT:
				dialog = new UploadProjectDialog(this);
				break;
			case DIALOG_LOGIN_REGISTER:
				dialog = new LoginRegisterDialog(this);
				break;
			default:
				dialog = null;
				break;
		}
		return dialog;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		if (projectManager.getCurrentProject() == null) {
			return;
		}

		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (projectManager.getCurrentProject() == null) {
			return;
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		// onPause is sufficient --> gets called before "process_killed",
		// onStop(), onDestroy(), onRestart()
		// also when you switch activities
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor edit = prefs.edit();
			edit.putString(PREF_PROJECTNAME_KEY, projectManager.getCurrentProject().getName());
			edit.commit();
		}
	}

	public void handleCurrentProjectButton(View v) {
		if (projectManager.getCurrentProject() != null) {
			Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
			startActivity(intent);
		}
	}

	public void handleNewProjectButton(View v) {
		showDialog(DIALOG_NEW_PROJECT);
	}

	public void handleLoadProjectButton(View v) {
		showDialog(DIALOG_LOAD_PROJECT);
	}

	public void handleUploadProjectButton(View v) {
		// as long as the token handling on the server is not implemented, we don't use the 
		// user concept. Always use the token 0
		//showDialog(DIALOG_UPLOAD_PROJECT);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String token = preferences.getString(Consts.TOKEN, null);

		if (token == null || token.length() == 0 || token.equals("0")) {
			showDialog(DIALOG_LOGIN_REGISTER);
		} else {
			new CheckTokenTask(this, token).execute();
		}
	}

	public void handleWebResourcesButton(View v) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.catroid_website).toString()));
		startActivity(browserIntent);
	}

	public void handleSettingsButton(View v) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_settings, (ViewGroup) findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("Settings not yet implemented!");

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public void handleTutorialButton(View v) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_tutorial, (ViewGroup) findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("Tutorial not yet implemented!");

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public void handleAboutCatroidButton(View v) {
		showDialog(DIALOG_ABOUT);
	}
}
