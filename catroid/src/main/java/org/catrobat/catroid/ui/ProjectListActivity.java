/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment;

public class ProjectListActivity extends BaseCastActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recycler);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.project_list_title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		BottomBar.hidePlayButton(this);

		loadFragment(new ProjectListFragment());
	}

	private void loadFragment(Fragment fragment) {
		getFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, fragment, fragment.getTag())
				.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_projects_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			getFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}

	public void handleAddButton(View view) {
		((RecyclerViewFragment) getFragmentManager().findFragmentById(R.id.fragment_container)).handleAddButton();
	}
}
