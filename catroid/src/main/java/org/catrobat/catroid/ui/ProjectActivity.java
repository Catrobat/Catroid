/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectSaver;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.TestResult;
import org.catrobat.catroid.ui.dialogs.LegoSensorConfigInfoDialog;
import org.catrobat.catroid.ui.fragment.ProjectOptionsFragment;
import org.catrobat.catroid.ui.recyclerview.controller.SceneController;
import org.catrobat.catroid.ui.recyclerview.dialog.NewSpriteDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher;
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SceneListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.EV3;
import static org.catrobat.catroid.common.Constants.JPEG_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.NXT;
import static org.catrobat.catroid.common.Constants.TMP_IMAGE_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_LOOKS_URL;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_OBJECT_URL;
import static org.catrobat.catroid.stage.TestResult.TEST_RESULT_MESSAGE;
import static org.catrobat.catroid.ui.SpriteActivity.REQUEST_CODE_VISUAL_PLACEMENT;
import static org.catrobat.catroid.ui.WebViewActivity.MEDIA_FILE_PATH;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.isCastSharedPreferenceEnabled;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.X_COORDINATE_BUNDLE_ARGUMENT;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.Y_COORDINATE_BUNDLE_ARGUMENT;

public class ProjectActivity extends BaseCastActivity {

	public static final String TAG = ProjectActivity.class.getSimpleName();

	public static final int FRAGMENT_SCENES = 0;
	public static final int FRAGMENT_SPRITES = 1;

	public static final int SPRITE_POCKET_PAINT = 0;
	public static final int SPRITE_LIBRARY = 1;
	public static final int SPRITE_FILE = 2;
	public static final int SPRITE_CAMERA = 3;
	public static final int SPRITE_OBJECT = 4;

