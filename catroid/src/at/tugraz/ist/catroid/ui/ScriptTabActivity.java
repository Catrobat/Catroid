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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.utils.ActivityHelper;

public class ScriptTabActivity extends TabActivity {
	private ActivityHelper activityHelper = new ActivityHelper(this);

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		activityHelper.setupActionBar(false, this.getResources().getString(R.string.sprite_list));

		activityHelper.addActionButton(R.id.btn_action_add_sprite, R.drawable.ic_plus_black,
				new View.OnClickListener() {
					public void onClick(View v) {
						showDialog(Consts.DIALOG_NEW_SPRITE);
					}
				}, false);

		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ScriptTabActivity.this, StageActivity.class);
				startActivity(intent);
			}
		}, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripttab);

		//Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ScriptActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("script").setIndicator("Script").setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, ScriptActivity.class);// just for demo because CostumeActivity is not ready yet
		//intent = new Intent().setClass(this, CostumeActivity.class);
		spec = tabHost.newTabSpec("costumes").setIndicator("Costumes").setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ScriptActivity.class);//just for demo because SoundActivity is not done yet
		spec = tabHost.newTabSpec("sounds").setIndicator("Sounds").setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(2);
	}
}
