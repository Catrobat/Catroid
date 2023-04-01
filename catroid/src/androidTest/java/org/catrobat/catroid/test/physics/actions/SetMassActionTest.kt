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
import org.catrobat.catroid.test.physics.actions.SetMassActionTest
import org.catrobat.catroid.physics.PhysicsObject
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class SetMassActionTest {
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
        initMassValue(MASS)
        Assert.assertEquals(MASS, physicsWorld!!.getPhysicsObject(sprite).mass)
    }

    @Test
    fun testNegativeValue() {
        val mass = -10f
        initMassValue(mass)
        Assert.assertEquals(PhysicsObject.MIN_MASS, physicsWorld!!.getPhysicsObject(sprite).mass)
    }

    @Test
    fun testZeroValue() {
        val mass = 0f
        initMassValue(mass)
        Assert.assertEquals(0.0f, physicsWorld!!.getPhysicsObject(sprite).mass)
    }

    private fun initMassValue(mass: Float) {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val action = sprite!!.actionFactory.createSetMassAction(
            sprite,
            SequenceAction(), Formula(mass)
        )
        Assert.assertEquals(PhysicsObject.DEFAULT_MASS, physicsObject.mass)
        action.act(1.0f)
        physicsWorld!!.step(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetMassAction(
            sprite,
            SequenceAction(), Formula(MASS.toString())
        ).act(1.0f)
        Assert.assertEquals(MASS, physicsObject.mass)
        sprite!!.actionFactory.createSetMassAction(
            sprite, SequenceAction(),
            Formula("not a numerical string")
        )
            .act(1.0f)
        Assert.assertEquals(MASS, physicsObject.mass)
    }

    @Test
    fun testNullFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetMassAction(sprite, SequenceAction(), null).act(1.0f)
        Assert.assertEquals(0f, physicsObject.mass)
    }

    @Test
    fun testNotANumberFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetMassAction(
            sprite, SequenceAction(),
            Formula(Double.NaN)
        ).act(1.0f)
        Assert.assertEquals(PhysicsObject.DEFAULT_MASS, physicsObject.mass)
    }

    @Test
    fun testMassAcceleration() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        physicsObject.type = PhysicsObject.Type.DYNAMIC
        physicsObject.mass = 5.0f
        physicsWorld!!.step(0.10f)
        val lastVelocity = Math.abs(physicsObject.velocity.y)
        physicsWorld!!.step(0.25f)
        physicsWorld!!.step(0.25f)
        physicsWorld!!.step(0.25f)
        physicsWorld!!.step(0.25f)
        val currentVelocity = Math.abs(physicsObject.velocity.y)
        org.junit.Assert.assertThat(
            currentVelocity - lastVelocity,
            Matchers.`is`(Matchers.greaterThan(1.0f))
        )
    }

    companion object {
        private const val MASS = 10f
    }
}
