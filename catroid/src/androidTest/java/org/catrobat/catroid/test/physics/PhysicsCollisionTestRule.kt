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

import android.util.Log
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.physics.PhysicsWorld
import org.catrobat.catroid.physics.content.ActionPhysicsFactory
import org.catrobat.catroid.test.utils.Reflection

open class PhysicsCollisionTestRule : PhysicsTestRule(), PhysicsCollisionTestReceiver {
    var sprite2: Sprite? = null
    var physicsCollisionTestListener: PhysicsCollisionTestListener? = null
    @JvmField
	var physicsObject1: PhysicsObject? = null
    @JvmField
	var physicsObject2: PhysicsObject? = null
    @JvmField
	var spritePosition: Vector2? = null
    @JvmField
	var sprite2Position: Vector2? = null
    @JvmField
	var physicsObject1Type = PhysicsObject.Type.NONE
    @JvmField
	var physicsObject2Type = PhysicsObject.Type.NONE
    var beginContactCounter = 0
    var endContactCounter = 0
    @Throws(Throwable::class)
    override fun before() {
        super.before()
        sprite2 = Sprite("TestSprite2")
        project.defaultScene.addSprite(sprite2)
        sprite2!!.look = PhysicsLook(sprite2, physicsWorld)
        sprite2!!.actionFactory = ActionPhysicsFactory()
        val lookdata = PhysicsTestUtils.generateLookData(rectangle125x125File)
        sprite2!!.look.lookData = lookdata
        Assert.assertNotNull(sprite2!!.look.lookData)
        physicsObject1 = physicsWorld.getPhysicsObject(sprite)
        physicsObject2 = physicsWorld.getPhysicsObject(sprite2)
        val world = Reflection.getPrivateField(
            PhysicsWorld::class.java, physicsWorld, "world"
        ) as World
        physicsCollisionTestListener = PhysicsCollisionTestListener(this, physicsWorld)
        world.setContactListener(physicsCollisionTestListener)
    }

    override fun after() {
        sprite2 = null
        physicsCollisionTestListener = null
        super.after()
    }

    fun initializeSpritesForCollision() {
        if (spritePosition == null || sprite2Position == null) {
            throw RuntimeException("You must initialize the sprite position for your test physicsObject1Type in your constructor.")
        }
        if (physicsObject1Type == PhysicsObject.Type.NONE || physicsObject2Type == PhysicsObject.Type.NONE) {
            throw RuntimeException("You must specify a type that can collide for both physics objects in your constructor")
        }
        sprite.look.setPositionInUserInterfaceDimensionUnit(spritePosition!!.x, spritePosition!!.y)
        sprite2!!.look.setPositionInUserInterfaceDimensionUnit(
            sprite2Position!!.x,
            sprite2Position!!.y
        )
        physicsObject1!!.type = physicsObject1Type
        physicsObject2!!.type = physicsObject2Type
        physicsObject1!!.setVelocity(0.0f, 0.0f)
        physicsObject2!!.setVelocity(0.0f, 0.0f)
        physicsObject1!!.rotationSpeed = 0.0f
        physicsObject2!!.rotationSpeed = 0.0f
    }

    fun collisionDetected(): Boolean {
        return beginContactCounter > 0
    }

    val isContactRateOk: Boolean
        get() {
            Log.d(TAG, "getContactDifference(): $contactDifference == 0")
            return contactDifference == 0
        }
    val contactDifference: Int
        get() = beginContactCounter - endContactCounter

    fun simulateFullCollision(): Boolean {
        var stepCount = 0
        while (stepCount < MAX_STEPS) {
            physicsWorld.step(DELTA_TIME)
            stepCount++
        }
        return if (beginContactCounter - endContactCounter == 0) {
            true
        } else {
            Log.e(
                TAG,
                "Attention, no full collision occurred."
            )
            false
        }
    }

    override fun beginContactCallback(contact: Contact?) {
        beginContactCounter++
        Log.d(TAG, "beginContactCallback $beginContactCounter")
    }

    override fun endContactCallback(contact: Contact?) {
        endContactCounter++
        Log.d(TAG, "endContactCallback $endContactCounter")
    }

    override fun preSolveCallback(contact: Contact?, oldManifold: Manifold?) {}
    override fun postSolveCallback(contact: Contact?, impulse: ContactImpulse?) {}

    companion object {
        private val TAG = PhysicsCollisionTestRule::class.java.simpleName
        const val DELTA_TIME = 0.1f
        const val MAX_STEPS = 25
    }
}
