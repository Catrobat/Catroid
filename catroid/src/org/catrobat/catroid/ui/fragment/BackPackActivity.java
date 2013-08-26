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
package org.catrobat.catroid.ui.fragment;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.adapter.ScriptActivityAdapterInterface;

import java.util.concurrent.locks.Lock;

public class BackPackActivity extends BaseActivity {
	public static final int FRAGMENT_BACKPACK_SCRIPTS = 0;
	public static final int FRAGMENT_BACKPACK_LOOKS = 1;
	public static final int FRAGMENT_BACKPACK_SOUNDS = 2;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";

	/*
	 * public static final String ACTION_SPRITES_LIST_INIT = "org.catrobat.catroid.SPRITES_LIST_INIT";
	 * public static final String ACTION_SPRITES_LIST_CHANGED = "org.catrobat.catroid.SPRITES_LIST_CHANGED";
	 * public static final String ACTION_BRICK_LIST_CHANGED = "org.catrobat.catroid.BRICK_LIST_CHANGED";
	 * public static final String ACTION_LOOK_DELETED = "org.catrobat.catroid.LOOK_DELETED";
	 * public static final String ACTION_LOOKS_LIST_INIT = "org.catrobat.catroid.LOOKS_LIST_INIT";
	 * public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	 * public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";
	 * public static final String ACTION_SOUNDS_LIST_INIT = "org.catrobat.catroid.SOUNDS_LIST_INIT";
	 * public static final String ACTION_VARIABLE_DELETED = "org.catrobat.catroid.VARIABLE_DELETED";
	 */

	private FragmentManager fragmentManager = getSupportFragmentManager();

	//private ScriptFragment scriptFragment = null;
	//private LookFragment lookFragment = null;
	private BackPackSoundFragment backPackSoundFragment = null;
	private BackPackLookFragment backPackLookFragment = null;
	private BackPackScriptFragment backPackScriptFragment = null;

	private BackPackActivityFragment currentFragment = null;

	private static int currentFragmentPosition;
	private String currentFragmentTag;

	private Lock viewSwitchLock = new ViewSwitchLock();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("TAG", "BackPackActivity-->onCreate()");

		setContentView(R.layout.activity_script);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		currentFragmentPosition = FRAGMENT_BACKPACK_SCRIPTS;

