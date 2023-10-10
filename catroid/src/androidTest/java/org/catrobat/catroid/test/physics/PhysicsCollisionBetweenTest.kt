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
import com.badlogic.gdx.physics.box2d.Contact
import junit.framework.Assert
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.eventids.BounceOffEventId
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.physics.PhysicalCollision
import org.catrobat.catroid.physics.PhysicsCollisionListener
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhysicsCollisionBetweenTest {
    @get:Rule
    var rule = PhysicsCollisionTestRule()
    private var sprite: Sprite? = null
    private var sprite2: Sprite? = null
    private var project: Project? = null
    @Before
    fun setUp() {
        sprite = rule.sprite
        sprite2 = rule.sprite2
        project = rule.project
        rule.spritePosition = Vector2(0.0f, 100.0f)
        rule.sprite2Position = Vector2(0.0f, -200.0f)
        rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC
        rule.physicsObject2Type = PhysicsObject.Type.FIXED
        rule.initializeSpritesForCollision()
    }

    @Throws(Exception::class)
    fun beginContactCallback(contact: Contact?) {
        rule.beginContactCallback(contact)
        val physicsCollisionBroadcasts = Reflection.getPrivateField(
            PhysicsCollisionListener::class.java,
            rule.physicsCollisionTestListener, "physicsCollisionBroadcasts"
        ) as Map<Int, PhysicalCollision>
        Assert.assertEquals(1, physicsCollisionBroadcasts.size)
        val parameters = arrayOf<Any?>(sprite, sprite2)
        val paramList = Reflection.ParameterList(*parameters)
        val key = Reflection.invokeMethod(
            PhysicsCollisionListener::class.java,
            rule.physicsCollisionTestListener,
            "generateKey", paramList
        ) as BounceOffEventId
        val collisionBroadcast = physicsCollisionBroadcasts.get(key)
        Assert.assertEquals(collisionBroadcast!!.contactCounter, rule.contactDifference)
    }

    @Throws(Exception::class)
    fun endContactCallback(contact: Contact?) {
        rule.endContactCallback(contact)
        val physicsCollisionBroadcasts = Reflection.getPrivateField(
            PhysicsCollisionListener::class.java,
            rule.physicsCollisionTestListener, "physicsCollisionBroadcasts"
        ) as Map<Int, PhysicalCollision>
        if (rule.contactDifference == 0) {
            Assert.assertEquals(0, physicsCollisionBroadcasts.size)
        } else {
            Assert.assertEquals(2, physicsCollisionBroadcasts.size)
        }
    }

    @Test
    fun testIfBroadcastsAreCorrectPreparedAndFired() {
        Assert.assertTrue(rule.isContactRateOk)
        Assert.assertTrue(rule.simulateFullCollision())
        Assert.assertTrue(rule.isContactRateOk)
    }

    @Test
    fun testBounceOffEvent() {
        val testXValue = 444
        val testYValue = 555
        val firstSpriteWhenBounceOffScript = WhenBounceOffScript(sprite2!!.name)
        firstSpriteWhenBounceOffScript.addBrick(PlaceAtBrick(-testXValue, -testYValue))
        sprite!!.addScript(firstSpriteWhenBounceOffScript)
        sprite!!.initializeEventThreads(EventId.START)
        val secondSpriteWhenBounceOffScript = WhenBounceOffScript(sprite!!.name)
        secondSpriteWhenBounceOffScript.addBrick(PlaceAtBrick(testXValue, testYValue))
        sprite2!!.addScript(secondSpriteWhenBounceOffScript)
        sprite2!!.initializeEventThreads(EventId.START)
        rule.simulateFullCollision()
        while (!allActionsOfAllSpritesAreFinished()) {
            for (spriteOfList in project!!.defaultScene.spriteList) {
                spriteOfList.look.act(1.0f)
            }
        }
        Assert.assertEquals(testXValue.toFloat(), sprite2!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(testYValue.toFloat(), sprite2!!.look.yInUserInterfaceDimensionUnit)
        Assert.assertEquals(-testXValue.toFloat(), sprite!!.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(-testYValue.toFloat(), sprite!!.look.yInUserInterfaceDimensionUnit)
    }

    fun allActionsOfAllSpritesAreFinished(): Boolean {
        for (spriteOfList in project!!.defaultScene.spriteList) {
            if (!spriteOfList.look.haveAllThreadsFinished()) {
                return false
            }
        }
        return true
    }
}
