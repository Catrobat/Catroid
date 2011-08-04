/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.ui;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.utils.ActivityHelper;

public class ScriptTabActivity extends TabActivity {
	protected ActivityHelper activityHelper;

	private TabHost tabHost;

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripttab);

		setupTabHost();
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		Intent intent; // Reusable Intent for each tab

		intent = new Intent().setClass(this, ScriptActivity.class);
		setupTab(new TextView(this), this.getString(R.string.scripts), intent);
		intent = new Intent().setClass(this, CostumeActivity.class);
		setupTab(new TextView(this), this.getString(R.string.costumes), intent);
		intent = new Intent().setClass(this, SoundActivity.class);
		setupTab(new TextView(this), this.getString(R.string.sounds), intent);

		setUpActionBar();
	}

	private void setUpActionBar() {
		activityHelper = new ActivityHelper(this);

		String title = this.getResources().getString(R.string.sprite_name) + " "
				+ ProjectManager.getInstance().getCurrentSprite().getName();
		activityHelper.setupActionBar(false, title);

		activityHelper.addActionButton(R.id.btn_action_add_sprite, R.drawable.ic_plus_black, null, false);

		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ScriptTabActivity.this, StageActivity.class);
				startActivity(intent);
			}
		}, false);
	}

	private void setupTab(final View view, final String tag, Intent intent) {
		View tabview = createTabView(tabHost.getContext(), tag);

		TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		tabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_tabscriptactivity_tabs, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}
