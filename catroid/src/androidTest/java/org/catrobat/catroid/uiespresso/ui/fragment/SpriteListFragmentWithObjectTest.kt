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
package org.catrobat.catroid.uiespresso.ui.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectActivity.Companion.EXTRA_FRAGMENT_POSITION
import org.catrobat.catroid.ui.ProjectActivity.Companion.FRAGMENT_SPRITES
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpriteListFragmentWithObjectTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java,
        EXTRA_FRAGMENT_POSITION,
        FRAGMENT_SPRITES
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProjectAndGetStartScript(SpriteListFragmentWithObjectTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testExplanationTextIsDisplayedAfterDeleteObjects() {
        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))

        onView(withText(R.string.fragment_sprite_text_description))
            .check(matches(not(isDisplayed())))

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.delete))
            .perform(click())

        onRecyclerView().atPosition(1)
            .performCheckItemClick()

        onView(withId(R.id.confirm))
            .perform(click())

        onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
            .perform(click())

        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))

        onView(withText(R.string.fragment_sprite_text_description))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBackpackInOverflow() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onView(withText(R.string.pack))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCopyInOverflow() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.copy))
            .perform(click())

        onView(withText(R.string.copy))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDeleteInOverflow() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.delete))
            .perform(click())

        onView(withText(R.string.delete))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRenameInOverflow() {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.rename))
            .perform(click())

        onView(withText(R.string.rename_sprite_dialog))
            .check(matches(isDisplayed()))
    }
}
