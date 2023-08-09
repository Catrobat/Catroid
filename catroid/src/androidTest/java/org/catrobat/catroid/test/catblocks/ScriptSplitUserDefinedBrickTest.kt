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
import junit.framework.TestCase.assertEquals
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.content.EmptyScript
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScriptSplitUserDefinedBrickTest {
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
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun testSplitUserDefinedBrick() {
        val currentSprite = projectManager.currentSprite
        val scriptCount = currentSprite.scriptList.size
        assertEquals(2, scriptCount)

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#IfLogicBeginBrick-0") {
            val workspaceRectangle = webViewUtils.getWorkspaceBoundingClientRect()
            val elementRect = webViewUtils.getBoundingClientRectOfElement("#IfLogicBeginBrick-0")
            webViewUtils.moveElementByPixels(
                "#IfLogicBeginBrick-0", workspaceRectangle
                    .width.toInt() - elementRect.width.toInt(), elementRect.y.toInt() * -1
            )
        }

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val scriptList = currentSprite.scriptList
        assertEquals(3, scriptList.size)

        assertEquals(true, scriptList[0] is UserDefinedScript)
        assertEquals(true, scriptList[1] is StartScript)
        assertEquals(true, scriptList[2] is EmptyScript)

        val brickListOfNewScript = scriptList[2].brickList
        assertEquals(1, brickListOfNewScript.size)
        assertEquals(true, brickListOfNewScript[0] is IfLogicBeginBrick)
    }

    private fun createProject() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)

        val userDefinedBrick = createUserDefinedBrick(sprite)

        val startScript: Script = StartScript()
        startScript.addBrick(userDefinedBrick)

        sprite.addScript(startScript)

        projectManager.currentProject = project
        projectManager.currentSprite = sprite
    }

    private fun createUserDefinedBrick(sprite: Sprite): UserDefinedBrick {
        val userDefinedBrick = UserDefinedBrick()
        userDefinedBrick.addInput("Input")
        userDefinedBrick.addLabel("Label")

        sprite.addUserDefinedBrick(userDefinedBrick)

        val userDefinedScript = UserDefinedScript(userDefinedBrick.userDefinedBrickID)
        val ifBrick = IfLogicBeginBrick()
        userDefinedScript.addBrick(ifBrick)

        sprite.addScript(userDefinedScript)

        val userDefinedCallingBrick = UserDefinedBrick(userDefinedBrick)
        userDefinedCallingBrick.setCallingBrick(true)
        return userDefinedCallingBrick
    }
}
