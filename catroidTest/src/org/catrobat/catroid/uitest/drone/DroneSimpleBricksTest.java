/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.List;

public class DroneSimpleBricksTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public DroneSimpleBricksTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		solo.waitForActivity(MainMenuActivity.class);
		solo.sleep(300);
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testDroneSwitchCamera() {

		UiTestUtils.addNewBrick(solo, R.string.category_drone, R.string.brick_drone_switch_camera);
		/*UiTestUtils.dragFloatingBrickDownwards(solo);

		solo.clickOnText(solo.getString(R.string.brick_drone_switch_camera));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.yes));

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("No Bricks should be in bricklist/project", 1, brickListToCheck.size());*/
	}

	public void testSetConfig() {

		UiTestUtils.addNewBrick(solo, R.string.category_drone, R.string.brick_drone_set_config);
		UiTestUtils.dragFloatingBrickDownwards(solo);

		solo.clickOnText(solo.getString(R.string.drone_config_default));
		solo.clickOnText(solo.getString(R.string.drone_config_indoor));

		solo.clickOnText(solo.getString(R.string.brick_drone_set_config));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.yes));

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("No Bricks should be in bricklist/project", 1, brickListToCheck.size());
	}

	public void testToggleCamera() {

		UiTestUtils.addNewBrick(solo, R.string.category_drone, R.string.brick_drone_toggle_video);
		UiTestUtils.dragFloatingBrickDownwards(solo);

		solo.clickOnText(solo.getString(R.string.brick_drone_toggle_video));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.yes));

		List<Brick> brickListToCheck = ProjectManager.getInstance().getCurrentScript().getBrickList();
		assertEquals("No Bricks should be in bricklist/project", 1, brickListToCheck.size());
	}
}
