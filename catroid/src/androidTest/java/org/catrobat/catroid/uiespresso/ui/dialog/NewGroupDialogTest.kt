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

package org.catrobat.catroid.uiespresso.ui.dialog

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent

@RunWith(AndroidJUnit4::class)
class NewGroupDialogTest {

    private lateinit var project: Project
    private lateinit var scene: Scene
    private val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        project = Project(ApplicationProvider.getApplicationContext(), NewGroupDialogTest::class.java.simpleName)

        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene

        scene = project.defaultScene
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(NewGroupDialogTest::class.java.simpleName)
    }

    @Test
    fun testIfNewGroupDialogIsDisplayed() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.new_group)).perform(click())

        onView(withText(R.string.new_group)).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNewGroupDialogCreatesNewGroupWithDefaultGroupName() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.new_group)).perform(click())

        onView(withText(R.string.new_group)).inRoot(isDialog()).check(matches(isDisplayed()))

        closeSoftKeyboard()

        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .inRoot(isDialog())
            .perform(click())

        onView(withText(R.string.default_group_name)).check(matches(isDisplayed()))
    }

    @Test
    fun testNewGroupDialogCreatesNewGroupWithCustomGroupName() {
        val customGroupName = "TestGroup123"
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.new_group)).perform(click())

        onView(withText(R.string.new_group)).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withText(R.string.default_group_name))
            .inRoot(isDialog())
            .perform(typeTextIntoFocusedView(customGroupName))

        closeSoftKeyboard()

        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .inRoot(isDialog())
            .perform(click())

        onView(withText(customGroupName)).check(matches(isDisplayed()))
    }

    @Test
    fun testNewGroupDialogCancelButton() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.new_group)).perform(click())

        onView(withText(R.string.new_group)).inRoot(isDialog()).check(matches(isDisplayed()))

        closeSoftKeyboard()

        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .inRoot(isDialog())
            .perform(click())

        onView(withText(R.string.default_group_name)).check(doesNotExist())
    }
}
