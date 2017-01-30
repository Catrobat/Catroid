/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.BackPackActivityFragment;

public class BackPackActivity extends CoreActivity {

	public static final String TAG = BackPackActivity.class.getSimpleName();
	public static final String FRAGMENT = "fragment";

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_backpack);

		Class<? extends Fragment> initialFragment = (Class<? extends Fragment>) getIntent().getSerializableExtra(FRAGMENT);
		loadFragment(initialFragment);
	}

	@Override
	public void onPause() {
		BackPackListManager.getInstance().saveBackpack();
		super.onPause();
	}

	private BackPackActivityFragment getFragment() {
		return (BackPackActivityFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_backpack, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem showDetailsItem = menu.findItem(R.id.show_details);
		if (showDetailsItem != null) {
			getFragment().setShowDetailsTitle(showDetailsItem);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.unpack:
				getFragment().startUnpackActionMode();
				break;
			case R.id.delete:
				getFragment().startDeleteActionMode();
				break;
			case R.id.show_details:
				getFragment().setShowDetails(!getFragment().getShowDetails());
				getFragment().setShowDetailsTitle(menuItem);
				break;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
		return true;
	}
}
