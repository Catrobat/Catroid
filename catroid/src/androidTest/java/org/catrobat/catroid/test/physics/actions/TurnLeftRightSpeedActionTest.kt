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
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.junit.runner.RunWith
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.physics.actions.TurnLeftRightSpeedActionTest
import org.catrobat.catroid.test.utils.TestUtils
import org.hamcrest.Matchers
import org.hamcrest.number.OrderingComparison
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class TurnLeftRightSpeedActionTest {
    private var physicsObject: PhysicsObject? = null

    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = rule.sprite
        physicsWorld = rule.physicsWorld
        physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject!!.setType(PhysicsObject.Type.DYNAMIC)
    }

    @Test
    fun testLeftSpeedRotation() {
        physicsObject!!.direction = 0f
        physicsObject!!.rotationSpeed = TURN_TEST_SPEED
        Assert.assertEquals(
            TURN_TEST_SPEED.toDouble(),
            physicsObject!!.rotationSpeed.toDouble(),
            TestUtils.DELTA
        )
        skipWorldStabilizingSteps()
        val expectedDegrees = TURN_TEST_SPEED * TEST_STEP_DELTA_TIME
        for (i in 0 until TEST_STEPS) {
            val preStepDirection = physicsObject!!.direction
            physicsWorld!!.step(TEST_STEP_DELTA_TIME)
            val postStepDirection = physicsObject!!.direction
            Assert.assertEquals(
                expectedDegrees.toDouble(),
                (postStepDirection - preStepDirection).toDouble(),
                TestUtils.DELTA
            )
            org.junit.Assert.assertThat(
                postStepDirection,
                Matchers.`is`(
                    OrderingComparison.greaterThan(preStepDirection)
                )
            )
        }
    }

    @Test
    fun testRightSpeedRotation() {
        physicsObject!!.direction = 0f
        physicsObject!!.rotationSpeed = -TURN_TEST_SPEED
        Assert.assertEquals(
            -TURN_TEST_SPEED.toDouble(),
            physicsObject!!.rotationSpeed.toDouble(),
            TestUtils.DELTA
        )
        skipWorldStabilizingSteps()
        val expectedDegrees = -TURN_TEST_SPEED * TEST_STEP_DELTA_TIME
        for (i in 0 until TEST_STEPS) {
            val preStepDirection = physicsObject!!.direction
            physicsWorld!!.step(TEST_STEP_DELTA_TIME)
            val postStepDirection = physicsObject!!.direction
            Assert.assertEquals(
                expectedDegrees.toDouble(),
                (postStepDirection - preStepDirection).toDouble(),
                TestUtils.DELTA
            )
            org.junit.Assert.assertThat(
                postStepDirection,
                Matchers.`is`(Matchers.lessThan(preStepDirection))
            )
        }
    }

    private fun skipWorldStabilizingSteps() {
        for (i in 0 until PhysicsWorld.STABILIZING_STEPS) {
            physicsWorld!!.step(1.0f)
        }
    }

    companion object {
        private const val TURN_TEST_SPEED = 10.0f
        private const val TEST_STEPS = 5
        private const val TEST_STEP_DELTA_TIME = 1.0f / 60.0f
    }
}
