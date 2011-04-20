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

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewProjectDialog extends Dialog {
	private final Context context;

	public NewProjectDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_new_project);
		setTitle(R.string.new_project_dialog_title);
		setCanceledOnTouchOutside(true);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		this.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(findViewById(R.id.new_project_edit_text), InputMethodManager.SHOW_IMPLICIT);
			}
		});

		Button createNewProjectButton = (Button) findViewById(R.id.new_project_dialog_create_button);
		createNewProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String projectName = ((EditText) findViewById(R.id.new_project_edit_text)).getText().toString().trim();
				if (projectName.length() == 0) {
					Utils.displayErrorMessage(context, context.getString(R.string.error_no_name_entered));
					return;
				}
				try {
					if (StorageHandler.getInstance().projectExists(projectName)) {
						Utils.displayErrorMessage(context, context.getString(R.string.error_project_exists));
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				ProjectManager.getInstance().initializeNewProject(projectName, context);
				Intent intent = new Intent(context, ProjectActivity.class);
				context.startActivity(intent);
				dismiss();
				((EditText) findViewById(R.id.new_project_edit_text)).setText(null);
			}
		});

		this.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
						case KeyEvent.KEYCODE_ENTER: {
							String projectName = ((EditText) findViewById(R.id.new_project_edit_text)).getText()
									.toString();

							if (projectName.length() == 0) {
								Utils.displayErrorMessage(context, context.getString(R.string.error_no_name_entered));
								return true;
							}

							try {
								if (StorageHandler.getInstance().projectExists(projectName)) {
									Utils.displayErrorMessage(context, context.getString(R.string.error_project_exists));
									return true;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}

							ProjectManager.getInstance().initializeNewProject(projectName, context);
							Intent intent = new Intent(context, ProjectActivity.class);
							context.startActivity(intent);

							dismiss();
							((EditText) findViewById(R.id.new_project_edit_text)).setText(null);

							return true;
						}
						default: {
							break;
						}
					}
				}
				return false;
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancelDialogButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				((EditText) findViewById(R.id.new_project_edit_text)).setText(null);
			}
		});
	}
}