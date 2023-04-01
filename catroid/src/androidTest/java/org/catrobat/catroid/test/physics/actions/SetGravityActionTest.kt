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
import kotlin.Throws
import org.catrobat.catroid.test.physics.actions.SetGravityActionTest
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class SetGravityActionTest {
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
    @Throws(Exception::class)
    fun testNormalBehavior() {
        val gravityX = GRAVITY_X
        val gravityY = GRAVITY_Y
        initGravityValues(gravityX, gravityY)
        val gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World)
            .gravity
        Assert.assertEquals(gravityX, gravityVector.x)
        Assert.assertEquals(gravityY, gravityVector.y)
    }

    @Test
    @Throws(Exception::class)
    fun testNegativeValue() {
        val gravityX = 10.0f
        val gravityY = -10.0f
        initGravityValues(gravityX, gravityY)
        val gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World)
            .gravity
        Assert.assertEquals(gravityX, gravityVector.x)
        Assert.assertEquals(gravityY, gravityVector.y)
    }

    @Test
    @Throws(Exception::class)
    fun testZeroValue() {
        val gravityX = 0.0f
        val gravityY = 10.0f
        initGravityValues(gravityX, gravityY)
        val gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World)
            .gravity
        Assert.assertEquals(gravityX, gravityVector.x)
        Assert.assertEquals(gravityY, gravityVector.y)
    }

    @Throws(Exception::class)
    private fun initGravityValues(gravityX: Float, gravityY: Float) {
        val action = sprite!!.actionFactory.createSetGravityAction(
            sprite,
            SequenceAction(), Formula(gravityX),
            Formula(gravityY)
        )
        val gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World)
            .gravity
        Assert.assertEquals(PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x)
        Assert.assertEquals(PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y)
        action.act(1.0f)
    }

    @Test
    @Throws(Exception::class)
    fun testBrickWithStringFormula() {
        sprite!!.actionFactory.createSetGravityAction(
            sprite,
            SequenceAction(), Formula(GRAVITY_X.toString()),
            Formula(GRAVITY_Y.toString())
        ).act(1.0f)
        var gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World)
            .gravity
        Assert.assertEquals(GRAVITY_X, gravityVector.x)
        Assert.assertEquals(GRAVITY_Y, gravityVector.y)
        sprite!!.actionFactory.createSetGravityAction(
            sprite, SequenceAction(),
            Formula("not a numerical string"),
            Formula("not a numerical string")
        ).act(1.0f)
        gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java,
            physicsWorld,
            "world"
        ) as World).gravity
        Assert.assertEquals(GRAVITY_X, gravityVector.x)
        Assert.assertEquals(GRAVITY_Y, gravityVector.y)
    }

    @Test
    @Throws(Exception::class)
    fun testNullFormula() {
        sprite!!.actionFactory.createSetGravityAction(
            sprite, SequenceAction(), null,
            null
        ).act(1.0f)
        val gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World)
            .gravity
        Assert.assertEquals(0f, gravityVector.x)
        Assert.assertEquals(0f, gravityVector.y)
    }

    @Test
    @Throws(Exception::class)
    fun testNotANumberFormula() {
        sprite!!.actionFactory.createSetGravityAction(
            sprite, SequenceAction(),
            Formula(Double.NaN),
            Formula(Double.NaN)
        )
            .act(1.0f)
        val gravityVector = (Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World)
            .gravity
        Assert.assertEquals(PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x)
        Assert.assertEquals(PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y)
    }

    companion object {
        private const val GRAVITY_X = 10.0f
        private const val GRAVITY_Y = 10.0f
    }
}
