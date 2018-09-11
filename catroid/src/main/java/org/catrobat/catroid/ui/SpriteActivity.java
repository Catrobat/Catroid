/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.ardrone.DroneStageActivity;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.PlaySceneDialogFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.NfcTagListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

public class SpriteActivity extends BaseActivity implements PlaySceneDialogFragment.PlaySceneInterface {

	public static final String TAG = SpriteActivity.class.getSimpleName();

	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_LOOKS = 1;
	public static final int FRAGMENT_SOUNDS = 2;
	public static final int FRAGMENT_NFC_TAGS = 3;

	public static final String EXTRA_FRAGMENT_POSITION = "FRAGMENT_POSITION";

	private static int fragmentPosition = FRAGMENT_SCRIPTS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFinishing()) {
			return;
		}

		SettingsFragment.setToChosenLanguage(this);

		setContentView(R.layout.activity_recycler);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		updateActionBarTitle();

		if (savedInstanceState == null) {
			Bundle bundle = this.getIntent().getExtras();

			if (bundle != null) {
				fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);
			}
		}

		loadFragment(fragmentPosition);
	}

	private void updateActionBarTitle() {
		String currentSceneName = ProjectManager.getInstance().getCurrentlyEditedScene().getName();
		String currentSpriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		if (ProjectManager.getInstance().getCurrentProject().getSceneList().size() == 1) {
			getSupportActionBar().setTitle(currentSpriteName);
		} else {
			getSupportActionBar().setTitle(currentSceneName + ": " + currentSpriteName);
		}
	}

	private void loadFragment(int fragmentPosition) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				fragmentTransaction.replace(R.id.fragment_container, new ScriptFragment(), ScriptFragment.TAG);
				break;
			case FRAGMENT_LOOKS:
				fragmentTransaction.replace(R.id.fragment_container, new LookListFragment(), LookListFragment.TAG);
				break;
			case FRAGMENT_SOUNDS:
				fragmentTransaction.replace(R.id.fragment_container, new SoundListFragment(), SoundListFragment.TAG);
				break;
			case FRAGMENT_NFC_TAGS:
				fragmentTransaction.replace(R.id.fragment_container, new NfcTagListFragment(), NfcTagListFragment.TAG);
				break;
			default:
				return;
		}

		fragmentTransaction.commit();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (fragmentPosition == FRAGMENT_NFC_TAGS) {
			((NfcTagListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).onNewIntent(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_script_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (fragmentPosition == FRAGMENT_SCRIPTS) {
			menu.findItem(R.id.comment_in_out).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case PreStageActivity.REQUEST_RESOURCES_INIT:

				if (resultCode == RESULT_OK) {
					if (DroneServiceWrapper.checkARDroneAvailability()) {
						startActivity(new Intent(this, DroneStageActivity.class));
					} else {
						startActivity(new Intent(this, StageActivity.class));
					}
				}
				break;
			case StageActivity.STAGE_ACTIVITY_FINISH:
				SensorHandler.stopSensorListeners();
				FaceDetectionHandler.stopFaceDetection();
				break;
		}
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (fragment instanceof FormulaEditorFragment) {
			((FormulaEditorFragment) fragment).promptSave();
			return;
		}

		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}

	public void handleAddButton(View view) {
		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
				if (fragment instanceof ScriptFragment) {
					((ScriptFragment) fragment).handleAddButton();
				}
				if (fragment instanceof DataListFragment) {
					((DataListFragment) fragment).handleAddButton();
				}
				break;
			case FRAGMENT_LOOKS:
			case FRAGMENT_SOUNDS:
			case FRAGMENT_NFC_TAGS:
				((RecyclerViewFragment) getSupportFragmentManager()
						.findFragmentById(R.id.fragment_container))
						.handleAddButton();
				break;
			default:
				break;
		}
	}

	public void handlePlayButton(View view) {
		if (isHoveringActive()) {
			ScriptFragment fragment = ((ScriptFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container));
			fragment.getListView().animateHoveringBrick();
			return;
		}

		while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		}

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();

		if (currentScene.getName().equals(currentProject.getDefaultScene().getName())) {
			ProjectManager.getInstance().setCurrentlyPlayingScene(currentScene);
			ProjectManager.getInstance().setStartScene(currentScene);
			startPreStageActivity();
			return;
		}

		PlaySceneDialogFragment playSceneDialog = new PlaySceneDialogFragment(this);
		playSceneDialog.show(getSupportFragmentManager(), PlaySceneDialogFragment.TAG);
	}

	@Override
	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	public boolean isHoveringActive() {
		if (fragmentPosition == FRAGMENT_SCRIPTS) {
			ScriptFragment fragment = ((ScriptFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container));
			return fragment.getListView().isCurrentlyDragging();
		} else {
			return false;
		}
	}
}
