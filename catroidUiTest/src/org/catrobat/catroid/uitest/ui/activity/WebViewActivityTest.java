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

import android.webkit.WebView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

public class WebViewActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String COPYRIGHT_CHARACTER = "\u00A9";

	public WebViewActivityTest() {
		super(MainMenuActivity.class);
	}

	public void testWebView() {
		solo.clickOnButton(solo.getString(R.string.main_menu_web));
		solo.waitForView(solo.getView(R.id.webView));
		solo.sleep(2000);

		assertEquals("Current Activity is not WebViewActivity", WebViewActivity.class, solo.getCurrentActivity()
				.getClass());

		WebView webView = (WebView) solo.getCurrentActivity().findViewById(R.id.webView);
		assertEquals("URL is not correct", Constants.BASE_URL_HTTPS, webView.getUrl());

		assertTrue("website hasn't been loaded properly", solo.searchText(COPYRIGHT_CHARACTER + " Catrobat"));
	}
}
