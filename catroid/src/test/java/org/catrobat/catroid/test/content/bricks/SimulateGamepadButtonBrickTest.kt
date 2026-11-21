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

package org.catrobat.catroid.test.content.bricks

import com.thoughtworks.xstream.io.xml.DomDriver
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.actions.SimulateGamepadButtonAction
import org.catrobat.catroid.content.bricks.SimulateGamepadButtonBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import java.io.StringReader

@RunWith(JUnit4::class)
class SimulateGamepadButtonBrickTest {

    private val xstream = XstreamSerializer.getInstance().xstream

    @Test
    fun `new brick presses button A`() {
        val brick = SimulateGamepadButtonBrick()

        assertEquals("A", brick.button)
        assertTrue(brick.pressed)
    }

    @Test
    fun `clone keeps button and state`() {
        val brick = SimulateGamepadButtonBrick("R2", false)

        val clone = brick.clone() as SimulateGamepadButtonBrick

        assertNotSame(brick, clone)
        assertEquals("R2", clone.button)
        assertFalse(clone.pressed)
    }

    @Test
    fun `clone is independent of the original brick`() {
        val brick = SimulateGamepadButtonBrick("R2", false)
        val clone = brick.clone() as SimulateGamepadButtonBrick

        clone.button = "START"
        clone.pressed = true

        assertEquals("R2", brick.button)
        assertFalse(brick.pressed)
    }

    @Test
    fun `brick is serialized with the brick alias, the stable button ID and the state`() {
        val xml = xstream.toXML(SimulateGamepadButtonBrick("SELECT", false))

        assertThat(xml, startsWith("<brick type=\"SimulateGamepadButtonBrick\">"))
        assertThat(xml, containsString("<button>SELECT</button>"))
        assertThat(xml, containsString("<pressed>false</pressed>"))
    }

    @Test
    fun `deserialized brick keeps button and state`() {
        val xml = xstream.toXML(SimulateGamepadButtonBrick("L1", false))

        val brick = fromXml(xml) as SimulateGamepadButtonBrick

        assertEquals("L1", brick.button)
        assertFalse(brick.pressed)
    }

    @Test
    fun `deserialized brick without button and state presses button A`() {
        val brick = fromXml("<brick type=\"SimulateGamepadButtonBrick\"/>")
            as SimulateGamepadButtonBrick

        assertEquals("A", brick.button)
        assertTrue(brick.pressed)
    }

    @Test
    fun `brick adds a simulate gamepad button action with its button and state`() {
        val sequence = ActionFactory.createScriptSequenceAction(mock(Script::class.java))
            as ScriptSequenceAction

        SimulateGamepadButtonBrick("DOWN", false).addActionToSequence(Sprite("sprite"), sequence)

        val action = sequence.actions.single() as SimulateGamepadButtonAction
        assertEquals("DOWN", action.button)
        assertFalse(action.pressed)
    }

    /**
     * Reads [xml] with the project's XStream setup. The default XmlPull parser of XStream is only
     * a stub in JVM unit tests, so the JDK based DOM driver reads the XML instead.
     */
    private fun fromXml(xml: String): Any =
        xstream.unmarshal(DomDriver().createReader(StringReader(xml)))
}
