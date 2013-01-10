/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.ui.dialogs.AboutDialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import org.catrobat.catroid.R;

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

		if (!manager.loadProject("project.xml", this, null, false)) {

			Builder builder = new AlertDialog.Builder(context);

			builder.setTitle(context.getString(R.string.error));
			builder.setMessage(context.getString(R.string.error_load_project));
			builder.setNeutralButton(context.getString(R.string.close), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.show();

		}
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
			AboutDialog aboutDialog = new AboutDialog(this);
			aboutDialog.show();
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
