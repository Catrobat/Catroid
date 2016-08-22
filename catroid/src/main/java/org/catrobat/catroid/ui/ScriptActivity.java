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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.drone.DroneStageActivity;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.adapter.ActionModeActivityAdapterInterface;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.PrototypeBrickAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.dialogs.NewSceneDialog;
import org.catrobat.catroid.ui.dialogs.PlaySceneDialog;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.BackPackLookFragment;
import org.catrobat.catroid.ui.fragment.BackPackScriptFragment;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorCategoryListFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorDataFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;
import org.catrobat.catroid.ui.fragment.ScriptActivityFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.ui.fragment.UserBrickElementEditorFragment;

import java.util.concurrent.locks.Lock;

public class ScriptActivity extends BaseActivity {
	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_LOOKS = 1;
	public static final int FRAGMENT_SOUNDS = 2;
	public static final int FRAGMENT_NFCTAGS = 3;
	public static final int USERBRICKS_PROTOTYPE_VIEW = 4;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";

	public static final String ACTION_SPRITE_RENAMED = "org.catrobat.catroid.SPRITE_RENAMED";
	public static final String ACTION_SPRITES_LIST_INIT = "org.catrobat.catroid.SPRITES_LIST_INIT";
	public static final String ACTION_SPRITES_LIST_CHANGED = "org.catrobat.catroid.SPRITES_LIST_CHANGED";
	public static final String ACTION_SCENE_RENAMED = "org.catrobat.catroid.SCENE_RENAMED";
	public static final String ACTION_SCENE_LIST_INIT = "org.catrobat.catroid.SCENE_LIST_INIT";
	public static final String ACTION_SCENE_LIST_CHANGED = "org.catrobat.catroid.SCENE_LIST_CHANGED";
	public static final String ACTION_SCENE_DELETED = "org.catrobat.catroid.SCENE_DELETED";
	public static final String ACTION_BRICK_LIST_CHANGED = "org.catrobat.catroid.BRICK_LIST_CHANGED";
	public static final String ACTION_LOOK_DELETED = "org.catrobat.catroid.LOOK_DELETED";
	public static final String ACTION_LOOK_RENAMED = "org.catrobat.catroid.LOOK_RENAMED";
	public static final String ACTION_LOOKS_LIST_INIT = "org.catrobat.catroid.LOOKS_LIST_INIT";
	public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	public static final String ACTION_SOUND_COPIED = "org.catrobat.catroid.SOUND_COPIED";
	public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";
	public static final String ACTION_SOUNDS_LIST_INIT = "org.catrobat.catroid.SOUNDS_LIST_INIT";
	public static final String ACTION_NFCTAG_DELETED = "org.catrobat.catroid.NFCTAG_DELETED";
	public static final String ACTION_NFCTAG_COPIED = "org.catrobat.catroid.NFCTAG_COPIED";
	public static final String ACTION_NFCTAG_RENAMED = "org.catrobat.catroid.NFCTAG_RENAMED";
	public static final String ACTION_NFCTAGS_LIST_INIT = "org.catrobat.catroid.NFCTAGS_LIST_INIT";
	public static final String ACTION_VARIABLE_DELETED = "org.catrobat.catroid.VARIABLE_DELETED";
	public static final String ACTION_USERLIST_DELETED = "org.catrobat.catroid.USERLIST_DELETED";
	public static final String ACTION_SCRIPT_GROUP_DELETED = "org.catrobat.catroid.SCRIPTGROUP_DELETED";
	public static final String ACTION_USERBRICK_GROUP_DELETED = "org.catrobat.catroid.USERBRICKGROUP_DELETED";
	public static final String ACTION_SPRITE_DELETED = "org.catrobat.catroid.SPRITE_DELETED";
	public static final String ACTION_SPRITE_TOUCH_ACTION_UP = "org.catrobat.catroid.SPRITE_TOUCH_ACTION_UP";
	public static final String ACTION_LOOK_TOUCH_ACTION_UP = "org.catrobat.catroid.LOOK_TOUCH_ACTION_UP";
	public static final String ACTION_NFC_TOUCH_ACTION_UP = "org.catrobat.catroid.NFC_TOUCH_ACTION_UP";
	public static final String ACTION_SOUND_TOUCH_ACTION_UP = "org.catrobat.catroid.SOUND_TOUCH_ACTION_UP";
	public static final String ACTION_SCENE_TOUCH_ACTION_UP = "org.catrobat.catroid.SCENE_TOUCH_ACTION_UP";

