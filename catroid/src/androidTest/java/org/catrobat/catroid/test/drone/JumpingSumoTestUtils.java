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
package org.catrobat.catroid.test.drone;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoBrickFactory;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoBrickFactory.JumpingSumoBricks;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public abstract class JumpingSumoTestUtils {

	private static final int DEFAULT_MOVE_TIME_IN_MILLISECONDS = 2000;
	private static final byte DEFAULT_MOVE_POWER_IN_PERCENT = 80;
	private static final float DEFAULT_ROTATE_IN_DEGREE = 90;

	public static void createDefaultJumpingSumoProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new SingleSprite("JumpingSumoBricksTest");
		Script script = new StartScript();

		for (JumpingSumoBricks brick : JumpingSumoBricks.values()) {
			BrickBaseType currentBrick = JumpingSumoBrickFactory.getInstanceOfJumpingSumoBrick(brick,
					DEFAULT_MOVE_TIME_IN_MILLISECONDS, DEFAULT_MOVE_POWER_IN_PERCENT, DEFAULT_ROTATE_IN_DEGREE, 0, 0);
			script.addBrick(currentBrick);
			sprite.addScript(script);
		}
		project.getDefaultScene().addSprite(sprite);
		setProjectAsCurrentProject(project, sprite, script);
	}

	private static void setProjectAsCurrentProject(Project project, Sprite sprite, Script script) {
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
