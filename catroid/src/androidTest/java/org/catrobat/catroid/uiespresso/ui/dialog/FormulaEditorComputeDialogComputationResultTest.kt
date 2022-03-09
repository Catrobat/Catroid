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
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.COLLISION_FORMULA
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.OPERATOR
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.STRING
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.USER_LIST
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.USER_VARIABLE
import org.catrobat.catroid.formulaeditor.Functions.ABS
import org.catrobat.catroid.formulaeditor.Functions.ARCCOS
import org.catrobat.catroid.formulaeditor.Functions.ARCSIN
import org.catrobat.catroid.formulaeditor.Functions.ARCTAN
import org.catrobat.catroid.formulaeditor.Functions.ARCTAN2
import org.catrobat.catroid.formulaeditor.Functions.CEIL
import org.catrobat.catroid.formulaeditor.Functions.COLLIDES_WITH_COLOR
import org.catrobat.catroid.formulaeditor.Functions.COLOR_AT_XY
import org.catrobat.catroid.formulaeditor.Functions.COLOR_EQUALS_COLOR
import org.catrobat.catroid.formulaeditor.Functions.COLOR_TOUCHES_COLOR
import org.catrobat.catroid.formulaeditor.Functions.CONTAINS
import org.catrobat.catroid.formulaeditor.Functions.COS
import org.catrobat.catroid.formulaeditor.Functions.EXP
import org.catrobat.catroid.formulaeditor.Functions.FALSE
import org.catrobat.catroid.formulaeditor.Functions.FLATTEN
import org.catrobat.catroid.formulaeditor.Functions.FLOOR
import org.catrobat.catroid.formulaeditor.Functions.IF_THEN_ELSE
import org.catrobat.catroid.formulaeditor.Functions.INDEX_CURRENT_TOUCH
import org.catrobat.catroid.formulaeditor.Functions.JOIN
import org.catrobat.catroid.formulaeditor.Functions.JOIN3
import org.catrobat.catroid.formulaeditor.Functions.LENGTH
import org.catrobat.catroid.formulaeditor.Functions.LETTER
import org.catrobat.catroid.formulaeditor.Functions.LIST_ITEM
import org.catrobat.catroid.formulaeditor.Functions.LN
import org.catrobat.catroid.formulaeditor.Functions.LOG
import org.catrobat.catroid.formulaeditor.Functions.MAX
import org.catrobat.catroid.formulaeditor.Functions.MIN
import org.catrobat.catroid.formulaeditor.Functions.MOD
import org.catrobat.catroid.formulaeditor.Functions.MULTI_FINGER_TOUCHED
import org.catrobat.catroid.formulaeditor.Functions.MULTI_FINGER_X
import org.catrobat.catroid.formulaeditor.Functions.MULTI_FINGER_Y
import org.catrobat.catroid.formulaeditor.Functions.NUMBER_OF_ITEMS
import org.catrobat.catroid.formulaeditor.Functions.POWER
import org.catrobat.catroid.formulaeditor.Functions.RASPIDIGITAL
import org.catrobat.catroid.formulaeditor.Functions.REGEX
import org.catrobat.catroid.formulaeditor.Functions.ROUND
import org.catrobat.catroid.formulaeditor.Functions.SIN
import org.catrobat.catroid.formulaeditor.Functions.SQRT
import org.catrobat.catroid.formulaeditor.Functions.TAN
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_FROM_CAMERA
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_LANGUAGE_FROM_CAMERA
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_SIZE
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_X
import org.catrobat.catroid.formulaeditor.Functions.TEXT_BLOCK_Y
import org.catrobat.catroid.formulaeditor.Functions.TRUE
import org.catrobat.catroid.formulaeditor.Operators.DIVIDE
import org.catrobat.catroid.formulaeditor.Operators.EQUAL
import org.catrobat.catroid.formulaeditor.Operators.GREATER_OR_EQUAL
import org.catrobat.catroid.formulaeditor.Operators.GREATER_THAN
import org.catrobat.catroid.formulaeditor.Operators.LOGICAL_AND
import org.catrobat.catroid.formulaeditor.Operators.LOGICAL_NOT
import org.catrobat.catroid.formulaeditor.Operators.LOGICAL_OR
import org.catrobat.catroid.formulaeditor.Operators.MINUS
import org.catrobat.catroid.formulaeditor.Operators.MULT
import org.catrobat.catroid.formulaeditor.Operators.NOT_EQUAL
import org.catrobat.catroid.formulaeditor.Operators.PLUS
import org.catrobat.catroid.formulaeditor.Operators.POW
import org.catrobat.catroid.formulaeditor.Operators.SMALLER_OR_EQUAL
import org.catrobat.catroid.formulaeditor.Operators.SMALLER_THAN
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
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
import org.koin.java.KoinJavaComponent.inject

