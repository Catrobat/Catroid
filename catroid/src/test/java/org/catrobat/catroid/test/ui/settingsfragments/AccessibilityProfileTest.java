/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.ui.settingsfragments;

import android.content.SharedPreferences;

import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityProfileTest {
	@Mock
	private SharedPreferences sharedPreferences;

	@Mock
	private SharedPreferences.Editor sharedPreferencesEditor;

	private AccessibilityProfile accessibilityProfile;

	@Before
	public void setUp() throws Exception {
		when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
	}

	@Test
	public void testInitializationFromNullProfile() {
		when(sharedPreferences.getStringSet(anyString(), any())).thenReturn(null);
		accessibilityProfile = AccessibilityProfile.fromCustomProfile(sharedPreferences);

		accessibilityProfile.setAsCurrent(sharedPreferences);
	}
}
