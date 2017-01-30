/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.dialogs.PlaySceneDialog;
import org.catrobat.catroid.ui.dialogs.RenameItemDialog;
import org.catrobat.catroid.ui.fragment.LookListFragment;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;

public class SpriteMemberSelectionActivity extends CoreActivity implements RenameItemDialog.RenameItemInterface {

	public static final String TAG = SpriteMemberSelectionActivity.class.getSimpleName();

	private Sprite currentSprite;
	private Scene currentScene;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentSprite = ProjectManager.getInstance().getCurrentSprite();
		currentScene = ProjectManager.getInstance().getCurrentScene();

		setContentView(R.layout.activity_sprite_member_selection);

		setActionBarTitle(currentSprite.getName());
		BottomBar.hideAddButton(this);
	}

	private void setActionBarTitle(String spriteName) {
		boolean multipleScenes = ProjectManager.getInstance().getCurrentProject().isScenesEnabled();
		String title = multipleScenes ? currentScene.getName().concat(" : ").concat(spriteName) : spriteName;
		getActionBar().setTitle(title);
	}

	@Override
	protected void onResume() {
		super.onResume();

		boolean isBackgroundSprite = ProjectManager.getInstance().getCurrentSpritePosition() == 0;
		int looksButtonTitle = isBackgroundSprite ? R.string.backgrounds : R.string.looks;
		((Button) findViewById(R.id.program_menu_button_looks)).setText(looksButtonTitle);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isNfcEnabled = sharedPreferences.getBoolean("setting_nfc_bricks", false);
		findViewById(R.id.program_menu_button_nfctags).setVisibility(isNfcEnabled ? View.VISIBLE : View.GONE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (ProjectManager.getInstance().getCurrentSpritePosition() != 0) {
			getMenuInflater().inflate(R.menu.menu_sprite_member_selection, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.rename:
				showRenameDialog();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void showRenameDialog() {
		RenameItemDialog dialog = new RenameItemDialog(R.string.dialog_rename_sprite, R.string.sprite_name,
				currentSprite.getName(), this);
		dialog.show(getFragmentManager(), RenameItemDialog.DIALOG_FRAGMENT_TAG);
	}

	public void handleScriptsButton(View view) {
		Intent intent = new Intent(this, ScriptActivity.class);
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, ScriptActivity.FRAGMENT_SCRIPTS);
		startActivity(intent);
	}

	public void handleLooksButton(View view) {
		Intent intent = new Intent(this, SpriteActivity.class);
		intent.putExtra(SpriteActivity.FRAGMENT, LookListFragment.class);
		startActivity(intent);
	}

	public void handleSoundsButton(View view) {
		Intent intent = new Intent(this, ScriptActivity.class);
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, ScriptActivity.FRAGMENT_SOUNDS);
		startActivity(intent);
	}

	public void handleNfcTagsButton(View view) {
		Intent intent = new Intent(this, SpriteActivity.class);
		intent.putExtra(SpriteActivity.FRAGMENT, NfcTagFragment.class);
		startActivity(intent);
	}

	public void handlePlayButton(View view) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		if (currentScene.getName().equals(currentProject.getDefaultScene().getName())) {
			ProjectManager.getInstance().setSceneToPlay(currentScene);
			startPreStageActivity();
			return;
		}

		PlaySceneDialog playSceneDialog = new PlaySceneDialog();
		playSceneDialog.show(getFragmentManager(), PlaySceneDialog.DIALOG_FRAGMENT_TAG);
	}

	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@Override
	public void clearCheckedItems() {
		//NOTHING TO CLEAR HERE.
	}

	@Override
	public boolean itemNameExists(String newItemName) {
		return ProjectManager.getInstance().spriteExists(newItemName);
	}

	@Override
	public void renameItem(String newItemName) {
		currentSprite.setName(newItemName);
		setActionBarTitle(newItemName);
	}
}