@SuppressWarnings("LargeClass")
@Category(AppUi::class, Smoke::class)
@RunWith(Parameterized::class)
class FormulaEditorComputeDialogComputationResultTest(
    private val name: String,
    private val formula: Formula,
    private val userVariableLeftValue: Any?,
    private val userVariableRightValue: Any?,
    private val userListLeftElements: List<Any>?,
    private val userListRightElements: List<Any>?,
    private val expectedString: String
) {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
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
        onView(withId(R.id.formula_editor_compute_dialog_textview))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedString)))
    }

    private fun openComputeDialog() {
        onView(withId(R.id.brick_note_edit_text))
            .perform(click())
        onFormulaEditor()
            .performCompute()
    }

    fun createProject() {
        val sprite = Sprite(spriteName)
        val collisionSprite = Sprite(collisionSpriteName)
        val script: Script = StartScript()
        val noteFormula = formula

        script.addBrick(NoteBrick(noteFormula))
        sprite.addScript(script)
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        project.defaultScene.addSprite(sprite)
        project.defaultScene.addSprite(collisionSprite)

        if (userVariableLeftValue != null) {
            project.addUserVariable(UserVariable(userVariableLeftName, userVariableLeftValue))
        }
        if (userVariableRightValue != null) {
            project.addUserVariable(UserVariable(userVariableRightName, userVariableRightValue))
        }
        if (userListLeftElements != null) {
            project.addUserList(UserList(userListLeftName, userListLeftElements))
        }
        if (userListRightElements != null) {
            project.addUserList(UserList(userListRightName, userListRightElements))
        }

        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
    }

    companion object {
        private lateinit var project: Project
        private const val projectName = "formulaEditorComputeDialogBooleanTest"
        private const val spriteName = "testSprite"
        private const val collisionSpriteName = "testCollisionSprite"
        private const val userListLeftName = "userListLeft"
        private const val userListRightName = "userListRight"
        private const val userVariableLeftName = "userVariableLeft"
        private const val userVariableRightName = "userVariableRight"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            *listOfBracketFormulas,
            *listOfNumberFormulas,
            *listOfStringFormulas,
            *listOfOperatorFormulas,
            *listOfBooleanOperatorFormulas,
            *listOfFunctionFormulas,
            *listOfBooleanFunctionFormulas,
            *listOfUserDataFormulas,
            *listOfBooleanUserDataFormulas,
            *listOfCollisionFormulas
        )

        private val applicationContext: Context =
            ApplicationProvider.getApplicationContext<Context>()
        private val trueString = applicationContext.getString(R.string.formula_editor_true)
        private val falseString = applicationContext.getString(R.string.formula_editor_false)

        private val listOfBracketFormulas =
            arrayOf(
                arrayOf(
                    "Brackets Number",
                    getFormula(
                        Pair(
                            BRACKET,
                            applicationContext.getString(R.string.formula_editor_bracket_open)
                        ),
                        null,
                        Pair(NUMBER, 5)
                    ),
                    null, null, null, null,
                    "5"
                )
            )

        private val listOfNumberFormulas =
            arrayOf(
                arrayOf(
                    "Number Int", getFormula(Pair(NUMBER, 5.toString())),
                    null, null, null, null,
                    "5"
                ),
                arrayOf(
                    "Number Int 1000", getFormula(Pair(NUMBER, 1_000.toString())),
                    null, null, null, null,
                    "1000"
                ),
                arrayOf(
                    "Number Double", getFormula(Pair(NUMBER, 1.234.toString())),
                    null, null, null, null,
                    "1.234"
                )
            )

        private val listOfStringFormulas =
            arrayOf(
                arrayOf(
                    "String empty", getFormula(Pair(STRING, "")),
                    null, null, null, null,
                    ""
                ),
                arrayOf(
                    "String Hello", getFormula(Pair(STRING, "Hello")),
                    null, null, null, null,
                    "Hello"
                ),
                arrayOf(
                    "String 1000", getFormula(Pair(STRING, "1000")),
                    null, null, null, null,
                    "1000"
                )
            )

        private val listOfOperatorFormulas =
            arrayOf(
                arrayOf(
                    "Operator PLUS Ints",
                    getFormula(Pair(OPERATOR, PLUS.name), Pair(NUMBER, 123), Pair(NUMBER, 111)),
                    null, null, null, null,
                    "234"
                ),
                arrayOf(
                    "Operator PLUS Ints 1000",
                    getFormula(Pair(OPERATOR, PLUS.name), Pair(NUMBER, 700), Pair(NUMBER, 300)),
                    null, null, null, null,
                    "1000"
                ),
                arrayOf(
                    "Operator Minus Ints",
                    getFormula(Pair(OPERATOR, MINUS.name), Pair(NUMBER, 123), Pair(NUMBER, 234)),
                    null, null, null, null,
                    "-111"
                ),
                arrayOf(
                    "Operator Minus Ints -1000000",
                    getFormula(
                        Pair(OPERATOR, MINUS.name), Pair(NUMBER, 300), Pair(NUMBER, 1_000_300)
                    ),
                    null, null, null, null,
                    "-1000000"
                ),
                arrayOf(
                    "Operator MULT Ints",
                    getFormula(Pair(OPERATOR, MULT.name), Pair(NUMBER, 5), Pair(NUMBER, -7)),
                    null, null, null, null,
                    "-35"
                ),
                arrayOf(
                    "Operator DIVIDE Ints",
                    getFormula(Pair(OPERATOR, DIVIDE.name), Pair(NUMBER, -35), Pair(NUMBER, 7)),
                    null, null, null, null,
                    "-5"
                ),
                arrayOf(
                    "Operator POW Ints",
                    getFormula(Pair(OPERATOR, POW.name), Pair(NUMBER, 2), Pair(NUMBER, 5)),
                    null, null, null, null,
                    "32"
                )
            )

        private val listOfBooleanOperatorFormulas =
            arrayOf(
                arrayOf(
                    "Operator LOGICAL_NOT 1",
                    getFormula(Pair(OPERATOR, LOGICAL_NOT.name), null, Pair(NUMBER, 1)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator LOGICAL_NOT 0",
                    getFormula(Pair(OPERATOR, LOGICAL_NOT.name), null, Pair(NUMBER, 0)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator LOGICAL_AND 0 0",
                    getFormula(Pair(OPERATOR, LOGICAL_AND.name), Pair(NUMBER, 0), Pair(NUMBER, 0)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator LOGICAL_AND 0 1",
                    getFormula(Pair(OPERATOR, LOGICAL_AND.name), Pair(NUMBER, 0), Pair(NUMBER, 1)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator LOGICAL_AND 1 0",
                    getFormula(Pair(OPERATOR, LOGICAL_AND.name), Pair(NUMBER, 1), Pair(NUMBER, 0)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator LOGICAL_AND 1 1",
                    getFormula(Pair(OPERATOR, LOGICAL_AND.name), Pair(NUMBER, 1), Pair(NUMBER, 1)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator LOGICAL_OR 0 0",
                    getFormula(Pair(OPERATOR, LOGICAL_OR.name), Pair(NUMBER, 0), Pair(NUMBER, 0)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator LOGICAL_OR 0 1",
                    getFormula(Pair(OPERATOR, LOGICAL_OR.name), Pair(NUMBER, 0), Pair(NUMBER, 1)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator LOGICAL_OR 1 0",
                    getFormula(Pair(OPERATOR, LOGICAL_OR.name), Pair(NUMBER, 1), Pair(NUMBER, 0)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator LOGICAL_OR 1 1",
                    getFormula(Pair(OPERATOR, LOGICAL_OR.name), Pair(NUMBER, 1), Pair(NUMBER, 1)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator EQUAL 1 1",
                    getFormula(Pair(OPERATOR, EQUAL.name), Pair(NUMBER, 1), Pair(NUMBER, 1)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator EQUAL 0 1",
                    getFormula(Pair(OPERATOR, EQUAL.name), Pair(NUMBER, 0), Pair(NUMBER, 1)),
                    null, null, null, null,
                    falseString
                ),

                arrayOf(
                    "Operator NOT_EQUAL 1 1",
                    getFormula(Pair(OPERATOR, NOT_EQUAL.name), Pair(NUMBER, 1), Pair(NUMBER, 1)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator NOT_EQUAL 0 1",
                    getFormula(Pair(OPERATOR, NOT_EQUAL.name), Pair(NUMBER, 0), Pair(NUMBER, 1)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator SMALLER_OR_EQUAL 1 1",
                    getFormula(
                        Pair(OPERATOR, SMALLER_OR_EQUAL.name), Pair(NUMBER, 1), Pair(NUMBER, 1)
                    ),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator SMALLER_OR_EQUAL 1 0",
                    getFormula(
                        Pair(OPERATOR, SMALLER_OR_EQUAL.name), Pair(NUMBER, 1), Pair(NUMBER, 0)
                    ),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator GREATER_OR_EQUAL 1 1",
                    getFormula(
                        Pair(OPERATOR, GREATER_OR_EQUAL.name), Pair(NUMBER, 1), Pair(NUMBER, 1)
                    ),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator GREATER_OR_EQUAL 0 1",
                    getFormula(
                        Pair(OPERATOR, GREATER_OR_EQUAL.name), Pair(NUMBER, 0), Pair(NUMBER, 1)
                    ),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator SMALLER_THAN 0 1",
                    getFormula(Pair(OPERATOR, SMALLER_THAN.name), Pair(NUMBER, 0), Pair(NUMBER, 1)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator SMALLER_THAN 1 1",
                    getFormula(Pair(OPERATOR, SMALLER_THAN.name), Pair(NUMBER, 1), Pair(NUMBER, 1)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Operator GREATER_THAN 0 1",
                    getFormula(Pair(OPERATOR, GREATER_THAN.name), Pair(NUMBER, 1), Pair(NUMBER, 0)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Operator GREATER_THAN 1 1",
                    getFormula(Pair(OPERATOR, GREATER_THAN.name), Pair(NUMBER, 1), Pair(NUMBER, 1)),
                    null, null, null, null,
                    falseString
                )
            )

        private val listOfFunctionFormulas =
            arrayOf(
                arrayOf(
                    "Function SIN Int",
                    getFormula(Pair(FUNCTION, SIN.name), Pair(NUMBER, 90)),
                    null, null, null, null,
                    "1"
                ),
                arrayOf(
                    "Function SIN String",
                    getFormula(Pair(FUNCTION, SIN.name), Pair(STRING, "90")),
                    null, null, null, null,
                    "1"
                ),
                arrayOf(
                    "Function COS Int",
                    getFormula(Pair(FUNCTION, COS.name), Pair(NUMBER, 180)),
                    null, null, null, null,
                    "-1"
                ),
                arrayOf(
                    "Function COS String",
                    getFormula(Pair(FUNCTION, COS.name), Pair(STRING, "180")),
                    null, null, null, null,
                    "-1"
                ),
                arrayOf(
                    "Function TAN Int",
                    getFormula(Pair(FUNCTION, TAN.name), Pair(NUMBER, 0)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function TAN String",
                    getFormula(Pair(FUNCTION, TAN.name), Pair(STRING, "0")),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function LN",
                    getFormula(Pair(FUNCTION, LN.name), Pair(NUMBER, 1)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function LOG",
                    getFormula(Pair(FUNCTION, LOG.name), Pair(NUMBER, 100)),
                    null, null, null, null,
                    "2"
                ),
                arrayOf(
                    "Function SQRT",
                    getFormula(Pair(FUNCTION, SQRT.name), Pair(NUMBER, 49)),
                    null, null, null, null,
                    "7"
                ),
                arrayOf(
                    "Function SQRT -1",
                    getFormula(Pair(FUNCTION, SQRT.name), Pair(NUMBER, -1)),
                    null, null, null, null,
                    "ERROR"
                ),
                arrayOf(
                    "Function ROUND",
                    getFormula(Pair(FUNCTION, ROUND.name), Pair(NUMBER, 4.12345)),
                    null, null, null, null,
                    "4"
                ),
                arrayOf(
                    "Function ABS",
                    getFormula(Pair(FUNCTION, ABS.name), Pair(NUMBER, -5)),
                    null, null, null, null,
                    "5"
                ),
                arrayOf(
                    "Function MOD",
                    getFormula(Pair(FUNCTION, MOD.name), Pair(NUMBER, 37), Pair(NUMBER, 5)),
                    null, null, null, null,
                    "2"
                ),
                arrayOf(
                    "Function ARCSIN",
                    getFormula(Pair(FUNCTION, ARCSIN.name), Pair(NUMBER, 1)),
                    null, null, null, null,
                    "90"
                ),
                arrayOf(
                    "Function ARCCOS",
                    getFormula(Pair(FUNCTION, ARCCOS.name), Pair(NUMBER, 0)),
                    null, null, null, null,
                    "90"
                ),
                arrayOf(
                    "Function ARCTAN",
                    getFormula(Pair(FUNCTION, ARCTAN.name), Pair(NUMBER, 1)),
                    null, null, null, null,
                    "45"
                ),
                arrayOf(
                    "Function ARCTAN2",
                    getFormula(Pair(FUNCTION, ARCTAN2.name), Pair(NUMBER, 0)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function EXP",
                    getFormula(Pair(FUNCTION, EXP.name), Pair(NUMBER, 0)),
                    null, null, null, null,
                    "1"
                ),
                arrayOf(
                    "Function POWER",
                    getFormula(Pair(FUNCTION, POWER.name), Pair(NUMBER, 2), Pair(NUMBER, 5)),
                    null, null, null, null,
                    "32"
                ),
                arrayOf(
                    "Function FLOOR",
                    getFormula(Pair(FUNCTION, FLOOR.name), Pair(NUMBER, 4.77)),
                    null, null, null, null,
                    "4"
                ),
                arrayOf(
                    "Function CEIL",
                    getFormula(Pair(FUNCTION, CEIL.name), Pair(NUMBER, 4.77)),
                    null, null, null, null,
                    "5"
                ),
                arrayOf(
                    "Function MAX",
                    getFormula(Pair(FUNCTION, MAX.name), Pair(NUMBER, 5), Pair(NUMBER, 8.3)),
                    null, null, null, null,
                    "8.3"
                ),
                arrayOf(
                    "Function MIN",
                    getFormula(Pair(FUNCTION, MIN.name), Pair(NUMBER, 5), Pair(NUMBER, 8.3)),
                    null, null, null, null,
                    "5"
                ),
                arrayOf(
                    "Function LENGTH",
                    getFormula(Pair(FUNCTION, LENGTH.name), Pair(STRING, "Hello")),
                    null, null, null, null,
                    "5"
                ),
                arrayOf(
                    "Function LETTER",
                    getFormula(Pair(FUNCTION, LETTER.name), Pair(NUMBER, 1), Pair(STRING, "Hello")),
                    null, null, null, null,
                    "H"
                ),
                arrayOf(
                    "Function JOIN",
                    getFormula(
                        Pair(FUNCTION, JOIN.name), Pair(STRING, "Hello"), Pair(STRING, " world!")
                    ),
                    null, null, null, null,
                    "Hello world!"
                ),
                arrayOf(
                    "Function JOIN3",
                    getFormula(
                        Pair(FUNCTION, JOIN3.name), Pair(STRING, "Hello"),
                        Pair(STRING, " world"), Pair(STRING, "!")
                    ),
                    null, null, null, null,
                    "Hello world!"
                ),
                arrayOf(
                    "Function REGEX",
                    getFormula(
                        Pair(FUNCTION, REGEX.name), Pair(STRING, " an? ([^ .]+)"),
                        Pair(STRING, "I am a panda")
                    ),
                    null, null, null, null,
                    "panda"
                ),
                arrayOf(
                    "Function LIST_ITEM Strings",
                    getFormula(
                        Pair(FUNCTION, LIST_ITEM.name), Pair(NUMBER, 3),
                        Pair(USER_LIST, userListLeftName)
                    ),
                    null, null, listOf("1", "2", "3", "4", "5"), null,
                    "3"
                ),
                arrayOf(
                    "Function LIST_ITEM Ints",
                    getFormula(
                        Pair(FUNCTION, LIST_ITEM.name), Pair(NUMBER, 3),
                        Pair(USER_LIST, userListLeftName)
                    ),
                    null, null, listOf(1, 2, 3, 4, 5), null,
                    "3"
                ),
                arrayOf(
                    "Function NUMBER_OF_ITEMS",
                    getFormula(
                        Pair(FUNCTION, NUMBER_OF_ITEMS.name),
                        Pair(USER_LIST, userListLeftName)
                    ),
                    null, null, listOf(1, 2, 3, 4), null,
                    "4"
                ),
                arrayOf(
                    "Function RASPIDIGITAL",
                    getFormula(Pair(FUNCTION, RASPIDIGITAL.name)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function MULTI_FINGER_X",
                    getFormula(Pair(FUNCTION, MULTI_FINGER_X.name)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function MULTI_FINGER_Y",
                    getFormula(Pair(FUNCTION, MULTI_FINGER_Y.name)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function INDEX_CURRENT_TOUCH",
                    getFormula(Pair(FUNCTION, INDEX_CURRENT_TOUCH.name)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function COLOR_AT_XY",
                    getFormula(Pair(FUNCTION, COLOR_AT_XY.name)),
                    null, null, null, null,
                    "NaN"
                ),
                arrayOf(
                    "Function TEXT_BLOCK_X",
                    getFormula(Pair(FUNCTION, TEXT_BLOCK_X.name), Pair(NUMBER, 1)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function TEXT_BLOCK_Y",
                    getFormula(Pair(FUNCTION, TEXT_BLOCK_Y.name), Pair(NUMBER, 1)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function TEXT_BLOCK_SIZE",
                    getFormula(Pair(FUNCTION, TEXT_BLOCK_SIZE.name), Pair(NUMBER, 1)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function TEXT_BLOCK_FROM_CAMERA",
                    getFormula(Pair(FUNCTION, TEXT_BLOCK_FROM_CAMERA.name), Pair(NUMBER, 1)),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function TEXT_BLOCK_LANGUAGE_FROM_CAMERA",
                    getFormula(
                        Pair(FUNCTION, TEXT_BLOCK_LANGUAGE_FROM_CAMERA.name), Pair(NUMBER, 1)
                    ),
                    null, null, null, null,
                    "0"
                ),
                arrayOf(
                    "Function IF_THEN_ELSE",
                    getFormula(
                        Pair(FUNCTION, IF_THEN_ELSE.name), Pair(FUNCTION, TRUE), Pair(NUMBER, 5),
                        Pair(NUMBER, 3)
                    ),
                    null, null, null, null,
                    "5"
                ),
                arrayOf(
                    "Function FLATTEN Strings",
                    getFormula(Pair(FUNCTION, FLATTEN.name), Pair(USER_LIST, userListLeftName)),
                    null, null, listOf("1", "2", "3", "4", "5"), null,
                    "12345"
                ),
                arrayOf(
                    "Function FLATTEN Ints",
                    getFormula(Pair(FUNCTION, FLATTEN.name), Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(1, 2, 3, 4, 5), null,
                    "12345"
                )
            )

        private val listOfBooleanFunctionFormulas =
            arrayOf(
                arrayOf(
                    "Function FALSE",
                    getFormula(Pair(FUNCTION, FALSE.name)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Function TRUE",
                    getFormula(Pair(FUNCTION, TRUE.name)),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Function CONTAINS false",
                    getFormula(
                        Pair(FUNCTION, CONTAINS.name), Pair(USER_LIST, userListLeftName),
                        Pair(NUMBER, 10)
                    ),
                    null, null, listOf(1, 2, 3, 4, 5), null,
                    falseString
                ),
                arrayOf(
                    "Function CONTAINS true",
                    getFormula(
                        Pair(FUNCTION, CONTAINS.name), Pair(USER_LIST, userListLeftName),
                        Pair(NUMBER, 3)
                    ),
                    null, null, listOf(1, 2, 3, 4, 5), null,
                    trueString
                ),
                arrayOf(
                    "Function MULTI_FINGER_TOUCHED",
                    getFormula(Pair(FUNCTION, MULTI_FINGER_TOUCHED.name)),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Function COLLIDES_WITH_COLOR",
                    getFormula(Pair(FUNCTION, COLLIDES_WITH_COLOR.name), Pair(STRING, "#FFFFFF")),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Function COLOR_TOUCHES_COLOR",
                    getFormula(
                        Pair(FUNCTION, COLOR_TOUCHES_COLOR.name), Pair(STRING, "#FFFFFF"),
                        Pair(STRING, "#FFFFFF")
                    ),
                    null, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Function COLOR_EQUALS_COLOR",
                    getFormula(
                        Pair(FUNCTION, COLOR_EQUALS_COLOR.name), Pair(STRING, "#FFFFFF"),
                        Pair(STRING, "#FFFFFF"), Pair(NUMBER, 1)
                    ),
                    null, null, null, null,
                    trueString
                ),
                arrayOf(
                    "Function COLOR_EQUALS_COLOR",
                    getFormula(
                        Pair(FUNCTION, COLOR_EQUALS_COLOR.name), Pair(STRING, "#FFFFFF"),
                        Pair(STRING, "#000000"), Pair(NUMBER, 1)
                    ),
                    null, null, null, null,
                    falseString
                )
            )

        private val listOfUserDataFormulas =
            arrayOf(
                arrayOf(
                    "UserVariable Int",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    123, null, null, null,
                    "123"
                ),
                arrayOf(
                    "UserVariable Int 1000",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    1000, null, null, null,
                    "1000"
                ),
                arrayOf(
                    "UserVariable Int -1000000",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    -1_000_000, null, null, null,
                    "-1000000"
                ),
                arrayOf(
                    "UserVariable Double",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    123.45, null, null, null,
                    "123.45"
                ),
                arrayOf(
                    "UserVariable String",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    "Hello", null, null, null,
                    "Hello"
                ),
                arrayOf(
                    "UserVariable Character",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    'X', null, null, null,
                    "X"
                ),
                arrayOf(
                    "UserList Int",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(1, 2, 3), null,
                    "123"
                ),
                arrayOf(
                    "UserList Int 2000 3000000",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(1, 2_000, 3_000_000), null,
                    "1 2000 3000000"
                ),
                arrayOf(
                    "UserList Int -2000 -3000000",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(-1, -2_000, -3_000_000), null,
                    "-1 -2000 -3000000"
                ),
                arrayOf(
                    "UserList Double",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(1.1, 2.2, 3.3), null,
                    "1.1 2.2 3.3"
                ),
                arrayOf(
                    "UserList Character",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf('H', 'e', 'l', 'l', 'o'), null,
                    "Hello"
                ),
                arrayOf(
                    "UserList String",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf("Hello", "world", "!"), null,
                    "Hello world !"
                ),
                arrayOf(
                    "UserList Mixed",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf("Hello", '!', 123, 3.1415, true), null,
                    "Hello ! 123 3.1415 $trueString"
                )
            )

        private val listOfBooleanUserDataFormulas =
            arrayOf(
                arrayOf(
                    "Function UserVariable false",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    false, null, null, null,
                    falseString
                ),
                arrayOf(
                    "Function UserVariable true",
                    getFormula(Pair(USER_VARIABLE, userVariableLeftName)),
                    true, null, null, null,
                    trueString
                ),
                arrayOf(
                    "UserList false",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(false), null,
                    falseString
                ),
                arrayOf(
                    "UserList true",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(true), null,
                    trueString
                ),
                arrayOf(
                    "UserList false false",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(false, false), null,
                    "$falseString $falseString"
                ),
                arrayOf(
                    "UserList false true",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(false, true), null,
                    "$falseString $trueString"
                ),
                arrayOf(
                    "UserList true false",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(true, false), null,
                    "$trueString $falseString"
                ),
                arrayOf(
                    "UserList true true",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(true, true), null,
                    "$trueString $trueString"
                ),
                arrayOf(
                    "UserList true false false",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(true, false, false), null,
                    "$trueString $falseString $falseString"
                ),
                arrayOf(
                    "UserList false true true",
                    getFormula(Pair(USER_LIST, userListLeftName)),
                    null, null, listOf(false, true, true), null,
                    "$falseString $trueString $trueString"
                )
            )

        private val listOfCollisionFormulas =
            arrayOf(
                arrayOf(
                    "Collision false",
                    getFormula(Pair(COLLISION_FORMULA, collisionSpriteName)),
                    null, null, null, null,
                    falseString
                )
            )

        private fun getFormula(
            parentPair: Pair<ElementType, Any>,
            leftChildPair: Pair<ElementType, Any>? = null,
            rightChildPair: Pair<ElementType, Any>? = null,
            additionalChildPair: Pair<ElementType, Any>? = null
        ): Formula {
            val parentType = parentPair.first
            val parentValue = parentPair.second
            val parentFormulaElement = FormulaElement(parentType, parentValue.toString(), null)

            if (leftChildPair != null) {
                val leftFormulaElement = pairToFormulaElement(leftChildPair, parentFormulaElement)
                parentFormulaElement.setLeftChild(leftFormulaElement)
            }
            if (rightChildPair != null) {
                val rightFormulaElement = pairToFormulaElement(rightChildPair, parentFormulaElement)
                parentFormulaElement.setRightChild(rightFormulaElement)
            }
            if (additionalChildPair != null) {
                val additionalFormulaElement =
                    pairToFormulaElement(additionalChildPair, parentFormulaElement)
                parentFormulaElement.additionalChildren = listOf(additionalFormulaElement)
            }
            return Formula(parentFormulaElement)
        }

        private fun pairToFormulaElement(
            typeValuePair: Pair<ElementType, Any>,
            parentFormulaElement: FormulaElement
        ): FormulaElement {
            val leftChildType = typeValuePair.first
            val leftChildValue = typeValuePair.second
            return FormulaElement(leftChildType, leftChildValue.toString(), parentFormulaElement)
        }
    }
}
