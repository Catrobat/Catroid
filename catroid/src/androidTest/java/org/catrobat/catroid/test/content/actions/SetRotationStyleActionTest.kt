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
package org.catrobat.catroid.test.content.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Look
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.physics.PhysicsLook
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class SetRotationStyleActionTest {
    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = rule.sprite
        physicsWorld = rule.physicsWorld
    }

    @Test
    fun testNormalMode() {
        val factory = sprite!!.actionFactory
        val rotationStyleAction =
            factory.createSetRotationStyleAction(sprite, Look.ROTATION_STYLE_ALL_AROUND)
        val pointInDirectionAction = factory.createPointInDirectionAction(
            sprite,
            SequenceAction(),
            Formula(90)
        )
        rotationStyleAction.act(1.0f)
        pointInDirectionAction.act(1.0f)
        Assert.assertEquals(90f, sprite!!.look.motionDirectionInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNoMode() {
        val factory = sprite!!.actionFactory
        val rotationStyleAction =
            factory.createSetRotationStyleAction(sprite, Look.ROTATION_STYLE_NONE)
        val pointInDirectionAction = factory.createPointInDirectionAction(
            sprite,
            SequenceAction(),
            Formula(-90)
        )
        rotationStyleAction.act(1.0f)
        pointInDirectionAction.act(1.0f)
        Assert.assertEquals(-90f, sprite!!.look.motionDirectionInUserInterfaceDimensionUnit)
    }

    @Test
    fun testLRMode() {
        val factory = sprite!!.actionFactory
        val rotationStyleAction =
            factory.createSetRotationStyleAction(sprite, Look.ROTATION_STYLE_LEFT_RIGHT_ONLY)
        val pointInDirectionAction = factory.createPointInDirectionAction(
            sprite,
            SequenceAction(),
            Formula(-90)
        )
        rotationStyleAction.act(1.0f)
        pointInDirectionAction.act(1.0f)
        Assert.assertEquals(-90f, sprite!!.look.motionDirectionInUserInterfaceDimensionUnit)
    }

    //Directions here get funky because in physics there is no UI Degree Offset as in the normal looks
    //Right is Left, Left is Right, Up is Up and Down is Down
    @Test
    fun testNormalModeInPhysics() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val physicsLook = PhysicsLook(sprite, physicsWorld)
        physicsLook.rotationMode = Look.ROTATION_STYLE_ALL_AROUND
        physicsLook.rotation = 90f
        Assert.assertEquals(90f, physicsObject.direction)
        Assert.assertEquals(90f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = -90f
        Assert.assertEquals(-90f, physicsObject.direction)
        Assert.assertEquals(-90f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = 0f
        Assert.assertEquals(0f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = 180f
        Assert.assertEquals(180f, physicsObject.direction)
        Assert.assertEquals(180f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
    }

    @Test
    fun testNoModeInPhysics() {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val physicsLook = PhysicsLook(sprite, physicsWorld)
        physicsLook.rotationMode = Look.ROTATION_STYLE_NONE
        physicsLook.rotation = 90f
        Assert.assertEquals(90f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = -90f
        Assert.assertEquals(-90f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = 0f
        Assert.assertEquals(0f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = 180f
        Assert.assertEquals(180f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
    }

    @Test
    fun testLRModeInPhysics() {
        Assert.assertNotNull(sprite!!.look.lookData)
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val physicsLook = PhysicsLook(sprite, physicsWorld)
        physicsLook.lookData = sprite!!.look.lookData
        physicsLook.rotationMode = Look.ROTATION_STYLE_LEFT_RIGHT_ONLY
        physicsLook.rotation = 90f
        Assert.assertEquals(90f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertTrue(physicsLook.isFlipped)
        physicsLook.rotation = -90f
        Assert.assertEquals(-90f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = 0f
        Assert.assertEquals(0f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertFalse(physicsLook.isFlipped)
        physicsLook.rotation = 180f
        Assert.assertEquals(180f, physicsObject.direction)
        Assert.assertEquals(0f, physicsLook.rotation)
        Assert.assertTrue(physicsLook.isFlipped)
    }
}