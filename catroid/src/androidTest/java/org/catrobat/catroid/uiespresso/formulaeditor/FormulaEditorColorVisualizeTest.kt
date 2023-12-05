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
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.formulaeditor.VisualizeColorImageSpan
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.ColorPickerInteractionWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent

class FormulaEditorColorVisualizeTest {

    private val COLOR_STRING_CONVERSION_CONSTANT = 16

    val projectManager: ProjectManager by KoinJavaComponent.inject(ProjectManager::class.java)

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
        Espresso.onView(ViewMatchers.withId(R.id.brick_set_color_edit_text))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.formula_editor_keyboard_color_picker))
            .perform(ViewActions.click())

        ColorPickerInteractionWrapper.onColorPickerPresetButton(0, 0)
            .perform(ViewActions.click())

        Espresso.closeSoftKeyboard()

        Espresso.onView(ViewMatchers.withText(R.string.color_picker_apply))
            .perform(ViewActions.click())

        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.brick_set_color_edit_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("'#0074CD ' ")))

        Espresso.onView(ViewMatchers.withId(R.id.brick_set_color_edit_text))
            .check(ViewAssertions.matches(VisualizeColorMatcher(
                arrayOf( getColorValueFromColorString("'#0074CD ' ")))))
    }

    @Test
    fun visualizeColorInFormulaEditor() {
        Espresso.onView(ViewMatchers.withId(R.id.brick_set_color_edit_text))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.formula_editor_keyboard_color_picker))
            .perform(ViewActions.click())

        ColorPickerInteractionWrapper.onColorPickerPresetButton(0, 0)
            .perform(ViewActions.click())

        Espresso.closeSoftKeyboard()

        Espresso.onView(ViewMatchers.withText(R.string.color_picker_apply))
            .perform(ViewActions.click())

        FormulaEditorWrapper.onFormulaEditor().checkShows("'#0074CD ' ")

        Espresso.onView(FormulaEditorWrapper.FORMULA_EDITOR_TEXT_FIELD_MATCHER)
            .check(ViewAssertions.matches(VisualizeColorMatcher(
                arrayOf( getColorValueFromColorString("'#0074CD ' ")))))
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
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

class VisualizeColorMatcher(private val expectedColors: Array<Int>) :
    TypeSafeMatcher<View?>(View::class.java) {
    var resourceName: String? = null

    override fun matchesSafely(item: View?): Boolean {
        var formulaTextView = item as TextView
        var spannableString = formulaTextView.text as Spannable
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
        description.appendText("test")
    }


}