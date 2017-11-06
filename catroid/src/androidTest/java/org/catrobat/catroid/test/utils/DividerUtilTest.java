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

package org.catrobat.catroid.test.utils;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.LinearLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.utils.DividerUtil;

public final class DividerUtilTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private MainMenuActivity mainMenuActivity;
	private Context context;
	private LinearLayout linearLayout;

	public DividerUtilTest() {
		super(MainMenuActivity.class);
	}

	public void setUp() {
		mainMenuActivity = getActivity();
		context = mainMenuActivity.getApplicationContext();
		linearLayout = (LinearLayout) mainMenuActivity.findViewById(R.id.main_menu_buttons_container);

		DividerUtil.setElementSpacing(true);
	}

	public void testAddIcons() {
		setUp();
		assertNotNull("No current activity.", mainMenuActivity);
		assertNotNull("No current context.", context);
		assertNotNull("Container layout not found.", linearLayout);

		mainMenuActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DividerUtil.setDivider(context, linearLayout);
				int drawable = linearLayout.getDividerPadding();
				assertNotNull("No current DividerPadding drawable.", drawable);
				assertEquals("Divider height did not match the expected height.", DividerUtil.dividerHeight, drawable);
			}
		});
	}
}
