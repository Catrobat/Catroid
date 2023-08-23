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

import android.content.Context
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.GdxNativesLoader
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class)
class ShowTextActionTest {
    private var sprite: Sprite? = null
    private var var0: UserVariable? = null
    private var var1: UserVariable? = null
    private var secondSprite: Sprite? = null
    private var contextMock: Context? = MockUtil.mockContextForProject()
    private var androidStringProviderMock = AndroidStringProvider(contextMock)
    @Before
    fun setUp() {
        sprite = Sprite(SPRITE_NAME)
        val project = Project(contextMock, "testProject")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        secondSprite = Sprite(SECOND_SPRITE_NAME)
        project.defaultScene.addSprite(secondSprite)
        var0 = UserVariable(USER_VARIABLE_NAME)
        var0?.visible = false
        sprite?.addUserVariable(var0)
        var1 = UserVariable(USER_VARIABLE_NAME)
        var1?.visible = false
        secondSprite?.addUserVariable(var1)
        PowerMockito.mockStatic(GdxNativesLoader::class.java)
    }

    @Test
    fun testShowVariablesVisibilitySameVariableNameAcrossSprites() {
        var factory = sprite?.actionFactory
        val firstSpriteAction = factory?.createShowVariableAction(
            sprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var0,
            androidStringProviderMock
        )
        factory = secondSprite?.actionFactory
        val secondSpriteAction = factory?.createShowVariableAction(
            secondSprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var1,
            androidStringProviderMock
        )
        firstSpriteAction?.act(1.0f)
        ProjectManager.getInstance().currentSprite = secondSprite
        secondSpriteAction?.act(1.0f)
        val variableOfFirstSprite = sprite?.getUserVariable(USER_VARIABLE_NAME)
        val variableOfSecondSprite = secondSprite?.getUserVariable(USER_VARIABLE_NAME)
        assertTrue(variableOfFirstSprite?.visible == true)
        assertTrue(variableOfSecondSprite?.visible == true)
    }

    @Test(expected = NullPointerException::class)
    fun testShowVariablesNullSprite() {
        val factory = sprite?.actionFactory
        val spriteAction = factory?.createShowVariableAction(
            null,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var1,
            androidStringProviderMock
        )
        spriteAction?.act(1.0f)
    }

    @Test
    fun testShowVariablesHaveSameValue() {
        var0?.value = 123
        var1?.value = 123
        var factory = sprite?.actionFactory
        val firstSpriteAction = factory?.createShowVariableAction(
            sprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var0,
            androidStringProviderMock
        )
        factory = secondSprite?.actionFactory
        val secondSpriteAction = factory?.createShowVariableAction(
            secondSprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var1,
            androidStringProviderMock
        )
        firstSpriteAction?.act(1.0f)
        secondSpriteAction?.act(1.0f)
        val variableOfFirstSprite = sprite?.getUserVariable(USER_VARIABLE_NAME)
        val variableOfSecondSprite = secondSprite?.getUserVariable(USER_VARIABLE_NAME)
        assertTrue(variableOfFirstSprite?.hasSameValue(variableOfSecondSprite) == true)
    }

    @Test
    fun testShowVariablesHaveSetValue() {
        var0?.value = 123
        var1?.value = 123
        var factory = sprite?.actionFactory
        val firstSpriteAction = factory?.createShowVariableAction(
            sprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var0,
            androidStringProviderMock
        )
        factory = secondSprite?.actionFactory
        val secondSpriteAction = factory?.createShowVariableAction(
            secondSprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var1,
            androidStringProviderMock
        )
        firstSpriteAction?.act(1.0f)
        secondSpriteAction?.act(1.0f)
        val variableOfFirstSprite = sprite?.getUserVariable(USER_VARIABLE_NAME)
        val variableOfSecondSprite = secondSprite?.getUserVariable(USER_VARIABLE_NAME)
        variableOfSecondSprite?.value = 1337
        assertEquals(123, variableOfFirstSprite?.value)
        assertEquals(1337, variableOfSecondSprite?.value)
    }

    companion object {
        private const val SPRITE_NAME = "Cat"
        private const val SECOND_SPRITE_NAME = "Dog"
        private const val USER_VARIABLE_NAME = "var"
    }
}
