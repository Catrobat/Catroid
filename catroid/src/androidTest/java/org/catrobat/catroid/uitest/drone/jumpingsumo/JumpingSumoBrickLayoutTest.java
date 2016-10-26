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
package org.catrobat.catroid.uitest.drone.jumpingsumo;

import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class JumpingSumoBrickLayoutTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public JumpingSumoBrickLayoutTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();
		UiTestUtils.prepareStageForTest();
		TestUtils.loadExistingOrCreateDefaultJumpingSumoProject(getActivity());
		SettingsActivity.enableJumpingSumoBricks(getActivity(), true);
	}

	@Override
	public void tearDown() throws Exception {
		SettingsActivity.enableJumpingSumoBricks(getActivity(), false);
		TestUtils.deleteTestProjects();
		solo.finishOpenedActivities();
		super.tearDown();
	}

	@Device
	public void testJumpingSumoBricksPrototypeView() {
		solo.waitForActivity(MainMenuActivity.class);

		assertEquals("Cannot create standard jumping sumo project",
				getActivity().getString(R.string.default_jumping_sumo_project_name), ProjectManager.getInstance()
						.getCurrentProject().getName()
		);
		assertEquals("The program name is wrong!", solo.getString(R.string
				.default_jumping_sumo_project_name), ProjectManager.getInstance().getCurrentProject().getName());

		solo.clickOnText(solo.getString(R.string.programs));
		solo.waitForText(solo.getString(R.string.default_jumping_sumo_project_name));
		solo.clickOnText(solo.getString(R.string.default_jumping_sumo_project_name));
		solo.waitForText(solo.getString(R.string.default_jumping_sumo_project_sprites_flip));
		solo.clickOnText(solo.getString(R.string.default_jumping_sumo_project_sprites_flip));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		solo.waitForText(solo.getString(R.string.category_control));

		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollListToBottom(fragmentListView);

		solo.waitForText(solo.getString(R.string.category_jumping_sumo));
		solo.clickOnText(solo.getString(R.string.category_jumping_sumo));
		solo.getText(solo.getString(R.string.brick_jumping_sumo_turn));
		solo.getText(solo.getString(R.string.brick_jumping_sumo_jump_long));
		solo.getText(solo.getString(R.string.brick_jumping_sumo_jump_high));
		solo.getText(solo.getString(R.string.brick_jumping_sumo_animation));
		solo.getText(solo.getString(R.string.brick_jumping_sumo_move_forward));
		solo.getText(solo.getString(R.string.brick_jumping_sumo_move_backward));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		solo.getText(solo.getString(R.string.brick_jumping_sumo_rotate_left));
		solo.getText(solo.getString(R.string.brick_jumping_sumo_rotate_right));
		solo.getText(solo.getString(R.string.brick_show_battery_status));
		solo.goBack();
		solo.scrollUpList(fragmentListView);
	}

	@Device
	public void testProjectCreationAfterDeletion() {
		solo.waitForActivity(MainMenuActivity.class);

		assertEquals("The program name is wrong!", solo.getString(R.string
				.default_jumping_sumo_project_name), ProjectManager.getInstance().getCurrentProject().getName());

		solo.waitForText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(solo.getString(R.string.main_menu_programs));

		if (solo.searchText(solo.getString(R.string.default_project_name))) {
			solo.clickLongOnText(solo.getString(R.string.default_project_name));
			solo.clickOnText(solo.getString(R.string.delete));
			solo.clickOnText(solo.getString(R.string.yes));
		}

		solo.waitForText(solo.getString(R.string.default_jumping_sumo_project_name));
		solo.clickLongOnText(solo.getString(R.string.default_jumping_sumo_project_name));
		solo.clickOnText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.yes));
		solo.waitForDialogToClose();

		assertTrue("Wrong Project was created! Should be the default Project!", solo.searchText(solo.getString(R.string
				.default_project_name)));

		SettingsActivity.enableARDroneBricks(getActivity(), false);

		solo.waitForText(solo.getString(R.string.default_project_name));
		solo.clickLongOnText(solo.getString(R.string.default_project_name));
		solo.clickOnText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.yes));
		solo.waitForText(solo.getString(R.string.default_project_name));

		assertTrue("Wrong Project was created! Fix this please!", solo.searchText(solo.getString(R.string.default_project_name)));

		solo.goBack();
	}
}
