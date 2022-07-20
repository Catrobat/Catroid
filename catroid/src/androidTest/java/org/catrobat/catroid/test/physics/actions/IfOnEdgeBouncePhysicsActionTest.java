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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.actions.IfOnEdgeBouncePhysicsAction;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
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

		float setYValue = -ScreenValues.SCREEN_HEIGHT / 2.0f;
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

		float setYValue = ScreenValues.SCREEN_HEIGHT / 2.0f;
		sprite.look.setYInUserInterfaceDimensionUnit(setYValue);
		float setVelocityYValue = IfOnEdgeBouncePhysicsAction.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE + 0.5f;
		physicsObject.setVelocity(physicsObject.getVelocity().x, setVelocityYValue);

		float yInUserInterfaceDimensionUnit = setYValue - sprite.look.getHeight() / 2;
		assertEquals(yInUserInterfaceDimensionUnit, sprite.look.getY());
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

		float setXValue = ScreenValues.SCREEN_WIDTH / 2.0f - sprite.look.getLookData().getPixmap().getWidth() / 4.0f;
		sprite.look.setXInUserInterfaceDimensionUnit(setXValue);
		float setYValue = ScreenValues.SCREEN_HEIGHT / 2.0f - sprite.look.getLookData().getPixmap().getHeight() / 4.0f;
		sprite.look.setYInUserInterfaceDimensionUnit(setYValue);

		float setVelocityXValue = 400.0f;
		float setVelocityYValue = 400.0f;
		physicsObject.setVelocity(setVelocityXValue, setVelocityYValue);

		float yInUserInterfaceDimensionUnit = setYValue - sprite.look.getHeight() / 2;
		float xInUserInterfaceDimensionUnit = setXValue - sprite.look.getWidth() / 2;
		assertEquals(yInUserInterfaceDimensionUnit, sprite.look.getY());
		assertEquals(xInUserInterfaceDimensionUnit, sprite.look.getX());

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

		float setXValue = ScreenValues.SCREEN_WIDTH / 2.0f - sprite.look.getLookData().getPixmap().getWidth() / 4.0f;
		sprite.look.setXInUserInterfaceDimensionUnit(setXValue);
		float setYValue = ScreenValues.SCREEN_HEIGHT / 2.0f - sprite.look.getLookData().getPixmap().getHeight() / 4.0f;
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

	private Action setUpBounceWithSteps(float direction, float xValue, float yValue) {
		assertNotNull(sprite.look.getLookData());
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		physicsObject.setType(PhysicsObject.Type.DYNAMIC);
		sprite.look.setXInUserInterfaceDimensionUnit(xValue);
		sprite.look.setYInUserInterfaceDimensionUnit(yValue);
		sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(direction);
		Action ifOnEdgeBouncePhysicsAction = sprite.getActionFactory().createIfOnEdgeBounceAction(sprite);
		sprite.movedByStepsBrick = true;
		return ifOnEdgeBouncePhysicsAction;
	}

	@Test
	public void testOnEdgeBounceWithStepsRight() {
		Action ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(90.0f,
				ScreenValues.SCREEN_WIDTH / 2.0f, sprite.look.getYInUserInterfaceDimensionUnit());
		float xValueBeforeAct = sprite.look.getXInUserInterfaceDimensionUnit();
		float directionBefore = sprite.look.getMotionDirectionInUserInterfaceDimensionUnit();
		ifOnEdgeBouncePhysicsAction.act(0.1f);
		float xValueAfterAct = sprite.look.getXInUserInterfaceDimensionUnit();
		float directionAfter = sprite.look.getMotionDirectionInUserInterfaceDimensionUnit();
		physicsWorld.step(1.0f);
		assertThat(xValueBeforeAct, is(greaterThan(xValueAfterAct)));
		assertEquals(-directionBefore, directionAfter);
	}

	@Test
	public void testOnEdgeBounceWithStepsLeft() {
		Action ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(-90.0f,
				-ScreenValues.SCREEN_WIDTH / 2.0f, sprite.look.getYInUserInterfaceDimensionUnit());
		float xValueBeforeAct = sprite.look.getXInUserInterfaceDimensionUnit();
		float directionBefore = sprite.look.getMotionDirectionInUserInterfaceDimensionUnit();
		ifOnEdgeBouncePhysicsAction.act(0.1f);
		float xValueAfterAct = sprite.look.getXInUserInterfaceDimensionUnit();
		float directionAfter = sprite.look.getMotionDirectionInUserInterfaceDimensionUnit();
		physicsWorld.step(1.0f);
		assertThat(xValueBeforeAct, is(lessThan(xValueAfterAct)));
		assertEquals(-directionBefore, directionAfter);
	}

	@Test
	public void testOnEdgeBounceWithStepsTop() {
		Action ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(0,
				sprite.look.getXInUserInterfaceDimensionUnit(), ScreenValues.SCREEN_HEIGHT / 2.0f);
		float yValueBeforeAct = sprite.look.getYInUserInterfaceDimensionUnit();
		ifOnEdgeBouncePhysicsAction.act(0.1f);
		float yValueAfterAct = sprite.look.getYInUserInterfaceDimensionUnit();
		physicsWorld.step(1.0f);
		assertThat(yValueBeforeAct, is(greaterThan(yValueAfterAct)));
		assertEquals(180.0f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit());
	}

	@Test
	public void testOnEdgeBounceWithStepsBottom() {
		Action ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(180,
				sprite.look.getXInUserInterfaceDimensionUnit(), -ScreenValues.SCREEN_HEIGHT / 2.0f);
		float yValueBeforeAct = sprite.look.getYInUserInterfaceDimensionUnit();
		ifOnEdgeBouncePhysicsAction.act(0.1f);
		float yValueAfterAct = sprite.look.getYInUserInterfaceDimensionUnit();
		physicsWorld.step(1.0f);
		assertThat(yValueBeforeAct, is(lessThan(yValueAfterAct)));
		assertEquals(0.0f, sprite.look.getMotionDirectionInUserInterfaceDimensionUnit());
	}

	@Test
	public void testFlipRightEdgeWithStepsAndLeftRightRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY, 90.0f,
				ScreenValues.SCREEN_WIDTH / 2.0f, 0);
		assertTrue(sprite.look.isFlipped());
	}

	@Test
	public void testFlipLeftEdgeWithStepsAndLeftRightRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY, -90.0f,
				-ScreenValues.SCREEN_WIDTH / 2.0f, 0);
		assertFalse(sprite.look.isFlipped());
	}

	@Test
	public void testFlipTopEdgeWithStepsAndLeftRightRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY, 0, 0, ScreenValues.SCREEN_HEIGHT / 2.0f);
		assertFalse(sprite.look.getLookData().getTextureRegion().isFlipY());
	}

	@Test
	public void testFlipBottomEdgeWithStepsAndLeftRightRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_LEFT_RIGHT_ONLY, 180, 0, ScreenValues.SCREEN_HEIGHT / 2.0f);
		assertFalse(sprite.look.getLookData().getTextureRegion().isFlipY());
	}

	@Test
	public void testFlipRightEdgeWithStepsAndNoRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_NONE, 90.0f,
				ScreenValues.SCREEN_WIDTH / 2.0f, 0);
		assertFalse(sprite.look.isFlipped());
	}

	@Test
	public void testFlipLeftEdgeWithStepsAndNoRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_NONE, -90.0f,
				-ScreenValues.SCREEN_WIDTH / 2.0f, 0);
		assertFalse(sprite.look.isFlipped());
	}

	@Test
	public void testFlipTopEdgeWithStepsAndNoRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_NONE, 0, 0, ScreenValues.SCREEN_HEIGHT / 2.0f);
		assertFalse(sprite.look.getLookData().getTextureRegion().isFlipY());
	}

	@Test
	public void testFlipBottomEdgeWithStepsAndNoRotationStyle() {
		startActionWithRotationStyle(Look.ROTATION_STYLE_NONE, 180, 0, ScreenValues.SCREEN_HEIGHT / 2.0f);
		assertFalse(sprite.look.getLookData().getTextureRegion().isFlipY());
	}

	private void startActionWithRotationStyle(int rotationStyle, float direction, float xValue, float yValue) {
		sprite.look.setRotationMode(rotationStyle);
		Action ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(direction, xValue, yValue);
		ifOnEdgeBouncePhysicsAction.act(0.1f);
	}

	private boolean allActionsOfAllSpritesAreFinished() {
		for (Sprite spriteOfList : project.getDefaultScene().getSpriteList()) {
			if (!spriteOfList.look.haveAllThreadsFinished()) {
				return false;
			}
		}
		return true;
	}
}
