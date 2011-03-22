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

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView;

public class ScriptActivity extends Activity {
    private static final int ADD_BRICK_DIALOG = 0;
    protected ListView brickListView;
    private ArrayList<Brick> adapterBrickList;
    private BrickAdapter adapter;
    private DragNDropListView listView;

  
    
        
    private void initListeners() {

        adapterBrickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
        adapter = new BrickAdapter(this, adapterBrickList);
        //adapter.isToolboxAdapter = true;
        
        listView = (DragNDropListView) findViewById(R.id.brickListView);
        listView.setTrashView((ImageView)findViewById(R.id.trash));
        listView.setOnCreateContextMenuListener(this);
        listView.setOnDropListener(adapter);
        listView.setOnRemoveListener(adapter);
        listView.setAdapter(adapter);
        //registerForContextMenu(listView);
        //        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
        //
        //            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                if (ProjectManager.getInstance().setCurrentSprite(adapter.getItem(position))) {
        //                    Intent intent = new Intent(ProjectActivity.this, SpriteActivity.class);
        //                    ProjectActivity.this.startActivity(intent);
		///                }
        //                //TODO: error if selected sprite is not in the project
        //            }
        //        });

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
        initListeners();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;

        switch (id) {
        case ADD_BRICK_DIALOG:
            dialog = new AddBrickDialog(this);
            break;
        default:
            dialog = null;
            break;
        }
        return dialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateTextAndAdapter();
        }
    }

    private void updateTextAndAdapter() {
        TextView currentProjectTextView = (TextView) findViewById(R.id.scriptNameTextView);
		currentProjectTextView.setText(this.getString(R.string.script_name) + " "
                + ProjectManager.getInstance().getCurrentScript().getName());
        adapterBrickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
        adapter.notifyDataSetChanged();
    }
}
