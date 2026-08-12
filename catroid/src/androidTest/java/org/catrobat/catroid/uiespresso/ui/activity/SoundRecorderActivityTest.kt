/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.R
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SoundRecorderActivityTest {

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SoundRecorderActivity::class.java, false, false
    )

    @get:Rule
    var runtimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    @Before
    fun setUp() {
        baseActivityTestRule.launchActivity(null)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun recordButtonStartsRecordingTest() {
        onView(withId(R.id.soundrecorder_record_button))
            .perform(click())

        onView(withId(R.id.soundrecorder_chronometer_time_recorded))
            .check(matches(isDisplayed()))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun recordThenStopReturnsRecordingTest() {
        onView(withId(R.id.soundrecorder_record_button))
            .perform(click())
        onView(withId(R.id.soundrecorder_record_button))
            .perform(click())

        assertTrue(baseActivityTestRule.activity.isFinishing)
        assertRecordingReturned()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun backWhileIdleFinishesWithoutRecordingTest() {
        val activity = baseActivityTestRule.activity

        pressBackUnconditionally()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertTrue(activity.isFinishing)
        assertEquals(Activity.RESULT_CANCELED, baseActivityTestRule.activityResult.resultCode)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun backWhileRecordingReturnsRecordingTest() {
        val activity = baseActivityTestRule.activity

        onView(withId(R.id.soundrecorder_record_button))
            .perform(click())

        pressBackUnconditionally()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertTrue(activity.isFinishing)
        assertRecordingReturned()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun upButtonWhileRecordingReturnsRecordingTest() {
        onView(withId(R.id.soundrecorder_record_button))
            .perform(click())

        onView(withContentDescription(R.string.abc_action_bar_up_description))
            .perform(click())

        assertTrue(baseActivityTestRule.activity.isFinishing)
        assertRecordingReturned()
    }

    private fun assertRecordingReturned() {
        assertEquals(Activity.RESULT_OK, baseActivityTestRule.activityResult.resultCode)
        val resultIntent = baseActivityTestRule.activityResult.resultData
        assertEquals(Intent.ACTION_PICK, resultIntent.action)
        assertNotNull(resultIntent.data)
    }
}