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

import android.content.Intent
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenStageExitedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ExitStageBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SceneTransitionBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableNotEqualsForTimeMs
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

/**
 * Checks that the event brick "When stage is exited" runs when Android Back or "Finish stage" exits
 * the project, before the stage menu opens or the stage closes. All its scripts run in parallel, and
 * a second Back press or a "Finish stage" brick in one of them ends the wait.
 */
@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(AndroidJUnit4::class)
class WhenStageExitedTest {

    @get:Rule
    val baseActivityTestRule = BaseActivityTestRule(StageActivity::class.java, true, false)

    private lateinit var project: Project
    private lateinit var sprite: Sprite
    private lateinit var stageStartedGate: ScriptEvaluationGateBrick
    private lateinit var catchState: UserVariable

    private val stageActivity: StageActivity
        get() = checkNotNull(baseActivityTestRule.activity) { "The stage is not running" }

    @Before
    fun setUp() {
        val startScript = StartScript()
        project = UiTestUtils.createProjectWithCustomScript(javaClass.simpleName, startScript)
        sprite = UiTestUtils.getDefaultTestSprite(project)
        stageStartedGate = ScriptEvaluationGateBrick.appendToScript(startScript)
        catchState = UserVariable("catch state", NOT_CAUGHT)
        project.addUserVariable(catchState)
    }

    @Test
    fun backWithoutCatchScriptOpensStageMenuAtOnce() {
        startStage()

        pressBack()

        onView(withId(R.id.stage_dialog_button_continue)).inRoot(isDialog()).check(matches(isDisplayed()))
        assertFalse(stageActivity.isFinishing)
    }

