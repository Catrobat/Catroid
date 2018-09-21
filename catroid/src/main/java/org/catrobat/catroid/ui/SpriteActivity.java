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

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.ardrone.DroneStageActivity;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.pocketmusic.PocketMusicActivity;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.controller.PocketPaintExchangeHandler;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.PlaySceneDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.NewItemTextWatcher;
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.NfcTagListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;

import static org.catrobat.catroid.common.Constants.EXTRA_PICTURE_PATH_POCKET_PAINT;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BACKGROUNDS_URL_LANDSCAPE;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BACKGROUNDS_URL_PORTRAIT;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_LOOKS_URL;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_SOUNDS_URL;
import static org.catrobat.catroid.ui.WebViewActivity.INTENT_PARAMETER_URL;
import static org.catrobat.catroid.ui.WebViewActivity.MEDIA_FILE_PATH;

public class SpriteActivity extends BaseActivity {

	public static final String TAG = SpriteActivity.class.getSimpleName();

	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_LOOKS = 1;
	public static final int FRAGMENT_SOUNDS = 2;
	public static final int FRAGMENT_NFC_TAGS = 3;

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

	public static final String EXTRA_FRAGMENT_POSITION = "fragmentPosition";

	private NewItemInterface<Sprite> onNewSpriteListener;
	private NewItemInterface<LookData> onNewLookListener;
	private NewItemInterface<SoundInfo> onNewSoundListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SettingsFragment.setToChosenLanguage(this);

		setContentView(R.layout.activity_recycler);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		updateActionBarTitle();

