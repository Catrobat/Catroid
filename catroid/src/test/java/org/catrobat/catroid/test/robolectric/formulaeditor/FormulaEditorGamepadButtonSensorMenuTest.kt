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

package org.catrobat.catroid.test.robolectric.formulaeditor

import android.os.Build
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class FormulaEditorGamepadButtonSensorMenuTest {
    private lateinit var activity: SpriteActivity
    private lateinit var formulaEditorFragment: FormulaEditorFragment
    private lateinit var formulaEditorEditText: FormulaEditorEditText

    @Before
    fun setUp() {
        val activityController = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = activityController.get()
        val brick = SetXBrick()
        createProject(brick)
        activityController.create().resume()
        val brickView = brick.getView(activity)
        brick.onClick(brickView.findViewById<TextView>(R.id.brick_set_x_edit_text))
        idleMainLooper()

        formulaEditorFragment = activity.supportFragmentManager
            .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG) as FormulaEditorFragment
        formulaEditorEditText = activity.findViewById(R.id.formula_editor_edit_field)
    }

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun `selected gamepad button sensor opens the menu with all buttons`() {
        insertGamepadButtonASensor()
        selectSensorToken()

        formulaEditorFragment.showGamepadButtonSensorMenuIfSelected()
        idleMainLooper()

        assertTrue(latestDialog().isShowing)
        assertEquals(EXPECTED_LABELS, shownLabels())
    }

    @Test
    fun `choosing a button replaces the selected sensor`() {
        insertGamepadButtonASensor()
        selectSensorToken()

        formulaEditorFragment.showGamepadButtonSensorMenuIfSelected()
        idleMainLooper()
        clickMenuItem(EXPECTED_LABELS.indexOf("Up"))

        assertEquals("gamepad button up pressed", formulaText())
        assertFalse(latestDialog().isShowing)
    }

    @Test
    fun `replacing the sensor can be undone`() {
        insertGamepadButtonASensor()
        selectSensorToken()

        formulaEditorFragment.showGamepadButtonSensorMenuIfSelected()
        idleMainLooper()
        clickMenuItem(EXPECTED_LABELS.indexOf("R2"))

        assertEquals("gamepad button R2 pressed", formulaText())
        assertTrue(formulaEditorEditText.undo())
        assertEquals("gamepad button A pressed", formulaText())
    }

    @Test
    fun `cancelling the menu keeps the sensor`() {
        insertGamepadButtonASensor()
        selectSensorToken()

        formulaEditorFragment.showGamepadButtonSensorMenuIfSelected()
        idleMainLooper()
        latestDialog().cancel()
        idleMainLooper()

        assertEquals("gamepad button A pressed", formulaText())
    }

    @Test
    fun `other selected tokens open no menu`() {
        formulaEditorEditText.quickSelect()

        formulaEditorFragment.showGamepadButtonSensorMenuIfSelected()
        idleMainLooper()

        assertNull(ShadowDialog.getLatestDialog())
    }

    @Test
    fun `gamepad button sensor without selection opens no menu`() {
        insertGamepadButtonASensor()

        formulaEditorFragment.showGamepadButtonSensorMenuIfSelected()
        idleMainLooper()

        assertNull(ShadowDialog.getLatestDialog())
    }

    private fun insertGamepadButtonASensor() {
        formulaEditorFragment.addResourceToActiveFormula(
            R.string.formula_editor_sensor_gamepad_button_a_pressed
        )
        assertEquals("gamepad button A pressed", formulaText())
    }

    /** Selects the sensor token in the same way as a tap on its text does. */
    private fun selectSensorToken() {
        formulaEditorEditText.internFormula.setCursorAndSelection(TAP_OFFSET, false)
    }

    private fun formulaText(): String = formulaEditorEditText.text.toString().trim()

    private fun latestDialog(): AlertDialog = ShadowDialog.getLatestDialog() as AlertDialog

    private fun shownLabels(): List<String> {
        val adapter = latestDialog().listView.adapter
        return (0 until adapter.count).map { position -> adapter.getItem(position).toString() }
    }

    private fun clickMenuItem(position: Int) {
        val listView = latestDialog().listView
        val itemView = listView.adapter.getView(position, null, listView)
        listView.performItemClick(itemView, position, listView.adapter.getItemId(position))
        idleMainLooper()
    }

    private fun idleMainLooper() = shadowOf(Looper.getMainLooper()).idle()

    private fun createProject(brick: SetXBrick) {
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        val script = StartScript()
        script.addBrick(brick)
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    private companion object {
        const val TAP_OFFSET = 2

        val EXPECTED_LABELS = listOf(
            "A", "B", "X", "Y",
            "Up", "Down", "Left", "Right",
            "Start", "Select",
            "L1", "L2", "R1", "R2"
        )
    }
}
