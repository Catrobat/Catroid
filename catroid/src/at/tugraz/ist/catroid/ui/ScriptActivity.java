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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;

/**
 * @author DENISE
 *
 */
public class ScriptActivity extends Activity implements OnItemClickListener {
    private static final int ADD_BRICK_DIALOG = 0;
    //private static final int LOAD_PROJECT_DIALOG = 1;
    private static final String PREFS_NAME = "at.tugraz.ist.catroid";
    private static final String PREF_PREFIX_KEY = "prefix_";
    private ContentManager contentManager;

    private void initListeners() {

        Button mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(ScriptActivity.this, MainMenuActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Button addBrickButton = (Button) findViewById(R.id.addBrickButton);
        addBrickButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 showDialog(ADD_BRICK_DIALOG); 
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.script_activity);
        
        // Try to load sharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String prefix = prefs.getString(PREF_PREFIX_KEY, null);
        if (prefix != null) {
            contentManager = new ContentManager(this, prefix);
        } else {
            contentManager = new ContentManager(this, null); //null: creates default project
        }
        

            ListView currentBrickListView = (ListView) findViewById(R.id.brickListView);
            //currentBrickListView.set(getString(R.string.current_project) + " " + getString(R.string.no_project));
        
        initListeners();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        contentManager.saveContent();

        switch (id) {
        case ADD_BRICK_DIALOG:
            dialog = new AddBrickDialog(this, contentManager);
            break;
        //case LOAD_PROJECT_DIALOG:
            //dialog = new LoadProjectDialog(this, contentManager);
            //break;
        default:
            dialog = null;
            break;
        }

        return dialog;
    }

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
    
    
}
