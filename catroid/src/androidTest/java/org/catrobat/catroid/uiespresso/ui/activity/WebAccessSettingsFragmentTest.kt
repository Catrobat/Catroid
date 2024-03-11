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

package org.catrobat.catroid.uiespresso.ui.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.openActionBarMenu
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebAccessSettingsFragmentTest {

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SettingsActivity::class.java, true, false
    )

    @Before
    fun setUp() {
        baseActivityTestRule.launchActivity(null)
        onView(withText(R.string.preference_title_web_access)).perform(click())
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun addAndDeleteTrustedDomainTest() {
        onView(withId(R.id.fab_add_trusted_domain)).perform(click())
        onView(withId(R.id.input_edit_text)).perform(typeText("testdomain.net"))
        onView(withText(R.string.ok)).perform(click())
        onView(withText("testdomain.net")).check(matches(isDisplayed()))

        openActionBarMenu()
        onView(withText(R.string.delete)).perform(click())
        onRecyclerView().atPosition(0).performCheckItemClick()
        onView(withId(R.id.menu_done)).perform(click())
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun editTrustedDomainTest() {
        onView(withId(R.id.fab_add_trusted_domain)).perform(click())
        onView(withId(R.id.input_edit_text)).perform(typeText("testdomain1.net"))
        onView(withText(R.string.ok)).perform(click())
        onView(withText("testdomain1.net")).check(matches(isDisplayed()))

        onView(withText("testdomain1.net")).perform(click())
        onView(withId(R.id.input_edit_text)).perform(typeText("testdomain2.net"))
        onView(withText(R.string.ok)).perform(click())
        onView(withText("testdomain2.net")).check(matches(isDisplayed()))

        openActionBarMenu()
        onView(withText(R.string.delete)).perform(click())
        onRecyclerView().atPosition(0).performCheckItemClick()
        onView(withId(R.id.menu_done)).perform(click())
    }
}
