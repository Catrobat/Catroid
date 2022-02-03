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

package org.catrobat.catroid.uiespresso.formulaeditor

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

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId

@Category(AppUi::class, Smoke::class)
@RunWith(Parameterized::class)
class FormulaEditorTextListTest(
    private val formulaEditorFunction: String,
    private val formulaEditorFunctionParameter: String,
    private val name: String
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
    fun testTextListElements() {
        BrickDataInteractionWrapper.onBrickAtPosition(whenBrickPosition)
            .checkShowsText(R.string.brick_when_started)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .checkShowsText(R.string.brick_change_size_by)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .onChildView(withId(R.id.brick_change_size_by_edit_text))
            .perform(ViewActions.click())
        val editorFunction = formulaEditorFunction + formulaEditorFunctionParameter
        val selectedFunctionString = getSelectedFunctionString(editorFunction)
        FormulaEditorWrapper.onFormulaEditor()
            .performOpenCategory(FormulaEditorWrapper.Category.TEXT)
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
            paramsData.addAll(listOfSensorsSpeechRecognition)
            paramsData.addAll(listOfSensorTextRecognition)
            paramsData.addAll(listOfStringFunctions)
            paramsData.addAll(listOfTextPropertiesFunctions)
            paramsData.addAll(listOfSensorsColorAtXY)
            paramsData.addAll(listOfSensorUserLanguage)
            return paramsData
        }

        private val listOfSensorsSpeechRecognition = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_listening_language_sensor),
                str(R.string.formula_editor_function_no_parameter),
                "listening language"
            )
        )

        private val listOfSensorTextRecognition = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_sensor_text_from_camera),
                str(R.string.formula_editor_function_no_parameter),
                "text from camera"
            ),
            arrayOf(
                str(R.string.formula_editor_sensor_text_blocks_number),
                str(R.string.formula_editor_function_no_parameter),
                "number of text blocks"
            ),
            arrayOf(
                str(R.string.formula_editor_function_text_block_x),
                str(R.string.formula_editor_function_text_block_parameter),
                "text block x from camera"
            ),
            arrayOf(
                str(R.string.formula_editor_function_text_block_y),
                str(R.string.formula_editor_function_text_block_parameter),
                "text block y from camera"
            ),
            arrayOf(
                str(R.string.formula_editor_function_text_block_size),
                str(R.string.formula_editor_function_text_block_parameter),
                "text block size from camera"
            ),
            arrayOf(
                str(R.string.formula_editor_function_text_block_from_camera),
                str(R.string.formula_editor_function_text_block_parameter),
                "text block from camera"
            ),
            arrayOf(
                str(R.string.formula_editor_function_text_block_language_from_camera),
                str(R.string.formula_editor_function_text_block_parameter),
                "text block language from camera"
            )
        )

        private val listOfStringFunctions = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_function_length),
                str(R.string.formula_editor_function_length_parameter),
                "length"
            ),
            arrayOf(
                str(R.string.formula_editor_function_letter),
                str(R.string.formula_editor_function_letter_parameter),
                "letter"
            ),
            arrayOf(
                str(R.string.formula_editor_function_join),
                str(R.string.formula_editor_function_join_parameter),
                "join"
            ),
            arrayOf(
                str(R.string.formula_editor_function_join3),
                str(R.string.formula_editor_function_join3_parameter),
                "join3"
            ),
            arrayOf(
                str(R.string.formula_editor_function_regex),
                str(R.string.formula_editor_function_regex_parameter),
                "regular expression"
            )
        )

        private val listOfTextPropertiesFunctions = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_object_look_name),
                str(R.string.formula_editor_function_no_parameter),
                "look name"
            ),
            arrayOf(
                str(R.string.formula_editor_object_background_number),
                str(R.string.formula_editor_function_no_parameter),
                "background number"
            ),
            arrayOf(
                str(R.string.formula_editor_object_background_name),
                str(R.string.formula_editor_function_no_parameter),
                "background name"
            )
        )

        private val listOfSensorsColorAtXY = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_sensor_color_at_x_y),
                str(R.string.formula_editor_sensor_color_at_x_y_parameter),
                "color at xy"
            )
        )

        private val listOfSensorUserLanguage = arrayListOf(
            arrayOf(
                str(R.string.formula_editor_sensor_user_language),
                str(R.string.formula_editor_function_no_parameter),
                "user language"
            )
        )
    }
}
