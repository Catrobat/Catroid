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
package org.catrobat.catroid.uitest.ui.menu;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class LogoutLoginTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private SharedPreferences preferences;
	private String saveToken;

	public LogoutLoginTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		saveToken = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		preferences.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();
	}

	@Override
	public void tearDown() throws Exception {
		preferences.edit().putString(Constants.TOKEN, saveToken).commit();
		super.tearDown();
	}

	public void testMenuItemVisibility() {
		solo.sendKey(Solo.MENU);
		assertFalse("Logout menu item visible although no user logged in yet", solo.searchText(solo.getString(R.string
				.main_menu_logout)));
		assertTrue("Login menu item not visible", solo.searchText(solo.getString(R.string
				.main_menu_login)));

		solo.goBack();
		UiTestUtils.createValidUser(getActivity());

		solo.sendKey(Solo.MENU);
		assertTrue("Logout menu item not visible after user logged in", solo.searchText(solo.getString(R.string
				.main_menu_logout)));
		assertFalse("Login menu visible despite user beeing logged in", solo.searchText(solo.getString(R.string
				.main_menu_login)));

		solo.clickOnMenuItem(solo.getString(R.string.main_menu_logout));
		solo.sleep(2000);
		solo.sendKey(Solo.MENU);
		assertFalse("Logout menu item visible after user logged out", solo.searchText(solo.getString(R.string
				.main_menu_logout)));

		assertTrue("Login menu item not visible after user logged out", solo.searchText(solo.getString(R.string
				.main_menu_login)));

		String token = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = preferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
		assertEquals("Token not cleared in preferences after logout", token, Constants.NO_TOKEN);
		assertEquals("Username not cleared in preferences after logout", username, Constants.NO_USERNAME);
	}
}
