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

import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonPressedBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.MockUtil
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WhenGamepadButtonPressedBrickTest {

    @Test
    fun `new brick creates a linked script reacting to button A`() {
        val brick = WhenGamepadButtonPressedBrick()

        val script = brick.script as WhenGamepadButtonPressedScript

        assertEquals("A", script.button)
        assertSame(brick, script.scriptBrick)
    }

    @Test
    fun `brick for a script is linked to that script`() {
        val script = WhenGamepadButtonPressedScript("START")

        val brick = WhenGamepadButtonPressedBrick(script)

        assertSame(script, brick.script)
        assertSame(brick, script.scriptBrick)
    }

    @Test
    fun `brick for a commented out script is commented out`() {
        val script = WhenGamepadButtonPressedScript("START").apply { isCommentedOut = true }

        assertTrue(WhenGamepadButtonPressedBrick(script).isCommentedOut)
        assertFalse(WhenGamepadButtonPressedBrick().isCommentedOut)
    }

    @Test
    fun `commenting out the brick comments out its script and bricks`() {
        val brick = WhenGamepadButtonPressedBrick()
        val setXBrick = SetXBrick()
        brick.script.addBrick(setXBrick)

        brick.isCommentedOut = true

        assertTrue(brick.script.isCommentedOut)
        assertTrue(setXBrick.isCommentedOut)
    }

    @Test
    fun `clone keeps the button in a linked copy of the script`() {
        val brick = WhenGamepadButtonPressedBrick(WhenGamepadButtonPressedScript("DOWN"))

        val clone = brick.clone() as WhenGamepadButtonPressedBrick

        val clonedScript = clone.script as WhenGamepadButtonPressedScript
        assertNotSame(brick, clone)
        assertNotSame(brick.script, clonedScript)
        assertEquals("DOWN", clonedScript.button)
        assertSame(clone, clonedScript.scriptBrick)
        assertSame(brick, brick.script.scriptBrick)
    }

    @Test
    fun `clone is independent of the original brick`() {
        val brick = WhenGamepadButtonPressedBrick(WhenGamepadButtonPressedScript("DOWN"))
        val clone = brick.clone() as WhenGamepadButtonPressedBrick

        (clone.script as WhenGamepadButtonPressedScript).button = "Y"

        assertEquals("DOWN", (brick.script as WhenGamepadButtonPressedScript).button)
    }

    @Test
    fun `brick adds no action to a sequence`() {
        val brick = WhenGamepadButtonPressedBrick()
        val sequence = ActionFactory.createScriptSequenceAction(brick.script) as ScriptSequenceAction

        brick.addActionToSequence(Sprite("sprite"), sequence)

        assertEquals(0, sequence.actions.size)
    }

    @Test
    fun `brick requires no resources`() {
        val resources = Brick.ResourcesSet()

        WhenGamepadButtonPressedBrick().addRequiredResources(resources)

        assertTrue(resources.isEmpty())
    }

    @Test
    fun `project using the brick requires no resources and is no Chromecast project`() {
        val project = Project(MockUtil.mockContextForProject(), "GamepadProject")
        val sprite = Sprite("sprite")
        sprite.addScript(WhenGamepadButtonPressedBrick().script)
        project.defaultScene.addSprite(sprite)

        val resources = project.requiredResources

        assertTrue(resources.toString(), resources.isEmpty())
        assertFalse(project.isCastProject)
    }

    @Test
    fun `brick is serialized with the brick alias`() {
        val xml = XstreamSerializer.getInstance().xstream.toXML(
            WhenGamepadButtonPressedBrick(WhenGamepadButtonPressedScript("UP"))
        )

        assertThat(xml, startsWith("<brick type=\"WhenGamepadButtonPressedBrick\">"))
        assertThat(xml, containsString("<button>UP</button>"))
        assertThat(xml, not(containsString("org.catrobat.catroid")))
    }

    @Test
    fun `help page is named after the brick`() {
        assertEquals(
            "https://wiki.catrobat.org/bin/view/Documentation/BrickDocumentation/" +
                "WhenGamepadButtonPressedBrick",
            WhenGamepadButtonPressedBrick().getHelpUrl("Device")
        )
    }
}
