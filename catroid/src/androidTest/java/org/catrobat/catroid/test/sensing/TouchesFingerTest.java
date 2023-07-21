/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.test.physics.collision.CollisionTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.TouchUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class TouchesFingerTest {
	protected Project project;
	protected Sprite sprite1;
	protected Sprite sprite2;

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects();

		project = new Project(ApplicationProvider.getApplicationContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite1 = new Sprite("TestSprite1");
		sprite2 = new Sprite("TestSprite2");
		project.getDefaultScene().addSprite(sprite1);
		project.getDefaultScene().addSprite(sprite2);

		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setCurrentProject(project);

		CollisionTestUtils.initializeSprite(sprite1, org.catrobat.catroid.test.R.raw.collision_donut,
				"collision_donut.png", InstrumentationRegistry.getInstrumentation().getContext(), project);
		CollisionTestUtils.initializeSprite(sprite2,
				org.catrobat.catroid.test.R.raw.rectangle_125x125,
				"rectangle_125x125.png", InstrumentationRegistry.getInstrumentation().getContext(), project);
	}

	@Test
	public void testBasicOneTouchingPoint() {
		TouchUtil.reset();
		TouchUtil.touchDown(170, 0, 1);
		assertEquals(1d, CollisionDetection.collidesWithFinger(
				sprite1.look.getCurrentCollisionPolygon(), TouchUtil.getCurrentTouchingPoints()),
				0d);

		TouchUtil.reset();
		TouchUtil.touchDown(0, 0, 1);
		assertEquals(0d, CollisionDetection.collidesWithFinger(
				sprite1.look.getCurrentCollisionPolygon(), TouchUtil.getCurrentTouchingPoints()),
				0d);
	}

	@Test
	public void testBasicMultipleTouchingPoints() {
		TouchUtil.reset();
		TouchUtil.touchDown(150, 150, 1);
		TouchUtil.touchDown(0, 0, 2);
		TouchUtil.touchDown(225, 0, 3);
		assertEquals(1d, CollisionDetection.collidesWithFinger(
				sprite1.look.getCurrentCollisionPolygon(), TouchUtil.getCurrentTouchingPoints()),
				0d);
	}

	@Test
	public void testAdvancedOneTouchingPoint() {
		TouchUtil.reset();
		TouchUtil.touchDown(0, 0, 1);

		assertNotEquals(1d, CollisionDetection.collidesWithFinger(
				sprite1.look.getCurrentCollisionPolygon(), TouchUtil.getCurrentTouchingPoints()));

		float x = sprite1.look.getXInUserInterfaceDimensionUnit();
		float y = sprite1.look.getYInUserInterfaceDimensionUnit();

		sprite1.look.setXInUserInterfaceDimensionUnit(x - 225);
		sprite1.look.setYInUserInterfaceDimensionUnit(y - 225);

		assertEquals(1d, CollisionDetection.collidesWithFinger(
				sprite1.look.getCurrentCollisionPolygon(), TouchUtil.getCurrentTouchingPoints()),
				0d);
	}

	@Test
	public void testTouchingRadius() {
		float distanceToBorder = 125.0f / 2.0f;
		float width =
				ProjectManager.getInstance().getCurrentProject().getXmlHeader().getVirtualScreenWidth();
		float touchRadius = (width / (float) ScreenValues.currentScreenResolution.getWidth())
				* Constants.COLLISION_WITH_FINGER_TOUCH_RADIUS;
		float offset = 10.0f;

		ActionFactory factory = new ActionFactory();
		sprite2.setActionFactory(factory);

		factory.createSetXAction(sprite2, new SequenceAction(), new Formula(100)).act(1.0f);
		factory.createSetYAction(sprite2, new SequenceAction(), new Formula(100)).act(1.0f);

		// Tap outside sprite, but within touch-radius
		TouchUtil.reset();
		TouchUtil.touchDown(100 + distanceToBorder + touchRadius - offset, 100,
				1);

		assertEquals(1d,
				CollisionDetection.collidesWithFinger(sprite2.look.getCurrentCollisionPolygon(),
						TouchUtil.getCurrentTouchingPoints()), 0d);

		// Tap outside sprite and touch-radius
		TouchUtil.reset();
		TouchUtil.touchDown(100 + distanceToBorder + touchRadius + offset, 100, 1);

		assertEquals(0d,
				CollisionDetection.collidesWithFinger(sprite2.look.getCurrentCollisionPolygon(),
						TouchUtil.getCurrentTouchingPoints()), 0d);
	}
}
