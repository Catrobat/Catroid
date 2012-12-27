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
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.ScriptActivityAdapterInterface;
import org.catrobat.catroid.ui.dragndrop.DragAndDropListView;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ScriptActivity extends SherlockFragmentActivity implements ErrorListenerInterface {
	public static final int FRAGMENT_SCRIPTS = 0;
	public static final int FRAGMENT_COSTUMES = 1;
	public static final int FRAGMENT_SOUNDS = 2;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";

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

	private boolean spinnerDisabled = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_script);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		if (savedInstanceState == null) {
			Log.d("TEST", "-------CREATE-------");

			Bundle bundle = this.getIntent().getExtras();

			int fragmentPosition = FRAGMENT_SCRIPTS;

			if (bundle != null) {
				fragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);
			} else {
				Log.d("TEST", "No given bundle to determine fragment");
			}
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			updateCurrentFragment(fragmentPosition, fragmentTransaction);
			fragmentTransaction.commit();
		}
		//		else {
		//			Log.d("TEST", "No saved Instance");
		//			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		//			updateCurrentFragment(FRAGMENT_SCRIPTS, fragmentTransaction);
		//			fragmentTransaction.commit();
		//		}
		actionBar = getSupportActionBar();

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		//		final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
		//				android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(
		//						R.array.sprite_activity_spinner_items));
		//
		//		actionBar.setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {
		//			@Override
		//			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		//				Log.d("TEST", "spinner clicked!");
		//				if (itemPosition != currentFragmentPosition) {
		//					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		//
		//					hideFragment(currentFragmentPosition, fragmentTransaction);
		//					updateCurrentFragment(itemPosition, fragmentTransaction);
		//
		//					fragmentTransaction.commit();
		//				}
		//				return true;
		//			}
		//		});
		//		actionBar.setSelectedNavigationItem(currentFragmentPosition);
	}

	private void hideFragment(int fragment, FragmentTransaction fragmentTransaction) {
		switch (fragment) {
			case FRAGMENT_SCRIPTS:
				fragmentTransaction.hide(scriptFragment);
				break;
			case FRAGMENT_COSTUMES:
				fragmentTransaction.hide(costumeFragment);
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
			Log.d("TEST", "[INIT] " + currentFragment.getClass().getSimpleName());
			fragmentTransaction.add(R.id.script_fragment_container, currentFragment);
		} else {
			Log.d("TEST", "[SHOW] " + currentFragment.getClass().getSimpleName());
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

		Log.d("TEST", "ON_PAUSE " + currentFragment.getClass().getSimpleName());

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();

		// Necessary to clear first if we save preferences onPause
		editor.clear();

		boolean showDetails = currentFragment.getShowDetails();

		String sharedPreferenceName = "showDetails" + getCurrentPreferenceAffix();
		Log.d("TEST", "ON_PAUSE  " + currentFragment.getClass().getSimpleName() + " showDetails: " + showDetails);
		Log.d("TEST", "          " + sharedPreferenceName);

		editor.putBoolean(sharedPreferenceName, showDetails);
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Restore preferences
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		String sharedPreferenceName = "showDetails" + getCurrentPreferenceAffix();
		boolean showDetails = settings.getBoolean(sharedPreferenceName, false);

		Log.d("TEST", "ON_RESUME " + currentFragment.getClass().getSimpleName() + " showDetails: " + showDetails);
		Log.d("TEST", "          " + sharedPreferenceName);
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
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d("TEST", "----------------------PREPARE-------------------");
		handleShowDetails(currentFragment.getShowDetails(), menu.findItem(R.id.show_details));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("TEST", "----------------------CREATE-------------------");
		getSupportMenuInflater().inflate(R.menu.menu_script_activity, menu);

		MenuItem item = menu.findItem(R.id.spinner);
		final Spinner spinner = (Spinner) item.getActionView();

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position != currentFragmentPosition) {
					Log.d("TEST", "spinner clicked!");
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

					hideFragment(currentFragmentPosition, fragmentTransaction);
					updateCurrentFragment(position, fragmentTransaction);

					fragmentTransaction.commit();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spinner.setSelection(currentFragmentPosition);

		spinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (isHoveringActive()) {
						spinner.setClickable(false);
						spinnerDisabled = true;
					} else if (spinnerDisabled) {
						spinner.setClickable(true);
						spinnerDisabled = false;
					}
				}
				return false;
			}
		});
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
		if (currentFragment.getActionModeActive()) {
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
		Log.d("TEST", "HANDLE_SHOW_DETAILS " + currentFragment.getClass().getSimpleName() + " showDetails: "
				+ showDetails);
		currentFragment.setShowDetails(showDetails);

		String menuItemText = "";
		if (showDetails) {
			menuItemText = getString(R.string.hide_details);
		} else {
			menuItemText = getString(R.string.show_details);
		}
		Log.d("TEST", "before -> " + item.getTitle());
		item.setTitle(menuItemText);
		Log.d("TEST", "after  -> " + item.getTitle());
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

	private String getCurrentPreferenceAffix() {
		String preferenceAffix = "";

		switch (currentFragmentPosition) {
			case FRAGMENT_SCRIPTS:
				preferenceAffix = "Scripts";
				break;
			case FRAGMENT_COSTUMES:
				preferenceAffix = "Costumes";
				break;
			case FRAGMENT_SOUNDS:
				preferenceAffix = "Sounds";
				break;
		}
		return preferenceAffix;
	}
}
