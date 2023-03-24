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
package org.catrobat.catroid.test.common

import org.junit.runner.RunWith
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.common.DefaultProjectHandler
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.junit.After
import org.junit.Test
import java.io.File
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class DefaultProjectHandlerTest {
    private var project: Project? = null
    @Before
    @Throws(IOException::class)
    fun setUp() {
        DefaultProjectHandler.getInstance()
            .setDefaultProjectCreator(DefaultProjectHandler.ProjectCreatorType.PROJECT_CREATOR_DEFAULT)
        val projectName = "defaultProject"
        project = DefaultProjectHandler
            .createAndSaveDefaultProject(
                projectName,
                ApplicationProvider.getApplicationContext(),
                false
            )
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        StorageOperations.deleteDir(project!!.directory)
    }

    @Test
    fun testCreateScaledDefaultProject() {
        val currentScene = project!!.defaultScene
        val spriteList = currentScene.spriteList
        Assert.assertEquals(4, spriteList.size)
        Assert.assertEquals(1, spriteList[1].numberOfScripts)
        Assert.assertEquals(1, spriteList[2].numberOfScripts)
        Assert.assertEquals(3, spriteList[3].numberOfScripts)
        Assert.assertEquals(1, spriteList[0].lookList.size)
        Assert.assertEquals(1, spriteList[1].lookList.size)
        Assert.assertEquals(1, spriteList[2].lookList.size)
        Assert.assertEquals(2, spriteList[3].lookList.size)
    }

    @Test
    fun testDefaultProjectScreenshot() {
        val currentScene = project!!.defaultScene
        var file = File(currentScene.directory, Constants.SCREENSHOT_MANUAL_FILE_NAME)
        Assert.assertFalse(file.exists())
        file = File(currentScene.directory, Constants.SCREENSHOT_AUTOMATIC_FILE_NAME)
        Assert.assertTrue(file.exists())
    }
}