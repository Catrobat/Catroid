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
package org.catrobat.catroid.test.physics.look

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import kotlin.Throws
import com.badlogic.gdx.math.Vector2
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class PhysicsLookPositionAndAngleTest {
    private var physicsObject: PhysicsObject? = null
    private var physicsLook: PhysicsLook? = null
    private var physicsWorldSpy: PhysicsWorld? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        val sprite = Mockito.mock(
            Sprite::class.java
        )
        physicsObject = Mockito.mock(PhysicsObject::class.java)
        physicsWorldSpy = Mockito.spy(PhysicsWorld(1920, 1600))
        Mockito.`when`(physicsWorldSpy!!.getPhysicsObject(sprite)).thenReturn(physicsObject)
        physicsLook = PhysicsLook(sprite, physicsWorldSpy)
    }

    @Test
    fun testPositionSetX() {
        val x = 0.5f
        physicsLook!!.x = x
        Mockito.verify(physicsObject, Mockito.times(1))!!.x = ArgumentMatchers.eq(x)
        Mockito.verifyNoMoreInteractions(physicsObject)
    }

    @Test
    fun testPositionGetX() {
        val x = 0.5f
        Mockito.`when`(physicsObject!!.x).thenReturn(x)
        Assert.assertEquals(x, physicsLook!!.x)
    }

    @Test
    fun testPositionSetY() {
        val y = 0.5f
        physicsLook!!.y = y
        Mockito.verify(physicsObject, Mockito.times(1))!!.y = ArgumentMatchers.eq(y)
        Mockito.verifyNoMoreInteractions(physicsObject)
    }

    @Test
    fun testPositionGetY() {
        val y = 0.5f
        Mockito.`when`(physicsObject!!.y).thenReturn(y)
        Assert.assertEquals(y, physicsLook!!.y)
    }

    @Test
    fun testSetPosition() {
        val x = 5.6f
        val y = 7.8f
        physicsLook!!.setPosition(x, y)
        Mockito.verify(physicsObject, Mockito.times(1))!!.x =
            ArgumentMatchers.eq(x)
        Mockito.verify(physicsObject, Mockito.times(1))!!.y =
            ArgumentMatchers.eq(y)
        Mockito.verifyNoMoreInteractions(physicsObject)
    }

    @Test
    fun testSetRotation() {
        val rotation = 9.0f
        physicsLook!!.rotation = rotation
        Mockito.verify(physicsObject, Mockito.times(1))!!.direction =
            ArgumentMatchers.eq(rotation)
    }

    @Test
    fun testCloneValues() {
        val cloneSprite = Mockito.mock(
            Sprite::class.java
        )
        val clonePhysicsObject = Mockito.mock(
            PhysicsObject::class.java
        )
        Mockito.`when`(physicsWorldSpy!!.getPhysicsObject(cloneSprite))
            .thenReturn(clonePhysicsObject)
        val cloneLook = PhysicsLook(cloneSprite, physicsWorldSpy)
        PhysicsWorld.activeArea = Vector2(0.0f, 0.0f)
        Mockito.`when`(clonePhysicsObject.massCenter).thenReturn(Vector2(0.0f, 0.0f))
        Mockito.`when`(clonePhysicsObject.circumference).thenReturn(0.0f)
        Mockito.doNothing().`when`(clonePhysicsObject).setFixedRotation(false)
        physicsLook!!.brightnessInUserInterfaceDimensionUnit = 32f
        physicsLook!!.copyTo(cloneLook)
        Assert.assertEquals(
            physicsLook!!.brightnessInUserInterfaceDimensionUnit,
            cloneLook.brightnessInUserInterfaceDimensionUnit
        )
        Mockito.verify(physicsObject, Mockito.times(1))!!.copyTo(clonePhysicsObject)
    }
}
