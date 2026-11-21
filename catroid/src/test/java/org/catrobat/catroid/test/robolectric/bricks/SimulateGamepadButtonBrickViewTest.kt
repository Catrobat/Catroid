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
import android.widget.Spinner
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SimulateGamepadButtonBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SimulateGamepadButtonBrickViewTest {

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun `spinners show the stored button and state`() {
        val brick = SimulateGamepadButtonBrick("R2", false)

        createView(brick)

        assertEquals("R2", selectedName(buttonSpinner(brick)))
        assertEquals("released", selectedName(stateSpinner(brick)))
    }

    @Test
    fun `selecting a button stores its stable ID`() {
        val brick = SimulateGamepadButtonBrick()
        createView(brick)

        select(buttonSpinner(brick), "Start")

        assertEquals("START", brick.button)
        assertTrue(brick.pressed)
    }

    @Test
    fun `selecting released stores the released state`() {
        val brick = SimulateGamepadButtonBrick()
        createView(brick)

        select(stateSpinner(brick), "released")

        assertFalse(brick.pressed)
        assertEquals("A", brick.button)
    }

    private fun createView(brick: SimulateGamepadButtonBrick) {
        val activityController = Robolectric.buildActivity(SpriteActivity::class.java)
        val activity = activityController.get()
        createProject(activity, brick)
        activityController.create().resume()
        brick.getView(activity)
    }

    private fun createProject(activity: SpriteActivity, brick: SimulateGamepadButtonBrick) {
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        sprite.addScript(StartScript().apply { addBrick(brick) })
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    private fun buttonSpinner(brick: SimulateGamepadButtonBrick): Spinner =
        brick.view.findViewById(R.id.brick_simulate_gamepad_button_spinner)

    private fun stateSpinner(brick: SimulateGamepadButtonBrick): Spinner =
        brick.view.findViewById(R.id.brick_simulate_gamepad_button_state_spinner)

    private fun selectedName(spinner: Spinner): String =
        (spinner.selectedItem as Nameable).name

    private fun select(spinner: Spinner, name: String) {
        val adapter = spinner.adapter
        val position = (0 until adapter.count).first { (adapter.getItem(it) as Nameable).name == name }
        spinner.onItemSelectedListener?.onItemSelected(spinner, null, position, adapter.getItemId(position))
    }
}
