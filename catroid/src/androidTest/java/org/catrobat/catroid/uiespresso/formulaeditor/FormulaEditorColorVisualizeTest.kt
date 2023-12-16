/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.text.Spannable
import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.formulaeditor.VisualizeColorImageSpan
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper.onColorPickerPresetButton
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.Category.LOGIC
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.FORMULA_EDITOR_TEXT_FIELD_MATCHER
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.koin.java.KoinJavaComponent.inject
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FormulaEditorColorVisualizeTest {

    private val COLOR_STRING_CONVERSION_CONSTANT = 16

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @JvmField
    @Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val script = createProjectAndGetStartScript(
            FormulaEditorMultiplayerVariablesTest::class.java.simpleName
        )
        script.addBrick(SetColorBrick())
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun visualizeColorInScriptView() {
        onView(withId(R.id.brick_set_color_edit_text)).perform(click())

        setTestColorWithColorPicker(0, 0)

        pressBack()

        onView(withId(R.id.brick_set_color_edit_text))
            .check(matches(withText("'#0074CD ' ")))

        onView(withId(R.id.brick_set_color_edit_text))
            .check(
                matches(
                    VisualizeColorMatcher(
                        intArrayOf(getColorValueFromColorString("'#0074CD ' "))
                    )
                )
            )
    }

    @Test
    fun visualizeMultipleColorsInScriptView() {
        onView(withId(R.id.brick_set_color_edit_text)).perform(click())

        setTestColorWithColorPicker(0, 0)

        onFormulaEditor().performOpenCategory(LOGIC).performSelect("or")

        setTestColorWithColorPicker(2, 2)

        pressBack()

        onView(withId(R.id.brick_set_color_edit_text))
            .check(matches(withText("'#0074CD ' or '#CA0186 ' ")))

        onView(withId(R.id.brick_set_color_edit_text))
            .check(
                matches(
                    VisualizeColorMatcher(
                        intArrayOf(
                            getColorValueFromColorString("'#0074CD ' "),
                            getColorValueFromColorString("'#CA0186 ' ")
                        )
                    )
                )
            )
    }

    @Test
    fun visualizeColorInFormulaEditor() {
        onView(withId(R.id.brick_set_color_edit_text))
            .perform(click())

        setTestColorWithColorPicker(0, 0)

        onFormulaEditor().checkShows("'#0074CD ' ")

        onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER)
            .check(
                matches(
                    VisualizeColorMatcher(
                        intArrayOf(getColorValueFromColorString("'#0074CD ' "))
                    )
                )
            )
    }

    @Test
    fun visualizeMultipleColorsInFormulaEditor() {
        onView(withId(R.id.brick_set_color_edit_text)).perform(click())

        setTestColorWithColorPicker(0, 0)

        onFormulaEditor().performOpenCategory(LOGIC).performSelect("or")

        setTestColorWithColorPicker(2, 2)

        onFormulaEditor().checkShows("'#0074CD ' or '#CA0186 ' ")

        onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER)
            .check(
                matches(
                    VisualizeColorMatcher(
                        intArrayOf(
                            getColorValueFromColorString("'#0074CD ' "),
                            getColorValueFromColorString("'#CA0186 ' ")
                        )
                    )
                )
            )
    }

    private fun setTestColorWithColorPicker(row: Int, column: Int) {
        onView(withId(R.id.formula_editor_keyboard_color_picker))
            .perform(click())

        onColorPickerPresetButton(row, column)
            .perform(click())

        closeSoftKeyboard()

        onView(withText(R.string.color_picker_apply))
            .perform(click())
    }

    private fun getColorValueFromColorString(colorString: String): Int {
        val newString = colorString.replace(Regex("[^A-Za-z0-9]"), "")
        return try {
            newString.toInt(COLOR_STRING_CONVERSION_CONSTANT)
        } catch (nfe: NumberFormatException) {
            0
        }
    }
}

class VisualizeColorMatcher(private val expectedColors: IntArray) :
    TypeSafeMatcher<View?>(View::class.java) {

    override fun matchesSafely(item: View?): Boolean {
        val formulaTextView = item as TextView
        val spannableString = formulaTextView.text as Spannable
        val spans: Array<VisualizeColorImageSpan> = spannableString.getSpans(
            0,
            spannableString.length,
            VisualizeColorImageSpan::class.java
        )
        if (expectedColors.size != spans.size) {
            return false
        }
        val expectedColorsIterator = expectedColors.iterator()
        for (span in spans) {
            if (span.colorValue != expectedColorsIterator.next()) {
                return false
            }
        }
        return true
    }

    override fun describeTo(description: Description) {
        description.appendText("")
    }
}
