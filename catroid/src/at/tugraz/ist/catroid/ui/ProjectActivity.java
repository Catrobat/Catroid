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
import android.view.MenuInflater;
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
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSpriteDialog;

public class ProjectActivity extends Activity {

	private ListView listView;
	private ArrayAdapter<Sprite> adapter;
	private ArrayList<Sprite> adapterSpriteList;
	private Sprite spriteToEdit;

	private void initListeners() {
		adapterSpriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		adapter = new ArrayAdapter<Sprite>(this, android.R.layout.simple_list_item_1, adapterSpriteList);

		listView = (ListView) findViewById(R.id.spriteListView);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProjectManager.getInstance().setCurrentSprite(adapter.getItem(position));
				Intent intent = new Intent(ProjectActivity.this, ScriptActivity.class);
				ProjectActivity.this.startActivity(intent);
			}
		});

		Button mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
		mainMenuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		Button NewSpriteButton = (Button) findViewById(R.id.addSpriteButton);
		NewSpriteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(Consts.DIALOG_NEW_SPRITE);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_project);
		initListeners();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch (id) {
			case Consts.DIALOG_NEW_SPRITE:
				dialog = new NewSpriteDialog(this);
				break;
			case Consts.DIALOG_RENAME_SPRITE:
				dialog = new RenameSpriteDialog(this);
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

	public Sprite getSpriteToEdit() {
		return spriteToEdit;
	}

	private void updateTextAndAdapter() {
		TextView currentProjectTextView = (TextView) findViewById(R.id.projectTitleTextView);
		currentProjectTextView.setText(this.getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		spriteToEdit = adapterSpriteList.get(info.position);

		if (spriteToEdit.getName().equalsIgnoreCase(getString(R.string.stage))) {
			return;
		}

		menu.setHeaderTitle(adapterSpriteList.get(info.position).getName());

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.project_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.project_menu_rename:
				this.showDialog(Consts.DIALOG_RENAME_SPRITE);
				return true;
			case R.id.project_menu_delete:
				ProjectManager projectManager = ProjectManager.getInstance();
				projectManager.getCurrentProject().getSpriteList().remove(spriteToEdit);
				if (projectManager.getCurrentSprite() != null && projectManager.getCurrentSprite().equals(spriteToEdit)) {
					projectManager.setCurrentSprite(null);
				}
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject(this);
		}
	}
}
