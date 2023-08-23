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

package org.catrobat.catroid.uiespresso.ui.dialog

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.userbrick.UserDefinedBrickData
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.java.KoinJavaComponent.inject

@RunWith(Parameterized::class)
class FormulaEditorComputeDialogUserDefinedBrickInputTest(
    private val name: String,
    private val formula: Formula,
    private val expectedString: String
) {
    private lateinit var userDefinedBrick: UserDefinedBrick
    private val projectManager by inject(ProjectManager::class.java)

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        UiTestUtils.createProjectAndGetStartScript(projectName)

        val input = UserDefinedBrickInput("Input")
        input.value = formula
        val userDefinedScript = UserDefinedScript()
        val label = UserDefinedBrickLabel("Label")
        userDefinedBrick = UserDefinedBrick(mutableListOf<UserDefinedBrickData>(label, input))
        userDefinedBrick.setCallingBrick(true)
        userDefinedBrick.formulaMap.putIfAbsent(input.inputFormulaField, formula)
        userDefinedScript.setUserDefinedBrickInputs(listOf(input))
        userDefinedScript.scriptBrick = UserDefinedReceiverBrick(userDefinedBrick)
        userDefinedScript.addBrick(
            ChangeSizeByNBrick(Formula(FormulaElement(USER_DEFINED_BRICK_INPUT, input.name, null)))
        )
        projectManager.currentSprite.addScript(userDefinedScript)
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun userDefinedBrickInputTest() {
        openComputeDialog()
        onView(withId(R.id.formula_editor_compute_dialog_textview)).check(
            matches(ViewMatchers.withText(expectedString))
        )
    }

    private fun openComputeDialog() {
        onView(withId(R.id.brick_change_size_by_edit_text)).perform(click())
        onFormulaEditor().performCompute()
    }

    companion object {
        private val applicationContext: Context = ApplicationProvider.getApplicationContext()

        private val trueString = applicationContext.getString(R.string.formula_editor_true)
        private val falseString = applicationContext.getString(R.string.formula_editor_false)

        private const val projectName = "DataListFragmentBooleanUserVariablesTest"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf(
                "Boolean false",
                Formula(
                    FormulaElement(
                        FormulaElement.ElementType.FUNCTION,
                        Functions.FALSE.toString(),
                        null
                    )
                ),
                falseString
            ),
            arrayOf(
                "Boolean true",
                Formula(
                    FormulaElement(
                        FormulaElement.ElementType.FUNCTION,
                        Functions.TRUE.toString(),
                        null
                    )
                ),
                trueString
            ),
            arrayOf("Int 1", Formula(1), "1"),
            arrayOf("Int 1000", Formula(1_000), "1000"),
            arrayOf("Int 1000000", Formula(1_000_000), "1000000"),
            arrayOf("Double 1.1", Formula(1.1), "1.1"),
            arrayOf("String hello", Formula("hello"), "hello")
        )
    }
}
