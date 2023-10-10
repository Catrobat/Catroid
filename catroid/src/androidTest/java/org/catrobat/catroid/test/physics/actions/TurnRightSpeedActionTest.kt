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
import org.junit.runner.RunWith
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import org.catrobat.catroid.test.physics.actions.TurnRightSpeedActionTest
import org.catrobat.catroid.physics.PhysicsObject
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class TurnRightSpeedActionTest {
    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    @Before
    fun setUp() {
        sprite = rule.sprite
        physicsWorld = rule.physicsWorld
    }

    @Test
    fun testNormalBehavior() {
        initRightSpeedValue(SPEED)
        Assert.assertEquals(-SPEED, physicsWorld!!.getPhysicsObject(sprite).rotationSpeed)
    }

    @Test
    fun testNegativeValue() {
        val speed = -45.55f
        initRightSpeedValue(speed)
        Assert.assertEquals(-speed, physicsWorld!!.getPhysicsObject(sprite).rotationSpeed)
    }

    @Test
    fun testZeroValue() {
        val speed = 0f
        initRightSpeedValue(speed)
        Assert.assertEquals(-speed, physicsWorld!!.getPhysicsObject(sprite).rotationSpeed)
    }

    private fun initRightSpeedValue(speed: Float) {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val action = sprite!!.actionFactory.createTurnRightSpeedAction(
            sprite,
            SequenceAction(), Formula(speed)
        )
        Assert.assertEquals(0.0f, physicsObject.rotationSpeed)
        action.act(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createTurnRightSpeedAction(
            sprite, SequenceAction(),
            Formula(SPEED.toString())
        ).act(1.0f)
        Assert.assertEquals(-SPEED, physicsObject.rotationSpeed)
        sprite!!.actionFactory.createTurnRightSpeedAction(
            sprite,
            SequenceAction(),
            Formula("not a numerical string")
        ).act(1.0f)
        Assert.assertEquals(-SPEED, physicsObject.rotationSpeed)
    }

    @Test
    fun testNullFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createTurnRightSpeedAction(sprite, SequenceAction(), null).act(1.0f)
        Assert.assertEquals(-0f, physicsObject.rotationSpeed)
    }

    @Test
    fun testNotANumberFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createTurnRightSpeedAction(
            sprite, SequenceAction(),
            Formula(Double.NaN)
        ).act(1.0f)
        Assert.assertEquals(0f, physicsObject.rotationSpeed)
    }

    companion object {
        private const val SPEED = 45.55f
    }
}
