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

package org.catrobat.catroid.uiespresso.stage

import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.formulaeditor.sensor
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableNotEqualsForTimeMs
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

/**
 * Sends key events of a physical game controller to a running stage and checks that they reach the
 * gamepad scripts and sensors of the project, that an unused B acts like Android Back, and that
 * everything else keeps Android's handling.
 */
@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(AndroidJUnit4::class)
class StageGamepadInputTest {

    @get:Rule
    val baseActivityTestRule = BaseActivityTestRule(StageActivity::class.java, true, false)

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var stageStartedGate: ScriptEvaluationGateBrick

    private val stageActivity: StageActivity
        get() = checkNotNull(baseActivityTestRule.activity) { "The stage is not running" }

    @Before
    fun setUp() {
        val startScript = StartScript()
        project = UiTestUtils.createProjectWithCustomScript(javaClass.simpleName, startScript)
        sprite = UiTestUtils.getDefaultTestSprite(project)
        stageStartedGate = ScriptEvaluationGateBrick.appendToScript(startScript)
    }

    @Test
    fun pressFiresGamepadScriptOnceIgnoringKeyRepeats() {
        val presses = addPressCounter(GamepadButton.A)
        startStage()

        sendKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)
        sendKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A, repeatCount = 1)
        sendKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)

        assertUserVariableEqualsWithTimeout(presses, 1.0, TIMEOUT_MILLIS)
        assertUserVariableNotEqualsForTimeMs(presses, 2.0, QUIET_PERIOD_MILLIS)

        pressKey(KeyEvent.KEYCODE_BUTTON_A)

        assertUserVariableEqualsWithTimeout(presses, 2.0, TIMEOUT_MILLIS)
    }

    @Test
    fun sensorIsTrueOnlyWhileButtonIsHeld() {
        val pressed = addSensorMirror(GamepadButton.A)
        startStage()
        assertUserVariableEqualsWithTimeout(pressed, "false", TIMEOUT_MILLIS)

        sendKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)
        assertUserVariableEqualsWithTimeout(pressed, "true", TIMEOUT_MILLIS)

        sendKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)
        assertUserVariableEqualsWithTimeout(pressed, "false", TIMEOUT_MILLIS)
    }

    @Test
    fun unusedBOpensStageMenuLikeBack() {
        val aPresses = addPressCounter(GamepadButton.A)
        startStage()
        pressKey(KeyEvent.KEYCODE_BUTTON_A)
        assertUserVariableEqualsWithTimeout(aPresses, 1.0, TIMEOUT_MILLIS)

        pressKey(KeyEvent.KEYCODE_BUTTON_B)

        waitUntilStageMenuShows()
        assertFalse(stageActivity.isFinishing)
    }

    @Test
    fun usedBIsConsumedAndFiresItsScript() {
        val bPresses = addPressCounter(GamepadButton.B)
        startStage()

        pressKey(KeyEvent.KEYCODE_BUTTON_B)

        assertUserVariableEqualsWithTimeout(bPresses, 1.0, TIMEOUT_MILLIS)
        onView(withId(R.id.stage_dialog_button_continue)).check(doesNotExist())
        assertFalse(stageActivity.isFinishing)
    }

    @Test
    fun keyboardDpadKeyIsNotConsumed() {
        val upPresses = addPressCounter(GamepadButton.UP)
        startStage()

        assertFalse(isConsumedByStage(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, KEYBOARD)))
        assertFalse(isConsumedByStage(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, KEYBOARD)))
        pressKey(KeyEvent.KEYCODE_DPAD_UP, KEYBOARD)
        assertUserVariableNotEqualsForTimeMs(upPresses, 1.0, QUIET_PERIOD_MILLIS)

        pressKey(KeyEvent.KEYCODE_DPAD_UP, GAMEPAD)
        assertUserVariableEqualsWithTimeout(upPresses, 1.0, TIMEOUT_MILLIS)
    }

    @Test
    fun projectWithoutGamepadElementsConsumesOnlyControllerB() {
        startStage()

        listOf(KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_DPAD_UP).forEach { keyCode ->
            assertFalse(isConsumedByStage(keyEvent(KeyEvent.ACTION_DOWN, keyCode, GAMEPAD)))
            assertFalse(isConsumedByStage(keyEvent(KeyEvent.ACTION_UP, keyCode, GAMEPAD)))
        }
        assertFalse(stageActivity.isFinishing)

        assertTrue(isConsumedByStage(keyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B, GAMEPAD)))
        assertTrue(isConsumedByStage(keyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B, GAMEPAD)))

        waitUntilStageMenuShows()
    }

    @Test
    fun heldButtonIsReleasedAfterStageMenu() {
        val pressed = addSensorMirror(GamepadButton.A)
        startStage()
        sendKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)
        assertUserVariableEqualsWithTimeout(pressed, "true", TIMEOUT_MILLIS)

        pressBack()
        onStageDialogContinueButton().perform(click())

        assertUserVariableEqualsWithTimeout(pressed, "false", TIMEOUT_MILLIS)
    }

    @Test
    fun heldButtonIsReleasedWhenStageLosesWindowFocus() {
        val pressed = addSensorMirror(GamepadButton.A)
        startStage()
        sendKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A)
        assertUserVariableEqualsWithTimeout(pressed, "true", TIMEOUT_MILLIS)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            stageActivity.onWindowFocusChanged(false)
            stageActivity.onWindowFocusChanged(true)
        }

        assertUserVariableEqualsWithTimeout(pressed, "false", TIMEOUT_MILLIS)
        sendKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A)
    }

    /** Adds a script that counts how often [button] gets pressed. */
    private fun addPressCounter(button: GamepadButton): UserVariable {
        val presses = UserVariable("${button.name} presses")
        project.addUserVariable(presses)
        sprite.addScript(
            WhenGamepadButtonPressedScript(button.name).apply {
                addBrick(ChangeVariableBrick(Formula(1), presses))
            }
        )
        return presses
    }

    /** Adds a script that keeps copying the "gamepad button pressed" sensor of [button]. */
    private fun addSensorMirror(button: GamepadButton): UserVariable {
        val pressed = UserVariable("${button.name} pressed")
        project.addUserVariable(pressed)
        val sensorFormula = Formula(FormulaElement(ElementType.SENSOR, button.sensor.name, null))
        val foreverBrick = ForeverBrick().apply {
            addBrick(SetVariableBrick(sensorFormula, pressed))
        }
        sprite.addScript(StartScript().apply { addBrick(foreverBrick) })
        return pressed
    }

    /**
     * Matches the continue button in the stage menu. Looking only at dialogs keeps Espresso from
     * picking the stage window while the window focus moves to the stage menu that opens.
     */
    private fun onStageDialogContinueButton(): ViewInteraction =
        onView(withId(R.id.stage_dialog_button_continue)).inRoot(isDialog())

    private fun startStage() {
        baseActivityTestRule.launchActivity(null)
        stageStartedGate.waitUntilEvaluated(STAGE_START_TIMEOUT_MILLIS)
    }

    private fun waitUntilStageMenuShows() {
        val deadline = SystemClock.uptimeMillis() + TIMEOUT_MILLIS
        while (!stageActivity.dialogIsShowing()) {
            assertTrue("The stage menu did not open", SystemClock.uptimeMillis() < deadline)
            SystemClock.sleep(POLL_INTERVAL_MILLIS)
        }
    }

    private fun pressKey(keyCode: Int, source: Int = GAMEPAD) {
        sendKey(KeyEvent.ACTION_DOWN, keyCode, source)
        sendKey(KeyEvent.ACTION_UP, keyCode, source)
    }

    /** Injects the key event through the system, like a real input device. */
    private fun sendKey(action: Int, keyCode: Int, source: Int = GAMEPAD, repeatCount: Int = 0) =
        InstrumentationRegistry.getInstrumentation()
            .sendKeySync(keyEvent(action, keyCode, source, repeatCount))

    /** Dispatches the key event directly to the stage and returns whether the stage consumed it. */
    private fun isConsumedByStage(event: KeyEvent): Boolean {
        var consumed = false
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            consumed = stageActivity.dispatchKeyEvent(event)
        }
        return consumed
    }

    private fun keyEvent(action: Int, keyCode: Int, source: Int, repeatCount: Int = 0): KeyEvent {
        val now = SystemClock.uptimeMillis()
        return KeyEvent(
            now, now, action, keyCode, repeatCount, NO_META_STATE,
            KeyCharacterMap.VIRTUAL_KEYBOARD, NO_SCAN_CODE, NO_FLAGS, source
        )
    }

    companion object {
        private const val GAMEPAD = InputDevice.SOURCE_GAMEPAD
        private const val KEYBOARD = InputDevice.SOURCE_KEYBOARD
        private const val NO_META_STATE = 0
        private const val NO_SCAN_CODE = 0
        private const val NO_FLAGS = 0
        private const val STAGE_START_TIMEOUT_MILLIS = 5000
        private const val TIMEOUT_MILLIS = 3000
        private const val QUIET_PERIOD_MILLIS = 500
        private const val POLL_INTERVAL_MILLIS = 50L
    }
}
