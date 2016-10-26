/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import org.catrobat.catroid.ui.dialogs.RenameSpriteDialog;

import java.util.concurrent.locks.Lock;

public class ProgramMenuActivity extends BaseActivity {

	public static final String FORWARD_TO_SCRIPT_ACTIVITY = "forwardToScriptActivity";

	private static final String TAG = ProgramMenuActivity.class.getSimpleName();
	private Lock viewSwitchLock = new ViewSwitchLock();
	private SpriteRenamedReceiver spriteRenamedReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(FORWARD_TO_SCRIPT_ACTIVITY)) {
			Intent intent = new Intent(this, ScriptActivity.class);
			intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, bundle.getInt(FORWARD_TO_SCRIPT_ACTIVITY));
			startActivity(intent);
		}

		setContentView(R.layout.activity_program_menu);

		BottomBar.hideAddButton(this);

		final ActionBar actionBar = getActionBar();

		//The try-catch block is a fix for this bug: https://github.com/Catrobat/Catroid/issues/618
		try {
			Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
			Scene scene = ProjectManager.getInstance().getCurrentScene();
			if (sprite != null && scene != null && actionBar != null) {
				String title = ProjectManager.getInstance().getCurrentProject().getSceneList().size() > 1 ? scene.getName() + ": " + sprite.getName() : sprite.getName();
				actionBar.setTitle(title);
				actionBar.setHomeButtonEnabled(true);
			}
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, "onCreate: NPE -> finishing", nullPointerException);
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_rename_sprite:
				showRenameDialog();
				break;
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ProjectManager.getInstance().getCurrentSpritePosition() == 0) {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.backgrounds);
		} else {
			((Button) findViewById(R.id.program_menu_button_looks)).setText(R.string.looks);
		}

		//Hide NFC if option is not set
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("setting_nfc_bricks", false) && BuildConfig.FEATURE_NFC_ENABLED) {
			findViewById(R.id.program_menu_button_nfctags).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.program_menu_button_nfctags).setVisibility(View.INVISIBLE);
		}

		if (spriteRenamedReceiver == null) {
			spriteRenamedReceiver = new SpriteRenamedReceiver();
		}

		IntentFilter intentFilterSpriteRenamed = new IntentFilter(ScriptActivity.ACTION_SPRITE_RENAMED);
		getBaseContext().registerReceiver(spriteRenamedReceiver, intentFilterSpriteRenamed);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (spriteRenamedReceiver != null) {
			getBaseContext().unregisterReceiver(spriteRenamedReceiver);
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

	private class SpriteRenamedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScriptActivity.ACTION_SPRITE_RENAMED)) {
				String newSpriteName = intent.getExtras().getString(RenameSpriteDialog.EXTRA_NEW_SPRITE_NAME);
				ProjectManager.getInstance().getCurrentSprite().setName(newSpriteName);
				final ActionBar actionBar = getActionBar();
				actionBar.setTitle(newSpriteName);
			}
		}
	}

	private void showRenameDialog() {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		RenameSpriteDialog dialog = RenameSpriteDialog.newInstance(sprite.getName());
		dialog.show(getFragmentManager(), RenameSpriteDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {

			Intent intent;
			if (DroneServiceWrapper.checkARDroneAvailability()) {
				intent = new Intent(ProgramMenuActivity.this, DroneStageActivity.class);
			} else {
				intent = new Intent(ProgramMenuActivity.this, StageActivity.class);
			}
			startActivity(intent);
		}
	}

	public void handleScriptsButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_SCRIPTS);
	}

	public void handleLooksButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_LOOKS);
	}

	public void handleSoundsButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_SOUNDS);
	}

	public void handleNfcTagsButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		startScriptActivity(ScriptActivity.FRAGMENT_NFCTAGS);
	}

	public void handlePlayButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();

		if (currentScene.getName().equals(currentProject.getDefaultScene().getName())) {
			ProjectManager.getInstance().setSceneToPlay(currentScene);
			startPreStageActivity();
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		PlaySceneDialog playSceneDialog = new PlaySceneDialog();
		playSceneDialog.show(fragmentTransaction, PlaySceneDialog.DIALOG_FRAGMENT_TAG);
	}

	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	private void startScriptActivity(int fragmentPosition) {
		Intent intent = new Intent(this, ScriptActivity.class);
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, fragmentPosition);
		startActivity(intent);
	}
}
