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
package org.catrobat.catroid.test.io

import android.content.Context
import org.junit.runner.RunWith
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.ProjectManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.R
import org.catrobat.catroid.common.defaultprojectcreators.ChromeCastProjectCreator
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.exceptions.LoadingProjectException
import org.catrobat.catroid.io.XstreamSerializer
import org.junit.After
import org.junit.Test
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class DefaultChromeCastProgramLoadingTest {
    private var projectName: String? = null
    private var currentProjectBuffer: Project? = null
    private var project: Project? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        currentProjectBuffer = ProjectManager.getInstance().currentProject
        projectName = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.default_cast_project_name)
        project = ChromeCastProjectCreator()
            .createDefaultProject(projectName, ApplicationProvider.getApplicationContext(), true)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ProjectManager.getInstance().currentProject = currentProjectBuffer
        TestUtils.deleteProjects(projectName)
    }

    @Test
    @Throws(IOException::class, LoadingProjectException::class)
    fun testLoadingChromeCastProgram() {
        val loadedProject = XstreamSerializer.getInstance()
            .loadProject(project!!.directory, ApplicationProvider.getApplicationContext())
        val preScene = project!!.defaultScene
        val postScene = loadedProject.defaultScene
        val preSpriteList = project!!.defaultScene.spriteList as ArrayList<Sprite>
        val postSpriteList = loadedProject.defaultScene.spriteList as ArrayList<Sprite>
        Assert.assertEquals(project!!.name, loadedProject.name)
        Assert.assertEquals(preScene.name, postScene.name)
        Assert.assertEquals(preSpriteList[0].name, postSpriteList[0].name)
        Assert.assertEquals(preSpriteList[1].name, postSpriteList[1].name)
        Assert.assertEquals(preSpriteList[2].name, postSpriteList[2].name)
        Assert.assertEquals(preSpriteList[3].name, postSpriteList[3].name)
    }
}
