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

package org.catrobat.catroid.ui.dialogs

import android.content.Context
import android.content.ContextWrapper
import android.hardware.input.InputManager
import android.os.Build
import android.preference.PreferenceManager
import android.view.InputDevice.SOURCE_GAMEPAD
import android.view.InputDevice.SOURCE_JOYSTICK
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * The Compute dialog shows the physical gamepad buttons of its formula live: it receives the
 * controller input itself, because the gamepad sensors otherwise read only the running stage.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class FormulaEditorComputeDialogGamepadTest {

    private val inputManager = mock(InputManager::class.java)
    private lateinit var activity: AppCompatActivity
    private lateinit var scope: Scope
    private var dialog: FormulaEditorComputeDialog? = null

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(AppCompatActivity::class.java).setup().get()
        activity.setTheme(R.style.Catroid)
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("sprite")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
        scope = Scope(project, sprite, SequenceAction())
    }

    @After
    fun tearDown() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit()
            .remove(SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS)
            .commit()
        dialog?.dismiss()
        ShadowLooper.shadowMainLooper().idle()
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun `shows a button of the formula as pressed while the controller holds it`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_A_PRESSED)
        assertEquals(falseString(), dialog.result())

        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)))
        assertEquals(trueString(), dialog.result())

        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)))
        assertEquals(falseString(), dialog.result())
    }

    @Test
    fun `shows a D-pad direction that the controller reports on its hat axis as pressed`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_UP_PRESSED)

        assertTrue(dialog.dispatchGenericMotionEvent(hatMotion(hatY = HAT_UP)))
        assertEquals(trueString(), dialog.result())

        assertTrue(dialog.dispatchGenericMotionEvent(hatMotion(hatY = HAT_CENTER)))
        assertEquals(falseString(), dialog.result())
    }

    @Test
    fun `shows the any sensor as pressed while a button is held`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_ANY_PRESSED)

        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_X)))
        assertEquals(trueString(), dialog.result())

        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_X)))
        assertEquals(falseString(), dialog.result())
    }

    @Test
    fun `any sensor leaves an unused B to Android`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_ANY_PRESSED)

        assertFalse(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        assertEquals(falseString(), dialog.result())
    }

    @Test
    fun `any sensor counts B when the project uses B`() {
        ProjectManager.getInstance().currentSprite.addScript(WhenGamepadButtonPressedScript("B"))
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_ANY_PRESSED)

        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        assertEquals(trueString(), dialog.result())
    }

    @Test
    fun `swapped face buttons show the controller A as B`() {
        swapFaceButtons()
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_B_PRESSED)

        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)))
        assertEquals(trueString(), dialog.result())
    }

    @Test
    fun `swapped controller A closes the dialog like Back when the formula does not use B`() {
        swapFaceButtons()
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_A_PRESSED)

        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)))
        assertTrue(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)))
        ShadowLooper.shadowMainLooper().idle()

        assertFalse(dialog.isShowing)
    }

    @Test
    fun `shows the left stick of the formula live`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_LEFT_STICK_X)

        assertTrue(dialog.dispatchGenericMotionEvent(stickMotion(leftX = 0.5f)))
        assertEquals(50.0, dialog.result().toDouble(), 0.0)

        assertTrue(dialog.dispatchGenericMotionEvent(stickMotion(leftX = 0f)))
        assertEquals(0.0, dialog.result().toDouble(), 0.0)
    }

    @Test
    fun `leaves buttons that the formula does not use to Android`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_A_PRESSED)

        assertFalse(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B)))
        assertEquals(falseString(), dialog.result())
    }

    @Test
    fun `shows the button as released when the controller is removed`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_A_PRESSED)
        dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A))

        registeredInputDeviceListener().onInputDeviceRemoved(GAMEPAD_DEVICE_ID)

        assertEquals(falseString(), dialog.result())
    }

    @Test
    fun `shows the button as released when the dialog loses the focus`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_A_PRESSED)
        dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A))
        assertEquals(trueString(), dialog.result())

        dialog.onWindowFocusChanged(false)

        assertEquals(falseString(), dialog.result())
    }

    @Test
    fun `stops listening for the controller when closed`() {
        val dialog = showDialogFor(Sensors.GAMEPAD_BUTTON_A_PRESSED)
        dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A))
        val listener = registeredInputDeviceListener()

        dialog.dismiss()
        ShadowLooper.shadowMainLooper().idle()

        verify(inputManager).unregisterInputDeviceListener(listener)
        assertEquals(RELEASED, SensorHandler.getSensorValue(Sensors.GAMEPAD_BUTTON_A_PRESSED))
    }

    @Test
    fun `does not listen for a controller without a gamepad button in the formula`() {
        val dialog = showDialogFor(Sensors.LOUDNESS)

        assertFalse(dialog.dispatchKeyEvent(gamepadKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)))
        verifyNoInteractions(inputManager)
    }

    private fun swapFaceButtons() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit()
            .putBoolean(SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS, true)
            .commit()
    }

    private fun showDialogFor(sensor: Sensors): FormulaEditorComputeDialog {
        val computeDialog = FormulaEditorComputeDialog(InputManagerContext(activity, inputManager), scope)
        computeDialog.setFormula(Formula(FormulaElement(ElementType.SENSOR, sensor.name, null)))
        computeDialog.show()
        ShadowLooper.shadowMainLooper().idle()
        dialog = computeDialog
        return computeDialog
    }

    private fun FormulaEditorComputeDialog.result(): String {
        ShadowLooper.shadowMainLooper().idle()
        return findViewById<TextView>(R.id.formula_editor_compute_dialog_textview)?.text.toString()
    }

    private fun registeredInputDeviceListener(): InputManager.InputDeviceListener {
        val listener = ArgumentCaptor.forClass(InputManager.InputDeviceListener::class.java)
        verify(inputManager).registerInputDeviceListener(listener.capture(), any())
        return listener.value
    }

    private fun trueString() = activity.getString(R.string.formula_editor_true)

    private fun falseString() = activity.getString(R.string.formula_editor_false)

    private fun gamepadKey(action: Int, keyCode: Int) =
        KeyEvent(0L, 0L, action, keyCode, 0, 0, GAMEPAD_DEVICE_ID, 0, 0, SOURCE_GAMEPAD)

    private fun stickMotion(leftX: Float): MotionEvent {
        val properties = MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_UNKNOWN
        }
        val coordinates = MotionEvent.PointerCoords().apply {
            setAxisValue(MotionEvent.AXIS_X, leftX)
        }
        return MotionEvent.obtain(
            0L, 0L, MotionEvent.ACTION_MOVE, 1, arrayOf(properties), arrayOf(coordinates),
            0, 0, 1f, 1f, GAMEPAD_DEVICE_ID, 0, SOURCE_JOYSTICK, 0
        )
    }

    private fun hatMotion(hatY: Float): MotionEvent {
        val properties = MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_UNKNOWN
        }
        val coordinates = MotionEvent.PointerCoords().apply {
            setAxisValue(MotionEvent.AXIS_HAT_Y, hatY)
        }
        return MotionEvent.obtain(
            0L, 0L, MotionEvent.ACTION_MOVE, 1, arrayOf(properties), arrayOf(coordinates),
            0, 0, 1f, 1f, GAMEPAD_DEVICE_ID, 0, SOURCE_JOYSTICK, 0
        )
    }

    /** Hands out [inputManager] as the input service, so tests can observe the controller listener. */
    private class InputManagerContext(base: Context, private val inputManager: InputManager) :
        ContextWrapper(base) {
        override fun getSystemService(name: String): Any? =
            if (name == Context.INPUT_SERVICE) inputManager else super.getSystemService(name)
    }

    private companion object {
        const val GAMEPAD_DEVICE_ID = 7
        const val HAT_UP = -1f
        const val HAT_CENTER = 0f
        const val RELEASED = 0.0
    }
}
