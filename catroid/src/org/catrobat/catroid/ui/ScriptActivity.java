/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastHandler;
import org.catrobat.catroid.drone.DroneInitializer;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.ScriptActivityAdapterInterface;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.FormulaEditorDataFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorListFragment;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.ScriptActivityFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.ui.fragment.UserBrickDataEditorFragment;

import java.util.concurrent.locks.Lock;

public class ScriptActivity extends BaseActivity {
	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_LOOKS = 1;
	public static final int FRAGMENT_SOUNDS = 2;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";

	public static final String ACTION_SPRITE_RENAMED = "org.catrobat.catroid.SPRITE_RENAMED";
	public static final String ACTION_SPRITES_LIST_INIT = "org.catrobat.catroid.SPRITES_LIST_INIT";
	public static final String ACTION_SPRITES_LIST_CHANGED = "org.catrobat.catroid.SPRITES_LIST_CHANGED";
	public static final String ACTION_BRICK_LIST_CHANGED = "org.catrobat.catroid.BRICK_LIST_CHANGED";
	public static final String ACTION_LOOK_DELETED = "org.catrobat.catroid.LOOK_DELETED";
	public static final String ACTION_LOOK_RENAMED = "org.catrobat.catroid.LOOK_RENAMED";
	public static final String ACTION_LOOKS_LIST_INIT = "org.catrobat.catroid.LOOKS_LIST_INIT";
	public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	public static final String ACTION_SOUND_COPIED = "org.catrobat.catroid.SOUND_COPIED";
	public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";
	public static final String ACTION_SOUNDS_LIST_INIT = "org.catrobat.catroid.SOUNDS_LIST_INIT";
	public static final String ACTION_VARIABLE_DELETED = "org.catrobat.catroid.VARIABLE_DELETED";
	public static final String ACTION_USERLIST_DELETED = "org.catrobat.catroid.USERLIST_DELETED";

	private static final String TAG = ScriptActivity.class.getSimpleName();
	private static int currentFragmentPosition;
	private FragmentManager fragmentManager = getSupportFragmentManager();
	private ScriptFragment scriptFragment = null;
	private LookFragment lookFragment = null;
	private SoundFragment soundFragment = null;
	private ScriptActivityFragment currentFragment = null;
	private DeleteModeListener deleteModeListener;
	private String currentFragmentTag;

	private Lock viewSwitchLock = new ViewSwitchLock();

