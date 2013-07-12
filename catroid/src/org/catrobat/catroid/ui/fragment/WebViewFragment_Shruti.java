package org.catrobat.catroid.ui.fragment;

import org.catrobat.catroid.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment_Shruti extends Fragment {

	String mURL = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			mURL = savedInstanceState.getString("currentURL", "");
		}
		if (!mURL.trim().equalsIgnoreCase("")) {
			WebView myWebView = (WebView) getView().findViewById(R.id.pageInfo);
			myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.setWebViewClient(new MyWebViewClient());
			myWebView.loadUrl(mURL.trim());
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
}