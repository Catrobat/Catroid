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
import org.catrobat.catroid.test.physics.actions.SetFrictionActionTest
import org.catrobat.catroid.physics.PhysicsObject
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class SetFrictionActionTest {
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
        initFrictionValue(FRICTION)
        Assert.assertEquals(FRICTION / 100.0f, physicsWorld!!.getPhysicsObject(sprite).friction)
    }

    @Test
    fun testNegativeValue() {
        val friction = -1f
        initFrictionValue(friction)
        Assert.assertEquals(
            PhysicsObject.MIN_FRICTION,
            physicsWorld!!.getPhysicsObject(sprite).friction
        )
    }

    @Test
    fun testHighValue() {
        val friction = 101f
        initFrictionValue(friction)
        Assert.assertEquals(
            PhysicsObject.MAX_FRICTION,
            physicsWorld!!.getPhysicsObject(sprite).friction
        )
    }

    private fun initFrictionValue(frictionFactor: Float) {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val action = sprite!!.actionFactory.createSetFrictionAction(
            sprite,
            SequenceAction(), Formula(frictionFactor)
        )
        Assert.assertEquals(PhysicsObject.DEFAULT_FRICTION, physicsObject.friction)
        action.act(1.0f)
        physicsWorld!!.step(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetFrictionAction(
            sprite,
            SequenceAction(), Formula(FRICTION.toString())
        ).act(1.0f)
        Assert.assertEquals(FRICTION / 100f, physicsObject.friction)
        sprite!!.actionFactory.createSetFrictionAction(
            sprite, SequenceAction(),
            Formula("not a numerical string")
        )
            .act(1.0f)
        Assert.assertEquals(FRICTION / 100f, physicsObject.friction)
    }

    @Test
    fun testNullFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetFrictionAction(sprite, SequenceAction(), null).act(1.0f)
        Assert.assertEquals(0f, physicsObject.friction)
    }

    @Test
    fun testNotANumberFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetFrictionAction(
            sprite, SequenceAction(),
            Formula(Double.NaN)
        ).act(1.0f)
        Assert.assertEquals(PhysicsObject.DEFAULT_FRICTION, physicsObject.friction)
    }

    companion object {
        private const val FRICTION = 100f
    }
}