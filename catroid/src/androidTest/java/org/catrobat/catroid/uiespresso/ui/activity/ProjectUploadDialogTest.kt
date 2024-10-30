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

package org.catrobat.catroid.uiespresso.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.UPLOAD_RESULT_RECEIVER_RESULT_CODE
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.ui.NUMBER_OF_UPLOADED_PROJECTS
import org.catrobat.catroid.ui.PROJECT_DIR
import org.catrobat.catroid.ui.ProjectUploadActivity
import org.catrobat.catroid.ui.controller.ProjectUploadController
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class ProjectUploadDialogTest {
    private lateinit var bundle: Bundle
    private lateinit var project: Project
    private lateinit var sharedPreferences: SharedPreferences
    private val projectName = ProjectUploadDialogTest::class.java.simpleName

    @get:Rule
    val activityTestRule = BaseActivityTestRule(
        ProjectUploadTestActivity::class.java,
        false, false)

    @Before
    fun setUp() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        bundle = Bundle()

        project = UiTestUtils.createDefaultTestProject(projectName)
        val firstScene = Scene("scene", project)
        project.addScene(firstScene)
        saveProjectSerial(project, getApplicationContext())
        val intent = Intent()
        intent.putExtra(PROJECT_DIR, project.directory)
        activityTestRule.launchActivity(intent)
    }

    @Test
    fun testUploadControllerGetsCalled() {
        sharedPreferences.edit()
            .putInt(NUMBER_OF_UPLOADED_PROJECTS, 1)
            .commit()

        onView(withText(R.string.next)).perform(click())
        getInstrumentation().waitForIdleSync()
        onView(withText(R.string.next)).perform(click())
        getInstrumentation().waitForIdleSync()
        onView(withId(android.R.id.button1)).perform(click())
        val projectUploadController = activityTestRule.activity.projectUploadController()

        Looper.myLooper()?.quit()
        verify(projectUploadController!!).startUpload(projectName, "", "", project)
        Mockito.verifyNoMoreInteractions(projectUploadController)
    }

    @Test
    fun uploadSuccessRatingDialogShowing() {
        sharedPreferences.edit()
            .putInt(NUMBER_OF_UPLOADED_PROJECTS, 1)
            .commit()

        onView(withId(R.id.next))
            .perform(click())

        onView(withId(R.id.next))
            .perform(click())

        onView(withText(R.string.next))
            .perform(click())

        getInstrumentation().runOnMainSync {
            activityTestRule.activity.showUploadDialog()
            activityTestRule.activity.onReceiveResult(UPLOAD_RESULT_RECEIVER_RESULT_CODE, bundle)
        }

        onView(withText(R.string.rating_dialog_rate_now))
            .check(matches(isDisplayed()))
    }

    @Test
    fun firstUploadSuccessRatingDialogNotShowing() {
        sharedPreferences.edit()
            .putInt(NUMBER_OF_UPLOADED_PROJECTS, 0)
            .commit()

        onView(withId(R.id.next))
            .perform(click())

        onView(withId(R.id.next))
            .perform(click())

        onView(withText(R.string.next))
            .perform(click())

        getInstrumentation().runOnMainSync {
            activityTestRule.activity.showUploadDialog()
            activityTestRule.activity.onReceiveResult(UPLOAD_RESULT_RECEIVER_RESULT_CODE, bundle)
        }

        onView(withText(R.string.rating_dialog_rate_now))
            .check(doesNotExist())
    }

    @Test
    fun thirdUploadSuccessRatingDialogNotShowing() {
        sharedPreferences.edit()
            .putInt(NUMBER_OF_UPLOADED_PROJECTS, 2)
            .commit()

        onView(withId(R.id.next))
            .perform(click())

        onView(withId(R.id.next))
            .perform(click())

        onView(withText(R.string.next))
            .perform(click())

        getInstrumentation().runOnMainSync {
            activityTestRule.activity.showUploadDialog()
            activityTestRule.activity.onReceiveResult(UPLOAD_RESULT_RECEIVER_RESULT_CODE, bundle)
        }

        onView(withText(R.string.rating_dialog_rate_now))
            .check(doesNotExist())
    }

    @Test
    fun uploadFailRatingDialogNotShowing() {
        sharedPreferences.edit()
            .putInt(NUMBER_OF_UPLOADED_PROJECTS, 1)
            .commit()

        onView(withId(R.id.next))
            .perform(click())

        onView(withId(R.id.next))
            .perform(click())

        onView(withText(R.string.next))
            .perform(click())

        getInstrumentation().runOnMainSync {
            activityTestRule.activity.showUploadDialog()
            activityTestRule.activity.onReceiveResult(0, bundle)
        }

        onView(withText(R.string.rating_dialog_rate_now))
            .check(doesNotExist())
    }

    @Test
    fun testUploadDefaultProjectName() {
        val defaultProjectName = getApplicationContext<Context>().resources.getString(R.string.default_project_name)
        val errorMessage = getApplicationContext<Context>().resources.getString(R.string.error_upload_project_with_default_name, defaultProjectName)

        onView(withId(R.id.project_upload_name))
            .perform(replaceText(defaultProjectName))

        onView(withText(errorMessage))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSoftKeyboardNotShownBehindDialog() {
        sharedPreferences.edit()
            .putInt(NUMBER_OF_UPLOADED_PROJECTS, 1)
            .commit()

        onView(withText(R.string.next))
            .perform(click())
        getInstrumentation().waitForIdleSync()

        onView(withId(R.id.project_notes_and_credits_upload))
            .perform(click())

        onView(withText(R.string.next))
            .perform(click())
        getInstrumentation().waitForIdleSync()

        assertFalse(isKeyboardVisible())
    }

    private fun isKeyboardVisible(): Boolean {
        val checkKeyboardCommand = "dumpsys input_method | grep mInputShown"
        return UiDevice.getInstance(getInstrumentation()).executeShellCommand(checkKeyboardCommand).contains("mInputShown=true")
    }

    class ProjectUploadTestActivity : ProjectUploadActivity() {
        override fun createProjectUploadController(): ProjectUploadController? {
            projectUploadController = spy(ProjectUploadController(this))
            return projectUploadController
        }

        override fun verifyUserIdentity() {
            onTokenCheckComplete(true, false)
        }

        fun projectUploadController(): ProjectUploadController? = projectUploadController
    }
}
