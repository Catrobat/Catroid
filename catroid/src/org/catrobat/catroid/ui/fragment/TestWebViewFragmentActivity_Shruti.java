package org.catrobat.catroid.ui.fragment;

import org.catrobat.catroid.R;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class TestWebViewFragmentActivity_Shruti extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.web_view_fragment_test);

		if (findViewById(R.id.testweb) != null) {

			getFragmentManager().popBackStack();

			WebViewFragment_Shruti webFragment = (WebViewFragment_Shruti) getFragmentManager().findFragmentById(
					R.id.testweb);
			if (webFragment == null) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				webFragment = new WebViewFragment_Shruti();
				ft.replace(R.id.testweb, webFragment, "Detail_Fragment1");
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
				Log.v("shruti", "maybe");
				webFragment.setURLContent("https://pocketcode.org");
				Log.v("shruti", "yes");
			}
		}

	}
}
