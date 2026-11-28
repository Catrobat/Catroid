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

package org.catrobat.catroid.test.stage.gamepad

import android.content.Context
import android.os.Build
import android.view.InputDevice
import android.view.KeyEvent
import org.catrobat.catroid.stage.gamepad.GamepadFaceButtonLayout
import org.catrobat.catroid.stage.gamepad.GamepadFaceButtonLayout.AS_LABELED
import org.catrobat.catroid.stage.gamepad.GamepadFaceButtonLayout.SWAPPED
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GamepadFaceButtonLayoutTest {
    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun `release of a reported A shows that the controller reports the buttons as labeled`() {
        GAME_CONTROLLER_SOURCES.forEach { source ->
            assertEquals(AS_LABELED, detectedBy(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A, source))
        }
    }

    @Test
    fun `release of a reported B shows that the controller reports A and B the other way round`() {
        GAME_CONTROLLER_SOURCES.forEach { source ->
            assertEquals(SWAPPED, detectedBy(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B, source))
        }
    }

    @Test
    fun `press of A or B shows no layout before its release`() {
        listOf(KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B).forEach { keyCode ->
            assertNull(detectedBy(KeyEvent.ACTION_DOWN, keyCode))
        }
    }

    @Test
    fun `other controller buttons show no layout`() {
        OTHER_BUTTON_KEY_CODES.forEach { keyCode ->
            assertNull(detectedBy(KeyEvent.ACTION_UP, keyCode))
        }
    }

    @Test
    fun `A and B keys that do not come from a game controller show no layout`() {
        listOf(KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B).forEach { keyCode ->
            assertNull(detectedBy(KeyEvent.ACTION_UP, keyCode, InputDevice.SOURCE_KEYBOARD))
        }
    }

    @Test
    fun `only the layout with A and B the other way round swaps the face buttons`() {
        assertFalse(AS_LABELED.swapsFaceButtons)
        assertTrue(SWAPPED.swapsFaceButtons)
    }

    @Test
    fun `each layout tells what was detected`() {
        assertEquals(
            "A is reported as A, so A/B and X/Y are not swapped",
            context.getString(AS_LABELED.detectedMessage)
        )
        assertEquals(
            "A is reported as B, so A/B and X/Y are swapped",
            context.getString(SWAPPED.detectedMessage)
        )
    }

    private fun detectedBy(
        action: Int,
        keyCode: Int,
        source: Int = InputDevice.SOURCE_GAMEPAD
    ): GamepadFaceButtonLayout? =
        GamepadFaceButtonLayout.detectedBy(
            KeyEvent(0L, 0L, action, keyCode, 0, 0, GAMEPAD_DEVICE_ID, 0, 0, source)
        )

    private companion object {
        const val GAMEPAD_DEVICE_ID = 11

        val GAME_CONTROLLER_SOURCES = listOf(InputDevice.SOURCE_GAMEPAD, InputDevice.SOURCE_JOYSTICK)

        val OTHER_BUTTON_KEY_CODES = listOf(
            KeyEvent.KEYCODE_BUTTON_X,
            KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_BUTTON_START,
            KeyEvent.KEYCODE_BUTTON_SELECT,
            KeyEvent.KEYCODE_BUTTON_L1,
            KeyEvent.KEYCODE_BUTTON_R2
        )
    }
}
