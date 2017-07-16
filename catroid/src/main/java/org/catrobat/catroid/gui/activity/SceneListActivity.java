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

package org.catrobat.catroid.gui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.gui.dialog.NewItemDialog;
import org.catrobat.catroid.gui.fragment.RecyclerViewListFragment;

public class SceneListActivity extends BaseAppCompatActivity {

	public static final String TAG = SceneListActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scenes);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.scenes);
	}

	public void handleAddButton(View view) {
		NewItemDialog dialog = new NewItemDialog(R.string.new_scene, getFragment());

		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, dialog)
				.addToBackStack(null)
				.commit();
	}

	private RecyclerViewListFragment getFragment() {
		return (RecyclerViewListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
	}
}
