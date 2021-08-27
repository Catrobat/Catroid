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

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers

import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
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
class FormulaEditorLogicListTest(
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
    fun testLogicListElements() {
        BrickDataInteractionWrapper.onBrickAtPosition(whenBrickPosition)
            .checkShowsText(R.string.brick_when_started)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .checkShowsText(R.string.brick_change_size_by)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .onChildView(ViewMatchers.withId(R.id.brick_change_size_by_edit_text))
            .perform(ViewActions.click())
        val editorFunction = formulaEditorFunction + formulaEditorFunctionParameter
        val selectedFunctionString = getSelectedFunctionString(editorFunction)
        FormulaEditorWrapper.onFormulaEditor()
            .performOpenCategory(FormulaEditorWrapper.Category.LOGIC)
            .performSelect(editorFunction)
        FormulaEditorWrapper.onFormulaEditor()
            .checkShows(selectedFunctionString)
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
        fun params(): ArrayList<Array<String>> {
            val paramsData = ArrayList<Array<String>>()
            paramsData.addAll(listOfBool)
            paramsData.addAll(listOfComparison)
            paramsData.addAll(listOfReporters)
            return paramsData
        }

        private val listOfBool = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_logic_and),
                str(R.string.formula_editor_function_no_parameter),
                "and"
            ),
            arrayOf(
                str(R.string.formula_editor_logic_or),
                str(R.string.formula_editor_function_no_parameter),
                "or"
            ),
            arrayOf(
                str(R.string.formula_editor_logic_not),
                str(R.string.formula_editor_function_no_parameter),
                "not"
            ),
            arrayOf(
                str(R.string.formula_editor_function_true),
                str(R.string.formula_editor_function_no_parameter),
                "true"
            ),
            arrayOf(
                str(R.string.formula_editor_function_false),
                str(R.string.formula_editor_function_no_parameter),
                "false"
            )
        )

        private val listOfComparison = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_logic_equal),
                str(R.string.formula_editor_function_no_parameter),
                "equal"
            ),
            arrayOf(
                str(R.string.formula_editor_logic_notequal),
                str(R.string.formula_editor_function_no_parameter),
                "not equal"
            ),
            arrayOf(
                str(R.string.formula_editor_logic_lesserthan),
                str(R.string.formula_editor_function_no_parameter),
                "lesser than"
            ),
            arrayOf(
                str(R.string.formula_editor_logic_leserequal),
                str(R.string.formula_editor_function_no_parameter),
                "lesser equal"
            ),
            arrayOf(
                str(R.string.formula_editor_logic_greaterthan),
                str(R.string.formula_editor_function_no_parameter),
                "greater than"
            ),
            arrayOf(
                str(R.string.formula_editor_logic_greaterequal),
                str(R.string.formula_editor_function_no_parameter),
                "greater equal"
            )
        )

        private val listOfReporters = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_function_if_then_else),
                str(R.string.formula_editor_function_if_then_else_parameter),
                "if then else"
            ),
            arrayOf(
                str(R.string.formula_editor_function_collides_with_edge),
                str(R.string.formula_editor_function_no_parameter),
                "touches edge"
            ),
            arrayOf(
                str(R.string.formula_editor_function_touched),
                str(R.string.formula_editor_function_no_parameter),
                "touches finger"
            ),
            arrayOf(
                str(R.string.formula_editor_function_collides_with_color),
                str(R.string.formula_editor_function_collides_with_color_parameter),
                "touches color"
            ),
            arrayOf(
                str(R.string.formula_editor_function_color_touches_color),
                str(R.string.formula_editor_function_color_touches_color_parameter),
                "color touches color"
            ),
            arrayOf(
                str(R.string.formula_editor_function_is_finger_touching),
                str(R.string.formula_editor_function_no_parameter),
                "stage is touched"
            ),
            arrayOf(
                str(R.string.formula_editor_function_is_multi_finger_touching),
                str(R.string.formula_editor_function_touch_parameter),
                "multi finger stage touched"
            ),
            arrayOf(
                str(R.string.formula_editor_sensor_face_detected),
                str(R.string.formula_editor_function_no_parameter),
                "first face detected"
            ),
            arrayOf(
                str(R.string.formula_editor_sensor_second_face_detected),
                str(R.string.formula_editor_function_no_parameter),
                "second face detected"
            )
        )
    }
}
