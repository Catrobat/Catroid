/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.test.io.catrobatlanguage.serializer

import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.ReportBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils
import org.catrobat.catroid.userbrick.InputFormulaField
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Random

@RunWith(AndroidJUnit4::class)
class UserDefinedBrickSerializationTest {
    @Test
    fun testFibonacci() {
        val userDefinedBrick = UserDefinedBrick()
        userDefinedBrick.setCallingBrick(false)
        addLabelToUserDefinedBrick(userDefinedBrick, "fibonacci")
        addInputToUserDefinedBrick(userDefinedBrick, "n")

        val receiverBrick = UserDefinedReceiverBrick(userDefinedBrick)
        val udbScript = receiverBrick.script as UserDefinedScript
        udbScript.screenRefresh = false

        val ifBrick = IfLogicBeginBrick(getFibonacciIfCondition())
        ifBrick.addBrickToIfBranch(ReportBrick(Formula(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, "n", null))))
        ifBrick.addBrickToElseBranch(
            ReportBrick(
                Formula(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, "n", null))
            )
        )
        udbScript.addBrick(ifBrick)

        val startScript = StartScript()
        val startBrick: Brick = WhenStartedBrick(startScript)
        val userDefinedCallingBrick = UserDefinedBrick(userDefinedBrick)
        userDefinedCallingBrick.setCallingBrick(true)
        val formula = Formula(10)
        initUserDefinedBrickInput(userDefinedCallingBrick, "n", formula)
        userDefinedCallingBrick.userDefinedBrickInputs[0].value = formula
        startScript.addBrick(userDefinedCallingBrick)

        testBrick(receiverBrick, """
            |Define (user defined brick: (`fibonacci [n]`), screen refresh: (off)) {
            |  If (condition: ([n] < 2)) {
            |    Return (value: ([n]));
            |  } else {
            |    Return (value: ([n]));
            |  }
            |}
            |""".trimMargin()
        )
        testBrick(startBrick, """
            |When scene starts {
            |  `fibonacci [n]` ([n]: (10));
            |}
            |""".trimMargin()
        )
    }

    @Test
    fun testEmptyUserDefinedBrick() {
        val userDefinedBrick = UserDefinedBrick()
        val receiverBrick = UserDefinedReceiverBrick(userDefinedBrick)

        val startScript = StartScript()
        val startBrick: Brick = WhenStartedBrick(startScript)
        val userDefinedCallingBrick = UserDefinedBrick(userDefinedBrick)
        userDefinedCallingBrick.setCallingBrick(true)
        startScript.addBrick(userDefinedCallingBrick)

        testBrick(receiverBrick, """
            |Define (user defined brick: (``), screen refresh: (on)) {
            |}
            |""".trimMargin()
        )
        testBrick(startBrick, """
            |When scene starts {
            |  `` ();
            |}
            |""".trimMargin()
        )
    }

    @Test
    fun testNestedUserDefinedBrick() {
        val userDefinedBrick1 = UserDefinedBrick()
        userDefinedBrick1.setCallingBrick(false)
        addInputToUserDefinedBrick(userDefinedBrick1, "[]")
        addLabelToUserDefinedBrick(userDefinedBrick1, "myBrick is super")
        addInputToUserDefinedBrick(userDefinedBrick1, "`o`")

        val userDefinedCallingBrick1 = UserDefinedBrick(userDefinedBrick1)
        userDefinedCallingBrick1.setCallingBrick(true)
        val formula1 = Formula(10)
        val formula2 = Formula(20)
        initUserDefinedBrickInput(userDefinedCallingBrick1, "[]", formula1)
        initUserDefinedBrickInput(userDefinedCallingBrick1, "`o`", formula2)
        userDefinedCallingBrick1.userDefinedBrickInputs[0].value = formula1
        userDefinedCallingBrick1.userDefinedBrickInputs[1].value = formula2

        val userDefinedBrick2 = UserDefinedBrick()
        userDefinedBrick2.setCallingBrick(false)
        addLabelToUserDefinedBrick(userDefinedBrick2, "prin[`t]")
        addInputToUserDefinedBrick(userDefinedBrick2, "value")

        val userDefinedCallingBrick2 = UserDefinedBrick(userDefinedBrick2)
        userDefinedCallingBrick2.setCallingBrick(true)
        val formula3 = Formula("hi")
        userDefinedCallingBrick2.userDefinedBrickInputs[0].value = formula3
        initUserDefinedBrickInput(userDefinedCallingBrick2, "value", formula3)

        val receiverBrick1 = UserDefinedReceiverBrick(userDefinedBrick1)
        val udbScript1 = receiverBrick1.script as UserDefinedScript
        val ifBrick = IfLogicBeginBrick()
        ifBrick.addBrickToIfBranch(userDefinedCallingBrick1)
        ifBrick.addBrickToElseBranch(userDefinedCallingBrick2)
        udbScript1.addBrick(ifBrick)

        val receiverBrick2 = UserDefinedReceiverBrick(userDefinedBrick2)
        val udbScript2 = receiverBrick2.script as UserDefinedScript
        udbScript2.addBrick(userDefinedCallingBrick2.clone())
        udbScript2.addBrick(userDefinedCallingBrick1.clone())

        testBrick(receiverBrick1, """
            |Define (user defined brick: (`[\[\]] myBrick is super [\`o\`]`), screen refresh: (on)) {
            |  If (condition: (0)) {
            |    `[\[\]] myBrick is super [\`o\`]` ([\[\]]: (10), [\`o\`]: (20));
            |  } else {
            |    `prin\[\`t\] [value]` ([value]: ('hi'));
            |  }
            |}
            |""".trimMargin()
        )
        testBrick(receiverBrick2, """
            |Define (user defined brick: (`prin\[\`t\] [value]`), screen refresh: (on)) {
            |  `prin\[\`t\] [value]` ([value]: ('hi'));
            |  `[\[\]] myBrick is super [\`o\`]` ([\[\]]: (10), [\`o\`]: (20));
            |}
            |""".trimMargin()
        )
    }

    private fun initUserDefinedBrickInput(
        userDefinedBrick: UserDefinedBrick,
        input: String,
        formula: Formula
    ) {
        val formulaFieldID = InputFormulaField(input)
        userDefinedBrick.formulaMap[formulaFieldID] = formula
        userDefinedBrick.formulaFieldToTextViewMap[formulaFieldID] = TextView(CatroidApplication.getAppContext())
    }

    private fun addInputToUserDefinedBrick(userDefinedBrick: UserDefinedBrick, label: String) {
        userDefinedBrick.addInput(label)
    }

    private fun addLabelToUserDefinedBrick(userDefinedBrick: UserDefinedBrick, label: String) {
        userDefinedBrick.addLabel(label)
    }

    private fun getFibonacciIfCondition(): Formula {
        val nParam = FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, "n", null)
        val valueParam = FormulaElement(FormulaElement.ElementType.NUMBER, "2", null)
        return Formula(FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.SMALLER_THAN.name, null, nParam, valueParam))
    }

    private fun testBrick(brick: Brick, expectedOutput: String) {
        testBasicImplementation(brick, expectedOutput)
        testDisabledBrick(brick, expectedOutput)
        testIndention(brick, expectedOutput)
    }

    private fun testBasicImplementation(brick: Brick, expectedOutput: String) {
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        Assert.assertEquals(expectedOutput, actualOutput)
    }

    private fun testDisabledBrick(brick: Brick, expectedOutput: String) {
        val anyCharacter = Regex("(?!\\s).")
        val disabledValue = expectedOutput.split("\n").map { line ->
            val found = anyCharacter.find(line)
            if (found != null) {
                line.replaceRange(found.range, "// " + found.value)
            } else {
                line
            }
        }.joinToString("\n")
        brick.isCommentedOut = true
        val actualOutput = brick.serializeToCatrobatLanguage(0)
        brick.isCommentedOut = false
        Assert.assertEquals(disabledValue, actualOutput)
    }

    private fun testIndention(brick: Brick, expectedOutput: String) {
        val randomIndention = Random().nextInt(4) + 2
        val indention = CatrobatLanguageUtils.getIndention(randomIndention)
        val actualOutput = brick.serializeToCatrobatLanguage(randomIndention)
        val newOutput = indention + expectedOutput.replace(Regex("\\n(?!\$)"), "\n$indention")
        Assert.assertEquals(newOutput, actualOutput)
    }
}
