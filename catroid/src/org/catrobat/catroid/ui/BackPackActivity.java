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

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.adapter.ScriptActivityAdapterInterface;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.BackPackActivityFragment;
import org.catrobat.catroid.ui.fragment.BackPackLookFragment;
import org.catrobat.catroid.ui.fragment.BackPackScriptFragment;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;

import java.util.Iterator;

public class BackPackActivity extends BaseActivity {
	public static final int FRAGMENT_BACKPACK_SCRIPTS = 0;
	public static final int FRAGMENT_BACKPACK_LOOKS = 1;
	public static final int FRAGMENT_BACKPACK_SOUNDS = 2;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";
	public static final String BACKPACK_ITEM = "backpackItem";
	public static final String ACTION_SOUND_DELETED = "org.catrobat.catroid.SOUND_DELETED";
	public static final String ACTION_LOOK_DELETED = "org.catrobat.catroid.LOOK_DELETED";
	public static final String ACTION_SCRIPT_DELETED = "org.catrobat.catroid.SCRIPT_DELETED";
	private static int currentFragmentPosition;
	private boolean backpackItem = false;
	private FragmentManager fragmentManager = getSupportFragmentManager();
	private BackPackSoundFragment backPackSoundFragment = null;
	private BackPackLookFragment backPackLookFragment = null;
	private BackPackScriptFragment backPackScriptFragment = null;
	private BackPackActivityFragment currentFragment = null;
	private String currentFragmentTag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.backpack_title);
		setContentView(R.layout.activity_script);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		currentFragmentPosition = FRAGMENT_BACKPACK_SCRIPTS;

		if (savedInstanceState == null) {
			Bundle bundle = this.getIntent().getExtras();

			if (bundle != null) {
				currentFragmentPosition = bundle.getInt(EXTRA_FRAGMENT_POSITION, FRAGMENT_BACKPACK_SCRIPTS);
				backpackItem = bundle.getBoolean(BACKPACK_ITEM);
			}
		}

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		setCurrentFragment(currentFragmentPosition);
		fragmentTransaction.commit();
		fragmentTransaction.add(R.id.script_fragment_container, currentFragment, currentFragmentTag);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (backpackItem) {
			Iterator<SoundInfo> iterator = BackPackListManager.getActionBarSoundInfoArrayList().iterator();

			while (iterator.hasNext()) {
				SoundInfo soundInfo = iterator.next();
				BackPackListManager.setCurrentSoundInfo(soundInfo);
				SoundController.getInstance().backPackSound(BackPackListManager.getCurrentSoundInfo(),
						backPackSoundFragment, BackPackListManager.getInstance().getSoundInfoArrayList(),
						backPackSoundFragment.getAdapter());
			}
			BackPackListManager.getActionBarSoundInfoArrayList().clear();
			backpackItem = false;
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
			menu.findItem(R.id.unpacking).setVisible(false);
			menu.findItem(R.id.backpack).setVisible(false);
			menu.findItem(R.id.cut).setVisible(false);
			menu.findItem(R.id.rename).setVisible(false);
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
			case R.id.show_details:
				handleShowDetails(!currentFragment.getShowDetails(), item);
				break;

			case R.id.unpacking:
				currentFragment.startUnPackingActionMode();
				break;

			case R.id.delete:
				currentFragment.startDeleteActionMode();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//Dismiss ActionMode without effecting checked items

		if (currentFragment != null && currentFragment.getActionModeActive()
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			ListAdapter adapter = null;
			if (currentFragment instanceof BackPackScriptFragment) {
				adapter = ((BackPackScriptFragment) currentFragment).getAdapter();
			} else {
				adapter = currentFragment.getListAdapter();
			}
			((ScriptActivityAdapterInterface) adapter).clearCheckedItems();
		}

		return super.dispatchKeyEvent(event);
	}

	public void handleShowDetails(boolean showDetails, MenuItem item) {
		currentFragment.setShowDetails(showDetails);

		item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
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
				if (backPackSoundFragment == null) {
					backPackSoundFragment = new BackPackSoundFragment();
				}
				currentFragment = backPackSoundFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_SOUNDS;
				currentFragmentTag = BackPackSoundFragment.TAG;

				break;
		}
	}
}
