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
package org.catrobat.catroid.test.physics.actions.conditional

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.junit.rules.ExpectedException
import org.catrobat.catroid.test.physics.PhysicsCollisionTestRule
import org.junit.Before
import com.badlogic.gdx.math.Vector2
import junit.framework.Assert
import org.catrobat.catroid.physics.PhysicsObject
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.junit.Rule
import org.junit.Test
import java.lang.NullPointerException

@RunWith(AndroidJUnit4::class)
class HideActionAndCollisionTest {
    @get:Rule
    val exception = ExpectedException.none()

    @get:Rule
    var rule = PhysicsCollisionTestRule()
    private var sprite: Sprite? = null
    @Before
    fun setUp() {
        sprite = rule.sprite
        rule.spritePosition = Vector2(0.0f, 100.0f)
        rule.sprite2Position = Vector2(0.0f, -200.0f)
        rule.physicsObject1Type = PhysicsObject.Type.DYNAMIC
        rule.physicsObject2Type = PhysicsObject.Type.FIXED
        rule.initializeSpritesForCollision()
    }

    @Test
    fun testNoCollisionAfterHide() {
        val action = sprite!!.actionFactory.createHideAction(sprite)
        action.act(1.0f)
        rule.simulateFullCollision()
        Assert.assertFalse(rule.collisionDetected())
    }

    @Test
    fun testCollisionAfterHide() {
        var action = sprite!!.actionFactory.createHideAction(sprite)
        action.act(1.0f)
        action = sprite!!.actionFactory.createShowAction(sprite)
        action.act(1.0f)
        rule.simulateFullCollision()
        Assert.assertTrue(rule.collisionDetected())
    }

    @Test
    fun testHide() {
        val action = sprite!!.actionFactory.createHideAction(sprite)
        action.act(1.0f)
        Assert.assertFalse(sprite!!.look.isLookVisible)
    }

    @Test
    fun testNullSprite() {
        val factory = ActionFactory()
        val action = factory.createHideAction(null)
        exception.expect(NullPointerException::class.java)
        action.act(1.0f)
    }
}
