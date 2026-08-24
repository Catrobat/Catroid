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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ShowTextColorSizeAlignmentAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as mockitoWhen

@RunWith(JUnit4::class)
class ShowTextColorSizeAlignmentActionTest {

    private lateinit var sprite: Sprite
    private lateinit var scope: Scope
    private lateinit var userVariable: UserVariable
    private lateinit var stageListenerMock: StageListener
    private lateinit var stageMock: com.badlogic.gdx.scenes.scene2d.Stage
    private val actorsArray = com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.Actor>()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
        scope = Scope(null, sprite, SequenceAction())
        userVariable = UserVariable("testVar")
        userVariable.visible = false

        stageListenerMock = mock(StageListener::class.java)
        stageMock = mock(com.badlogic.gdx.scenes.scene2d.Stage::class.java)
        mockitoWhen(stageListenerMock.stage).thenReturn(stageMock)
        mockitoWhen(stageMock.actors).thenReturn(actorsArray)

        StageActivity.stageListener = stageListenerMock
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testShowVariableColorSizeAlignmentVisibilityOnBegin() {
        val action = ShowTextColorSizeAlignmentAction().apply {
            setPosition(Formula(10), Formula(20))
            setRelativeTextSize(Formula(150))
            setColor(Formula("RED"))
            setVariableToShow(userVariable)
            setScope(scope)
            setAlignment(1)
        }

        assertFalse(userVariable.visible)
        action.act(1.0f)
        assertTrue(userVariable.visible)
        verify(stageListenerMock).addActor(any(com.badlogic.gdx.scenes.scene2d.Actor::class.java))
    }

    @Test
    fun testShowVariableColorSizeAlignmentInvisibleWhenSizeIsZeroOrLess() {
        val action = ShowTextColorSizeAlignmentAction().apply {
            setPosition(Formula(10), Formula(20))
            setRelativeTextSize(Formula(0))
            setColor(Formula("RED"))
            setVariableToShow(userVariable)
            setScope(scope)
            setAlignment(1)
        }

        userVariable.visible = true
        action.act(1.0f)
        assertFalse(userVariable.visible)
        verify(stageListenerMock, never()).addActor(any(com.badlogic.gdx.scenes.scene2d.Actor::class.java))
    }

    @Test
    fun testShowVariableNullSafety() {
        val action = ShowTextColorSizeAlignmentAction()
        // act with all null values should not throw exception
        action.act(1.0f)
    }
}