		if (savedInstanceState == null) {
			Bundle bundle = this.getIntent().getExtras();

			if (bundle != null) {
				currentFragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_BACKPACK_SCRIPTS);
			}
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		updateCurrentFragment(currentFragmentPosition, fragmentTransaction);
		fragmentTransaction.commit();

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(currentFragmentPosition);
	}

	private void updateCurrentFragment(int fragmentPosition, FragmentTransaction fragmentTransaction) {
		boolean fragmentExists = true;
		currentFragmentPosition = fragmentPosition;

		switch (currentFragmentPosition) {
			case FRAGMENT_BACKPACK_SCRIPTS:
				if (backPackScriptFragment == null) {
					backPackScriptFragment = new BackPackScriptFragment();
					fragmentExists = false;
					currentFragmentTag = BackPackScriptFragment.TAG;
				}
				currentFragment = backPackScriptFragment;
				break;
			case FRAGMENT_BACKPACK_LOOKS:
				if (backPackLookFragment == null) {
					backPackLookFragment = new BackPackLookFragment();
					fragmentExists = false;
					currentFragmentTag = LookFragment.TAG;
				}
				currentFragment = backPackLookFragment;
				break;
			case FRAGMENT_BACKPACK_SOUNDS:
				if (backPackSoundFragment == null) {
					backPackSoundFragment = new BackPackSoundFragment();
					fragmentExists = false;
					currentFragmentTag = SoundFragment.TAG;
				}
				currentFragment = backPackSoundFragment;
				break;
		}

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

		/*
		 * if (isHoveringActive()) {
		 * backPackScriptFragment.getListView().animateHoveringBrick();
		 * return super.onOptionsItemSelected(item);
		 * }
		 * 
		 * FormulaEditorVariableListFragment formulaEditorVariableListFragment = (FormulaEditorVariableListFragment)
		 * getSupportFragmentManager()
		 * .findFragmentByTag(FormulaEditorVariableListFragment.VARIABLE_TAG);
		 * 
		 * if (formulaEditorVariableListFragment != null) {
		 * if (formulaEditorVariableListFragment.isVisible()) {
		 * return super.onOptionsItemSelected(item);
		 * }
		 * }
		 */

		switch (item.getItemId()) {
			case android.R.id.home:
				Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
				mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainMenuIntent);
				break;

			case R.id.show_details:
				handleShowDetails(!currentFragment.getShowDetails(), item);
				break;

			case R.id.move:
				break;

			case R.id.delete:
				currentFragment.startDeleteActionMode();
				break;

			case R.id.settings:
				Intent settingsIntent = new Intent(BackPackActivity.this, SettingsActivity.class);
				startActivity(settingsIntent);
				break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		FragmentManager fragmentManager = getSupportFragmentManager();

		if (backPackScriptFragment != null) {
			if (backPackScriptFragment.isVisible()) {
				if (backPackScriptFragment.onKey(null, keyCode, event)) {
					return true;
				}
			}
		}

		if (backPackSoundFragment != null) {
			if (backPackSoundFragment.isVisible()) {
				if (backPackSoundFragment.onKey(null, keyCode, event)) {
					return true;
				}
			}
		}

		if (backPackLookFragment != null) {
			if (backPackLookFragment.isVisible()) {
				if (backPackLookFragment.onKey(null, keyCode, event)) {
					return true;
				}
			}
		}

		/*
		 * int backStackEntryCount = fragmentManager.getBackStackEntryCount();
		 * for (int i = backStackEntryCount; i > 0; --i) {
		 * String backStackEntryName = fragmentManager.getBackStackEntryAt(i - 1).getName();
		 * if (backStackEntryName != null
		 * && (backStackEntryName.equals(BackPackLookFragment.TAG) ||
		 * backStackEntryName.equals(BackPackSoundFragment.TAG))) {
		 * fragmentManager.popBackStack();
		 * } else {
		 * break;
		 * }
		 * }
		 * 
		 * if (keyCode == KeyEvent.KEYCODE_BACK) {
		 * if (currentFragmentPosition == FRAGMENT_BACKPACK_SCRIPTS) {
		 * DragAndDropListView listView = backPackScriptFragment.getListView();
		 * if (listView.isCurrentlyDragging()) {
		 * listView.resetDraggingScreen();
		 * 
		 * BrickAdapter adapter = scriptFragment.getAdapter();
		 * adapter.removeDraggedBrick();
		 * return true;
		 * }
		 * }
		 * }
		 */
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * @Override
	 * public void onWindowFocusChanged(boolean hasFocus) {
	 * super.onWindowFocusChanged(hasFocus);
	 * if (hasFocus) {
	 * if (backPackScriptFragment != null) {
	 * if (backPackScriptFragment.isVisible()) {
	 * sendBroadcast(new Intent(BackPackActivity.ACTION_SOUNDS_LIST_INIT));
	 * 
	 * }
	 * }
	 * if (lookFragment != null) {
	 * if (lookFragment.isVisible()) {
	 * sendBroadcast(new Intent(BackPackActivity.ACTION_LOOKS_LIST_INIT));
	 * }
	 * }
	 * 
	 * }
	 * }
	 */

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting checked items

		if (currentFragment != null && currentFragment.getActionModeActive()) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
				ListAdapter adapter = null;
				if (currentFragment instanceof BackPackScriptFragment) {
					adapter = ((BackPackScriptFragment) currentFragment).getAdapter();
				} else {
					adapter = currentFragment.getListAdapter();
				}
				((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
			}
		}

		return super.dispatchKeyEvent(event);
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

	public BackPackActivityFragment getFragment(int fragmentPosition) {
		BackPackActivityFragment fragment = null;

		switch (fragmentPosition) {
			case FRAGMENT_BACKPACK_SCRIPTS:
				fragment = backPackScriptFragment;
				break;
			case FRAGMENT_BACKPACK_LOOKS:
				fragment = backPackLookFragment;
				break;
			case FRAGMENT_BACKPACK_SOUNDS:
				fragment = backPackSoundFragment;
				break;
		}
		return fragment;
	}

	public void setCurrentFragment(int fragmentPosition) {

		switch (fragmentPosition) {
			case FRAGMENT_BACKPACK_SCRIPTS:
				currentFragment = backPackScriptFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_SCRIPTS;
				currentFragmentTag = BackPackScriptFragment.TAG;
				break;
			case FRAGMENT_BACKPACK_LOOKS:
				currentFragment = backPackLookFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_LOOKS;
				currentFragmentTag = BackPackLookFragment.TAG;
				break;
			case FRAGMENT_BACKPACK_SOUNDS:
				currentFragment = backPackSoundFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_SOUNDS;
				currentFragmentTag = BackPackSoundFragment.TAG;
				break;
		}
	}
}
