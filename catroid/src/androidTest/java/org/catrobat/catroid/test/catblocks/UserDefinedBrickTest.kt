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

package org.catrobat.catroid.test.catblocks

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.recyclerview.fragment.CatblocksScriptFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDefinedBrickTest {
    private val projectName = javaClass.simpleName

    companion object {
        private const val TIMEOUT: Long = 30
    }

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        CatblocksScriptFragment.testingMode = true
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false)
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false)
    }

    @Test
    fun testUserDefinedBrickNestingAndRecursion() {
        val currentSprite = UiTestCatroidApplication.projectManager.currentSprite
        val scriptCount = currentSprite.scriptList.size
        assertEquals(3, scriptCount)

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)

        checkIfIDSequenceExists(
            webViewUtils, arrayOf(
                "UserDefinedScript-0", "IfLogicBeginBrick-0",
                "UserDefinedScript-0-Call-0"
            )
        )
        checkIfIDSequenceExists(
            webViewUtils, arrayOf(
                "UserDefinedScript-0", "IfLogicBeginBrick-0",
                "UserDefinedScript-0-Call-1"
            )
        )
        checkIfIDSequenceExists(
            webViewUtils, arrayOf(
                "UserDefinedScript-0", "IfLogicBeginBrick-0",
                "UserDefinedScript-1-Call-0"
            )
        )

        checkIfIDSequenceExists(
            webViewUtils, arrayOf(
                "UserDefinedScript-1", "IfLogicBeginBrick-1",
                "UserDefinedScript-1-Call-1"
            )
        )
        checkIfIDSequenceExists(
            webViewUtils, arrayOf(
                "UserDefinedScript-1", "IfLogicBeginBrick-1",
                "UserDefinedScript-1-Call-2"
            )
        )
        checkIfIDSequenceExists(
            webViewUtils, arrayOf(
                "UserDefinedScript-1", "IfLogicBeginBrick-1",
                "UserDefinedScript-0-Call-2"
            )
        )
    }

    private fun checkIfIDSequenceExists(webViewUtils: WebViewUtils, ids: Array<String>) {
        val querySelector = ids.joinToString(" > ") { id -> "#$id" }
        webViewUtils.waitForElement(querySelector)
    }

    private fun createProject() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)

        val userDefinedBrick1 = createUserDefinedBrick(sprite, "UDB1")
        val userDefinedBrick2 = createUserDefinedBrick(sprite, "UDB2")

        sprite.scriptList[0].addBrick(userDefinedBrick2)
        sprite.scriptList[1].addBrick(userDefinedBrick1)

        val startScript: Script = StartScript()
        startScript.addBrick(userDefinedBrick1.clone())
        startScript.addBrick(userDefinedBrick2.clone())

        sprite.addScript(startScript)

        UiTestCatroidApplication.projectManager.currentProject = project
        UiTestCatroidApplication.projectManager.currentSprite = sprite
    }

    private fun createUserDefinedBrick(sprite: Sprite, name: String): UserDefinedBrick {
        val userDefinedBrick = UserDefinedBrick()
        userDefinedBrick.addInput("Input")
        userDefinedBrick.addLabel(name)

        sprite.addUserDefinedBrick(userDefinedBrick)

        val userDefinedScript = UserDefinedScript(userDefinedBrick.userDefinedBrickID)
        val ifBrick = IfLogicBeginBrick()
        userDefinedScript.addBrick(ifBrick)

        sprite.addScript(userDefinedScript)

        val userDefinedCallingBrick = UserDefinedBrick(userDefinedBrick)
        userDefinedCallingBrick.setCallingBrick(true)
        ifBrick.addBrickToIfBranch(userDefinedCallingBrick.clone())
        ifBrick.addBrickToElseBranch(userDefinedCallingBrick.clone())

        return userDefinedCallingBrick
    }
}
