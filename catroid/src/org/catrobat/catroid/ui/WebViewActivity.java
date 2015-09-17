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
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends BaseActivity {

	public static final String INTENT_PARAMETER_URL = "url";
	public static final String ANDROID_APPLICATION_EXTENSION = ".apk";
	public static final String MEDIA_FILE_PATH = "media_file_path";
	public static final String CALLING_ACTIVITY = "calling_activity";
	private static final String FILENAME_TAG = "fname=";

	private WebView webView;
	private boolean callMainMenu = false;
	private String url;
	private String callingActivity;
	private ProgressDialog progressDialog;
	private Intent resultIntent = new Intent();

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
		callingActivity = intent.getStringExtra(CALLING_ACTIVITY);

		webView = (WebView) findViewById(R.id.webView);
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);

		String language = String.valueOf(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);
		String flavor = Constants.FLAVOR_DEFAULT;
		String version = Utils.getVersionName(getApplicationContext());
		String platform = Constants.PLATFORM_DEFAULT;
		webView.getSettings().setUserAgentString("Catrobat/" + language + " " + flavor + "/"
				+ version + " Platform/" + platform);

		webView.loadUrl(url);

		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
					long contentLength) {
				Log.d(WebViewActivity.class.getSimpleName(), "contentDisposition: " + contentDisposition + "   " + mimetype);

				if (getExtentionFromContentDisposition(contentDisposition).contains(Constants.CATROBAT_EXTENSION)) {
					DownloadUtil.getInstance().prepareDownloadAndStartIfPossible(WebViewActivity.this, url);
				} else if (url.contains(Constants.LIBRARY_BASE_URL)) {
					String name = getMediaNameFromUrl(url);
					String mediaType = getMediaTypeFromContentDisposition(contentDisposition);
					String fileName = name + getExtentionFromContentDisposition(contentDisposition);
					String tempPath = null;
					switch (mediaType) {
						case Constants.MEDIA_TYPE_LOOK:
							tempPath = Constants.TMP_LOOKS_PATH;
							break;
						case Constants.MEDIA_TYPE_SOUND:
							tempPath = Constants.TMP_SOUNDS_PATH;
					}
					String filePath = Utils.buildPath(tempPath, fileName);
					resultIntent.putExtra(MEDIA_FILE_PATH, filePath);
					DownloadUtil.getInstance().prepareMediaDownloadAndStartIfPossible(WebViewActivity.this, url,
							mediaType, name, filePath, callingActivity);
				} else {
					DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

					request.setTitle(getString(R.string.notification_download_title_pending) + " " + DownloadUtil.getInstance().getProjectNameFromUrl(url));
					request.setDescription(getString(R.string.notification_download_pending));
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
							DownloadUtil.getInstance().getProjectNameFromUrl(url) + ANDROID_APPLICATION_EXTENSION);
					request.setMimeType(mimetype);

					registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

					DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
					downloadManager.enqueue(request);
				}
			}
		});
	}

	BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			long id = intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
			DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setDataAndType(downloadManager.getUriForDownloadedFile(id),
					downloadManager.getMimeTypeForDownloadedFile(id));
			startActivity(intent);
		}
	};

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

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

			int errorMessage;
			if (!isConnected) {
				errorMessage = R.string.error_internet_connection;
			} else {
				errorMessage = R.string.error_unknown_error;
			}
			ToastUtil.showError(getBaseContext(), errorMessage);
			finish();
		}

		private boolean checkIfWebViewVisitExternalWebsite(String url) {
			if (url.contains(Constants.BASE_URL_HTTPS) || url.contains(Constants.CATROBAT_ABOUT_URL) || url.contains(Constants.LIBRARY_BASE_URL)) {
				return false;
			}
			return true;
		}
	}

	public void createProgressDialog(String mediaName) {
		progressDialog = new ProgressDialog(WebViewActivity.this);

		progressDialog.setTitle(getString(R.string.notification_download_title_pending) + mediaName);
		progressDialog.setMessage(getString(R.string.notification_download_pending));
		progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog.setProgressNumberFormat(null);
		progressDialog.show();
	}

	public void updateProgressDialog(long progress) {
		if (progress == 100) {
			progressDialog.setProgress(progressDialog.getMax());
			setResult(RESULT_OK, resultIntent);
			progressDialog.dismiss();
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

	private String getMediaNameFromUrl(String url) {
		int mediaNameIndex = url.lastIndexOf(FILENAME_TAG) + FILENAME_TAG.length();
		String mediaName = url.substring(mediaNameIndex);
		try {
			mediaName = URLDecoder.decode(mediaName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e("WebViewActivity", "Could not decode program name: " + mediaName, e);
			return null;
		}
		return mediaName;
	}

	private String getMediaTypeFromContentDisposition(String contentDisposition) {
		String mediaType = null;
		for (String extension : Constants.IMAGE_EXTENTIONS) {
			if (getExtentionFromContentDisposition(contentDisposition).compareTo(extension) == 0) {
				mediaType = Constants.MEDIA_TYPE_LOOK;
			}
		}

		for (String extention : Constants.SOUND_EXTENTIONS) {
			if (getExtentionFromContentDisposition(contentDisposition).compareTo(extention) == 0) {
				mediaType = Constants.MEDIA_TYPE_SOUND;
			}
		}
		return mediaType;
	}

	private String getExtentionFromContentDisposition(String contentDisposition) {
		int extentionIndex = contentDisposition.lastIndexOf('.');
		String extention = contentDisposition.substring(extentionIndex);
		extention = extention.substring(0, extention.length() - 1);
		return extention;
	}
}
