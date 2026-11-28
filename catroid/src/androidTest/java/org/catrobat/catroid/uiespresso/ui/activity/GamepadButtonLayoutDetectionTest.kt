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

import android.os.SystemClock
import android.preference.PreferenceManager
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.PreferenceMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.ui.settingsfragments.GamepadSettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.GAMEPAD_SCREEN_KEY
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The detection of the button layout in the gamepad settings, with controller keys injected
 * through the system like a real controller.
 */
@RunWith(AndroidJUnit4::class)
class GamepadButtonLayoutDetectionTest {

    @get:Rule
    val baseActivityTestRule = BaseActivityTestRule(SettingsActivity::class.java, true, false)

    private val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
    private var initialSettings: Map<String, Boolean?> = emptyMap()

    @Before
    fun setUp() {
        initialSettings = SETTINGS.associateWith { key ->
            if (sharedPreferences.contains(key)) sharedPreferences.getBoolean(key, false) else null
        }
        sharedPreferences.edit()
            .putBoolean(SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, true)
            .putBoolean(SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS, false)
            .commit()
    }

    @After
    fun tearDown() {
        val editor = sharedPreferences.edit()
        initialSettings.forEach { (key, value) ->
            if (value == null) editor.remove(key) else editor.putBoolean(key, value)
        }
        editor.commit()
    }

    @Test
    fun reportedBForTheButtonLabeledATurnsTheSwapOn() {
        openDetection()

        pressKey(KeyEvent.KEYCODE_BUTTON_B)

        assertDetectionClosedWithSwap(true)
    }

    @Test
    fun reportedAForTheButtonLabeledATurnsTheSwapOff() {
        sharedPreferences.edit().putBoolean(SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS, true).commit()
        openDetection()

        pressKey(KeyEvent.KEYCODE_BUTTON_A)

        assertDetectionClosedWithSwap(false)
    }

    @Test
    fun otherButtonsKeepTheDetectionOpenUntilItIsCanceled() {
        openDetection()

        OTHER_BUTTON_KEY_CODES.forEach(::pressKey)

        onView(withText(R.string.dialog_gamepad_detect_face_buttons)).inRoot(isDialog())
            .check(matches(isDisplayed()))
        assertFalse(isSwapEnabled())

        onView(withText(R.string.cancel)).inRoot(isDialog()).perform(click())

        onView(withText(R.string.dialog_gamepad_detect_face_buttons)).check(doesNotExist())
        assertFalse(isSwapEnabled())
    }

    @Test
    fun keyLinkerHintIsShownInTheGamepadSettings() {
        openGamepadSettings()

        onData(PreferenceMatchers.withKey(GamepadSettingsFragment.KEYLINKER_HINT_KEY))
            .check(matches(isDisplayed()))
        onView(withText(R.string.preference_description_gamepad_keylinker)).check(matches(isDisplayed()))
    }

    private fun openGamepadSettings() {
        baseActivityTestRule.launchActivity(null)
        onData(PreferenceMatchers.withKey(GAMEPAD_SCREEN_KEY)).perform(click())
    }

    private fun openDetection() {
        openGamepadSettings()
        onData(PreferenceMatchers.withKey(GamepadSettingsFragment.DETECT_FACE_BUTTONS_KEY)).perform(click())
        onView(withText(R.string.dialog_gamepad_detect_face_buttons)).inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    private fun assertDetectionClosedWithSwap(swapped: Boolean) {
        onView(withText(R.string.dialog_gamepad_detect_face_buttons)).check(doesNotExist())
        onData(PreferenceMatchers.withKey(SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS))
            .onChildView(withId(android.R.id.checkbox))
            .check(matches(if (swapped) isChecked() else isNotChecked()))
        assertEquals(swapped, isSwapEnabled())
    }

    private fun isSwapEnabled(): Boolean = sharedPreferences.getBoolean(SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS, false)

    /** Injects a press and release of a controller key through the system, like a real controller. */
    private fun pressKey(keyCode: Int) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.sendKeySync(keyEvent(KeyEvent.ACTION_DOWN, keyCode))
        instrumentation.sendKeySync(keyEvent(KeyEvent.ACTION_UP, keyCode))
        instrumentation.waitForIdleSync()
    }

    private fun keyEvent(action: Int, keyCode: Int): KeyEvent {
        val now = SystemClock.uptimeMillis()
        return KeyEvent(
            now, now, action, keyCode, NO_REPEAT, NO_META_STATE,
            KeyCharacterMap.VIRTUAL_KEYBOARD, NO_SCAN_CODE, NO_FLAGS, InputDevice.SOURCE_GAMEPAD
        )
    }

    private companion object {
        const val NO_REPEAT = 0
        const val NO_META_STATE = 0
        const val NO_SCAN_CODE = 0
        const val NO_FLAGS = 0

        val SETTINGS = listOf(SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS)

        val OTHER_BUTTON_KEY_CODES = listOf(
            KeyEvent.KEYCODE_BUTTON_X,
            KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_BUTTON_START,
            KeyEvent.KEYCODE_BUTTON_L1
        )
    }
}
