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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.ardrone.DroneStageActivity;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.dialogs.LegoSensorConfigInfoDialog;
import org.catrobat.catroid.ui.recyclerview.activity.ProjectUploadActivity;
import org.catrobat.catroid.ui.recyclerview.controller.SceneController;
import org.catrobat.catroid.ui.recyclerview.dialog.PlaySceneDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.NewItemTextWatcher;
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SceneListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.EXTRA_PICTURE_PATH_POCKET_PAINT;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_LOOKS_URL;
import static org.catrobat.catroid.ui.WebViewActivity.MEDIA_FILE_PATH;

public class ProjectActivity extends BaseCastActivity {

	public static final String TAG = ProjectActivity.class.getSimpleName();

	public static final int FRAGMENT_SCENES = 0;
	public static final int FRAGMENT_SPRITES = 1;

	public static final int SPRITE_POCKET_PAINT = 0;
	public static final int SPRITE_LIBRARY = 1;
	public static final int SPRITE_FILE = 2;
	public static final int SPRITE_CAMERA = 3;

	public static final String EXTRA_FRAGMENT_POSITION = "fragmentPosition";

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

		int fragmentPosition = FRAGMENT_SCENES;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCENES);
		}
		loadFragment(fragmentPosition);
		showLegoSensorConfigInfo();
	}

	private void loadFragment(int fragmentPosition) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		switch (fragmentPosition) {
			case FRAGMENT_SCENES:
				fragmentTransaction.replace(R.id.fragment_container, new SceneListFragment(), SceneListFragment.TAG);
				break;
			case FRAGMENT_SPRITES:
				fragmentTransaction.replace(R.id.fragment_container, new SpriteListFragment(), SpriteListFragment.TAG);
				break;
			default:
				throw new IllegalArgumentException("Invalid fragmentPosition in Activity.");
		}

		fragmentTransaction.commit();
	}

	private Fragment getCurrentFragment() {
		return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_project_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_scene:
				handleAddSceneButton();
				break;
			case R.id.upload:
				Project currentProject = ProjectManager.getInstance().getCurrentProject();
				Intent intent = new Intent(this, ProjectUploadActivity.class);
				intent.putExtra(ProjectUploadActivity.PROJECT_NAME, currentProject.getName());
				startActivity(intent);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
			return;
		}

		boolean multiSceneProject = ProjectManager.getInstance().getCurrentProject().getSceneList().size() > 1;

		if (getCurrentFragment() instanceof SpriteListFragment && multiSceneProject) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, new SceneListFragment(), SceneListFragment.TAG)
					.commit();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			SensorHandler.stopSensorListeners();
			FaceDetectionHandler.stopFaceDetection();
		}

		if (resultCode != RESULT_OK) {
			if (SettingsFragment.isCastSharedPreferenceEnabled(this)
					&& ProjectManager.getInstance().getCurrentProject().isCastProject()
					&& !CastManager.getInstance().isConnected()) {

				CastManager.getInstance().openDeviceSelectorOrDisconnectDialog(this);
			}
			return;
		}

		Uri uri;

		switch (requestCode) {
			case PreStageActivity.REQUEST_RESOURCES_INIT:
				startStageActivity();
				break;
			case SPRITE_POCKET_PAINT:
				uri = Uri.fromFile(new File(data.getStringExtra(EXTRA_PICTURE_PATH_POCKET_PAINT)));
				addSpriteFromUri(uri);
				break;
			case SPRITE_LIBRARY:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				addSpriteFromUri(uri);
				break;
			case SPRITE_FILE:
				uri = data.getData();
				addSpriteFromUri(uri);
				break;
			case SPRITE_CAMERA:
				uri = new ImportFromCameraLauncher(this).getCacheCameraUri();
				addSpriteFromUri(uri);
				break;
		}
	}

	public void addSpriteFromUri(final Uri uri) {
		final Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();

		String name = StorageOperations.resolveFileName(getContentResolver(), uri);
		if (name == null) {
			name = getString(R.string.default_look_name);
		} else {
			name = StorageOperations.getSanitizedFileName(name);
		}
		name = new UniqueNameProvider().getUniqueNameInNameables(name, currentScene.getSpriteList());
		final String lookName = name;

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this);

		builder.setHint(getString(R.string.sprite_name_label))
				.setText(name)
				.setTextWatcher(new NewItemTextWatcher<>(currentScene.getSpriteList()))
				.setPositiveButton(getString(R.string.ok), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
						File imageDirectory = new File(currentScene.getDirectory(), IMAGE_DIRECTORY_NAME);
						Sprite sprite = new Sprite(textInput);
						currentScene.getSpriteList().add(sprite);

						try {
							File file = StorageOperations.copyUriToDir(getContentResolver(), uri, imageDirectory, lookName);
							sprite.getLookList().add(new LookData(lookName, file));
						} catch (IOException e) {
							Log.e(TAG, Log.getStackTraceString(e));
						}

						if (getCurrentFragment() instanceof SpriteListFragment) {
							((SpriteListFragment) getCurrentFragment()).notifyDataSetChanged();
						}
					}
				});

		builder.setTitle(R.string.new_sprite_dialog_title)
				.setNegativeButton(R.string.cancel, null)
				.create()
				.show();
	}

	public void handleAddButton(View view) {
		if (getCurrentFragment() instanceof SceneListFragment) {
			handleAddSceneButton();
			return;
		}
		if (getCurrentFragment() instanceof SpriteListFragment) {
			handleAddSpriteButton();
		}
	}

	public void handleAddSceneButton() {
		final Project currentProject = ProjectManager.getInstance().getCurrentProject();

		String defaultSceneName = SceneController
				.getUniqueDefaultSceneName(getResources(), currentProject.getSceneList());

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this);

		builder.setHint(getString(R.string.scene_name_label))
				.setText(defaultSceneName)
				.setTextWatcher(new NewItemTextWatcher<>(currentProject.getSceneList()))
				.setPositiveButton(getString(R.string.ok), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
						Scene scene = SceneController
								.newSceneWithBackgroundSprite(textInput, getString(R.string.background), currentProject);
						currentProject.addScene(scene);

						if (getCurrentFragment() instanceof SceneListFragment) {
							((RecyclerViewFragment) getCurrentFragment()).notifyDataSetChanged();
						} else {
							Intent intent = new Intent(ProjectActivity.this, ProjectActivity.class);
							intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
							startActivity(intent);
							finish();
						}
					}
				});

		builder.setTitle(R.string.new_scene_dialog)
				.setNegativeButton(R.string.cancel, null)
				.create()
				.show();
	}

	public void handleAddSpriteButton() {
		View view = View.inflate(this, R.layout.dialog_new_look, null);

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_sprite_dialog_title)
				.setView(view)
				.create();

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.dialog_new_look_paintroid:
						new ImportFromPocketPaintLauncher(ProjectActivity.this)
								.startActivityForResult(SPRITE_POCKET_PAINT);
						break;
					case R.id.dialog_new_look_media_library:
						new ImportFormMediaLibraryLauncher(ProjectActivity.this, LIBRARY_LOOKS_URL)
								.startActivityForResult(SPRITE_LIBRARY);
						break;
					case R.id.dialog_new_look_gallery:
						new ImportFromFileLauncher(ProjectActivity.this, "image/*", getString(R.string.select_look_from_gallery))
								.startActivityForResult(SPRITE_FILE);
						break;
					case R.id.dialog_new_look_camera:
						new ImportFromCameraLauncher(ProjectActivity.this)
								.startActivityForResult(SPRITE_CAMERA);
						break;
				}
				alertDialog.dismiss();
			}
		};

		view.findViewById(R.id.dialog_new_look_paintroid).setOnClickListener(onClickListener);
		view.findViewById(R.id.dialog_new_look_media_library).setOnClickListener(onClickListener);
		view.findViewById(R.id.dialog_new_look_gallery).setOnClickListener(onClickListener);
		view.findViewById(R.id.dialog_new_look_camera).setOnClickListener(onClickListener);
		alertDialog.show();
	}

	public void handlePlayButton(View view) {
		ProjectManager projectManager = ProjectManager.getInstance();
		Scene currentScene = projectManager.getCurrentlyEditedScene();
		Scene defaultScene = projectManager.getCurrentProject().getDefaultScene();

		if (currentScene.getName().equals(defaultScene.getName())) {
			projectManager.setCurrentlyPlayingScene(defaultScene);
			projectManager.setStartScene(defaultScene);
			startPreStageActivity();
		} else {
			new PlaySceneDialog.Builder(this)
					.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startPreStageActivity();
						}
					})
					.create()
					.show();
		}
	}

	void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	void startStageActivity() {
		if (DroneServiceWrapper.checkARDroneAvailability()) {
			startActivity(new Intent(this, DroneStageActivity.class));
		} else {
			startActivity(new Intent(this, StageActivity.class));
		}
	}

	private void showLegoSensorConfigInfo() {
		if (ProjectManager.getInstance().getCurrentProject() == null) {
			return;
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean nxtDialogDisabled = preferences
				.getBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, false);
		boolean ev3DialogDisabled = preferences
				.getBoolean(SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED, false);

		Brick.ResourcesSet resourcesSet = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		if (!nxtDialogDisabled && resourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)) {
			DialogFragment dialog = LegoSensorConfigInfoDialog.newInstance(Constants.NXT);
			dialog.show(getSupportFragmentManager(), LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
		if (!ev3DialogDisabled && resourcesSet.contains(Brick.BLUETOOTH_LEGO_EV3)) {
			DialogFragment dialog = LegoSensorConfigInfoDialog.newInstance(Constants.EV3);
			dialog.show(getSupportFragmentManager(), LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
	}
}
