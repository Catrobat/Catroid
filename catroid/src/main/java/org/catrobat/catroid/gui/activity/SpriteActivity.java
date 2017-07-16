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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.gui.dialog.NewItemDialog;
import org.catrobat.catroid.gui.dialog.NewItemDialog.NewItemInterface;
import org.catrobat.catroid.gui.fragment.LookListFragment;
import org.catrobat.catroid.gui.fragment.ScriptListFragment;
import org.catrobat.catroid.gui.fragment.SoundListFragment;
import org.catrobat.catroid.gui.tabpager.SpriteFragmentPager;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.gui.fragment.SpriteListFragment.SELECTED_SPRITE;

public class SpriteActivity extends BaseAppCompatActivity {

	public static final String TAG = SpriteActivity.class.getSimpleName();

	private SpriteFragmentPager fragmentPager;
	private ViewPager viewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sprite);

		String spriteName = getIntent().getStringExtra(SELECTED_SPRITE);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(spriteName);

		viewPager = (ViewPager) findViewById(R.id.pager);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

		List<String> titles = new ArrayList<>();
		titles.add(getString(R.string.looks));
		titles.add(getString(R.string.sounds));
		titles.add(getString(R.string.scripts));

		List<Fragment> fragments = new ArrayList<>();
		fragments.add(new LookListFragment());
		fragments.add(new SoundListFragment());
		fragments.add(new ScriptListFragment());

		fragmentPager = new SpriteFragmentPager(getSupportFragmentManager(), titles, fragments);

		viewPager.setAdapter(fragmentPager);
		tabLayout.setupWithViewPager(viewPager);
	}

	public void handleAddButton(View view) {
		Fragment fragment = fragmentPager.getItem(viewPager.getCurrentItem());

		NewItemDialog dialog;

		switch (viewPager.getCurrentItem()) {
			case 0:
				dialog = new NewItemDialog(R.string.new_look, (NewItemInterface) fragment);
				break;
			case 1:
				dialog = new NewItemDialog(R.string.new_sound, (NewItemInterface) fragment);
				break;
			case 2:
			default:
				return;
		}

		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, dialog)
				.addToBackStack(null)
				.commit();
	}
}
