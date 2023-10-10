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
package org.catrobat.catroid.test.content.backwardcompatibility

import android.content.Context
import kotlin.Throws
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.exceptions.LoadingProjectException
import org.catrobat.catroid.io.XstreamSerializer
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.UserDataWrapper
import org.junit.After
import org.junit.Test
import java.io.File
import java.io.IOException

class XstreamParserTest {
    private var projectDir: File? = null
    @After
    @Throws(IOException::class)
    fun tearDown() {
        if (projectDir != null && projectDir!!.isDirectory) {
            StorageOperations.deleteDir(projectDir)
        }
    }

    @Throws(IOException::class)
    private fun copyProjectFromAssets(assetName: String, projectName: String) {
        val inputStream =
            InstrumentationRegistry.getInstrumentation().context.assets.open(assetName)
        ZipArchiver().unzip(
            inputStream,
            File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName)
        )
    }

    @Throws(IOException::class, LoadingProjectException::class)
    private fun testLoadProjectWithoutScenes(projectName: String, assetName: String) {
        copyProjectFromAssets(assetName, projectName)
        projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName)
        val project = XstreamSerializer.getInstance()
            .loadProject(projectDir, ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(project)
        org.junit.Assert.assertEquals(projectName, project.name)
        org.junit.Assert.assertEquals(1, project.sceneList.size.toLong())
        org.junit.Assert.assertEquals(
            ApplicationProvider.getApplicationContext<Context>()
                .getString(R.string.default_scene_name),
            project.sceneList[0].name
        )
    }

    @Test
    @Throws(IOException::class, LoadingProjectException::class)
    fun testLoadProjectWithLanguageVersion08() {
        val projectName = "Falling balls"
        val assetName = "Falling_balls.catrobat"
        testLoadProjectWithoutScenes(projectName, assetName)
    }

    @Test
    @Throws(IOException::class, LoadingProjectException::class)
    fun testLoadProjectLanguageVersion091() {
        val projectName = "Air fight 0.5"
        val assetName = "Air_fight_0.5.catrobat"
        testLoadProjectWithoutScenes(projectName, assetName)
    }

    @Test
    @Throws(IOException::class, LoadingProjectException::class)
    fun testLoadProjectLanguageVersion092() {
        val projectName = "NoteAndSpeakBrick"
        val assetName = "Note_And_Speak_Brick.catrobat"
        testLoadProjectWithoutScenes(projectName, assetName)
    }

    @Test
    @Throws(IOException::class, LoadingProjectException::class)
    fun testLoadProjectLanguageVersion095() {
        val projectName = "GhostEffectBricks"
        val assetName = "Ghost_Effect_Bricks.catrobat"
        testLoadProjectWithoutScenes(projectName, assetName)
    }

    @Test
    @Throws(IOException::class, LoadingProjectException::class)
    fun testLoadProjectLanguageVersion0999() {
        val projectName = "TestUserDataConversion0999To09991"
        val assetName = "TestUserDataConversion0999To09991.catrobat"
        copyProjectFromAssets(assetName, projectName)
        projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName)
        val project = XstreamSerializer.getInstance()
            .loadProject(projectDir, ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(project)
        org.junit.Assert.assertEquals(projectName, project.name)
        org.junit.Assert.assertEquals(2, project.sceneList.size.toLong())
        val scene1 = project.sceneList[0]
        val scene2 = project.sceneList[1]
        org.junit.Assert.assertEquals(
            "Scene 1",
            scene1.name
        )
        org.junit.Assert.assertEquals(
            "Scene 2",
            scene2.name
        )
        val scopeLocal = Scope(
            project,
            scene1.getSprite("SpriteWithLocalVarAndList"),
            SequenceAction()
        )
        val scopeGlobal = Scope(
            project,
            scene1.getSprite("SpriteWithGlobalVarAndList"),
            SequenceAction()
        )
        Assert.assertNotNull(UserDataWrapper.getUserVariable("localVar", scopeLocal))
        Assert.assertNotNull(UserDataWrapper.getUserList("localList", scopeLocal))
        Assert.assertNull(UserDataWrapper.getUserVariable("localVar", scopeGlobal))
        Assert.assertNull(UserDataWrapper.getUserList("localList", scopeGlobal))
        Assert.assertNotNull(UserDataWrapper.getUserVariable("globalVar", scopeLocal))
        Assert.assertNotNull(UserDataWrapper.getUserList("globalList", scopeLocal))
        Assert.assertNull(UserDataWrapper.getUserVariable("localVar", scopeGlobal))
        Assert.assertNull(UserDataWrapper.getUserList("localList", scopeGlobal))
        org.junit.Assert.assertNotSame(
            UserDataWrapper.getUserVariable("localVar", scopeLocal),
            UserDataWrapper.getUserVariable("globalList", scopeLocal)
        )
        org.junit.Assert.assertNotSame(
            UserDataWrapper.getUserList("localList", scopeLocal),
            UserDataWrapper.getUserList("globalList", scopeLocal)
        )
    }
}