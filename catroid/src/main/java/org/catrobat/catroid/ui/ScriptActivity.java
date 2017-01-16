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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

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
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.controller.OldLookController;
import org.catrobat.catroid.ui.dialogs.NewSceneDialog;
import org.catrobat.catroid.ui.dialogs.PlaySceneDialog;
import org.catrobat.catroid.ui.dragndrop.BrickDragAndDropListView;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.BackPackLookListFragment;
import org.catrobat.catroid.ui.fragment.BackPackScriptListFragment;
import org.catrobat.catroid.ui.fragment.BackPackSoundListFragment;
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

	public static final String ACTION_SPRITES_LIST_CHANGED = "org.catrobat.catroid.SPRITES_LIST_CHANGED";

	public static final String ACTION_BRICK_LIST_CHANGED = "org.catrobat.catroid.BRICK_LIST_CHANGED";
	public static final String ACTION_LOOK_DELETED = "org.catrobat.catroid.LOOK_DELETED";
	public static final String ACTION_LOOK_RENAMED = "org.catrobat.catroid.LOOK_RENAMED";
	public static final String ACTION_LOOKS_LIST_INIT = "org.catrobat.catroid.LOOKS_LIST_INIT";
	public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	public static final String ACTION_SOUND_COPIED = "org.catrobat.catroid.SOUND_COPIED";
	public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";
	public static final String ACTION_SOUNDS_LIST_INIT = "org.catrobat.catroid.SOUNDS_LIST_INIT";
	public static final String ACTION_NFCTAG_RENAMED = "org.catrobat.catroid.NFCTAG_RENAMED";
	public static final String ACTION_VARIABLE_DELETED = "org.catrobat.catroid.VARIABLE_DELETED";
	public static final String ACTION_USERLIST_DELETED = "org.catrobat.catroid.USERLIST_DELETED";
	public static final String ACTION_LOOK_TOUCH_ACTION_UP = "org.catrobat.catroid.LOOK_TOUCH_ACTION_UP";
	public static final String ACTION_SOUND_TOUCH_ACTION_UP = "org.catrobat.catroid.SOUND_TOUCH_ACTION_UP";

	private static final String TAG = ScriptActivity.class.getSimpleName();
	private static int currentFragmentPosition;
	private FragmentManager fragmentManager = getFragmentManager();
	private ScriptFragment scriptFragment = null;
	private LookFragment lookFragment = null;
	private SoundFragment soundListFragment = null;
	private NfcTagFragment nfcTagFragment = null;

	private ScriptActivityFragment currentFragment = null;
	private DeleteModeListener deleteModeListener;
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
		setContentView(R.layout.activity_sprite);
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
			OldLookController.getInstance().switchToScriptFragment(lookFragment, this);
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
		actionBar.setDisplayShowTitleEnabled(true);
		try {
			Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
			Scene scene = ProjectManager.getInstance().getCurrentScene();
			if (sprite != null && scene != null) {
				String title;
				if (ProjectManager.getInstance().getCurrentProject().getSceneList().size() == 1) {
					title = sprite.getName();
				} else {
					title = scene.getName() + ": " + sprite.getName();
				}
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
			case FRAGMENT_SOUNDS:
				if (soundListFragment == null) {
					soundListFragment = new SoundFragment();
					fragmentExists = false;
					currentFragmentTag = SoundFragment.TAG;
				}
				currentFragment = soundListFragment;
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
		if (currentFragment == scriptFragment) {
			menu.findItem(R.id.comment_in_out).setVisible(true);
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

			case R.id.comment_in_out:
				currentFragment.startCommentOutActionMode();
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

	private void showBackPackChooser() {

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

		if (soundListFragment != null && soundListFragment.isVisible() && soundListFragment.onKey(null, keyCode, event)) {
			return true;
		}

		int backStackEntryCount = fragmentManager.getBackStackEntryCount();
		for (int i = backStackEntryCount; i > 0; --i) {
			String backStackEntryName = fragmentManager.getBackStackEntryAt(i - 1).getName();
			if (backStackEntryName != null
					&& (backStackEntryName.equals(LookFragment.TAG) || backStackEntryName.equals(SoundFragment.TAG)
					|| backStackEntryName.equals(BackPackScriptListFragment.TAG) || backStackEntryName.equals(BackPackLookListFragment
					.TAG) || backStackEntryName.equals(BackPackSoundListFragment.TAG) || backStackEntryName.equals(NfcTagFragment.TAG))) {
				fragmentManager.popBackStack();
			} else {
				break;
			}
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && currentFragmentPosition == FRAGMENT_SCRIPTS) {
			if (scriptFragment.getAdapter().getActionMode() == BrickAdapter.ActionModeEnum.BACKPACK) {
				scriptFragment.getAdapter().setActionMode(BrickAdapter.ActionModeEnum.NO_ACTION);
			}
			AddBrickFragment addBrickFragment = (AddBrickFragment) getFragmentManager().findFragmentByTag(AddBrickFragment.ADD_BRICK_FRAGMENT_TAG);
			if (addBrickFragment == null || !addBrickFragment.isVisible()) {
				scriptFragment.setBackpackMenuIsVisible(true);
			}

			BrickDragAndDropListView listView = scriptFragment.getListView();
			if (listView.isCurrentlyDragging()) {
				listView.resetDraggingScreen();

				BrickAdapter adapter = scriptFragment.getAdapter();
				adapter.removeDraggedBrick();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
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
		if (soundListFragment != null && currentFragment != soundListFragment) {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.remove(soundListFragment);
			fragmentTransaction.commit();
			soundListFragment = null;
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

	public ScriptActivityFragment getFragment(int fragmentPosition) {
		ScriptActivityFragment fragment = null;

		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				fragment = scriptFragment;
				break;
			case FRAGMENT_SOUNDS:
				fragment = soundListFragment;
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
			case FRAGMENT_SOUNDS:
				currentFragment = soundListFragment;
				currentFragmentPosition = FRAGMENT_SOUNDS;
				currentFragmentTag = SoundFragment.TAG;
				break;
		}
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
			case FRAGMENT_SOUNDS:
				isSoundFragmentFromPlaySoundBrickNew = true;
				fragmentTransaction.addToBackStack(SoundFragment.TAG);
				if (soundListFragment == null) {
					ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(true);
					soundListFragment = new SoundFragment();
					fragmentTransaction.add(R.id.fragment_container, soundListFragment, SoundFragment.TAG);
				} else {
					ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(true);
					fragmentTransaction.show(soundListFragment);
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
		OldLookController.getInstance().switchToScriptFragment(lookFragment, this);
	}

	public void showEmptyActionModeDialog(String actionMode) {
	}
}

