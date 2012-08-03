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

import java.io.File;
import java.net.URLDecoder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.transfers.CheckTokenTask;
import at.tugraz.ist.catroid.transfers.ProjectDownloadTask;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoadProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoginRegisterDialog;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.UploadProjectDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;

public class MainMenuActivity extends Activity {
	private static final String PROJECTNAME_TAG = "fname=";
	private ActivityHelper activityHelper;
	private TextView titleText;
	public static final int DIALOG_NEW_PROJECT = 0;
	private static final int DIALOG_LOAD_PROJECT = 1;
	public static final int DIALOG_UPLOAD_PROJECT = 2;
	private static final int DIALOG_ABOUT = 3;
	private static final int DIALOG_LOGIN_REGISTER = 4;
	private boolean ignoreResume = false;

	public void updateProjectName() {
		onPause();
		onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityHelper = new ActivityHelper(this);
		Utils.updateScreenWidthAndHeight(this);

		setContentView(R.layout.activity_main_menu);

		Utils.loadProjectIfNeeded(this);

		if (ProjectManager.INSTANCE.getCurrentProject() == null) {
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
			projectName = URLDecoder.decode(projectName);
			new ProjectDownloadTask(this, url, projectName).execute();

		} else if (loadExternalProjectUri.getScheme().equals("file")) {

			String path = loadExternalProjectUri.getPath();
			int a = path.lastIndexOf('/') + 1;
			int b = path.lastIndexOf('.');
			String projectName = path.substring(a, b);
			if (!UtilZip.unZipFile(path, Utils.buildProjectPath(projectName))) {
				Utils.displayErrorMessage(this, getResources().getString(R.string.error_load_project));
			} else {
				if (ProjectManager.INSTANCE.loadProject(projectName, this, true)) {
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

		Project currentProject = ProjectManager.INSTANCE.getCurrentProject();

		if (currentProject != null) {

			activityHelper.setupActionBar(true,
					getResources().getString(R.string.project_name) + " " + currentProject.getName());

			activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, R.string.start,
					new View.OnClickListener() {
						public void onClick(View v) {
							if (ProjectManager.INSTANCE.getCurrentProject() != null) {
								Intent intent = new Intent(MainMenuActivity.this, PreStageActivity.class);
								ignoreResume = true;
								startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
							}
						}
					}, false);
			this.titleText = (TextView) findViewById(R.id.tv_title);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(MainMenuActivity.this, StageActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		if (ProjectManager.INSTANCE.getCurrentProject() != null
				&& StorageHandler.getInstance().projectExistsCheckCase(
						ProjectManager.INSTANCE.getCurrentProject().getName())) {
			ProjectManager.INSTANCE.saveProject();
		}

		switch (id) {
			case DIALOG_NEW_PROJECT:
				dialog = new NewProjectDialog(this).dialog;
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
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
			case DIALOG_UPLOAD_PROJECT:
				Project currentProject = ProjectManager.INSTANCE.getCurrentProject();
				String currentProjectName = currentProject.getName();
				TextView projectRename = (TextView) dialog.findViewById(R.id.tv_project_rename);
				EditText projectDescriptionField = (EditText) dialog.findViewById(R.id.project_description_upload);
				EditText projectUploadName = (EditText) dialog.findViewById(R.id.project_upload_name);
				TextView sizeOfProject = (TextView) dialog.findViewById(R.id.dialog_upload_size_of_project);
				sizeOfProject.setText(UtilFile.getSizeAsString(new File(Constants.DEFAULT_ROOT + "/"
						+ currentProjectName)));

				projectRename.setVisibility(View.GONE);
				projectUploadName.setText(ProjectManager.INSTANCE.getCurrentProject().getName());
				projectDescriptionField.setText("");
				projectUploadName.requestFocus();
				projectUploadName.selectAll();
				break;
			case DIALOG_LOGIN_REGISTER:
				EditText usernameEditText = (EditText) dialog.findViewById(R.id.username);
				EditText passwordEditText = (EditText) dialog.findViewById(R.id.password);
				usernameEditText.setText("");
				passwordEditText.setText("");
				break;
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
				+ ProjectManager.INSTANCE.getCurrentProject().getName();
		titleText.setText(title);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (ProjectManager.INSTANCE.getCurrentProject() == null) {
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
		showDialog(DIALOG_NEW_PROJECT);
	}

	public void handleLoadProjectButton(View v) {
		Intent intent = new Intent(MainMenuActivity.this, MyProjectsActivity.class);
		startActivity(intent);
	}

	public void handleUploadProjectButton(View v) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String token = preferences.getString(Constants.TOKEN, null);

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
		Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	public void handleForumButton(View v) {
		Intent browerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.catrobat_forum).toString()));
		startActivity(browerIntent);
	}

	public void handleAboutCatroidButton(View v) {
		showDialog(DIALOG_ABOUT);
	}
}
