/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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
import org.catrobat.catroid.ui.fragment.CostumeFragment;
import org.catrobat.catroid.ui.fragment.ScriptActivityFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.utils.ErrorListenerInterface;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ScriptActivity extends SherlockFragmentActivity implements ErrorListenerInterface {
	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_COSTUMES = 1;
	public static final int FRAGMENT_SOUNDS = 2;

	public static final String ACTION_SPRITE_RENAMED = "org.catrobat.catroid.SPRITE_RENAMED";
	public static final String ACTION_SPRITES_LIST_INIT = "org.catrobat.catroid.SPRITES_LIST_INIT";
	public static final String ACTION_SPRITES_LIST_CHANGED = "org.catrobat.catroid.SPRITES_LIST_CHANGED";
	public static final String ACTION_NEW_BRICK_ADDED = "org.catrobat.catroid.NEW_BRICK_ADDED";
	public static final String ACTION_BRICK_LIST_CHANGED = "org.catrobat.catroid.BRICK_LIST_CHANGED";
	public static final String ACTION_COSTUME_DELETED = "org.catrobat.catroid.COSTUME_DELETED";
	public static final String ACTION_COSTUME_RENAMED = "org.catrobat.catroid.COSTUME_RENAMED";
	public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	public static final String ACTION_SOUND_RENAMED = "org.catrobat.catroid.SOUND_RENAMED";

	private ActionBar actionBar;
	private FragmentManager fragmentManager = getSupportFragmentManager();

	private ScriptFragment scriptFragment = null;
	private CostumeFragment costumeFragment = null;
	private SoundFragment soundFragment = null;

	private ScriptActivityFragment currentFragment = null;

	private static int currentFragmentPosition;

	private boolean showDetails = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_script);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		if (savedInstanceState == null) {
			Log.d("TEST", "-----CREATE-----");

			Bundle bundle = this.getIntent().getExtras();

			int fragmentPosition = FRAGMENT_SCRIPTS;

			if (bundle != null) {
				fragmentPosition = bundle.getInt("fragment", FRAGMENT_SCRIPTS);
			} else {
				Log.d("CATROID", "No given bundle to determine fragment");
			}
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			updateCurrentFragment(fragmentPosition, fragmentTransaction);
			fragmentTransaction.commit();
		}
		actionBar = getSupportActionBar();

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(
						R.array.sprite_activity_spinner_items));

		actionBar.setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				if (itemPosition != currentFragmentPosition) {
					Log.d("TEST", "____NAVIGATE____");
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
				Log.d("TEST", "______HIDE______ScriptFragment");
				break;
			case FRAGMENT_COSTUMES:
				fragmentTransaction.hide(costumeFragment);
				Log.d("TEST", "______HIDE______CostumeFragment");
				break;
			case FRAGMENT_SOUNDS:
				fragmentTransaction.hide(soundFragment);
				Log.d("TEST", "______HIDE______SoundFragment");
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
			case FRAGMENT_COSTUMES:
				if (costumeFragment == null) {
					costumeFragment = new CostumeFragment();
					fragmentDoesNotExist = true;
				}
				currentFragment = costumeFragment;
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
			Log.d("TEST", "------INIT------" + currentFragment.getClass().getSimpleName());
			fragmentTransaction.add(R.id.script_fragment_container, currentFragment);
		} else {
			Log.d("TEST", "------SHOW------" + currentFragment.getClass().getSimpleName());
			fragmentTransaction.show(currentFragment);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();

		// Necessary to clear first if we save preferences onPause
		editor.clear();
		editor.putBoolean("showDetails", currentFragment.getShowDetails());
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Restore preferences
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		showDetails = settings.getBoolean("showDetails", false);
		currentFragment.setShowDetails(showDetails);
	}

	// Code from Stackoverflow to reduce memory problems
	// onDestroy() and unbindDrawables() methods taken from
	// http://stackoverflow.com/a/6779067
	@Override
	protected void onDestroy() {
		super.onDestroy();
		setVolumeControlStream(AudioManager.STREAM_RING);
		unbindDrawables(findViewById(R.id.SoundActivityRoot));
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_script_activity, menu);
		handleShowDetails(showDetails, menu.getItem(0));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, MainMenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			case R.id.show_details:
				boolean newShowDetailsValue = !currentFragment.getShowDetails();
				handleShowDetails(newShowDetailsValue, item);
				break;

			case R.id.copy:
				break;

			case R.id.cut:
				break;

			case R.id.insert_below:
				break;

			case R.id.move:
				break;

			case R.id.rename:
				currentFragment.startRenameActionMode();
				break;

			case R.id.delete:
				currentFragment.startDeleteActionMode();
				break;

			case R.id.settings:
				intent = new Intent(ScriptActivity.this, SettingsActivity.class);
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

	public void handleAddButton(View view) {
		currentFragment.handleAddButton();
	}

	public void handlePlayButton(View view) {
		Intent intent = new Intent(this, PreStageActivity.class);
		startActivityForResult(intent, PreStageActivity.REQUEST_RESOURCES_INIT);
	}

	@Override
	public void showErrorDialog(String errorMessage) {
		Utils.displayErrorMessageFragment(getSupportFragmentManager(), errorMessage);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting sounds
		//		if (currentFragment.getActionModeActive()) {
		//			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
		//				//				currentFragment.getListAdapter();
		//				//				adapter.clearCheckedSounds();
		//			}
		//		}
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

	public ScriptActivityFragment getFragment(int fragmentPosition) {
		ScriptActivityFragment fragment = null;

		switch (fragmentPosition) {
			case FRAGMENT_SCRIPTS:
				fragment = scriptFragment;
				break;
			case FRAGMENT_COSTUMES:
				fragment = costumeFragment;
				break;
			case FRAGMENT_SOUNDS:
				fragment = soundFragment;
				break;
		}
		return fragment;
	}
}