	private boolean isSoundFragmentFromPlaySoundBrickNew = false;
	private boolean isSoundFragmentHandleAddButtonHandled = false;
	private boolean isLookFragmentFromSetLookBrickNew = false;
	private boolean isLookFragmentHandleAddButtonHandled = false;

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
		updateHandleAddButtonClickListener();
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
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		String currentSprite = null;
		try {
			currentSprite = ProjectManager.getInstance().getCurrentSprite().getName();
		} catch (NullPointerException nullPointerException) {
			Log.e(TAG, Log.getStackTraceString(nullPointerException));
			finish();
		}
		actionBar.setTitle(currentSprite);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupActionBar();
		setupBottomBar();
	}

	public void updateHandleAddButtonClickListener() {
		if (buttonAdd == null) {
			buttonAdd = (ImageButton) findViewById(R.id.button_add);
		}
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
		}

		updateHandleAddButtonClickListener();

		if (fragmentExists) {
			fragmentTransaction.show(currentFragment);
		} else {
			fragmentTransaction.add(R.id.script_fragment_container, currentFragment, currentFragmentTag);
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
		getSupportMenuInflater().inflate(R.menu.menu_script_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (isHoveringActive()) {
			scriptFragment.getListView().animateHoveringBrick();
			return super.onOptionsItemSelected(item);
		}

		FormulaEditorDataFragment formulaEditorDataFragment = (FormulaEditorDataFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		if (formulaEditorDataFragment != null && formulaEditorDataFragment.isVisible()) {
			return super.onOptionsItemSelected(item);
		}

		switch (item.getItemId()) {
			case R.id.backpack:
				currentFragment.startBackPackActionMode();
				break;

			case R.id.show_details:
				handleShowDetails(!currentFragment.getShowDetails(), item);
				break;

			case R.id.copy:
				currentFragment.startCopyActionMode();
				break;

			case R.id.cut:
				break;

			case R.id.unpacking:
				Intent intent = new Intent(currentFragment.getActivity(), BackPackActivity.class);
				intent.putExtra(BackPackActivity.EXTRA_FRAGMENT_POSITION, FRAGMENT_SOUNDS);
				startActivity(intent);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		updateHandleAddButtonClickListener();

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ScriptActivity.this, StageActivity.class);
			DroneInitializer.addDroneSupportExtraToNewIntentIfPresentInOldIntent(data, intent);
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FragmentManager fragmentManager = getSupportFragmentManager();

		for (String tag : FormulaEditorListFragment.TAGS) {
			FormulaEditorListFragment fragment = (FormulaEditorListFragment) fragmentManager.findFragmentByTag(tag);
			if (fragment != null && fragment.isVisible()) {
				return fragment.onKey(null, keyCode, event);
			}
		}

		String tag1 = UserBrickDataEditorFragment.BRICK_DATA_EDITOR_FRAGMENT_TAG;
		UserBrickDataEditorFragment fragment = (UserBrickDataEditorFragment) fragmentManager.findFragmentByTag(tag1);
		if (fragment != null && fragment.isVisible()) {
			return fragment.onKey(null, keyCode, event);
		}

		FormulaEditorDataFragment formulaEditorDataFragment = (FormulaEditorDataFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		if (formulaEditorDataFragment != null && formulaEditorDataFragment.isVisible()) {
			return formulaEditorDataFragment.onKey(null, keyCode, event);
		}

		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getSupportFragmentManager().findFragmentByTag(
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

		int backStackEntryCount = fragmentManager.getBackStackEntryCount();
		for (int i = backStackEntryCount; i > 0; --i) {
			String backStackEntryName = fragmentManager.getBackStackEntryAt(i - 1).getName();
			if (backStackEntryName != null
					&& (backStackEntryName.equals(LookFragment.TAG) || backStackEntryName.equals(SoundFragment.TAG))) {
				fragmentManager.popBackStack();
			} else {
				break;
			}
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && currentFragmentPosition == FRAGMENT_SCRIPTS) {
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
		BroadcastHandler.clearActionMaps();
		if (isHoveringActive()) {
			scriptFragment.getListView().animateHoveringBrick();
		} else {
			if (!viewSwitchLock.tryLock()) {
				return;
			}
			ProjectManager.getInstance().getCurrentProject().getDataContainer().resetAllDataObjects();
			Intent intent = new Intent(this, PreStageActivity.class);
			startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting checked items

		FormulaEditorDataFragment formulaEditorDataFragment = (FormulaEditorDataFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorDataFragment.USER_DATA_TAG);

		if (formulaEditorDataFragment != null && formulaEditorDataFragment.isVisible()) {
			ListAdapter adapter = formulaEditorDataFragment.getListAdapter();
			((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
			return super.dispatchKeyEvent(event);
		}

		if (currentFragment != null && currentFragment.getActionModeActive()
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			ListAdapter adapter = null;
			if (currentFragment instanceof ScriptFragment) {
				adapter = ((ScriptFragment) currentFragment).getAdapter();
			} else {
				adapter = currentFragment.getListAdapter();
			}
			((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
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
		}
	}

	public boolean getIsSoundFragmentFromPlaySoundBrickNew() {
		return this.isSoundFragmentFromPlaySoundBrickNew;
	}

	public void setIsSoundFragmentFromPlaySoundBrickNewFalse() {
		this.isSoundFragmentFromPlaySoundBrickNew = false;
		// TODO quickfix for issue #521 - refactor design (activity and fragment interaction)
		updateHandleAddButtonClickListener();
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
		// TODO quickfix for issue #521 - refactor design (activity and fragment interaction)
		updateHandleAddButtonClickListener();
	}

	public boolean getIsLookFragmentHandleAddButtonHandled() {
		return this.isLookFragmentHandleAddButtonHandled;
	}

	public void setIsLookFragmentHandleAddButtonHandled(boolean isLookFragmentHandleAddButtonHandled) {
		this.isLookFragmentHandleAddButtonHandled = isLookFragmentHandleAddButtonHandled;
	}

	public void setupBrickAdapter(BrickAdapter adapter) {
	}

	public ScriptFragment getScriptFragment() {
		return scriptFragment;
	}

	public void setScriptFragment(ScriptFragment scriptFragment) {
		this.scriptFragment = scriptFragment;
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
					fragmentTransaction.add(R.id.script_fragment_container, lookFragment, LookFragment.TAG);
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
					fragmentTransaction.add(R.id.script_fragment_container, soundFragment, SoundFragment.TAG);
				} else {
					ProjectManager.getInstance().setComingFromScriptFragmentToSoundFragment(true);
					fragmentTransaction.show(soundFragment);
				}
				setCurrentFragment(FRAGMENT_SOUNDS);
				break;
		}

		updateHandleAddButtonClickListener();
		fragmentTransaction.commit();
	}

	public void setSwitchToScriptFragment(boolean switchToScriptFragment) {
		this.switchToScriptFragment = switchToScriptFragment;
	}
}
