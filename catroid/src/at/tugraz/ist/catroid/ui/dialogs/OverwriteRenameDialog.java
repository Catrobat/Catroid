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
package at.tugraz.ist.catroid.ui.dialogs;

import java.util.UUID;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.ErrorListenerInterface;
import at.tugraz.ist.catroid.utils.StatusBarNotificationManager;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;

public class OverwriteRenameDialog extends Dialog implements OnClickListener {
	protected Button okButton, cancelButton;
	protected RadioButton replaceButton, renameButton;
	protected String projectName, zipFileString;
	protected Context context;
	protected EditText projectText;
	protected ErrorListenerInterface errorListenerInterface;
	protected MainMenuActivity activity;

	//public OverwriteRenameDialog(MainMenuActivity activity, String projectName, String zipFileString) {
	public OverwriteRenameDialog(Context context, String projectName, String zipFileString,
			ErrorListenerInterface errorListenerInterface, MainMenuActivity activity) {
		super(context);
		this.projectName = projectName;
		this.zipFileString = zipFileString;
		this.context = context;
		this.errorListenerInterface = errorListenerInterface;
		this.activity = activity;
	}

	public void setActivity(MainMenuActivity activity) {
		this.context = activity;
		this.activity = activity;
		this.errorListenerInterface = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.overwrite_text);
		setContentView(R.layout.dialog_overwrite_project);
		setCanceledOnTouchOutside(true);
		replaceButton = (RadioButton) findViewById(R.id.dialog_overwrite_project_radio_replace);
		replaceButton.setOnClickListener(this);
		renameButton = (RadioButton) findViewById(R.id.dialog_overwrite_project_radio_rename);
		renameButton.setOnClickListener(this);
		okButton = (Button) findViewById(R.id.dialog_overwrite_project_button_ok);
		okButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.dialog_overwrite_project_button_cancel);
		cancelButton.setOnClickListener(this);
		projectText = (EditText) findViewById(R.id.dialog_overwrite_project_edit);
		projectText.setText(projectName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_overwrite_project_button_ok:
				if (replaceButton.isChecked()) {
					UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
					ProjectManager.getInstance().loadProject(projectName, context, errorListenerInterface, true);
					activity.writeProjectTitleInTextfield();
				} else if (renameButton.isChecked()) {
					String newProjectName = projectName + UUID.randomUUID();
					ProjectManager.getInstance().loadProject(projectName, context, errorListenerInterface, true);
					ProjectManager.getInstance().renameProject(newProjectName, context, errorListenerInterface);
					UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
					ProjectManager.getInstance().loadProject(projectName, context, errorListenerInterface, true);
					boolean error = !ProjectManager.getInstance().renameProject(projectText.getText().toString(),
							context, errorListenerInterface);
					if (error) {
						ProjectManager.getInstance().deleteCurrentProject();
					}
					ProjectManager.getInstance().loadProject(newProjectName, context, errorListenerInterface, true);
					ProjectManager.getInstance().renameProject(projectName, context, errorListenerInterface);
					if (error) {
						break;
					} else {
						ProjectManager.getInstance().loadProject(projectText.getText().toString(), context,
								errorListenerInterface, true);
						activity.writeProjectTitleInTextfield();
					}
				}
				Toast.makeText(context, R.string.success_project_download, Toast.LENGTH_SHORT).show();
				dismiss();
				break;

			case R.id.dialog_overwrite_project_button_cancel:
				dismiss();
				Toast.makeText(context, R.string.notification_load_project_cancel, Toast.LENGTH_SHORT).show();
				break;

			case R.id.dialog_overwrite_project_radio_replace:
				projectText.setVisibility(EditText.GONE);
				break;

			case R.id.dialog_overwrite_project_radio_rename:
				projectText.setVisibility(EditText.VISIBLE);
				break;

			default:
				break;
		}
		StatusBarNotificationManager.INSTANCE.downloadProjectName.remove(projectName);
		StatusBarNotificationManager.INSTANCE.downloadProjectZipFileString.remove(zipFileString);
	}
}
