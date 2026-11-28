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

package org.catrobat.catroid.uiespresso.formulaeditor

import android.preference.PreferenceManager
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.FORMULA_EDITOR_TEXT_FIELD_MATCHER
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(AndroidJUnit4::class)
class FormulaEditorGamepadButtonMenuTest {

    @JvmField
    @Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    private val sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
    private var initialSetting = false

    @Before
    @Throws(Exception::class)
    fun setUp() {
        initialSetting = sharedPreferences
            .getBoolean(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, false)
        setPhysicalGamepadSetting(true)

        val script = createProjectAndGetStartScript(javaClass.simpleName)
        script.addBrick(ChangeSizeByNBrick(Formula(0)))
        baseActivityTestRule.launchActivity()

        onBrickAtPosition(1)
            .onFormulaTextField(R.id.brick_change_size_by_edit_text)
            .perform(click())
        onFormulaEditor()
            .performOpenCategory(FormulaEditorWrapper.Category.DEVICE)
            .performSelect(R.string.formula_editor_sensor_gamepad_button_a_pressed)
        onFormulaEditor()
            .checkShows(str(R.string.formula_editor_sensor_gamepad_button_a_pressed) + " ")
    }

    @After
    fun tearDown() {
        setPhysicalGamepadSetting(initialSetting)
    }

    @Test
    fun tapOnGamepadButtonSensorShowsAllButtons() {
        onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER).perform(tapOnFirstToken())

        onView(isAssignableFrom(ListView::class.java))
            .inRoot(isDialog())
            .check(matches(withItemCount(EXPECTED_LABEL_IDS.size)))
        EXPECTED_LABEL_IDS.forEachIndexed { position, labelId ->
            onData(anything())
                .inAdapterView(isAssignableFrom(ListView::class.java))
                .inRoot(isDialog())
                .atPosition(position)
                .check(matches(withText(labelId)))
        }
    }

    @Test
    fun choosingAButtonReplacesTheSensor() {
        onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER).perform(tapOnFirstToken())

        onView(withText(R.string.gamepad_button_up))
            .inRoot(isDialog())
            .perform(click())

        onFormulaEditor()
            .checkShows(str(R.string.formula_editor_sensor_gamepad_button_up_pressed) + " ")
    }

    @Test
    fun cancellingTheMenuKeepsTheSensor() {
        onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER).perform(tapOnFirstToken())
        onView(isAssignableFrom(ListView::class.java))
            .inRoot(isDialog())
            .check(matches(withItemCount(EXPECTED_LABEL_IDS.size)))

        pressBack()

        onFormulaEditor()
            .checkShows(str(R.string.formula_editor_sensor_gamepad_button_a_pressed) + " ")
    }

    private fun setPhysicalGamepadSetting(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, enabled)
            .commit()
    }

    /** Taps on the first line of the formula, inside the first token, like a user selecting it. */
    private fun tapOnFirstToken(): ViewAction = GeneralClickAction(
        Tap.SINGLE,
        CoordinatesProvider { view -> firstTokenCoordinates(view as TextView) },
        Press.FINGER,
        InputDevice.SOURCE_UNKNOWN,
        MotionEvent.BUTTON_PRIMARY
    )

    private fun firstTokenCoordinates(textView: TextView): FloatArray {
        val screenLocation = IntArray(2)
        textView.getLocationOnScreen(screenLocation)
        val x = screenLocation[0] + textView.totalPaddingLeft +
            textView.layout.getPrimaryHorizontal(TAP_OFFSET) - textView.scrollX
        val y = screenLocation[1] + textView.lineHeight / 2f
        return floatArrayOf(x, y)
    }

    private fun withItemCount(count: Int): Matcher<View> =
        object : BoundedMatcher<View, ListView>(ListView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("list with $count items")
            }

            override fun matchesSafely(listView: ListView): Boolean = listView.adapter?.count == count
        }

    private companion object {
        const val TAP_OFFSET = 2

        val EXPECTED_LABEL_IDS = listOf(
            R.string.gamepad_button_any,
            R.string.gamepad_button_a,
            R.string.gamepad_button_b,
            R.string.gamepad_button_x,
            R.string.gamepad_button_y,
            R.string.gamepad_button_up,
            R.string.gamepad_button_down,
            R.string.gamepad_button_left,
            R.string.gamepad_button_right,
            R.string.gamepad_button_start,
            R.string.gamepad_button_select,
            R.string.gamepad_button_l1,
            R.string.gamepad_button_l2,
            R.string.gamepad_button_r1,
            R.string.gamepad_button_r2
        )

        fun str(resId: Int): String = UiTestUtils.getResourcesString(resId) ?: ""
    }
}
