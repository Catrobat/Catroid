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

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;
import at.tugraz.ist.catroid.R;

/**
 * @author ainulhusna
 * 
 */
public class ScriptActivityTab extends Activity {

	//private int ADD_NEW_TAB = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripttab);
		TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
		tabs.setup();

		//		TabSpec tspec1 = tabs.newTabSpec("First Tab");
		//		tspec1.setIndicator("One");
		//		tspec1.setContent(R.id.tabcontent);
		//		tabs.addTab(tspec1);
		//
		//		TabSpec tspec2 = tabs.newTabSpec("Second Tab");
		//		tspec2.setIndicator("Two");
		//		tspec2.setContent(R.id.tabcontent);
		//		tabs.addTab(tspec2);
		//		TabSpec tspec3 = tabs.newTabSpec("Third Tab");
		//		tspec3.setIndicator("Three");
		//		tspec3.setContent(R.id.tabcontent);
		//		tabs.addTab(tspec3);
	}

	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
	//super.onCreateOptionsMenu(menu);
	//menu.add(0, ADD_NEW_TAB, 0, “New Tabs”);
	//return true;
	//}

	//Dynamically delete tabs, then add one again
	//Problem with SDK 1.1 returns null pointer exception

	/*
	 * @Override
	 * public boolean onOptionsItemSelected(MenuItem item) {
	 * TabHost tabs = (TabHost) this.findViewById(R.id.my_tabhost);
	 * tabs.clearAllTabs();
	 * tabs.setup();
	 * TabSpec tspec1 = tabs.newTabSpec(”New Tab”);
	 * tspec1.setIndicator(”NEWTAB”, this.getResources().getDrawable(R.drawable.icon));
	 * tspec1.setContent(R.id.content);
	 * tabs.addTab(tspec1);
	 * return super.onOptionsItemSelected(item);
	 * }
	 */
}
