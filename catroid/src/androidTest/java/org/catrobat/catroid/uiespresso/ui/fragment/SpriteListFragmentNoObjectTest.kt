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
package org.catrobat.catroid.uiespresso.ui.fragment

import android.app.Activity.RESULT_OK
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.core.AllOf.allOf
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import org.hamcrest.core.IsNot.not
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.SharedPreferenceKeys.NEW_SPRITE_VISUAL_PLACEMENT_KEY
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectActivity.Companion.EXTRA_FRAGMENT_POSITION
import org.catrobat.catroid.ui.ProjectActivity.Companion.FRAGMENT_SPRITES
import org.catrobat.catroid.ui.ProjectActivity.Companion.SPRITE_CAMERA
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent
import android.view.View
import androidx.test.espresso.ViewAssertion
import org.hamcrest.Matcher

@RunWith(AndroidJUnit4::class)
class SpriteListFragmentNoObjectTest {
    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)
    private val waitThreshold: Long = 3000

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java,
        EXTRA_FRAGMENT_POSITION,
        FRAGMENT_SPRITES
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createNoObjectsProject()
        baseActivityTestRule.launchActivity()
    }

    private fun assertNotDisplayedOrDoesNotExist(viewMatcher: Matcher<View>) {
        onView(viewMatcher).check(ViewAssertion { view, noViewFoundException ->
            if (noViewFoundException != null) return@ViewAssertion

            if (view != null && isDisplayed().matches(view)) {
                throw AssertionError("Expected view to be NOT displayed (or not exist), but it *is* displayed: $viewMatcher")
            }
        })
    }


    @Test
    fun testEmptyViewOnStart() {
        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))

        onView(withText(R.string.fragment_sprite_text_description))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testOverflowItemsWithNoObjects() {
        assertOverflowItemDoesNotLeaveEmptyState(R.string.backpack)
        assertOverflowItemDoesNotLeaveEmptyState(R.string.copy)
        assertOverflowItemDoesNotLeaveEmptyState(R.string.delete)
        assertOverflowItemDoesNotLeaveEmptyState(R.string.rename)
    }

    private fun assertOverflowItemDoesNotLeaveEmptyState(overflowItem: Int) {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)
        onView(withText(overflowItem)).perform(click())

        // Still on the empty sprites screen
        onView(withId(R.id.empty_view)).check(matches(isDisplayed()))
        onView(withText(R.string.fragment_sprite_text_description)).check(matches(isDisplayed()))

        // And the delete confirmation dialog is NOT shown (important after your new feature)
        onView(withId(R.id.deleteDialogTitle)).check(doesNotExist())
    }

    @Test
    fun testAddSprite() {
        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))

        getDefaultSharedPreferences(getApplicationContext())
            .edit()
            .putBoolean(NEW_SPRITE_VISUAL_PLACEMENT_KEY, false)
            .commit()

        onView(withText(R.string.fragment_sprite_text_description))
            .check(matches(isDisplayed()))

        getInstrumentation().runOnMainSync {
            baseActivityTestRule.activity.onActivityResult(SPRITE_CAMERA, RESULT_OK, null)
        }

        closeSoftKeyboard()

        // If an OK dialog appears, click it. If it doesn't, continue.
        val okButtonMatcher = allOf(withId(android.R.id.button1), withText(R.string.ok))
        try {
            onView(isRoot()).perform(waitFor(okButtonMatcher, waitThreshold))
            onView(okButtonMatcher).perform(click())
        } catch (_: Throwable) {
            println("A Throwable was caught, maybe the dialog didn't show.")
        }

        assertNotDisplayedOrDoesNotExist(withId(R.id.empty_view))
        assertNotDisplayedOrDoesNotExist(withText(R.string.fragment_sprite_text_description))

        getDefaultSharedPreferences(getApplicationContext())
            .edit()
            .remove(NEW_SPRITE_VISUAL_PLACEMENT_KEY)
            .apply()
    }

    private fun createNoObjectsProject() {
        val project = Project(getApplicationContext(), SpriteListFragmentNoObjectTest::class.java.simpleName)
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
    }

}
