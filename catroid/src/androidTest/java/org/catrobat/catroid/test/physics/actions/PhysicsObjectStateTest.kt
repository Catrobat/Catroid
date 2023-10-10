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
import com.badlogic.gdx.scenes.scene2d.Action
import org.junit.runner.RunWith
import org.catrobat.catroid.test.physics.PhysicsTestRule
import org.catrobat.catroid.physics.PhysicsWorld
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.physics.PhysicsLook
import org.catrobat.catroid.content.actions.SetXAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SetYAction
import org.catrobat.catroid.content.actions.SetTransparencyAction
import org.catrobat.catroid.content.actions.SetVisibleAction
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class PhysicsObjectStateTest {
    private lateinit var physicsObjectStateHandler:Any

    @get:Rule
    var rule = PhysicsTestRule()
    private var sprite: Sprite? = null
    private var physicsWorld: PhysicsWorld? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = rule.sprite
        physicsWorld = rule.physicsWorld
        physicsObjectStateHandler =
            Reflection.getPrivateField(sprite!!.look, "physicsObjectStateHandler")
    }

    @Test
    @Throws(Exception::class)
    fun testVisibility() {
        allConditionsInactiveCheck()
        transparency(100)
        hangupNonCollidingActiveCheck()
        transparency(0)
        allConditionsInactiveCheck()
        hide()
        hangupNonCollidingActiveCheck()
        show()
        allConditionsInactiveCheck()
        transparency(100)
        hangupNonCollidingActiveCheck()
        show()
        hangupNonCollidingActiveCheck()
        hide()
        hangupNonCollidingActiveCheck()
        transparency(0)
        hangupNonCollidingActiveCheck()
        show()
        allConditionsInactiveCheck()
        hide()
        hangupNonCollidingActiveCheck()
        transparency(100)
        hangupNonCollidingActiveCheck()
        show()
        hangupNonCollidingActiveCheck()
        transparency(0)
        allConditionsInactiveCheck()
    }

    @Test
    @Throws(Exception::class)
    fun testPos() {
        allConditionsInactiveCheck()
        setX(10 + PhysicsWorld.activeArea.x)
        hangupNonCollidingActiveCheck()
        setX(0.0f)
        allConditionsInactiveCheck()
        setX(-10 - PhysicsWorld.activeArea.x)
        hangupNonCollidingActiveCheck()
        setX(0.0f)
        allConditionsInactiveCheck()
        setY(-10 - PhysicsWorld.activeArea.y)
        hangupNonCollidingActiveCheck()
        setY(0f)
        allConditionsInactiveCheck()
        setY(10 + PhysicsWorld.activeArea.y)
        hangupNonCollidingActiveCheck()
        setY(0f)
        allConditionsInactiveCheck()
    }

    @Test
    @Throws(Exception::class)
    fun testGlideTo() {
        allConditionsInactiveCheck()
        val action = glideTo(Formula(100), Formula(100))
        hangupFixedActiveCheck()
        action.act(1.0f)
        allConditionsInactiveCheck()
    }

    @Test
    @Throws(Exception::class)
    fun testPositionAndGlideTo() {
        allConditionsInactiveCheck()
        val action = glideTo(Formula(10 + PhysicsWorld.activeArea.x), Formula(100))
        hangupFixedActiveCheck()
        action.act(1.0f)
        hangupNonCollidingActiveCheck()
        setX(0.0f)
        allConditionsInactiveCheck()
    }

    @Test
    @Throws(Exception::class)
    fun testVisibleAndPositionAndGlideTo() {
        allConditionsInactiveCheck()
        var action = glideTo(Formula(10 + PhysicsWorld.activeArea.x), Formula(100))
        hangupFixedActiveCheck()
        hide()
        hangupFixedNonCollidingActiveCheck()
        show()
        hangupFixedActiveCheck()
        hide()
        action.act(1.0f)
        hangupNonCollidingActiveCheck()
        setX(0.0f)
        hangupNonCollidingActiveCheck()
        show()
        allConditionsInactiveCheck()
        action = glideTo(Formula(100), Formula(10 + PhysicsWorld.activeArea.y))
        hangupFixedActiveCheck()
        show()
        hangupFixedActiveCheck()
        hide()
        hangupFixedNonCollidingActiveCheck()
        show()
        hangupFixedActiveCheck()
        action.act(1.0f)
        hangupNonCollidingActiveCheck()
        setY(0.0f)
        allConditionsInactiveCheck()
    }

    @Throws(Exception::class)
    private fun allConditionsInactiveCheck() {
        (sprite!!.look as PhysicsLook).updatePhysicsObjectState(true)
        val hangedUp = Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp") as Boolean
        Assert.assertFalse(hangedUp)
        val nonColliding =
            Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding") as Boolean
        Assert.assertFalse(nonColliding)
        val fixed = Reflection.getPrivateField(physicsObjectStateHandler, "fixed") as Boolean
        Assert.assertFalse(fixed)
    }

    @Throws(Exception::class)
    private fun hangupNonCollidingActiveCheck() {
        (sprite!!.look as PhysicsLook).updatePhysicsObjectState(true)
        val hangedUp = Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp") as Boolean
        Assert.assertTrue(hangedUp)
        val nonColliding =
            Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding") as Boolean
        Assert.assertTrue(nonColliding)
        val fixed = Reflection.getPrivateField(physicsObjectStateHandler, "fixed") as Boolean
        Assert.assertFalse(fixed)
    }

    @Throws(Exception::class)
    private fun hangupFixedActiveCheck() {
        (sprite!!.look as PhysicsLook).updatePhysicsObjectState(true)
        val hangedUp = Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp") as Boolean
        Assert.assertTrue(hangedUp)
        val nonColliding =
            Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding") as Boolean
        Assert.assertFalse(nonColliding)
        val fixed = Reflection.getPrivateField(physicsObjectStateHandler, "fixed") as Boolean
        Assert.assertTrue(fixed)
    }

    @Throws(Exception::class)
    private fun hangupFixedNonCollidingActiveCheck() {
        (sprite!!.look as PhysicsLook).updatePhysicsObjectState(true)
        val hangedUp = Reflection.getPrivateField(physicsObjectStateHandler, "hangedUp") as Boolean
        Assert.assertTrue(hangedUp)
        val fixed = Reflection.getPrivateField(physicsObjectStateHandler, "fixed") as Boolean
        Assert.assertTrue(fixed)
        val nonColliding =
            Reflection.getPrivateField(physicsObjectStateHandler, "nonColliding") as Boolean
        Assert.assertTrue(nonColliding)
    }

    private fun setX(value: Float) {
        val setXAction = sprite!!.actionFactory.createSetXAction(
            sprite,
            SequenceAction(), Formula(value)
        ) as SetXAction
        setXAction.act(1.0f)
        sprite!!.look.x
    }

    private fun setY(value: Float) {
        val setYAction = sprite!!.actionFactory.createSetYAction(
            sprite,
            SequenceAction(), Formula(value)
        ) as SetYAction
        setYAction.act(1.0f)
        sprite!!.look.y
    }

    private fun transparency(percent: Int) {
        val ghostEffectAction = sprite!!.actionFactory
            .createSetTransparencyAction(
                sprite, SequenceAction(),
                Formula(percent)
            ) as SetTransparencyAction
        ghostEffectAction.act(1.0f)
    }

    private fun show() {
        setVisible(true)
    }

    private fun hide() {
        setVisible(false)
    }

    private fun setVisible(visible: Boolean) {
        val showAction = SetVisibleAction()
        showAction.setSprite(sprite)
        showAction.setVisible(visible)
        showAction.act(1.0f)
    }

    private fun glideTo(x: Formula, y: Formula): Action {
        return sprite!!.actionFactory.createGlideToPhysicsAction(
            sprite, sprite!!.look as PhysicsLook,
            SequenceAction(), x, y, 2.0f, 1.0f
        )
    }
}