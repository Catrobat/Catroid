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

package org.catrobat.catroid.uiespresso.ui.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import org.catrobat.catroid.R
import org.catrobat.catroid.TrustedDomainManager
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.ui.settingsfragments.webaccess.TrustedDomain
import org.catrobat.catroid.ui.settingsfragments.webaccess.WebAccessSettingsFragment
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewItemMatcher
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.clickOnSettingsItem
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebAccessSettingsFragmentTest {
    private val webAccessItemName = "test.com"

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SettingsActivity::class.java, true, false
    )

    @Before
    fun setup() {
        baseActivityTestRule.launchActivity(null)
        clickOnSettingsItem(R.string.preference_title_web_access)
    }

    @After
    fun teardown() {
        TrustedDomainManager.resetUserTrustList()
    }

    @Test
    fun createWebAccessEntry() {
        addWebAccessItem(webAccessItemName)

        onView(withText(webAccessItemName))
            .check(matches(isDisplayed()))

        assertTrue(TrustedDomainManager.getUserTrustList().contains(webAccessItemName))

        runOnUiThread {
            (baseActivityTestRule.activity.supportFragmentManager.fragments[0] as WebAccessSettingsFragment)
                .deleteItems(listOf(TrustedDomain(webAccessItemName)))
        }
    }

    @Test
    fun createAndDeleteWebAccessEntry() {
        addWebAccessItem(webAccessItemName)

        deleteWebAccessItem(0)

        assertFalse(TrustedDomainManager.getUserTrustList().contains(webAccessItemName))
    }

    @Test
    fun createAndDeleteWebAccessMultipleEntries() {
        addWebAccessItem(webAccessItemName)
        addWebAccessItem("${webAccessItemName}1")
        addWebAccessItem("${webAccessItemName}2")

        assertEquals(3, TrustedDomainManager.getUserTrustList().size)

        deleteWebAccessItem(2)
        assertEquals(2, TrustedDomainManager.getUserTrustList().size)

        deleteWebAccessItem(1)
        assertEquals(1, TrustedDomainManager.getUserTrustList().size)

        deleteWebAccessItem(0)
        assertEquals(0, TrustedDomainManager.getUserTrustList().size)
    }

    private fun addWebAccessItem(name: String) {
        onView(withId(R.id.fab_add_trusted_domain))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(replaceText(name), closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())
    }

    private fun deleteWebAccessItem(position: Int) {
        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.delete))
            .perform(click())

        onView(RecyclerViewItemMatcher(R.id.rv_web_access).withIdInsidePosition(R.id.checkbox, position))
            .perform(click())

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withText(R.string.delete))
            .perform(click())
    }
}
