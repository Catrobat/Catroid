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

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.getResourcesString
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StageResourceFailedTest {
    @get:Rule
    var baseActivityTestRule: BaseActivityTestRule<StageActivity?> =
        BaseActivityTestRule(StageActivity::class.java, true, false)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject("StagePausedTest")
        val sensorHandler = SensorHandler.getInstance(ApplicationProvider.getApplicationContext())
        sensorHandler.setAccelerationUnavailable()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testResourceFailedDialog() {
        baseActivityTestRule.launchActivity(null)

        onView(withText(R.string.prestage_resource_not_available_title)).inRoot(isDialog())
            .check(matches(isDisplayed()))

        val failedResourceMessage =
            (getResourcesString(R.string.prestage_resource_not_available_text) + getResourcesString(
                R.string.prestage_no_acceleration_sensor_available
            ))

        onView(withText(failedResourceMessage)).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    fun createProject(projectName: String?) {
        val script = createProjectAndGetStartScript(projectName)
        val accelerationFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.SENSOR, Sensors.X_ACCELERATION.name, null
            )
        )
        val setXBrick = SetXBrick(accelerationFormula)
        script.addBrick(setXBrick)
    }
}
