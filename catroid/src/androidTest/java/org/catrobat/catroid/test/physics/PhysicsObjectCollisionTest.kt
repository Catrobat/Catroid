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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import junit.framework.Assert
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.test.utils.Reflection
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhysicsObjectCollisionTest {
    private var contactFixturePairs: MutableList<HashSet<Fixture>?>? = ArrayList()
    private var expectedcontactFixtures: HashSet<Fixture>? = HashSet()

    @get:Rule
    var rule: PhysicsCollisionTestRule = object : PhysicsCollisionTestRule() {
        override fun beginContactCallback(contact: Contact?) {
            super.beginContactCallback(contact)
            val contactFixtureSet = HashSet<Fixture>()
            contactFixtureSet.add(contact!!.fixtureA)
            contactFixtureSet.add(contact.fixtureB)
            contactFixturePairs!!.add(contactFixtureSet)
        }
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        rule.spritePosition = Vector2(-125f, 0f)
        rule.sprite2Position = Vector2(125f, 0f)
        rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC
        rule.physicsObject2Type = PhysicsObject.Type.DYNAMIC
        rule.physicsObject1!!.gravityScale = 0f
        rule.physicsObject2!!.gravityScale = 0f
        rule.physicsObject1!!.setVelocity(64f, 0f)
        rule.physicsObject2!!.setVelocity(-64f, 0f)
        val body1 = Reflection.getPrivateField(rule.physicsObject1, "body") as Body
        val expectedContactFixture1 = body1.fixtureList[0]
        expectedcontactFixtures!!.add(expectedContactFixture1)
        val body2 = Reflection.getPrivateField(rule.physicsObject2, "body") as Body
        val expectedContactFixture2 = body2.fixtureList[0]
        expectedcontactFixtures!!.add(expectedContactFixture2)
        rule.initializeSpritesForCollision()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        contactFixturePairs = null
        expectedcontactFixtures = null
    }

    @Test
    fun testCollisionDynamicNone() {
        rule.physicsObject2!!.type = PhysicsObject.Type.NONE
        Assert.assertTrue(rule.simulateFullCollision())
        Assert.assertFalse(contactFixturePairs!!.contains(expectedcontactFixtures))
    }

    @Test
    fun testCollisionFixedFixed() {
        rule.physicsObject1!!.type = PhysicsObject.Type.FIXED
        rule.physicsObject2!!.type = PhysicsObject.Type.FIXED
        Assert.assertTrue(rule.simulateFullCollision())
        Assert.assertFalse(contactFixturePairs!!.contains(expectedcontactFixtures))
    }

    @Test
    fun testCollisionFixedNone() {
        rule.physicsObject1!!.type = PhysicsObject.Type.FIXED
        rule.physicsObject2!!.type = PhysicsObject.Type.NONE
        Assert.assertTrue(rule.simulateFullCollision())
        Assert.assertFalse(contactFixturePairs!!.contains(expectedcontactFixtures))
    }

    @Test
    fun testCollisionNoneNone() {
        rule.physicsObject1!!.type = PhysicsObject.Type.NONE
        rule.physicsObject2!!.type = PhysicsObject.Type.NONE
        Assert.assertTrue(rule.simulateFullCollision())
        Assert.assertFalse(contactFixturePairs!!.contains(expectedcontactFixtures))
    }
}
