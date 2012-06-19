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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.PreStageActivity;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.dialogs.CustomIconContextMenu;
import at.tugraz.ist.catroid.ui.dialogs.NewSpriteDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSpriteDialog;
import at.tugraz.ist.catroid.ui.fragment.SpritesListFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class ProjectActivity extends SherlockFragmentActivity {

	private Sprite spriteToEdit;
	private CustomIconContextMenu iconContextMenu;
	private RenameSpriteDialog renameDialog;
	private NewSpriteDialog newSpriteDialog;

	public static final int DIALOG_NEW_SPRITE = 0;
	public static final int DIALOG_RENAME_SPRITE = 1;
	private static final int DIALOG_CONTEXT_MENU = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
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
                showDialog(DIALOG_NEW_SPRITE);
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
		}
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
}
