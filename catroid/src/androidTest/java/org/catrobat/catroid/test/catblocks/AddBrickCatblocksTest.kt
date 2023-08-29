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

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.test.utils.TestUtils
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

@RunWith(AndroidJUnit4::class)
class AddBrickCatblocksTest {
    companion object {
        private const val TIMEOUT: Long = 30
    }

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        CatblocksScriptFragment.testingMode = true
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), true)
        UiTestUtils.createProjectWithOutDefaultScript(TestUtils.DEFAULT_TEST_PROJECT_NAME)
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false)
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun addBricksFromCatblocksView() {
        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement(".blocklyWorkspace")

        val catblocksView = baseActivityTestRule.activity.findViewById<View>(R.id.catblocksWebView)
        val catblocksFragment = FragmentManager.findFragment<Fragment>(catblocksView) as CatblocksScriptFragment
        catblocksFragment.activity?.runOnUiThread {
            catblocksFragment.handleAddButton()
        }

        webViewUtils.waitForElement("#categoryEVENT") {
            webViewUtils.waitForElementVisible("#categoryEVENT")
            webViewUtils.clickElement("#categoryEVENT")
        }

        webViewUtils.waitForElement("#brickStartScript") {
            webViewUtils.isElementVisible("#brickStartScript")
            webViewUtils.waitForElementVisible("#brickStartScript")
            webViewUtils.clickElement("#brickStartScript")
            webViewUtils.waitForElementInvisible("#brickStartScript")
        }

        Assert.assertEquals(UiTestCatroidApplication.projectManager.currentSprite.scriptList.count(), 1)
        val addedScript = UiTestCatroidApplication.projectManager.currentSprite.scriptList.first()
        Assert.assertNotNull(addedScript)
        if (!(addedScript is StartScript)) {
            Assert.fail("Added script is not a StartScript: " + addedScript.javaClass.simpleName)
        }
    }
}
