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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.transfers.ProjectUploadTask;
import at.tugraz.ist.catroid.utils.Utils;

public class UploadProjectDialog extends Dialog implements OnClickListener {

	private final Context context;
	private String currentProjectName;
	private EditText projectUploadName;
	private EditText projectDescriptionField;
	private TextView projectRename;
	private Button uploadButton;
	private String newProjectName;

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

		currentProjectName = ProjectManager.getInstance().getCurrentProject().getName();
		projectRename = (TextView) findViewById(R.id.tv_project_rename);
		projectDescriptionField = (EditText) findViewById(R.id.project_description_upload);
		projectUploadName = (EditText) findViewById(R.id.project_upload_name);
		uploadButton = (Button) findViewById(R.id.upload_button);
		uploadButton.setOnClickListener(this);

		projectUploadName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		projectDescriptionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		projectUploadName.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				currentProjectName = ProjectManager.getInstance().getCurrentProject().getName();
				if (!projectUploadName.getText().toString().equals(currentProjectName)) {
					projectRename.setVisibility(View.VISIBLE);
					newProjectName = projectUploadName.getText().toString();
				} else {
					projectRename.setVisibility(View.GONE);
				}
				if (s.length() == 0) {
					Toast.makeText(context, R.string.notification_invalid_text_entered, Toast.LENGTH_SHORT).show();
					uploadButton.setEnabled(false);
				} else {
					uploadButton.setEnabled(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

		this.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(projectUploadName, InputMethodManager.SHOW_IMPLICIT);
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
					boolean renamed = projectManager.renameProject(newProjectName, context);
					if (!renamed) {
						break;
					}
				}

				projectManager.getCurrentProject().setDeviceData(context);
				projectManager.saveProject();

				dismiss();
				String projectPath = Consts.DEFAULT_ROOT + "/" + projectManager.getCurrentProject().getName();
				String projectDescription;

				if (projectDescriptionField.length() != 0) {
					projectDescription = projectDescriptionField.getText().toString();
				} else {
					projectDescription = "";
				}

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				String token = prefs.getString(Consts.TOKEN, "0");
				new ProjectUploadTask(context, uploadName, projectDescription, projectPath, token).execute();
				break;

			case R.id.cancel_button:
				dismiss();
				break;
		}
	}
}
