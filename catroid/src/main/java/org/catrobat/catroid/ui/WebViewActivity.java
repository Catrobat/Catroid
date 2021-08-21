/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.annotations.VisibleForTesting;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.utils.MediaDownloader;
import org.catrobat.catroid.utils.ProjectDownloadUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.Cookie;
import org.catrobat.catroid.web.GlobalProjectDownloadQueue;
import org.catrobat.catroid.web.ProjectDownloader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import static org.catrobat.catroid.common.Constants.MAIN_URL_HTTPS;
import static org.catrobat.catroid.common.Constants.MEDIA_LIBRARY_CACHE_DIR;
import static org.catrobat.catroid.common.FlavoredConstants.CATROBAT_HELP_URL;
import static org.catrobat.catroid.common.FlavoredConstants.LIBRARY_BASE_URL;
import static org.catrobat.catroid.ui.MainMenuActivity.surveyCampaign;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends AppCompatActivity {

	private static final String TAG = WebViewActivity.class.getSimpleName();

	public static final String INTENT_PARAMETER_URL = "url";
	public static final String INTENT_FORCE_OPEN_IN_APP = "openInApp";
	public static final String ANDROID_APPLICATION_EXTENSION = ".apk";
	public static final String MEDIA_FILE_PATH = "media_file_path";
	private static final String PACKAGE_NAME_WHATSAPP = "com.whatsapp";

	private WebView webView;
	private boolean allowGoBack = false;
	private boolean forceOpenInApp = false;
	private ProgressDialog progressDialog;
	private ProgressDialog webViewLoadingDialog;
	private Intent resultIntent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		String url = getIntent().getStringExtra(INTENT_PARAMETER_URL);
		if (url == null) {
			url = FlavoredConstants.BASE_URL_HTTPS;
		}

		forceOpenInApp = getIntent().getBooleanExtra(INTENT_FORCE_OPEN_IN_APP, false);

		webView = findViewById(R.id.webView);
		webView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.app_background, null));
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		String language = String.valueOf(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);
		String flavor = Constants.FLAVOR_DEFAULT;
		String version = Utils.getVersionName(getApplicationContext());
		String platform = Constants.PLATFORM_DEFAULT;
		String buildType = BuildConfig.FLAVOR.equals("pocketCodeBeta") ? "debug" : BuildConfig.BUILD_TYPE;
		webView.getSettings().setUserAgentString("Catrobat/" + language + " " + flavor + "/"
				+ version + " Platform/" + platform + " BuildType/" + buildType);

		setLoginCookies(url, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), CookieManager.getInstance());
		webView.loadUrl(url);

		webView.setDownloadListener((downloadUrl, userAgent, contentDisposition, mimetype, contentLength) -> {

			if (getExtensionFromContentDisposition(contentDisposition).contains(Constants.CATROBAT_EXTENSION) && !downloadUrl.contains(LIBRARY_BASE_URL)) {
				new ProjectDownloader(GlobalProjectDownloadQueue.INSTANCE.getQueue(), downloadUrl,
						ProjectDownloadUtil.INSTANCE).download(this);
			} else if (downloadUrl.contains(LIBRARY_BASE_URL)) {
				String fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype);

				MEDIA_LIBRARY_CACHE_DIR.mkdirs();
				if (!MEDIA_LIBRARY_CACHE_DIR.isDirectory()) {
					Log.e(TAG, "Cannot create " + MEDIA_LIBRARY_CACHE_DIR);
					return;
				}

				File file = new File(MEDIA_LIBRARY_CACHE_DIR, fileName);
				resultIntent.putExtra(MEDIA_FILE_PATH, file.getAbsolutePath());
				new MediaDownloader(this)
						.startDownload(this, downloadUrl, fileName, file.getAbsolutePath());
			} else {
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
				String projectName = ProjectDownloader.Companion.getProjectNameFromUrl(downloadUrl);
				request.setTitle(getString(R.string.notification_download_title_pending) + " " + projectName);
				request.setDescription(getString(R.string.notification_download_pending));
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
						projectName + ANDROID_APPLICATION_EXTENSION);
				request.setMimeType(mimetype);

				DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				downloadManager.enqueue(request);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			allowGoBack = false;
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String urlClient, Bitmap favicon) {
			if (webViewLoadingDialog == null && !allowGoBack) {
				webViewLoadingDialog = new ProgressDialog(view.getContext(), R.style.WebViewLoadingCircle);
				webViewLoadingDialog.setCancelable(true);
				webViewLoadingDialog.setCanceledOnTouchOutside(false);
				webViewLoadingDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
				webViewLoadingDialog.show();
			} else if (allowGoBack && (urlClient.equals(FlavoredConstants.BASE_URL_HTTPS)
					|| urlClient.equals(Constants.BASE_APP_URL_HTTPS))) {
				allowGoBack = false;
				onBackPressed();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			allowGoBack = true;
			if (webViewLoadingDialog != null) {
				webViewLoadingDialog.dismiss();
				webViewLoadingDialog = null;
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url != null && url.startsWith(Constants.WHATSAPP_URI)) {
				if (isWhatsappInstalled()) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				} else {
					ToastUtil.showError(getBaseContext(), R.string.error_no_whatsapp);
				}
				return true;
			} else if (!forceOpenInApp && checkIfWebViewVisitExternalWebsite(url)) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				return true;
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			if (Utils.checkIsNetworkAvailableAndShowErrorMessage(WebViewActivity.this)) {
				ToastUtil.showError(getBaseContext(), R.string.error_unknown_error);
			}

			if (errorCode == ERROR_CONNECT
					|| errorCode == ERROR_FILE_NOT_FOUND
					|| errorCode == ERROR_HOST_LOOKUP
					|| errorCode == ERROR_TIMEOUT
					|| errorCode == ERROR_PROXY_AUTHENTICATION
					|| errorCode == ERROR_UNKNOWN) {
				setContentView(R.layout.activity_network_error);
				setSupportActionBar(findViewById(R.id.toolbar));
				getSupportActionBar().setIcon(R.drawable.pc_toolbar_icon);
				getSupportActionBar().setTitle(R.string.app_name);
			} else {
				Log.e(TAG, "couldn't connect to the server! info: " + description + " : " + errorCode);
			}
		}

		private boolean checkIfWebViewVisitExternalWebsite(String url) {
			// help URL has to be opened in an external browser
			return (!url.contains(MAIN_URL_HTTPS) || url.contains(CATROBAT_HELP_URL))
					&& !url.contains(LIBRARY_BASE_URL);
		}
	}

	public void createProgressDialog(String mediaName) {
		progressDialog = new ProgressDialog(this);

		progressDialog.setTitle(getString(R.string.notification_download_title_pending) + mediaName);
		progressDialog.setMessage(getString(R.string.notification_download_pending));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog.setProgressNumberFormat(null);
		if (!isFinishing()) {
			progressDialog.show();
		}
	}

	public void updateProgressDialog(long progress) {
		if (progress == 100) {
			if (progressDialog.isShowing() && !isFinishing()) {
				progressDialog.setProgress(progressDialog.getMax());
				setResult(RESULT_OK, resultIntent);
				progressDialog.dismiss();
			}
			finish();
		} else {
			progressDialog.setProgress((int) progress);
		}
	}

	public void dismissProgressDialog() {
		progressDialog.dismiss();
	}

	public Intent getResultIntent() {
		return resultIntent;
	}

	public void setResultIntent(Intent intent) {
		resultIntent = intent;
	}

	private String getExtensionFromContentDisposition(String contentDisposition) {
		int extensionIndex = contentDisposition.lastIndexOf('.');
		String extension = contentDisposition.substring(extensionIndex);
		extension = extension.substring(0, extension.length() - 1);
		return extension;
	}

	@VisibleForTesting
	public static void setLoginCookies(String url, SharedPreferences sharedPreferences, CookieManager cookieManager) {
		String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);

		if (username.equals(Constants.NO_USERNAME) || token.equals(Constants.NO_TOKEN)) {
			return;
		}

		String encodedUsername;
		try {
			encodedUsername = URLEncoder.encode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return;
		}

		Cookie usernameCookie = new Cookie(Constants.USERNAME_COOKIE_NAME, encodedUsername);
		Cookie tokenCookie = new Cookie(Constants.TOKEN_COOKIE_NAME, token);
		cookieManager.setCookie(url, usernameCookie.generateCookieString());
		cookieManager.setCookie(url, tokenCookie.generateCookieString());
	}

	public static void clearCookies() {
		CookieManager.getInstance().removeAllCookies(null);
		CookieManager.getInstance().flush();
	}

	private boolean isWhatsappInstalled() {
		PackageManager packageManager = getPackageManager();
		try {
			packageManager.getPackageInfo(PACKAGE_NAME_WHATSAPP, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		webView.setDownloadListener(null);
		webView.destroy();

		if (surveyCampaign != null) {
			surveyCampaign.showSurvey(this);
		}

		super.onDestroy();
	}
}
