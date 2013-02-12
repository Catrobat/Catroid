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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.ScriptActivityAdapterInterface;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.ScriptActivityFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ScriptActivity extends SherlockFragmentActivity implements ErrorListenerInterface {
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
	public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";

	private ActionBar actionBar;
	private FragmentManager fragmentManager = getSupportFragmentManager();

	private ScriptFragment scriptFragment = null;
	private LookFragment lookFragment = null;
	private SoundFragment soundFragment = null;

	private ScriptActivityFragment currentFragment = null;

	private static int currentFragmentPosition;

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

		actionBar = getSupportActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(
						R.array.script_activity_spinner_items));

		actionBar.setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				if (itemPosition != currentFragmentPosition) {

					if (currentFragmentPosition == FRAGMENT_SOUNDS && soundFragment.isSoundPlaying()) {
						soundFragment.stopSoundAndUpdateList();
					}

					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

					hideFragment(currentFragmentPosition, fragmentTransaction);
					updateCurrentFragment(itemPosition, fragmentTransaction);

					fragmentTransaction.commit();
				}
				return true;
			}
		});
		actionBar.setSelectedNavigationItem(currentFragmentPosition);
	}

	private void hideFragment(int fragment, FragmentTransaction fragmentTransaction) {
		switch (fragment) {
			case FRAGMENT_SCRIPTS:
				fragmentTransaction.hide(scriptFragment);
				break;
			case FRAGMENT_LOOKS:
				fragmentTransaction.hide(lookFragment);
				break;
			case FRAGMENT_SOUNDS:
				fragmentTransaction.hide(soundFragment);
				break;
		}
	}

	private void updateCurrentFragment(int fragmentPosition, FragmentTransaction fragmentTransaction) {
		boolean fragmentDoesNotExist = false;
		currentFragmentPosition = fragmentPosition;

		switch (currentFragmentPosition) {
			case FRAGMENT_SCRIPTS:
				if (scriptFragment == null) {
					scriptFragment = new ScriptFragment();
					fragmentDoesNotExist = true;
				}
				currentFragment = scriptFragment;
				break;
			case FRAGMENT_LOOKS:
				if (lookFragment == null) {
					lookFragment = new LookFragment();
					fragmentDoesNotExist = true;
				}
				currentFragment = lookFragment;
				break;
			case FRAGMENT_SOUNDS:
				if (soundFragment == null) {
					soundFragment = new SoundFragment();
					fragmentDoesNotExist = true;
				}
				currentFragment = soundFragment;
				break;
		}
		if (fragmentDoesNotExist) {
			fragmentTransaction.add(R.id.script_fragment_container, currentFragment);
		} else {
			fragmentTransaction.show(currentFragment);
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
		switch (item.getItemId()) {
			case android.R.id.home:
				if (!isHoveringActive()) {
					Intent intent = new Intent(this, MainMenuActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
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
				Intent intent = new Intent(ScriptActivity.this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent = new Intent(ScriptActivity.this, StageActivity.class);
			startActivityForResult(intent, StageActivity.STAGE_ACTIVITY_FINISH);
		}
		if (requestCode == StageActivity.STAGE_ACTIVITY_FINISH) {
			ProjectManager projectManager = ProjectManager.getInstance();
			int currentSpritePos = projectManager.getCurrentSpritePosition();
			int currentScriptPos = projectManager.getCurrentScriptPosition();
			projectManager.loadProject(projectManager.getCurrentProject().getName(), this, this, false);
			projectManager.setCurrentSpriteWithPosition(currentSpritePos);
			projectManager.setCurrentScriptWithPosition(currentScriptPos);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentFragmentPosition == FRAGMENT_SCRIPTS) {
				DragAndDropListView listView = ((ScriptFragment) currentFragment).getListView();
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
		currentFragment.handleAddButton();
	}

	public void handlePlayButton(View view) {
		if (!isHoveringActive()) {
			Intent intent = new Intent(this, PreStageActivity.class);
			startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
		}
	}

	@Override
	public void showErrorDialog(String errorMessage) {
		Utils.displayErrorMessageFragment(getSupportFragmentManager(), errorMessage);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting sounds
		if (currentFragment != null && currentFragment.getActionModeActive()) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
				ListAdapter adapter = currentFragment.getListAdapter();
				((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public boolean isHoveringActive() {
		if (currentFragmentPosition == FRAGMENT_SCRIPTS
				&& ((ScriptFragment) currentFragment).getListView().setHoveringBrick()) {
			return true;
		} else {
			return false;
		}
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
}
