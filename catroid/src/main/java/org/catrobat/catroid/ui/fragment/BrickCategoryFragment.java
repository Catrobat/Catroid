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
package org.catrobat.catroid.ui.fragment;

import android.app.ActionBar;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.adapter.BrickCategoryAdapter;
import org.catrobat.catroid.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class BrickCategoryFragment extends ListFragment {

	public static final String BRICK_CATEGORY_FRAGMENT_TAG = "brick_category_fragment";

	private CharSequence previousActionBarTitle;
	private OnCategorySelectedListener scriptFragment;
	private BrickCategoryAdapter adapter;
	private BrickAdapter brickAdapter;

	private Lock viewSwitchLock = new ViewSwitchLock();

	public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
		scriptFragment = listener;
	}

	public void setBrickAdapter(BrickAdapter brickAdapter) {
		this.brickAdapter = brickAdapter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_brick_categories, container, false);

		setUpActionBar();
		BottomBar.hideBottomBar(getActivity());
		setupBrickCategories();

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		getListView().setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!viewSwitchLock.tryLock()) {
					return;
				}

				if (scriptFragment != null) {
					String category = adapter.getItem(position);
					String userBrickCategory = getActivity().getString(R.string.category_user_bricks);
					if (category.equals(userBrickCategory)) {
						scriptFragment.onUserBricksSelected();
					} else {
						scriptFragment.onCategorySelected(category);
						SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_bricks);
					}
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		BottomBar.hideBottomBar(getActivity());
		BottomBar.hideAddButton(getActivity());
		setupBrickCategories();
	}

	@Override
	public void onPause() {
		super.onPause();
		BottomBar.showBottomBar(getActivity());
		BottomBar.showPlayButton(getActivity());
	}

	@Override
	public void onDestroy() {
		resetActionBar();
		super.onDestroy();
		BottomBar.showBottomBar(getActivity());
		BottomBar.showPlayButton(getActivity());
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.delete).setVisible(false);
		menu.findItem(R.id.copy).setVisible(false);
		menu.findItem(R.id.backpack).setVisible(false);
		menu.findItem(R.id.comment_in_out).setVisible(false);
	}

	private void setUpActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);

		this.previousActionBarTitle = actionBar.getTitle();
		actionBar.setTitle(R.string.categories);
	}

	private void resetActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(this.previousActionBarTitle);
	}

	private void setupBrickCategories() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		List<View> categories = new ArrayList<>();
		categories.add(inflater.inflate(R.layout.brick_category_event, null));
		categories.add(inflater.inflate(R.layout.brick_category_control, null));
		categories.add(inflater.inflate(R.layout.brick_category_motion, null));
		categories.add(inflater.inflate(R.layout.brick_category_sound, null));
		categories.add(inflater.inflate(R.layout.brick_category_looks, null));
		categories.add(inflater.inflate(R.layout.brick_category_pen, null));
		categories.add(inflater.inflate(R.layout.brick_category_data, null));

		if (SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_lego_nxt, null));
		}

		if (SettingsActivity.isMindstormsEV3SharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_lego_ev3, null));
		}

		if (BuildConfig.FEATURE_USERBRICKS_ENABLED && brickAdapter.getUserBrick() == null) {
			categories.add(inflater.inflate(R.layout.brick_category_userbricks, null));
		}

		if (SettingsActivity.isDroneSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_drone, null));
		}

		if (SettingsActivity.isPhiroSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_phiro, null));
		}

		if (SettingsActivity.isArduinoSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_arduino, null));
		}

		if (SettingsActivity.isRaspiSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_raspi, null));
		}

		adapter = new BrickCategoryAdapter(categories);
		this.setListAdapter(adapter);
	}

	public interface OnCategorySelectedListener {
		void onCategorySelected(String category);
		void onUserBricksSelected();
	}
}
