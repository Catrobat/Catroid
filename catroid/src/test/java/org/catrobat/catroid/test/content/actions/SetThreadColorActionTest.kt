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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert.assertEquals
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SetThreadColorAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetThreadColorActionTest {

    private var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
    }

    @Test
    fun testNormalBehaviorColorChangeWhite() {
        createTestableSetThreadColorAction(
            sprite, SequenceAction(),
            Formula(COLOR_TEST_VALUE_WHITE)
        )?.act(1.0f)
        assertEquals(Color.WHITE, sprite?.embroideryThreadColor)
    }

    @Test
    fun testNormalBehaviorColorChangeBlue() {
        createTestableSetThreadColorAction(
            sprite, SequenceAction(),
            Formula(COLOR_TEST_VALUE_BLUE)
        )?.act(1.0f)
        assertEquals(Color.BLUE, sprite?.embroideryThreadColor)
    }

    @Test
    fun testNullFormula() {
        createTestableSetThreadColorAction(sprite, SequenceAction(), null)?.act(1.0f)
        assertEquals(COLOR_DEFAULT_VALUE, sprite?.embroideryThreadColor)
    }

    @Test
    fun testTooShortFormula() {
        createTestableSetThreadColorAction(
            sprite,
            SequenceAction(), Formula("#000")
        )?.act(1.0f)
        assertEquals(COLOR_INITIALIZED_VALUE, sprite?.embroideryThreadColor)
    }

    @Test
    fun testInvalidColorCode() {
        createTestableSetThreadColorAction(
            sprite,
            SequenceAction(), Formula(TEST_INVALID_VALUE)
        )?.act(1.0f)
        assertEquals(COLOR_INITIALIZED_VALUE, sprite?.embroideryThreadColor)
    }

    private fun createTestableSetThreadColorAction(sprite: Sprite?, sequence: SequenceAction?, color: Formula?): Action? {
        val action = Actions.action(
            TestableSetThreadColorAction::class.java
        )
        val scope = sprite?.let {
            Scope(ProjectManager.getInstance().currentProject, it, sequence)
        }
        action.setSprite(sprite)
        action.setScope(scope)
        action.setColor(color)
        return action
    }

    class TestableSetThreadColorAction : SetThreadColorAction() {
        override fun argbToInt(redInt: Int, greenInt: Int, blueInt: Int) =
            COLOR_ALPHA and 0xff shl 24 or (redInt and 0xff shl 16) or (greenInt and 0xff shl 8) or (blueInt and 0xff)
    }

    companion object {
        private val COLOR_INITIALIZED_VALUE = Color(0x000000ff)
        private val COLOR_DEFAULT_VALUE = Color.RED
        private const val COLOR_TEST_VALUE_WHITE = "#ffffff"
        private const val COLOR_TEST_VALUE_BLUE = "#0000ff"
        private const val TEST_INVALID_VALUE = "#XXXXXX"
        private const val COLOR_ALPHA = 0xFF
    }
}
