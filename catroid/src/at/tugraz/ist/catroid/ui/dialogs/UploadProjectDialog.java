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
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.transfers.ProjectUploadTask;
import at.tugraz.ist.catroid.utils.Utils;

public class UploadProjectDialog extends Dialog implements OnClickListener {
	private final Context context;
	private String currentProjectName;
	private EditText projectUploadName;
	private EditText projectDescriptionField;
	private TextView projectRename;
	private String newProjectName;
	private boolean renameProject = false;

	public UploadProjectDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_upload_project);
		setTitle(R.string.upload_project_dialog_title);
		setCanceledOnTouchOutside(true);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		projectRename = (TextView) findViewById(R.id.tv_project_rename);
		projectRename.setVisibility(View.GONE);
		projectDescriptionField = (EditText) findViewById(R.id.project_description_upload);
		projectUploadName = (EditText) findViewById(R.id.project_upload_name);
		currentProjectName = ProjectManager.getInstance().getCurrentProject().getName();
		final Button uploadButton = (Button) findViewById(R.id.upload_button);
		uploadButton.setOnClickListener(this);
		projectUploadName.setText(currentProjectName);

		projectUploadName.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!projectUploadName.getText().toString().equals(currentProjectName)) {
					projectRename.setVisibility(View.VISIBLE);
					newProjectName = projectUploadName.getText().toString();
					renameProject = true;
				} else if (projectUploadName.getText().toString().equals(currentProjectName)) {
					projectRename.setVisibility(View.GONE);
					renameProject = false;
				}
				if (s.length() == 0) {
					Toast.makeText(UploadProjectDialog.this.context, R.string.notification_invalid_text_entered,
							Toast.LENGTH_SHORT).show();
					uploadButton.setEnabled(false);
				}
				else {
					uploadButton.setEnabled(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		ProjectManager projectManager = ProjectManager.getInstance();

		switch (v.getId()) {
			case R.id.upload_button:
				String uploadName = projectUploadName.getText().toString();
				if (uploadName.length() == 0) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_no_name_entered));
					return;
				} else if (!uploadName.equals(currentProjectName)) {
					projectRename.setVisibility(View.VISIBLE);
					renameProject = true;
				}

				if (renameProject) {
					boolean renamed = projectManager.renameProject(newProjectName, context);
					if (!renamed) {
						break;
					}
				}
				projectManager.getCurrentProject().setDeviceData();
				projectManager.saveProject(context);

				dismiss();
				String projectPath = Consts.DEFAULT_ROOT + "/" + projectManager.getCurrentProject().getName();
				String projectDescription;

				if (projectDescriptionField.length() != 0) {
					projectDescription = projectDescriptionField.getText().toString();
				} else {
					projectDescription = "";
				}

				new ProjectUploadTask(context, uploadName, projectDescription, projectPath).execute();
				break;

			case R.id.cancel_button:
				dismiss();
				((EditText) findViewById(R.id.project_upload_name)).setText(ProjectManager.getInstance()
						.getCurrentProject().getName());
				((EditText) findViewById(R.id.project_description_upload)).setText(null);
				projectRename.setVisibility(View.GONE);

				break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dismiss();
			((EditText) findViewById(R.id.project_upload_name)).setText(ProjectManager.getInstance()
					.getCurrentProject().getName());
			((EditText) findViewById(R.id.project_description_upload)).setText(null);
			projectRename.setVisibility(View.GONE);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
