/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.actions.IfOnEdgeBouncePhysicsAction;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class IfOnEdgeBouncePhysicsActionTest {

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;
	private Project project;

	@Before
	public void setUp() {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
		project = rule.project;
	}

	@Test
	public void testNormalBehavior() {

		assertNotNull(sprite.look.getLookData());

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);

		float setYValue = -ScreenValues.SCREEN_HEIGHT / 2 + 1; // So that nearly the half of the rectangle should be outside of the screen
		sprite.look.setYInUserInterfaceDimensionUnit(setYValue);
		float setVelocityYValue = -(IfOnEdgeBouncePhysicsAction.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE - 1.0f);
		physicsObject.setVelocity(physicsObject.getVelocity().x, setVelocityYValue);

		assertEquals(setYValue, sprite.look.getYInUserInterfaceDimensionUnit());

		ActionFactory factory = sprite.getActionFactory();
		Action ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);

		ifOnEdgeBouncePhysicsAction.act(0.1f);
		float setYValueAfterAct = sprite.look.getYInUserInterfaceDimensionUnit();

		physicsWorld.step(0.3f);

		assertThat(sprite.look.getYInUserInterfaceDimensionUnit(), is(greaterThan(setYValueAfterAct)));
	}

	@Test
	public void testVelocityThresholdAtTopCollision() {
		assertNotNull(sprite.look.getLookData());

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);

		float setYValue = ScreenValues.SCREEN_HEIGHT / 2 - 1; // So that nearly the half of the rectangle should be outside of the screen
		sprite.look.setYInUserInterfaceDimensionUnit(setYValue);
		float setVelocityYValue = IfOnEdgeBouncePhysicsAction.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE + 0.5f;
		physicsObject.setVelocity(physicsObject.getVelocity().x, setVelocityYValue);

		assertEquals(setYValue, sprite.look.getY());
		assertEquals(setVelocityYValue, physicsObject.getVelocity().y);

		ActionFactory factory = sprite.getActionFactory();
		Action ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);

		ifOnEdgeBouncePhysicsAction.act(0.1f);

		assertEquals(setVelocityYValue, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.step(0.3f);

		assertThat(sprite.look.getYInUserInterfaceDimensionUnit(), is(lessThan(setYValue)));
	}

	@Test
	public void testSpriteOverlapsRightAndTopAxis() {
		assertNotNull(sprite.look.getLookData());

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);

		float setXValue = ScreenValues.SCREEN_WIDTH / 2 - sprite.look.getLookData().getPixmap().getWidth() / 4;
		sprite.look.setXInUserInterfaceDimensionUnit(setXValue);
		float setYValue = ScreenValues.SCREEN_HEIGHT / 2 - sprite.look.getLookData().getPixmap().getHeight() / 4;
		sprite.look.setYInUserInterfaceDimensionUnit(setYValue);

		float setVelocityXValue = 400.0f;
		float setVelocityYValue = 400.0f;
		physicsObject.setVelocity(setVelocityXValue, setVelocityYValue);

		assertEquals(setXValue, sprite.look.getX());
		assertEquals(setYValue, sprite.look.getY());
		assertEquals(setVelocityXValue, physicsObject.getVelocity().x);
		assertEquals(setVelocityYValue, physicsObject.getVelocity().y);

		ActionFactory factory = sprite.getActionFactory();
		Action ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);

		ifOnEdgeBouncePhysicsAction.act(0.1f);

		float borderX = sprite.look.getXInUserInterfaceDimensionUnit();
		float borderY = sprite.look.getYInUserInterfaceDimensionUnit();

		assertThat(borderX, is(lessThan(setXValue)));
		assertThat(borderY, is(lessThan(setYValue)));

		assertEquals(setVelocityXValue, physicsObject.getVelocity().x, TestUtils.DELTA);
		assertEquals(setVelocityYValue, physicsObject.getVelocity().y, TestUtils.DELTA);

		physicsWorld.step(0.1f);

		float prevX = sprite.look.getXInUserInterfaceDimensionUnit();
		float prevY = sprite.look.getYInUserInterfaceDimensionUnit();
		ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);
		ifOnEdgeBouncePhysicsAction.act(0.1f);
		assertEquals(prevX, sprite.look.getXInUserInterfaceDimensionUnit(), TestUtils.DELTA);
		assertEquals(prevY, sprite.look.getYInUserInterfaceDimensionUnit(), TestUtils.DELTA);

		physicsWorld.step(2.3f);

		assertThat(sprite.look.getXInUserInterfaceDimensionUnit(), is(lessThan(setXValue)));
		assertThat(sprite.look.getYInUserInterfaceDimensionUnit(), is(lessThan(setYValue)));
	}

	@Test
	public void testCollisionBroadcastOnIfOnEdgeBounce() throws Exception {
		assertNotNull(sprite.look.getLookData());

		WhenBounceOffScript spriteWhenBounceOffScript = new WhenBounceOffScript(null);
		spriteWhenBounceOffScript.setSpriteToBounceOffName("");
		spriteWhenBounceOffScript.getScriptBrick();
		int testXValue = 300;
		int testYValue = 250;
		PlaceAtBrick testBrick = new PlaceAtBrick(testXValue, testYValue);
		spriteWhenBounceOffScript.addBrick(testBrick);
		sprite.addScript(spriteWhenBounceOffScript);

		sprite.initializeEventThreads(EventId.START);

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);

		float setXValue = ScreenValues.SCREEN_WIDTH / 2 - sprite.look.getLookData().getPixmap().getWidth() / 4;
		sprite.look.setXInUserInterfaceDimensionUnit(setXValue);
		float setYValue = ScreenValues.SCREEN_HEIGHT / 2 - sprite.look.getLookData().getPixmap().getHeight() / 4;
		sprite.look.setYInUserInterfaceDimensionUnit(setYValue);

		assertEquals(setXValue, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals(setYValue, sprite.look.getYInUserInterfaceDimensionUnit());

		float setVelocityXValue = 400.0f;
		float setVelocityYValue = 400.0f;
		physicsObject.setVelocity(setVelocityXValue, setVelocityYValue);

		ActionFactory factory = sprite.getActionFactory();
		Action ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite);

		ArrayList<Sprite> activeVerticalBounces = (ArrayList<Sprite>) Reflection.getPrivateField(PhysicsWorld.class,
				physicsWorld, "activeVerticalBounces");
		ArrayList<Sprite> activeHorizontalBounces = (ArrayList<Sprite>) Reflection.getPrivateField(PhysicsWorld.class,
				physicsWorld, "activeHorizontalBounces");

		assertTrue(activeVerticalBounces.isEmpty());
		assertTrue(activeHorizontalBounces.isEmpty());

		ifOnEdgeBouncePhysicsAction.act(1.0f);

		assertFalse(activeVerticalBounces.isEmpty());
		assertFalse(activeHorizontalBounces.isEmpty());

		assertThat(sprite.look.getXInUserInterfaceDimensionUnit(), is(lessThan(setXValue)));
		assertThat(sprite.look.getYInUserInterfaceDimensionUnit(), is(lessThan(setYValue)));

		physicsWorld.step(2.0f);

		assertTrue(activeVerticalBounces.isEmpty());
		assertTrue(activeHorizontalBounces.isEmpty());

		while (!allActionsOfAllSpritesAreFinished()) {
			for (Sprite spriteOfList : project.getDefaultScene().getSpriteList()) {
				spriteOfList.look.act(1.0f);
			}
		}

		assertEquals((float) testXValue, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals((float) testYValue, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	public boolean allActionsOfAllSpritesAreFinished() {
		for (Sprite spriteOfList : project.getDefaultScene().getSpriteList()) {
			if (!spriteOfList.look.haveAllThreadsFinished()) {
				return false;
			}
		}
		return true;
	}
}
