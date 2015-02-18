/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.DownloadUtil;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends BaseActivity {

	public static final String INTENT_PARAMETER_URL = "url";
	private WebView webView;
	private boolean callMainMenu = false;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		Intent intent = getIntent();
		url = intent.getStringExtra(INTENT_PARAMETER_URL);
		if (url == null) {
			url = Constants.BASE_URL_HTTPS;
		}

		webView = (WebView) findViewById(R.id.webView);
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);

		webView.loadUrl(url);

		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
					long contentLength) {
				DownloadUtil.getInstance().prepareDownloadAndStartIfPossible(WebViewActivity.this, url);
				Toast.makeText(WebViewActivity.this, getText(R.string.notification_download_pending), Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			callMainMenu = false;
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String urlClient, Bitmap favicon) {
			if (callMainMenu && urlClient.equals(url)) {
				Intent intent = new Intent(getBaseContext(), MainMenuActivity.class);
				startActivity(intent);
			}

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			callMainMenu = true;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (checkIfWebViewVisitExternalWebsite(url)) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				return true;
			}
			return false;
		}

		private boolean checkIfWebViewVisitExternalWebsite(String url) {
			if (url.contains(Constants.BASE_URL_HTTPS) || url.contains(Constants.CATROBAT_ABOUT_URL)) {
				return false;
			}
			return true;
		}

	}
}
