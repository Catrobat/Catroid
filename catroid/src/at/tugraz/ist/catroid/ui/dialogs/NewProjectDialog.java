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

import java.io.IOException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewProjectDialog extends TextDialog {

	public NewProjectDialog(Activity activity) {
		super(activity, activity.getString(R.string.new_project_dialog_title), null);
		initKeyAndClickListener();
	}

	public void handleOkButton() {
		String projectName = (input.getText().toString().trim());

		if (projectName.length() == 0) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.error_no_name_entered));
			return;
		}

		if (StorageHandler.getInstance().projectExists(projectName)) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.error_project_exists));
			return;
		}

		try {
			ProjectManager.getInstance().initializeNewProject(projectName, activity);
		} catch (IOException e) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.error_new_project));
			activity.dismissDialog(MainMenuActivity.DIALOG_NEW_PROJECT);
		}

		Utils.saveToPreferences(activity, Consts.PREF_PROJECTNAME_KEY, projectName);
		Intent intent = new Intent(activity, ProjectActivity.class);
		activity.startActivity(intent);
		activity.dismissDialog(MainMenuActivity.DIALOG_NEW_PROJECT);
	}

	private void initKeyAndClickListener() {
		dialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					handleOkButton();
					return true;
				}
				return false;
			}
		});

		buttonPositive.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleOkButton();
			}
		});

		buttonNegative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				activity.dismissDialog(MainMenuActivity.DIALOG_NEW_PROJECT);
			}
		});
	}
}
