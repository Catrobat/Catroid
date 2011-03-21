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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;
import at.tugraz.ist.catroid.ui.dialogs.LoadProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;

public class MainMenuActivity extends Activity {
    private static final int NEW_PROJECT_DIALOG = 0;
    private static final int LOAD_PROJECT_DIALOG = 1;
    private static final int ABOUT_DIALOG = 2;
    private static final String PREFS_NAME = "at.tugraz.ist.catroid";
    private static final String PREF_PREFIX_KEY = "prefix_";
    private ProjectManager projectManager;

    private void initListeners() {

        Button resumeButton = (Button) findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (projectManager.getCurrentProject() != null) {
                    Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button toStageButton = (Button) findViewById(R.id.toStageButton);
        toStageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (projectManager.getCurrentProject() != null) {
                }
            }
        });

        Button newProjectButton = (Button) findViewById(R.id.newProjectButton);
        newProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(NEW_PROJECT_DIALOG);
            }
        });

        Button loadProjectButton = (Button) findViewById(R.id.loadProjectButton);
        loadProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(LOAD_PROJECT_DIALOG);               
            }
        });

        Button uploadProjectButton = (Button) findViewById(R.id.uploadProjectButton);
        uploadProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            
            }
        });

        Button aboutCatroidButton = (Button) findViewById(R.id.aboutCatroidButton);
        aboutCatroidButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showDialog(ABOUT_DIALOG);
            }
        });
    }

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.main_menu);
    	projectManager = ProjectManager.getInstance();

		if (projectManager.getCurrentProject() != null) {
			initListeners();
			return;
		}

    	// Try to load sharedPreferences
    	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    	String projectName = prefs.getString(PREF_PREFIX_KEY, null);

    	if (projectName != null) {
    		projectManager.loadProject(projectName, this);
    	} else {
    		projectManager.loadProject(this.getString(R.string.default_project_name), this); //default project is created
    	}

    	if (projectManager.getCurrentProject() == null) {
    		Button resumeButton = (Button) findViewById(R.id.resumeButton);
    		resumeButton.setEnabled(false);
    		Button toStageButton = (Button) findViewById(R.id.toStageButton);
    		toStageButton.setEnabled(false);

    		TextView currentProjectTextView = (TextView) findViewById(R.id.currentProjectNameTextView);
    		currentProjectTextView.setText(getString(R.string.current_project) + " " + getString(R.string.no_project));
    	}
    	initListeners();
    	}

    
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        projectManager.saveProject(this);

        switch (id) {
        case NEW_PROJECT_DIALOG:
            dialog = new NewProjectDialog(this);
            break;
        case LOAD_PROJECT_DIALOG:
            dialog = new LoadProjectDialog(this);
            break;
        case ABOUT_DIALOG:
            dialog = new AboutDialog(this);
            break;
        default:
            dialog = null;
            break;
        }

        return dialog;
    }


    @Override
    protected void onResume() {
    	super.onResume();
    	if (projectManager.getCurrentProject() == null) {
    		return;
    	}
    	projectManager.saveProject(this); //TODO: this here good?
    	TextView currentProjectTextView = (TextView) findViewById(R.id.currentProjectNameTextView);
    	currentProjectTextView.setText(getString(R.string.current_project) + " "
    			+ projectManager.getCurrentProject().getName());
    	}

    @Override
    public void onPause() {
        super.onPause();
    	//onPause is sufficient --> gets called before "process_killed", onStop(), onDestroy(), onRestart()
    	//also when you switch activities
    	if (projectManager.getCurrentProject() != null) {
    		projectManager.saveProject(this);
    	}
    	SharedPreferences.Editor prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
    	prefs.putString(PREF_PREFIX_KEY, projectManager.getCurrentProject().getName());
    }

}
