/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.catrobat.catroid.R;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.drone.DroneStageActivity;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.Utils;

public abstract class CoreActivity extends Activity {

	public static final String TAG = CoreActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new BaseExceptionHandler(this));
		Utils.checkIfCrashRecoveryAndFinishActivity(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	protected void loadFragment(Class<? extends Fragment> fragmentClass) {
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

		try {
			Fragment newFragment = fragmentClass.newInstance();
			fragmentTransaction.replace(R.id.fragment_container, newFragment, fragmentClass.getSimpleName());
			fragmentTransaction.commit();
		} catch (Exception e) {
			Log.e(TAG, "Error while instantiating new fragment" , e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_core_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.settings:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivity(settingsIntent);
				break;
			default:
				super.onOptionsItemSelected(menuItem);
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PreStageActivity.REQUEST_RESOURCES_INIT && resultCode == RESULT_OK) {
			Intent intent;
			if (DroneServiceWrapper.checkARDroneAvailability()) {
				intent = new Intent(this, DroneStageActivity.class);
			} else {
				intent = new Intent(this, StageActivity.class);
			}
			startActivity(intent);
		}
	}
}
