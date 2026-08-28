/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.SetThreadColorAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetThreadColorActionTest {

    private lateinit var sprite: Sprite

    @Before
    fun setUp() {
        initializeStaticSingletonMethods()

        sprite = Sprite("testSprite")
        sprite.embroideryThreadColor = INITIAL_COLOR
    }

    @Test
    fun testColorChangeWhite() {
        createAction(Formula("#ffffff")).act(1f)

        assertEquals(Color.WHITE, sprite.embroideryThreadColor)
    }

    @Test
    fun testColorChangeBlue() {
        createAction(Formula("#0000ff")).act(1f)

        assertEquals(Color.BLUE, sprite.embroideryThreadColor)
    }

    @Test
    fun testNullFormulaUsesDefaultColor() {
        createAction(null).act(1f)

        assertEquals(DEFAULT_COLOR, sprite.embroideryThreadColor)
    }

    @Test
    fun testTooShortFormulaDoesNotChangeColor() {
        createAction(Formula("#000")).act(1f)

        assertEquals(INITIAL_COLOR, sprite.embroideryThreadColor)
    }

    @Test
    fun testInvalidColorCodeDoesNotChangeColor() {
        createAction(Formula("#XXXXXX")).act(1f)

        assertEquals(INITIAL_COLOR, sprite.embroideryThreadColor)
    }

    private fun createAction(formula: Formula?): Action {
        val action = Actions.action(SetThreadColorAction::class.java)
        val scope = Scope(ProjectManager.getInstance().currentProject, sprite, SequenceAction())

        action.setSprite(sprite)
        action.setScope(scope)
        action.setColor(formula)

        return action
    }

    companion object {
        private val INITIAL_COLOR = Color(0x000000ff)
        private val DEFAULT_COLOR = Color.RED
    }
}