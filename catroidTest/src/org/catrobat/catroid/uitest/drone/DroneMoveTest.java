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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.drone.DroneUtils;
import org.catrobat.catroid.drone.DroneUtils.DroneBricks;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class DroneMoveTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private static final int TIME_IN_MILLISECONDS = 2000;
	private static final int POWER_IN_PERCENT = 20;
	private static final int TIME_IN_SECONDS_TO_CHANGE = 3;
	private static final int POWER_IN_PERCENT_TO_CHANGE = 40;
	private Project project;

	public DroneMoveTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {

		createProject();
		super.setUp();
	}

	public void testAllMoveBricks() {

		int numberOfBricks = ProjectManager.getInstance().getCurrentScript().getBrickList().size();

		for (int count = 0; count < numberOfBricks; count++) {
			makeSingleBrickTest(count);
		}
	}

	private void makeSingleBrickTest(int brickIndex) {

		Brick brickTest = ProjectManager.getInstance().getCurrentScript().getBrick(0);

		assertNotNull("TextView does not exist.", solo.getView(R.id.brick_drone_move_text_view_second));

		UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_drone_move_edit_text_second, TIME_IN_SECONDS_TO_CHANGE,
				"timeToFlyInSeconds", brickTest);

		assertNotNull("TextView does not exist.", solo.getView(R.id.brick_drone_move_text_view_power));
		assertNotNull("TextView does not exist.", solo.getView(R.id.brick_set_power_to_percent));
		UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_drone_move_edit_text_power, POWER_IN_PERCENT_TO_CHANGE,
				"powerInPercent", brickTest);

		solo.clickOnView(solo.getView(R.id.brick_drone_move_label));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.yes));
	}

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("DoneMoveBricksTest");
		Script script = new StartScript(sprite);

		for (DroneBricks brick : DroneUtils.DroneBricks.values()) {

			if (brick.name().toLowerCase().contains("move") || brick.name().toLowerCase().contains("turn")) {

				BrickBaseType moveBrick = DroneUtils.getInstanceOfDroneBrick(brick, sprite, TIME_IN_MILLISECONDS,
						POWER_IN_PERCENT);
				script.addBrick(moveBrick);

				sprite.addScript(script);
			}
		}

		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
