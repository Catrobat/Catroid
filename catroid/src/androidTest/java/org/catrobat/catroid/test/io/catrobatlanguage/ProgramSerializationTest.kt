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
import org.catrobat.catroid.io.catlang.CatrobatLanguageProjectSerializer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ProgramSerializationTest {

    @Test
    fun testProgramSerialization() {
        val testProject = createProject()
        val serializedProject = CatrobatLanguageProjectSerializer(testProject).serialize()

        val catrobatVersionRegex = Regex("Catrobat version: '.*'")
        val catrobatAppVersionRegex = Regex("Catrobat app version: '.*'")

        val emptyVersionString = "Catrobat version: ''"
        val emptyAppVersionString = "Catrobat app version: ''"

        val cleanReferenceProject = referenceProgramString
            .replace(catrobatVersionRegex, emptyVersionString)
            .replace(catrobatAppVersionRegex, emptyAppVersionString)

        val cleanSerializedProject = serializedProject
            .replace(catrobatVersionRegex, emptyVersionString)
            .replace(catrobatAppVersionRegex, emptyAppVersionString)

        Assert.assertEquals(cleanReferenceProject, cleanSerializedProject)
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

    private val referenceProgramString = "#! Catrobat Language Version 0.1\n" +
    "Program 'Test Project' {\n" +
    "  Metadata {\n" +
    "    Description: 'This is a dummy description.'\n" +
    "    Catrobat version: '1.12'\n" +
    "    Catrobat app version: '1.1.2'\n" +
    "  }\n" +
    "\n" +
    "  Stage {\n" +
    "    Landscape mode: 'false'\n" +
    "    Width: '1080'\n" +
    "    Height: '1920'\n" +
    "    Display mode: 'STRETCH'\n" +
    "  }\n" +
    "\n" +
    "  Globals {\n" +
    "    \"Global variable\",\n" +
    "    \"Another global variable\",\n" +
    "    *Global list*,\n" +
    "    *Another global list*\n" +
    "  }\n" +
    "\n" +
    "  Scene 'Scene' {\n" +
    "    Background {\n" +
    "    }\n" +
    "    Actor or object 'Test Sprite 1' {\n" +
    "      Looks {\n" +
    "        'Test Look 1': 'testLook1.png'\n" +
    "      }\n" +
    "      Sounds {\n" +
    "        'Test Sound 1': 'testSound1.mp3'\n" +
    "      }\n" +
    "      Locals {\n" +
    "        \"Local variable\",\n" +
    "        \"Another local variable\",\n" +
    "        *Local list*,\n" +
    "        *Another local list*\n" +
    "      }\n" +
    "      Scripts {\n" +
    "        When scene starts {\n" +
    "        }\n" +
    "\n" +
    "        When tapped {\n" +
    "          # It's just a simple comment.\n" +
    "        }\n" +
    "\n" +
    "      }\n" +
    "    }\n" +
    "    Actor or object 'Second sprite' {\n" +
    "      Looks {\n" +
    "        'Test Look 2': 'testLook2.png',\n" +
    "        'Test Look 3': 'testLook3.png'\n" +
    "      }\n" +
    "      Sounds {\n" +
    "        'Test Sound 2': 'testSound2.mp3',\n" +
    "        'Test Sound 3': 'testSound3.mp3'\n" +
    "      }\n" +
    "      Locals {\n" +
    "        \"Local variable 2\"\n" +
    "      }\n" +
    "    }\n" +
    "  }\n" +
    "  Scene 'Empty scene' {\n" +
    "  }\n" +
    "}\n"
}