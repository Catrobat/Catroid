/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.content.brick.stage

import android.Manifest
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.CameraBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Cat.Quarantine
import org.catrobat.catroid.testsuites.annotations.Level.Functional
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category

class CameraResourceTest {
    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun cameraResourceNotUsedTest() {
        val script = UiTestUtils.createProjectAndGetStartScript("cameraResourceNotUsed")
        val lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script)

        baseActivityTestRule.launchActivity()
        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        Assert.assertNull(StageActivity.getActiveCameraManager())
    }

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun cameraOnTest() {
        val script = UiTestUtils.createProjectAndGetStartScript("cameraOnTest").also {
            it.addBrick(CameraBrick(ON))
        }
        val lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script)

        baseActivityTestRule.launchActivity()
        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        assertTrue(StageActivity.getActiveCameraManager().isCameraActive)
        assertTrue(StageActivity.getActiveCameraManager().isCameraFacingFront)
    }

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun cameraStagePausedTest() {
        val script = UiTestUtils.createProjectAndGetStartScript("cameraStagePausedTest").also {
            it.addBrick(CameraBrick(ON))
        }
        val lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script)

        baseActivityTestRule.launchActivity()
        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        Espresso.pressBack()
        assertFalse(StageActivity.getActiveCameraManager().isCameraActive)
    }

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun cameraOffTest() {
        val script = UiTestUtils.createProjectAndGetStartScript("cameraOffTest").also {
            it.addBrick(CameraBrick(!ON))
        }
        val lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script)

        baseActivityTestRule.launchActivity()
        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        assertFalse(StageActivity.getActiveCameraManager().isCameraActive)
    }

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun cameraFacingFrontTest() {
        val script = UiTestUtils.createProjectAndGetStartScript("cameraFacingFrontTest").also {
            it.addBrick(ChooseCameraBrick(FRONT))
            it.addBrick(CameraBrick(ON))
        }
        val lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script)

        baseActivityTestRule.launchActivity()
        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        assertTrue(StageActivity.getActiveCameraManager().isCameraActive)
        assertTrue(StageActivity.getActiveCameraManager().isCameraFacingFront)
    }

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun cameraFacingBackTest() {
        val script = UiTestUtils.createProjectAndGetStartScript("cameraFacingBackTest").also {
            it.addBrick(ChooseCameraBrick(!FRONT))
            it.addBrick(CameraBrick(ON))
        }
        val lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script)

        baseActivityTestRule.launchActivity()
        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        assertTrue(StageActivity.getActiveCameraManager().isCameraActive)
        assertFalse(StageActivity.getActiveCameraManager().isCameraFacingFront)
    }

    companion object {
        private const val FRONT = true
        private const val ON = true
    }
}
