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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.ui.dialogs.LoadProjectDialog;
import at.tugraz.ist.catroid.ui.dialogs.NewProjectDialog;

public class MainMenuActivity extends Activity {
    private static final int NEW_PROJECT_DIALOG = 0;
    private static final int LOAD_PROJECT_DIALOG = 1;
    private Project currentProject;
    private ContentManager contentManager;

    private void initListeners() {
        //final Context context = this;

        Button resumeButton = (Button) findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentProject != null) {
                    Intent intent = new Intent(MainMenuActivity.this, ProjectActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button toStageButton = (Button) findViewById(R.id.toStageButton);
        toStageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentProject != null) {
                    // TODO: Start new stage activity with current project
                }
            }
        });

        Button newProjectButton = (Button) findViewById(R.id.newProjectButton);
        newProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(NEW_PROJECT_DIALOG);
                // TODO: Start new project activity
                // TODO: set currentProject here or via Observer?
            }
        });

        Button loadProjectButton = (Button) findViewById(R.id.loadProjectButton);
        loadProjectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(LOAD_PROJECT_DIALOG);               
                // TODO: Start new project activity
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);

        // Try to load project
        contentManager = new ContentManager(this, null);
        currentProject = contentManager.getCurrentProject();
        
        
        if (currentProject == null) {
            contentManager = new ContentManager(this, null); //creates default project (could be new currentProject?)
            Button resumeButton = (Button) findViewById(R.id.resumeButton);
            resumeButton.setEnabled(false);
            Button toStageButton = (Button) findViewById(R.id.toStageButton);
            toStageButton.setEnabled(false);

            TextView currentProjectTextView = (TextView) findViewById(R.id.currentProjectNameTextView);
            currentProjectTextView.setText(getString(R.string.current_project) + " " + getString(R.string.no_project));
        }
        //contentManager = new ContentManager(this, currentProject.getName());
        initListeners();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;

        switch (id) {
        case NEW_PROJECT_DIALOG:
            dialog = new NewProjectDialog(this, contentManager);
            break;
        case LOAD_PROJECT_DIALOG:
            dialog = new LoadProjectDialog(this, contentManager);
            break;
        default:
            dialog = null;
            break;
        }

        return dialog;
    }
    
    protected void onResume() {
    	super.onResume();
    	if (contentManager.getCurrentProject() != null)
    		currentProject = contentManager.getCurrentProject();
    	else
    		return;
    	TextView currentProjectTextView = (TextView) findViewById(R.id.currentProjectNameTextView);
        currentProjectTextView.setText(getString(R.string.current_project) + " " + currentProject.getName());
    }
    
    

}
