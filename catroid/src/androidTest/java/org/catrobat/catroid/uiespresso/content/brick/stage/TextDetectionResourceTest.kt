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
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.runner.Flaky
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Cat.Quarantine
import org.catrobat.catroid.testsuites.annotations.Level.Functional
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category

class TextDetectionResourceTest {
    private lateinit var formula: Formula
    private lateinit var lastBrickInScript: ScriptEvaluationGateBrick

    @get:Rule var runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun testTextDetectionEnabled() {
        createProject(FormulaElement.ElementType.SENSOR, Sensors.TEXT_BLOCKS_NUMBER.name)
        baseActivityTestRule.launchActivity()

        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        assertTrue(textDetectionOn())
    }

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Test
    fun testTextDetectionDisabled() {
        createProject(FormulaElement.ElementType.NUMBER, "42")
        baseActivityTestRule.launchActivity()

        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        assertFalse(textDetectionOn())
    }

    @Category(AppUi::class, Functional::class, Quarantine::class)
    @Flaky
    @Test
    fun testTextDetectionChanged() {
        createProject(FormulaElement.ElementType.SENSOR, Sensors.TEXT_BLOCKS_NUMBER.name)
        baseActivityTestRule.launchActivity()

        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())
        lastBrickInScript.waitUntilEvaluated(3000)

        assertTrue(textDetectionOn())

        Espresso.pressBack()
        onView(ViewMatchers.withId(R.id.stage_dialog_button_back)).perform(ViewActions.click())
        formula.root = FormulaElement(FormulaElement.ElementType.NUMBER, "42", null)
        onView(ViewMatchers.withId(R.id.button_play)).perform(ViewActions.click())

        assertFalse(textDetectionOn())
    }

    private fun createProject(type: FormulaElement.ElementType, value: String) {
        formula = Formula(FormulaElement(type, value, null))

        val script = UiTestUtils.createProjectAndGetStartScript("TextDetectionResourceTest")
            .also {
            it.addBrick(SetSizeToBrick(formula))
        }
        lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(script)
    }

    private fun textDetectionOn() = StageActivity.getActiveCameraManager()?.detectionOn ?: false
}
