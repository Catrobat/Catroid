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

package org.catrobat.catroid.uiespresso.ui.activity.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import java.util.List;

import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_FONTFACE;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_FONTFACE_VALUE_STANDARD;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_MYPROFILE_FONTFACE;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_PROFILE_ACTIVE;
import static org.catrobat.catroid.ui.BaseSettingsActivity.ACCESS_PROFILE_NONE;

public final class AccessibilityActivitiesUtils {
	private AccessibilityActivitiesUtils() {
		throw new AssertionError();
	}

	public static void resetSettings(List<String> settings, List<String> myProfileSettings) {
		SettingsActivityUtils.setAllSettingsTo(settings, false);
		SettingsActivityUtils.setAllSettingsTo(myProfileSettings, false);

		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();

		sharedPreferencesEditor.putString(ACCESS_PROFILE_ACTIVE, ACCESS_PROFILE_NONE);
		sharedPreferencesEditor.putString(ACCESS_FONTFACE, ACCESS_FONTFACE_VALUE_STANDARD);
		sharedPreferencesEditor.putString(ACCESS_MYPROFILE_FONTFACE, ACCESS_FONTFACE_VALUE_STANDARD);

		sharedPreferencesEditor.commit();
	}
}
