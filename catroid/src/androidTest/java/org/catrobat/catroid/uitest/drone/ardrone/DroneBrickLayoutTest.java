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
package org.catrobat.catroid.uitest.drone.ardrone;

import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class DroneBrickLayoutTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public DroneBrickLayoutTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();
		UiTestUtils.prepareStageForTest();
		TestUtils.loadExistingOrCreateDefaultDroneProject(getActivity());
		SettingsActivity.enableARDroneBricks(getActivity(), true);
	}

	@Override
	public void tearDown() throws Exception {
		SettingsActivity.enableARDroneBricks(getActivity(), false);
		TestUtils.deleteTestProjects();
		solo.finishOpenedActivities();
		super.tearDown();
	}

	@Device
	public void testDroneBricksPrototypeView() {
		solo.waitForActivity(MainMenuActivity.class);

		assertEquals("Cannot create standard drone project",
				getActivity().getString(R.string.default_drone_project_name), ProjectManager.getInstance()
						.getCurrentProject().getName()
		);
		assertEquals("The program name is wrong!", solo.getString(R.string
				.default_drone_project_name), ProjectManager.getInstance().getCurrentProject().getName());

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForText(solo.getString(R.string.default_drone_project_sprites_takeoff));
		solo.clickOnText(solo.getString(R.string.default_drone_project_sprites_takeoff));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		solo.waitForText(solo.getString(R.string.category_control));

		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);

		solo.waitForText(solo.getString(R.string.category_drone));
		solo.clickOnText(solo.getString(R.string.category_drone));

		solo.getText(solo.getString(R.string.brick_drone_takeoff_land));
		//solo.getText(solo.getString(R.string.brick_drone_play_led_animation)); //TODO Drone: add when brick works, correct solo scroll down
		solo.getText(solo.getString(R.string.brick_drone_flip));
		solo.getText(solo.getString(R.string.brick_drone_emergency));
		solo.getText(solo.getString(R.string.brick_drone_move_up));
		solo.getText(solo.getString(R.string.brick_drone_move_down));
		solo.getText(solo.getString(R.string.brick_drone_move_left));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		solo.getText(solo.getString(R.string.brick_drone_move_right));
		solo.getText(solo.getString(R.string.brick_drone_move_forward));
		solo.getText(solo.getString(R.string.brick_drone_move_backward));
		solo.getText(solo.getString(R.string.brick_drone_turn_left));
		solo.getText(solo.getString(R.string.brick_drone_turn_right));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		solo.getText(solo.getString(R.string.brick_drone_switch_camera));
		//solo.getText(solo.getString(R.string.brick_drone_set_advanced_config));

		solo.goBack();
		solo.scrollUpList(fragmentListView);
	}

	@Device
	public void testDroneVideoLookVisibility() {
		solo.waitForActivity(MainMenuActivity.class);

		assertEquals("The program name is wrong!", solo.getString(R.string
				.default_drone_project_name), ProjectManager.getInstance().getCurrentProject().getName());

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForText(solo.getString(R.string.default_drone_project_sprites_takeoff));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		assertTrue("No video selection available", solo.searchText(solo.getString(R.string.add_look_drone_video), true));
		solo.goBack();

		SettingsActivity.enableARDroneBricks(getActivity(), false);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		assertFalse("Video selection still available", solo.searchText(solo.getString(R.string
				.add_look_drone_video), true));

		solo.goBack();
	}

	@Device
	public void testProjectCreationAfterDeletion() {
		solo.waitForActivity(MainMenuActivity.class);

		assertEquals("The program name is wrong!", solo.getString(R.string
				.default_drone_project_name), ProjectManager.getInstance().getCurrentProject().getName());

		solo.waitForText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(solo.getString(R.string.main_menu_programs));

		if (solo.searchText(solo.getString(R.string.default_project_name))) {
			solo.clickLongOnText(solo.getString(R.string.default_project_name));
			solo.clickOnText(solo.getString(R.string.delete));
			solo.clickOnText(solo.getString(R.string.yes));
		}

		solo.waitForText(solo.getString(R.string.default_drone_project_name));
		solo.clickLongOnText(solo.getString(R.string.default_drone_project_name));
		solo.clickOnText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.yes));
		solo.waitForDialogToClose();

		assertTrue("Wrong Project was created! Should be the Drone Project!", solo.searchText(solo.getString(R.string
				.default_drone_project_name)));

		SettingsActivity.enableARDroneBricks(getActivity(), false);

		solo.waitForText(solo.getString(R.string.default_drone_project_name));
		solo.clickLongOnText(solo.getString(R.string.default_drone_project_name));
		solo.clickOnText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.yes));
		solo.waitForDialogToClose();

		assertTrue("Wrong Project was created! Fix this please!", solo.searchText(solo.getString(R.string.default_project_name)));

		solo.goBack();
	}
}
