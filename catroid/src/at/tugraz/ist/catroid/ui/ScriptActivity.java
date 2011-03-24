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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrick;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;

public class ScriptActivity extends Activity {
    protected ListView brickListView;
    private ArrayList<Brick> adapterBrickList;
    private BrickAdapter adapter;
    private ListView listView;
    private Sprite sprite;

    private void initListeners() {
        sprite = ProjectManager.getInstance().getCurrentSprite();
        adapterBrickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
        adapter = new BrickAdapter(this, adapterBrickList);

        listView = (ListView) findViewById(R.id.brickListView);
        listView.setAdapter(adapter);
        // registerForContextMenu(listView);
        // listView.setOnItemClickListener(new ListView.OnItemClickListener() {
        //
        // public void onItemClick(AdapterView<?> parent, View view, int
        // position, long id) {
        // if
        // (ProjectManager.getInstance().setCurrentSprite(adapter.getItem(position)))
        // {
        // Intent intent = new Intent(ProjectActivity.this,
        // SpriteActivity.class);
        // ProjectActivity.this.startActivity(intent);
        // / }
        // //TODO: error if selected sprite is not in the project
        // }
        // });

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
                showDialog(Consts.ADD_BRICK_DIALOG);
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
        case Consts.ADD_BRICK_DIALOG:
            dialog = new AddBrickDialog(this, sprite);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
         * This is used for getting the results of the gallery intent when the
         * user selected an image. requestCode holds the ID / position of the
         * brick that issued the request. If and when we have different kinds of
         * intents we need to find a better way.
         */

        if (resultCode == RESULT_OK) {
            SetCostumeBrick affectedBrick = (SetCostumeBrick) adapterBrickList.get(requestCode);
            if (affectedBrick != null) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPathFromContentUri(selectedImageUri);
                try {
                    File outputFile = StorageHandler.getInstance().copyImage(ProjectManager.getInstance().getCurrentProject().getName(),
                            selectedImagePath);
                    if (outputFile != null) {
                        affectedBrick.setCostume(outputFile.getAbsolutePath());
                        adapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public String getPathFromContentUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }
}
