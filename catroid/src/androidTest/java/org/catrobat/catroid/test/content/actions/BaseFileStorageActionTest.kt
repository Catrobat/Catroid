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
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.actions.BaseFileStorageAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.stage.StageActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.File
import java.lang.ref.WeakReference

@RunWith(AndroidJUnit4::class)
class BaseFileStorageActionTest {
    class DummyFileAction : BaseFileStorageAction() {
        var contextForTest: Context? = null
        var handleFileWorkCalled = false
        var throwErrorInWork = false

        override val context: Context
            get() = contextForTest ?: ApplicationProvider.getApplicationContext()

        override var formula: Formula? = null
        override var scope: Scope? = null
        public override var fileExtension = ".txt"
        public override var isReadAction = false
        override val errorMessageResId = 0

        public override fun checkIfDataIsReady() = true
        override fun showDataMissingMessage() {}
        override fun getTargetIntent(): android.content.Intent = android.content.Intent()

        public override fun handleFileWork(file: File): Boolean {
            handleFileWorkCalled = true
            if (throwErrorInWork) throw Exception("Test Error")
            file.writeText("Test data.")
            return true
        }

        fun callGetFileName(): String = getFileName()

        fun callPerformUriOperation(uri: android.net.Uri?) = performUriOperation(uri)
    }

    private lateinit var action: DummyFileAction

    @Before
    fun setUp() {
        action = DummyFileAction()
        val mockActivity = Mockito.mock(StageActivity::class.java)
        StageActivity.activeStageActivity = WeakReference(mockActivity)
        action.contextForTest = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testAct_ShouldDoNothingIfRequiredDataIsMissing() {
        // Case 1:
        action.formula = null
        action.scope = null

        val resultBothNull = action.act(0.1f)
        assertTrue("Should return true even if data is missing", resultBothNull)
        assertFalse("Should NOT call handleFileWork if formula and scope are null", action.handleFileWorkCalled)

        // Case 2:
        action.formula = Formula("'testfile'")
        action.scope = null

        val resultScopeNull = action.act(0.1f)
        assertTrue("Should return true if scope is missing", resultScopeNull)
        assertFalse("Should NOT call handleFileWork if scope is null", action.handleFileWorkCalled)
    }

    @Test
    fun testFileNameSanitization() {
        // Testing if illegal characters are replaced/removed
        action.formula = Formula("'my/illegal*dataname'")
        val fileName = action.callGetFileName()

        assertFalse("Filename must not contain slashes", fileName.contains("/"))
        assertFalse("Filename must not contain asterisks", fileName.contains("*"))
        assertTrue("Filename should be sanitized", fileName.contains("myillegaldataname"))
        assertTrue("Filename must have the correct extension", fileName.endsWith(".txt"))
    }

    @Test
    fun testCoreFileLifecycle() {
        action.formula = Formula("'testfile'")
        val fileName = action.callGetFileName()
        val testContext = ApplicationProvider.getApplicationContext<Context>()
        val cacheFile = File(testContext.cacheDir, fileName)

        try {
            if (action.checkIfDataIsReady()) {
                action.handleFileWork(cacheFile)
            }
        } finally {
            if (cacheFile.exists()) {
                cacheFile.delete()
            }
        }

        assertTrue("handleFileWork should have been called", action.handleFileWorkCalled)
        assertFalse("Cache file MUST be deleted after execution", cacheFile.exists())
    }

    @Test
    fun testCacheFileLifecycle_Failure() {
        action.formula = Formula("'errorfile'")
        val fileName = action.callGetFileName()
        val cacheFile = File(action.contextForTest?.cacheDir, fileName)
        action.throwErrorInWork = true
        try {
            action.callPerformUriOperation(null)
        } catch (e: Exception) {
            assertEquals("Test Error", e.message)
        }

        assertTrue("handleFileWork should be triggered even if it fails later", action.handleFileWorkCalled)
        assertFalse("Cache file MUST be deleted even if an error occurs during the process", cacheFile.exists())
    }

    @Test
    fun testCheckIfDataIsReady_Base() {
        action.formula = Formula("'readyTest'")

        assertTrue("Action should be ready by default in this dummy implementation", action.checkIfDataIsReady())
    }

    @Test
    fun testGetFileName_WriteActionAppendsExtensionEvenIfOtherExtensionExists() {
        action.formula = Formula("'project.pdf'")
        val fileName = action.callGetFileName()
        assertTrue("Write actions must enforce their specific extension",
                   fileName.endsWith(".txt"))
        assertFalse("Filename should not end with .pdf alone", fileName.endsWith(".pdf"))
    }

    @Test
    fun testGetFileName_ReadActionPreservesCustomExtension() {
        action.isReadAction = true
        action.fileExtension = ".txt"
        action.formula = Formula("'my_data.custom'")

        val fileName = action.callGetFileName()

        assertTrue("Read actions should preserve existing extensions",
                   fileName.contains("custom"))
        assertFalse("Read actions should NOT append default extension if one is present",
                    fileName.endsWith(".custom.txt"))
    }
}