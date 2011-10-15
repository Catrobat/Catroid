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
package at.tugraz.ist.catroid.stage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;

public class NativeAppActivity extends StageActivity {
	private static Context context = null;
	private static final int DIALOG_ABOUT = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ProjectManager manager = ProjectManager.getInstance();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Values.SCREEN_WIDTH = dm.widthPixels;
		Values.SCREEN_HEIGHT = dm.heightPixels;

		context = this;

		manager.loadProject("project.xml", this, false);
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
		getMenuInflater().inflate(R.menu.nativeapp_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.nativeappMenuAbout) {
			showDialog(DIALOG_ABOUT);
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == DIALOG_ABOUT) {
			dialog = new AboutDialog(this);
		}
		return dialog;
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
