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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert.assertEquals
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ChangeColorByNActionTest {
    @get:Rule
    public val exception:ExpectedException = ExpectedException.none()
    private var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
        sprite!!.actionFactory.createSetColorAction(
            sprite,
            SequenceAction(), Formula(INITIALIZED_VALUE)
        ).act(1.0f)
    }

    @Test
    fun testNormalBehavior() {
        assertEquals(INITIALIZED_VALUE, sprite!!.look.colorInUserInterfaceDimensionUnit)
        sprite!!.actionFactory.createChangeColorByNAction(
            sprite, SequenceAction(),
            Formula(DELTA)
        ).act(1.0f)
        assertEquals(
            INITIALIZED_VALUE + DELTA,
            sprite!!.look.colorInUserInterfaceDimensionUnit
        )
        sprite!!.actionFactory.createChangeColorByNAction(
            sprite, SequenceAction(),
            Formula(-DELTA)
        ).act(1.0f)
        assertEquals(INITIALIZED_VALUE, sprite!!.look.colorInUserInterfaceDimensionUnit)
    }

    @Test(expected = NullPointerException::class)
    fun testNullSprite() {
        val action = sprite!!.actionFactory.createChangeColorByNAction(
            null,
            SequenceAction(), Formula(
                DELTA
            )
        )
        action.act(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        sprite!!.actionFactory.createChangeColorByNAction(
            sprite,
            SequenceAction(), Formula(DELTA.toString())
        ).act(1.0f)
        assertEquals(
            INITIALIZED_VALUE + DELTA,
            sprite!!.look.colorInUserInterfaceDimensionUnit
        )
        sprite!!.actionFactory.createChangeColorByNAction(
            sprite,
            SequenceAction(), Formula(
                NOT_NUMERICAL_STRING
            )
        ).act(1.0f)
        assertEquals(
            INITIALIZED_VALUE + DELTA,
            sprite!!.look.colorInUserInterfaceDimensionUnit
        )
    }

    @Test
    fun testNullFormula() {
        sprite!!.actionFactory.createChangeColorByNAction(sprite, SequenceAction(), null).act(1.0f)
        assertEquals(25.0f, sprite!!.look.colorInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNotANumberFormula() {
        sprite!!.actionFactory.createChangeColorByNAction(
            sprite,
            SequenceAction(), Formula(Double.NaN)
        ).act(1.0f)
        assertEquals(INITIALIZED_VALUE, sprite!!.look.colorInUserInterfaceDimensionUnit)
    }

    @Test
    fun testWrapAround() {
        sprite!!.actionFactory.createSetColorAction(
            sprite, SequenceAction(),
            Formula(199.0f)
        ).act(1.0f)
        assertEquals(199.0f, sprite!!.look.colorInUserInterfaceDimensionUnit)
        sprite!!.actionFactory.createChangeColorByNAction(
            sprite, SequenceAction(),
            Formula(DELTA)
        ).act(1.0f)
        assertEquals(0.0f, sprite!!.look.colorInUserInterfaceDimensionUnit)
        sprite!!.actionFactory.createChangeColorByNAction(
            sprite, SequenceAction(),
            Formula(-DELTA)
        ).act(1.0f)
        assertEquals(199.0f, sprite!!.look.colorInUserInterfaceDimensionUnit)
    }

    companion object {
        private const val INITIALIZED_VALUE = 0f
        private const val NOT_NUMERICAL_STRING = "color"
        private const val DELTA = 1f
    }
}