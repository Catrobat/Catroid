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
package org.catrobat.catroid.uitest.drone;

import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;

//import org.easymock.EasyMock;

public class DroneBrickLayoutTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public DroneBrickLayoutTest() {
		super(MainMenuActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	public void testSimpleUITest() {
		// UtilFile.deleteStandardDroneProject(getActivity());
		UtilFile.createStandardDroneProject(getActivity());
		solo.waitForActivity(MainMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForText(solo.getString(R.string.default_drone_project_sprites_takeoff));
		solo.clickOnText(solo.getString(R.string.default_drone_project_sprites_takeoff));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);

		solo.waitForText(solo.getString(R.string.category_drone));
		solo.clickOnText(solo.getString(R.string.category_drone));

		solo.getText(solo.getString(R.string.brick_drone_takeoff));
		solo.getText(solo.getString(R.string.brick_drone_land));
		solo.getText(solo.getString(R.string.brick_drone_play_led_animation));
		solo.getText(solo.getString(R.string.brick_drone_flip));
		solo.getText(solo.getString(R.string.brick_drone_move_up));
		solo.getText(solo.getString(R.string.brick_drone_move_down));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		solo.getText(solo.getString(R.string.brick_drone_move_left));
		solo.getText(solo.getString(R.string.brick_drone_move_right));
		solo.getText(solo.getString(R.string.brick_drone_move_forward));
		solo.getText(solo.getString(R.string.brick_drone_move_backward));
		solo.getText(solo.getString(R.string.brick_drone_turn_left));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		solo.getText(solo.getString(R.string.brick_drone_turn_right));

		solo.goBack();
		solo.scrollUpList(fragmentListView);
	}
}
