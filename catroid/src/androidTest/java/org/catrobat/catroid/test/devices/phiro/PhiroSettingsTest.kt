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
package org.catrobat.catroid.test.devices.phiro

import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.junit.runner.RunWith
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.After
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class PhiroSettingsTest {
    private var sharedPreferenceBuffer = false
    private val projectName = "testProject"
    private var project: Project? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sharedPreferenceBuffer =
            SettingsFragment.isPhiroSharedPreferenceEnabled(ApplicationProvider.getApplicationContext())
        SettingsFragment.setPhiroSharedPreferenceEnabled(
            ApplicationProvider.getApplicationContext(),
            false
        )
        createProject()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        SettingsFragment
            .setPhiroSharedPreferenceEnabled(
                ApplicationProvider.getApplicationContext(),
                sharedPreferenceBuffer
            )
    }

    @Test
    @Throws(IOException::class, ProjectException::class)
    fun testIfPhiroBricksAreEnabledIfItItUsedInAProgram() {
        Assert.assertFalse(SettingsFragment.isPhiroSharedPreferenceEnabled(ApplicationProvider.getApplicationContext()))
        ProjectManager.getInstance()
            .loadProject(project!!.directory, ApplicationProvider.getApplicationContext())
        Assert.assertTrue(SettingsFragment.isPhiroSharedPreferenceEnabled(ApplicationProvider.getApplicationContext()))
        StorageOperations.deleteDir(File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName))
    }

    private fun createProject() {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("Phiro")
        val startScript = StartScript()
        val setSizeToBrick = SetSizeToBrick(
            Formula(
                FormulaElement(
                    FormulaElement.ElementType.SENSOR,
                    Sensors.PHIRO_BOTTOM_LEFT.name, null
                )
            )
        )
        startScript.addBrick(setSizeToBrick)
        sprite.addScript(startScript)
        project!!.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
    }
}
