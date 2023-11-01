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
import org.catrobat.catroid.content.actions.ReadVariableFromFileAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.powermock.api.mockito.PowerMockito.doReturn
import org.powermock.api.mockito.PowerMockito.spy
import java.io.File

@RunWith(Parameterized::class)
class ReadVariableFromFileActionTest(
    private val name: String,
    private val formula: Formula?,
    private val userVariable: UserVariable?,
    private val fileContent: String,
    private val expectedValue: Any?,
    private val createFile: Int,
    private val readFromFile: Int,
    private val deleteFile: Boolean
) {
    private lateinit var sprite: Sprite
    private lateinit var file: File

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("USER_VARIABLE_NULL", Formula("file.txt"), null, DEFAULT_FILE_CONTENT,
                    null, 0, 0, false),
            arrayOf("FORMULA_NULL", null, UserVariable(VAR_NAME), DEFAULT_FILE_CONTENT, 0.0, 0,
                    0, false),
            arrayOf("VALID_FILE_NAME", Formula(DEFAULT_FILE_NAME), UserVariable(VAR_NAME),
                    DEFAULT_FILE_CONTENT, DEFAULT_FILE_CONTENT, 1, 1, false),
            arrayOf("DELETE_FILE", Formula(DEFAULT_FILE_NAME), UserVariable(VAR_NAME),
                    DEFAULT_FILE_CONTENT, DEFAULT_FILE_CONTENT, 1, 1, true),
            arrayOf("CANNOT_READ_FILE", Formula(DEFAULT_FILE_NAME), UserVariable(VAR_NAME),
                    DEFAULT_FILE_CONTENT, 0.0, 1, 0, false),
            arrayOf("NO_SUFFIX", Formula("file"), UserVariable(VAR_NAME),
                    DEFAULT_FILE_CONTENT, DEFAULT_FILE_CONTENT, 1, 1, false),
            arrayOf("INVALID_FILE_NAME", Formula("\"f\\i^^ *\\\"l\\|\"e.t xt\\\""),
                    UserVariable(VAR_NAME), DEFAULT_FILE_CONTENT, DEFAULT_FILE_CONTENT, 1, 1, false),
            arrayOf("UNICODE", Formula(DEFAULT_FILE_NAME), UserVariable(VAR_NAME),
                    "🐼~🐵~🐘", "🐼~🐵~🐘", 1, 1, false),
            arrayOf("NUMBER", Formula(DEFAULT_FILE_NAME), UserVariable(VAR_NAME),
                    "-3.14", -3.14, 1, 1, false)
        )

        private const val DEFAULT_FILE_NAME = "file.txt"
        private const val VAR_NAME = "testUserVariable"
        private const val DEFAULT_FILE_CONTENT = "testContent"
    }

    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        file = Mockito.mock(File::class.java)
        doReturn(true).`when`(file).delete()
    }

    @Test
    fun testReadVariableFromFile() {
        val action = spy(sprite.actionFactory.createReadVariableFromFileAction(
            sprite,
            SequenceAction(),
            formula,
            userVariable,
            deleteFile
        ) as ReadVariableFromFileAction)

        if (readFromFile > 0) {
            doReturn(file).`when`(action).getFile(anyString())
        } else {
            doReturn(null).`when`(action).getFile(anyString())
        }

        doReturn(fileContent).`when`(action).readFromFile(file)
        Assert.assertTrue(action.act(1f))
        Assert.assertEquals(expectedValue, userVariable?.value)

        verify(action, times(createFile)).getFile(DEFAULT_FILE_NAME)
        verify(action, times(readFromFile)).readFromFile(file)

        if (readFromFile > 0 && deleteFile) {
            verify(file, times(1)).delete()
        }
    }
}
