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
package org.catrobat.catroid.test.physics

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxNativesLoader
import junit.framework.Assert
import org.catrobat.catroid.physics.PhysicsBoundaryBox
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhysicsBoundaryBoxTest {
    private var world: World? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        world = World(PhysicsWorld.DEFAULT_GRAVITY, PhysicsWorld.IGNORE_SLEEPING_OBJECTS)
    }

    @Test
    fun testDefaultSettings() {
        Assert.assertEquals(5, PhysicsBoundaryBox.FRAME_SIZE)
        Assert.assertEquals(0x0004, PhysicsWorld.MASK_BOUNDARYBOX.toInt())
        Assert.assertEquals(0x0002, PhysicsWorld.CATEGORY_BOUNDARYBOX.toInt())
    }

    @Test
    fun testProperties() {
        Assert.assertEquals(0, world!!.bodyCount)
        PhysicsBoundaryBox(world).create(40, 40)
        Assert.assertEquals(4, world!!.bodyCount)
        val bodies = Array<Body>()
        world!!.getBodies(bodies)
        Assert.assertEquals(4, bodies.size)
        for (body in bodies) {
            Assert.assertEquals(BodyType.StaticBody, body.type)
            val fixtures = body.fixtureList
            Assert.assertEquals(1, fixtures.size)
            val fixture = fixtures[0]
            val filter = fixture.filterData
            Assert.assertEquals(PhysicsWorld.MASK_BOUNDARYBOX, filter.maskBits)
            Assert.assertEquals(PhysicsWorld.CATEGORY_BOUNDARYBOX, filter.categoryBits)
        }
    }

    companion object {
        init {
            GdxNativesLoader.load()
        }
    }
}
