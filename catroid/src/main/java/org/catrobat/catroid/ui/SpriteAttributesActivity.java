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

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.ardrone.DroneStageActivity;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.PlaySceneDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.RenameItemDialog;

import java.util.HashSet;
import java.util.Set;

public class SpriteAttributesActivity extends BaseActivity implements
		PlaySceneDialog.PlaySceneInterface,
		RenameItemDialog.RenameItemInterface {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SettingsActivity.setToChosenLanguage(this);

		setContentView(R.layout.activity_sprite_attributes);
		BottomBar.hideAddButton(this);

		updateActionBarTitle();
	}

	private void updateActionBarTitle() {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		String currentSceneName = ProjectManager.getInstance().getCurrentScene().getName();
		String currentSpriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		String title = currentSpriteName;

		if (currentProject.getSceneList().size() > 1) {
			title = currentSceneName + ": " + currentSpriteName;
		}

		getActionBar().setTitle(title);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.menu_rename_sprite:
				showRenameDialog();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.backgrounds);
		} else {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.looks);
		}

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("setting_nfc_bricks", false) && BuildConfig.FEATURE_NFC_ENABLED) {
			findViewById(R.id.program_menu_button_nfctags).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.program_menu_button_nfctags).setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			return super.onCreateOptionsMenu(menu);
		}
		getMenuInflater().inflate(R.menu.menu_program_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void showRenameDialog() {
		String name = ProjectManager.getInstance().getCurrentSprite().getName();
		RenameItemDialog dialog = new RenameItemDialog(
				R.string.rename_sprite_dialog,
				R.string.sprite_name_label, name,
				this);
		dialog.show(getFragmentManager(), RenameItemDialog.TAG);
	}

	@Override
	public boolean isNameUnique(String name) {
		return !getScope().contains(name);
	}

	protected Set<String> getScope() {
		Set<String> scope = new HashSet<>();
		for (Sprite item : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			scope.add(item.getName());
		}
		return scope;
	}

	@Override
	public void renameItem(String name) {
		Sprite item = ProjectManager.getInstance().getCurrentSprite();
		if (!item.getName().equals(name)) {
			item.setName(name);
		}
		updateActionBarTitle();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {

			Intent intent;
			if (DroneServiceWrapper.checkARDroneAvailability()) {
				intent = new Intent(this, DroneStageActivity.class);
			} else {
				intent = new Intent(this, StageActivity.class);
			}
			startActivity(intent);
		}
	}

	public void handleScriptsButton(View view) {
		startScriptActivity(SpriteActivity.FRAGMENT_SCRIPTS);
	}

	public void handleLooksButton(View view) {
		startScriptActivity(SpriteActivity.FRAGMENT_LOOKS);
	}

	public void handleSoundsButton(View view) {
		startScriptActivity(SpriteActivity.FRAGMENT_SOUNDS);
	}

	public void handleNfcTagsButton(View view) {
		startScriptActivity(SpriteActivity.FRAGMENT_NFC_TAGS);
	}

	public void handlePlayButton(View view) {

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();

		if (currentScene.getName().equals(currentProject.getDefaultScene().getName())) {
			ProjectManager.getInstance().setSceneToPlay(currentScene);
			ProjectManager.getInstance().setStartScene(currentScene);
			startPreStageActivity();
			return;
		}

		PlaySceneDialog playSceneDialog = new PlaySceneDialog(this);
		playSceneDialog.show(getFragmentManager(), PlaySceneDialog.TAG);
	}

	@Override
	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	private void startScriptActivity(int fragmentPosition) {
		Intent intent = new Intent(this, SpriteActivity.class);
		intent.putExtra(SpriteActivity.EXTRA_FRAGMENT_POSITION, fragmentPosition);
		startActivity(intent);
	}
}
