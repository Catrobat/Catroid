/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.common.annotations.VisibleForTesting;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.MediaDownloader;
import org.catrobat.catroid.utils.ProjectDownloadUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.Cookie;
import org.catrobat.catroid.web.GlobalProjectDownloadQueue;
import org.catrobat.catroid.web.ProjectDownloader;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.catrobat.catroid.common.Constants.MAIN_URL_HTTPS;
import static org.catrobat.catroid.common.Constants.MEDIA_LIBRARY_CACHE_DIRECTORY;
import static org.catrobat.catroid.common.FlavoredConstants.CATROBAT_CONTENT_DOWNLOAD_URL;
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
	public static final String MEDIA_FILE_PATHS = "media_file_paths";
	private static final String PACKAGE_NAME_WHATSAPP = "com.whatsapp";
	private static final Pattern PROJECT_DOWNLOAD_PATTERN =
			Pattern.compile("/api/projects/([a-zA-Z0-9-]+)/catrobat");

	private WebView webView;
	private org.catrobat.catroid.web.JwtTokenStore tokenStore;
	private boolean allowGoBack = false;
	private boolean forceOpenInApp = false;
	private ProgressDialog webViewLoadingDialog;
	private Intent resultIntent = new Intent();
	private final java.util.ArrayList<String> downloadedMediaPaths = new java.util.ArrayList<>();
	private ValueCallback<Uri[]> fileUploadCallback;
	private final ActivityResultLauncher<Intent> fileChooserLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
				if (fileUploadCallback == null) {
					return;
				}
				Uri[] uris = null;
				if (result.getResultCode() == RESULT_OK && result.getData() != null) {
					String dataString = result.getData().getDataString();
					if (dataString != null) {
						uris = new Uri[]{Uri.parse(dataString)};
					}
				}
				fileUploadCallback.onReceiveValue(uris);
				fileUploadCallback = null;
			});

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
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback,
					FileChooserParams fileChooserParams) {
				if (fileUploadCallback != null) {
					fileUploadCallback.onReceiveValue(null);
				}
				fileUploadCallback = callback;
				Intent intent = fileChooserParams.createIntent();
				fileChooserLauncher.launch(intent);
				return true;
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		String language = String.valueOf(Constants.CURRENT_CATROBAT_LANGUAGE_VERSION);
		String flavor = Constants.FLAVOR_DEFAULT;
		String version = Utils.getVersionName(getApplicationContext());
		String platform = Constants.PLATFORM_DEFAULT;
		String buildType = BuildConfig.FLAVOR.equals("pocketCodeBeta") ? "debug" : BuildConfig.BUILD_TYPE;
		webView.getSettings().setUserAgentString("Catrobat/" + language + " " + flavor + "/"
				+ version + " Platform/" + platform + " BuildType/" + buildType);

		tokenStore = org.koin.java.KoinJavaComponent.inject(org.catrobat.catroid.web.JwtTokenStore.class).getValue();
		setLoginCookies(url, CookieManager.getInstance(), tokenStore.getAccessToken());
		webView.loadUrl(url);

		webView.setDownloadListener((downloadUrl, userAgent, contentDisposition, mimetype, contentLength) -> {
			if (downloadUrl != null && downloadUrl.startsWith("blob:")) {
				return;
			}
			if (contentDisposition != null && getExtensionFromContentDisposition(contentDisposition).contains(Constants.CATROBAT_EXTENSION) && !downloadUrl.contains(LIBRARY_BASE_URL)) {
				String projectName = extractProjectNameFromContentDisposition(contentDisposition);
				new ProjectDownloader(GlobalProjectDownloadQueue.INSTANCE.getQueue(), downloadUrl,
						ProjectDownloadUtil.INSTANCE, projectName).download(this);
			} else if (downloadUrl.contains(CATROBAT_CONTENT_DOWNLOAD_URL)
						|| downloadUrl.contains("/resources/media/")
						|| downloadUrl.contains("/api/media/assets/")) {
				String fileName = getFilenameFromContentDisposition(contentDisposition, downloadUrl, mimetype);

				MEDIA_LIBRARY_CACHE_DIRECTORY.mkdirs();
				if (!MEDIA_LIBRARY_CACHE_DIRECTORY.isDirectory()) {
					Log.e(TAG, "Cannot create " + MEDIA_LIBRARY_CACHE_DIRECTORY);
					return;
				}

				File file = new File(MEDIA_LIBRARY_CACHE_DIRECTORY, fileName);
				downloadedMediaPaths.add(file.getAbsolutePath());
				resultIntent.putExtra(MEDIA_FILE_PATH, file.getAbsolutePath());
				resultIntent.putStringArrayListExtra(MEDIA_FILE_PATHS, downloadedMediaPaths);
				new MediaDownloader(WebViewActivity.this)
						.startDownload(WebViewActivity.this, downloadUrl, fileName, file.getAbsolutePath());
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

	private final class MyWebViewClient extends WebViewClient {
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
			String url = request.getUrl().toString();

			Matcher projectMatcher = PROJECT_DOWNLOAD_PATTERN.matcher(url);
			if (projectMatcher.find()) {
				return interceptProjectDownload(url);
			}

			return super.shouldInterceptRequest(view, request);
		}

		private WebResourceResponse interceptProjectDownload(String url) {
			try {
				okhttp3.Request httpRequest = new okhttp3.Request.Builder().url(url).build();
				okhttp3.Response httpResponse = CatrobatWebClient.INSTANCE.getClient()
						.newCall(httpRequest).execute();
				if (httpResponse.isSuccessful() && httpResponse.body() != null) {
					String projectName = extractProjectNameFromContentDisposition(
							httpResponse.header("Content-Disposition"));
					if (projectName == null) {
						Matcher m = PROJECT_DOWNLOAD_PATTERN.matcher(url);
						projectName = m.find() ? m.group(1) : "Project";
					}

					File tempFile = new File(Constants.CACHE_DIRECTORY,
							Constants.TMP_DIRECTORY_NAME + "/down.catrobat");
					if (tempFile.getParentFile() != null) {
						tempFile.getParentFile().mkdirs();
					}

					okio.BufferedSink sink = okio.Okio.buffer(okio.Okio.sink(tempFile));
					sink.writeAll(httpResponse.body().source());
					sink.close();
					httpResponse.close();

					String safeName = FileMetaDataExtractor
							.encodeSpecialCharsForFileSystem(projectName);
					File projectDir = new File(
							FlavoredConstants.DEFAULT_ROOT_DIRECTORY, safeName);
					if (projectDir.exists()) {
						org.catrobat.catroid.io.StorageOperations.deleteDir(projectDir);
					}
					new ZipArchiver().unzip(tempFile, projectDir);

					if (!tempFile.delete()) {
						Log.w(TAG, "Could not delete temp file: " + tempFile.getAbsolutePath());
					}

					final String finalName = projectName;
					new Handler(Looper.getMainLooper()).post(() -> {
						ProjectManager.getInstance()
								.addNewDownloadedProject(finalName);
						Intent resultIntent = new Intent(WebViewActivity.this, MainMenuActivity.class);
						resultIntent.setAction(Intent.ACTION_MAIN);
						resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						resultIntent.putExtra(Constants.EXTRA_PROJECT_NAME, finalName);
						startActivity(resultIntent);
						finish();
					});
					return null;
				}
			} catch (Exception e) {
				Log.e(TAG, "Project download interception failed", e);
			}
			return null;
		}

		@Override
		public void onPageStarted(WebView view, String urlClient, Bitmap favicon) {
			if (webViewLoadingDialog == null && !allowGoBack) {
				webViewLoadingDialog = new ProgressDialog(view.getContext(), R.style.WebViewLoadingCircle);
				webViewLoadingDialog.setCancelable(true);
				webViewLoadingDialog.setCanceledOnTouchOutside(false);
				webViewLoadingDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
				webViewLoadingDialog.show();
			} else if (allowGoBack && (urlClient.contains("/exit")
					|| urlClient.equals(FlavoredConstants.BASE_URL_HTTPS)
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
			syncLoginStateFromCookies(url);
		}

		private void syncLoginStateFromCookies(String url) {
			if (url == null || !url.contains(MAIN_URL_HTTPS)) {
				return;
			}

			String cookies = CookieManager.getInstance().getCookie(url);
			String bearerToken = extractBearerFromCookies(cookies);

			if (bearerToken != null && !bearerToken.isEmpty()
					&& org.catrobat.catroid.web.JwtTokenStore.Companion.isValidJwtFormat(bearerToken)) {
				if (!tokenStore.isLoggedIn()) {
					tokenStore.setAccessTokenOnly(bearerToken);
				}
			} else if (bearerToken == null || bearerToken.isEmpty()) {
				if (tokenStore.isLoggedIn()) {
					tokenStore.clearTokens();
				}
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

	public Intent getResultIntent() {
		return resultIntent;
	}

	public void setResultIntent(Intent intent) {
		resultIntent = intent;
	}

	@VisibleForTesting
	static String getFilenameFromContentDisposition(String contentDisposition, String url, String mimetype) {
		if (contentDisposition != null) {
			Matcher starMatcher = CONTENT_DISPOSITION_FILENAME_STAR.matcher(contentDisposition);
			if (starMatcher.find()) {
				try {
					return URLDecoder.decode(starMatcher.group(1), StandardCharsets.UTF_8.name());
				} catch (Exception ignored) {
					Log.w(TAG, "Failed to decode filename from Content-Disposition", ignored);
				}
			}
			Matcher quotedMatcher = CONTENT_DISPOSITION_FILENAME_QUOTED.matcher(contentDisposition);
			if (quotedMatcher.find()) {
				return quotedMatcher.group(1);
			}
		}
		return URLUtil.guessFileName(url, null, mimetype);
	}

	private static final Pattern CONTENT_DISPOSITION_FILENAME_STAR =
			Pattern.compile("filename\\*\\s*=\\s*[Uu][Tt][Ff]-8''(.+?)(?:;|$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CONTENT_DISPOSITION_FILENAME_QUOTED =
			Pattern.compile("filename\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

	private static String extractProjectNameFromContentDisposition(String contentDisposition) {
		String filename = getFilenameFromContentDisposition(contentDisposition, "", null);
		if (filename != null && filename.endsWith(Constants.CATROBAT_EXTENSION)) {
			return filename.substring(0, filename.length() - Constants.CATROBAT_EXTENSION.length());
		}
		return filename;
	}

	// TODO: Delete this function, when the Catrobat share server completely closes
	private String getExtensionFromContentDisposition(String contentDisposition) {
		int extensionIndex = contentDisposition.lastIndexOf('.');
		if (extensionIndex == -1) {
			return "NotProject";
		}
		String extension = contentDisposition.substring(extensionIndex);
		extension = extension.substring(0, extension.length() - 1);
		return extension;
	}

	@VisibleForTesting
	public static void setLoginCookies(String url, CookieManager cookieManager, String jwtToken) {
		if (jwtToken == null || jwtToken.isEmpty()) {
			return;
		}

		boolean secure = url != null && url.startsWith("https://");
		Cookie bearerCookie = new Cookie("BEARER", jwtToken, secure);
		cookieManager.setCookie(url, bearerCookie.generateCookieString());
	}

	public static void clearCookies() {
		CookieManager.getInstance().removeAllCookies(null);
		CookieManager.getInstance().flush();
	}

	@VisibleForTesting
	public static String extractBearerFromCookies(String cookies) {
		if (cookies == null) {
			return null;
		}
		for (String cookie : cookies.split(";")) {
			String trimmed = cookie.trim();
			if (trimmed.startsWith("BEARER=")) {
				return trimmed.substring("BEARER=".length());
			}
		}
		return null;
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
