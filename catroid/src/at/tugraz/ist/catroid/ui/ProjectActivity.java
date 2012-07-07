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

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.dialogs.CustomIconContextMenu;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSpriteDialog;
import at.tugraz.ist.catroid.ui.fragment.SpritesListFragment;
import at.tugraz.ist.catroid.ui.fragment.SpritesListFragment.OnSpriteToEditSelectedListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class ProjectActivity extends SherlockFragmentActivity implements OnSpriteToEditSelectedListener {
	
	public static final String ACTION_SPRITE_RENAMED = "at.tugraz.ist.catroid.SPRITE_RENAMED";
	
	private Sprite spriteToEdit;
	private CustomIconContextMenu iconContextMenu;
	
	private SpriteRenamedReceiver spriteRenamedReceiver;
	
	private static final int CONTEXT_MENU_ITEM_RENAME = 0; // or R.id.project_menu_rename
	private static final int CONTEXT_MENU_ITEM_DELETE = 1; // or R.id.project_menu_delete

	public static final int DIALOG_CONTEXT_MENU = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		spriteToEdit = (Sprite) getLastCustomNonConfigurationInstance();
	}

	@Override
	protected void onStart() {
		super.onStart();
		initCustomContextMenu();
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		final Sprite savedSelectedSprite = spriteToEdit;
		return savedSelectedSprite;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (spriteRenamedReceiver == null) {
			spriteRenamedReceiver = new SpriteRenamedReceiver();
		}
		
		IntentFilter intentFilterSpriteRenamed = new IntentFilter(ACTION_SPRITE_RENAMED);
		registerReceiver(spriteRenamedReceiver, intentFilterSpriteRenamed);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (spriteRenamedReceiver != null) {
			unregisterReceiver(spriteRenamedReceiver);
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home: {
                Intent intent = new Intent(this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
            case R.id.menu_add: {
            	NewSpriteDialog dialog = new NewSpriteDialog();
            	dialog.show(getSupportFragmentManager(), "dialog_new_sprite");
                return true;
            }
            case R.id.menu_start: {
                Intent intent = new Intent(this, PreStageActivity.class);
                startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ProjectActivity.this, StageActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		switch (id) {
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			notifySpritesAdapterDataChanged();
		}
	}

	public void notifySpritesAdapterDataChanged() {
		FragmentManager fm = getSupportFragmentManager();
		SpritesListFragment spritesListFragment = (SpritesListFragment) fm.findFragmentById(R.id.fr_sprites_list);
		spritesListFragment.notifySpriteAdapter();
	}
	
	public Sprite getSpriteToEdit() {
		return spriteToEdit;
	}

	public void handleProjectActivityItemLongClick(View view) {
	}
	
	@Override
	public void onSpriteToEditSelected(Sprite sprite) {
		spriteToEdit = sprite;
	}
	
	private void initCustomContextMenu() {
		Resources resources = getResources();
		iconContextMenu = new CustomIconContextMenu(this, DIALOG_CONTEXT_MENU);
		iconContextMenu.addItem(resources, getString(R.string.rename), R.drawable.ic_context_rename,
				CONTEXT_MENU_ITEM_RENAME);
		iconContextMenu.addItem(resources, getString(R.string.delete), R.drawable.ic_context_delete,
				CONTEXT_MENU_ITEM_DELETE);

		iconContextMenu.setOnClickListener(new CustomIconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) {
				switch (menuId) {
					case CONTEXT_MENU_ITEM_RENAME:
						RenameSpriteDialog dialog = RenameSpriteDialog.newInstance(spriteToEdit.getName());
						dialog.show(getSupportFragmentManager(), "dialog_rename_sprite");
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
	
	private class SpriteRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_SPRITE_RENAMED)) {
				String newSpriteName = intent.getExtras().getString(RenameSpriteDialog.EXTRA_NEW_SPRITE_NAME);
				spriteToEdit.setName(newSpriteName);
			}
		}
	}
}
