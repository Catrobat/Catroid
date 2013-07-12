package org.catrobat.catroid.ui;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.ProjectsListFragment;
import org.catrobat.catroid.ui.fragment.WebViewFragment_Shruti;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainMenuActivity_Shruti extends SherlockFragmentActivity implements
		WebViewFragment_Shruti.OnHeadlineSelectedListener {
	public static int flag = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_landscape_home_screen_shruti);
		MainMenuActivityFragment_Shruti listFragment = (MainMenuActivityFragment_Shruti) getSupportFragmentManager()
				.findFragmentById(R.id.projectList);

		android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		listFragment = new MainMenuActivityFragment_Shruti();
		Log.v("reached111111", "till here");
		ft.replace(R.id.projectList, listFragment, "List_Fragment");

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
		Log.v("reached", "till here");

		/*
		 * ProjectsListFragment listFragment = (ProjectsListFragment) getSupportFragmentManager().findFragmentById(
		 * R.id.projectList);
		 * 
		 * if (savedInstanceState == null) {
		 * android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		 * listFragment = new ProjectsListFragment();
		 * Log.v("reached111111", "till here");
		 * ft.replace(R.id.projectList, listFragment, "List_Fragment");
		 * 
		 * ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		 * ft.commit();
		 * Log.v("reached", "till here");
		 */

		if (findViewById(R.id.webSite) != null) {

			getFragmentManager().popBackStack();

			WebViewFragment_Shruti webFragment = (WebViewFragment_Shruti) getSupportFragmentManager().findFragmentById(
					R.id.webSite);

			android.support.v4.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
			webFragment = new WebViewFragment_Shruti();
			ft1.replace(R.id.webSite, webFragment, "Detail_Fragment1");
			ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft1.commit();

			webFragment.setURLContent("https://pocketcode.org");

		}
	}

	String yourData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.catroid.ui.fragment.WebViewFragment_Shruti.OnHeadlineSelectedListener#onArticleSelected(int)
	 */
	@Override
	public void onArticleSelected(int position) {
		// TODO Auto-generated method stub

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				yourData = intent.getStringExtra("tag");
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.your.app.DATA_BROADCAST");
		registerReceiver(receiver, filter);
		flag = position;
		Toast.makeText(this, "flag = " + flag + "and " + "position = " + position, Toast.LENGTH_LONG).show();
		try {
			if (flag == 1) {
				String flag1 = yourData;
				if (flag1.equals("1")) {
					//getFragmentManager().popBackStack();
					ProjectsListFragment f4 = new ProjectsListFragment();
					android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					ft.replace(R.id.projectList, f4); // f2_container is your FrameLayout container
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					ft.addToBackStack(null);
					ft.commit();

				}
			}
		} catch (Exception e) {
			Toast.makeText(this, "Error!!!!!!", Toast.LENGTH_LONG).show();
		}

	}
}
