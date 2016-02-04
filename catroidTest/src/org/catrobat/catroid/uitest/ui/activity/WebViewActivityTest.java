/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.uitest.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.robotium.solo.By;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.dialogs.LogInDialog;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.web.ServerCalls;

public class WebViewActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private boolean containsSetting;
	private boolean showWarning;
	private SharedPreferences preferences;
	private String saveToken;

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

		saveToken = preferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		preferences.edit().putString(Constants.TOKEN, Constants.NO_TOKEN).commit();
	}

	@Override
	protected void tearDown() throws Exception {
		if (containsSetting) {
			preferences.edit().putBoolean(MainMenuActivity.SHARED_PREFERENCES_SHOW_BROWSER_WARNING, showWarning)
					.commit();
		}
		preferences.edit().putString(Constants.TOKEN, saveToken).commit();
		super.tearDown();
	}

	public void testWebViewExplore() {
		String webButtonText = solo.getString(R.string.main_menu_web);
		solo.clickOnButton(webButtonText);

		solo.waitForView(solo.getView(R.id.webView));
		solo.sleep(2000);

		assertEquals("Current Activity is not WebViewActivity", WebViewActivity.class, solo.getCurrentActivity()
				.getClass());

		final WebView webView = (WebView) solo.getCurrentActivity().findViewById(R.id.webView);
		solo.getCurrentActivity().runOnUiThread(new Runnable() {
			public void run() {
				assertEquals("Catrobat URL is not correct", Constants.BASE_URL_HTTPS, webView.getUrl());
			}
		});

		assertTrue("website hasn't been loaded properly", solo.searchText("© Catrobat"));
	}

	public void testWebViewHelp() {
		String helpButtonText = solo.getString(R.string.main_menu_help);

		solo.clickOnButton(helpButtonText);
		solo.waitForView(solo.getView(R.id.webView));
		solo.sleep(2000);

		assertEquals("Current Activity is not WebViewActivity", WebViewActivity.class, solo.getCurrentActivity()
				.getClass());

		final WebView webView = (WebView) solo.getCurrentActivity().findViewById(R.id.webView);
		solo.getCurrentActivity().runOnUiThread(new Runnable() {
			public void run() {
				assertEquals("Catrobat help URL is not correct", Constants.CATROBAT_HELP_URL, webView.getUrl());
			}
		});

		assertTrue("website hasn't been loaded properly", solo.searchText("© Catrobat"));
	}

	public void testWebViewPasswordForgotten() {
		String uploadButtonText = solo.getString(R.string.main_menu_upload);
		solo.clickOnButton(uploadButtonText);

		solo.waitForDialogToOpen();
		assertTrue("No Sign-In dialog appeared", solo.searchText(solo.getString(R.string.sign_in_dialog_title)));

		solo.clickOnText(solo.getString(R.string.register));
		solo.waitForDialogToOpen();

		String passwordForgottenButtonText = solo.getString(R.string.password_forgotten);
		solo.clickOnButton(passwordForgottenButtonText);

		solo.waitForView(solo.getView(R.id.webView));
		solo.sleep(2000);

		assertEquals("Current Activity is not WebViewActivity", WebViewActivity.class, solo.getCurrentActivity()
				.getClass());

		String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTPS : Constants.BASE_URL_HTTPS;
		final String url = baseUrl + LogInDialog.PASSWORD_FORGOTTEN_PATH;

		final WebView webView = (WebView) solo.getCurrentActivity().findViewById(R.id.webView);
		solo.getCurrentActivity().runOnUiThread(new Runnable() {
			public void run() {
				assertEquals("Catrobat password forgotten URL is not correct", url, webView.getUrl());
			}
		});

		assertTrue("website hasn't been loaded properly", solo.searchText("© Catrobat"));
	}

	public void testWebViewExploreTokenLogIn() {
		UiTestUtils.createValidUser(getActivity());
		String webButtonText = solo.getString(R.string.main_menu_web);

		solo.clickOnButton(webButtonText);
		solo.waitForView(solo.getView(R.id.webView));
		solo.sleep(2000);
		solo.clickOnWebElement(By.className("img-avatar"));
		assertTrue("User is not automatically logged in in webview", solo.searchText("My Profile"));

		solo.goBackToActivity("MainMenuActivity");
		solo.clickOnMenuItem(solo.getString(R.string.main_menu_logout));
		solo.sleep(200);

		solo.clickOnButton(webButtonText);
		solo.waitForView(solo.getView(R.id.webView));
		solo.sleep(2000);
		solo.clickOnWebElement(By.className("img-avatar"));
		assertTrue("User is not automatically logged out in webview", solo.searchText("Login"));
	}
}
