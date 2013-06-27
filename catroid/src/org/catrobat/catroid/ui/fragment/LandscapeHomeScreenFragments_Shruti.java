package org.catrobat.catroid.ui.fragment;

import org.catrobat.catroid.R;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class LandscapeHomeScreenFragments_Shruti extends SherlockFragmentActivity {

	boolean detailPage = false;

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
			detailPage = true;
			getFragmentManager().popBackStack();

			LandscapeHomeScreenWebFragment_Shruti detailFragment = (LandscapeHomeScreenWebFragment_Shruti) getFragmentManager()
					.findFragmentById(R.id.webSite);
			if (detailFragment == null) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				detailFragment = new LandscapeHomeScreenWebFragment_Shruti();
				ft.replace(R.id.webSite, detailFragment, "Detail_Fragment1");
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
				Log.v("shruti", "maybe");
				detailFragment.setURLContent("https://pocketcode.org");
				Log.v("shruti", "yes");
			}
		}

	}

}
