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

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.bricks.BackgroundRequestBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.LookRequestBrick
import org.catrobat.catroid.content.bricks.OpenUrlBrick
import org.catrobat.catroid.content.bricks.StartListeningBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class WarningForWebAccessBricksDownloadedProjectTest{

    private lateinit var project: Project
    private val projectManager by inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val sharedPreferences = projectManager.applicationContext.getSharedPreferences(
            projectManager.applicationContext.getString(R.string.preference_approved_list_file_key),
            MODE_PRIVATE
        )
        sharedPreferences.edit().clear().apply()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        val sharedPreferences = projectManager.applicationContext.getSharedPreferences(
            projectManager.applicationContext.getString(R.string.preference_approved_list_file_key),
            MODE_PRIVATE
        )
        sharedPreferences.edit().clear().apply()
        TestUtils.deleteProjects(project.name)
    }

    private fun createDownloadedProjectWithBrick(name: String, brick: Brick) {
        project = UiTestUtils.createDefaultTestProject(name)
        UiTestUtils.getDefaultTestScript(project).addBrick(brick)
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())
        val intent = Intent()
        project.xmlHeader.remixParentsUrlString = name
        baseActivityTestRule.launchActivity(intent)
    }

    @Test
    fun showWarningForWebRequestBrick() {
        createDownloadedProjectWithBrick("showWarningForWebRequestBrick", WebRequestBrick())
        onView(withText(R.string.security_warning_dialog_msg_web_access))
            .check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
    }

    @Test
    fun showWarningForStartListeningBrick() {
        createDownloadedProjectWithBrick("showWarningForStartListeningBrick", StartListeningBrick())
        onView(withText(R.string.security_warning_dialog_msg_web_access))
            .check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
    }

    @Test
    fun showWarningForLookRequestBrick() {
        createDownloadedProjectWithBrick("showWarningForLookRequestBrick", LookRequestBrick())
        onView(withText(R.string.security_warning_dialog_msg_web_access))
            .check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
    }

    @Test
    fun showWarningForBackgroundRequestBrick() {
        createDownloadedProjectWithBrick("showWarningForBackgroundRequestBrick", BackgroundRequestBrick())
        onView(withText(R.string.security_warning_dialog_msg_web_access))
            .check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
    }

    @Test
    fun showWarningForOpenUrlBrick() {
        createDownloadedProjectWithBrick("showWarningForOpenUrlBrick", OpenUrlBrick())
        onView(withText(R.string.security_warning_dialog_msg_web_access))
            .check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
    }
}
