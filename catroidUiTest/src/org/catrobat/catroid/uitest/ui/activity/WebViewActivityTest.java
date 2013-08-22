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
package org.catrobat.catroid.uitest.ui.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

public class WebViewActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String COPYRIGHT_CHARACTER = "\u00A9";
	private boolean containsSetting;
	private boolean showWarning;
	private SharedPreferences preferences;

	public WebViewActivityTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		containsSetting = preferences.contains(MainMenuActivity.SHARED_PREFERENCES_SHOW_BROWSER_WARNING);
		showWarning = preferences.getBoolean(MainMenuActivity.SHARED_PREFERENCES_SHOW_BROWSER_WARNING, true);
		preferences.edit().remove(MainMenuActivity.SHARED_PREFERENCES_SHOW_BROWSER_WARNING).commit();
	}

	@Override
	protected void tearDown() throws Exception {
		if (containsSetting) {
			preferences.edit().putBoolean(MainMenuActivity.SHARED_PREFERENCES_SHOW_BROWSER_WARNING, showWarning)
					.commit();
		}

		super.tearDown();
	}

	public void testWebView() {
		String webButtonText = solo.getString(R.string.main_menu_web);

		solo.clickOnButton(webButtonText);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			solo.waitForView(solo.getView(R.id.webView));
			solo.sleep(2000);

			assertEquals("Current Activity is not WebViewActivity", WebViewActivity.class, solo.getCurrentActivity()
					.getClass());

			WebView webView = (WebView) solo.getCurrentActivity().findViewById(R.id.webView);
			assertEquals("URL is not correct", Constants.BASE_URL_HTTPS, webView.getUrl());

			assertTrue("website hasn't been loaded properly", solo.searchText(COPYRIGHT_CHARACTER + " Catrobat"));
		} else {
			fail("This test is made for API 11 or above");
		}
	}

	@Device
	public void testWebOnOldDevices() {
		String webButtonText = solo.getString(R.string.main_menu_web);
		String cancelButtonText = solo.getString(R.string.cancel_button);
		String dialogTitleText = solo.getString(R.string.main_menu_web_dialog_title);

		solo.clickOnButton(webButtonText);

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			solo.sleep(300);
			assertTrue("Alert dialog title not found", solo.searchText(dialogTitleText));
			assertTrue("Alert dialog message not found",
					solo.searchText(solo.getString(R.string.main_menu_web_dialog_message)));
			assertTrue("OK button not found", solo.searchText(solo.getString(R.string.ok)));
			assertTrue("Cancel button not found", solo.searchText(cancelButtonText));

			solo.clickOnButton(cancelButtonText);
			solo.sleep(200);
			assertFalse("Dialog was not closed when pressing cancel", solo.searchText(dialogTitleText));
			solo.clickOnButton(webButtonText);
			solo.sleep(300);
			solo.goBack();
			assertFalse("Dialog was not closed when clicked back button", solo.searchText(dialogTitleText));
		} else {
			fail("This test is made for API 10 or lower");
		}

	}
}
