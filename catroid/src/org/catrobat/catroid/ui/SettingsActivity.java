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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;

public class SettingsActivity extends SherlockPreferenceActivity {

	CheckBoxPreference dronePreference = null;

	public static final String setting_quadcopter_bricks = "setting_quadcopter_bricks";

	PreferenceScreen screen = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.preference_title);
		actionBar.setHomeButtonEnabled(true);

		dronePreference = (CheckBoxPreference) findPreference(setting_quadcopter_bricks);
		screen = getPreferenceScreen();

		if (BuildConfig.DEBUG) {
			dronePreference.setEnabled(true);
			//dronePreference.setChecked(true);
			screen.addPreference(dronePreference);
		}
	}
}
