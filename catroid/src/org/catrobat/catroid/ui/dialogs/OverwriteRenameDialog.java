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
package org.catrobat.catroid.ui.dialogs;

import java.util.UUID;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class OverwriteRenameDialog extends Dialog implements OnClickListener {
	protected Button okButton, cancelButton;
	protected RadioButton replaceButton, renameButton;
	protected String projectName, zipFileString;
	protected Context context;
	protected EditText projectText;
	protected ErrorListenerInterface errorListenerInterface;

	public OverwriteRenameDialog(Context context, String projectName, String zipFileString,
			ErrorListenerInterface errorListenerInterface) {
		super(context);
		this.projectName = projectName;
		this.zipFileString = zipFileString;
		this.context = context;
		this.errorListenerInterface = errorListenerInterface;
	}

	public void setActivity(MainMenuActivity activity) {
		this.context = activity;
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
					ProjectManager.INSTANCE.loadProject(projectName, context, errorListenerInterface, false);
				} else if (renameButton.isChecked()) {
					String newProjectName = projectName + UUID.randomUUID();
					ProjectManager.INSTANCE.loadProject(projectName, context, errorListenerInterface, false);
					ProjectManager.INSTANCE.renameProject(newProjectName, context, errorListenerInterface);
					UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
					ProjectManager.INSTANCE.loadProject(projectName, context, errorListenerInterface, false);
					boolean error = !ProjectManager.INSTANCE.renameProject(projectText.getText().toString(), context,
							errorListenerInterface);
					if (error) {
						ProjectManager.INSTANCE.deleteCurrentProject();
					}
					ProjectManager.INSTANCE.loadProject(newProjectName, context, errorListenerInterface, false);
					ProjectManager.INSTANCE.renameProject(projectName, context, errorListenerInterface);
					if (error) {
						break;
					}
					ProjectManager.INSTANCE.loadProject(projectText.getText().toString(), context,
							errorListenerInterface, false);
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
