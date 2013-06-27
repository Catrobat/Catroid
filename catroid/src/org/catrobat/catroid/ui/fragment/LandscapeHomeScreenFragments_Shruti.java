package org.catrobat.catroid.ui.fragment;

import org.catrobat.catroid.R;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class LandscapeHomeScreenFragments_Shruti extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.landscape_home_screen);

		ProjectsListFragment listFragment = (ProjectsListFragment) getSupportFragmentManager().findFragmentById(
				R.id.projectList);

		if (savedInstanceState == null) {
			android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			listFragment = new ProjectsListFragment();
			Log.v("reached111111", "till here");
			ft.replace(R.id.projectList, listFragment, "List_Fragment");

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			Log.v("reached", "till here");

		}

		if (findViewById(R.id.webSite) != null) {

			getFragmentManager().popBackStack();

			LandscapeHomeScreenWebFragment_Shruti webFragment = (LandscapeHomeScreenWebFragment_Shruti) getFragmentManager()
					.findFragmentById(R.id.webSite);
			if (webFragment == null) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				webFragment = new LandscapeHomeScreenWebFragment_Shruti();
				ft.replace(R.id.webSite, webFragment, "Detail_Fragment1");
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
				Log.v("shruti", "maybe");
				webFragment.setURLContent("https://pocketcode.org");
				Log.v("shruti", "yes");
			}
		}

	}

}