	private static final String TAG = ScriptActivity.class.getSimpleName();
	private static int currentFragmentPosition;
	private FragmentManager fragmentManager = getFragmentManager();
	private ScriptFragment scriptFragment = null;
	private LookFragment lookFragment = null;
	private SoundFragment soundFragment = null;
	private NfcTagFragment nfcTagFragment = null;

	private ScriptActivityFragment currentFragment = null;
	private DeleteModeListener deleteModeListener;
	private BackPackModeListener backPackModeListener;
	private String currentFragmentTag;

	private Lock viewSwitchLock = new ViewSwitchLock();

	private boolean isSoundFragmentFromPlaySoundBrickNew = false;
	private boolean isSoundFragmentHandleAddButtonHandled = false;
	private boolean isLookFragmentFromSetLookBrickNew = false;
	private boolean isLookFragmentHandleAddButtonHandled = false;
	private boolean isNfcTagFragmentFromWhenNfcTagBrickNew = false;
	private boolean isNfcTagFragmentHandleAddButtonHandled = false;

	private ImageButton buttonAdd;
	private boolean switchToScriptFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_script);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		currentFragmentPosition = FRAGMENT_SCRIPTS;

		if (savedInstanceState == null) {
			Bundle bundle = this.getIntent().getExtras();

			if (bundle != null) {
				currentFragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);
			}
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		updateCurrentFragment(currentFragmentPosition, fragmentTransaction);
		fragmentTransaction.commit();

		setupActionBar();
		setupBottomBar();

		buttonAdd = (ImageButton) findViewById(R.id.button_add);

		if (switchToScriptFragment) {
			LookController.getInstance().switchToScriptFragment(lookFragment, this);
			switchToScriptFragment = false;
		}
	}

	private void setupBottomBar() {
		BottomBar.showBottomBar(this);
		BottomBar.showAddButton(this);
		BottomBar.showPlayButton(this);
		updateHandleAddButtonClickListener();
	}

	public void setupActionBar() {
		final ActionBar actionBar = getActionBar();
		//actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		try {
			Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
			Scene scene = ProjectManager.getInstance().getCurrentScene();
			if (sprite != null && scene != null) {
				String title = scene.getName() + ": " + sprite.getName();
				actionBar.setTitle(title);
			}
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, Log.getStackTraceString(nullPointerException));
			finish();
		}
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupActionBar();
		setupBottomBar();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// needed for NFC
		Log.d("ScriptActivity", "onNewIntent");
		//setIntent(intent);
		if (nfcTagFragment != null && currentFragment == nfcTagFragment) {
			nfcTagFragment.onNewIntent(intent);
		}
	}

	public void updateHandleAddButtonClickListener() {
		buttonAdd = (ImageButton) findViewById(R.id.button_add);
		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				handleAddButton(view);
			}
		});
	}

	private void updateCurrentFragment(int fragmentPosition, FragmentTransaction fragmentTransaction) {
		boolean fragmentExists = true;
		currentFragmentPosition = fragmentPosition;

		switch (currentFragmentPosition) {
			case FRAGMENT_SCRIPTS:
				if (scriptFragment == null) {
					scriptFragment = new ScriptFragment();
					fragmentExists = false;
					currentFragmentTag = ScriptFragment.TAG;
				}
				currentFragment = scriptFragment;
				break;
			case FRAGMENT_LOOKS:
				if (lookFragment == null) {
					lookFragment = new LookFragment();
					fragmentExists = false;
					currentFragmentTag = LookFragment.TAG;
				}
				currentFragment = lookFragment;
				break;
			case FRAGMENT_SOUNDS:
				if (soundFragment == null) {
					soundFragment = new SoundFragment();
					fragmentExists = false;
					currentFragmentTag = SoundFragment.TAG;
				}
				currentFragment = soundFragment;
				break;
			case FRAGMENT_NFCTAGS:
				if (nfcTagFragment == null) {
					nfcTagFragment = new NfcTagFragment();
					fragmentExists = false;
					currentFragmentTag = NfcTagFragment.TAG;
				}
				currentFragment = nfcTagFragment;
				break;
		}

		updateHandleAddButtonClickListener();

		if (fragmentExists) {
			fragmentTransaction.show(currentFragment);
		} else {
			fragmentTransaction.add(R.id.fragment_container, currentFragment, currentFragmentTag);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setVolumeControlStream(AudioManager.STREAM_RING);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (currentFragment != null) {
			handleShowDetails(currentFragment.getShowDetails(), menu.findItem(R.id.show_details));
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.menu_script_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (isHoveringActive()) {
			scriptFragment.getListView().animateHoveringBrick();
			return super.onOptionsItemSelected(item);
		}

		if (isFormulaEditorFragmentVisible()) {
			return super.onOptionsItemSelected(item);
		}

		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;

			case R.id.backpack:
				showBackPackChooser();
				break;

			case R.id.show_details:
				handleShowDetails(!currentFragment.getShowDetails(), item);
				break;

			case R.id.copy:
				currentFragment.startCopyActionMode();
				break;

			case R.id.cut:
				break;

			case R.id.insert_below:
				break;

			case R.id.move:
				break;

			case R.id.rename:
				if (currentFragmentPosition != FRAGMENT_SCRIPTS) {
					currentFragment.startRenameActionMode();
				}
				break;

			case R.id.delete:
				if (deleteModeListener != null) {
					deleteModeListener.startDeleteActionMode();
				} else {
					currentFragment.startDeleteActionMode();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isFormulaEditorFragmentVisible() {
		FormulaEditorDataFragment formulaEditorDataFragment = (FormulaEditorDataFragment) getFragmentManager()
				.findFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		FormulaEditorFragment formulaEditorFragment = (FormulaEditorFragment) getFragmentManager()
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

		FormulaEditorCategoryListFragment formulaEditorObjectFragment = (FormulaEditorCategoryListFragment)
				getFragmentManager()
						.findFragmentByTag(FormulaEditorCategoryListFragment.OBJECT_TAG);
		FormulaEditorCategoryListFragment formulaEditorFunctionFragment = (FormulaEditorCategoryListFragment)
				getFragmentManager()
						.findFragmentByTag(FormulaEditorCategoryListFragment.FUNCTION_TAG);

		FormulaEditorCategoryListFragment formulaEditorLogicFragment = (FormulaEditorCategoryListFragment)
				getFragmentManager()
						.findFragmentByTag(FormulaEditorCategoryListFragment.LOGIC_TAG);

		FormulaEditorCategoryListFragment formulaEditorSensorFragment = (FormulaEditorCategoryListFragment)
				getFragmentManager()
						.findFragmentByTag(FormulaEditorCategoryListFragment.SENSOR_TAG);

		if (formulaEditorFragment != null && formulaEditorFragment.isVisible()
				|| formulaEditorObjectFragment != null && formulaEditorObjectFragment.isVisible()
				|| formulaEditorFunctionFragment != null && formulaEditorFunctionFragment.isVisible()
				|| formulaEditorLogicFragment != null && formulaEditorLogicFragment.isVisible()
				|| formulaEditorSensorFragment != null && formulaEditorSensorFragment.isVisible()
				|| formulaEditorDataFragment != null && formulaEditorDataFragment.isVisible()) {
			return true;
		}
		return false;
	}

	private void openBackPack() {
		Intent intent = new Intent(currentFragment.getActivity(), BackPackActivity.class);
		if (currentFragment == lookFragment) {
			intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, FRAGMENT_LOOKS);
		} else if (currentFragment == soundFragment) {
			intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, FRAGMENT_SOUNDS);
		} else if (currentFragment == scriptFragment) {
			if (scriptFragment.isInUserBrickOverview()) {
				intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, USERBRICKS_PROTOTYPE_VIEW);
			} else {
				intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);
			}
		}
		startActivity(intent);
	}

	private void showBackPackChooser() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		CharSequence[] items;
		int numberOfItemsInBackpack = 0;
		int numberOfItemsInAdapter = 0;

		switch (currentFragmentPosition) {
			case FRAGMENT_SCRIPTS:
				if (scriptFragment.isInUserBrickOverview()) {
					numberOfItemsInBackpack = BackPackListManager.getInstance().getBackPackedUserBricks().size();
					Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
					numberOfItemsInAdapter = currentSprite.getUserBrickList().size();
				} else {
					numberOfItemsInBackpack = BackPackListManager.getInstance().getBackPackedScripts().size();
					numberOfItemsInAdapter = ((ScriptFragment) currentFragment).getAdapter().getCount();
				}
				break;
			case FRAGMENT_LOOKS:
				numberOfItemsInBackpack = BackPackListManager.getInstance().getBackPackedLooks().size();
				numberOfItemsInAdapter = currentFragment.getListAdapter().getCount();
				break;
			case FRAGMENT_SOUNDS:
				numberOfItemsInBackpack = BackPackListManager.getInstance().getBackPackedSounds().size();
				numberOfItemsInAdapter = currentFragment.getListAdapter().getCount();
				break;
		}

		if (numberOfItemsInBackpack > 0 && numberOfItemsInAdapter == 0) {
			openBackPack();
		} else if (numberOfItemsInBackpack == 0) {
			if (backPackModeListener != null) {
				backPackModeListener.startBackPackActionMode();
			} else {
				currentFragment.startBackPackActionMode();
			}
		} else {
			items = new CharSequence[] { getString(R.string.packing), getString(R.string.unpack) };

			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						if (backPackModeListener != null) {
							backPackModeListener.startBackPackActionMode();
						} else {
							currentFragment.startBackPackActionMode();
						}
					} else if (which == 1) {
						openBackPack();
					}
					dialog.dismiss();
				}
			});
			builder.setTitle(R.string.backpack_title);
			builder.setCancelable(true);
			builder.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		updateHandleAddButtonClickListener();

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent;
			if (DroneServiceWrapper.checkARDroneAvailability()) {
				intent = new Intent(ScriptActivity.this, DroneStageActivity.class);
			} else {
				intent = new Intent(ScriptActivity.this, StageActivity.class);
			}
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FragmentManager fragmentManager = getFragmentManager();

		for (String tag : FormulaEditorCategoryListFragment.TAGS) {
			FormulaEditorCategoryListFragment fragment = (FormulaEditorCategoryListFragment) fragmentManager.findFragmentByTag(tag);
			if (fragment != null && fragment.isVisible()) {
				return fragment.onKey(null, keyCode, event);
			}
		}

		String tag1 = UserBrickElementEditorFragment.BRICK_DATA_EDITOR_FRAGMENT_TAG;
		UserBrickElementEditorFragment fragment = (UserBrickElementEditorFragment) fragmentManager.findFragmentByTag(tag1);
		if (fragment != null && fragment.isVisible()) {
			return fragment.onKey(null, keyCode, event);
		}

		FormulaEditorDataFragment formulaEditorDataFragment = (FormulaEditorDataFragment) getFragmentManager()
				.findFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		if (formulaEditorDataFragment != null && formulaEditorDataFragment.isVisible()) {
			return formulaEditorDataFragment.onKey(null, keyCode, event);
		}

		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getFragmentManager().findFragmentByTag(
				FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditor != null && formulaEditor.isVisible()) {
			scriptFragment.getAdapter().updateProjectBrickList();
			return formulaEditor.onKey(null, keyCode, event);
		}

		if (soundFragment != null && soundFragment.isVisible() && soundFragment.onKey(null, keyCode, event)) {
			return true;
		}

		if (lookFragment != null && lookFragment.isVisible() && lookFragment.onKey(null, keyCode, event)) {
			return true;
		}

		if (nfcTagFragment != null && nfcTagFragment.isVisible() && nfcTagFragment.onKey(null, keyCode, event)) {
			return true;
		}

		int backStackEntryCount = fragmentManager.getBackStackEntryCount();
		for (int i = backStackEntryCount; i > 0; --i) {
			String backStackEntryName = fragmentManager.getBackStackEntryAt(i - 1).getName();
			if (backStackEntryName != null
					&& (backStackEntryName.equals(LookFragment.TAG) || backStackEntryName.equals(SoundFragment.TAG)
					|| backStackEntryName.equals(BackPackScriptFragment.TAG) || backStackEntryName.equals(BackPackLookFragment
					.TAG) || backStackEntryName.equals(BackPackSoundFragment.TAG) || backStackEntryName.equals(NfcTagFragment.TAG))) {
				fragmentManager.popBackStack();
			} else {
				break;
			}
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && currentFragmentPosition == FRAGMENT_SCRIPTS) {
			if (scriptFragment.getAdapter().isBackPackActionMode()) {
				scriptFragment.getAdapter().setIsBackPackActionMode(false);
			}
			AddBrickFragment addBrickFragment = (AddBrickFragment) getFragmentManager().findFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
			if (addBrickFragment == null || !addBrickFragment.isVisible()) {
				scriptFragment.setBackpackMenuIsVisible(true);
			}

			DragAndDropListView listView = scriptFragment.getListView();
			if (listView.isCurrentlyDragging()) {
				listView.resetDraggingScreen();

				BrickAdapter adapter = scriptFragment.getAdapter();
				adapter.removeDraggedBrick();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if (soundFragment != null && soundFragment.isVisible()) {
				sendBroadcast(new Intent(ScriptActivity.ACTION_SOUNDS_LIST_INIT));
			}

			if (nfcTagFragment != null && nfcTagFragment.isVisible()) {
				sendBroadcast(new Intent(ScriptActivity.ACTION_NFCTAGS_LIST_INIT));
			}

			if (lookFragment != null && lookFragment.isVisible()) {
				sendBroadcast(new Intent(ScriptActivity.ACTION_LOOKS_LIST_INIT));
			}
		}
	}

	public void handleAddButton(View view) {
		if (!viewSwitchLock.tryLock()) {
			return;
		}
		currentFragment.handleAddButton();
	}

	public void handlePlayButton(View view) {
		updateHandleAddButtonClickListener();

		Fragment formulaEditorFragment = fragmentManager
				.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);
		if (formulaEditorFragment != null) {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.remove(formulaEditorFragment);
			fragmentTransaction.commit();
		}
		if (soundFragment != null && currentFragment != soundFragment) {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.remove(soundFragment);
			fragmentTransaction.commit();
			soundFragment = null;
		}
		if (lookFragment != null && currentFragment != lookFragment) {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.remove(lookFragment);
			fragmentTransaction.commit();
			lookFragment = null;
		}
		if (nfcTagFragment != null && currentFragment != nfcTagFragment) {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.remove(nfcTagFragment);
			fragmentTransaction.commit();
			nfcTagFragment = null;
		}
		BroadcastHandler.clearActionMaps();
		if (isHoveringActive()) {
			scriptFragment.getListView().animateHoveringBrick();
		} else {
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
			Fragment previousFragment = getFragmentManager().findFragmentByTag(NewSceneDialog.DIALOG_FRAGMENT_TAG);
			if (previousFragment != null) {
				fragmentTransaction.remove(previousFragment);
			}

			PlaySceneDialog playSceneDialog = new PlaySceneDialog();
			playSceneDialog.show(fragmentTransaction, PlaySceneDialog.DIALOG_FRAGMENT_TAG);
		}
	}

	public void startPreStageActivity() {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting checked items

		FormulaEditorDataFragment formulaEditorDataFragment = (FormulaEditorDataFragment) getFragmentManager()
				.findFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		if (formulaEditorDataFragment != null && formulaEditorDataFragment.isVisible()) {
			ListAdapter adapter = formulaEditorDataFragment.getListAdapter();
			((ActionModeActivityAdapterInterface) adapter).clearCheckedItems();
			return super.dispatchKeyEvent(event);
		}

		AddBrickFragment addBrickFragment = (AddBrickFragment) getFragmentManager()
				.findFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);

		if (addBrickFragment != null && addBrickFragment.isVisible()
				&& addBrickFragment.isActionModeActive()) {
			ListAdapter adapter = addBrickFragment.getListAdapter();
			((PrototypeBrickAdapter) adapter).clearCheckedItems();
			return super.dispatchKeyEvent(event);
		}

		if (currentFragment != null && currentFragment.getActionModeActive()
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			if (scriptFragment != null && scriptFragment.getAdapter() != null && scriptFragment.getAdapter().isBackPackActionMode()) {
				scriptFragment.getAdapter().setIsBackPackActionMode(false);
			}
			ListAdapter adapter;
			if (currentFragment instanceof ScriptFragment) {
				adapter = ((ScriptFragment) currentFragment).getAdapter();
			} else {
				adapter = currentFragment.getListAdapter();
			}
			((ActionModeActivityAdapterInterface) adapter).clearCheckedItems();
		}

		return super.dispatchKeyEvent(event);
	}

	public boolean isHoveringActive() {
		if (currentFragmentPosition == FRAGMENT_SCRIPTS && scriptFragment.getListView().isCurrentlyDragging()) {
			return true;
		}
		return false;
	}

	public void handleShowDetails(boolean showDetails, MenuItem item) {
		currentFragment.setShowDetails(showDetails);

		item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
	}

	public void setDeleteModeListener(DeleteModeListener listener) {
		deleteModeListener = listener;
	}

	public void setBackPackModeListener(BackPackModeListener listener) {
		backPackModeListener = listener;
	}

	public ScriptActivityFragment getFragment(int fragmentPosition) {
		ScriptActivityFragment fragment = null;

		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				fragment = scriptFragment;
				break;
			case FRAGMENT_LOOKS:
				fragment = lookFragment;
				break;
			case FRAGMENT_SOUNDS:
				fragment = soundFragment;
				break;
			case FRAGMENT_NFCTAGS:
				fragment = nfcTagFragment;
				break;
		}
		return fragment;
	}

	public void setCurrentFragment(int fragmentPosition) {

		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				currentFragment = scriptFragment;
				currentFragmentPosition = FRAGMENT_SCRIPTS;
				currentFragmentTag = ScriptFragment.TAG;
				break;
			case FRAGMENT_LOOKS:
				currentFragment = lookFragment;
				currentFragmentPosition = FRAGMENT_LOOKS;
				currentFragmentTag = LookFragment.TAG;
				break;
			case FRAGMENT_SOUNDS:
				currentFragment = soundFragment;
				currentFragmentPosition = FRAGMENT_SOUNDS;
				currentFragmentTag = SoundFragment.TAG;
				break;
			case FRAGMENT_NFCTAGS:
				currentFragment = nfcTagFragment;
				currentFragmentPosition = FRAGMENT_NFCTAGS;
				currentFragmentTag = NfcTagFragment.TAG;
				break;
		}
	}

	public boolean getIsNfcTagFragmentFromWhenNfcBrickNew() {
		return this.isNfcTagFragmentFromWhenNfcTagBrickNew;
	}

	public void setIsNfcTagFragmentFromWhenNfcBrickNewFalse() {
		this.isNfcTagFragmentFromWhenNfcTagBrickNew = false;
	}

	public boolean getIsNfcTagFragmentHandleAddButtonHandled() {
		return this.isNfcTagFragmentHandleAddButtonHandled;
	}

	public void setIsNfcTagFragmentHandleAddButtonHandled(boolean isNfcTagFragmentHandleAddButtonHandled) {
		this.isNfcTagFragmentHandleAddButtonHandled = isNfcTagFragmentHandleAddButtonHandled;
	}

	public boolean getIsSoundFragmentFromPlaySoundBrickNew() {
		return this.isSoundFragmentFromPlaySoundBrickNew;
	}

	public void setIsSoundFragmentFromPlaySoundBrickNewFalse() {
		this.isSoundFragmentFromPlaySoundBrickNew = false;
	}

	public boolean getIsSoundFragmentHandleAddButtonHandled() {
		return this.isSoundFragmentHandleAddButtonHandled;
	}

	public void setIsSoundFragmentHandleAddButtonHandled(boolean isSoundFragmentHandleAddButtonHandled) {
		this.isSoundFragmentHandleAddButtonHandled = isSoundFragmentHandleAddButtonHandled;
	}

	public boolean getIsLookFragmentFromSetLookBrickNew() {
		return this.isLookFragmentFromSetLookBrickNew;
	}

	public void setIsLookFragmentFromSetLookBrickNewFalse() {
		this.isLookFragmentFromSetLookBrickNew = false;
	}

	public boolean getIsLookFragmentHandleAddButtonHandled() {
		return this.isLookFragmentHandleAddButtonHandled;
	}

	public void setIsLookFragmentHandleAddButtonHandled(boolean isLookFragmentHandleAddButtonHandled) {
		this.isLookFragmentHandleAddButtonHandled = isLookFragmentHandleAddButtonHandled;
	}

	public ScriptFragment getScriptFragment() {
		return scriptFragment;
	}

	public void redrawBricks() {
		scriptFragment.getAdapter().notifyDataSetInvalidated();
	}

	public void switchToFragmentFromScriptFragment(int fragmentPosition) {

		ScriptActivityFragment scriptFragment = getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (scriptFragment.isVisible()) {
			fragmentTransaction.hide(scriptFragment);
		}

		switch (fragmentPosition) {
			case FRAGMENT_LOOKS:
				isLookFragmentFromSetLookBrickNew = true;
				fragmentTransaction.addToBackStack(LookFragment.TAG);
				if (lookFragment == null) {
					ProjectManager.getInstance().setComingFromScriptFragmentToLooksFragment(true);
					lookFragment = new LookFragment();
					fragmentTransaction.add(R.id.fragment_container, lookFragment, LookFragment.TAG);
				} else {
					ProjectManager.getInstance().setComingFromScriptFragmentToLooksFragment(true);
					fragmentTransaction.show(lookFragment);
				}
				setCurrentFragment(FRAGMENT_LOOKS);
				break;

			case FRAGMENT_SOUNDS:
				isSoundFragmentFromPlaySoundBrickNew = true;
				fragmentTransaction.addToBackStack(SoundFragment.TAG);
				if (soundFragment == null) {
					ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(true);
					soundFragment = new SoundFragment();
					fragmentTransaction.add(R.id.fragment_container, soundFragment, SoundFragment.TAG);
				} else {
					ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(true);
					fragmentTransaction.show(soundFragment);
				}
				setCurrentFragment(FRAGMENT_SOUNDS);
				break;
			case FRAGMENT_NFCTAGS:
				isNfcTagFragmentFromWhenNfcTagBrickNew = true;

				fragmentTransaction.addToBackStack(NfcTagFragment.TAG);
				if (nfcTagFragment == null) {
					nfcTagFragment = new NfcTagFragment();
					fragmentTransaction.add(R.id.fragment_container, nfcTagFragment, NfcTagFragment.TAG);
				} else {
					fragmentTransaction.show(nfcTagFragment);
				}
				setCurrentFragment(FRAGMENT_NFCTAGS);
				break;
		}

		updateHandleAddButtonClickListener();
		fragmentTransaction.commit();
	}

	public void setSwitchToScriptFragment(boolean switchToScriptFragment) {
		this.switchToScriptFragment = switchToScriptFragment;
	}

	public void switchFromLookToScriptFragment() {
		LookController.getInstance().switchToScriptFragment(lookFragment, this);
	}

	public void showEmptyActionModeDialog(String actionMode) {
		@SuppressLint("InflateParams")
		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_action_mode_empty, null);
		TextView actionModeEmptyText = (TextView) dialogView.findViewById(R.id.dialog_action_mode_emtpy_text);

		if (actionMode.equals(getString(R.string.backpack))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_backpack_and_unpack));
		} else if (actionMode.equals(getString(R.string.delete))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_delete));
		} else if (actionMode.equals(getString(R.string.copy))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_copy));
		} else if (actionMode.equals(getString(R.string.rename))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_rename));
		}

		AlertDialog actionModeEmptyDialog = new AlertDialog.Builder(this).setView(dialogView)
				.setTitle(actionMode)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		actionModeEmptyDialog.setCanceledOnTouchOutside(true);
		actionModeEmptyDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		actionModeEmptyDialog.show();
	}
}

