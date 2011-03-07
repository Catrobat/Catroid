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
package at.tugraz.ist.catroid.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;

public class MainMenuActivity extends Activity {
	private static final int NEW_PROJECT_DIALOG = 0;
	private Project currentProject;

	private void initListeners() {
		final Context context = this;

		Button resumeButton = (Button) this.findViewById(R.id.resumeButton);
		resumeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (currentProject != null) {
					// TODO: Start new project activity with current project
				}
			}
		});

		Button toStageButton = (Button) this.findViewById(R.id.toStageButton);
		toStageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (currentProject != null) {
					// TODO: Start new stage activity with current project
				}
			}
		});

		Button newProjectButton = (Button) this
				.findViewById(R.id.newProjectButton);
		newProjectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					showDialog(NEW_PROJECT_DIALOG);
					currentProject = new Project(context, "");
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				// TODO: Start new project activity
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_menu);

		// Try to load project
		currentProject = null;

		if (currentProject == null) {
			Button resumeButton = (Button) this.findViewById(R.id.resumeButton);
			resumeButton.setEnabled(false);
			Button toStageButton = (Button) this
					.findViewById(R.id.toStageButton);
			toStageButton.setEnabled(false);

			TextView currentProjectTextView = (TextView) this
					.findViewById(R.id.currentProjectNameTextView);
			currentProjectTextView.setText(getString(R.string.current_project)
					+ " " + getString(R.string.no_project));
		}
		initListeners();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch (id) {
		case NEW_PROJECT_DIALOG:
			dialog = new NewProjectDialog(this);
			break;
		default:
			dialog = null;
			break;
		}
		
		return dialog;
	}
}
