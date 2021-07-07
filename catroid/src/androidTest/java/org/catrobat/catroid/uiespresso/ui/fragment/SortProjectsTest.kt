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

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SortProjectsTest {
    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(PROJECT_B)
        createProject(PROJECT_A)
        createProject(PROJECT_LOWER_B)
        createProject(PROJECT_C)
        defaultSharedPreferences.edit()
            .putBoolean(SharedPreferenceKeys.SORT_PROJECTS_PREFERENCE_KEY, false)
            .apply()
        baseActivityTestRule.launchActivity(null)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        defaultSharedPreferences.edit()
            .remove(SharedPreferenceKeys.SORT_PROJECTS_PREFERENCE_KEY)
            .apply()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun projectsListIsSortedTest() {
        openActionBarOverflowOrOptionsMenu(targetContext)
        onView(withText(R.string.sort_projects))
            .perform(click())
        onRecyclerView()
            .checkHasSortedOrder()

        openActionBarOverflowOrOptionsMenu(targetContext)
        onView(withText(R.string.unsort_projects))
            .perform(click())
    }

    private fun createProject(projectName: String) {
        val project = UiTestUtils.createEmptyProject(projectName)
        XstreamSerializer.getInstance().saveProject(project)
    }

    private val defaultSharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

    companion object {
        private const val PROJECT_A = "A"
        private const val PROJECT_B = "B"
        private const val PROJECT_C = "C"
        private const val PROJECT_LOWER_B = "b"
    }
}
