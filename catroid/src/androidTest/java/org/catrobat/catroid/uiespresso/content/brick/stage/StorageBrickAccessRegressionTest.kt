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
package org.catrobat.catroid.uiespresso.content.brick.stage

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.bricks.*
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class StorageBrickAccessRegressionTest {

    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var testFile: File? = null

    @After
    fun tearDown() {
        val qPlusDir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_DOWNLOADS
        )
        val legacyDir = Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY
        val fileName = "test_read_Variable.txt"
        val fileInDownloads = File(qPlusDir, fileName)
        val fileInLegacy = File(legacyDir, fileName)

        if (fileInDownloads.exists()) fileInDownloads.delete()
        if (fileInLegacy.exists()) fileInLegacy.delete()
    }

    @Test
    fun testReadVariableFromFileBrickEndToEndFlow() {
        val fileName = "test_read_Variable.txt"
        val secretValue = 2.0
        val tempValue = 8.0

        val directory = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
        } else {
            Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY
        }

        val fileToRead = File(directory, fileName)
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                uiAutomation.adoptShellPermissionIdentity()
            }

            if (!directory.exists()) {
                directory.mkdirs()
            }
            fileToRead.writeText(secretValue.toString())
        } finally {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                uiAutomation.dropShellPermissionIdentity()
            }
        }

        val myVar = UserVariable("test_read_Variable", tempValue)
        val readBrick = ReadVariableFromFileBrick(Formula(fileName)).apply {
            userVariable = myVar
        }

        runStorageFlowTest(
            "ReadVariableFromFileRegression",
            fileName,
            emptyList(),
            readBrick, // Der Test öffnet direkt den Lese-Dialog
            true,
            secretValue,
            myVar
        )
    }

    @Test
    fun testWriteEmbroideryToFileBrickEndToEndFlow() {
        val fileName = "test_embroidery_${System.currentTimeMillis()}.dst"
        val saveBrick = WriteEmbroideryToFileBrick(Formula(fileName))
        val setupBricks: List<Brick> = listOf(
            PlaceAtBrick(), RunningStitchBrick(Formula(10.0)),
            MoveNStepsBrick(50.0)
        )

        runStorageFlowTest(
            "writeEmbroideryRegression",
            fileName,
            setupBricks,
            saveBrick,
            false
        )
    }

    @Test
    fun testSavePlotBrickEndToEndFlow() {
        val fileName = "test_plot_${System.currentTimeMillis()}.svg"
        val saveBrick = SavePlotBrick(Formula(fileName))
        val setupBricks: List<Brick> =
            listOf(PlaceAtBrick(), StartPlotBrick(), MoveNStepsBrick(50.0))

        runStorageFlowTest(
            "savePlotRegression",
            fileName,
            setupBricks,
            saveBrick,
            false
        )
    }

    @Test
    fun testSaveLaserBrickEndToEndFlow() {
        val fileName = "test_laser_${System.currentTimeMillis()}.svg"
        val saveBrick = SaveLaserBrick(Formula(fileName))
        val setupBricks: List<Brick> =
            listOf(PlaceAtBrick(), StartCutBrick(), MoveNStepsBrick(50.0))

        runStorageFlowTest(
            "saveLaserRegression",
            fileName,
            setupBricks,
            saveBrick,
            false
        )
    }

    @Test
    fun testWriteVariableToFileBrickEndToEndFlow() {
        val fileName = "test_write_Variable_${System.currentTimeMillis()}.txt"
        val myVar = UserVariable("test_write_Variable", 0.0)
        val writeBrick = WriteVariableToFileBrick(Formula(fileName)).apply {
            userVariable = myVar
        }

        val setupBricks = listOf(
            SetVariableBrick(Formula("1.0"), myVar)
        )

        runStorageFlowTest(
            "WriteVariableToFileRegression",
            fileName,
            setupBricks,
            writeBrick,
            false,
            variableToRegister = myVar
        )
    }


    private fun waitForFile(file: File, timeoutMs: Long = 10000) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (file.exists()) return
            Thread.sleep(100)
        }
        fail("The file was not generated in time: ${file.absolutePath}")
    }

    private fun waitForVariableValue(
        variable: UserVariable,
        expected: Double,
        timeoutMs: Long = 10000
    ) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            val value = variable.value as? Double
            if (value != null && kotlin.math.abs(value - expected) < 0.001) return
            Thread.sleep(100)
        }
        fail("The variable was not set to $expected in time. Current value: ${variable.value}")
    }

    private fun runStorageFlowTest(
        projectName: String,
        expectedFileName: String,
        generateDataBricks: List<Brick>,
        mainBrick: Brick,
        isRead: Boolean,
        expectedVal: Double = 0.0,
        variableToRegister: UserVariable? = null
    ) {
        val project = UiTestUtils.createDefaultTestProject(projectName)
        val script = UiTestUtils.getDefaultTestScript(project)
        val sprite = requireNotNull(UiTestUtils.getDefaultTestSprite(project)) {
            "Error: Could not retrieve the default sprite!"
        }

        if (variableToRegister != null) {
            // Reset variable to a clean state before the test starts
            variableToRegister.value = 0.0
            sprite.userVariables.add(variableToRegister)
        }

        generateDataBricks.forEach { script.addBrick(it) }
        script.addBrick(mainBrick)

        baseActivityTestRule.launchActivity()
        onView(withId(R.id.button_play)).perform(click())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

            val saveButton = device.findObject(
                androidx.test.uiautomator.UiSelector().textMatches("(?i)save|speichern|auswählen")
            )
            if (saveButton.waitForExists(5000)) {
                saveButton.click()
                saveButton.waitUntilGone(5000)
            }

            if (isRead) {
                val fileSelector = By.text(expectedFileName)
                val fileEntry = device.wait(Until.findObject(fileSelector), 10000)

                if (fileEntry != null) {
                    fileEntry.click()
                    device.wait(Until.gone(fileSelector), 5000)
                } else {
                    fail("File $expectedFileName not found in the selection dialog!")
                }
            }
        }

        val directory = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
        } else {
            Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY
        }
        val fileToCheck = File(directory, expectedFileName)
        testFile = fileToCheck

        if (!isRead) {
            waitForFile(fileToCheck)
            assertTrue(
                "The E2E flow failed. File was not created at: ${fileToCheck.absolutePath}",
                fileToCheck.exists()
            )
        } else {
            assertNotNull("No variable provided for verification!", variableToRegister)

            waitForVariableValue(variableToRegister!!, expectedVal)

            assertEquals(
                "The read value does not match the expected stored value!",
                expectedVal,
                variableToRegister.value as Double,
                0.001
            )
        }
    }

}