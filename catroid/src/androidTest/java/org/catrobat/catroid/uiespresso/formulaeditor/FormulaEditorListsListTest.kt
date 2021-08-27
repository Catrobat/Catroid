/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId

import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@Category(Cat.AppUi::class, Level.Smoke::class)
@RunWith(Parameterized::class)
class FormulaEditorListsListTest(
    private val formulaEditorFunction: String,
    private val formulaEditorFunctionParameter: String,
    @Suppress("unused")
    private val testName: String
) {
    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    private val whenBrickPosition = 0
    private val changeSizeBrickPosition = 1

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorFunctionListTest")
        script.addBrick(ChangeSizeByNBrick(0.0))
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testListsListElements() {
        BrickDataInteractionWrapper.onBrickAtPosition(whenBrickPosition)
            .checkShowsText(R.string.brick_when_started)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .checkShowsText(R.string.brick_change_size_by)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .onChildView(withId(R.id.brick_change_size_by_edit_text))
            .perform(click())
        val editorFunction = formulaEditorFunction + formulaEditorFunctionParameter
        var selectedFunctionString = getSelectedFunctionString(editorFunction)

        val listVariableName = "myList"
        val replaceListNameRegexString = "(?<=\\*).*(?=\\*)"
        selectedFunctionString =
            selectedFunctionString.replace(replaceListNameRegexString.toRegex(), listVariableName)

        onFormulaEditor()
            .performOpenCategory(FormulaEditorWrapper.Category.LISTS)
            .performSelect(editorFunction)

        onView(withId(R.id.input_edit_text)).perform(clearText(), typeText(listVariableName))
        onView(withId(android.R.id.button1)).perform(click())

        onFormulaEditor().checkShows(selectedFunctionString)
    }

    private fun getSelectedFunctionString(functionString: String): String {
        return functionString
            .replace("^(.+?)\\(".toRegex(), "$1( ")
            .replace(",", " , ")
            .replace("-", "- ")
            .replace("\\)$".toRegex(), " )") + " "
    }

    companion object {
        private fun str(paramId: Int): String = UiTestUtils.getResourcesString(paramId) ?: ""

        @JvmStatic
        @Parameterized.Parameters(name = "{2}" + "-Test")
        fun params() = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_function_number_of_items),
                str(R.string.formula_editor_function_number_of_items_parameter),
                "number of items"
            ),
            arrayOf(
                str(R.string.formula_editor_function_list_item),
                str(R.string.formula_editor_function_list_item_parameter),
                "list item"
            ),
            arrayOf(
                str(R.string.formula_editor_function_contains),
                str(R.string.formula_editor_function_contains_parameter),
                "list contains"
            ),
            arrayOf(
                str(R.string.formula_editor_function_index_of_item),
                str(R.string.formula_editor_function_index_of_item_parameter),
                "list item's index"
            ),
            arrayOf(
                str(R.string.formula_editor_function_flatten),
                str(R.string.formula_editor_function_flatten_parameter),
                "list flatten"
            )
        )
    }
}
