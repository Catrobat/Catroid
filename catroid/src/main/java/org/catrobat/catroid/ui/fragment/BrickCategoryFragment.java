/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.ViewSwitchLock;
import org.catrobat.catroid.ui.adapter.BrickCategoryAdapter;
import org.catrobat.catroid.ui.settingsfragments.RaspberryPiSettingsFragment;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import static org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.BEGINNER_BRICKS;

public class BrickCategoryFragment extends ListFragment {

	public static final String BRICK_CATEGORY_FRAGMENT_TAG = "brick_category_fragment";

	private OnCategorySelectedListener listener;
	private BrickCategoryAdapter adapter;

	private Lock viewSwitchLock = new ViewSwitchLock();

	public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
		this.listener = listener;
	}

	private boolean onlyBeginnerBricks() {
		return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(BEGINNER_BRICKS, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_brick_categories, container, false);

		setUpActionBar();
		setupBrickCategories();

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		getListView().setOnItemClickListener((parent, view, position, id) -> {
			if (!viewSwitchLock.tryLock()) {
				return;
			}

			if (listener != null) {
				listener.onCategorySelected(adapter.getItem(position));
				SnackbarUtil.showHintSnackbar(getActivity(), R.string.hint_bricks);
			}

			String categoryName = adapter.getItem(position);



		});
	}

	private void setUpActionBar() {
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.categories);
	}

	private void setupBrickCategories() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		List<View> categories = new ArrayList<>();

		if (SettingsFragment.isEmroiderySharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_embroidery, null));
		}

		if (SettingsFragment.isMindstormsNXTSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_lego_nxt, null));
		}

		if (SettingsFragment.isMindstormsEV3SharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_lego_ev3, null));
		}

		if (SettingsFragment.isDroneSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_drone, null));
		}

		if (SettingsFragment.isJSSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_drone_js, null));
		}

		if (SettingsFragment.isArduinoSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_arduino, null));
		}

		if (RaspberryPiSettingsFragment.isRaspiSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_raspi, null));
		}

		if (SettingsFragment.isPhiroSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_phiro, null));
		}

		if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
			categories.add(inflater.inflate(R.layout.brick_category_chromecast, null));
		}

		categories.add(inflater.inflate(R.layout.brick_category_event, null));
		categories.add(inflater.inflate(R.layout.brick_category_control, null));
		categories.add(inflater.inflate(R.layout.brick_category_motion, null));
		categories.add(inflater.inflate(R.layout.brick_category_sound, null));
		categories.add(inflater.inflate(R.layout.brick_category_looks, null));

		if (!onlyBeginnerBricks()) {
			categories.add(inflater.inflate(R.layout.brick_category_pen, null));
		}
		categories.add(inflater.inflate(R.layout.brick_category_data, null));
		categories.add(inflater.inflate(R.layout.brick_category_device, null));
		if (!onlyBeginnerBricks() && BuildConfig.FEATURE_USERBRICKS_ENABLED) {
			categories.add(inflater.inflate(R.layout.brick_category_userbrick, null));
		}
		if (SettingsFragment.isTestSharedPreferenceEnabled(getActivity())) {
			categories.add(inflater.inflate(R.layout.brick_category_assert, null));
		}

		adapter = new BrickCategoryAdapter(categories);
		setListAdapter(adapter);
	}

	public interface OnCategorySelectedListener {

		void onCategorySelected(String category);
	}
}
