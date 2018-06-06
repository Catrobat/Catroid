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

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.ardrone.DroneStageActivity;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.LegoSensorConfigInfoDialog;
import org.catrobat.catroid.ui.recyclerview.activity.ProjectUploadActivity;
import org.catrobat.catroid.ui.recyclerview.dialog.PlaySceneDialogFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SceneListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ProjectActivity extends BaseCastActivity implements PlaySceneDialogFragment.PlaySceneInterface {

	public static final String EXTRA_FRAGMENT_POSITION = "FRAGMENT_POSITION";

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({FRAGMENT_SCENES, FRAGMENT_SPRITES})
	@interface FragmentPosition {}
	public static final int FRAGMENT_SCENES = 0;
	public static final int FRAGMENT_SPRITES = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SettingsFragment.setToChosenLanguage(this);

		setContentView(R.layout.activity_recycler);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int fragmentPosition = FRAGMENT_SCENES;
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCENES);
		}

		loadFragment(fragmentPosition);
		showLegoSensorConfigInfo();
	}

	private void loadFragment(@FragmentPosition int fragmentPosition) {
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

		switch (fragmentPosition) {
			case FRAGMENT_SCENES:
				fragmentTransaction.replace(R.id.fragment_container, new SceneListFragment(), SceneListFragment.TAG);
				break;
			case FRAGMENT_SPRITES:
				fragmentTransaction.replace(R.id.fragment_container, new SpriteListFragment(), SpriteListFragment.TAG);
				break;
			default:
				return;
		}

		fragmentTransaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_project_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.upload:
				Intent intent = new Intent(this, ProjectUploadActivity.class)
						.putExtra(ProjectUploadActivity.PROJECT_NAME,
								ProjectManager.getInstance().getCurrentProject().getName());
				startActivity(intent);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			getFragmentManager().popBackStack();
		} else {
			if ((ProjectManager.getInstance().getCurrentProject().getSceneList().size() > 1)
					&& (getFragmentManager().findFragmentById(R.id.fragment_container) instanceof SpriteListFragment)) {
				getFragmentManager().beginTransaction()
						.replace(R.id.fragment_container, new SceneListFragment(), SceneListFragment.TAG)
						.commit();
			} else {
				super.onBackPressed();
			}
		}
	}

	public void handleAddButton(View view) {
		((RecyclerViewFragment) getFragmentManager().findFragmentById(R.id.fragment_container)).handleAddButton();
	}

	public void handlePlayButton(View view) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();

		if (currentScene.getName().equals(currentProject.getDefaultScene().getName())) {
			Intent intent = new Intent(this, PreStageActivity.class);
			startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
		} else {
			PlaySceneDialogFragment playSceneDialog = new PlaySceneDialogFragment(this);
			playSceneDialog.show(getFragmentManager(), PlaySceneDialogFragment.TAG);
		}
	}

	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
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

		if (requestCode != RESULT_OK
				&& SettingsFragment.isCastSharedPreferenceEnabled(this)
				&& ProjectManager.getInstance().getCurrentProject().isCastProject()
				&& !CastManager.getInstance().isConnected()) {

			CastManager.getInstance().openDeviceSelectorOrDisconnectDialog(this);
		}
	}

	private void showLegoSensorConfigInfo() {
		int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean nxtDialogDisabled = preferences.getBoolean(
				SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, false);
		boolean ev3DialogDisabled = preferences.getBoolean(
				SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED, false);

		if (!nxtDialogDisabled && (Brick.BLUETOOTH_LEGO_NXT & requiredResources) != 0) {
			DialogFragment dialog = new LegoSensorConfigInfoDialog(LegoSensorConfigInfoDialog.NXT);
			dialog.show(getFragmentManager(), LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
		if (!ev3DialogDisabled && (Brick.BLUETOOTH_LEGO_EV3 & requiredResources) != 0) {
			DialogFragment dialog = new LegoSensorConfigInfoDialog(LegoSensorConfigInfoDialog.EV3);
			dialog.show(getFragmentManager(), LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
	}
}
