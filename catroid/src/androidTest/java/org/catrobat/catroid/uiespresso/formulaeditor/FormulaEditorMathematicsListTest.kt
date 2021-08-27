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
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
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
import org.junit.runners.Parameterized.Parameters

@Category(AppUi::class, Smoke::class)
@RunWith(Parameterized::class)
class FormulaEditorMathematicsListTest(
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
    fun testMathematicsListElements() {
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
            .performOpenCategory(FormulaEditorWrapper.Category.MATHEMATICS)
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
        @Parameters(name = "{2}" + "-Test")
        fun params(): ArrayList<Array<String>> {
            val paramsData = ArrayList<Array<String>>()
            paramsData.addAll(listOfTrigonometric)
            paramsData.addAll(listOfFirstMisc)
            paramsData.addAll(listOfArcs)
            paramsData.addAll(listOfSecondMisc)
            return paramsData
        }

        private val listOfTrigonometric = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_function_sin),
                str(R.string.formula_editor_function_sin_parameter),
                "sine"
            ),
            arrayOf(
                str(R.string.formula_editor_function_cos),
                str(R.string.formula_editor_function_cos_parameter),
                "cosine"
            ),
            arrayOf(
                str(R.string.formula_editor_function_tan),
                str(R.string.formula_editor_function_tan_parameter),
                "tangent"
            )
        )

        private val listOfFirstMisc = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_function_ln),
                str(R.string.formula_editor_function_ln_parameter),
                "natural logarithm"
            ),
            arrayOf(
                str(R.string.formula_editor_function_log),
                str(R.string.formula_editor_function_log_parameter),
                "decimal logarithm"
            ),
            arrayOf(
                str(R.string.formula_editor_function_pi),
                str(R.string.formula_editor_function_pi_parameter),
                "pi"
            ),
            arrayOf(
                str(R.string.formula_editor_function_sqrt),
                str(R.string.formula_editor_function_sqrt_parameter),
                "square root"
            ),
            arrayOf(
                str(R.string.formula_editor_function_rand),
                str(R.string.formula_editor_function_rand_parameter),
                "random value from to"
            ),
            arrayOf(
                str(R.string.formula_editor_function_abs),
                str(R.string.formula_editor_function_abs_parameter),
                "absolute value"
            ),
            arrayOf(
                str(R.string.formula_editor_function_round),
                str(R.string.formula_editor_function_round_parameter),
                "round"
            ),
            arrayOf(
                str(R.string.formula_editor_function_mod),
                str(R.string.formula_editor_function_mod_parameter),
                "modulo"
            )
        )

        private val listOfArcs = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_function_arcsin),
                str(R.string.formula_editor_function_arcsin_parameter),
                "arcsine"
            ),
            arrayOf(
                str(R.string.formula_editor_function_arccos),
                str(R.string.formula_editor_function_arccos_parameter),
                "arccosine"
            ),
            arrayOf(
                str(R.string.formula_editor_function_arctan),
                str(R.string.formula_editor_function_arctan_parameter),
                "arctangent"
            ),
            arrayOf(
                str(R.string.formula_editor_function_arctan2),
                str(R.string.formula_editor_function_arctan2_parameter),
                "arctangent2"
            )
        )

        private val listOfSecondMisc = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_function_exp),
                str(R.string.formula_editor_function_exp_parameter),
                "exponent"
            ),
            arrayOf(
                str(R.string.formula_editor_function_power),
                str(R.string.formula_editor_function_power_parameter),
                "power"
            ),
            arrayOf(
                str(R.string.formula_editor_function_floor),
                str(R.string.formula_editor_function_floor_parameter),
                "floor"
            ),
            arrayOf(
                str(R.string.formula_editor_function_ceil),
                str(R.string.formula_editor_function_ceil_parameter),
                "ceil"
            ),
            arrayOf(
                str(R.string.formula_editor_function_max),
                str(R.string.formula_editor_function_max_parameter),
                "maximum of"
            ),
            arrayOf(
                str(R.string.formula_editor_function_min),
                str(R.string.formula_editor_function_min_parameter),
                "minimum of"
            ),
            arrayOf(
                str(R.string.formula_editor_function_if_then_else),
                str(R.string.formula_editor_function_if_then_else_parameter),
                "if then else"
            )
        )
    }
}