    @Test
    fun backInNormalModeRunsCatchThenOpensStageMenu() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(CATCH_MILLIS), setCatchState(CATCH_DONE))
        startStage()

        pressBackUnconditionally()

        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        assertFalse(stageActivity.dialogIsShowing())
        waitUntilStageMenuShows()
        assertEquals(CATCH_DONE, catchState.value)
        assertFalse(stageActivity.isFinishing)
    }

    @Test
    fun allCatchScriptsRunInParallelBeforeStageMenu() {
        val secondCatchState = UserVariable("second catch state", NOT_CAUGHT)
        project.addUserVariable(secondCatchState)
        addCatchScript(setCatchState(CAUGHT), WaitBrick(CATCH_MILLIS), setCatchState(CATCH_DONE))
        addCatchScript(
            SetVariableBrick(Formula(CAUGHT), secondCatchState),
            WaitBrick(2 * CATCH_MILLIS),
            SetVariableBrick(Formula(CATCH_DONE), secondCatchState)
        )
        startStage()

        pressBackUnconditionally()

        assertUserVariableEqualsWithTimeout(secondCatchState, CAUGHT, CATCH_MILLIS / 2)
        assertEquals(CAUGHT, catchState.value)
        assertUserVariableEqualsWithTimeout(catchState, CATCH_DONE, TIMEOUT_MILLIS)
        assertFalse(stageActivity.dialogIsShowing())
        waitUntilStageMenuShows()
        assertEquals(CATCH_DONE, secondCatchState.value)
    }

    @Test
    fun secondBackInNormalModeOpensStageMenuDuringLongCatch() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(LONG_CATCH_MILLIS), setCatchState(CATCH_DONE))
        startStage()
        pressBackUnconditionally()
        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        assertFalse(stageActivity.dialogIsShowing())

        pressBackUnconditionally()

        waitUntilStageMenuShows()
        assertEquals(CAUGHT, catchState.value)
    }

    @Test
    fun finishStageInsideCatchOfBackOpensStageMenuWithoutWaitingForOtherCatch() {
        addCatchScript(WaitBrick(LONG_CATCH_MILLIS))
        addCatchScript(setCatchState(CAUGHT), ExitStageBrick())
        startStage()

        pressBackUnconditionally()

        waitUntilStageMenuShows()
        assertEquals(CAUGHT, catchState.value)
        assertFalse(stageActivity.isFinishing)
    }

    @Test
    fun backAfterContinueRunsCatchAgain() {
        addCatchScript(ChangeVariableBrick(Formula(CAUGHT), catchState))
        startStage()
        pressBackUnconditionally()
        waitUntilStageMenuShows()
        assertEquals(CAUGHT, catchState.value)

        onView(withId(R.id.stage_dialog_button_continue)).inRoot(isDialog()).perform(click())
        pressBackUnconditionally()

        assertUserVariableEqualsWithTimeout(catchState, 2 * CAUGHT, TIMEOUT_MILLIS)
        waitUntilStageMenuShows()
    }

    @Test
    fun stageMenuBackLeavesWithoutRunningCatchAgain() {
        addCatchScript(ChangeVariableBrick(Formula(CAUGHT), catchState))
        startStage()
        pressBackUnconditionally()
        waitUntilStageMenuShows()

        onView(withId(R.id.stage_dialog_button_back)).inRoot(isDialog()).perform(click())

        waitUntilStageFinishes()
        assertEquals(CAUGHT, catchState.value)
    }

    @Test
    fun backInShortcutModeRunsCatchThenExits() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(CATCH_MILLIS), setCatchState(CATCH_DONE))
        startStage(fromShortcut = true)

        pressBackUnconditionally()

        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        assertFalse(stageActivity.isFinishing)
        waitUntilStageFinishes()
        assertEquals(CATCH_DONE, catchState.value)
    }

    @Test
    fun secondBackInShortcutModeForcesExitDuringLongCatch() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(LONG_CATCH_MILLIS), setCatchState(CATCH_DONE))
        startStage(fromShortcut = true)
        pressBackUnconditionally()
        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        assertUserVariableNotEqualsForTimeMs(catchState, CATCH_DONE, QUIET_PERIOD_MILLIS)
        assertFalse(stageActivity.isFinishing)

        pressBackUnconditionally()

        waitUntilStageFinishes()
        assertEquals(CAUGHT, catchState.value)
    }

    @Test
    fun finishStageRunsCatchThenExits() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(CATCH_MILLIS), setCatchState(CATCH_DONE))
        addFinishStageScript()
        startStage()

        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        assertFalse(stageActivity.isFinishing)
        waitUntilStageFinishes()
        assertEquals(CATCH_DONE, catchState.value)
    }

    @Test
    fun backDuringCatchOfFinishStageForcesExitWithoutStageMenu() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(LONG_CATCH_MILLIS), setCatchState(CATCH_DONE))
        addFinishStageScript()
        startStage()
        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)

        pressBackUnconditionally()

        waitUntilStageFinishes()
        assertFalse(stageActivity.dialogIsShowing())
        assertEquals(CAUGHT, catchState.value)
    }

    @Test
    fun startSceneInsideCatchOfFinishStageCompletesTheExit() {
        addSecondScene()
        addCatchScript(setCatchState(CAUGHT), SceneStartBrick(SECOND_SCENE), WaitBrick(LONG_CATCH_MILLIS))
        addFinishStageScript()
        startStage()

        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        waitUntilStageFinishes()
        assertFalse(stageActivity.dialogIsShowing())
    }

    @Test
    fun continueSceneInsideCatchOfBackOpensStageMenu() {
        addSecondScene()
        addCatchScript(setCatchState(CAUGHT), SceneTransitionBrick(SECOND_SCENE), WaitBrick(LONG_CATCH_MILLIS))
        startStage()

        pressBackUnconditionally()

        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        waitUntilStageMenuShows()
        assertFalse(stageActivity.isFinishing)
    }

    @Test
    fun unusedGamepadBRunsCatchThenOpensStageMenuLikeBack() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(CATCH_MILLIS), setCatchState(CATCH_DONE))
        startStage()

        pressGamepadB()

        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        assertFalse(stageActivity.dialogIsShowing())
        waitUntilStageMenuShows()
        assertEquals(CATCH_DONE, catchState.value)
        assertFalse(stageActivity.isFinishing)
    }

    @Test
    fun unusedGamepadBInShortcutModeRunsCatchThenExitsLikeBack() {
        addCatchScript(setCatchState(CAUGHT), WaitBrick(CATCH_MILLIS), setCatchState(CATCH_DONE))
        startStage(fromShortcut = true)

        pressGamepadB()

        assertUserVariableEqualsWithTimeout(catchState, CAUGHT, TIMEOUT_MILLIS)
        assertFalse(stageActivity.isFinishing)
        waitUntilStageFinishes()
        assertEquals(CATCH_DONE, catchState.value)
    }

    private fun addCatchScript(vararg bricks: Brick) {
        sprite.addScript(WhenStageExitedScript().apply { bricks.forEach { addBrick(it) } })
    }

    /** Adds a script that uses the brick "Finish stage" once the stage has run for a moment. */
    private fun addFinishStageScript() {
        sprite.addScript(
            StartScript().apply {
                addBrick(WaitBrick(FINISH_STAGE_DELAY_MILLIS))
                addBrick(ExitStageBrick())
            }
        )
    }

    /** Adds a second scene, whose start leaves the scene with the catch scripts. */
    private fun addSecondScene() {
        project.addScene(Scene(SECOND_SCENE, project).apply { addSprite(Sprite("Background")) })
    }

    private fun setCatchState(value: Double): Brick = SetVariableBrick(Formula(value), catchState)

    private fun startStage(fromShortcut: Boolean = false) {
        val intent = Intent().putExtra(StageActivity.EXTRA_IS_FROM_SHORTCUT, fromShortcut)
        baseActivityTestRule.launchActivity(intent)
        stageStartedGate.waitUntilEvaluated(STAGE_START_TIMEOUT_MILLIS)
    }

    private fun pressGamepadB() {
        listOf(KeyEvent.ACTION_DOWN, KeyEvent.ACTION_UP).forEach { action ->
            val now = SystemClock.uptimeMillis()
            val event = KeyEvent(
                now, now, action, KeyEvent.KEYCODE_BUTTON_B, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_GAMEPAD
            )
            InstrumentationRegistry.getInstrumentation().sendKeySync(event)
        }
    }

    private fun waitUntilStageMenuShows() {
        val deadline = SystemClock.uptimeMillis() + TIMEOUT_MILLIS
        while (!stageActivity.dialogIsShowing()) {
            assertTrue("The stage menu did not open", SystemClock.uptimeMillis() < deadline)
            SystemClock.sleep(POLL_INTERVAL_MILLIS)
        }
    }

    private fun waitUntilStageFinishes() {
        val deadline = SystemClock.uptimeMillis() + TIMEOUT_MILLIS
        while (!stageActivity.isFinishing) {
            assertTrue("The stage did not finish", SystemClock.uptimeMillis() < deadline)
            SystemClock.sleep(POLL_INTERVAL_MILLIS)
        }
    }

    companion object {
        private const val NOT_CAUGHT = 0.0
        private const val CAUGHT = 1.0
        private const val CATCH_DONE = 2.0
        private const val CATCH_MILLIS = 1500
        private const val LONG_CATCH_MILLIS = 60_000
        private const val FINISH_STAGE_DELAY_MILLIS = 300
        private const val STAGE_START_TIMEOUT_MILLIS = 5000
        private const val TIMEOUT_MILLIS = 5000
        private const val QUIET_PERIOD_MILLIS = 500
        private const val POLL_INTERVAL_MILLIS = 50L
        private const val SECOND_SCENE = "Second scene"
    }
}
