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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.ui.dialogs.NewScriptDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameScriptDialog;

public class SpriteActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<Script> adapter;
    private ArrayList<Script> adapterScriptList;
    private Script scriptToEdit;

    private void initListeners() {

        adapterScriptList = (ArrayList<Script>) ProjectManager.getInstance().getCurrentSprite().getScriptList();
        adapter = new ArrayAdapter<Script>(this, android.R.layout.simple_list_item_1, adapterScriptList);

        listView = (ListView) findViewById(R.id.scriptListView);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ProjectManager.getInstance().setCurrentScript(adapter.getItem(position))) {
                    Intent intent = new Intent(SpriteActivity.this, ScriptActivity.class);
                    SpriteActivity.this.startActivity(intent);
                }
                // TODO: error if selected sprite is not in the project
            }
        });

        Button mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SpriteActivity.this, MainMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Button NewSpriteButton = (Button) findViewById(R.id.addScriptButton);
        NewSpriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(Consts.NEW_SCRIPT_DIALOG);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sprite_activity);
        initListeners();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        // Save Content here?

        switch (id) {
        case Consts.NEW_SCRIPT_DIALOG:
            dialog = new NewScriptDialog(this);
            break;
        case Consts.RENAME_SCRIPT_DIALOG:
            dialog = new RenameScriptDialog(this);
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
        updateTextAndAdapter();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateTextAndAdapter();
        }
    }

    private void updateTextAndAdapter() {
        TextView currentProjectTextView = (TextView) findViewById(R.id.spriteNameTextView);
        currentProjectTextView.setText(this.getString(R.string.sprite_name) + " "
                + ProjectManager.getInstance().getCurrentSprite().getName());
        adapterScriptList = (ArrayList<Script>) ProjectManager.getInstance().getCurrentSprite().getScriptList();
        adapter.notifyDataSetChanged();
    }

    public Script getScriptToEdit() {
        return scriptToEdit;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        if (view.getId() == R.id.scriptListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(adapterScriptList.get(info.position).toString());
            scriptToEdit = adapterScriptList.get(info.position);
            String[] menuItems = getResources().getStringArray(R.array.menu_project_activity);

            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
        case 0: // rename
            this.showDialog(Consts.RENAME_SCRIPT_DIALOG);
            break;
        case 1: // delete
            ProjectManager projectManager = ProjectManager.getInstance();
            projectManager.getCurrentSprite().getScriptList().remove(scriptToEdit);
            if (projectManager.getCurrentScript() != null && projectManager.getCurrentScript().equals(scriptToEdit)) {
                projectManager.setCurrentScript(null);
            }
            break;
        }
        return true;
    }
    
    @Override
    public void onPause() { 
        super.onPause();
        System.out.println("ONPAUSE SpriteActivity");
        ProjectManager projectManager = ProjectManager.getInstance();
        if (projectManager.getCurrentProject() != null) {
            projectManager.saveProject(this);
        }
    }
}
