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
package org.catrobat.catroid.test.content.bricks

import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.ui.ProjectActivity
import org.junit.Before
import android.content.Intent
import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.action.ViewActions
import org.catrobat.catroid.R
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.WaitForConditionAction
import androidx.test.espresso.assertion.ViewAssertions
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.common.FlavoredConstants
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.IOException

class RecentBricksTest {
    private val projectName = "recentBrickTest"
    private val spriteName = "testSprite"

    @Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java,
        ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity(Intent())
    }

    @Test
    fun testCheckRecentBrickVisible() {
        Espresso.onView(ViewMatchers.withText(spriteName)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.category_control))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.brick_forever))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withId(android.R.id.list)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.category_recently_used))
            .perform(ViewActions.click())
        BrickDataInteractionWrapper.onBrickAtPosition(1).checkShowsText(R.string.brick_forever)
    }

    @Test
    fun testCheckNonBackgroundBricksAreHiddenForBackgroundSprites() {
        Espresso.onView(ViewMatchers.withText(spriteName)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.category_motion))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.brick_if_on_edge_bounce))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withText(R.string.brick_if_on_edge_bounce))
            .perform(waitFor(ViewMatchers.isDisplayed(), 2000), ViewActions.click())
        Espresso.onView(ViewMatchers.withId(android.R.id.list)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.category_recently_used))
            .perform(ViewActions.click())
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .checkShowsText(R.string.brick_if_on_edge_bounce)
        Espresso.pressBack()
        Espresso.pressBack()
        Espresso.pressBack()
        Espresso.onView(ViewMatchers.withText(R.string.background)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.category_recently_used))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.brick_if_on_edge_bounce))
            .check(ViewAssertions.doesNotExist())
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        try {
            StorageOperations.deleteDir(File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName))
        } catch (e: IOException) {
            Log.d(javaClass.simpleName, "Cannot delete test project in tear down.")
        }
    }

    fun createProject(): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite(spriteName)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        return project
    }
}