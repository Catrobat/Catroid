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
import org.catrobat.catroid.test.physics.actions.SetVelocityActionTest
import org.catrobat.catroid.physics.PhysicsObject
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.formulaeditor.Formula
import com.badlogic.gdx.math.Vector2
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class SetVelocityActionTest {
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
        initVelocityValue(VELOCITY_X, VELOCITY_Y)
        Assert.assertEquals(VELOCITY_X, physicsWorld!!.getPhysicsObject(sprite).velocity.x)
        Assert.assertEquals(VELOCITY_Y, physicsWorld!!.getPhysicsObject(sprite).velocity.y)
    }

    @Test
    fun testNegativeValue() {
        val velocityX = 10.0f
        val velocityY = -10.0f
        initVelocityValue(velocityX, velocityY)
        Assert.assertEquals(velocityX, physicsWorld!!.getPhysicsObject(sprite).velocity.x)
        Assert.assertEquals(velocityY, physicsWorld!!.getPhysicsObject(sprite).velocity.y)
    }

    @Test
    fun testZeroValue() {
        val velocityX = 0.0f
        val velocityY = 10.0f
        initVelocityValue(velocityX, velocityY)
        Assert.assertEquals(velocityX, physicsWorld!!.getPhysicsObject(sprite).velocity.x)
        Assert.assertEquals(velocityY, physicsWorld!!.getPhysicsObject(sprite).velocity.y)
    }

    private fun initVelocityValue(velocityX: Float, velocityY: Float) {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val action = sprite!!.actionFactory.createSetVelocityAction(
            sprite, SequenceAction(), Formula(velocityX),
            Formula(velocityY)
        )
        val velocityVector = physicsObject.velocity
        Assert.assertEquals(0.0f, velocityVector.x)
        Assert.assertEquals(0.0f, velocityVector.y)
        action.act(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetVelocityAction(
            sprite, SequenceAction(), Formula(VELOCITY_X.toString()),
            Formula(VELOCITY_Y.toString())
        ).act(1.0f)
        var velocityVector = physicsObject.velocity
        Assert.assertEquals(VELOCITY_X, velocityVector.x)
        Assert.assertEquals(VELOCITY_Y, velocityVector.y)
        sprite!!.actionFactory.createSetVelocityAction(
            sprite, SequenceAction(), Formula("not a numerical string"),
            Formula("not a numerical string")
        ).act(1.0f)
        velocityVector = physicsObject.velocity
        Assert.assertEquals(VELOCITY_X, velocityVector.x)
        Assert.assertEquals(VELOCITY_Y, velocityVector.y)
    }

    @Test
    fun testNullFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetVelocityAction(sprite, SequenceAction(), null, null)
            .act(1.0f)
        val velocityVector = physicsObject.velocity
        Assert.assertEquals(0f, velocityVector.x)
        Assert.assertEquals(0f, velocityVector.y)
    }

    @Test
    fun testNotANumberFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetVelocityAction(
            sprite, SequenceAction(), Formula(Double.NaN), Formula(
                Double.NaN
            )
        )
            .act(1.0f)
        val velocityVector = physicsObject.velocity
        Assert.assertEquals(0f, velocityVector.x)
        Assert.assertEquals(0f, velocityVector.y)
    }

    companion object {
        private const val VELOCITY_X = 10.0f
        private const val VELOCITY_Y = 11.0f
    }
}
