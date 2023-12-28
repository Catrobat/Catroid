/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.io.catrobatlanguage

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.common.Constants.CACHE_DIRECTORY
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.asynctask.unzipAndImportProjects
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageProjectSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsCollectionContaining
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProgramSerializationTest {
    companion object {
        private val CATROBAT_VERSION_REGEX = Regex("Catrobat version: '.*'")
        private val CATROBAT_APP_VERSION_REGEX = Regex("Catrobat app version: '.*'")
        private const val EMPTY_VERSION_STRING = "Catrobat version: ''"
        private const val EMPTY_APP_VERSION_STRING = "Catrobat app version: ''"
    }

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java,
        ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Test
    fun testProgramSerialization() {
        val testProject = createProject()
        val serializedProject = CatrobatLanguageProjectSerializer(testProject, InstrumentationRegistry.getInstrumentation().context).serialize()

        val cleanReferenceProject = referenceProgramString
            .replace(CATROBAT_VERSION_REGEX, EMPTY_VERSION_STRING)
            .replace(CATROBAT_APP_VERSION_REGEX, EMPTY_APP_VERSION_STRING)

        val cleanSerializedProject = serializedProject
            .replace(CATROBAT_VERSION_REGEX, EMPTY_VERSION_STRING)
            .replace(CATROBAT_APP_VERSION_REGEX, EMPTY_APP_VERSION_STRING)

        val referenceLines = cleanReferenceProject.split("\n")
        val serializedLines = cleanSerializedProject.split("\n")
        Assert.assertEquals("Equal Line Count", referenceLines.size, serializedLines.size)
        for (i in referenceLines.indices) {
            Assert.assertEquals("Error in Line " + i, referenceLines[i], serializedLines[i])
        }
    }

    private fun createProject(): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), "Test Project")

        project.description = "This is a dummy description."
        project.xmlHeader.virtualScreenWidth = 1080
        project.xmlHeader.virtualScreenHeight = 1920
        project.xmlHeader.setlandscapeMode(false)
        project.xmlHeader.screenMode = ScreenModes.STRETCH

        project.userLists.add(UserList("Global list"))
        project.userLists.add(UserList("Another global list"))
        project.userVariables.add(UserVariable("Global variable"))
        project.userVariables.add(UserVariable("Another global variable"))

        val sprite = Sprite("Test Sprite 1")
        sprite.userLists.add(UserList("Local list"))
        sprite.userLists.add(UserList("Another local list"))
        sprite.userVariables.add(UserVariable("Local variable"))
        sprite.userVariables.add(UserVariable("Another local variable"))

        sprite.soundList.add(SoundInfo("Test Sound 1", File("testSound1.mp3")))
        sprite.lookList.add(LookData("Test Look 1", File("testLook1.png")))

        val script = StartScript()
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        val whenScript = WhenScript()
        sprite.scriptList.add(whenScript)
        whenScript.addBrick(NoteBrick("It's just a simple comment."))

        val secondSprite = Sprite("Second sprite")
        project.defaultScene.addSprite(secondSprite)
        secondSprite.lookList.add(LookData("Test Look 2", File("testLook2.png")))
        secondSprite.lookList.add(LookData("Test Look 3", File("testLook3.png")))
        secondSprite.soundList.add(SoundInfo("Test Sound 2", File("testSound2.mp3")))
        secondSprite.soundList.add(SoundInfo("Test Sound 3", File("testSound3.mp3")))
        secondSprite.userVariables.add(UserVariable("Local variable 2"))

        project.addScene(Scene("Empty scene", project))
        return project
    }

    private val referenceProgramString = """
        |#! Catrobat Language Version 0.1
        |Program 'Test Project' {
        |  Metadata {
        |    Description: 'This is a dummy description.',
        |    Catrobat version: '1.12',
        |    Catrobat app version: '1.1.2'
        |  }
        |
        |  Stage {
        |    Landscape mode: 'false',
        |    Width: '1080',
        |    Height: '1920',
        |    Display mode: 'STRETCH'
        |  }
        |
        |  Globals {
        |    "Global variable",
        |    "Another global variable",
        |    *Global list*,
        |    *Another global list*
        |  }
        |
        |  Scene 'Scene' {
        |    Background {
        |    }
        |    Actor or object 'Test Sprite 1' {
        |      Looks {
        |        'Test Look 1': 'testLook1.png'
        |      }
        |      Sounds {
        |        'Test Sound 1': 'testSound1.mp3'
        |      }
        |      Locals {
        |        "Local variable",
        |        "Another local variable",
        |        *Local list*,
        |        *Another local list*
        |      }
        |      Scripts {
        |        When scene starts {
        |        }
        |        When tapped {
        |          # It's just a simple comment.
        |        }
        |      }
        |    }
        |    Actor or object 'Second sprite' {
        |      Looks {
        |        'Test Look 2': 'testLook2.png',
        |        'Test Look 3': 'testLook3.png'
        |      }
        |      Sounds {
        |        'Test Sound 2': 'testSound2.mp3',
        |        'Test Sound 3': 'testSound3.mp3'
        |      }
        |      Locals {
        |        "Local variable 2"
        |      }
        |    }
        |  }
        |  Scene 'Empty scene' {
        |  }
        |}
        |""".trimMargin()

    @Test
    @Throws(IOException::class)
    fun testRealProgramSerialization() {
        val projectName = "The Binding of Krishna"
        val projectAssetName = "binding-of-krishna"
        val folderName = "catrobatLanguageTests/"

        TestUtils.deleteProjects(projectName)
        DEFAULT_ROOT_DIRECTORY.mkdir()
        CACHE_DIRECTORY.mkdir()

        val inputStream = InstrumentationRegistry.getInstrumentation().context.assets.open(folderName + projectAssetName + ".catrobat")
        val projectFile = StorageOperations.copyStreamToDir(inputStream, CACHE_DIRECTORY, projectAssetName + ".catrobat")
        TestCase.assertTrue(unzipAndImportProjects(arrayOf(projectFile)))
        MatcherAssert.assertThat(
            FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY),
            IsCollectionContaining.hasItems(projectName)
        )
        val projectDir = File(DEFAULT_ROOT_DIRECTORY, projectName)
        val project = XstreamSerializer.getInstance().loadProject(projectDir, ApplicationProvider.getApplicationContext())
        TestCase.assertNotNull(project)

        projectManager.currentProject = project
        baseActivityTestRule.launchActivity()
        val serializedProject = CatrobatLanguageProjectSerializer(project, InstrumentationRegistry.getInstrumentation().context).serialize()
        baseActivityTestRule.finishActivity()

        val compareFile = InstrumentationRegistry.getInstrumentation().context.assets.open(folderName + projectAssetName + ".txt")
        val referenceProject = compareFile.bufferedReader().use { it.readText() }

        val referenceLines = referenceProject.split('\n')
        val serializedLines = serializedProject.split('\n')
        Assert.assertEquals("Equal Line Count", referenceLines.size, serializedLines.size)
        for (i in referenceLines.indices) {
            Assert.assertEquals("Error in Line " + i, referenceLines[i], serializedLines[i])
        }

        StorageOperations.deleteDir(CACHE_DIRECTORY)
        TestUtils.deleteProjects(projectName)
    }
}