	public static final String EXTRA_FRAGMENT_POSITION = "fragmentPosition";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFinishing()) {
			return;
		}

		setContentView(R.layout.activity_recycler);
		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int fragmentPosition = FRAGMENT_SCENES;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCENES);
		}
		loadFragment(fragmentPosition);
		ProjectUtils.showWarningForSuspiciousBricksOnce(this);
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

	public void setShowProgressBar(boolean show) {
		findViewById(R.id.progress_bar).setVisibility(show ? View.VISIBLE : View.GONE);
		findViewById(R.id.fragment_container).setVisibility(show ? View.GONE : View.VISIBLE);
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
			case R.id.project_options:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container, new ProjectOptionsFragment(), ProjectOptionsFragment.TAG)
						.addToBackStack(ProjectOptionsFragment.TAG)
						.commit();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		saveProject(currentProject);
	}

	@Override
	public void onBackPressed() {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null) {
			finish();
			return;
		}

		Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

		if (!(currentFragment instanceof ProjectOptionsFragment)) {
			saveProject(currentProject);
		}

		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
			BottomBar.showBottomBar(this);
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

	private void saveProject(Project currentProject) {
		if (currentProject == null) {
			Utils.setLastUsedProjectName(getApplicationContext(), null);
			return;
		}
		new ProjectSaver(currentProject, getApplicationContext()).saveProjectAsync();
		Utils.setLastUsedProjectName(getApplicationContext(), currentProject.getName());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == TestResult.STAGE_ACTIVITY_TEST_SUCCESS
				|| resultCode == TestResult.STAGE_ACTIVITY_TEST_FAIL) {
			String message = data.getStringExtra(TEST_RESULT_MESSAGE);
			ToastUtil.showError(this, message);
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			ClipData testResult = ClipData.newPlainText("TestResult",
					ProjectManager.getInstance().getCurrentProject().getName() + "\n" + message);
			clipboard.setPrimaryClip(testResult);
		}

		if (resultCode != RESULT_OK) {
			if (isCastSharedPreferenceEnabled(this)
					&& ProjectManager.getInstance().getCurrentProject().isCastProject()
					&& !CastManager.getInstance().isConnected()) {

				CastManager.getInstance().openDeviceSelectorOrDisconnectDialog(this);
			}
			return;
		}

		Uri uri;

		switch (requestCode) {
			case SPRITE_POCKET_PAINT:
				uri = new ImportFromPocketPaintLauncher(this).getPocketPaintCacheUri();
				addSpriteFromUri(uri);
				break;
			case SPRITE_LIBRARY:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				addSpriteFromUri(uri);
				break;
			case SPRITE_OBJECT:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				break;
			case SPRITE_FILE:
				uri = data.getData();
				addSpriteFromUri(uri, JPEG_IMAGE_EXTENSION);
				break;
			case SPRITE_CAMERA:
				uri = new ImportFromCameraLauncher(this).getCacheCameraUri();
				addSpriteFromUri(uri, JPEG_IMAGE_EXTENSION);
				break;
			case REQUEST_CODE_VISUAL_PLACEMENT:
				Bundle extras = data.getExtras();
				if (extras == null) {
					return;
				}
				int xCoordinate = extras.getInt(X_COORDINATE_BUNDLE_ARGUMENT);
				int yCoordinate = extras.getInt(Y_COORDINATE_BUNDLE_ARGUMENT);

				PlaceAtBrick placeAtBrick = new PlaceAtBrick(xCoordinate, yCoordinate);
				Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
				StartScript startScript = new StartScript();
				currentSprite.addScript(startScript);
				startScript.addBrick(placeAtBrick);
				break;
		}
	}

	public void addSpriteFromUri(final Uri uri) {
		addSpriteFromUri(uri, DEFAULT_IMAGE_EXTENSION);
	}

	public void addSpriteFromUri(final Uri uri, final String imageExtension) {
		final Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();

		String resolvedName;
		String resolvedFileName = StorageOperations.resolveFileName(getContentResolver(), uri);

		final String lookDataName;
		final String lookFileName;

		boolean useDefaultSpriteName = resolvedFileName == null
				|| StorageOperations.getSanitizedFileName(resolvedFileName).equals(TMP_IMAGE_FILE_NAME);

		if (useDefaultSpriteName) {
			resolvedName = getString(R.string.default_sprite_name);
			lookFileName = resolvedName + imageExtension;
		} else {
			resolvedName = StorageOperations.getSanitizedFileName(resolvedFileName);
			lookFileName = resolvedFileName;
		}

		lookDataName = new UniqueNameProvider().getUniqueNameInNameables(resolvedName, currentScene.getSpriteList());

		new NewSpriteDialogFragment(lookDataName, lookFileName, getContentResolver(), uri, getCurrentFragment())
				.show(getSupportFragmentManager(), NewSpriteDialogFragment.Companion.getTAG());
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
		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		String defaultSceneName = SceneController
				.getUniqueDefaultSceneName(getResources(), currentProject.getSceneList());

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this);

		builder.setHint(getString(R.string.scene_name_label))
				.setText(defaultSceneName)
				.setTextWatcher(new DuplicateInputTextWatcher<>(currentProject.getSceneList()))
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					Scene scene = SceneController
							.newSceneWithBackgroundSprite(textInput, getString(R.string.background), currentProject);
					currentProject.addScene(scene);

					if (getCurrentFragment() instanceof SceneListFragment) {
						((RecyclerViewFragment) getCurrentFragment()).notifyDataSetChanged();
					} else {
						Intent intent = new Intent(this, ProjectActivity.class);
						intent.putExtra(ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SCENES);
						startActivity(intent);
						finish();
					}
				});

		builder.setTitle(R.string.new_scene_dialog)
				.setNegativeButton(R.string.cancel, null)
				.show();
	}

	public void handleAddSpriteButton() {
		View root = View.inflate(this, R.layout.dialog_new_actor, null);

		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_sprite_dialog_title)
				.setView(root)
				.create();

		root.findViewById(R.id.dialog_new_look_paintroid).setOnClickListener(view -> {
			new ImportFromPocketPaintLauncher(this)
					.startActivityForResult(SPRITE_POCKET_PAINT);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_media_library).setOnClickListener(view -> {
			new ImportFormMediaLibraryLauncher(this, LIBRARY_LOOKS_URL)
					.startActivityForResult(SPRITE_LIBRARY);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_object_library).setOnClickListener(view -> {
			new ImportFormMediaLibraryLauncher(this, LIBRARY_OBJECT_URL)
					.startActivityForResult(SPRITE_OBJECT);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_gallery).setOnClickListener(view -> {
			new ImportFromFileLauncher(this, "image/*", getString(R.string.select_look_from_gallery))
					.startActivityForResult(SPRITE_FILE);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_camera).setOnClickListener(view -> {
			new ImportFromCameraLauncher(this)
					.startActivityForResult(SPRITE_CAMERA);
			alertDialog.dismiss();
		});

		alertDialog.show();
	}

	public void handlePlayButton(View view) {
		StageActivity.handlePlayButton(ProjectManager.getInstance(), this);
	}

	private void showLegoSensorConfigInfo() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		boolean nxtDialogDisabled = preferences
				.getBoolean(SETTINGS_MINDSTORMS_NXT_SHOW_SENSOR_INFO_BOX_DISABLED, false);
		boolean ev3DialogDisabled = preferences
				.getBoolean(SETTINGS_MINDSTORMS_EV3_SHOW_SENSOR_INFO_BOX_DISABLED, false);

		Brick.ResourcesSet resourcesSet = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		if (!nxtDialogDisabled && resourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)) {
			DialogFragment dialog = LegoSensorConfigInfoDialog.newInstance(NXT);
			dialog.show(getSupportFragmentManager(), LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
		if (!ev3DialogDisabled && resourcesSet.contains(Brick.BLUETOOTH_LEGO_EV3)) {
			DialogFragment dialog = LegoSensorConfigInfoDialog.newInstance(EV3);
			dialog.show(getSupportFragmentManager(), LegoSensorConfigInfoDialog.DIALOG_FRAGMENT_TAG);
		}
	}
}
