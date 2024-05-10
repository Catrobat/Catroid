/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.test.io.catrobatlanguage.serializer

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenConditionScript
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisabledBrickSerializationTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("settings put global window_animation_scale 0")
        device.executeShellCommand("settings put global transition_animation_scale 0")
        device.executeShellCommand("settings put global animator_duration_scale 0")
    }

    @After
    fun tearDown() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("settings put global window_animation_scale 1")
        device.executeShellCommand("settings put global transition_animation_scale 1")
        device.executeShellCommand("settings put global animator_duration_scale 1")

        baseActivityTestRule.finishActivity()
    }

    @Test
    fun testDisabledBrickBehavior() {
        val startBrick = createProject()
        baseActivityTestRule.launchActivity()

        val serialzedProject = startBrick.serializeToCatrobatLanguage(0)
        Assert.assertEquals("Wrong serialization of disabled brick", """
                |When condition becomes true (condition: ('1 < 2')) {
                |  Forever {
                |    Set (variable: ("var1"), value: (10));
                |    If (condition: ('true')) {
                |      Broadcast (message: ('test'));
                |    } else {
                |      Repeat until (condition: ('"var1" == 5')) {
                |        Set (variable: ("var1"), value: ('"var1" + 1'));
                |      }
                |    }
                |  }
                |}
                |""".trimMargin(),
            serialzedProject
        )

        onView(withId(R.id.when_conditon_label))
            .perform(ViewActions.click())
        onView(withText(R.string.brick_context_dialog_comment_out_script))
            .perform(ViewActions.click())

        val commentedOutProject = startBrick.serializeToCatrobatLanguage(0)
        Assert.assertEquals("Wrong serialization of disabled brick", """
                |// When condition becomes true (condition: ('1 < 2')) {
                |  // Forever {
                |    // Set (variable: ("var1"), value: (10));
                |    // If (condition: ('true')) {
                |      // Broadcast (message: ('test'));
                |    // } else {
                |      // Repeat until (condition: ('"var1" == 5')) {
                |        // Set (variable: ("var1"), value: ('"var1" + 1'));
                |      // }
                |    // }
                |  // }
                |// }
                |""".trimMargin(),
            commentedOutProject
        )

        onView(withId(R.id.if_label))
            .perform(ViewActions.click())
        onView(withText(R.string.brick_context_dialog_comment_in))
            .perform(ViewActions.click())

        val partialCommentedOut = startBrick.serializeToCatrobatLanguage(0)
        Assert.assertEquals("Wrong serialization of disabled brick", """
                |// When condition becomes true (condition: ('1 < 2')) {
                |  // Forever {
                |    // Set (variable: ("var1"), value: (10));
                |    If (condition: ('true')) {
                |      Broadcast (message: ('test'));
                |    } else {
                |      Repeat until (condition: ('"var1" == 5')) {
                |        Set (variable: ("var1"), value: ('"var1" + 1'));
                |      }
                |    }
                |  // }
                |// }
                |""".trimMargin(),
            partialCommentedOut
        )
    }

    private fun createProject(): WhenConditionBrick {
        val project = Project(
            ApplicationProvider.getApplicationContext(),
            javaClass.simpleName
        )
        val sprite = Sprite("testSprite")
        val script = WhenConditionScript(Formula("1 < 2"))
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
        projectManager.currentProject.userVariables.add(UserVariable("var1"))

        val foreverBrick = ForeverBrick()
        val setVariableBrick = SetVariableBrick(
            Formula(10),
            projectManager.currentProject.userVariables[0]
        )
        foreverBrick.addBrick(setVariableBrick)

        val ifElseBrick = IfLogicBeginBrick(Formula("true"))
        val broadcastBrick = BroadcastBrick("test")
        ifElseBrick.addBrickToIfBranch(broadcastBrick)

        val loopUntilBrick = RepeatUntilBrick(Formula("\"var1\" == 5"))
        val setVariable2Brick = SetVariableBrick(Formula("\"var1\" + 1"), projectManager.currentProject.userVariables[0])
        loopUntilBrick.addBrick(setVariable2Brick)
        ifElseBrick.addBrickToElseBranch(loopUntilBrick)

        foreverBrick.addBrick(ifElseBrick)
        script.addBrick(foreverBrick)

        return WhenConditionBrick(script)
    }
}
