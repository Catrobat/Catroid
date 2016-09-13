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
package org.catrobat.catroid.uitest.drone;

import org.catrobat.catroid.R;
import org.catrobat.catroid.test.drone.DroneTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class DroneLookTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public DroneLookTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		DroneTestUtils.createDefaultDroneProject();
		SettingsActivity.enableARDroneBricks(getActivity(), true);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.enableARDroneBricks(getActivity(), false);
		TestUtils.deleteTestProjects();
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testAddLook() {
		TestUtils.loadExistingOrCreateDefaultDroneProject(getActivity());

		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.programs));
		solo.waitForText(solo.getString(R.string.default_drone_project_name));
		solo.clickOnText(solo.getString(R.string.default_drone_project_name));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		boolean value = solo.waitForText(solo.getString(R.string.add_look_drone_video));
		assertTrue(solo.getString(R.string.add_look_drone_video) + " not found!", value);
		solo.clickOnText(solo.getString(R.string.add_look_drone_video));
		solo.enterText(0, "Test 12345");
		solo.clickOnText(solo.getString(R.string.ok));

		solo.goBack();
		solo.goBack();
	}
}
