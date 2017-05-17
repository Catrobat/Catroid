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

package org.catrobat.catroid.uitest.content.brick;

import android.test.ActivityInstrumentationTestCase2;

import android.view.View;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.Locale;

public class Bricks_RTL_Test extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	public Bricks_RTL_Test() {
		super(MainMenuActivity.class);
	}

	private int[] Bricks = { R.id.brick_when_started_layout, R.id.brick_hide_layout, R.id.brick_show_layout,
			R.id.brick_set_size_to_layout, R.id.brick_go_back_layout, R.id.brick_go_to_front_layout,
			R.id.brick_place_at_layout };
	private Solo solo;

	@Override
	public void setUp() throws Exception {
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	//make sure that your SmartPhoneLanguage is Arabic or another RTL language
	public void testBrickScale() throws Exception {
		assertTrue(isRTL());
		solo.assertCurrentActivity("Something wrong! this Activity is not MainMenuActivity", MainMenuActivity.class);
		solo.clickOnView(solo.getView(R.id.main_menu_button_continue));
		solo.clickOnView(solo.getView(R.id.spritelist_item_background));
		solo.clickOnView(solo.getView(R.id.program_menu_button_scripts));
		solo.sleep(2000);
		for (int id = 1; id < Bricks.length; id++) {
			BrickScaleTest(Bricks[id]);
		}
	}

	private static boolean isRTL() {
		return isRTL(Locale.getDefault());
	}

	private static boolean isRTL(Locale locale) {
		final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
				directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}

	private void BrickScaleTest(int eventBricksID) {
		View BRICK = solo.getView(eventBricksID);
		assertTrue(BRICK.getScaleX() == -1);
	}
}




