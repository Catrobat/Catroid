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

package org.catrobat.catroid.test.content.eventids

import com.google.common.collect.LinkedHashMultimap
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.content.eventids.GamepadEventId
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadButton.A
import org.catrobat.catroid.gamepad.GamepadButton.B
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GamepadButtonPressedEventIdTest {

    @Test
    fun `event id exposes its button`() {
        assertEquals(B, GamepadButtonPressedEventId(B).button)
    }

    @Test
    fun `event ids of the same button are equal and share a hash code`() {
        GamepadButton.entries.forEach { button ->
            val eventId = GamepadButtonPressedEventId(button)
            val sameEventId = GamepadButtonPressedEventId(button)

            assertEquals(eventId, sameEventId)
            assertEquals(eventId.hashCode(), sameEventId.hashCode())
        }
    }

    @Test
    fun `event ids of different buttons are not equal`() {
        GamepadButton.entries.forEach { button ->
            GamepadButton.entries.filter { it != button }.forEach { otherButton ->
                assertNotEquals(GamepadButtonPressedEventId(button), GamepadButtonPressedEventId(otherButton))
            }
        }
    }

    @Test
    fun `event id is not equal to the legacy cast gamepad event id`() {
        val eventId: EventId = GamepadButtonPressedEventId(A)
        val legacyEventId: EventId = GamepadEventId(A.name)

        assertNotEquals(eventId, legacyEventId)
        assertNotEquals(legacyEventId, eventId)
    }

    @Test
    fun `event id is not equal to a typed event id`() {
        val eventId: EventId = GamepadButtonPressedEventId(A)
        val startEventId = EventId(EventId.START)

        assertNotEquals(eventId, startEventId)
        assertNotEquals(startEventId, eventId)
    }

    @Test
    fun `event id finds only the scripts of its own button in an event map`() {
        val eventMap = LinkedHashMultimap.create<EventId, String>()
        eventMap.put(GamepadButtonPressedEventId(A), "script for A")
        eventMap.put(GamepadButtonPressedEventId(B), "script for B")
        eventMap.put(GamepadEventId(A.name), "legacy script for A")

        assertEquals(setOf("script for A"), eventMap.get(GamepadButtonPressedEventId(A)))
        assertEquals(setOf("script for B"), eventMap.get(GamepadButtonPressedEventId(B)))
    }
}
