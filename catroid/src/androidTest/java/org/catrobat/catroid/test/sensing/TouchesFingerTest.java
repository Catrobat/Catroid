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

package org.catrobat.catroid.test.sensing;

import junit.framework.Assert;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.test.BaseInstrumentationTest;
import org.catrobat.catroid.test.utils.CollisionTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.TouchUtil;

public class TouchesFingerTest extends BaseInstrumentationTest {
	protected Project project;
	protected Sprite sprite1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite1 = createSprite("TestSprite1");
		project.getDefaultScene().addSprite(sprite1);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		CollisionTestUtils.initializeSprite(sprite1, org.catrobat.catroid.test.R.raw.collision_donut,
				"collision_donut.png", getInstrumentation().getContext(), project);
	}

	public void testBasicOneTouchingPoint() {
		TouchUtil.reset();
		TouchUtil.touchDown(150, 150, 1);
		Assert.assertTrue("Not detected as touching", CollisionDetection.collidesWithFinger(sprite1.look) == 1d);
		TouchUtil.touchUp(1);
		TouchUtil.touchDown(0, 0, 1);
		Assert.assertFalse("Detected as touching", CollisionDetection.collidesWithFinger(sprite1.look) == 1d);
	}

	public void testBasicMultipleTouchingPoints() {
		TouchUtil.reset();
		TouchUtil.touchDown(150, 150, 1);
		TouchUtil.touchDown(0, 0, 2);
		TouchUtil.touchDown(151, 151, 3);
		Assert.assertTrue("Not detected as touching", CollisionDetection.collidesWithFinger(sprite1.look) == 1d);
	}

	public void testAdvancedOneTouchingPoint() {
		TouchUtil.reset();
		TouchUtil.touchDown(0, 0, 1);

		Assert.assertFalse("Detected as touching", CollisionDetection.collidesWithFinger(sprite1.look) == 1d);

		float x = sprite1.look.getXInUserInterfaceDimensionUnit();
		float y = sprite1.look.getYInUserInterfaceDimensionUnit();

		sprite1.look.setXInUserInterfaceDimensionUnit(x - 150);
		sprite1.look.setYInUserInterfaceDimensionUnit(y - 150);

		Assert.assertTrue("Not detected as touching", CollisionDetection.collidesWithFinger(sprite1.look) == 1d);
	}
}
