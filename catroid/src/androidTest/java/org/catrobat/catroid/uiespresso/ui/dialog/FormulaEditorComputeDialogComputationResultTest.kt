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
package org.catrobat.catroid.uiespresso.ui.dialog

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.BRACKET
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.OPERATOR
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.STRING
import org.catrobat.catroid.formulaeditor.Functions.FALSE
import org.catrobat.catroid.formulaeditor.Functions.TRUE
import org.catrobat.catroid.formulaeditor.Operators.LOGICAL_AND
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@Category(AppUi::class, Smoke::class)
@RunWith(Parameterized::class)
class FormulaEditorComputeDialogComputationResultTest(
    private val name: String,
    private val formula: Formula,
    private val userListElements: List<Any>?,
    private val expectedString: String
) {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
    }

    @Test
    fun testComputeDialogValue() {
        openComputeDialog()
        onView(withId(R.id.formula_editor_compute_dialog_textview)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(expectedString)
            )
        )
    }

    private fun openComputeDialog() {
        onView(withId(R.id.brick_note_edit_text)).perform(click())
        onFormulaEditor().performCompute()
    }

    fun createProject() {
        val sprite = Sprite(spriteName)
        val script: Script = StartScript()
        val noteFormula = formula

        script.addBrick(NoteBrick(noteFormula))
        sprite.addScript(script)
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.defaultScene.addSprite(sprite)

        if (userListElements != null) {
            project.addUserList(UserList(userListName, userListElements))
        }

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
    }

    companion object {
        private lateinit var project: Project
        private const val projectName = "formulaEditorComputeDialogBooleanTest"
        private const val spriteName = "testSprite"
        private const val userListName = "userList"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf(
                "Brackets Number", getFormula(
                    Pair(
                        BRACKET, applicationContext.getString(R.string.formula_editor_bracket_open)
                    ), null, Pair(NUMBER, 5)
                ), null, "5"
            ), arrayOf(
                "Number Double", getFormula(Pair(NUMBER, 1.234.toString())), null, "1.234"
            ), arrayOf(
                "String empty", getFormula(Pair(STRING, "")), null, ""
            ), arrayOf(
                "String Hello", getFormula(Pair(STRING, "Hello")), null, "Hello"
            ), arrayOf(
                "Operator LOGICAL_AND 1 1",
                getFormula(Pair(OPERATOR, LOGICAL_AND.name), Pair(NUMBER, 1), Pair(NUMBER, 1)),
                null,
                trueString
            ), arrayOf(
                "Function TRUE", getFormula(Pair(FUNCTION, TRUE.name)), null, trueString
            ), arrayOf(
                "Function FALSE", getFormula(Pair(FUNCTION, FALSE.name)), null, falseString
            ), arrayOf(
                "User List boolean combination",
                getFormula(Pair(ElementType.USER_LIST, userListName)),
                listOf(
                    false,
                    true,
                    false,
                    true,
                    false,
                    false,
                    false,
                    true,
                    true,
                    true,
                    false,
                    true,
                    true,
                    true,
                    false,
                    false,
                    false
                ),
                "$falseString $trueString $falseString $trueString $falseString $falseString " + "$falseString $trueString $trueString $trueString $falseString $trueString " + "$trueString $trueString $falseString $falseString $falseString"
            )
        )

        private val applicationContext: Context =
            ApplicationProvider.getApplicationContext<Context>()
        private val trueString = applicationContext.getString(R.string.formula_editor_true)
        private val falseString = applicationContext.getString(R.string.formula_editor_false)

        private fun getFormula(
            parentPair: Pair<ElementType, Any>,
            leftChildPair: Pair<ElementType, Any>? = null,
            rightChildPair: Pair<ElementType, Any>? = null
        ): Formula {
            val parentFormulaElement = pairToFormulaElement(parentPair, null)

            if (leftChildPair != null) {
                val leftFormulaElement = pairToFormulaElement(leftChildPair, parentFormulaElement)
                parentFormulaElement.setLeftChild(leftFormulaElement)
            }
            if (rightChildPair != null) {
                val rightFormulaElement = pairToFormulaElement(rightChildPair, parentFormulaElement)
                parentFormulaElement.setRightChild(rightFormulaElement)
            }

            return Formula(parentFormulaElement)
        }

        private fun pairToFormulaElement(
            typeValuePair: Pair<ElementType, Any>,
            parentFormulaElement: FormulaElement?
        ): FormulaElement {
            val type = typeValuePair.first
            val value = typeValuePair.second
            return FormulaElement(type, value.toString(), parentFormulaElement)
        }
    }
}
