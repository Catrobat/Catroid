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

package org.catrobat.catroid.test.io.catrobatlanguage.parser

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.UiTestCatroidApplication
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.catlang.parser.parameter.CatrobatFormulaParser
import org.catrobat.catroid.io.catlang.parser.parameter.error.FormulaParsingException
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FormulaParserTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun testSimpleString() {
        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.STRING, "mySimpleString:)", null))
        testFormula(expectedFormula)
    }

    @Test
    fun testSpecialString() {
        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.STRING, "'test(\":)", null))
        testFormula(expectedFormula)
    }

    @Test
    fun testEscapedString() {
        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.STRING, "mySimple\\'String\\\":)", null))
        testFormula(expectedFormula)
    }

    @Test
    fun testNumber() {
        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.NUMBER, "1234567890", null))
        testFormula(expectedFormula)
    }

    @Test
    fun testDecimal() {
        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.NUMBER, "534.535", null))
        testFormula(expectedFormula)
    }

    @Test
    fun testLogicalOperator() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.OPERATOR,
                Operators.LOGICAL_NOT.name,
                null,
                null,
                FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.TRUE.name, null)
            )
        )
        testFormula(expectedFormula)
    }

    @Test
    fun testNestedLogicalOperator() {
        val orElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.LOGICAL_OR.name,
            null
        )
        val andElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.LOGICAL_AND.name,
            orElement
        )
        orElement.leftChild = andElement
        val geElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.GREATER_OR_EQUAL.name,
            orElement,
            FormulaElement(FormulaElement.ElementType.NUMBER, "5", null),
            FormulaElement(FormulaElement.ElementType.NUMBER, "6", null)
        )
        orElement.rightChild = geElement

        val neElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.NOT_EQUAL.name,
            andElement,
            FormulaElement(FormulaElement.ElementType.NUMBER, "1", null),
            FormulaElement(FormulaElement.ElementType.NUMBER, "2", null)
        )
        andElement.leftChild = neElement

        val notElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.LOGICAL_NOT.name,
            andElement,
            null,
            FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.TRUE.name, null)
        )
        andElement.rightChild = notElement

        val bracketElement = FormulaElement(
            FormulaElement.ElementType.BRACKET,
            null,
            notElement
        )
        notElement.rightChild = bracketElement

        val seElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.SMALLER_OR_EQUAL.name,
            bracketElement,
            FormulaElement(FormulaElement.ElementType.NUMBER, "3", null),
            FormulaElement(FormulaElement.ElementType.NUMBER, "4", null)
        )
        bracketElement.rightChild = seElement

        val expectedFormula = Formula(orElement)
        testFormula(expectedFormula)
    }

    @Test
    fun testSimpleAddition() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.OPERATOR,
                Operators.PLUS.name,
                null,
                FormulaElement(FormulaElement.ElementType.NUMBER, "1", null),
                FormulaElement(FormulaElement.ElementType.NUMBER, "2", null)
            )
        )
        testFormula(expectedFormula)
    }

    @Test
    fun testBracketCalculation() {
        val firstBracket = FormulaElement(
            FormulaElement.ElementType.BRACKET,
            null,
            null
        )
        val division = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.DIVIDE.name,
            firstBracket
        )
        firstBracket.rightChild = division
        val number1 = FormulaElement(FormulaElement.ElementType.NUMBER, "3", division)
        division.rightChild = number1

        val multElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.MULT.name,
            division
        )
        division.leftChild = multElement

        val number2 = FormulaElement(FormulaElement.ElementType.NUMBER, "5", multElement)
        multElement.leftChild = number2

        val secondBracket = FormulaElement(
            FormulaElement.ElementType.BRACKET,
            null,
            multElement
        )
        multElement.rightChild = secondBracket

        val minusElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.MINUS.name,
            secondBracket
        )
        secondBracket.rightChild = minusElement

        val number3 = FormulaElement(FormulaElement.ElementType.NUMBER, "2", minusElement)
        minusElement.leftChild = number3
        val number4 = FormulaElement(FormulaElement.ElementType.NUMBER, "8", minusElement)
        minusElement.rightChild = number4

        val expectedFormula = Formula(firstBracket)
        testFormula(expectedFormula)
    }

    @Test
    fun testBracketNesting() {
        val bracket = FormulaElement(
            FormulaElement.ElementType.BRACKET,
            null,
            null
        )
        val bracket2 = FormulaElement(
            FormulaElement.ElementType.BRACKET,
            null,
            bracket
        )
        bracket.rightChild = bracket2
        val bracket3 = FormulaElement(
            FormulaElement.ElementType.BRACKET,
            null,
            bracket2
        )
        bracket2.rightChild = bracket3
        val addition = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.PLUS.name,
            bracket3,
            FormulaElement(FormulaElement.ElementType.NUMBER, "1", null),
            FormulaElement(FormulaElement.ElementType.NUMBER, "2", null)
        )
        bracket3.rightChild = addition
        val expectedFormula = Formula(bracket)
        testFormula(expectedFormula)
    }

    @Test
    fun testFunctionAndSensor() {
        val plusElement = FormulaElement(
            FormulaElement.ElementType.OPERATOR,
            Operators.PLUS.name,
            null,
            null,
            FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.STAGE_WIDTH.name, null)
        )
        val roundElement = FormulaElement(
            FormulaElement.ElementType.FUNCTION,
            Functions.ROUND.name,
            plusElement,
            FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.PI.name, null),
            null
        )
        plusElement.leftChild = roundElement

        val expectedFormula = Formula(plusElement)
        testFormula(expectedFormula)
    }

    @Test
    fun testSimpleUserVariable() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_VARIABLE,
                "var1",
                null
            )
        )
        testFormula(expectedFormula)
    }

    @Test
    fun testUserVariableWithEscape() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_VARIABLE,
                "\"va\"r1\"",
                null
            )
        )
        val brick = WaitBrick(expectedFormula)
        createProject(brick)
        UiTestCatroidApplication.projectManager.currentProject.userVariables.add(UserVariable("\"va\"r1\""))
        val actualFormula = getParsedFormula(brick)
        compareFormulas(expectedFormula, actualFormula)
    }

    @Test
    fun testUserVariableWithDoubleEscape() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_VARIABLE,
                "va\\\"r1",
                null
            )
        )
        val brick = WaitBrick(expectedFormula)
        createProject(brick)
        UiTestCatroidApplication.projectManager.currentProject.userVariables.add(UserVariable("va\\\"r1"))
        val actualFormula = getParsedFormula(brick)
        compareFormulas(expectedFormula, actualFormula)
    }

    @Test
    fun failForUndefinedVariable() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_VARIABLE,
                "undefinedVariable",
                null
            )
        )
        try {
            testFormula(expectedFormula)
            Assert.fail("Expected ArgumentParsingException")
        } catch (e: FormulaParsingException) {
            Assert.assertEquals("Unknown variable: undefinedVariable", e.message)
        }
    }

    @Test
    fun testUserList() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_LIST,
                "list1",
                null
            )
        )
        testFormula(expectedFormula)
    }

    @Test
    fun testUserListWithEscape() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_LIST,
                "***list1****",
                null
            )
        )
        val brick = WaitBrick(expectedFormula)
        createProject(brick)
        UiTestCatroidApplication.projectManager.currentProject.userLists.add(UserList("***list1****"))
        val actualFormula = getParsedFormula(brick)
        compareFormulas(expectedFormula, actualFormula)
    }

    @Test
    fun testUserListWithDoubleEscape() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_LIST,
                "\\*\\*\\*list1****",
                null
            )
        )
        val brick = WaitBrick(expectedFormula)
        createProject(brick)
        UiTestCatroidApplication.projectManager.currentProject.userLists.add(UserList("\\*\\*\\*list1****"))
        val actualFormula = getParsedFormula(brick)
        compareFormulas(expectedFormula, actualFormula)
    }

    @Test
    fun failForUndefinedList() {
        val expectedFormula = Formula(
            FormulaElement(
                FormulaElement.ElementType.USER_LIST,
                "undefinedList",
                null
            )
        )
        try {
            testFormula(expectedFormula)
            Assert.fail("Expected ArgumentParsingException")
        } catch (e: FormulaParsingException) {
            Assert.assertEquals("Unknown list: undefinedList", e.message)
        }
    }

    @Test
    fun failForUndefinedFunction() {
        val brick = WaitBrick()
        createProject(brick)
        val parameterParser = CatrobatFormulaParser(
            ApplicationProvider.getApplicationContext(),
            UiTestCatroidApplication.projectManager.currentProject,
            UiTestCatroidApplication.projectManager.startScene,
            UiTestCatroidApplication.projectManager.currentSprite,
            brick
        )
        try {
            parameterParser.parseArgument("undefinedFunction(1,2,3)")
            Assert.fail("Expected ArgumentParsingException")
        } catch (e: FormulaParsingException) {
            Assert.assertEquals("Unknown sensor, property or method: undefinedFunction", e.message)
        }
    }

    @Test
    fun failForJoinFunctionParameterCount() {
        val brick = WaitBrick()
        createProject(brick)
        val parameterParser = CatrobatFormulaParser(
            ApplicationProvider.getApplicationContext(),
            UiTestCatroidApplication.projectManager.currentProject,
            UiTestCatroidApplication.projectManager.startScene,
            UiTestCatroidApplication.projectManager.currentSprite,
            brick
        )
        try {
            parameterParser.parseArgument("join(3)")
            Assert.fail("Expected ArgumentParsingException")
        } catch (e: FormulaParsingException) {
            Assert.assertEquals("Invalid number of arguments for function join. Expected 2 or 3, but got 1", e.message)
        }
    }

    @Test
    fun failForWrongParameterNumber() {
        val brick = WaitBrick()
        createProject(brick)
        val parameterParser = CatrobatFormulaParser(
            ApplicationProvider.getApplicationContext(),
            UiTestCatroidApplication.projectManager.currentProject,
            UiTestCatroidApplication.projectManager.startScene,
            UiTestCatroidApplication.projectManager.currentSprite,
            brick
        )
        try {
            parameterParser.parseArgument("sine(1,2)")
            Assert.fail("Expected ArgumentParsingException")
        } catch (e: FormulaParsingException) {
            Assert.assertEquals("Invalid number of arguments for function sine. Expected 1, but got 2", e.message)
        }
    }

    @Test
    fun testUserDefinedBrick() {
        val userDefinedBrick = UserDefinedBrick()
        userDefinedBrick.addLabel("Label")
        userDefinedBrick.addInput("Input")
        userDefinedBrick.setCallingBrick(true)
        val udbRB = UserDefinedReceiverBrick(userDefinedBrick)

        val userDefinedScript = udbRB.script as UserDefinedScript
        val input = UserDefinedBrickInput("Input")
        userDefinedScript.setUserDefinedBrickInputs(listOf(input))

        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, input.name, null))
        val brick = ChangeSizeByNBrick(expectedFormula)
        userDefinedScript.addBrick(brick)
        createProject(userDefinedScript)
        val actualFormula = getParsedFormula(brick)
        compareFormulas(expectedFormula, actualFormula)
    }

    @Test
    fun testUserDefinedBrickEscaped() {
        val userDefinedBrick = UserDefinedBrick()
        userDefinedBrick.addLabel("Label")
        userDefinedBrick.addInput("[I[npu]t]")
        userDefinedBrick.setCallingBrick(true)
        val udbRB = UserDefinedReceiverBrick(userDefinedBrick)

        val userDefinedScript = udbRB.script as UserDefinedScript
        val input = UserDefinedBrickInput("[I[npu]t]")
        userDefinedScript.setUserDefinedBrickInputs(listOf(input))

        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, input.name, null))
        val brick = ChangeSizeByNBrick(expectedFormula)
        userDefinedScript.addBrick(brick)
        createProject(userDefinedScript)
        val actualFormula = getParsedFormula(brick)
        compareFormulas(expectedFormula, actualFormula)
    }

    @Test
    fun failForUndefinedUserInput() {
        val userDefinedBrick = UserDefinedBrick()
        userDefinedBrick.addLabel("Label")
        userDefinedBrick.addInput("Input")
        userDefinedBrick.setCallingBrick(true)
        val udbRB = UserDefinedReceiverBrick(userDefinedBrick)

        val userDefinedScript = udbRB.script as UserDefinedScript
        val input = UserDefinedBrickInput("Input")
        userDefinedScript.setUserDefinedBrickInputs(listOf(input))

        val expectedFormula = Formula(FormulaElement(FormulaElement.ElementType.USER_DEFINED_BRICK_INPUT, "Input2", null))
        val brick = ChangeSizeByNBrick(expectedFormula)
        userDefinedScript.addBrick(brick)
        createProject(userDefinedScript)
        try {
            getParsedFormula(brick)
            Assert.fail("Expected ArgumentParsingException")
        } catch (e: FormulaParsingException) {
            Assert.assertEquals("Unknown user defined brick parameter: Input2", e.message)
        }
    }

    private fun testFormula(expectedFormula: Formula) {
        val brick = WaitBrick(expectedFormula)
        createProject(brick)
        val actualFormula = getParsedFormula(brick)
        compareFormulas(expectedFormula, actualFormula)
    }

    private fun compareFormulas(expectedFormula: Formula, actualFormula: Formula) {
        val expectedContent = expectedFormula.getTrimmedFormulaStringForCatrobatLanguage(ApplicationProvider.getApplicationContext())
        val actualContent = actualFormula.getTrimmedFormulaStringForCatrobatLanguage(ApplicationProvider.getApplicationContext())
        Assert.assertEquals(expectedContent, actualContent)

        compareFormulaElement(expectedFormula.formulaTree, actualFormula.formulaTree)
    }

    private fun compareFormulaElement(expectedElement: FormulaElement, actualElement: FormulaElement) {
        Assert.assertEquals("Value Mismatch", expectedElement.value, actualElement.value)
        Assert.assertEquals("Type Mismatch", expectedElement.elementType, actualElement.elementType)
        if (expectedElement.leftChild != null) {
            compareFormulaElement(expectedElement.leftChild, actualElement.leftChild)
        } else if (actualElement.leftChild != null) {
            Assert.fail("Left child is not null")
        }
        if (expectedElement.rightChild != null) {
            compareFormulaElement(expectedElement.rightChild, actualElement.rightChild)
        } else if (actualElement.rightChild != null) {
            Assert.fail("Right child is not null")
        }
        if (expectedElement.additionalChildren != null) {
            Assert.assertEquals("Additional children size mismatch", expectedElement.additionalChildren.size, actualElement.additionalChildren.size)
            for (i in expectedElement.additionalChildren.indices) {
                compareFormulaElement(expectedElement.additionalChildren[i], actualElement.additionalChildren[i])
            }
        } else if (actualElement.additionalChildren != null) {
            Assert.fail("Additional children is not null")
        }
    }

    private fun getParsedFormula(brick: FormulaBrick): Formula {
        val parameterParser = CatrobatFormulaParser(
            ApplicationProvider.getApplicationContext(),
            UiTestCatroidApplication.projectManager.currentProject,
            UiTestCatroidApplication.projectManager.startScene,
            UiTestCatroidApplication.projectManager.currentSprite,
            brick
        )
        return parameterParser.parseArgument(brick.formulas[0].getTrimmedFormulaStringForCatrobatLanguage(ApplicationProvider.getApplicationContext()))
    }

    private fun createProject(script: Script) {
        createProject()
        
        UiTestCatroidApplication.projectManager.currentSprite.addScript(script)
        baseActivityTestRule.launchActivity()
    }

    private fun createProject(brick: Brick) {
        createProject()

        val script = StartScript()
        UiTestCatroidApplication.projectManager.currentSprite.addScript(script)
        script.addBrick(brick)
        baseActivityTestRule.launchActivity()
    }
    
    private fun createProject() {
        val projectName = javaClass.simpleName
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        val sprite1 = Sprite("testSprite1")
        val sprite2 = Sprite("testSprite2")
        val sprite3 = Sprite("testSprite3")

        project.sceneList.add(Scene("s1", project))
        project.sceneList.add(Scene("s2", project))
        project.sceneList.add(Scene("s3", project))

        project.defaultScene.addSprite(sprite)
        project.defaultScene.addSprite(sprite1)
        project.defaultScene.addSprite(sprite2)
        project.defaultScene.addSprite(sprite3)
        UiTestCatroidApplication.projectManager.currentProject = project
        UiTestCatroidApplication.projectManager.currentSprite = sprite

        project.defaultScene.backgroundSprite.lookList.add(LookData("look1", File("look1.jpg")))
        project.defaultScene.backgroundSprite.lookList.add(LookData("look2", File("look2.jpg")))
        project.defaultScene.backgroundSprite.lookList.add(LookData("look3", File("look3.jpg")))

        sprite.lookList.add(LookData("spritelook1", File("look1.jpg")))
        sprite.lookList.add(LookData("spritelook2", File("look2.jpg")))
        sprite.lookList.add(LookData("spritelook3", File("look3.jpg")))

        sprite.soundList.add(SoundInfo("sound1", File("sound1.mp3")))
        sprite.soundList.add(SoundInfo("sound2", File("sound1.mp3")))
        sprite.soundList.add(SoundInfo("sound3", File("sound3.mp3")))

        UiTestCatroidApplication.projectManager.currentProject.userVariables.add(UserVariable("var1"))
        UiTestCatroidApplication.projectManager.currentProject.userVariables.add(UserVariable("var2"))
        UiTestCatroidApplication.projectManager.currentProject.userVariables.add(UserVariable("var3"))

        UiTestCatroidApplication.projectManager.currentProject.userLists.add(UserList("list1"))
        UiTestCatroidApplication.projectManager.currentProject.userLists.add(UserList("list2"))
        UiTestCatroidApplication.projectManager.currentProject.userLists.add(UserList("list3"))

        UiTestCatroidApplication.projectManager.currentProject.broadcastMessageContainer.addBroadcastMessage("Broadcast1")
    }
}