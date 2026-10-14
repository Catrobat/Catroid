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

import android.content.Context
import android.content.res.Resources
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.WriteVariableToFileAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.File

@RunWith(Parameterized::class)
class WriteVariableToFileActionTest(
    private val name: String,
    private val formula: Formula?,
    private val userVariable: UserVariable?,
    private val expectedFileContent: String,
    private val shouldWork: Boolean
) {
    private lateinit var sprite: Sprite
    private lateinit var mockContext: Context
    private lateinit var mockResources: Resources
    private lateinit var testFile: File

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            // name, formula, userVariable, expectedContent, shouldWork
            arrayOf("USER_VARIABLE_NULL", Formula("file.txt"), null, "", false),
            arrayOf("VALID_DOUBLE", Formula("file.txt"), UserVariable("v", 3.14), "3.14", true),
            arrayOf("VALID_STRING", Formula("file.txt"), UserVariable("v", "Hello"), "Hello", true),
            arrayOf("LARGE_NUMBER", Formula("file.txt"), UserVariable("v", 10000000.0), "10000000", true),
            arrayOf("UNICODE", Formula("file.txt"), UserVariable("v", "🐼"), "🐼", true)
        )
    }

    @Before
    fun setUp() {
        mockContext = Mockito.mock(Context::class.java)
        mockResources = Mockito.mock(Resources::class.java)

        `when`(mockContext.resources).thenReturn(mockResources)
        `when`(mockResources.getString(anyInt())).thenReturn("Mock Error String")

        sprite = Mockito.mock(Sprite::class.java)

        testFile = File.createTempFile("test_prefix", ".txt")
        testFile.deleteOnExit()
    }

    @Test
    fun testHandleFileWorkLogic() {
        val action = WriteVariableToFileAction()
        action.formula = formula
        action.userVariable = userVariable
        if (userVariable == null) {
            Assert.assertFalse("Action should not be ready without a user variable", action.checkIfDataIsReady())
        } else {
            Assert.assertTrue("Action should be ready", action.checkIfDataIsReady())
            val result = action.handleFileWork(testFile)

            Assert.assertTrue(result)
            Assert.assertEquals(expectedFileContent, testFile.readText())
        }
    }
}