/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;

/**
 * This is a helper class that implements the management of tabs and all details of connecting a
 * ViewPager with associated TabHost. It relies on a trick. Normally a tab host has a simple API for
 * supplying a View or Intent that each tab will show. This is not sufficient for switching between
 * pages. So instead we make the content part of the tab host 0dp high (it is not shown) and the
 * TabsAdapter supplies its own dummy view to show as the tab content. It listens to changes in
 * tabs, and takes care of switch to the correct paged in the ViewPager whenever the selected tab
 * changes.
 * 
 * Taken from ActionBarSherlock sample.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener,
		ViewPager.OnPageChangeListener {

	private final Context mContext;
	private final TabHost mTabHost;
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle arguments;

		TabInfo(Class<?> _class, Bundle _arguments) {
			clss = _class;
			arguments = _arguments;
		}
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View view = new View(mContext);
			view.setMinimumWidth(0);
			view.setMinimumHeight(0);
			return view;
		}
	}

	public TabsPagerAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mTabHost = tabHost;
		mViewPager = pager;
		mTabHost.setOnTabChangedListener(this);
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle arguments) {
		tabSpec.setContent(new DummyTabFactory(mContext));

		TabInfo tabInfo = new TabInfo(clss, arguments);
		mTabs.add(tabInfo);
		mTabHost.addTab(tabSpec);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo tabInfo = mTabs.get(position);
		return Fragment.instantiate(mContext, tabInfo.clss.getName(), tabInfo.arguments);
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		mViewPager.setCurrentItem(position);

		ScriptTabActivity activity = (ScriptTabActivity) mContext;
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		if (fragment != null) {
			DragAndDropListView listView = fragment.getListView();
			if (listView != null) {
				listView.resetDraggingScreen();
			}

			BrickAdapter adapter = fragment.getAdapter();
			if (adapter != null) {
				adapter.removeDraggedBrick();
			}
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mTabHost.setCurrentTab(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}