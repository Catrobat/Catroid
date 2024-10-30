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
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.WriteVariableToFileAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito.doNothing
import org.powermock.api.mockito.PowerMockito.doReturn
import org.powermock.api.mockito.PowerMockito.spy
import java.io.File

@RunWith(Parameterized::class)
class WriteVariableToFileActionTest(
    private val name: String,
    private val formula: Formula?,
    private val userVariable: UserVariable?,
    private val expectedFileContent: String,
    private val createFile: Int,
    private val writeToFile: Int
) {
    private lateinit var sprite: Sprite
    private lateinit var sequence: SequenceAction
    private lateinit var file: File

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("USER_VARIABLE_NULL", Formula("file.txt"), null, "", 0, 0),
            arrayOf("FORMULA_NULL", null, UserVariable(VAR_NAME, DEFAULT_VAR_VALUE), "", 0, 0),
            arrayOf("VALID_FILE_NAME", Formula(DEFAULT_FILE_NAME),
                    UserVariable(VAR_NAME, DEFAULT_VAR_VALUE), DEFAULT_VAR_VALUE, 1, 1),
            arrayOf("CANNOT_CREATE_FILE", Formula(DEFAULT_FILE_NAME),
                    UserVariable(VAR_NAME, DEFAULT_VAR_VALUE), DEFAULT_VAR_VALUE, 1, 0),
            arrayOf("NO_SUFFIX", Formula("file"),
                    UserVariable(VAR_NAME, DEFAULT_VAR_VALUE), DEFAULT_VAR_VALUE, 1, 1),
            arrayOf("INVALID_FILE_NAME", Formula("\"f\\i^^ *\\\"l\\|\"e.t xt\\\""),
                    UserVariable(VAR_NAME, DEFAULT_VAR_VALUE), DEFAULT_VAR_VALUE, 1, 1),
            arrayOf("UNICODE", Formula(DEFAULT_FILE_NAME),
                    UserVariable(VAR_NAME, "🐼~🐵~🐘"), "🐼~🐵~🐘", 1, 1),
            arrayOf("NUMBER", Formula(DEFAULT_FILE_NAME),
                    UserVariable(VAR_NAME, -3.14), "-3.14", 1, 1)
        )

        private const val DEFAULT_FILE_NAME = "file.txt"
        private const val VAR_NAME = "testUserVariable"
        private const val DEFAULT_VAR_VALUE = "testValue"
    }

    @Before
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
        sequence = SequenceAction()
        file = Mockito.mock(File::class.java)
    }

    @Test
    fun testWriteVariableToFile() {
        val action = spy(sprite.actionFactory.createWriteVariableToFileAction(
            sprite,
            sequence,
            formula,
            userVariable
        ) as WriteVariableToFileAction)

        if (writeToFile > 0) {
            doReturn(file).`when`(action).createFile(anyString())
        } else {
            doReturn(null).`when`(action).createFile(anyString())
        }

        doNothing().`when`(action).writeToFile(file, expectedFileContent)
        Assert.assertTrue(action.act(1f))

        verify(action, times(createFile)).createFile(DEFAULT_FILE_NAME)
        verify(action, times(writeToFile)).writeToFile(file, expectedFileContent)
    }
}