		int fragmentPosition = FRAGMENT_SCRIPTS;

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);
		}
		loadFragment(fragmentPosition);
	}

	private void updateActionBarTitle() {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (currentProject.getSceneList().size() == 1) {
			getSupportActionBar().setTitle(currentSprite.getName());
		} else {
			getSupportActionBar().setTitle(currentScene.getName() + ": " + currentSprite.getName());
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
				throw new IllegalArgumentException("Invalid fragmentPosition in Activity.");
		}

		fragmentTransaction.commit();
	}

	private Fragment getCurrentFragment() {
		return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (getCurrentFragment() instanceof NfcTagListFragment) {
			((NfcTagListFragment) getCurrentFragment()).onNewIntent(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_script_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (getCurrentFragment() instanceof ScriptFragment) {
			menu.findItem(R.id.comment_in_out).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (getCurrentFragment() instanceof FormulaEditorFragment) {
			((FormulaEditorFragment) getCurrentFragment()).promptSave();
		} else {
			if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
				getSupportFragmentManager().popBackStack();
			} else {
				super.onBackPressed();
			}
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
			return;
		}

		Uri uri;

		switch (requestCode) {
			case PreStageActivity.REQUEST_RESOURCES_INIT:
				startStage();
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
				uri = Uri.fromFile(new File(DEFAULT_ROOT_DIRECTORY, getString(R.string.default_look_name) + ".jpg"));
				addSpriteFromUri(uri);
				break;
			case BACKGROUND_POCKET_PAINT:
				uri = Uri.fromFile(new File(data.getStringExtra(EXTRA_PICTURE_PATH_POCKET_PAINT)));
				addBackgroundFromUri(uri);
				break;
			case BACKGROUND_LIBRARY:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				addBackgroundFromUri(uri);
				break;
			case BACKGROUND_FILE:
				uri = data.getData();
				addBackgroundFromUri(uri);
				break;
			case BACKGROUND_CAMERA:
				uri = Uri.fromFile(new File(DEFAULT_ROOT_DIRECTORY, getString(R.string.default_look_name) + ".jpg"));
				addBackgroundFromUri(uri);
				break;
			case LOOK_POCKET_PAINT:
				uri = Uri.fromFile(new File(data.getStringExtra(EXTRA_PICTURE_PATH_POCKET_PAINT)));
				addLookFromUri(uri);
				break;
			case LOOK_LIBRARY:
				uri = Uri.fromFile(new File(data.getStringExtra(MEDIA_FILE_PATH)));
				addLookFromUri(uri);
				break;
			case LOOK_FILE:
				uri = data.getData();
				addLookFromUri(uri);
				break;
			case LOOK_CAMERA:
				uri = Uri.fromFile(new File(DEFAULT_ROOT_DIRECTORY, getString(R.string.default_look_name) + ".jpg"));
				addLookFromUri(uri);
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
		}
	}

	public void registerOnNewSpriteListener(NewItemInterface<Sprite> listener) {
		onNewSpriteListener = listener;
	}

	public void unregisterOnNewSpriteListener() {
		onNewSpriteListener = null;
	}

	public void registerOnNewLookListener(NewItemInterface<LookData> listener) {
		onNewLookListener = listener;
	}

	public void unregisterOnNewLookListener() {
		onNewLookListener = null;
	}

	public void registerOnNewSoundListener(NewItemInterface<SoundInfo> listener) {
		onNewSoundListener = listener;
	}

	public void unregisterOnNewSoundListener() {
		onNewSoundListener = null;
	}

	private void addSpriteFromUri(final Uri uri) {
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
						try {
							File file = StorageOperations.copyUriToDir(getContentResolver(), uri, imageDirectory, lookName);
							sprite.getLookList().add(new LookData(lookName, file));
							currentScene.getSpriteList().add(sprite);
						} catch (IOException e) {
							Log.e(TAG, Log.getStackTraceString(e));
						}
						if (onNewSpriteListener != null) {
							onNewSpriteListener.addItem(sprite);
						}
					}
				});

		builder.setTitle(R.string.new_sprite_dialog_title)
				.setNegativeButton(R.string.cancel, null)
				.create()
				.show();
	}

	private void addBackgroundFromUri(Uri uri) {
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = currentScene.getBackgroundSprite();

		File imageDirectory = new File(currentScene.getDirectory(), IMAGE_DIRECTORY_NAME);
		String name = StorageOperations.resolveFileName(getContentResolver(), uri);
		if (name == null) {
			name = getString(R.string.background);
		} else {
			name = StorageOperations.getSanitizedFileName(name);
		}
		name = new UniqueNameProvider().getUniqueNameInNameables(name, currentSprite.getLookList());
		try {
			File file = StorageOperations.copyUriToDir(getContentResolver(), uri, imageDirectory, name);
			LookData look = new LookData(name, file);
			currentSprite.getLookList().add(look);
			if (onNewLookListener != null) {
				onNewLookListener.addItem(look);
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private void addLookFromUri(Uri uri) {
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		File imageDirectory = new File(currentScene.getDirectory(), IMAGE_DIRECTORY_NAME);
		String name = StorageOperations.resolveFileName(getContentResolver(), uri);
		if (name == null) {
			name = getString(R.string.default_look_name);
		} else {
			name = StorageOperations.getSanitizedFileName(name);
		}
		name = new UniqueNameProvider().getUniqueNameInNameables(name, currentSprite.getLookList());
		try {
			File file = StorageOperations.copyUriToDir(getContentResolver(), uri, imageDirectory, name);
			LookData look = new LookData(name, file);
			currentSprite.getLookList().add(look);
			if (onNewLookListener != null) {
				onNewLookListener.addItem(look);
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	private void addSoundFromUri(Uri uri) {
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		File soundDirectory = new File(currentScene.getDirectory(), SOUND_DIRECTORY_NAME);
		String name = StorageOperations.resolveFileName(getContentResolver(), uri);
		if (name == null) {
			name = getString(R.string.default_sound_name);
		} else {
			name = StorageOperations.getSanitizedFileName(name);
		}
		name = new UniqueNameProvider().getUniqueNameInNameables(name, currentSprite.getSoundList());
		try {
			File file = StorageOperations.copyUriToDir(getContentResolver(), uri, soundDirectory, name);
			SoundInfo sound = new SoundInfo(name, file);
			currentSprite.getSoundList().add(sound);
			if (onNewSoundListener != null) {
				onNewSoundListener.addItem(sound);
			}
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public void handleAddButton(View view) {
		if (getCurrentFragment() instanceof ScriptFragment) {
			((ScriptFragment) getCurrentFragment()).handleAddButton();
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
				Intent intent;
				switch (view.getId()) {
					case R.id.dialog_new_look_paintroid:
						intent = PocketPaintExchangeHandler
								.createPocketPaintIntent(getString(R.string.default_look_name));

						startPocketPaintIntent(intent, SPRITE_POCKET_PAINT);
						break;
					case R.id.dialog_new_look_media_library:
						intent = new Intent(SpriteActivity.this, WebViewActivity.class);
						intent.putExtra(INTENT_PARAMETER_URL, LIBRARY_LOOKS_URL);
						startActivityForResult(intent, SPRITE_LIBRARY);
						break;
					case R.id.dialog_new_look_gallery:
						intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
						startActivityForResult(Intent.createChooser(intent, getString(R.string.select_look_from_gallery)), SPRITE_FILE);
						break;
					case R.id.dialog_new_look_camera:
						intent = new Intent(ACTION_IMAGE_CAPTURE);
						Uri uri = Uri.fromFile(new File(DEFAULT_ROOT_DIRECTORY, getString(R.string.default_look_name) + ".jpg"));
						intent.putExtra(EXTRA_OUTPUT, uri);
						Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
						startActivityForResult(chooser, SPRITE_CAMERA);
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

	public void handleAddBackgroundButton() {
		View view = View.inflate(this, R.layout.dialog_new_look, null);

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_look_dialog_title)
				.setView(view)
				.create();

		final String mediaLibraryUrl;

		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
			mediaLibraryUrl = LIBRARY_BACKGROUNDS_URL_LANDSCAPE;
		} else {
			mediaLibraryUrl = LIBRARY_BACKGROUNDS_URL_PORTRAIT;
		}

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent;
				switch (view.getId()) {
					case R.id.dialog_new_look_paintroid:
						intent = PocketPaintExchangeHandler
								.createPocketPaintIntent(getString(R.string.default_look_name));
						startPocketPaintIntent(intent, BACKGROUND_POCKET_PAINT);
						break;
					case R.id.dialog_new_look_media_library:
						intent = new Intent(SpriteActivity.this, WebViewActivity.class);
						intent.putExtra(INTENT_PARAMETER_URL, mediaLibraryUrl);
						startActivityForResult(intent, BACKGROUND_LIBRARY);
						break;
					case R.id.dialog_new_look_gallery:
						intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
						startActivityForResult(Intent.createChooser(intent, getString(R.string.select_look_from_gallery)), BACKGROUND_FILE);
						break;
					case R.id.dialog_new_look_camera:
						intent = new Intent(ACTION_IMAGE_CAPTURE);
						Uri uri = Uri.fromFile(new File(DEFAULT_ROOT_DIRECTORY, getString(R.string.default_look_name) + ".jpg"));
						intent.putExtra(EXTRA_OUTPUT, uri);
						Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
						startActivityForResult(chooser, BACKGROUND_CAMERA);
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

	public void handleAddLookButton() {
		View view = View.inflate(this, R.layout.dialog_new_look, null);

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_look_dialog_title)
				.setView(view)
				.create();

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent;
				switch (view.getId()) {
					case R.id.dialog_new_look_paintroid:
						intent = PocketPaintExchangeHandler
								.createPocketPaintIntent(getString(R.string.default_look_name));
						startPocketPaintIntent(intent, LOOK_POCKET_PAINT);
						break;
					case R.id.dialog_new_look_media_library:
						intent = new Intent(SpriteActivity.this, WebViewActivity.class);
						intent.putExtra(INTENT_PARAMETER_URL, getMediaLibraryUrl());
						startActivityForResult(intent, LOOK_LIBRARY);
						break;
					case R.id.dialog_new_look_gallery:
						intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
						startActivityForResult(Intent.createChooser(intent, getString(R.string.select_look_from_gallery)), LOOK_FILE);
						break;
					case R.id.dialog_new_look_camera:
						intent = new Intent(ACTION_IMAGE_CAPTURE);
						Uri uri = Uri.fromFile(new File(DEFAULT_ROOT_DIRECTORY, getString(R.string.default_look_name) + ".jpg"));
						intent.putExtra(EXTRA_OUTPUT, uri);
						Intent chooser = Intent.createChooser(intent, getString(R.string.select_look_from_camera));
						startActivityForResult(chooser, LOOK_CAMERA);
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

	public void handleAddSoundButton() {
		View view = View.inflate(this, R.layout.dialog_new_sound, null);

		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.new_sound_dialog_title)
				.setView(view)
				.create();

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent;
				switch (view.getId()) {
					case R.id.dialog_new_sound_recorder:
						intent = new Intent(SpriteActivity.this, SoundRecorderActivity.class);
						startActivityForResult(intent, SOUND_RECORD);
						break;
					case R.id.dialog_new_sound_media_library:
						intent = new Intent(SpriteActivity.this, WebViewActivity.class);
						intent.putExtra(INTENT_PARAMETER_URL, LIBRARY_SOUNDS_URL);
						startActivityForResult(intent, SOUND_LIBRARY);
						break;
					case R.id.dialog_new_sound_gallery:
						intent = new Intent(Intent.ACTION_GET_CONTENT).setType("audio/*");
						startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)), SOUND_FILE);
						break;
					case R.id.dialog_new_sound_pocketmusic:
						intent = new Intent(SpriteActivity.this, PocketMusicActivity.class);
						startActivity(intent);
						break;
				}
				alertDialog.dismiss();
			}
		};

		view.findViewById(R.id.dialog_new_sound_recorder).setOnClickListener(onClickListener);
		view.findViewById(R.id.dialog_new_sound_media_library).setOnClickListener(onClickListener);
		view.findViewById(R.id.dialog_new_sound_gallery).setOnClickListener(onClickListener);

		if (BuildConfig.FEATURE_POCKETMUSIC_ENABLED) {
			view.findViewById(R.id.dialog_new_sound_pocketmusic).setVisibility(View.VISIBLE);
			view.findViewById(R.id.dialog_new_sound_pocketmusic).setOnClickListener(onClickListener);
		}
		alertDialog.show();
	}

	public void handleAddUserDataButton() {
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		final Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		final DataContainer dataContainer = currentScene.getDataContainer();

		View view = View.inflate(this, R.layout.dialog_new_user_data, null);
		final CheckBox makeListCheckBox = view.findViewById(R.id.make_list);
		final RadioButton addToProjectUserDataRadioButton = view.findViewById(R.id.global);

		makeListCheckBox.setVisibility(View.VISIBLE);

		final List<UserData> variables = new ArrayList<>();
		variables.addAll(dataContainer.getProjectUserVariables());
		variables.addAll(dataContainer.getSpriteUserVariables(currentSprite));

		final List<UserData> lists = new ArrayList<>();
		lists.addAll(dataContainer.getProjectUserLists());
		lists.addAll(dataContainer.getSpriteUserLists(currentSprite));

		final NewItemTextWatcher<UserData> textWatcher = new NewItemTextWatcher<>(variables);

		TextInputDialog.Builder builder = new TextInputDialog.Builder(this);

		builder.setTextWatcher(textWatcher)
				.setPositiveButton(getString(R.string.ok), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
						boolean addToProjectUserData = addToProjectUserDataRadioButton.isChecked();

						if (makeListCheckBox.isChecked()) {
							UserList userList = new UserList(textInput);
							if (addToProjectUserData) {
								dataContainer.addUserList(userList);
							} else {
								dataContainer.addUserList(currentSprite, userList);
							}
						} else {
							UserVariable userVariable = new UserVariable(textInput);
							if (addToProjectUserData) {
								dataContainer.addUserVariable(userVariable);
							} else {
								dataContainer.addUserVariable(currentSprite, userVariable);
							}
						}

						if (getCurrentFragment() instanceof DataListFragment) {
							((DataListFragment) getCurrentFragment()).notifyDataSetChanged();
						}
					}
				});

		final AlertDialog alertDialog = builder.setTitle(R.string.formula_editor_variable_dialog_title)
				.setView(view)
				.create();

		makeListCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				if (checked) {
					alertDialog.setTitle(getString(R.string.formula_editor_list_dialog_title));
					textWatcher.setScope(lists);
				} else {
					alertDialog.setTitle(getString(R.string.formula_editor_variable_dialog_title));
					textWatcher.setScope(variables);
				}
			}
		});

		alertDialog.show();
	}

	String getMediaLibraryUrl() {
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (currentSprite.equals(currentScene.getBackgroundSprite())) {
			if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
				return LIBRARY_BACKGROUNDS_URL_LANDSCAPE;
			} else {
				return LIBRARY_BACKGROUNDS_URL_PORTRAIT;
			}
		} else {
			return LIBRARY_LOOKS_URL;
		}
	}

	public void handlePlayButton(View view) {
		boolean draggingActive = getCurrentFragment() instanceof ScriptFragment
				&& ((ScriptFragment) getCurrentFragment()).getListView().isCurrentlyDragging();

		if (draggingActive) {
			ScriptFragment fragment = ((ScriptFragment) getCurrentFragment());
			fragment.getListView().animateHoveringBrick();
			return;
		}

		while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		}

		ProjectManager projectManager = ProjectManager.getInstance();
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		Scene defaultScene = projectManager.getCurrentProject().getDefaultScene();

		if (currentScene.getName().equals(defaultScene.getName())) {
			projectManager.setCurrentlyPlayingScene(defaultScene);
			projectManager.setStartScene(defaultScene);
			startPreStage();
		} else {
			new PlaySceneDialog.Builder(this)
					.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startPreStage();
						}
					})
					.create()
					.show();
		}
	}

	void startPreStage() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	void startStage() {
		if (DroneServiceWrapper.checkARDroneAvailability()) {
			startActivity(new Intent(this, DroneStageActivity.class));
		} else {
			startActivity(new Intent(this, StageActivity.class));
		}
	}

	void startPocketPaintIntent(Intent intent, int requestCode) {
		if (PocketPaintExchangeHandler.isPocketPaintInstalled(this, intent)) {
			startActivityForResult(intent, LOOK_POCKET_PAINT);
		} else {
			BroadcastReceiver receiver = PocketPaintExchangeHandler
					.createPocketPaintBroadcastReceiver(this, intent, requestCode);
			PocketPaintExchangeHandler.installPocketPaintAndRegister(receiver, this);
		}
	}
}
