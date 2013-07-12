package org.catrobat.catroid.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.ProjectDownloadService;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class WebViewFragment_Shruti extends SherlockFragment {

	String mURL = "";
	OnHeadlineSelectedListener mCallback;
	private String TYPE_FILE = "file";
	private String TYPE_HTTP = "http";
	private static final String TAG = "MainMenuActivity_Shruti";
	private static final String PROJECTNAME_TAG = "fname=";

	// Container Activity must implement this interface
	public interface OnHeadlineSelectedListener {
		public void onArticleSelected(int i);
	}

	private class DownloadReceiver extends ResultReceiver {

		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == Constants.UPDATE_DOWNLOAD_PROGRESS) {
				long progress = resultData.getLong("currentDownloadProgress");
				boolean endOfFileReached = resultData.getBoolean("endOfFileReached");
				Integer notificationId = resultData.getInt("notificationId");
				String projectName = resultData.getString("projectName");
				if (endOfFileReached) {
					progress = 100;
				}
				String notificationMessage = "Download " + progress + "% "
						+ getString(R.string.notification_percent_completed) + ":" + projectName;

				StatusBarNotificationManager.INSTANCE.updateNotification(notificationId, notificationMessage,
						Constants.DOWNLOAD_NOTIFICATION, endOfFileReached);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static WebView myWebView;
	String theUrl;
	Uri loadExternalProjectUri;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			mURL = savedInstanceState.getString("currentURL", "");
		}
		if (!mURL.trim().equalsIgnoreCase("")) {
			myWebView = (WebView) getView().findViewById(R.id.pageInfo);
			myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.loadUrl(mURL.trim());
			myWebView.setWebViewClient(new MyWebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {

					theUrl = url;
					Toast.makeText(getActivity(), theUrl, Toast.LENGTH_LONG).show();
					loadExternalProjectUri = Uri.parse(theUrl);
					if (loadExternalProjectUri != null) {
						if (theUrl.contains("download")) {
							loadProgramFromExternalSource(loadExternalProjectUri);
							mCallback.onArticleSelected(1);
						}
					}

				}
			});

		}
		//String strtext = getArguments().getString("webSite");

		//Toast.makeText(getActivity(), "Shruti's " + loadExternalProjectUri, Toast.LENGTH_LONG).show();
		//getActivity().getIntent().setData(null);

	}

	public int createNotification(String downloadName) {
		StatusBarNotificationManager manager = StatusBarNotificationManager.INSTANCE;
		int notificationId = manager.createNotification(downloadName, getActivity(), Constants.DOWNLOAD_NOTIFICATION);
		return notificationId;
	}

	private void loadProgramFromExternalSource(Uri loadExternalProjectUri) {
		String scheme = loadExternalProjectUri.getScheme();
		if (scheme.startsWith((TYPE_HTTP))) {
			String url = loadExternalProjectUri.toString();
			int projectNameIndex = url.lastIndexOf(PROJECTNAME_TAG) + PROJECTNAME_TAG.length();
			String projectName = url.substring(projectNameIndex);
			try {
				projectName = URLDecoder.decode(projectName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Could not decode project name: " + projectName, e);
			}

			Intent downloadIntent = new Intent(getActivity(), ProjectDownloadService.class);
			downloadIntent.putExtra("receiver", new DownloadReceiver(new Handler()));
			downloadIntent.putExtra("downloadName", projectName);
			downloadIntent.putExtra("url", url);
			int notificationId = createNotification(projectName);
			downloadIntent.putExtra("notificationId", notificationId);
			getActivity().startService(downloadIntent);

		} else if (scheme.equals(TYPE_FILE)) {

			String path = loadExternalProjectUri.getPath();
			int a = path.lastIndexOf('/') + 1;
			int b = path.lastIndexOf('.');
			String projectName = path.substring(a, b);
			if (!UtilZip.unZipFile(path, Utils.buildProjectPath(projectName))) {
				Utils.showErrorDialog(getActivity(), getResources().getString(R.string.error_load_project));
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("currentURL", mURL);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_webview_shruti, container, false);
		return view;
	}

	public void setURLContent(String URL) {
		mURL = URL;
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnHeadlineSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

}