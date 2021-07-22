/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.catrobat.catroid.utils.Utils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class BackPressedTest {
    var project: Project? = null
    var defaultProjectName = "defaultProject"

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        project = Project(
            ApplicationProvider.getApplicationContext(),
            Utils.sanitizeFileName(defaultProjectName)
        )
        XstreamSerializer.getInstance().saveProject(project)
        val sprite = Sprite("testSprite")
        project?.defaultScene?.addSprite(sprite)
        baseActivityTestRule.launchActivity(null)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(project?.name)
        baseActivityTestRule.activity.finish()
    }

    @Test
    fun pressBackTest() {
        Espresso.onView(ViewMatchers.withText(defaultProjectName)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val appCompatImageButton = Espresso.onView(childAtPosition(Matchers.allOf(ViewMatchers.withId(R.id.toolbar), childAtPosition(ViewMatchers.withClassName(Matchers.`is`("android.widget.LinearLayout")), 0)), 1))
        appCompatImageButton.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        appCompatImageButton.perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(defaultProjectName)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent) && view == parent.getChildAt(position)
            }
        }
    }
}
