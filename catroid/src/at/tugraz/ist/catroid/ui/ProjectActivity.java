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
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.adapter.SpriteAdapter;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSpriteDialog;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectActivity extends ListActivity {

	private SpriteAdapter adapter;
	private ArrayList<Sprite> adapterSpriteList;
	private Sprite spriteToEdit;

	private void initListeners() {
		adapterSpriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		adapter = new SpriteAdapter(this, R.layout.list, R.id.title, adapterSpriteList,
				(LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE));

		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);

		registerForContextMenu(getListView());
		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProjectManager.getInstance().setCurrentSprite(adapter.getItem(position));
				Intent intent = new Intent(ProjectActivity.this, ScriptActivity.class);
				ProjectActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		ActivityHelper helper = new ActivityHelper(this);
		helper.setupActionBar(false, this.getResources().getString(R.string.sprite_list));

		helper.addActionButton(R.drawable.plus_black, new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(Consts.DIALOG_NEW_SPRITE);
			}
		}, false);

		helper.addActionButton(R.drawable.play_black, new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ProjectActivity.this, StageActivity.class);
				startActivity(intent);
			}
		}, false);
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
	protected void onStart() {
		super.onStart();
		initListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		removeDialog(Consts.DIALOG_RENAME_SPRITE);
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
