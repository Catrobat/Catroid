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
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class)
class ShowTextActionTest {
    private lateinit var sprite: Sprite
    private lateinit var var0: UserVariable
    private lateinit var var1: UserVariable
    private lateinit var secondSprite: Sprite
    var contextMock: Context = MockUtil.mockContextForProject()
    var androidStringProviderMock = AndroidStringProvider(contextMock)

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
        var0.visible = false
        sprite.addUserVariable(var0)
        var1 = UserVariable(USER_VARIABLE_NAME)
        var1.visible = false
        secondSprite.addUserVariable(var1)
        PowerMockito.mockStatic(GdxNativesLoader::class.java)
    }

    @Test
    fun testShowVariablesVisibilitySameVariableNameAcrossSprites() {
        var factory = sprite.actionFactory
        val firstSpriteAction = factory.createShowVariableAction(
            sprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var0,
            androidStringProviderMock
        )
        factory = secondSprite.actionFactory
        val secondSpriteAction = factory.createShowVariableAction(
            secondSprite,
            SequenceAction(),
            Formula(0),
            Formula(0),
            var1,
            androidStringProviderMock
        )
        firstSpriteAction.act(1.0f)
        ProjectManager.getInstance().currentSprite = secondSprite
        secondSpriteAction.act(1.0f)
        val variableOfFirstSprite = sprite.getUserVariable(USER_VARIABLE_NAME)
        val variableOfSecondSprite = sprite.getUserVariable(USER_VARIABLE_NAME)
        Assert.assertTrue(variableOfFirstSprite.visible)
        Assert.assertTrue(variableOfSecondSprite.visible)
    }

    companion object {
        private const val SPRITE_NAME = "Cat"
        private const val SECOND_SPRITE_NAME = "Dog"
        private const val USER_VARIABLE_NAME = "var"
    }
}
