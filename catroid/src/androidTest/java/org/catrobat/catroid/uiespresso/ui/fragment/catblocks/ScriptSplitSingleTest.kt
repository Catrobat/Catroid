/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.fragment.catblocks

import android.webkit.WebView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import junit.framework.TestCase.assertEquals
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.UiTestCatroidApplication.Companion.projectManager
import org.catrobat.catroid.content.EmptyScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScriptSplitSingleTest {
    private val projectName = javaClass.simpleName

    companion object {
        private const val TIMEOUT: Long = (5 * 1000).toLong()
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
    fun testSplitOfSingleScript() {
        val currentSprite = projectManager.currentProject.defaultScene.getSprite(TEST_SPRITE)
        val scriptCount = currentSprite.scriptList.size
        assertEquals(1, scriptCount)

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(ViewActions.click())

        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.wait(Until.findObject(By.clazz(WebView::class.java)), TIMEOUT)

        val ifBrick = uiDevice.findObject(
            UiSelector().resourceId("IfLogicBeginBrick-0-text")
        )
        assertEquals(true, ifBrick.waitForExists(TIMEOUT))
        ifBrick.dragTo(30, 800, 1)

        openContextualActionModeOverflowMenu()
        onView(withText(R.string.catblocks)).perform(ViewActions.click())

        val scriptList = currentSprite.scriptList
        assertEquals(2, scriptList.size)

        assertEquals(true, scriptList[0] is StartScript)
        assertEquals(true, scriptList[1] is EmptyScript)

        val brickListOfNewScript = scriptList[1].brickList
        assertEquals(1, brickListOfNewScript.size)
        assertEquals(true, brickListOfNewScript[0] is IfLogicBeginBrick)
    }

    private fun createProject() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite(TEST_SPRITE)
        project.defaultScene.addSprite(sprite)

        val startScript: Script = StartScript()
        val ifBrick = IfLogicBeginBrick()
        ifBrick.addBrickToIfBranch(SetXBrick())
        ifBrick.addBrickToElseBranch(ChangeXByNBrick())
        startScript.addBrick(ifBrick)
        startScript.setParents()
        sprite.addScript(startScript)

        projectManager.currentProject = project
        projectManager.currentSprite = sprite
    }
}