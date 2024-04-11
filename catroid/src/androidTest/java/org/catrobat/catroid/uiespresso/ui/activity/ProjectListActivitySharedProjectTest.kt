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

import android.app.Activity
import android.app.ActivityManager
import android.content.ContentValues
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.openActionBarMenu
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.JELLY_BEAN_MR2)
class ProjectListActivitySharedProjectTest {

    @get:Rule
    var activityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java,
        false,
        false
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    @Test
    fun launchProjectListActivityWithIntentForEmptySharedProjectTest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = createUriEntry()

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            activityTestRule.launchActivity(intent)

            onView(withText(R.string.project_list_title))
                .check(matches(isDisplayed()))

            onView(withText(R.string.error_import_project))
                .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
                .check(matches(isDisplayed()))

            deleteUriEntry()
        } else {
            return
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Test
    fun launchMainMenuActivityWithSharedProjectTest() {
        val device = UiDevice.getInstance(
            InstrumentationRegistry.getInstrumentation()
        )
        activityTestRule.launchActivity(Intent())
        waitUntilActivityIsVisible<ProjectListActivity>()

        onView(withText(R.string.project_list_title))
            .check(matches(isDisplayed()))
        onView(withText(TEST_SHARED_PROJECT))
            .check(matches(isDisplayed()))
            .perform(click())

        waitUntilActivityIsVisible<ProjectActivity>()
        openActionBarMenu()
        onView(withText(R.string.project_options))
            .check(matches(isDisplayed()))
        onView(withText(R.string.project_options))
            .perform(click())
        onView(withId(R.id.project_options_layout))
            .check(matches(isDisplayed()))
        onView(withText(R.string.export_project))
            .perform(click())

        assertTrue(saveProjectOnDevice(device))

        onView(withText(R.string.project_options))
            .check(matches(isDisplayed()))

        assertTrue(shareProjectFromDevice(device))

        waitUntilActivityIsVisible<MainMenuActivity>()
        onView(withText(R.string.app_name))
            .check(matches(isDisplayed()))

        onView(withText(R.string.main_menu_programs))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText("$TEST_SHARED_PROJECT (1)"))
            .check(matches(isDisplayed()))
            .perform(click())

        openActionBarMenu()
        onView(withText(R.string.project_options))
            .perform(click())
        onView(withId(R.id.project_options_layout))
            .check(matches(isDisplayed()))

        onView(withText("DELETE PROJECT"))
            .perform(scrollTo(), click())

        onView(withText("YES")).perform(click())

        assertTrue(deleteProjectFromDevice(device))

        device.pressBack()
        device.pressBack()

        activityTestRule.finishActivity()
    }

    private inline fun <reified T : Activity> waitUntilActivityIsVisible() {
        val startTime = System.currentTimeMillis()
        while (!isVisible<T>()) {
            Thread.sleep(THREAD_SLEEP)
            if (System.currentTimeMillis() - startTime >= WAIT_FOR_ACTIVITY_TIMEOUT) {
                throw AssertionError("Activity ${T::class.java.simpleName} not visible")
            }
        }
    }

    private inline fun <reified T : Activity> isVisible(): Boolean {
        val activityManager = ApplicationProvider
            .getApplicationContext<Context>()
            .getSystemService(ACTIVITY_SERVICE) as ActivityManager

        val topActivity = activityManager.appTasks[0].taskInfo.topActivity?.className
        Log.d(TAG, topActivity.toString())
        val targetActivity = T::class.java.name
        return topActivity == targetActivity
    }

    private fun deleteProjectFromDevice(device: UiDevice): Boolean {
        device.pressHome()

        val maxY = device.displayHeight
        val maxX = device.displayWidth

        device.swipe(maxX / 2, maxY, maxX / 2, 0, SWIPE_STEPS)

        val filesButton = device.findObject(
            UiSelector().text(FILES_BUTTON).className(TEXT_VIEW)
        )

        if (!filesButton.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        filesButton.click()

        var project = device.findObject(
            UiSelector().text("$TEST_SHARED_PROJECT.catrobat")
        )
        if (!project.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        project.click()

        val navBarMenu = device.findObject(UiSelector().clickable(true))
        if (!navBarMenu.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        assertEquals("android.widget.ImageButton", navBarMenu.className)
        navBarMenu.click()

        val downloads = device.findObject(
            UiSelector().text("Downloads")
        )
        if (!downloads.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        downloads.click()

        project = device.findObject(
            UiSelector().text("$TEST_SHARED_PROJECT.catrobat")
        )
        if (!project.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        project.longClick()

        val deleteButton = device.findObject(
            UiSelector().descriptionContains("Delete").className(TEXT_VIEW)
        )
        if (!deleteButton.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        deleteButton.click()

        val okButton = device.findObject(
            UiSelector().text("OK")
        )
        if (!okButton.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        okButton.click()

        return true
    }

    private fun saveProjectOnDevice(device: UiDevice): Boolean {
        val saveButton = device.findObject(
            UiSelector()
                .textContains("SAVE")
                .className("android.widget.Button")
        )
        if (!saveButton.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        saveButton.click()
        return true
    }

    private fun shareProjectFromDevice(device: UiDevice): Boolean {
        device.pressHome()

        val maxY = device.displayHeight
        val maxX = device.displayWidth

        device.swipe(maxX / 2, maxY, maxX / 2, 0, SWIPE_STEPS)

        val filesButton = device.findObject(
            UiSelector()
                .text(FILES_BUTTON)
                .className(TEXT_VIEW)
        )
        if (!filesButton.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        filesButton.click()

        val project = device.findObject(
            UiSelector().text("$TEST_SHARED_PROJECT.catrobat")
        )
        if (!project.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        project.longClick()

        val shareButton = device.findObject(
            UiSelector()
                .descriptionContains("Share")
                .className(TEXT_VIEW)
        )
        if (!shareButton.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        shareButton.click()

        device.swipe(maxX / 2, maxY, maxX / 2, 0, SWIPE_STEPS)

        val pocketCodeButton = device.findObject(
            UiSelector()
                .text("Pocket Code")
                .className(TEXT_VIEW)
        )
        if (!pocketCodeButton.waitForExists(WAIT_UI_OBJECT_EXISTS_TIMEOUT)) return false
        pocketCodeButton.click()

        return true
    }

    private fun createUriEntry(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, EMPTY_SHARED_PROJECT)
            put(MediaStore.MediaColumns.MIME_TYPE, "*/*")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        val outputStream = uri?.let {
            resolver.openOutputStream(it)
        }

        assertNotNull(uri)
        assertNotNull(outputStream)

        return uri
    }

    private fun deleteUriEntry() {
        val selectionClause = MediaStore.MediaColumns.DISPLAY_NAME + " LIKE ?"
        val selectionArgs = arrayOf(EMPTY_SHARED_PROJECT)

        val resolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
        val rowsDeleted = resolver.delete(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            selectionClause,
            selectionArgs
        )
        assertEquals(1, rowsDeleted)
    }

    companion object {
        val TAG: String = ProjectListActivitySharedProjectTest::class.java.simpleName
        const val EMPTY_SHARED_PROJECT: String = "testEmptyProject.catrobat"
        const val TEST_SHARED_PROJECT: String = "My project"
        const val TEXT_VIEW: String = "android.widget.TextView"
        const val FILES_BUTTON: String = "Files"
        const val SWIPE_STEPS = 15
        const val THREAD_SLEEP = 200L
        const val WAIT_FOR_ACTIVITY_TIMEOUT = 10_000L
        const val WAIT_UI_OBJECT_EXISTS_TIMEOUT = 1000L
    }
}
