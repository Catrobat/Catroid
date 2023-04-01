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
import org.catrobat.catroid.test.physics.PhysicsCollisionTestRule
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import com.badlogic.gdx.math.Vector2
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.test.physics.actions.SetBounceFactorActionTest
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Sprite
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class SetBounceFactorActionTest {
    @get:Rule
    var rule = PhysicsCollisionTestRule()
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    @Before
    fun setUp() {
        sprite = rule.sprite
        physicsWorld = rule.physicsWorld
        rule.spritePosition = Vector2(0.0f, 100.0f)
        rule.sprite2Position = Vector2(0.0f, -200.0f)
        rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC
        rule.physicsObject2Type = PhysicsObject.Type.FIXED
        rule.initializeSpritesForCollision()
    }

    @Test
    fun testNormalBounceFactor() {
        initBounceFactorValue(BOUNCE_FACTOR)
        Assert.assertEquals(
            BOUNCE_FACTOR / 100.0f,
            physicsWorld!!.getPhysicsObject(sprite).bounceFactor
        )
    }

    @Test
    fun testZeroValue() {
        val bounceFactor = 0.0f
        initBounceFactorValue(bounceFactor)
        Assert.assertEquals(
            bounceFactor / 100.0f,
            physicsWorld!!.getPhysicsObject(sprite).bounceFactor
        )
    }

    @Test
    fun testNegativeValue() {
        val bounceFactor = -50.0f
        initBounceFactorValue(bounceFactor)
        Assert.assertEquals(
            PhysicsObject.MIN_BOUNCE_FACTOR,
            physicsWorld!!.getPhysicsObject(sprite).bounceFactor
        )
    }

    @Test
    fun testHighValue() {
        val bounceFactor = 1000.0f
        initBounceFactorValue(bounceFactor)
        Assert.assertEquals(
            bounceFactor / 100.0f,
            physicsWorld!!.getPhysicsObject(sprite).bounceFactor
        )
    }

    private fun initBounceFactorValue(bounceFactor: Float) {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val action = sprite!!.actionFactory.createSetBounceFactorAction(
            sprite,
            SequenceAction(), Formula(bounceFactor)
        )
        Assert.assertEquals(PhysicsObject.DEFAULT_BOUNCE_FACTOR, physicsObject.bounceFactor)
        action.act(1.0f)
        physicsWorld!!.step(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetBounceFactorAction(
            sprite,
            SequenceAction(), Formula(BOUNCE_FACTOR.toString())
        )
            .act(1.0f)
        Assert.assertEquals(BOUNCE_FACTOR / 100f, physicsObject.bounceFactor)
        sprite!!.actionFactory.createSetBounceFactorAction(
            sprite, SequenceAction(),
            Formula("not a numerical string")
        ).act(1.0f)
        Assert.assertEquals(BOUNCE_FACTOR / 100f, physicsObject.bounceFactor)
    }

    @Test
    fun testNullFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetBounceFactorAction(sprite, SequenceAction(), null).act(1.0f)
        Assert.assertEquals(0f, physicsObject.bounceFactor)
    }

    @Test
    fun testNotANumberFormula() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        sprite!!.actionFactory.createSetBounceFactorAction(
            sprite, SequenceAction(),
            Formula(Double.NaN)
        ).act(1.0f)
        Assert.assertEquals(PhysicsObject.DEFAULT_BOUNCE_FACTOR, physicsObject.bounceFactor)
    }

    @Test
    fun testBounceWithDifferentValues() {
        val bounce01Height = bounce(0.1f)
        val bounce06Height = bounce(0.6f)
        org.junit.Assert.assertThat(
            bounce01Height,
            Matchers.`is`(Matchers.lessThan(bounce06Height))
        )
    }

    private fun bounce(bounceFactor: Float): Float {
        rule.initializeSpritesForCollision()
        physicsWorld!!.getPhysicsObject(sprite).setVelocity(0f, 0f)
        physicsWorld!!.getPhysicsObject(sprite).mass = 20f
        physicsWorld!!.getPhysicsObject(sprite).bounceFactor = bounceFactor
        while (!rule.collisionDetected()) {
            physicsWorld!!.step(0.3f)
        }
        var y = physicsWorld!!.getPhysicsObject(sprite).y + ScreenValues.SCREEN_HEIGHT / 2
        physicsWorld!!.step(0.3f)
        while (y < physicsWorld!!.getPhysicsObject(sprite).y + ScreenValues.SCREEN_HEIGHT / 2) {
            y = physicsWorld!!.getPhysicsObject(sprite).y + ScreenValues.SCREEN_HEIGHT / 2
            physicsWorld!!.step(0.3f)
        }
        return y
    }

    companion object {
        private const val BOUNCE_FACTOR = 50f
    }
}
