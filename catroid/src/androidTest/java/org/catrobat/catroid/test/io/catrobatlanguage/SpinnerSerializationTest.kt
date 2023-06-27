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

import androidx.annotation.IdRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.CloneBrick
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import kotlin.random.Random

class SpinnerSerializationTest {
    private lateinit var startScript: StartScript;

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject()
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun testCloneBrick() {
        executeTest(
            R.id.brick_clone_spinner,
            CloneBrick(),
            "Create clone of (actor or object: (yourself));\n",
            mapOf(
                "testSprite1" to "Create clone of (actor or object: ('testSprite1'));\n",
                "testSprite2" to "Create clone of (actor or object: ('testSprite2'));\n",
                "testSprite3" to "Create clone of (actor or object: ('testSprite3'));\n"
            )
        )
    }

    private fun executeTest(
        @IdRes brickSpinnerId: Int,
        brick: Brick,
        defaultValue: String,
        mapOf: Map<String, String>
    ) {
        startScript.addBrick(brick)
        baseActivityTestRule.launchActivity()

        val initialValue = brick.serializeToCatrobatLanguage(0)
        Assert.assertEquals(initialValue, defaultValue)

        testIndentAndComment(brick, defaultValue)

        for ((key, value) in mapOf) {
            onView(withId(brickSpinnerId)).perform(click())
            onView(withText(key)).perform(click())

            val newValue = brick.serializeToCatrobatLanguage(0)
            Assert.assertEquals(newValue, value)
        }
    }

    private fun testIndentAndComment(brick: Brick, baseValue: String) {
        brick.isCommentedOut = true

        val trimmedBaseValue = baseValue.substring(0, baseValue.length - 1)
        val disabledValue = brick.serializeToCatrobatLanguage(0)
        Assert.assertEquals("/* $trimmedBaseValue */\n", disabledValue)

        val randomIndent = Random.nextInt(2, 10)
        val disabledIndent = brick.serializeToCatrobatLanguage(randomIndent)
        val indent = CatrobatLanguageUtils.getIndention(randomIndent)
        Assert.assertEquals("$indent/* $trimmedBaseValue */\n", disabledIndent)

        brick.isCommentedOut = false
        val enabledValue = brick.serializeToCatrobatLanguage(randomIndent)
        Assert.assertEquals("$indent$baseValue", enabledValue)
    }

    private fun createProject() {
        val projectName = javaClass.simpleName
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        val sprite1 = Sprite("testSprite1")
        val sprite2 = Sprite("testSprite2")
        val sprite3 = Sprite("testSprite3")

        project.sceneList.add(Scene("s1", project))
        project.sceneList.add(Scene("s2", project))
        project.sceneList.add(Scene("s3", project))

        project.defaultScene.addSprite(sprite)
        project.defaultScene.addSprite(sprite1)
        project.defaultScene.addSprite(sprite2)
        project.defaultScene.addSprite(sprite3)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite

        project.defaultScene.backgroundSprite.lookList.add(LookData("look1", File("look1.jpg")))
        project.defaultScene.backgroundSprite.lookList.add(LookData("look2", File("look2.jpg")))
        project.defaultScene.backgroundSprite.lookList.add(LookData("look3", File("look3.jpg")))

        sprite.lookList.add(LookData("spritelook1", File("look1.jpg")))
        sprite.lookList.add(LookData("spritelook2", File("look2.jpg")))
        sprite.lookList.add(LookData("spritelook3", File("look3.jpg")))

        sprite.soundList.add(SoundInfo("sound1", File("sound1.mp3")))
        sprite.soundList.add(SoundInfo("sound2", File("sound1.mp3")))
        sprite.soundList.add(SoundInfo("sound3", File("sound3.mp3")))

        projectManager.currentProject.userVariables.add(UserVariable("var1"))
        projectManager.currentProject.userVariables.add(UserVariable("var2"))
        projectManager.currentProject.userVariables.add(UserVariable("var3"))

        projectManager.currentProject.userLists.add(UserList("list1"))
        projectManager.currentProject.userLists.add(UserList("list2"))
        projectManager.currentProject.userLists.add(UserList("list3"))

        projectManager.currentProject.broadcastMessageContainer.addBroadcastMessage("Broadcast1")

        val script = StartScript()
        projectManager.currentSprite.addScript(script)
        startScript = script
    }

    class SpinnerValueMapping {
        val spinnerId: Int
        val expectedStrings: List<String>

        constructor(spinnerId: Int, expectedStrings: List<String>) {
            this.spinnerId = spinnerId
            this.expectedStrings = expectedStrings
        }
    }
}

