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

package org.catrobat.catroid.test.robolectric.bricks

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Spinner
import android.widget.TextView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.bricks.WhenGamepadButtonPressedBrick
import org.catrobat.catroid.content.bricks.brickspinner.GamepadButtonOption
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.gamepad.labelResId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class WhenGamepadButtonPressedBrickViewTest {

    private lateinit var activity: SpriteActivity

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun `brick reads when gamepad button pressed`() {
        val brick = WhenGamepadButtonPressedBrick()

        createView(brick)

        assertEquals(listOf("When gamepad button", "pressed"), labelsOf(brick.view))
        assertEquals("pressed", activity.getString(R.string.brick_when_gamepad_button_pressed))
    }

    @Test
    fun `spinner offers any and the localized labels of all buttons in button order`() {
        val brick = WhenGamepadButtonPressedBrick()

        createView(brick)

        assertEquals(EXPECTED_LABELS, optionNames(buttonSpinner(brick)))
        assertEquals(
            listOf(activity.getString(R.string.gamepad_button_any)) +
                GamepadButton.entries.map { activity.getString(it.labelResId) },
            optionNames(buttonSpinner(brick))
        )
    }

    @Test
    fun `spinner shows any for a script of any button`() {
        val brick = WhenGamepadButtonPressedBrick(WhenGamepadButtonPressedScript(GamepadButton.ANY_ID))

        createView(brick)

        assertEquals("Any", selectedName(buttonSpinner(brick)))
    }

    @Test
    fun `spinner shows button A and not any for an unknown button`() {
        val brick = WhenGamepadButtonPressedBrick(WhenGamepadButtonPressedScript("HOME"))

        createView(brick)

        assertEquals("A", selectedName(buttonSpinner(brick)))
    }

    @Test
    fun `selecting any stores its stable ID`() {
        val brick = WhenGamepadButtonPressedBrick()
        createView(brick)

        select(buttonSpinner(brick), "Any")

        assertEquals(GamepadButton.ANY_ID, script(brick).button)
    }

    @Test
    fun `spinner shows button A for a new brick`() {
        val brick = WhenGamepadButtonPressedBrick()

        createView(brick)

        assertEquals("A", selectedName(buttonSpinner(brick)))
    }

    @Test
    fun `spinner shows the button of the script`() {
        val brick = WhenGamepadButtonPressedBrick(WhenGamepadButtonPressedScript("SELECT"))

        createView(brick)

        assertEquals("Select", selectedName(buttonSpinner(brick)))
        assertEquals("SELECT", script(brick).button)
    }

    @Test
    fun `selecting a button stores its stable ID in the script`() {
        val brick = WhenGamepadButtonPressedBrick()
        createView(brick)

        select(buttonSpinner(brick), "Left")

        assertEquals("LEFT", script(brick).button)
    }

    @Test
    fun `every offered button option stores its stable ID in the script`() {
        val brick = WhenGamepadButtonPressedBrick()
        createView(brick)

        buttonOptions(buttonSpinner(brick)).forEach { option ->
            brick.onItemSelected(R.id.brick_when_gamepad_button_pressed_spinner, option)

            assertEquals(option.buttonId, script(brick).button)
        }
    }

    private fun createView(brick: WhenGamepadButtonPressedBrick) {
        val activityController = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = activityController.get()
        createProject(activity, brick)
        activityController.create().resume()
        brick.getView(activity)
    }

    private fun createProject(activity: SpriteActivity, brick: WhenGamepadButtonPressedBrick) {
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        sprite.addScript(brick.script)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    private fun script(brick: WhenGamepadButtonPressedBrick): WhenGamepadButtonPressedScript =
        brick.script as WhenGamepadButtonPressedScript

    private fun buttonSpinner(brick: WhenGamepadButtonPressedBrick): Spinner =
        brick.view.findViewById(R.id.brick_when_gamepad_button_pressed_spinner)

    private fun optionNames(spinner: Spinner): List<String> =
        (0 until spinner.adapter.count).map { (spinner.adapter.getItem(it) as Nameable).name }

    private fun buttonOptions(spinner: Spinner): List<GamepadButtonOption> =
        (0 until spinner.adapter.count).map { spinner.adapter.getItem(it) as GamepadButtonOption }

    private fun selectedName(spinner: Spinner): String =
        (spinner.selectedItem as Nameable).name

    private fun select(spinner: Spinner, name: String) {
        val adapter = spinner.adapter
        val position = (0 until adapter.count).first { (adapter.getItem(it) as Nameable).name == name }
        spinner.onItemSelectedListener?.onItemSelected(spinner, null, position, adapter.getItemId(position))
    }

    /** The texts of the brick in reading order, without the spinner and the selection checkbox. */
    private fun labelsOf(view: View): List<String> = when (view) {
        is Spinner, is CompoundButton -> emptyList()
        is TextView -> listOf(view.text.toString())
        is ViewGroup -> (0 until view.childCount).flatMap { labelsOf(view.getChildAt(it)) }
        else -> emptyList()
    }

    private companion object {
        val EXPECTED_LABELS = listOf(
            "Any", "A", "B", "X", "Y", "Up", "Down", "Left", "Right", "Start", "Select", "L1", "L2", "R1", "R2"
        )
    }
}
