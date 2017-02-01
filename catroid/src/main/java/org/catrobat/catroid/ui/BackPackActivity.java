/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.BackPackActivityFragment;
import org.catrobat.catroid.ui.fragment.BackPackLookListFragment;
import org.catrobat.catroid.ui.fragment.BackPackSceneListFragment;
import org.catrobat.catroid.ui.fragment.BackPackScriptListFragment;
import org.catrobat.catroid.ui.fragment.BackPackSoundListFragment;
import org.catrobat.catroid.ui.fragment.BackPackSpriteListFragment;

public class BackPackActivity extends BaseActivity {
	public static final int FRAGMENT_BACKPACK_SCRIPTS = 0;
	public static final int FRAGMENT_BACKPACK_LOOKS = 1;
	public static final int FRAGMENT_BACKPACK_SOUNDS = 2;
	public static final int FRAGMENT_BACKPACK_SPRITES = 3;
	public static final int FRAGMENT_BACKPACK_SCENES = 4;

	public static final String EXTRA_FRAGMENT_POSITION = "org.catrobat.catroid.ui.fragmentPosition";
	private static int currentFragmentPosition;
	private FragmentManager fragmentManager = getFragmentManager();
	private BackPackSoundListFragment backPackSoundListFragment = null;
	private BackPackLookListFragment backPackLookListFragment = null;
	private BackPackScriptListFragment backPackScriptListFragment = null;
	private BackPackSpriteListFragment backPackSpriteListFragment = null;
	private BackPackSceneListFragment backPackSceneListFragment = null;
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
			}
		}

		setCurrentFragment(currentFragmentPosition);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, currentFragment, currentFragmentTag).commit();

		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		int currentApi = android.os.Build.VERSION.SDK_INT;
		if (currentApi >= Build.VERSION_CODES.LOLLIPOP) {
			actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#83B3C7")));
			String title = getApplicationContext().getString(R.string.backpack_title);
			actionBar.setTitle(Html.fromHtml("<font color='#00475E'>" + title + "</font>")); //4D7F8F
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
		getMenuInflater().inflate(R.menu.menu_backpack_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;

			case R.id.show_details:
				handleShowDetails(!currentFragment.getShowDetails(), item);
				break;

			case R.id.unpacking:
				currentFragment.startUnpackActionMode();
				break;

			case R.id.unpacking_object:
				currentFragment.startUnpackActionMode();
				break;

			case R.id.delete:
				currentFragment.startDeleteActionMode();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void handleShowDetails(boolean showDetails, MenuItem item) {
		currentFragment.setShowDetails(showDetails);

		item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
	}

	public BackPackActivityFragment getFragment(int fragmentPosition) {
		BackPackActivityFragment fragment = null;

		switch (fragmentPosition) {
			case FRAGMENT_BACKPACK_SCRIPTS:
				fragment = backPackScriptListFragment;
				break;
			case FRAGMENT_BACKPACK_LOOKS:
				fragment = backPackLookListFragment;
				break;
			case FRAGMENT_BACKPACK_SOUNDS:
				fragment = backPackSoundListFragment;
				break;
			case FRAGMENT_BACKPACK_SPRITES:
				fragment = backPackSpriteListFragment;
				break;
			case FRAGMENT_BACKPACK_SCENES:
				fragment = backPackSceneListFragment;
		}
		return fragment;
	}

	public void setCurrentFragment(int fragmentPosition) {

		switch (fragmentPosition) {
			case FRAGMENT_BACKPACK_SCRIPTS:
				if (backPackScriptListFragment == null) {
					backPackScriptListFragment = new BackPackScriptListFragment();
				}
				currentFragment = backPackScriptListFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_SCRIPTS;
				currentFragmentTag = BackPackScriptListFragment.TAG;
				break;
			case FRAGMENT_BACKPACK_LOOKS:
				if (backPackLookListFragment == null) {
					backPackLookListFragment = new BackPackLookListFragment();
				}
				currentFragment = backPackLookListFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_LOOKS;
				currentFragmentTag = BackPackLookListFragment.TAG;
				break;
			case FRAGMENT_BACKPACK_SOUNDS:
				if (backPackSoundListFragment == null) {
					backPackSoundListFragment = new BackPackSoundListFragment();
				}
				currentFragment = backPackSoundListFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_SOUNDS;
				currentFragmentTag = BackPackSoundListFragment.TAG;
				break;
			case FRAGMENT_BACKPACK_SPRITES:
				if (backPackSpriteListFragment == null) {
					backPackSpriteListFragment = new BackPackSpriteListFragment();
				}
				currentFragment = backPackSpriteListFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_SPRITES;
				currentFragmentTag = BackPackSpriteListFragment.TAG;
				break;
			case FRAGMENT_BACKPACK_SCENES:
				if (backPackSceneListFragment == null) {
					backPackSceneListFragment = new BackPackSceneListFragment();
				}
				currentFragment = backPackSceneListFragment;
				currentFragmentPosition = FRAGMENT_BACKPACK_SCENES;
				currentFragmentTag = BackPackSceneListFragment.TAG;
				break;
		}
	}

	public void returnToScriptActivity(int fragmentPosition) {
		Intent intent = new Intent(this, ScriptActivity.class);
		intent.putExtra(ScriptActivity.EXTRA_FRAGMENT_POSITION, fragmentPosition);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void showEmptyActionModeDialog(String actionMode) {
		@SuppressLint("InflateParams")
		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_action_mode_empty, null);
		TextView actionModeEmptyText = (TextView) dialogView.findViewById(R.id.dialog_action_mode_emtpy_text);

		if (actionMode.equals(getString(R.string.unpack))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_unpack));
		} else if (actionMode.equals(getString(R.string.delete))) {
			actionModeEmptyText.setText(getString(R.string.nothing_to_delete));
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
