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

package org.catrobat.catroid.test.catblocks

import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.recyclerview.fragment.CatblocksScriptFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class AdvancedModeSettingsTest {

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        CatblocksScriptFragment.testingMode = true
        SettingsFragment.setCatBlocksAdvancedMode(ApplicationProvider.getApplicationContext(),
                                                  true)
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        SettingsFragment.setCatBlocksAdvancedMode(ApplicationProvider.getApplicationContext(), false)
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun testAdvancedModeSetting() {
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity)
        webViewUtils.waitForElement("#catroid-catblocks-container") {
            webViewUtils.waitForElement("#catroid-catblocks-container > div > svg > g")
        }

        UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posX = 50f
        UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posY = 50f
        Assert.assertEquals(
            UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posX,
            50.0f
        )
        Assert.assertEquals(
            UiTestCatroidApplication.projectManager.currentSprite.getScript(0).posY,
            50.0f
        )
    }

    private fun createProject() {
        val projectName = javaClass.simpleName
        val project = UiTestUtils.createDefaultTestProject(projectName)
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