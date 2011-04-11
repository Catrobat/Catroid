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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.utils.Utils;


public class UploadProjectDialog extends Dialog {
	private final Context context;
	private String currentProjectName;
	private EditText projectUploadName;
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
		projectUploadName = (EditText) findViewById(R.id.project_upload_name);
		currentProjectName = ProjectManager.getInstance().getCurrentProject().getName();
		projectUploadName.setText(currentProjectName);

		Button uploadButton = (Button) findViewById(R.id.upload_button);
		uploadButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				String uploadName = projectUploadName.getText().toString();
				if(uploadName.length() == 0) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_no_name_entered));
				}
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				((EditText) findViewById(R.id.project_upload_name)).setText(currentProjectName);
				((EditText) findViewById(R.id.project_description_upload)).setText(null);
			}
		});

	}
}
