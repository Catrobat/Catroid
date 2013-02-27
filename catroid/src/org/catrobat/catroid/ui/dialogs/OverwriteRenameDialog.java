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
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class OverwriteRenameDialog extends DialogFragment implements OnClickListener {
	protected RadioButton replaceButton, renameButton;
	protected String projectName, zipFileString;
	protected Context context;
	protected EditText projectText;

	public static final String DIALOG_FRAGMENT_TAG = "overwrite_rename_look";

	public OverwriteRenameDialog(Context context, String projectName, String zipFileString) {
		super();
		this.projectName = projectName;
		this.zipFileString = zipFileString;
		this.context = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_overwrite_project, null);

		replaceButton = (RadioButton) dialogView.findViewById(R.id.dialog_overwrite_project_radio_replace);
		replaceButton.setOnClickListener(this);
		renameButton = (RadioButton) dialogView.findViewById(R.id.dialog_overwrite_project_radio_rename);
		renameButton.setOnClickListener(this);
		projectText = (EditText) dialogView.findViewById(R.id.dialog_overwrite_project_edit);
		projectText.setText(projectName);

		Dialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView).setTitle(R.string.overwrite_text)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButton();
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(context, R.string.notification_load_project_cancel, Toast.LENGTH_SHORT).show();
					}
				}).create();

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					boolean okButtonResult = handleOkButton();
					if (okButtonResult) {
						dismiss();
					}
					return okButtonResult;
				}

				return false;
			}
		});

		return dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_overwrite_project_radio_replace:
				projectText.setVisibility(EditText.GONE);
				break;

			case R.id.dialog_overwrite_project_radio_rename:
				projectText.setVisibility(EditText.VISIBLE);
				break;

			default:
				break;
		}
	}

	private boolean handleOkButton() {
		if (replaceButton.isChecked()) {
			UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
			ProjectManager.INSTANCE.loadProject(projectName, context, false);
		} else if (renameButton.isChecked()) {
			String newProjectName = projectName + UUID.randomUUID();
			ProjectManager.INSTANCE.loadProject(projectName, context, false);
			ProjectManager.INSTANCE.renameProject(newProjectName, context);
			UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
			ProjectManager.INSTANCE.loadProject(projectName, context, false);
			boolean error = !ProjectManager.INSTANCE.renameProject(projectText.getText().toString(), context);

			if (error) {
				ProjectManager.INSTANCE.deleteCurrentProject();
			}
			ProjectManager.INSTANCE.loadProject(newProjectName, context, false);
			ProjectManager.INSTANCE.renameProject(projectName, context);
			if (error) {
				return false;
			}
			ProjectManager.INSTANCE.loadProject(projectText.getText().toString(), context, false);
		}
		Toast.makeText(context, R.string.success_project_download, Toast.LENGTH_SHORT).show();
		dismiss();

		StatusBarNotificationManager.INSTANCE.downloadProjectName.remove(projectName);
		StatusBarNotificationManager.INSTANCE.downloadProjectZipFileString.remove(zipFileString);

		return true;
	}

	/*
	 * @Override
	 * protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setTitle(R.string.overwrite_text);
	 * setContentView(R.layout.dialog_overwrite_project);
	 * setCanceledOnTouchOutside(true);
	 * replaceButton = (RadioButton) findViewById(R.id.dialog_overwrite_project_radio_replace);
	 * replaceButton.setOnClickListener(this);
	 * renameButton = (RadioButton) findViewById(R.id.dialog_overwrite_project_radio_rename);
	 * renameButton.setOnClickListener(this);
	 * okButton = (Button) findViewById(R.id.dialog_overwrite_project_button_ok);
	 * okButton.setOnClickListener(this);
	 * cancelButton = (Button) findViewById(R.id.dialog_overwrite_project_button_cancel);
	 * cancelButton.setOnClickListener(this);
	 * projectText = (EditText) findViewById(R.id.dialog_overwrite_project_edit);
	 * projectText.setText(projectName);
	 * }
	 */

	//	@Override
	//	public void onClick(View v) {
	//		switch (v.getId()) {
	//			case R.id.dialog_overwrite_project_button_ok:
	//				if (replaceButton.isChecked()) {
	//					UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
	//					ProjectManager.INSTANCE.loadProject(projectName, context, false);
	//				} else if (renameButton.isChecked()) {
	//					String newProjectName = projectName + UUID.randomUUID();
	//					ProjectManager.INSTANCE.loadProject(projectName, context, false);
	//					ProjectManager.INSTANCE.renameProject(newProjectName, context);
	//					UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
	//					ProjectManager.INSTANCE.loadProject(projectName, context, false);
	//					boolean error = !ProjectManager.INSTANCE.renameProject(projectText.getText().toString(), context);
	//
	//					if (error) {
	//						ProjectManager.INSTANCE.deleteCurrentProject();
	//					}
	//					ProjectManager.INSTANCE.loadProject(newProjectName, context, false);
	//					ProjectManager.INSTANCE.renameProject(projectName, context);
	//					if (error) {
	//						break;
	//					}
	//					ProjectManager.INSTANCE.loadProject(projectText.getText().toString(), context, false);
	//				}
	//				Toast.makeText(context, R.string.success_project_download, Toast.LENGTH_SHORT).show();
	//				dismiss();
	//				break;
	//
	//			case R.id.dialog_overwrite_project_button_cancel:
	//				dismiss();
	//				Toast.makeText(context, R.string.notification_load_project_cancel, Toast.LENGTH_SHORT).show();
	//				break;
	//
	//			case R.id.dialog_overwrite_project_radio_replace:
	//				projectText.setVisibility(EditText.GONE);
	//				break;
	//
	//			case R.id.dialog_overwrite_project_radio_rename:
	//				projectText.setVisibility(EditText.VISIBLE);
	//				break;
	//
	//			default:
	//				break;
	//		}
	//		StatusBarNotificationManager.INSTANCE.downloadProjectName.remove(projectName);
	//		StatusBarNotificationManager.INSTANCE.downloadProjectZipFileString.remove(zipFileString);
	//	}

}
