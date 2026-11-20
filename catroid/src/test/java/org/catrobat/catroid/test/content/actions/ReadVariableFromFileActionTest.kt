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

import org.catrobat.catroid.content.actions.ReadVariableFromFileAction
import org.catrobat.catroid.formulaeditor.UserVariable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class ReadVariableFromFileActionTest {
    private lateinit var action: ReadVariableFromFileAction
    private lateinit var testFile: File

    @Before
    fun setUp() {
        action = ReadVariableFromFileAction()
        testFile = File.createTempFile("read_test", ".txt")
        testFile.deleteOnExit()
    }

    @Test
    fun testCheckIfDataIsReady() {
        action.userVariable = null
        assertTrue("Action should not be ready when no user variable is assigned", !action
            .checkIfDataIsReady())

        action.userVariable = UserVariable("test", 0.0)
        assertTrue("Action should be ready when a user variable is assigned", action.checkIfDataIsReady())
    }

    @Test
    fun testHandleFileWorkReadsDoubleCorrectly() {
        testFile.writeText("42.5")
        val variable = UserVariable("v", 0.0)
        action.userVariable = variable
        val result = action.handleFileWork(testFile)

        assertTrue(result)
        assertEquals(42.5, variable.value)
    }

    @Test
    fun testHandleFileWorkReadsStringCorrectly() {
        val expectedText = "Hallo Catrobat"
        testFile.writeText(expectedText)
        val variable = UserVariable("v", "")
        action.userVariable = variable
        action.handleFileWork(testFile)

        assertEquals(expectedText, variable.value)
    }

    @Test
    fun testHandleFileWorkWithInvalidNumberFallsBackToString() {
        val mixedText = "123_abc"
        testFile.writeText(mixedText)
        val variable = UserVariable("v", 0.0)
        action.userVariable = variable
        action.handleFileWork(testFile)

        assertEquals(mixedText, variable.value)
    }

    @Test
    fun testHandleFileWorkWithEmptyFile() {
        testFile.writeText("")
        val variable = UserVariable("v", "initial")
        action.userVariable = variable
        action.handleFileWork(testFile)

        assertEquals("", variable.value)
    }
}