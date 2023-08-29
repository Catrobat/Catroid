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
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.formulaeditor.Formula
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
class ScriptSplitMultipleTest {
    private val projectName = javaClass.simpleName

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
    fun testSplitOfMultipleScripts() {
        val currentSprite = projectManager.currentProject.defaultScene.getSprite(TEST_SPRITE)
        val scriptCount = currentSprite.scriptList.size
        assertEquals(2, scriptCount)

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val webViewUtils = WebViewUtils(baseActivityTestRule.activity, TIMEOUT)
        webViewUtils.waitForElement("#ChangeVolumeByNBrick-0") {
            webViewUtils.moveElementByPixels("#ChangeVolumeByNBrick-0", 200, 1600)
        }

        webViewUtils.waitForElement("#ChangeSizeByNBrick-0") {
            webViewUtils.moveElementByPixels("#ChangeSizeByNBrick-0", 200, 1900)
        }

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(click())

        val scriptList = currentSprite.scriptList
        assertEquals(4, scriptList.size)

        assertEquals(true, scriptList[0] is StartScript)
        assertEquals(true, scriptList[1] is WhenScript)
        assertEquals(true, scriptList[2] is EmptyScript)
        assertEquals(true, scriptList[3] is EmptyScript)

        val brickListOfFirstNewScript = scriptList[2].brickList
        assertEquals(1, brickListOfFirstNewScript.size)
        assertEquals(true, brickListOfFirstNewScript[0] is ChangeVolumeByNBrick)

        val brickListOfSecondNewScript = scriptList[3].brickList
        assertEquals(1, brickListOfSecondNewScript.size)
        assertEquals(true, brickListOfSecondNewScript[0] is ChangeSizeByNBrick)
    }

    private fun createProject() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite(TEST_SPRITE)
        project.defaultScene.addSprite(sprite)

        val startScript: Script = StartScript()
        val foreverBrick = ForeverBrick()
        foreverBrick.addBrick(ChangeVolumeByNBrick(10.0))
        startScript.addBrick(foreverBrick)
        startScript.setParents()
        sprite.addScript(startScript)

        val whenScript: Script = WhenScript()
        val repeatBrick = RepeatUntilBrick(Formula(10.0))
        repeatBrick.addBrick(ChangeSizeByNBrick(5.0))
        whenScript.addBrick(repeatBrick)
        whenScript.setParents()
        sprite.addScript(whenScript)

        projectManager.currentProject = project
        projectManager.currentSprite = sprite
    }
}
