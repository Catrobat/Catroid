/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.google.android.material.textfield.TextInputEditText;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.VisualPlacementBrick;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectSaver;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.TestResult;
import org.catrobat.catroid.ui.controller.RecentBrickListManager;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher;
import org.catrobat.catroid.ui.recyclerview.fragment.CatblocksScriptFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.ListSelectorFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.DEFAULT_SOUND_EXTENSION;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.JPEG_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.MEDIA_LIBRARY_CACHE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.TMP_IMAGE_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BACKGROUNDS_URL_LANDSCAPE;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BACKGROUNDS_URL_PORTRAIT;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_LOOKS_URL;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_SOUNDS_URL;
import static org.catrobat.catroid.common.SharedPreferenceKeys.INDEXING_VARIABLE_PREFERENCE_KEY;
import static org.catrobat.catroid.stage.TestResult.TEST_RESULT_MESSAGE;
import static org.catrobat.catroid.ui.SpriteActivityOnTabSelectedListenerKt.addTabLayout;
import static org.catrobat.catroid.ui.SpriteActivityOnTabSelectedListenerKt.getTabPositionInSpriteActivity;
import static org.catrobat.catroid.ui.SpriteActivityOnTabSelectedListenerKt.isFragmentWithTablayout;
import static org.catrobat.catroid.ui.SpriteActivityOnTabSelectedListenerKt.loadFragment;
import static org.catrobat.catroid.ui.SpriteActivityOnTabSelectedListenerKt.removeTabLayout;
import static org.catrobat.catroid.ui.WebViewActivity.MEDIA_FILE_PATH;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.CHANGED_COORDINATES;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.X_COORDINATE_BUNDLE_ARGUMENT;
import static org.catrobat.catroid.visualplacement.VisualPlacementActivity.Y_COORDINATE_BUNDLE_ARGUMENT;

public class SpriteActivity extends BaseActivity {

	public static final String TAG = SpriteActivity.class.getSimpleName();

	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_LOOKS = 1;
	public static final int FRAGMENT_SOUNDS = 2;

	public static final int SPRITE_POCKET_PAINT = 0;
	public static final int SPRITE_LIBRARY = 1;
	public static final int SPRITE_FILE = 2;
	public static final int SPRITE_CAMERA = 3;

	public static final int BACKGROUND_POCKET_PAINT = 4;
	public static final int BACKGROUND_LIBRARY = 5;
	public static final int BACKGROUND_FILE = 6;
	public static final int BACKGROUND_CAMERA = 7;

	public static final int LOOK_POCKET_PAINT = 8;
	public static final int LOOK_LIBRARY = 9;
	public static final int LOOK_FILE = 10;
	public static final int LOOK_CAMERA = 11;

	public static final int SOUND_RECORD = 12;
	public static final int SOUND_LIBRARY = 13;
	public static final int SOUND_FILE = 14;

	public static final int REQUEST_CODE_VISUAL_PLACEMENT = 2019;
	public static final int EDIT_LOOK = 2020;

	public static final String EXTRA_FRAGMENT_POSITION = "fragmentPosition";
	public static final String EXTRA_BRICK_HASH = "BRICK_HASH";

	public static final String EXTRA_X_TRANSFORM = "X";
	public static final String EXTRA_Y_TRANSFORM = "Y";
	public static final String EXTRA_TEXT = "TEXT";
	public static final String EXTRA_TEXT_COLOR = "TEXT_COLOR";
	public static final String EXTRA_TEXT_SIZE = "TEXT_SIZE";
	public static final String EXTRA_TEXT_ALIGNMENT = "TEXT_ALIGNMENT";

	private NewItemInterface<Sprite> onNewSpriteListener;
	private NewItemInterface<LookData> onNewLookListener;
	private NewItemInterface<SoundInfo> onNewSoundListener;

	private ProjectManager projectManager;
	private Project currentProject;
	private Sprite currentSprite;
	private Scene currentScene;
	private LookData currentLookData;
	private String generatedVariableName;

