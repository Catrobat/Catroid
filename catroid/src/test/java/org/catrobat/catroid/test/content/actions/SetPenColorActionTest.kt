/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert.assertTrue
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.PenColor
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SetPenColorAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetPenColorActionTest {
    private var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
    }

    @Test
    fun testNormalBehaviorColorChangeInitBlue() {
        sprite?.penConfiguration?.penColor?.let {
            comparePenColors(COLOR_BLUE_INITIAL, it)
        }?.let { assertTrue(it) }
    }

    @Test
    fun testNormalBehaviorColorChangeRed() {
        createTestableSetPenColorAction(
            sprite, SequenceAction(),
            Formula(255), Formula(0), Formula(0)
        )?.act(1.0f)

        sprite?.penConfiguration?.penColor?.let {
            comparePenColors(COLOR_RED, it)
        }?.let { assertTrue(it) }
    }

    @Test
    fun testNormalBehaviorColorChangeBlack() {
        createTestableSetPenColorAction(
            sprite, SequenceAction(),
            Formula(0), Formula(0), Formula(0)
        )?.act(1.0f)

        sprite?.penConfiguration?.penColor?.let {
            comparePenColors(COLOR_BLACK, it)
        }?.let { assertTrue(it) }
    }

    @Test(expected = NullPointerException::class)
    fun testNullSprite() {
        sprite?.actionFactory?.createSetPenColorAction(
            null, SequenceAction(),
            Formula(255), Formula(0), Formula(0)
        )?.act(1.0f)
    }

    @Test
    fun testNullFormulaRed() {
        createTestableSetPenColorAction(
            sprite, SequenceAction(),
            null, Formula(0), Formula(0)
        )?.act(1.0f)
        sprite?.penConfiguration?.penColor?.let {
            comparePenColors(COLOR_BLACK, it)
        }?.let { assertTrue(it) }
    }

    @Test
    fun testNullFormulaGreen() {
        createTestableSetPenColorAction(
            sprite, SequenceAction(),
            Formula(0), null, Formula(0)
        )?.act(1.0f)
        sprite?.penConfiguration?.penColor?.let {
            comparePenColors(COLOR_BLACK, it)
        }?.let { assertTrue(it) }
    }

    @Test
    fun testNullFormulaBlue() {
        createTestableSetPenColorAction(
            sprite, SequenceAction(),
            Formula(0), Formula(0), null
        )?.act(1.0f)
        sprite?.penConfiguration?.penColor?.let {
            comparePenColors(COLOR_BLACK, it)
        }?.let { assertTrue(it) }
    }

    @Test
    fun testNullFormulaAll() {
        createTestableSetPenColorAction(
            sprite, SequenceAction(),
            null, null, null
        )?.act(1.0f)
        sprite?.penConfiguration?.penColor?.let {
            comparePenColors(COLOR_BLACK, it)
        }?.let { assertTrue(it) }
    }

    private fun compareColor(color1: Float, color2: Float): Boolean = color1 == color2

    private fun comparePenColors(color1: PenColor, color2: PenColor) = compareColor(color1.a,
                                                                                    color2.a) && compareColor(color1.r, color2.r) && compareColor(color1.g, color2.g) && compareColor(color1.b, color2.b)

    private fun createTestableSetPenColorAction(sprite: Sprite?, sequence: SequenceAction, red: Formula?, green: Formula?, blue: Formula?): Action? {
        val action = Actions.action(
            TestableSetThreadColorAction::class.java
        )
        val scope = sprite?.let { Scope(ProjectManager.getInstance().currentProject, it, sequence) }
        action.setScope(scope)
        action.setRed(red)
        action.setGreen(green)
        action.setBlue(blue)
        return action
    }

    class TestableSetThreadColorAction : SetPenColorAction() {
        override fun argbToInt(redInt: Int, greenInt: Int, blueInt: Int) =
            COLOR_ALPHA and 0xff shl 24 or (redInt and 0xff shl 16) or (greenInt and 0xff shl 8) or (blueInt and 0xff)
    }

    companion object {
        private val COLOR_RED = PenColor(1f, 0f, 0f, 1f)
        private val COLOR_BLACK = PenColor(0f, 0f, 0f, 1f)
        private val COLOR_BLUE_INITIAL = PenColor(0f, 0f, 1f, 1f)
        private const val COLOR_ALPHA = 0xFF
    }
}
