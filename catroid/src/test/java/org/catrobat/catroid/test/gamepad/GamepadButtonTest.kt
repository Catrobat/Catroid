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

package org.catrobat.catroid.test.gamepad

import org.catrobat.catroid.gamepad.GamepadButton
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GamepadButtonTest {

    @Test
    fun `buttons are declared with stable names in the specified order`() {
        val expectedIds = listOf(
            "A", "B", "X", "Y",
            "UP", "DOWN", "LEFT", "RIGHT",
            "START", "SELECT",
            "L1", "L2", "R1", "R2"
        )

        assertEquals(expectedIds, GamepadButton.entries.map { it.name })
    }

    @Test
    fun `fromId returns the button for its exact id`() {
        GamepadButton.entries.forEach { button ->
            assertEquals(button, GamepadButton.fromId(button.name))
        }
    }

    @Test
    fun `fromId returns null for null`() {
        assertNull(GamepadButton.fromId(null))
    }

    @Test
    fun `fromId returns null for unknown ids`() {
        listOf("", " ", "Z", "L3", "BUTTON_A", "GAMEPAD_A_PRESSED", "Up").forEach { id ->
            assertNull("Unexpected button for id '$id'", GamepadButton.fromId(id))
        }
    }

    @Test
    fun `fromId is case sensitive`() {
        listOf("a", "b", "up", "start", "select", "l1", "r2").forEach { id ->
            assertNull("Unexpected button for id '$id'", GamepadButton.fromId(id))
        }
    }

    @Test
    fun `fromId does not trim whitespace`() {
        listOf(" A", "A ", "\tSTART", "R1\n").forEach { id ->
            assertNull("Unexpected button for id '$id'", GamepadButton.fromId(id))
        }
    }
}