	private boolean isUndoMenuItemVisible = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFinishing()) {
			return;
		}

		projectManager = ProjectManager.getInstance();
		currentProject = projectManager.getCurrentProject();
		currentSprite = projectManager.getCurrentSprite();
		currentScene = projectManager.getCurrentlyEditedScene();

		setContentView(R.layout.activity_sprite);
		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(createActionBarTitle());

		if (RecentBrickListManager.getInstance().getRecentBricks(true).size() == 0) {
			RecentBrickListManager.getInstance().loadRecentBricks();
		}

		int fragmentPosition = FRAGMENT_SCRIPTS;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);
		}
		loadFragment(this, fragmentPosition);
		addTabLayout(this, fragmentPosition);
	}

	public String createActionBarTitle() {
		if (currentProject != null && currentProject.getSceneList() != null && currentProject.getSceneList().size() == 1) {
			return currentSprite.getName();
		} else {
			return currentScene.getName() + ": " + currentSprite.getName();
		}
	}

	Fragment getCurrentFragment() {
		return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_script_activity, menu);
		optionsMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	public void showUndo(boolean visible) {
		if (optionsMenu != null) {
			optionsMenu.findItem(R.id.menu_undo).setVisible(visible);
			if (visible) {
				ProjectManager.getInstance().changedProject(currentProject.getName());
			}
		}
	}

	public void checkForChange() {
		if (optionsMenu != null) {
			if (optionsMenu.findItem(R.id.menu_undo).isVisible()) {
				ProjectManager.getInstance().changedProject(currentProject.getName());
			} else {
				ProjectManager.getInstance().resetChangedFlag(currentProject);
			}
		}
	}

	public void setUndoMenuItemVisibility(boolean isVisible) {
		isUndoMenuItemVisible = isVisible;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (getCurrentFragment() instanceof ScriptFragment) {
			menu.findItem(R.id.comment_in_out).setVisible(true);
			showUndo(isUndoMenuItemVisible);
		} else if (getCurrentFragment() instanceof LookListFragment) {
			showUndo(isUndoMenuItemVisible);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean isDragAndDropActiveInFragment = getCurrentFragment() instanceof ScriptFragment
				&& ((ScriptFragment) getCurrentFragment()).isCurrentlyMoving();

		if (item.getItemId() == android.R.id.home && isDragAndDropActiveInFragment) {
			((ScriptFragment) getCurrentFragment()).highlightMovingItem();
			return true;
		}

		if (item.getItemId() == R.id.menu_undo && getCurrentFragment() instanceof LookListFragment) {
			setUndoMenuItemVisibility(false);
			showUndo(isUndoMenuItemVisible);
			Fragment fragment = getCurrentFragment();
			if (fragment instanceof LookListFragment && !((LookListFragment) fragment).undo() && currentLookData != null) {
				((LookListFragment) fragment).deleteItem(currentLookData);
				currentLookData.dispose();
				currentLookData = null;
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveProject();
		RecentBrickListManager.getInstance().saveRecentBrickList();
	}

	@Override
	public void onBackPressed() {
		saveProject();

		Fragment currentFragment = getCurrentFragment();

		if (currentFragment instanceof ScriptFragment) {
			if (((ScriptFragment) currentFragment).isCurrentlyMoving()) {
				((ScriptFragment) currentFragment).cancelMove();
				return;
			}
			if (((ScriptFragment) currentFragment).isFinderOpen()) {
				((ScriptFragment) currentFragment).closeFinder();
				return;
			}
			if (((ScriptFragment) currentFragment).isCurrentlyHighlighted()) {
				((ScriptFragment) currentFragment).cancelHighlighting();
				return;
			}
		} else if (currentFragment instanceof FormulaEditorFragment) {
			((FormulaEditorFragment) currentFragment).exitFormulaEditorFragment();
			return;
		} else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

			if (currentFragment instanceof BrickCategoryFragment) {
				SnackbarUtil.showHintSnackbar(this, R.string.hint_scripts);
			} else if (currentFragment instanceof AddBrickFragment) {
				SnackbarUtil.showHintSnackbar(this, R.string.hint_category);
			}

			getSupportFragmentManager().popBackStack();
			return;
		}
		super.onBackPressed();
	}

	private void saveProject() {
		currentProject = ProjectManager.getInstance().getCurrentProject();
		new ProjectSaver(currentProject, getApplicationContext()).saveProjectAsync();
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
			if (SettingsFragment.isCastSharedPreferenceEnabled(this)
					&& projectManager.getCurrentProject().isCastProject()
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
			case SPRITE_FILE:
				uri = data.getData();
				addSpriteFromUri(uri, JPEG_IMAGE_EXTENSION);
				break;
			case SPRITE_CAMERA:
				uri = new ImportFromCameraLauncher(this).getCacheCameraUri();
				addSpriteFromUri(uri, JPEG_IMAGE_EXTENSION);
				break;
			case BACKGROUND_POCKET_PAINT:
				uri = new ImportFromPocketPaintLauncher(this).getPocketPaintCacheUri();
				addBackgroundFromUri(uri);
				break;
			case BACKGROUND_LIBRARY:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				addBackgroundFromUri(uri);
				break;
			case BACKGROUND_FILE:
				uri = data.getData();
				addBackgroundFromUri(uri, JPEG_IMAGE_EXTENSION);
				break;
			case BACKGROUND_CAMERA:
				uri = new ImportFromCameraLauncher(this).getCacheCameraUri();
				addBackgroundFromUri(uri, JPEG_IMAGE_EXTENSION);
				break;
			case LOOK_POCKET_PAINT:
				uri = new ImportFromPocketPaintLauncher(this).getPocketPaintCacheUri();
				addLookFromUri(uri);
				setUndoMenuItemVisibility(true);
				break;
			case LOOK_LIBRARY:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				addLookFromUri(uri);
				setUndoMenuItemVisibility(true);
				break;
			case LOOK_FILE:
				uri = data.getData();
				addLookFromUri(uri, JPEG_IMAGE_EXTENSION);
				setUndoMenuItemVisibility(true);
				break;
			case LOOK_CAMERA:
				uri = new ImportFromCameraLauncher(this).getCacheCameraUri();
				addLookFromUri(uri, JPEG_IMAGE_EXTENSION);
				setUndoMenuItemVisibility(true);
				break;
			case SOUND_RECORD:
			case SOUND_FILE:
				uri = data.getData();
				addSoundFromUri(uri);
				break;
			case SOUND_LIBRARY:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				addSoundFromUri(uri);
				break;
			case REQUEST_CODE_VISUAL_PLACEMENT:
				Bundle extras = data.getExtras();
				if (extras == null) {
					return;
				}

				int xCoordinate = extras.getInt(X_COORDINATE_BUNDLE_ARGUMENT);
				int yCoordinate = extras.getInt(Y_COORDINATE_BUNDLE_ARGUMENT);
				int brickHash = extras.getInt(EXTRA_BRICK_HASH);

				Fragment fragment = getCurrentFragment();
				Brick brick = null;

				if (fragment instanceof ScriptFragment) {
					brick = ((ScriptFragment) fragment).findBrickByHash(brickHash);
				} else if (fragment instanceof FormulaEditorFragment) {
					brick = ((FormulaEditorFragment) fragment).getFormulaBrick();
				}

				if (brick != null) {
					((VisualPlacementBrick) brick).setCoordinates(xCoordinate, yCoordinate);
					if (fragment instanceof FormulaEditorFragment) {
						((FormulaEditorFragment) fragment).updateFragmentAfterVisualPlacement();
					}
				}

				setUndoMenuItemVisibility(extras.getBoolean(CHANGED_COORDINATES));

				break;
		}
	}

	public void registerOnNewSpriteListener(NewItemInterface<Sprite> listener) {
		onNewSpriteListener = listener;
	}

	public void registerOnNewLookListener(NewItemInterface<LookData> listener) {
		onNewLookListener = listener;
	}

	public void registerOnNewSoundListener(NewItemInterface<SoundInfo> listener) {
		onNewSoundListener = listener;
	}

	private void addSpriteFromUri(final Uri uri) {
		addSpriteFromUri(uri, DEFAULT_IMAGE_EXTENSION);
	}

	private void addSpriteFromUri(final Uri uri, String imageExtension) {
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

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this);

		builder.setHint(getString(R.string.sprite_name_label))
				.setText(lookDataName)
				.setTextWatcher(new DuplicateInputTextWatcher<>(currentScene.getSpriteList()))
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					Sprite sprite = new Sprite(textInput);
					currentScene.addSprite(sprite);
					try {
						File imageDirectory = new File(currentScene.getDirectory(), IMAGE_DIRECTORY_NAME);
						File file = StorageOperations
								.copyUriToDir(getContentResolver(), uri, imageDirectory, lookFileName);
						Utils.removeExifData(imageDirectory, lookFileName);
						LookData lookData = new LookData(textInput, file);
						sprite.getLookList().add(lookData);
						lookData.getCollisionInformation().calculate();
					} catch (IOException e) {
						Log.e(TAG, Log.getStackTraceString(e));
					}
					if (onNewSpriteListener != null) {
						onNewSpriteListener.addItem(sprite);
						Fragment currentFragment = getCurrentFragment();
						if (currentFragment instanceof ScriptFragment) {
							((ScriptFragment) currentFragment).notifyDataSetChanged();
						}
					}
				});

		builder.setTitle(R.string.new_sprite_dialog_title)
				.setNegativeButton(R.string.cancel, (dialog, which) -> {
					try {
						if (MEDIA_LIBRARY_CACHE_DIRECTORY.exists()) {
							StorageOperations.deleteDir(MEDIA_LIBRARY_CACHE_DIRECTORY);
						}
					} catch (IOException e) {
						Log.e(TAG, Log.getStackTraceString(e));
					}
				})
				.show();
	}

	private void addBackgroundFromUri(Uri uri) {
		addBackgroundFromUri(uri, DEFAULT_IMAGE_EXTENSION);
	}

	private void addBackgroundFromUri(Uri uri, String imageExtension) {
		String resolvedFileName = StorageOperations.resolveFileName(getContentResolver(), uri);
		String lookDataName;
		String lookFileName;

		boolean useSpriteName = resolvedFileName == null
				|| StorageOperations.getSanitizedFileName(resolvedFileName).equals(TMP_IMAGE_FILE_NAME);

		if (useSpriteName) {
			lookDataName = currentSprite.getName();
			lookFileName = lookDataName + imageExtension;
		} else {
			lookDataName = StorageOperations.getSanitizedFileName(resolvedFileName);
			lookFileName = resolvedFileName;
		}

		lookDataName = new UniqueNameProvider().getUniqueNameInNameables(lookDataName, currentSprite.getLookList());

		try {
			File imageDirectory = new File(currentScene.getDirectory(), IMAGE_DIRECTORY_NAME);
			File file = StorageOperations.copyUriToDir(getContentResolver(), uri, imageDirectory,
					lookFileName);
			Utils.removeExifData(imageDirectory, lookFileName);
			LookData look = new LookData(lookDataName, file);
			currentSprite.getLookList().add(look);
			look.getCollisionInformation().calculate();
			if (onNewLookListener != null) {
				onNewLookListener.addItem(look);
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private void addLookFromUri(Uri uri) {
		addLookFromUri(uri, DEFAULT_IMAGE_EXTENSION);
	}

	private void addLookFromUri(Uri uri, String imageExtension) {
		String resolvedFileName = StorageOperations.resolveFileName(getContentResolver(), uri);
		String lookDataName;
		String lookFileName;

		boolean useSpriteName = resolvedFileName == null
				|| StorageOperations.getSanitizedFileName(resolvedFileName).equals(TMP_IMAGE_FILE_NAME);

		if (useSpriteName) {
			lookDataName = currentSprite.getName();
			lookFileName = lookDataName + imageExtension;
		} else {
			lookDataName = StorageOperations.getSanitizedFileName(resolvedFileName);
			lookFileName = resolvedFileName;
		}

		lookDataName = new UniqueNameProvider().getUniqueNameInNameables(lookDataName, currentSprite.getLookList());

		try {
			File imageDirectory = new File(currentScene.getDirectory(), IMAGE_DIRECTORY_NAME);
			File file = StorageOperations.copyUriToDir(getContentResolver(), uri, imageDirectory, lookFileName);
			Utils.removeExifData(imageDirectory, lookFileName);
			LookData look = new LookData(lookDataName, file);
			this.currentLookData = look;
			currentSprite.getLookList().add(look);
			look.getCollisionInformation().calculate();
			if (onNewLookListener != null) {
				onNewLookListener.addItem(look);
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private void addSoundFromUri(Uri uri) {
		String resolvedFileName = StorageOperations.resolveFileName(getContentResolver(), uri);
		String soundInfoName;
		String soundFileName;

		boolean useSpriteName = resolvedFileName == null;

		if (useSpriteName) {
			soundInfoName = currentSprite.getName();
			soundFileName = soundInfoName + DEFAULT_SOUND_EXTENSION;
		} else {
			soundInfoName = StorageOperations.getSanitizedFileName(resolvedFileName);
			soundFileName = resolvedFileName;
		}

		soundInfoName = new UniqueNameProvider().getUniqueNameInNameables(soundInfoName, currentSprite.getSoundList());

		try {
			File soundDirectory = new File(currentScene.getDirectory(), SOUND_DIRECTORY_NAME);

			File file = StorageOperations.copyUriToDir(getContentResolver(), uri, soundDirectory, soundFileName);
			SoundInfo sound = new SoundInfo(soundInfoName, file);
			currentSprite.getSoundList().add(sound);
			if (onNewSoundListener != null) {
				onNewSoundListener.addItem(sound);
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public void handleAiAssistButton(View view) {
		Log.d(TAG, "Here a Flutter module will be called in the future.");
	}

	public void handleAddButton(View view) {
		if (getCurrentFragment() instanceof ScriptFragment) {
			((ScriptFragment) getCurrentFragment()).handleAddButton();
			return;
		}
		if (getCurrentFragment() instanceof CatblocksScriptFragment) {
			((CatblocksScriptFragment) getCurrentFragment()).handleAddButton();
			return;
		}
		if (getCurrentFragment() instanceof DataListFragment) {
			handleAddUserDataButton();
			return;
		}
		if (getCurrentFragment() instanceof LookListFragment) {
			handleAddLookButton();
			return;
		}
		if (getCurrentFragment() instanceof SoundListFragment) {
			handleAddSoundButton();
		}
		if (getCurrentFragment() instanceof ListSelectorFragment) {
			handleAddUserListButton();
		}
	}

	public void handleAddSpriteButton() {
		View root = View.inflate(this, R.layout.dialog_new_look, null);

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

	public void handleAddBackgroundButton() {
		View root = View.inflate(this, R.layout.dialog_new_look, null);

		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_look_dialog_title)
				.setView(root)
				.create();

		String mediaLibraryUrl;

		if (projectManager.isCurrentProjectLandscapeMode()) {
			mediaLibraryUrl = LIBRARY_BACKGROUNDS_URL_LANDSCAPE;
		} else {
			mediaLibraryUrl = LIBRARY_BACKGROUNDS_URL_PORTRAIT;
		}

		root.findViewById(R.id.dialog_new_look_paintroid).setOnClickListener(view -> {
			new ImportFromPocketPaintLauncher(this)
					.startActivityForResult(BACKGROUND_POCKET_PAINT);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_media_library).setOnClickListener(view -> {
			new ImportFormMediaLibraryLauncher(this, mediaLibraryUrl)
					.startActivityForResult(BACKGROUND_LIBRARY);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_gallery).setOnClickListener(view -> {
			new ImportFromFileLauncher(this, "image/*", getString(R.string.select_look_from_gallery))
					.startActivityForResult(BACKGROUND_FILE);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_camera).setOnClickListener(view -> {
			new ImportFromCameraLauncher(this)
					.startActivityForResult(BACKGROUND_CAMERA);
			alertDialog.dismiss();
		});

		alertDialog.show();
	}

	public void handleAddLookButton() {
		View root = View.inflate(this, R.layout.dialog_new_look, null);

		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_look_dialog_title)
				.setView(root)
				.create();

		String mediaLibraryUrl;

		if (currentSprite.equals(currentScene.getBackgroundSprite())) {
			if (projectManager.isCurrentProjectLandscapeMode()) {
				mediaLibraryUrl = LIBRARY_BACKGROUNDS_URL_LANDSCAPE;
			} else {
				mediaLibraryUrl = LIBRARY_BACKGROUNDS_URL_PORTRAIT;
			}
		} else {
			mediaLibraryUrl = LIBRARY_LOOKS_URL;
		}

		root.findViewById(R.id.dialog_new_look_paintroid).setOnClickListener(view -> {
			new ImportFromPocketPaintLauncher(this)
					.startActivityForResult(LOOK_POCKET_PAINT);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_media_library).setOnClickListener(view -> {
			new ImportFormMediaLibraryLauncher(this, mediaLibraryUrl)
					.startActivityForResult(LOOK_LIBRARY);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_gallery).setOnClickListener(view -> {
			new ImportFromFileLauncher(this, "image/*", getString(R.string.select_look_from_gallery))
					.startActivityForResult(LOOK_FILE);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_look_camera).setOnClickListener(view -> {
			new ImportFromCameraLauncher(this)
					.startActivityForResult(LOOK_CAMERA);
			alertDialog.dismiss();
		});

		alertDialog.show();
	}

	public void handleAddSoundButton() {
		View root = View.inflate(this, R.layout.dialog_new_sound, null);

		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_sound_dialog_title)
				.setView(root)
				.create();

		root.findViewById(R.id.dialog_new_sound_recorder).setOnClickListener(view -> {
			startActivityForResult(new Intent(this, SoundRecorderActivity.class), SOUND_RECORD);
			alertDialog.dismiss();
		});

		root.findViewById(R.id.dialog_new_sound_media_library).setOnClickListener(view -> {
			new ImportFormMediaLibraryLauncher(this, LIBRARY_SOUNDS_URL)
					.startActivityForResult(SOUND_LIBRARY);
			alertDialog.dismiss();
		});
		root.findViewById(R.id.dialog_new_sound_gallery).setOnClickListener(view -> {
			new ImportFromFileLauncher(this, "audio/*", getString(R.string.sound_select_source))
					.startActivityForResult(SOUND_FILE);
			alertDialog.dismiss();
		});

		if (BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
			root.findViewById(R.id.dialog_new_sound_pocketmusic).setVisibility(View.VISIBLE);
			root.findViewById(R.id.dialog_new_sound_pocketmusic).setOnClickListener(view -> {
				startActivity(new Intent(this, PocketMusicActivity.class));
				alertDialog.dismiss();
			});
		}
		alertDialog.show();
	}

	public void handleAddUserDataButton() {
		View view = View.inflate(this, R.layout.dialog_new_user_data, null);

		CheckBox makeListCheckBox = view.findViewById(R.id.make_list);
		makeListCheckBox.setVisibility(View.VISIBLE);

		RadioButton multiplayerRadioButton = view.findViewById(R.id.multiplayer);
		if (SettingsFragment.isMultiplayerVariablesPreferenceEnabled(getApplicationContext())) {
			multiplayerRadioButton.setVisibility(View.VISIBLE);
			multiplayerRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
				makeListCheckBox.setEnabled(!isChecked);
			});
		}

		RadioButton addToProjectUserDataRadioButton = view.findViewById(R.id.global);

		List<UserData> variables = new ArrayList<>();

		ProjectManager projectManager = ProjectManager.getInstance();
		currentSprite = projectManager.getCurrentSprite();
		currentProject = projectManager.getCurrentProject();

		variables.addAll(currentProject.getUserVariables());
		variables.addAll(currentProject.getMultiplayerVariables());
		variables.addAll(currentSprite.getUserVariables());

		List<UserData> lists = new ArrayList<>();
		lists.addAll(currentProject.getUserLists());
		lists.addAll(currentSprite.getUserLists());

		DuplicateInputTextWatcher<UserData> textWatcher = new DuplicateInputTextWatcher(variables);
		TextInputDialog.Builder builder = new TextInputDialog.Builder(this);
		UniqueNameProvider uniqueVariableNameProvider = builder.createUniqueNameProvider(R.string.default_variable_name);
		UniqueNameProvider uniqueListNameProvider = builder.createUniqueNameProvider(R.string.default_list_name);
		generatedVariableName = uniqueVariableNameProvider.getUniqueName(getString(R.string.default_variable_name), null);
		builder.setTextWatcher(textWatcher)
				.setText(generatedVariableName)
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					boolean addToProjectUserData = addToProjectUserDataRadioButton.isChecked();
					boolean addToMultiplayerData = multiplayerRadioButton.isChecked();

					PreferenceManager.getDefaultSharedPreferences(this).edit()
							.putBoolean(INDEXING_VARIABLE_PREFERENCE_KEY, false).apply();

					if (makeListCheckBox.isChecked()) {
						UserList userList = new UserList(textInput);
						if (addToProjectUserData) {
							currentProject.addUserList(userList);
						} else {
							currentSprite.addUserList(userList);
						}
					} else {
						UserVariable userVariable = new UserVariable(textInput);
						if (addToMultiplayerData) {
							currentProject.addMultiplayerVariable(userVariable);
						} else if (addToProjectUserData) {
							currentProject.addUserVariable(userVariable);
						} else {
							currentSprite.addUserVariable(userVariable);
						}
					}

					if (getCurrentFragment() instanceof DataListFragment) {
						((DataListFragment) getCurrentFragment()).notifyDataSetChanged();
						((DataListFragment) getCurrentFragment()).indexAndSort();
					}
				});

		final AlertDialog alertDialog = builder.setTitle(R.string.formula_editor_variable_dialog_title)
				.setView(view)
				.setNegativeButton(getString(R.string.cancel), null)
				.create();

		makeListCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
			TextInputEditText textInputEditText = alertDialog.findViewById(R.id.input_edit_text);
			String currentName = textInputEditText.getText().toString();
			if (checked) {
				alertDialog.setTitle(getString(R.string.formula_editor_list_dialog_title));
				textWatcher.setOriginalScope(lists);
				if (currentName.equals(generatedVariableName)) {
					generatedVariableName = uniqueListNameProvider.getUniqueName(getString(R.string.default_list_name),
							null);
					textInputEditText.setText(generatedVariableName);
				}
			} else {
				alertDialog.setTitle(getString(R.string.formula_editor_variable_dialog_title));
				textWatcher.setOriginalScope(variables);
				if (currentName.equals(generatedVariableName)) {
					generatedVariableName =
							uniqueVariableNameProvider.getUniqueName(getString(R.string.default_variable_name),
									null);
					textInputEditText.setText(generatedVariableName);
				}
			}
			multiplayerRadioButton.setEnabled(!checked);
		});

		alertDialog.show();
	}

	public void handleAddUserListButton() {
		View view = View.inflate(this, R.layout.dialog_new_user_data, null);
		RadioButton addToProjectUserDataRadioButton = view.findViewById(R.id.global);

		List<UserData> lists = new ArrayList<>();
		lists.addAll(currentProject.getUserLists());
		lists.addAll(currentSprite.getUserLists());

		DuplicateInputTextWatcher<UserData> textWatcher = new DuplicateInputTextWatcher<>(lists);

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this)
				.setTextWatcher(textWatcher)
				.setPositiveButton(getString(R.string.ok), (TextInputDialog.OnClickListener) (dialog, textInput) -> {
					boolean addToProjectUserData = addToProjectUserDataRadioButton.isChecked();

					UserList userList = new UserList(textInput);
					if (addToProjectUserData) {
						currentProject.addUserList(userList);
					} else {
						currentSprite.addUserList(userList);
					}

					if (getCurrentFragment() instanceof ListSelectorFragment) {
						((ListSelectorFragment) getCurrentFragment()).notifyDataSetChanged();
					}
				});

		final AlertDialog alertDialog = builder.setTitle(R.string.formula_editor_list_dialog_title)
				.setView(view)
				.create();

		alertDialog.show();
	}

	public void handlePlayButton(View view) {
		Fragment currentFragment = getCurrentFragment();
		if (currentFragment instanceof ScriptFragment) {
			if (((ScriptFragment) currentFragment).isCurrentlyHighlighted()) {
				((ScriptFragment) currentFragment).cancelHighlighting();
			}
			if (((ScriptFragment) currentFragment).isCurrentlyMoving()) {
				((ScriptFragment) getCurrentFragment()).highlightMovingItem();
				return;
			}
		}

		while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		}
		StageActivity.handlePlayButton(projectManager, this);
	}

	@Nullable
	@Override
	public ActionMode startActionMode(ActionMode.Callback callback) {
		Fragment fragment = getCurrentFragment();
		if (isFragmentWithTablayout(fragment)) {
			removeTabLayout(this);
		}
		return super.startActionMode(callback);
	}

	@Override
	public void onActionModeFinished(ActionMode mode) {
		Fragment fragment = getCurrentFragment();
		if (isFragmentWithTablayout(fragment) && (!(fragment instanceof ScriptFragment) || !((ScriptFragment) fragment).isFinderOpen())) {
			addTabLayout(this, getTabPositionInSpriteActivity(fragment));
		}
		super.onActionModeFinished(mode);
	}

	public void setCurrentSprite(Sprite sprite) {
		currentSprite = sprite;
	}

	public void setCurrentSceneAndSprite(Scene scene, Sprite sprite) {
		this.currentScene = scene;
		this.currentSprite = sprite;
	}

	public void removeTabs() {
		removeTabLayout(this);
	}

	public void addTabs() {
		addTabLayout(this, getTabPositionInSpriteActivity(getCurrentFragment()));
	}
}
