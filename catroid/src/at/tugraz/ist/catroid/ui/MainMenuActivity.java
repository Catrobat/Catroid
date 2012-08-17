/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.transfers.CheckTokenTask;
import at.tugraz.ist.catroid.transfers.CheckTokenTask.OnCheckTokenCompleteListener;
import at.tugraz.ist.catroid.transfers.ProjectDownloadTask;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoginRegisterDialog;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.UploadProjectDialog;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainMenuActivity extends SherlockFragmentActivity implements OnCheckTokenCompleteListener {

	private static final String TAG = "MainMenuActivity";
	private static final String PROJECTNAME_TAG = "fname=";

	private ProjectManager projectManager;

	private ActionBar actionBar;

	private boolean ignoreResume = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.updateScreenWidthAndHeight(this);

		setContentView(R.layout.activity_main_menu);

		actionBar = getSupportActionBar();
		actionBar.setDisplayUseLogoEnabled(true);

		projectManager = ProjectManager.getInstance();
		Utils.loadProjectIfNeeded(this);

		if (projectManager.getCurrentProject() == null) {
			findViewById(R.id.current_project_button).setEnabled(false);
		}

		// Load external project from URL or local file system.
		Uri loadExternalProjectUri = getIntent().getData();
		getIntent().setData(null);

		if (loadExternalProjectUri == null) {
			return;
		}
		if (loadExternalProjectUri.getScheme().equals("http")) {
			String url = loadExternalProjectUri.toString();
			int projectNameIndex = url.lastIndexOf(PROJECTNAME_TAG) + PROJECTNAME_TAG.length();
			String projectName = url.substring(projectNameIndex);
			try {
				projectName = URLDecoder.decode(projectName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Could not decode project name: " + projectName, e);
			}

			new ProjectDownloadTask(this, url, projectName).execute();
		} else if (loadExternalProjectUri.getScheme().equals("file")) {

			String path = loadExternalProjectUri.getPath();
			int a = path.lastIndexOf('/') + 1;
			int b = path.lastIndexOf('.');
			String projectName = path.substring(a, b);
			if (!UtilZip.unZipFile(path, Utils.buildProjectPath(projectName))) {
				Utils.displayErrorMessage(this, getResources().getString(R.string.error_load_project));
			} else {
				if (projectManager.loadProject(projectName, this, true)) {
					writeProjectTitleInTextfield();
				}
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		ignoreResume = false;
		PreStageActivity.shutdownPersistentResources();

		if (projectManager.getCurrentProject() != null) {
			String title = getString(R.string.project_name) + " " + projectManager.getCurrentProject().getName();
			actionBar.setTitle(title);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_start: {
				if (projectManager.getCurrentProject() != null) {
					Intent intent = new Intent(MainMenuActivity.this, PreStageActivity.class);
					ignoreResume = true;
					startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
				}
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(MainMenuActivity.this, StageActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		if (ProjectManager.INSTANCE.getCurrentProject() == null) {
			return;
		}
		if (!ignoreResume) {
			PreStageActivity.shutdownPersistentResources();
		}
		ignoreResume = false;

		ProjectManager.INSTANCE.loadProject(ProjectManager.INSTANCE.getCurrentProject().getName(), this, false);
		writeProjectTitleInTextfield();
	}

	public void writeProjectTitleInTextfield() {
		String title = this.getResources().getString(R.string.project_name) + " "
				+ projectManager.getCurrentProject().getName();
		actionBar.setTitle(title);
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
		if (ProjectManager.INSTANCE.getCurrentProject() != null) {
			ProjectManager.INSTANCE.saveProject();
			Utils.saveToPreferences(this, Constants.PREF_PROJECTNAME_KEY, ProjectManager.INSTANCE.getCurrentProject()
					.getName());
		}
	}

	public void handleCurrentProjectButton(View v) {
		if (ProjectManager.INSTANCE.getCurrentProject() != null) {
			Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
			startActivity(intent);
		}
	}

	public void handleNewProjectButton(View v) {
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.show(getSupportFragmentManager(), NewProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	public void handleLoadProjectButton(View v) {
		Intent intent = new Intent(MainMenuActivity.this, MyProjectsActivity.class);
		startActivity(intent);
	}

	public void handleUploadProjectButton(View v) {
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

	public void handleWebResourcesButton(View v) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.catroid_website).toString()));
		startActivity(browserIntent);
	}

	public void handleSettingsButton(View v) {
		Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	public void handleForumButton(View v) {
		Intent browerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.catrobat_forum).toString()));
		startActivity(browerIntent);
	}

	public void handleAboutCatroidButton(View v) {
		AboutDialog aboutDialog = new AboutDialog(this);
		aboutDialog.show();
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

	private void showLoginRegisterDialog() {
		LoginRegisterDialog loginRegisterDialog = new LoginRegisterDialog();
		loginRegisterDialog.show(getSupportFragmentManager(), LoginRegisterDialog.DIALOG_FRAGMENT_TAG);
	}
}
