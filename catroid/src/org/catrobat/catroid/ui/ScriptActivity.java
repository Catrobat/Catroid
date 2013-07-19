/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui;

import java.util.concurrent.locks.Lock;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.ScriptActivityAdapterInterface;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorListFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorVariableListFragment;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.ScriptActivityFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ScriptActivity extends SherlockFragmentActivity {
	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_LOOKS = 1;
	public static final int FRAGMENT_SOUNDS = 2;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";

	public static final String ACTION_SPRITE_RENAMED = "org.catrobat.catroid.SPRITE_RENAMED";
	public static final String ACTION_SPRITES_LIST_INIT = "org.catrobat.catroid.SPRITES_LIST_INIT";
	public static final String ACTION_SPRITES_LIST_CHANGED = "org.catrobat.catroid.SPRITES_LIST_CHANGED";
	public static final String ACTION_NEW_BRICK_ADDED = "org.catrobat.catroid.NEW_BRICK_ADDED";
	public static final String ACTION_BRICK_LIST_CHANGED = "org.catrobat.catroid.BRICK_LIST_CHANGED";
	public static final String ACTION_LOOK_DELETED = "org.catrobat.catroid.LOOK_DELETED";
	public static final String ACTION_LOOK_RENAMED = "org.catrobat.catroid.LOOK_RENAMED";
	public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	public static final String ACTION_SOUND_COPIED = "org.catrobat.catroid.SOUND_COPIED";
	public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";
	public static final String ACTION_VARIABLE_DELETED = "org.catrobat.catroid.VARIABLE_DELETED";

	private FragmentManager fragmentManager = getSupportFragmentManager();

	private ScriptFragment scriptFragment = null;
	private LookFragment lookFragment = null;
	private SoundFragment soundFragment = null;

	private ScriptActivityFragment currentFragment = null;

	private static int currentFragmentPosition;
	private String currentFragmentTag;

	private Lock viewSwitchLock = new ViewSwitchLock();

	private boolean isSoundFragmentFromPlaySoundBrickNew = false;
	private boolean isSoundFragmentHandleAddButtonHandled = false;
	private boolean isLookFragmentFromSetLookBrickNew = false;
	private boolean isLookFragmentHandleAddButtonHandled = false;

	private LinearLayout btn_add = null;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("ScriptActivity", "ScriptActivityOnResume");
	}

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

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				R.layout.activity_script_spinner_item, getResources().getStringArray(
						R.array.script_activity_spinner_items));

		actionBar.setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				if (isHoveringActive()) {
					scriptFragment.getListView().animateHoveringBrick();
					return true;
				}
				if (itemPosition != currentFragmentPosition) {

					if (currentFragmentPosition == FRAGMENT_SOUNDS && soundFragment.isSoundPlaying()) {
						soundFragment.stopSoundAndUpdateList();
					}

					if (currentFragmentPosition == FRAGMENT_SOUNDS && isSoundFragmentFromPlaySoundBrickNew) {
						isSoundFragmentFromPlaySoundBrickNew = false;
						isSoundFragmentHandleAddButtonHandled = false;
					}

					if (currentFragmentPosition == FRAGMENT_LOOKS && isLookFragmentFromSetLookBrickNew) {
						isLookFragmentFromSetLookBrickNew = false;
						isSoundFragmentHandleAddButtonHandled = false;
					}

					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

					fragmentTransaction.hide(getFragment(currentFragmentPosition));
					updateCurrentFragment(itemPosition, fragmentTransaction);

					fragmentTransaction.commit();
				}
				return true;
			}
		});
		actionBar.setSelectedNavigationItem(currentFragmentPosition);
		btn_add = (LinearLayout) findViewById(R.id.button_add);
		updateHandleAddButtonClickListener();
	}

	public void updateHandleAddButtonClickListener() {
		if (btn_add == null) {
			btn_add = (LinearLayout) findViewById(R.id.button_add);
		}
		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleAddButton(v);
			}
		});
	}

	private void updateCurrentFragment(int fragmentPosition, FragmentTransaction fragmentTransaction) {
		boolean fragmentExists = true;
		currentFragmentPosition = fragmentPosition;

		Log.d("CatroidFragmentTag", "ScriptActivity updateCurrentFragment");
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

	// Code from Stackoverflow to reduce memory problems
	// onDestroy() and unbindDrawables() methods taken from
	// http://stackoverflow.com/a/6779067
	@Override
	protected void onDestroy() {
		super.onDestroy();
		setVolumeControlStream(AudioManager.STREAM_RING);
		unbindDrawables(findViewById(R.id.activity_script_root_layout));
		System.gc();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
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

		FormulaEditorVariableListFragment formulaEditorVariableListFragment = (FormulaEditorVariableListFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		if (formulaEditorVariableListFragment != null) {
			if (formulaEditorVariableListFragment.isVisible()) {
				return super.onOptionsItemSelected(item);
			}
		}

		switch (item.getItemId()) {
			case android.R.id.home:
				Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
				mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainMenuIntent);
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
				currentFragment.startDeleteActionMode();
				break;

			case R.id.settings:
				Intent settingsIntent = new Intent(ScriptActivity.this, SettingsActivity.class);
				startActivity(settingsIntent);
				break;

			case R.id.edit_in_pocket_paint:
				currentFragment.startEditInPocketPaintActionMode();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		updateHandleAddButtonClickListener();

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			SensorHandler.startSensorListener(this);
			Intent intent = new Intent(ScriptActivity.this, StageActivity.class);
			startActivityForResult(intent, StageActivity.STAGE_ACTIVITY_FINISH);
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			SensorHandler.stopSensorListeners();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.i("info", "onKeyDown() ScriptActivity.... keyCode: " + keyCode);

		FragmentManager fragmentManager = getSupportFragmentManager();

		for (String tag : FormulaEditorListFragment.TAGS) {
			FormulaEditorListFragment fragment = (FormulaEditorListFragment) fragmentManager.findFragmentByTag(tag);
			if (fragment != null) {
				if (fragment.isVisible()) {
					return fragment.onKey(null, keyCode, event);
				}
			}
		}

		FormulaEditorVariableListFragment formulaEditorVariableListFragment = (FormulaEditorVariableListFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		if (formulaEditorVariableListFragment != null) {
			if (formulaEditorVariableListFragment.isVisible()) {
				return formulaEditorVariableListFragment.onKey(null, keyCode, event);
			}
		}

		FormulaEditorFragment formulaEditor = (FormulaEditorFragment) getSupportFragmentManager().findFragmentByTag(
				FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG);

		if (formulaEditor != null) {
			if (formulaEditor.isVisible()) {
				scriptFragment.getAdapter().updateProjectBrickList();
				return formulaEditor.onKey(null, keyCode, event);
			}
		}

		if (soundFragment != null) {
			if (soundFragment.isVisible()) {
				if (soundFragment.onKey(null, keyCode, event)) {
					return true;
				}
			}
		}

		if (lookFragment != null) {
			if (lookFragment.isVisible()) {
				if (lookFragment.onKey(null, keyCode, event)) {
					return true;
				}
			}
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

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentFragmentPosition == FRAGMENT_SCRIPTS) {
				DragAndDropListView listView = scriptFragment.getListView();
				if (listView.isCurrentlyDragging()) {
					listView.resetDraggingScreen();

					BrickAdapter adapter = scriptFragment.getAdapter();
					adapter.removeDraggedBrick();
					return true;
				}
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
		if (isHoveringActive()) {
			scriptFragment.getListView().animateHoveringBrick();
		} else {
			if (!viewSwitchLock.tryLock()) {
				return;
			}
			ProjectManager.INSTANCE.getCurrentProject().getUserVariables().resetAllUserVariables();
			Intent intent = new Intent(this, PreStageActivity.class);
			startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting checked items

		FormulaEditorVariableListFragment formulaEditorVariableListFragment = (FormulaEditorVariableListFragment) getSupportFragmentManager()
				.findFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);

		if (formulaEditorVariableListFragment != null) {
			if (formulaEditorVariableListFragment.isVisible()) {
				ListAdapter adapter = formulaEditorVariableListFragment.getListAdapter();
				((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
				return super.dispatchKeyEvent(event);
			}
		}

		if (currentFragment != null && currentFragment.getActionModeActive()) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
				ListAdapter adapter = null;
				if (currentFragment instanceof ScriptFragment) {
					adapter = ((ScriptFragment) currentFragment).getAdapter();
				} else {
					adapter = currentFragment.getListAdapter();
				}
				((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
			}
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

		String menuItemText = "";
		if (showDetails) {
			menuItemText = getString(R.string.hide_details);
		} else {
			menuItemText = getString(R.string.show_details);
		}
		item.setTitle(menuItemText);
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

	public void switchToFragmentFromScriptFragment(int fragmentPosition) {
		ActionBar actionBar = getSupportActionBar();

		ScriptActivityFragment scriptFragment = getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (scriptFragment.isVisible()) {
			fragmentTransaction.hide(scriptFragment);
		}

		switch (fragmentPosition) {
			case FRAGMENT_LOOKS:
				actionBar.setSelectedNavigationItem(ScriptActivity.FRAGMENT_LOOKS);
				isLookFragmentFromSetLookBrickNew = true;

				fragmentTransaction.addToBackStack(LookFragment.TAG);
				if (lookFragment == null) {
					lookFragment = new LookFragment();
					fragmentTransaction.add(R.id.script_fragment_container, lookFragment, LookFragment.TAG);
				} else {
					fragmentTransaction.show(lookFragment);
				}
				setCurrentFragment(FRAGMENT_LOOKS);
				break;

			case FRAGMENT_SOUNDS:
				actionBar.setSelectedNavigationItem(ScriptActivity.FRAGMENT_SOUNDS);
				isSoundFragmentFromPlaySoundBrickNew = true;

				fragmentTransaction.addToBackStack(SoundFragment.TAG);
				if (soundFragment == null) {
					soundFragment = new SoundFragment();
					fragmentTransaction.add(R.id.script_fragment_container, soundFragment, SoundFragment.TAG);
				} else {
					fragmentTransaction.show(soundFragment);
				}
				setCurrentFragment(FRAGMENT_SOUNDS);
				break;
		}

		updateHandleAddButtonClickListener();
		fragmentTransaction.commit();
	}
}
