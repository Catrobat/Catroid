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

		assertEquals(WebViewActivity.class, solo.getCurrentActivity().getClass());

		WebView webView = (WebView) solo.getCurrentActivity().findViewById(R.id.webView);
		assertEquals(MainMenuActivity.WEBVIEW_URL, webView.getUrl());

	}

	@Device
	public void testWebViewCheckIfPageHasLoaded() {

		solo.clickOnButton(solo.getString(R.string.main_menu_web));

		solo.waitForWebElement(By.id(solo.getString(R.id.webView)));

		assertEquals(WebViewActivity.class, solo.getCurrentActivity().getClass());

		assertTrue(solo.searchText("\u00A9 Catrobat"));

	}

}
