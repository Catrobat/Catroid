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
package org.catrobat.catroid.test.physics.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.scenes.scene2d.Action
import junit.framework.Assert
import org.junit.runner.RunWith
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.actions.IfOnEdgeBouncePhysicsAction
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.test.utils.TestUtils
import kotlin.Throws
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.Reflection
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import java.lang.Exception
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class IfOnEdgeBouncePhysicsActionTest {
    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    private var project: Project? = null
    @Before
    fun setUp() {
        sprite = rule.sprite
        physicsWorld = rule.physicsWorld
        project = rule.project
    }

    @Test
    fun testNormalBehavior() {
        Assert.assertNotNull(sprite!!.look.lookData)
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject.type = PhysicsObject.Type.DYNAMIC
        val setYValue = -ScreenValues.SCREEN_HEIGHT / 2.0f
        sprite!!.look.yInUserInterfaceDimensionUnit = setYValue
        val setVelocityYValue =
            -(IfOnEdgeBouncePhysicsAction.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE - 1.0f)
        physicsObject.setVelocity(physicsObject.velocity.x, setVelocityYValue)
        Assert.assertEquals(setYValue, sprite!!.look.yInUserInterfaceDimensionUnit)
        val factory = sprite!!.actionFactory
        val ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite)
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        val setYValueAfterAct = sprite!!.look.yInUserInterfaceDimensionUnit
        physicsWorld!!.step(0.3f)
        org.junit.Assert.assertThat(
            sprite!!.look.yInUserInterfaceDimensionUnit, Matchers.`is`(
                Matchers.greaterThan(setYValueAfterAct)
            )
        )
    }

    @Test
    fun testVelocityThresholdAtTopCollision() {
        Assert.assertNotNull(sprite!!.look.lookData)
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject.type = PhysicsObject.Type.DYNAMIC
        val setYValue = ScreenValues.SCREEN_HEIGHT / 2.0f
        sprite!!.look.yInUserInterfaceDimensionUnit = setYValue
        val setVelocityYValue =
            IfOnEdgeBouncePhysicsAction.THRESHOLD_VELOCITY_TO_ACTIVATE_BOUNCE + 0.5f
        physicsObject.setVelocity(physicsObject.velocity.x, setVelocityYValue)
        val yInUserInterfaceDimensionUnit = setYValue - sprite!!.look.height / 2
        Assert.assertEquals(yInUserInterfaceDimensionUnit, sprite!!.look.y)
        Assert.assertEquals(setVelocityYValue, physicsObject.velocity.y)
        val factory = sprite!!.actionFactory
        val ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite)
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        Assert.assertEquals(
            setVelocityYValue.toDouble(),
            physicsObject.velocity.y.toDouble(),
            TestUtils.DELTA
        )
        physicsWorld!!.step(0.3f)
        org.junit.Assert.assertThat(
            sprite!!.look.yInUserInterfaceDimensionUnit, Matchers.`is`(
                Matchers.lessThan(setYValue)
            )
        )
    }

    @Test
    fun testSpriteOverlapsRightAndTopAxis() {
        Assert.assertNotNull(sprite!!.look.lookData)
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject.type = PhysicsObject.Type.DYNAMIC
        val setXValue =
            ScreenValues.SCREEN_WIDTH / 2.0f - sprite!!.look.lookData.pixmap.width / 4.0f
        sprite!!.look.xInUserInterfaceDimensionUnit = setXValue
        val setYValue =
            ScreenValues.SCREEN_HEIGHT / 2.0f - sprite!!.look.lookData.pixmap.height / 4.0f
        sprite!!.look.yInUserInterfaceDimensionUnit = setYValue
        val setVelocityXValue = 400.0f
        val setVelocityYValue = 400.0f
        physicsObject.setVelocity(setVelocityXValue, setVelocityYValue)
        val yInUserInterfaceDimensionUnit = setYValue - sprite!!.look.height / 2
        val xInUserInterfaceDimensionUnit = setXValue - sprite!!.look.width / 2
        Assert.assertEquals(yInUserInterfaceDimensionUnit, sprite!!.look.y)
        Assert.assertEquals(xInUserInterfaceDimensionUnit, sprite!!.look.x)
        Assert.assertEquals(setVelocityXValue, physicsObject.velocity.x)
        Assert.assertEquals(setVelocityYValue, physicsObject.velocity.y)
        val factory = sprite!!.actionFactory
        var ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite)
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        val borderX = sprite!!.look.xInUserInterfaceDimensionUnit
        val borderY = sprite!!.look.yInUserInterfaceDimensionUnit
        org.junit.Assert.assertThat(borderX, Matchers.`is`(Matchers.lessThan(setXValue)))
        org.junit.Assert.assertThat(borderY, Matchers.`is`(Matchers.lessThan(setYValue)))
        Assert.assertEquals(
            setVelocityXValue.toDouble(),
            physicsObject.velocity.x.toDouble(),
            TestUtils.DELTA
        )
        Assert.assertEquals(
            setVelocityYValue.toDouble(),
            physicsObject.velocity.y.toDouble(),
            TestUtils.DELTA
        )
        physicsWorld!!.step(0.1f)
        val prevX = sprite!!.look.xInUserInterfaceDimensionUnit
        val prevY = sprite!!.look.yInUserInterfaceDimensionUnit
        ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite)
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        Assert.assertEquals(
            prevX.toDouble(),
            sprite!!.look.xInUserInterfaceDimensionUnit.toDouble(),
            TestUtils.DELTA
        )
        Assert.assertEquals(
            prevY.toDouble(),
            sprite!!.look.yInUserInterfaceDimensionUnit.toDouble(),
            TestUtils.DELTA
        )
        physicsWorld!!.step(2.3f)
        org.junit.Assert.assertThat(
            sprite!!.look.xInUserInterfaceDimensionUnit, Matchers.`is`(
                Matchers.lessThan(setXValue)
            )
        )
        org.junit.Assert.assertThat(
            sprite!!.look.yInUserInterfaceDimensionUnit, Matchers.`is`(
                Matchers.lessThan(setYValue)
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun testCollisionBroadcastOnIfOnEdgeBounce() {
        Assert.assertNotNull(sprite!!.look.lookData)
        val spriteWhenBounceOffScript = WhenBounceOffScript(null)
        spriteWhenBounceOffScript.spriteToBounceOffName = ""
        spriteWhenBounceOffScript.scriptBrick
        val testXValue = 300
        val testYValue = 250
        val testBrick = PlaceAtBrick(testXValue, testYValue)
        spriteWhenBounceOffScript.addBrick(testBrick)
        sprite!!.addScript(spriteWhenBounceOffScript)
        sprite!!.initializeEventThreads(EventId.START)
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject.type = PhysicsObject.Type.DYNAMIC
        val setXValue =
            ScreenValues.SCREEN_WIDTH / 2.0f - sprite!!.look.lookData.pixmap.width / 4.0f
        sprite!!.look.xInUserInterfaceDimensionUnit = setXValue
        val setYValue =
            ScreenValues.SCREEN_HEIGHT / 2.0f - sprite!!.look.lookData.pixmap.height / 4.0f
        sprite!!.look.yInUserInterfaceDimensionUnit = setYValue
        Assert.assertEquals(setXValue, sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(setYValue, sprite!!.look.yInUserInterfaceDimensionUnit)
        val setVelocityXValue = 400.0f
        val setVelocityYValue = 400.0f
        physicsObject.setVelocity(setVelocityXValue, setVelocityYValue)
        val factory = sprite!!.actionFactory
        val ifOnEdgeBouncePhysicsAction = factory.createIfOnEdgeBounceAction(sprite)
        val activeVerticalBounces = Reflection.getPrivateField(
            PhysicsWorld::class.java,
            physicsWorld, "activeVerticalBounces"
        ) as ArrayList<Sprite>
        val activeHorizontalBounces = Reflection.getPrivateField(
            PhysicsWorld::class.java,
            physicsWorld, "activeHorizontalBounces"
        ) as ArrayList<Sprite>
        Assert.assertTrue(activeVerticalBounces.isEmpty())
        Assert.assertTrue(activeHorizontalBounces.isEmpty())
        ifOnEdgeBouncePhysicsAction.act(1.0f)
        Assert.assertFalse(activeVerticalBounces.isEmpty())
        Assert.assertFalse(activeHorizontalBounces.isEmpty())
        org.junit.Assert.assertThat(
            sprite!!.look.xInUserInterfaceDimensionUnit, Matchers.`is`(
                Matchers.lessThan(setXValue)
            )
        )
        org.junit.Assert.assertThat(
            sprite!!.look.yInUserInterfaceDimensionUnit, Matchers.`is`(
                Matchers.lessThan(setYValue)
            )
        )
        physicsWorld!!.step(2.0f)
        Assert.assertTrue(activeVerticalBounces.isEmpty())
        Assert.assertTrue(activeHorizontalBounces.isEmpty())
        while (!allActionsOfAllSpritesAreFinished()) {
            for (spriteOfList in project!!.defaultScene.spriteList) {
                spriteOfList.look.act(1.0f)
            }
        }
        Assert.assertEquals(testXValue.toFloat(), sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(testYValue.toFloat(), sprite!!.look.yInUserInterfaceDimensionUnit)
    }

    private fun setUpBounceWithSteps(direction: Float, xValue: Float, yValue: Float): Action {
        Assert.assertNotNull(sprite!!.look.lookData)
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject.type = PhysicsObject.Type.DYNAMIC
        sprite!!.look.xInUserInterfaceDimensionUnit = xValue
        sprite!!.look.yInUserInterfaceDimensionUnit = yValue
        sprite!!.look.motionDirectionInUserInterfaceDimensionUnit = direction
        val ifOnEdgeBouncePhysicsAction = sprite!!.actionFactory.createIfOnEdgeBounceAction(sprite)
        sprite!!.movedByStepsBrick = true
        return ifOnEdgeBouncePhysicsAction
    }

    @Test
    fun testOnEdgeBounceWithStepsRight() {
        val ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(
            90.0f,
            ScreenValues.SCREEN_WIDTH / 2.0f,
            sprite!!.look.yInUserInterfaceDimensionUnit
        )
        val xValueBeforeAct = sprite!!.look.xInUserInterfaceDimensionUnit
        val directionBefore = sprite!!.look.motionDirectionInUserInterfaceDimensionUnit
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        val xValueAfterAct = sprite!!.look.xInUserInterfaceDimensionUnit
        val directionAfter = sprite!!.look.motionDirectionInUserInterfaceDimensionUnit
        physicsWorld!!.step(1.0f)
        org.junit.Assert.assertThat(
            xValueBeforeAct,
            Matchers.`is`(Matchers.greaterThan(xValueAfterAct))
        )
        Assert.assertEquals(-directionBefore, directionAfter)
    }

    @Test
    fun testOnEdgeBounceWithStepsLeft() {
        val ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(
            -90.0f,
            -ScreenValues.SCREEN_WIDTH / 2.0f,
            sprite!!.look.yInUserInterfaceDimensionUnit
        )
        val xValueBeforeAct = sprite!!.look.xInUserInterfaceDimensionUnit
        val directionBefore = sprite!!.look.motionDirectionInUserInterfaceDimensionUnit
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        val xValueAfterAct = sprite!!.look.xInUserInterfaceDimensionUnit
        val directionAfter = sprite!!.look.motionDirectionInUserInterfaceDimensionUnit
        physicsWorld!!.step(1.0f)
        org.junit.Assert.assertThat(
            xValueBeforeAct,
            Matchers.`is`(Matchers.lessThan(xValueAfterAct))
        )
        Assert.assertEquals(-directionBefore, directionAfter)
    }

    @Test
    fun testOnEdgeBounceWithStepsTop() {
        val ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(
            0f,
            sprite!!.look.xInUserInterfaceDimensionUnit, ScreenValues.SCREEN_HEIGHT / 2.0f
        )
        val yValueBeforeAct = sprite!!.look.yInUserInterfaceDimensionUnit
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        val yValueAfterAct = sprite!!.look.yInUserInterfaceDimensionUnit
        physicsWorld!!.step(1.0f)
        org.junit.Assert.assertThat(
            yValueBeforeAct,
            Matchers.`is`(Matchers.greaterThan(yValueAfterAct))
        )
        Assert.assertEquals(180.0f, sprite!!.look.motionDirectionInUserInterfaceDimensionUnit)
    }

    @Test
    fun testOnEdgeBounceWithStepsBottom() {
        val ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(
            180f,
            sprite!!.look.xInUserInterfaceDimensionUnit, -ScreenValues.SCREEN_HEIGHT / 2.0f
        )
        val yValueBeforeAct = sprite!!.look.yInUserInterfaceDimensionUnit
        ifOnEdgeBouncePhysicsAction.act(0.1f)
        val yValueAfterAct = sprite!!.look.yInUserInterfaceDimensionUnit
        physicsWorld!!.step(1.0f)
        org.junit.Assert.assertThat(
            yValueBeforeAct,
            Matchers.`is`(Matchers.lessThan(yValueAfterAct))
        )
        Assert.assertEquals(0.0f, sprite!!.look.motionDirectionInUserInterfaceDimensionUnit)
    }

    @Test
    fun testFlipRightEdgeWithStepsAndLeftRightRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_LEFT_RIGHT_ONLY, 90.0f,
            ScreenValues.SCREEN_WIDTH / 2.0f, 0f
        )
        Assert.assertTrue(sprite!!.look.isFlipped)
    }

    @Test
    fun testFlipLeftEdgeWithStepsAndLeftRightRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_LEFT_RIGHT_ONLY, -90.0f,
            -ScreenValues.SCREEN_WIDTH / 2.0f, 0f
        )
        Assert.assertFalse(sprite!!.look.isFlipped)
    }

    @Test
    fun testFlipTopEdgeWithStepsAndLeftRightRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_LEFT_RIGHT_ONLY,
            0f,
            0f,
            ScreenValues.SCREEN_HEIGHT / 2.0f
        )
        Assert.assertFalse(sprite!!.look.lookData.textureRegion.isFlipY)
    }

    @Test
    fun testFlipBottomEdgeWithStepsAndLeftRightRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_LEFT_RIGHT_ONLY,
            180f,
            0f,
            ScreenValues.SCREEN_HEIGHT / 2.0f
        )
        Assert.assertFalse(sprite!!.look.lookData.textureRegion.isFlipY)
    }

    @Test
    fun testFlipRightEdgeWithStepsAndNoRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_NONE, 90.0f,
            ScreenValues.SCREEN_WIDTH / 2.0f, 0f
        )
        Assert.assertFalse(sprite!!.look.isFlipped)
    }

    @Test
    fun testFlipLeftEdgeWithStepsAndNoRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_NONE, -90.0f,
            -ScreenValues.SCREEN_WIDTH / 2.0f, 0f
        )
        Assert.assertFalse(sprite!!.look.isFlipped)
    }

    @Test
    fun testFlipTopEdgeWithStepsAndNoRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_NONE,
            0f,
            0f,
            ScreenValues.SCREEN_HEIGHT / 2.0f
        )
        Assert.assertFalse(sprite!!.look.lookData.textureRegion.isFlipY)
    }

    @Test
    fun testFlipBottomEdgeWithStepsAndNoRotationStyle() {
        startActionWithRotationStyle(
            Look.ROTATION_STYLE_NONE,
            180f,
            0f,
            ScreenValues.SCREEN_HEIGHT / 2.0f
        )
        Assert.assertFalse(sprite!!.look.lookData.textureRegion.isFlipY)
    }

    private fun startActionWithRotationStyle(
        rotationStyle: Int,
        direction: Float,
        xValue: Float,
        yValue: Float
    ) {
        sprite!!.look.rotationMode = rotationStyle
        val ifOnEdgeBouncePhysicsAction = setUpBounceWithSteps(direction, xValue, yValue)
        ifOnEdgeBouncePhysicsAction.act(0.1f)
    }

    private fun allActionsOfAllSpritesAreFinished(): Boolean {
        for (spriteOfList in project!!.defaultScene.spriteList) {
            if (!spriteOfList.look.haveAllThreadsFinished()) {
                return false
            }
        }
        return true
    }
}
