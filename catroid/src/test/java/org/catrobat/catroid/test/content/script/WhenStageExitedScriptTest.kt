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

import com.thoughtworks.xstream.io.xml.DomDriver
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenStageExitedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WhenStageExitedBrick
import org.catrobat.catroid.content.eventids.BroadcastEventId
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.content.eventids.GamepadEventId
import org.catrobat.catroid.content.eventids.StageExitedEventId
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.io.XstreamSerializer
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
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
class WhenStageExitedScriptTest {

    private val sprite: Sprite = mock(Sprite::class.java)
    private val xstream = XstreamSerializer.getInstance().xstream

    @Test
    fun `event id is the stage exited event id`() {
        assertEquals(StageExitedEventId, WhenStageExitedScript().createEventId(sprite))
    }

    @Test
    fun `stage exited event id equals no other fired event id`() {
        otherFiredEventIds().forEach { eventId ->
            assertNotEquals(eventId.toString(), eventId, StageExitedEventId)
            assertNotEquals(eventId.toString(), StageExitedEventId, eventId)
        }
    }

    @Test
    fun `script brick is a when stage exited brick linked to the script`() {
        val script = WhenStageExitedScript()

        val scriptBrick = script.scriptBrick

        assertTrue(scriptBrick is WhenStageExitedBrick)
        assertSame(script, scriptBrick.script)
        assertSame(scriptBrick, script.scriptBrick)
    }

    @Test
    fun `clone keeps the bricks and the comment out state and has its own script brick`() {
        val script = WhenStageExitedScript()
        script.addBrick(SetXBrick(SET_X_VALUE))
        script.isCommentedOut = true
        val originalBrick = script.scriptBrick

        val clone = script.clone() as WhenStageExitedScript

        assertNotSame(script, clone)
        assertEquals(1, clone.brickList.size)
        assertNotSame(script.brickList[0], clone.brickList[0])
        assertTrue(clone.isCommentedOut)
        assertNotSame(originalBrick, clone.scriptBrick)
        assertSame(clone, clone.scriptBrick.script)
        assertEquals(StageExitedEventId, clone.createEventId(sprite))
    }

    @Test
    fun `script requires no resources besides those of its bricks`() {
        val resources = Brick.ResourcesSet()

        WhenStageExitedScript().addRequiredResources(resources)

        assertTrue(resources.isEmpty())
    }

    @Test
    fun `script is serialized with the script alias`() {
        val xml = xstream.toXML(WhenStageExitedScript())

        assertThat(xml, startsWith("<script type=\"WhenStageExitedScript\""))
        assertThat(xml, not(containsString("org.catrobat.catroid")))
    }

    @Test
    fun `deserialized script keeps its bricks`() {
        val script = WhenStageExitedScript().apply { addBrick(SetXBrick(SET_X_VALUE)) }

        val deserialized = fromXml(xstream.toXML(script)) as WhenStageExitedScript

        assertTrue(deserialized.brickList.single() is SetXBrick)
        assertEquals(StageExitedEventId, deserialized.createEventId(sprite))
    }

    /** All other event ids that the stage, touches, broadcasts, gamepads, NFC and Chromecast fire. */
    private fun otherFiredEventIds(): List<EventId> =
        GamepadButton.entries.map { GamepadButtonPressedEventId(it) } +
            FIRED_EVENT_TYPES.map { EventId(it) } +
            listOf(GamepadEventId("B"), BroadcastEventId("stage exited"))

    /**
     * Reads [xml] with the project's XStream setup. The default XmlPull parser of XStream is only
     * a stub in JVM unit tests, so the JDK based DOM driver reads the XML instead.
     */
    private fun fromXml(xml: String): Any =
        xstream.unmarshal(DomDriver().createReader(StringReader(xml)))

    private companion object {
        const val SET_X_VALUE = 42

        val FIRED_EVENT_TYPES = listOf(
            EventId.TAP,
            EventId.TAP_BACKGROUND,
            EventId.START,
            EventId.START_AS_CLONE,
            EventId.ANY_NFC
        )
    }
}
