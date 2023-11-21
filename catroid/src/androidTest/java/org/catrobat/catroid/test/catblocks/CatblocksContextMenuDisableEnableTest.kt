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
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
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
class CatblocksContextMenuDisableEnableTest {

    companion object {
        private const val TIMEOUT: Long = 30
        private const val TEST_SPRITE = "testSprite"
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
    fun testContextMenuItemsOfBrickDisableEnable() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#SetXBrick-0") {
            webViewUtils.waitForElementVisible("#SetXBrick-0")
            webViewUtils.rightClickElement("#SetXBrick-0")
        }

        assertEquals(
            true, webViewUtils.isElementVisibleByString(
                ".blocklyMenuItemContent",
                context.getString(R.string.brick_context_dialog_comment_out)
            )
        )

        // Disable brick
        webViewUtils.clickElementByString(
            ".blocklyMenuItemContent", context.getString(
                R.string
                    .brick_context_dialog_comment_out
            )
        )
        webViewUtils.waitForElement("#SetXBrick-0") {
            webViewUtils.rightClickElement("#SetXBrick-0")
        }

        assertEquals(
            true, webViewUtils.isElementVisibleByString(
                ".blocklyMenuItemContent",
                context.getString(
                    R.string
                        .brick_context_dialog_comment_in
                )
            )
        )

        // Enable brick
        webViewUtils.clickElementByString(
            ".blocklyMenuItemContent", context.getString(
                R.string
                    .brick_context_dialog_comment_in
            )
        )
        webViewUtils.waitForElement("#SetXBrick-0") {
            webViewUtils.rightClickElement("#SetXBrick-0")
        }

