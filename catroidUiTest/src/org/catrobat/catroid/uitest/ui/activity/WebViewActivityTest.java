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

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

import android.webkit.WebView;

import com.jayway.android.robotium.solo.By;

public class WebViewActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public WebViewActivityTest() {
		super(MainMenuActivity.class);
	}

	@Device
	public void testWebViewSimple() {

		solo.clickOnButton(solo.getString(R.string.main_menu_web));

		solo.waitForWebElement(By.id(solo.getString(R.id.webView)));

		assertEquals("Current Activity is not WebViewActivity", WebViewActivity.class, solo.getCurrentActivity()
				.getClass());

		WebView webView = (WebView) solo.getCurrentActivity().findViewById(R.id.webView);
		assertEquals("URL is not correct", MainMenuActivity.WEBVIEW_URL, webView.getUrl());

	}

	@Device
	public void testWebViewCheckIfPageHasLoaded() {

		solo.clickOnButton(solo.getString(R.string.main_menu_web));

		solo.waitForWebElement(By.id(solo.getString(R.id.webView)));

		assertEquals("Current Activity is not WebViewActivity", WebViewActivity.class, solo.getCurrentActivity()
				.getClass());

		String copyright = "\u00A9";
		assertTrue("website hasn't been loaded properly", solo.searchText(copyright + " Catrobat"));

	}

}
