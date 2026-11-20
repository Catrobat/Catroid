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

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.EventWrapper
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.content.eventids.GamepadEventId
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Checks that "When gamepad button pressed" scripts start when the stage fires the pressed event of
 * their button, as StageListener does for every released-to-pressed edge of [GamepadInput].
 */
@RunWith(JUnit4::class)
class WhenGamepadButtonPressedScriptEventTest {

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private val gamepadInput = GamepadInput()
    private var scriptsStarted = false

    @Before
    fun setUp() {
        project = Project(MockUtil.mockContextForProject(), "TestProject")
        ProjectManager.getInstance().currentProject = project
        sprite = Sprite("testSprite")
        sprite.look.setPositionInUserInterfaceDimensionUnit(0f, 0f)
        project.defaultScene.addSprite(sprite)
    }

    @Test
    fun `pressing the button of the script runs the script`() {
        addScript(GamepadButton.UP.name)

        press(GamepadButton.UP)

        assertEquals(X_DELTA, xPosition())
    }

    @Test
    fun `pressing another button does not run the script`() {
        addScript(GamepadButton.UP.name)

        press(GamepadButton.DOWN)

        assertEquals(0, xPosition())
    }

    @Test
    fun `only the scripts of the pressed button run`() {
        addScript(GamepadButton.A.name)
        addScript(GamepadButton.A.name)
        addScript(GamepadButton.B.name)

        press(GamepadButton.A)

        assertEquals(2 * X_DELTA, xPosition())
    }

    @Test
    fun `every new press runs the script again`() {
        addScript(GamepadButton.R1.name)

        press(GamepadButton.R1)
        release(GamepadButton.R1)
        press(GamepadButton.R1)

        assertEquals(2 * X_DELTA, xPosition())
    }

    @Test
    fun `holding the button runs the script only once`() {
        addScript(GamepadButton.R1.name)

        press(GamepadButton.R1)
        press(GamepadButton.R1)

        assertEquals(X_DELTA, xPosition())
    }

    @Test
    fun `legacy Chromecast gamepad events do not run the script`() {
        addScript(GamepadButton.A.name)

        fire(GamepadEventId("A"))

        assertEquals(0, xPosition())
    }

    @Test
    fun `commented out script does not run`() {
        addScript(GamepadButton.X.name).isCommentedOut = true

        press(GamepadButton.X)

        assertEquals(0, xPosition())
    }

    @Test
    fun `script of an unknown button does not run for any button`() {
        addScript("HOME")

        GamepadButton.entries.forEach { press(it) }

        assertEquals(0, xPosition())
    }

    private fun addScript(button: String): WhenGamepadButtonPressedScript =
        WhenGamepadButtonPressedScript(button).also { script ->
            script.addBrick(ChangeXByNBrick(X_DELTA))
            sprite.addScript(script)
        }

    private fun press(button: GamepadButton) = setButtonState(button, true)

    private fun release(button: GamepadButton) = setButtonState(button, false)

    /** Changes the button like a controller would and applies it like the next stage frame. */
    private fun setButtonState(button: GamepadButton, pressed: Boolean) {
        startScriptsIfNeeded()
        gamepadInput.setButtonState(button, GamepadInputSource.KEY, pressed)
        gamepadInput.applyPendingInput { pressedButton ->
            project.fireToAllSprites(EventWrapper(GamepadButtonPressedEventId(pressedButton), false))
        }
        runAllThreads()
    }

    private fun fire(eventId: EventId) {
        startScriptsIfNeeded()
        project.fireToAllSprites(EventWrapper(eventId, false))
        runAllThreads()
    }

    /** Creates the event threads of all scripts once, like the stage does when it starts. */
    private fun startScriptsIfNeeded() {
        if (!scriptsStarted) {
            sprite.initializeEventThreads(EventId.START)
            scriptsStarted = true
        }
    }

    private fun runAllThreads() {
        repeat(MAX_FRAMES) {
            if (sprite.look.haveAllThreadsFinished()) {
                return
            }
            sprite.look.act(1.0f)
        }
    }

    private fun xPosition(): Int = sprite.look.xInUserInterfaceDimensionUnit.toInt()

    private companion object {
        const val X_DELTA = 15
        const val MAX_FRAMES = 100
    }
}
