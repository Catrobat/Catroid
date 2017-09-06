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
import android.support.test.espresso.matcher.PreferenceMatchers;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public final class SettingsActivityUtils {

	// Suppress default constructor for noninstantiability
	private SettingsActivityUtils() {
		throw new AssertionError();
	}

	public static void setAllSettingsTo(List<String> allSettings, boolean value) {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();

		for (String setting : allSettings) {
			sharedPreferencesEditor.putBoolean(setting, value);
		}
		sharedPreferencesEditor.commit();
	}

	public static void checkPreference(int displayedTitleResourceString, String sharedPreferenceTag) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString))
				.perform(click());

		assertFalse(sharedPreferences.getBoolean(sharedPreferenceTag, false));

		onData(PreferenceMatchers.withTitle(displayedTitleResourceString))
				.perform(click());

		assertTrue(sharedPreferences.getBoolean(sharedPreferenceTag, false));
	}
}
