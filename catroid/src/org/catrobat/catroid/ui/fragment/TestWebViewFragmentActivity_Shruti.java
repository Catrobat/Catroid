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

		WebViewFragment_Shruti webFragment = (WebViewFragment_Shruti) getSupportFragmentManager().findFragmentById(
				R.id.testweb);
		Bundle xyz = new Bundle();

		xyz.putString("webSite", "https://pocketcode.org");
		//set Fragmentclass Arguments

		android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		webFragment = new WebViewFragment_Shruti();
		webFragment.setArguments(xyz);
		ft.replace(R.id.testweb, webFragment, "Detail_Fragment1");
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
		Log.v("shruti", "maybe");
		webFragment.setURLContent("https://pocketcode.org");
		Log.v("shruti", "yes");

	}
}
