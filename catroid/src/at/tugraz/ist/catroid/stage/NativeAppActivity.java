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
package at.tugraz.ist.catroid.stage;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Values;

public class NativeAppActivity extends StageActivity {
	private static Context context = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ProjectManager manager = ProjectManager.getInstance();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Values.SCREEN_WIDTH = dm.widthPixels;
		Values.SCREEN_HEIGHT = dm.heightPixels;

		context = this;

		manager.loadProject("project", this, false);
		manager = ProjectManager.getInstance();
		super.onCreate(savedInstanceState);
	}

	public static Context getContext() {
		return context;
	}

	public static boolean isRunning() {
		if (context == null) {
			return false;
		} else {
			return true;
		}
	}

	public static void setContext(Context context) {
		NativeAppActivity.context = context;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Do nothing.
		return true;
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
