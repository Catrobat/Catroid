/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.backpack;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BaseActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class ActorAndObjectActivity extends BaseActivity {

	public static final String TAG = ActorAndObjectActivity.class.getSimpleName();

	public static final String EXTRA_FRAGMENT_POSITION = "fragmentPosition";
	public static final int FRAGMENT_SPRITES = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recycler_backpack);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.actor_object_title);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#83B3C7")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		switchToFragment();
	}

	private void switchToFragment() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, new ActorAndObjectSpriteFragment(), ActorAndObjectSpriteFragment.TAG);
		fragmentTransaction.commit();
	}
}
