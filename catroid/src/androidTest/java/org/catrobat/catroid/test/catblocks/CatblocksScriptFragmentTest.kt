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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
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
class CatblocksScriptFragmentTest {
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
    fun testContextMenuItems() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks_reorder)).check(doesNotExist())
        onView(withText(R.string.catblocks)).perform(click())
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks_reorder))
            .check(matches(isDisplayed()))
        onView(withText(R.string.undo)).check(doesNotExist())
        onView(withText(R.string.backpack)).check(doesNotExist())
        onView(withText(R.string.copy)).check(doesNotExist())
        onView(withText(R.string.delete)).check(doesNotExist())
        onView(withText(R.string.rename)).check(doesNotExist())
        onView(withText(R.string.show_details)).check(doesNotExist())
        onView(withText(R.string.comment_in_out)).check(doesNotExist())
        onView(withText(R.string.catblocks)).perform(click())
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks_reorder)).check(doesNotExist())
        onView(withText(R.string.backpack))
            .check(matches(isDisplayed()))
        onView(withText(R.string.copy))
            .check(matches(isDisplayed()))
        onView(withText(R.string.comment_in_out))
            .check(matches(isDisplayed()))
        onView(withText(R.string.catblocks))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testReorderScript() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val webViewUtils = WebViewUtils(baseActivityTestRule.activity)
        webViewUtils.waitForElement("#catroid-catblocks-container") {
            webViewUtils.waitForElement("#catroid-catblocks-container > div > svg > g")
        }

        UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posX = 50f
        UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posY = 50f
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks_reorder)).perform(click())
        assertEquals(UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posX, 0.0f)
        assertEquals(UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posY, 0.0f)
    }

    private fun createProject() {
        val projectName = javaClass.simpleName
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        val startScript: Script = StartScript()
        val ifBrick = IfLogicBeginBrick()
        ifBrick.addBrickToIfBranch(SetXBrick())
        ifBrick.addBrickToElseBranch(ChangeXByNBrick())
        startScript.addBrick(ifBrick)
        startScript.setParents()
        sprite.addScript(startScript)
        UiTestCatroidApplication.projectManager.currentProject = project
        UiTestCatroidApplication.projectManager.currentSprite = sprite
    }
}
