/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.base.Stopwatch
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.WhenBackgroundChangesScript
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class WhenBackgroundChangesToBrickTest {
    private var brickPosition: Int = 0

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        brickPosition = 0
        UiTestUtils.createProjectWithCustomScript(
            PROJECT_NAME,
            WhenBackgroundChangesScript()
        )
        Intents.init()
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(PROJECT_NAME)
    }

    @Category(Cat.AppUi::class, Level.Smoke::class)
    @Test
    fun testCreateNewBackground() {
        onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_when_background)

        onBrickAtPosition(brickPosition).onSpinner(R.id.brick_when_background_spinner)
            .performSelectNameable(R.string.new_option)

        onView(withId(R.id.dialog_new_look_paintroid))
            .perform(click())

        waitOnViewAndClick(R.id.pocketpaint_btn_skip)

        onView(withId(R.id.pocketpaint_drawing_surface_view))
            .perform(waitFor(isDisplayed(), 3000))
            .perform(click())
        pressBack()

        onView(isRoot()).perform(CustomActions.wait(500))

        val lookDataList: List<LookData> = ProjectManager.getInstance().currentProject
            .defaultScene.backgroundSprite.lookList

        assertEquals(1, lookDataList.size)
    }

    private fun waitOnViewAndClick(viewId: Int, timeout: Int = 1000): Boolean {
        val stopWatch = Stopwatch.createStarted()
        var viewFound: Boolean
        do {
            viewFound = true
            try {
                onView(withId(viewId)).perform(click())
            } catch (e: NoMatchingViewException) {
                viewFound = false
                if (stopWatch.elapsed(TimeUnit.MILLISECONDS) >= timeout.toLong()) {
                    break
                }
            }
        } while (!viewFound)
        return viewFound
    }

    companion object {
        private const val PROJECT_NAME = "WhenBackgroundChangesToBrickTest"
    }
}
