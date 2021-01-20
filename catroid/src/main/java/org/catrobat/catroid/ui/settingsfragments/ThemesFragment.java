/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.ToastUtil;

import static org.catrobat.catroid.ui.BaseActivity.THEME_CLASSIC;
import static org.catrobat.catroid.ui.BaseActivity.THEME_LIGHT;

public class ThemesFragment extends Fragment {

	public static final String TAG = ThemesFragment.class.getSimpleName();

	boolean changed = false;

	public ThemesFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_themes, container, false);
		RadioGroup themesGroup = v.findViewById(R.id.radio_group_themes);

		SharedPreferences sharedPreferences =
				PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

		int themeId = sharedPreferences.getInt("theme", -1);
		switch (themeId) {
			case THEME_LIGHT:
				themesGroup.check(R.id.theme_catroid_light);
				break;
			case THEME_CLASSIC:
			default:
				themesGroup.check(R.id.theme_catroid_classic);
		}

		themesGroup.setOnCheckedChangeListener((group, checkedId) -> {
			changed = true;
			SharedPreferences.Editor editor = sharedPreferences.edit();
			switch (checkedId) {
				case R.id.theme_catroid_classic:
					editor.putInt("theme", THEME_CLASSIC);
					break;
				case R.id.theme_catroid_light:
					editor.putInt("theme", THEME_LIGHT);
					break;
			}
			editor.apply();
			getActivity().getFragmentManager().popBackStack();
		});
		return v;
	}


	@Override
	public void onDetach() {
		super.onDetach();
		if (changed) {
			startActivity(new Intent(getActivity().getBaseContext(), MainMenuActivity.class));
			startActivity(new Intent(getActivity().getBaseContext(), SettingsActivity.class));
			ToastUtil.showSuccess(getActivity(), getString(R.string.accessibility_settings_applied));
			getActivity().finishAffinity();
		}
	}

}