        assertEquals(
            true, webViewUtils.isElementVisibleByString(
                ".blocklyMenuItemContent", context
                    .getString(R.string.brick_context_dialog_comment_out)
            )
        )
    }

    @Test
    fun testContextMenuItemsOfScriptDisableEnable() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#StartScript-0") {
            webViewUtils.waitForElementVisible("#StartScript-0")
            webViewUtils.rightClickElement("#StartScript-0")
        }

        assertEquals(
            true, webViewUtils.isElementVisibleByString(
                ".blocklyMenuItemContent",
                context.getString(R.string.brick_context_dialog_comment_out_script)
            )
        )

        // Disable script
        webViewUtils.clickElementByString(
            ".blocklyMenuItemContent",
            context.getString(R.string.brick_context_dialog_comment_out_script)
        )

        webViewUtils.waitForElement("#StartScript-0") {
            webViewUtils.rightClickElement("#StartScript-0")
        }

        assertEquals(
            true, webViewUtils.isElementVisibleByString(
                ".blocklyMenuItemContent",
                context.getString(
                    R.string
                        .brick_context_dialog_comment_in_script
                )
            )
        )

        // Enable script again
        webViewUtils.clickElementByString(
            ".blocklyMenuItemContent", context.getString(
                R.string
                    .brick_context_dialog_comment_in_script
            )
        )
        webViewUtils.waitForElement("#StartScript-0") {
            webViewUtils.rightClickElement("#StartScript-0")
        }

        assertEquals(
            true, webViewUtils.isElementVisibleByString(
                ".blocklyMenuItemContent",
                context.getString(R.string.brick_context_dialog_comment_out_script)
            )
        )
    }

    @Test
    fun testCommentOutScript() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        val script = UiTestCatroidApplication.projectManager.currentProject.defaultScene
            .getSprite(TEST_SPRITE).getScript(0)

        assertEquals(false, script.isCommentedOut)
        for (brick in script.brickList) {
            assertIfInnerPartIsCommentedOutOrIn(false, brick)
        }

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#StartScript-0") {
            webViewUtils.waitForElementVisible("#StartScript-0")
            webViewUtils.rightClickElement("#StartScript-0")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_out_script
                )
            )
        }
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        assertEquals(true, script.isCommentedOut)
        for (brick in script.brickList) {
            assertIfInnerPartIsCommentedOutOrIn(true, brick)
        }
    }

    @Test
    fun testCommentInScript() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        val script = UiTestCatroidApplication.projectManager.currentProject.defaultScene
            .getSprite(TEST_SPRITE).getScript(0)

        assertEquals(false, script.isCommentedOut)
        for (brick in script.brickList) {
            assertIfInnerPartIsCommentedOutOrIn(false, brick)
        }

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#StartScript-0") {
            webViewUtils.waitForElementVisible("#StartScript-0")
            webViewUtils.rightClickElement("#StartScript-0")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_out_script
                )
            )
            webViewUtils.waitForElementVisible("#StartScript-0")
            webViewUtils.rightClickElement("#StartScript-0")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_in_script
                )
            )
        }

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        assertEquals(false, script.isCommentedOut)
        for (brick in script.brickList) {
            assertIfInnerPartIsCommentedOutOrIn(false, brick)
        }
    }

    @Test
    fun testCommentOutIfBrick() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        val script = UiTestCatroidApplication.projectManager.currentProject.defaultScene
            .getSprite(TEST_SPRITE).getScript(0)

        assertIfInnerPartIsCommentedOutOrIn(false, script.brickList[0])

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#IfLogicBeginBrick-1") {
            webViewUtils.waitForElementVisible("#IfLogicBeginBrick-1")
            webViewUtils.rightClickElement("#IfLogicBeginBrick-1")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_out
                )
            )
        }
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        assertIfInnerPartIsCommentedOutOrIn(true, script.brickList[0])
    }

    @Test
    fun testCommentInIfBrick() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        val script = UiTestCatroidApplication.projectManager.currentProject.defaultScene
            .getSprite(TEST_SPRITE).getScript(0)

        assertIfInnerPartIsCommentedOutOrIn(false, script.brickList[0])

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#IfLogicBeginBrick-1") {
            webViewUtils.waitForElementVisible("#IfLogicBeginBrick-1")
            webViewUtils.rightClickElement("#IfLogicBeginBrick-1")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_out
                )
            )
            webViewUtils.rightClickElement("#IfLogicBeginBrick-1")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_in
                )
            )
        }
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        assertIfInnerPartIsCommentedOutOrIn(false, script.brickList[0])
    }

    @Test
    fun testCommentOutSingleBrick() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        val script = UiTestCatroidApplication.projectManager.currentProject.defaultScene
            .getSprite(TEST_SPRITE).getScript(0)

        assertIfInnerPartIsCommentedOutOrIn(false, script.brickList[2])

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#SetXBrick-0") {
            webViewUtils.waitForElementVisible("#SetXBrick-0")
            webViewUtils.rightClickElement("#SetXBrick-0")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_out
                )
            )
        }
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        assertIfInnerPartIsCommentedOutOrIn(true, script.brickList[2])
    }

    @Test
    fun testCommentInSingleBrick() {
        val context = UiTestCatroidApplication.projectManager.applicationContext
        val script = UiTestCatroidApplication.projectManager.currentProject.defaultScene
            .getSprite(TEST_SPRITE).getScript(0)

        assertIfInnerPartIsCommentedOutOrIn(false, script.brickList[3])

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#GoToBrick-0") {
            webViewUtils.waitForElementVisible("#GoToBrick-0")
            webViewUtils.rightClickElement("#GoToBrick-0")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_out
                )
            )
            webViewUtils.rightClickElement("#GoToBrick-0")
            webViewUtils.clickElementByString(
                ".blocklyMenuItemContent", context.getString(
                    R.string
                        .brick_context_dialog_comment_in
                )
            )
        }
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        assertIfInnerPartIsCommentedOutOrIn(false, script.brickList[3])
    }

    private fun createProject() {
        val projectName = javaClass.simpleName
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        val startScript: Script = StartScript()
        val ifBrick = IfLogicBeginBrick()
        val loopBrick = ForeverBrick()
        loopBrick.addBrick(SetXBrick())
        ifBrick.addBrickToIfBranch(loopBrick)
        ifBrick.addBrickToIfBranch(SetXBrick())
        ifBrick.addBrickToElseBranch(ChangeXByNBrick())
        ifBrick.addBrickToElseBranch(loopBrick)
        loopBrick.addBrick(ifBrick.clone())
        startScript.addBrick(ifBrick)
        startScript.addBrick(loopBrick)
        startScript.addBrick(SetXBrick())
        startScript.addBrick(GoToBrick())
        startScript.setParents()
        sprite.addScript(startScript)
        UiTestCatroidApplication.projectManager.currentProject = project
        UiTestCatroidApplication.projectManager.currentSprite = sprite
    }

    private fun assertIfInnerPartIsCommentedOutOrIn(outIn: Boolean, brick: Brick) {
        assertEquals(outIn, brick.isCommentedOut)

        when (brick) {
            is IfLogicBeginBrick -> {
                // if-branch
                for (b in brick.nestedBricks)
                    assertIfInnerPartIsCommentedOutOrIn(outIn, b)
                // else-branch
                for (b in brick.allParts[1].dragAndDropTargetList)
                    assertIfInnerPartIsCommentedOutOrIn(outIn, b)
            }
            is ForeverBrick ->
                for (b in brick.nestedBricks)
                    assertIfInnerPartIsCommentedOutOrIn(outIn, b)
        }
    }
}
