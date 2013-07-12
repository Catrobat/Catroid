package org.catrobat.catroid.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.ProjectDownloadService;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;

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

public class LandscapeHomeScreenWebFragment_Shruti extends SherlockFragment {
	public class DownloadReceiver extends ResultReceiver {

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

	private String TYPE_FILE = "file";

	String mURL = "";
	private String TYPE_HTTP = "http";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onAttach(MainMenuActivity activity) {
		super.onAttach(activity);
	}

	//private Lock viewSwitchLock = new ViewSwitchLock();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Toast.makeText(getActivity(), R.string.success_project_download, Toast.LENGTH_SHORT).show();

		if (savedInstanceState != null) {
			mURL = savedInstanceState.getString("currentURL", "");
		}
		if (!mURL.trim().equalsIgnoreCase("")) {

			WebView myWebView = (WebView) getView().findViewById(R.id.pageInfo);
			myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.setWebViewClient(new MyWebViewClient());
			myWebView.loadUrl(mURL.trim());
		}

		/*
		 * Intent browserIntent = new Intent(Intent.ACTION_VIEW,
		 * Uri.parse(getText(R.string.pocketcode_website).toString()));
		 * startActivity(browserIntent);
		 */

		//Uri loadExternalProjectUri = getActivity().getIntent().getParcelableExtra("webUri");
		//Toast.makeText(getActivity(), "mudit-shruti" + loadExternalProjectUri, Toast.LENGTH_SHORT).show();

		//getActivity().getIntent().setData(null);
		Uri loadExternalProjectUri = null;
		Toast.makeText(getActivity(), "mudit-shruti" + loadExternalProjectUri, Toast.LENGTH_SHORT).show();
		if (loadExternalProjectUri != null) {
			loadProgramFromExternalSource(loadExternalProjectUri);
			Toast.makeText(getActivity(), R.string.success_project_download, Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("currentURL", mURL);
	}

	public int createNotification(String downloadName) {
		StatusBarNotificationManager manager = StatusBarNotificationManager.INSTANCE;
		int notificationId = manager.createNotification(downloadName, getActivity(), Constants.DOWNLOAD_NOTIFICATION);
		return notificationId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_webview_shruti, container, false);
		return view;
	}

	public void setURLContent(String URL) {
		mURL = "https://pocketcode.org";
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}
	}

	private static final String TAG = "MainMenuActivity_Shruti";
	private static final String PROJECTNAME_TAG = "fname=";

	private void loadProgramFromExternalSource(Uri loadExternalProjectUri) {
		String scheme = loadExternalProjectUri.getScheme();
		if (scheme.startsWith((TYPE_HTTP))) {
			String url = loadExternalProjectUri.toString();
			Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();

			int projectNameIndex = url.lastIndexOf(PROJECTNAME_TAG) + PROJECTNAME_TAG.length();
			String projectName = url.substring(projectNameIndex);
			try {
				projectName = URLDecoder.decode(projectName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Could not decode project name: " + projectName, e);
			}

			Intent downloadIntent = new Intent(getActivity(), ProjectDownloadService.class);

			downloadIntent.putExtra("receiver", new DownloadReceiver(new Handler()));
			//downloadIntent.putExtra("receiver", "shruti");
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

}