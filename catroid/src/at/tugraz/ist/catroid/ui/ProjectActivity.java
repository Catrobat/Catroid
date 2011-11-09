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

import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.adapter.CustomIconContextMenu;
import at.tugraz.ist.catroid.ui.adapter.SpriteAdapter;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSpriteDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectActivity extends ListActivity {

	private SpriteAdapter spriteAdapter;
	private ArrayList<Sprite> spriteList;
	private Sprite spriteToEdit;
	private ActivityHelper activityHelper = new ActivityHelper(this);
	private CustomIconContextMenu iconContextMenu;
	private static final int CONTEXT_MENU_ITEM_RENAME = 0; //or R.id.project_menu_rename
	private static final int CONTEXT_MENU_ITEM_DELETE = 1; //or R.id.project_menu_delete

	private void initListeners() {
		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		spriteAdapter = new SpriteAdapter(this, R.layout.activity_project_spritelist_item, R.id.sprite_title,
				spriteList);

		setListAdapter(spriteAdapter);
		getListView().setTextFilterEnabled(true);

		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProjectManager.getInstance().setCurrentSprite(spriteAdapter.getItem(position));
				Intent intent = new Intent(ProjectActivity.this, ScriptActivity.class);
				ProjectActivity.this.startActivity(intent);
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				spriteToEdit = spriteList.get(position);
				if (spriteToEdit.getName().equalsIgnoreCase( //better make a independent object for stage (to solve problem when switching languages)
						ProjectActivity.this.getString(R.string.background))) {
					return true;
				}
				showDialog(Consts.DIALOG_CONTEXT_MENU);
				return true;
			}
		});
	}

	private void initCustomContextMenu() {
		Resources resources = getResources();
		iconContextMenu = new CustomIconContextMenu(this, Consts.DIALOG_CONTEXT_MENU);
		iconContextMenu.addItem(resources, this.getString(R.string.rename), R.drawable.ic_context_rename,
				CONTEXT_MENU_ITEM_RENAME);
		iconContextMenu.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			public void onClick(int menuId) {
				switch (menuId) {
					case CONTEXT_MENU_ITEM_RENAME:
						removeDialog(Consts.DIALOG_RENAME_SPRITE);
						ProjectActivity.this.showDialog(Consts.DIALOG_RENAME_SPRITE);
						break;
					case CONTEXT_MENU_ITEM_DELETE:
						ProjectManager projectManager = ProjectManager.getInstance();
						projectManager.getCurrentProject().getSpriteList().remove(spriteToEdit);
						if (projectManager.getCurrentSprite() != null
								&& projectManager.getCurrentSprite().equals(spriteToEdit)) {
							projectManager.setCurrentSprite(null);
						}
						break;
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		String title = this.getResources().getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName();
		activityHelper.setupActionBar(false, title);

		activityHelper.addActionButton(R.id.btn_action_add_sprite, R.drawable.ic_plus_black,
				new View.OnClickListener() {
					public void onClick(View v) {
						showDialog(Consts.DIALOG_NEW_SPRITE);
					}
				}, false);

		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ProjectActivity.this, StageActivity.class);
				startActivity(intent);
			}
		}, false);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initListeners();
		initCustomContextMenu();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;

		switch (id) {
			case Consts.DIALOG_NEW_SPRITE:
				dialog = new NewSpriteDialog(this);
				break;
			case Consts.DIALOG_RENAME_SPRITE:
				if (spriteToEdit == null) {
					dialog = null;
				} else {
					dialog = new RenameSpriteDialog(this);
				}
				break;
			case Consts.DIALOG_CONTEXT_MENU:
				if (iconContextMenu == null || spriteToEdit == null) {
					dialog = null;
				} else {
					dialog = iconContextMenu.createMenu(spriteToEdit.getName());
					dialog.setOnShowListener(new OnShowListener() { //TODO try to find a better place: not in init Custom.. (there this is not initialized) also not in CustomIconContextMenu 
						public void onShow(DialogInterface dialogInterface) {
							dialog.setTitle(spriteToEdit.getName());
						}
					});
				}
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
		if (!Utils.checkForSdCard(this)) {
			return;
		}
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
		spriteAdapter.notifyDataSetChanged();
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
