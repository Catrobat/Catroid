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
package org.catrobat.catroid.test.devices.arduino

import android.content.Context
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.junit.runner.RunWith
import org.catrobat.catroid.test.devices.arduino.ArduinoSettingsTest
import org.junit.Before
import kotlin.Throws
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick
import org.junit.After
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class ArduinoSettingsTest {
    private var sharedPreferenceBuffer = false
    private val projectName = ArduinoSettingsTest::class.java.simpleName
    private var project: Project? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPreferenceBuffer = SettingsFragment.isArduinoSharedPreferenceEnabled(context)
        SettingsFragment.setArduinoSharedPreferenceEnabled(context, false)
        createProjectArduino()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
        SettingsFragment
            .setArduinoSharedPreferenceEnabled(
                ApplicationProvider.getApplicationContext(),
                sharedPreferenceBuffer
            )
    }

    @Test
    @Throws(IOException::class, ProjectException::class)
    fun testIfArduinoBricksAreEnabledIfItItUsedInAProgram() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Assert.assertFalse(SettingsFragment.isArduinoSharedPreferenceEnabled(context))
        ProjectManager.getInstance().loadProject(project!!.directory, context)
        Assert.assertTrue(SettingsFragment.isArduinoSharedPreferenceEnabled(context))
        StorageOperations.deleteDir(File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName))
    }

    private fun createProjectArduino() {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("Arduino")
        val startScript = StartScript()
        val brick = ArduinoSendPWMValueBrick(3, 255)
        startScript.addBrick(brick)
        sprite.addScript(startScript)
        project!!.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
    }
}