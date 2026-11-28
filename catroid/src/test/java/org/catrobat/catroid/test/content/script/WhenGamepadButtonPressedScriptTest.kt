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

package org.catrobat.catroid.test.content.script

import com.google.common.collect.LinkedHashMultimap
import com.thoughtworks.xstream.io.xml.DomDriver
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonPressedBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.content.eventids.GamepadEventId
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.io.XstreamSerializer
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import java.io.StringReader

@RunWith(JUnit4::class)
class WhenGamepadButtonPressedScriptTest {

    private val sprite: Sprite = mock(Sprite::class.java)
    private val xstream = XstreamSerializer.getInstance().xstream

    @Test
    fun `new script reacts to button A`() {
        assertEquals("A", WhenGamepadButtonPressedScript().button)
    }

    @Test
    fun `event id of a known button is the pressed event id of that button`() {
        GamepadButton.entries.forEach { button ->
            val eventId = WhenGamepadButtonPressedScript(button.name).createEventId(sprite)

            assertEquals(button.name, GamepadButtonPressedEventId(button), eventId)
        }
    }

    @Test
    fun `event id follows a changed button`() {
        val script = WhenGamepadButtonPressedScript()

        script.button = GamepadButton.SELECT.name

        assertEquals(GamepadButtonPressedEventId(GamepadButton.SELECT), script.createEventId(sprite))
    }

    @Test
    fun `event id of an unknown button equals no fired event id`() {
        UNKNOWN_BUTTON_IDS.forEach { id ->
            val eventId = WhenGamepadButtonPressedScript(id).createEventId(sprite)

            firedEventIds().forEach { firedEventId ->
                assertNotEquals("$id vs $firedEventId", firedEventId, eventId)
                assertNotEquals("$firedEventId vs $id", eventId, firedEventId)
            }
        }
    }

    @Test
    fun `script of an unknown button is started by no fired event`() {
        val eventMap = LinkedHashMultimap.create<EventId, String>()
        UNKNOWN_BUTTON_IDS.forEach { id ->
            eventMap.put(WhenGamepadButtonPressedScript(id).createEventId(sprite), id)
        }

        firedEventIds().forEach { firedEventId ->
            assertTrue(firedEventId.toString(), eventMap[firedEventId].isEmpty())
        }
    }

    @Test
    fun `script brick is a when gamepad button pressed brick linked to the script`() {
        val script = WhenGamepadButtonPressedScript("X")

        val scriptBrick = script.scriptBrick

        assertTrue(scriptBrick is WhenGamepadButtonPressedBrick)
        assertSame(script, scriptBrick.script)
        assertSame(scriptBrick, script.scriptBrick)
    }

    @Test
    fun `clone keeps the button`() {
        val script = WhenGamepadButtonPressedScript("L2")

        val clone = script.clone() as WhenGamepadButtonPressedScript

        assertNotSame(script, clone)
        assertEquals("L2", clone.button)
        assertEquals(GamepadButtonPressedEventId(GamepadButton.L2), clone.createEventId(sprite))
    }

    @Test
    fun `clone has its own script brick and stays independent of the original`() {
        val script = WhenGamepadButtonPressedScript("L2")
        val originalBrick = script.scriptBrick

        val clone = script.clone() as WhenGamepadButtonPressedScript
        clone.button = "R2"

        assertNotSame(originalBrick, clone.scriptBrick)
        assertSame(clone, clone.scriptBrick.script)
        assertSame(script, originalBrick.script)
        assertEquals("L2", script.button)
    }

    @Test
    fun `clone keeps the bricks and the comment out state`() {
        val script = WhenGamepadButtonPressedScript("UP")
        script.addBrick(SetXBrick(SET_X_VALUE))
        script.isCommentedOut = true

        val clone = script.clone() as WhenGamepadButtonPressedScript

        assertEquals(1, clone.brickList.size)
        assertNotSame(script.brickList[0], clone.brickList[0])
        assertTrue(clone.isCommentedOut)
    }

    @Test
    fun `script requires no resources besides those of its bricks`() {
        val resources = Brick.ResourcesSet()

        WhenGamepadButtonPressedScript("A").addRequiredResources(resources)

        assertTrue(resources.isEmpty())
    }

    @Test
    fun `script is serialized with the script alias and the stable button ID`() {
        val xml = xstream.toXML(WhenGamepadButtonPressedScript("UP"))

        assertThat(xml, startsWith("<script type=\"WhenGamepadButtonPressedScript\""))
        assertThat(xml, containsString("<button>UP</button>"))
        assertThat(xml, not(containsString("org.catrobat.catroid")))
    }

    @Test
    fun `deserialized script keeps its button and bricks`() {
        val script = WhenGamepadButtonPressedScript("UP").apply { addBrick(SetXBrick(SET_X_VALUE)) }

        val deserialized = fromXml(xstream.toXML(script)) as WhenGamepadButtonPressedScript

        assertEquals("UP", deserialized.button)
        assertEquals(GamepadButtonPressedEventId(GamepadButton.UP), deserialized.createEventId(sprite))
        assertTrue(deserialized.brickList.single() is SetXBrick)
    }

    @Test
    fun `deserialized script without button reacts to button A`() {
        val script = fromXml("<script type=\"WhenGamepadButtonPressedScript\"/>")
            as WhenGamepadButtonPressedScript

        assertEquals("A", script.button)
    }

    @Test
    fun `deserialized script keeps an unknown button ID`() {
        val script = fromXml(
            "<script type=\"WhenGamepadButtonPressedScript\"><button>HOME</button></script>"
        ) as WhenGamepadButtonPressedScript

        assertEquals("HOME", script.button)
        assertFalse(script.createEventId(sprite) is GamepadButtonPressedEventId)
    }

    /** All event ids that the stage, touches, broadcasts, NFC and Chromecast fire. */
    private fun firedEventIds(): List<EventId> =
        GamepadButton.entries.map { GamepadButtonPressedEventId(it) } +
            FIRED_EVENT_TYPES.map { EventId(it) } +
            LEGACY_GAMEPAD_ACTIONS.map { GamepadEventId(it) }

    /**
     * Reads [xml] with the project's XStream setup. The default XmlPull parser of XStream is only
     * a stub in JVM unit tests, so the JDK based DOM driver reads the XML instead.
     */
    private fun fromXml(xml: String): Any =
        xstream.unmarshal(DomDriver().createReader(StringReader(xml)))

    private companion object {
        const val SET_X_VALUE = 42

        val UNKNOWN_BUTTON_IDS = listOf("", "a", "Up", " A", "HOME", "GAMEPAD_A")

        val FIRED_EVENT_TYPES = listOf(
            EventId.TAP,
            EventId.TAP_BACKGROUND,
            EventId.START,
            EventId.START_AS_CLONE,
            EventId.ANY_NFC
        )

        val LEGACY_GAMEPAD_ACTIONS = listOf("A", "B", "up", "down", "left", "right", "HOME")
    }
}
