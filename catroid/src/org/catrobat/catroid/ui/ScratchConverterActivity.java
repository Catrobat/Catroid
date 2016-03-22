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

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.fragment.ScratchSearchProjectsListFragment;

public class ScratchConverterActivity extends BaseActivity {

    private static final String TAG = ScratchConverterActivity.class.getSimpleName();

    private ScratchSearchProjectsListFragment scratchSearchProjectsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_converter);
        setUpActionBar();
        scratchSearchProjectsListFragment = (ScratchSearchProjectsListFragment)getFragmentManager().findFragmentById(
                R.id.fragment_scratch_search_projects_list);
        Log.i(TAG, "Scratch Converter Activity created");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scratch_projects, menu);
        return super.onCreateOptionsMenu(menu);
    }

   	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		handleShowDetails(scratchSearchProjectsListFragment.getShowDetails(),
                menu.findItem(R.id.menu_scratch_projects_show_details));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.menu_scratch_projects_convert:
                Log.d(TAG, "Selected menu item 'convert'");
                scratchSearchProjectsListFragment.startConvertActionMode();
                break;
			case R.id.menu_scratch_projects_show_details:
                Log.d(TAG, "Selected menu item 'Show/Hide details'");
				handleShowDetails(!scratchSearchProjectsListFragment.getShowDetails(), item);
				break;
		}
        return super.onOptionsItemSelected(item);
    }

    private void setUpActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.title_activity_scratch_converter);
        actionBar.setHomeButtonEnabled(true);
    }

    private void handleShowDetails(boolean showDetails, MenuItem item) {
        scratchSearchProjectsListFragment.setShowDetails(showDetails);
        item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
    }

}
