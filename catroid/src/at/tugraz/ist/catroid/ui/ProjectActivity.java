/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.adapter.SpriteAdapter;
import at.tugraz.ist.catroid.ui.dialogs.CustomIconContextMenu;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSpriteDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectActivity extends ListActivity {

	private SpriteAdapter spriteAdapter;
	private ArrayList<Sprite> spriteList;
	private Sprite spriteToEdit;
	private ActivityHelper activityHelper;
	private CustomIconContextMenu iconContextMenu;
	private RenameSpriteDialog renameDialog;
	private NewSpriteDialog newSpriteDialog;
	private static final int CONTEXT_MENU_ITEM_RENAME = 0; //or R.id.project_menu_rename
	private static final int CONTEXT_MENU_ITEM_DELETE = 1; //or R.id.project_menu_delete 
	public static final int DIALOG_NEW_SPRITE = 0;
	public static final int DIALOG_RENAME_SPRITE = 1;
	private static final int DIALOG_CONTEXT_MENU = 2;

	private void initListeners() {
		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		spriteAdapter = new SpriteAdapter(this, R.layout.activity_project_spritelist_item, R.id.sprite_title,
				spriteList);

		setListAdapter(spriteAdapter);
		getListView().setTextFilterEnabled(true);
		getListView().setDivider(null);
		getListView().setDividerHeight(0);

		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProjectManager.getInstance().setCurrentSprite(spriteAdapter.getItem(position));
				Intent intent = new Intent(ProjectActivity.this, ScriptTabActivity.class);
				startActivity(intent);
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				spriteToEdit = spriteList.get(position);

				//as long as background sprite is always the first one, we're fine
				if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(spriteToEdit) == 0) {
					return true;
				}
				removeDialog(DIALOG_CONTEXT_MENU);
				showDialog(DIALOG_CONTEXT_MENU);
				return true;
			}
		});
	}

	private void initCustomContextMenu() {
		Resources resources = getResources();
		iconContextMenu = new CustomIconContextMenu(this, DIALOG_CONTEXT_MENU);
		iconContextMenu.addItem(resources, this.getString(R.string.rename), R.drawable.ic_context_rename,
				CONTEXT_MENU_ITEM_RENAME);
		iconContextMenu.addItem(resources, this.getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			public void onClick(int menuId) {
				switch (menuId) {
					case CONTEXT_MENU_ITEM_RENAME:
						showDialog(DIALOG_RENAME_SPRITE);
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
		activityHelper = new ActivityHelper(this);
		setContentView(R.layout.activity_project);
		Utils.loadProjectIfNeeded(this);
		spriteToEdit = (Sprite) getLastNonConfigurationInstance();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final Sprite savedSelectedSprite = spriteToEdit;
		return savedSelectedSprite;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		String title = this.getResources().getString(R.string.project_name) + " "
				+ ProjectManager.getInstance().getCurrentProject().getName();
		activityHelper.setupActionBar(false, title);

		activityHelper.addActionButton(R.id.btn_action_add_button, R.drawable.ic_plus_black, R.string.add,
				new View.OnClickListener() {
					public void onClick(View v) {
						showDialog(DIALOG_NEW_SPRITE);
					}
				}, false);

		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, R.string.start,
				new View.OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(ProjectActivity.this, PreStageActivity.class);
						startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
					}
				}, false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ProjectActivity.this, StageActivity.class);
			startActivity(intent);
		}
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
			case DIALOG_NEW_SPRITE:
				newSpriteDialog = new NewSpriteDialog(this);
				dialog = newSpriteDialog.dialog;
				break;
			case DIALOG_RENAME_SPRITE:
				if (spriteToEdit == null) {
					dialog = null;
				} else {
					renameDialog = new RenameSpriteDialog(this);
					dialog = renameDialog.dialog;
				}
				break;
			case DIALOG_CONTEXT_MENU:
				if (iconContextMenu == null || spriteToEdit == null) {
					dialog = null;
				} else {
					dialog = iconContextMenu.createMenu(spriteToEdit.getName());
				}
				break;
			default:
				dialog = null;
				break;
		}

		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
			case DIALOG_RENAME_SPRITE:
				if (dialog != null && spriteToEdit != null) {
					EditText spriteTitleInput = (EditText) dialog.findViewById(R.id.dialog_text_EditText);
					spriteTitleInput.setText(spriteToEdit.getName());
				}
				break;
			case DIALOG_NEW_SPRITE:
				if (dialog != null) {
					Button buttonPositive = (Button) dialog.findViewById(R.id.dialog_text_ok);
					buttonPositive.setEnabled(false);
				}
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		spriteAdapter.notifyDataSetChanged();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			spriteAdapter.notifyDataSetChanged();
		}
	}

	public Sprite getSpriteToEdit() {
		return spriteToEdit;
	}

	@Override
	public void onPause() {
		super.onPause();
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null) {
			projectManager.saveProject();
		}
	}

	public void handleProjectActivityItemLongClick(View view) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Utils.saveToPreferences(this, Consts.PREF_PROJECTNAME_KEY, ProjectManager.getInstance().getCurrentProject()
					.getName());
			Intent intent = new Intent(this, MainMenuActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

}
