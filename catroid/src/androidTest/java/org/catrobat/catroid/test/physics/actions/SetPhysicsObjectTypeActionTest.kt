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
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import org.catrobat.catroid.physics.PhysicsObject
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class SetPhysicsObjectTypeActionTest {
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
    fun testPhysicsTypeNone() {
        val type = PhysicsObject.Type.NONE
        initPhysicsTypeValue(type)
        Assert.assertEquals(type, physicsWorld!!.getPhysicsObject(sprite).type)
    }

    @Test
    fun testPhysicsTypeDynamic() {
        val type = PhysicsObject.Type.DYNAMIC
        initPhysicsTypeValue(type)
        Assert.assertEquals(type, physicsWorld!!.getPhysicsObject(sprite).type)
    }

    @Test
    fun testPhysicsTypeFixed() {
        val type = PhysicsObject.Type.FIXED
        initPhysicsTypeValue(type)
        Assert.assertEquals(type, physicsWorld!!.getPhysicsObject(sprite).type)
    }

    private fun initPhysicsTypeValue(type: PhysicsObject.Type) {
        val physicsObject = physicsWorld!!.getPhysicsObject(sprite)
        val action = sprite!!.actionFactory.createSetPhysicsObjectTypeAction(sprite, type)
        Assert.assertEquals(PhysicsObject.Type.NONE, physicsObject.type)
        action.act(1.0f)
    }
}
