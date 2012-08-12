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
package at.tugraz.ist.catroid.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.adapter.TabsPagerAdapter;
import at.tugraz.ist.catroid.ui.fragment.CostumeFragment;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;
import at.tugraz.ist.catroid.ui.fragment.SoundFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class BaseScriptTabActivity extends SherlockFragmentActivity {

	public static final int INDEX_TAB_SCRIPTS = 0;
	public static final int INDEX_TAB_COSTUMES = 1;
	public static final int INDEX_TAB_SOUNDS = 2;

	protected ViewPager viewPager;
	protected TabsPagerAdapter tabsAdapter;
	protected TabHost tabHost;

	public Fragment getTabFragment(int position) {
		if (position < 0 || position > 2) {
			throw new IllegalArgumentException("There is no tab Fragment with index: " + position);
		}

		return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + position);
	}

	public Fragment getCurrentTabFragment() {
		return getTabFragment(tabHost.getCurrentTab());
	}

	@Override
	protected void onStart() {
		super.onStart();
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		if (tabHost != null) {
			tabHost.setup();
			viewPager = (ViewPager) findViewById(R.id.pager);
			tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
			tabsAdapter = new TabsPagerAdapter(this, tabHost, viewPager);
		}
	}

	protected void setUpSpriteTabs() {
		tabsAdapter.clearTabs();
		setupTab(R.drawable.ic_tab_scripts_selector, getString(R.string.scripts), ScriptFragment.class, null);

		int costumeIcon;
		String costumeLabel;

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (ProjectManager.getInstance().getCurrentProject().getSpriteList().indexOf(currentSprite) == 0) {
			costumeIcon = R.drawable.ic_tab_background_selector;
			costumeLabel = this.getString(R.string.backgrounds);
		} else {
			costumeIcon = R.drawable.ic_tab_costumes_selector;
			costumeLabel = this.getString(R.string.costumes);
		}

		setupTab(costumeIcon, costumeLabel, CostumeFragment.class, null);
		setupTab(R.drawable.ic_tab_sounds_selector, getString(R.string.sounds), SoundFragment.class, null);
	}

	private void setupTab(Integer drawableId, final String tag, Class<?> clss, Bundle args) {
		tabsAdapter.addTab(tabHost.newTabSpec(tag).setIndicator(createTabView(drawableId, this, tag)), clss, args);
	}

	private static View createTabView(Integer id, final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_tabscriptactivity_tabs, null);
		TextView tabTextView = (TextView) view.findViewById(R.id.tabsText);
		ImageView tabImageView = (ImageView) view.findViewById(R.id.tabsIcon);
		tabTextView.setText(text);
		if (id != null) {
			tabImageView.setImageResource(id);
			tabImageView.setVisibility(ImageView.VISIBLE);
			tabImageView.setTag(id);
		}
		return view;
	}
}
