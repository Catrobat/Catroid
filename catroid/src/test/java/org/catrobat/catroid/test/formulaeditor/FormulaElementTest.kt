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

package org.catrobat.catroid.test.formulaeditor

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.Functions
import org.catrobat.catroid.formulaeditor.InternFormulaParser
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.formulaeditor.Operators
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.assertEqualsTokenLists
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.LinkedList

@RunWith(JUnit4::class)
class FormulaElementTest {

    private lateinit var scope: Scope

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val project = Project(MockUtil.mockContextForProject(), "Project")
        val background = project.defaultScene.backgroundSprite
        val sprite = Sprite("testSprite")

        scope = Scope(project, background, SequenceAction())

        project.defaultScene.addSprite(sprite)

        ProjectManager.getInstance()?.currentProject = project
        ProjectManager.getInstance()?.currentlyEditedScene = project.defaultScene
    }

    @Test
    fun testGetInternTokenList() {
        val internTokenList = LinkedList<InternToken>().apply {
            add(InternToken(InternTokenType.BRACKET_OPEN))
            add(InternToken(InternTokenType.OPERATOR, Operators.MINUS.name))
            add(InternToken(InternTokenType.NUMBER, "1"))
            add(InternToken(InternTokenType.BRACKET_CLOSE))
        }

        val internParser = InternFormulaParser(internTokenList)
        val parseTree = internParser.parseFormula(scope)

        assertNotNull(parseTree)
        assertEquals(-1.0, parseTree.interpretRecursive(scope))

        val internTokenListAfterConversion = parseTree.internTokenList
        assertEquals(internTokenListAfterConversion.size, internTokenList.size)

        assertEqualsTokenLists(internTokenList, internTokenListAfterConversion)
    }

    @Test
    fun testInterpretNonExistingUserVariable() {
        val formulaElement = FormulaElement(ElementType.USER_VARIABLE, "notExistingUserVariable", null)
        assertEquals(0.0, formulaElement.interpretRecursive(scope))
    }

    @Test
    fun testInterpretNonExistingUserList() {
        val formulaElement = FormulaElement(ElementType.USER_LIST, "notExistingUserList", null)
        assertEquals(0.0, formulaElement.interpretRecursive(scope))
    }

    @Test
    fun testInterpretNotExisitingUnaryOperator() {
        val formulaElement = FormulaElement(
            ElementType.OPERATOR, Operators.PLUS.name, null, null,
            FormulaElement(ElementType.NUMBER, "1.0", null)
        )

        assertEquals(0.0, formulaElement.interpretRecursive(scope))
    }

    @Test
    fun testCheckDegeneratedDoubleValues() {
        var formulaElement = FormulaElement(
            ElementType.OPERATOR, Operators.PLUS.name, null,
            FormulaElement(ElementType.NUMBER, Double.MAX_VALUE.toString(), null),
            FormulaElement(ElementType.NUMBER, Double.MAX_VALUE.toString(), null)
        )

        assertEquals(Double.MAX_VALUE, formulaElement.interpretRecursive(scope))

        formulaElement = FormulaElement(
            ElementType.OPERATOR, Operators.MINUS.name, null,
            FormulaElement(ElementType.NUMBER, (Double.MAX_VALUE * -1.0).toString(), null),
            FormulaElement(ElementType.NUMBER, Double.MAX_VALUE.toString(), null)
        )

        assertEquals(Double.MAX_VALUE * -1.0, formulaElement.interpretRecursive(scope))

        formulaElement = FormulaElement(
            ElementType.OPERATOR, Operators.DIVIDE.name, null,
            FormulaElement(ElementType.NUMBER, "0", null),
            FormulaElement(ElementType.NUMBER, "0", null)
        )

        assertEquals(Double.NaN, formulaElement.interpretRecursive(scope))
    }

    @Test
    fun testIsBoolean() {
        val formulaElement = FormulaElement(ElementType.USER_VARIABLE, "notExistingUserVariable", null)
        assertFalse(formulaElement.isBoolean(scope))
    }

    @Test
    fun testContainsElement() {
        var formulaElement = FormulaElement(
            ElementType.OPERATOR, Operators.MINUS.name, null,
            FormulaElement(ElementType.NUMBER, "0.0", null),
            FormulaElement(ElementType.USER_VARIABLE, "user-variable", null)
        )
        assertTrue(formulaElement.containsElement(ElementType.USER_VARIABLE))

        formulaElement = FormulaElement(
            ElementType.FUNCTION, Functions.SIN.name, null,
            FormulaElement(ElementType.OPERATOR, "+", null), null
        )

        assertFalse(formulaElement.containsElement(ElementType.NUMBER))
    }

    @Test
    fun testClone() {
        var formulaElement = FormulaElement(
            ElementType.OPERATOR, Operators.MINUS.name, null,
            FormulaElement(ElementType.NUMBER, "0.0", null),
            FormulaElement(ElementType.USER_VARIABLE, "user-variable", null)
        )

        val internTokenList = formulaElement.internTokenList

        val clonedFormulaElement = formulaElement.clone()
        val internTokenListAfterClone = clonedFormulaElement.internTokenList

        assertEqualsTokenLists(internTokenList, internTokenListAfterClone)

        formulaElement = FormulaElement(
            ElementType.OPERATOR, Operators.MINUS.name, null, null,
            FormulaElement(ElementType.USER_VARIABLE, "user-variable", null)
        )

        val internTokenList2 = formulaElement.internTokenList

        val clonedFormulaElement2 = formulaElement.clone()
        val internTokenListAfterClone2 = clonedFormulaElement2.internTokenList

        assertEqualsTokenLists(internTokenList2, internTokenListAfterClone2)
    }

    @Test
    fun testCorrectDecimals() {
        val formulaElementAddition = FormulaElement(
            ElementType.OPERATOR,
            Operators.PLUS.name, null,
            FormulaElement(ElementType.NUMBER, "1.1", null),
            FormulaElement(ElementType.NUMBER, "0.1", null)
        )

        assertEquals(1.2, formulaElementAddition.interpretRecursive(scope))

        val formulaElementSubtraction = FormulaElement(
            ElementType.OPERATOR,
            Operators.MINUS.name, null,
            FormulaElement(ElementType.NUMBER, "15.3", null),
            FormulaElement(ElementType.NUMBER, "3.2", null)
        )

        assertEquals(12.1, formulaElementSubtraction.interpretRecursive(scope))

        val formulaElementMultiplication = FormulaElement(
            ElementType.OPERATOR,
            Operators.MULT.name, null,
            FormulaElement(ElementType.NUMBER, "3.5", null),
            FormulaElement(ElementType.NUMBER, "3.2", null)
        )

        assertEquals(11.2, formulaElementMultiplication.interpretRecursive(scope))

        val formulaElementDivision = FormulaElement(
            ElementType.OPERATOR,
            Operators.DIVIDE.name, null,
            FormulaElement(ElementType.NUMBER, "1.1", null),
            FormulaElement(ElementType.NUMBER, "5", null)
        )

        assertEquals(0.22, formulaElementDivision.interpretRecursive(scope))

        val formulaElementDivisionInfiniteDecimals = FormulaElement(
            ElementType.OPERATOR,
            Operators.DIVIDE.name, null,
            FormulaElement(ElementType.NUMBER, "1", null),
            FormulaElement(ElementType.NUMBER, "2.34", null)
        )

        assertEquals(0.42735042735042733, formulaElementDivisionInfiniteDecimals.interpretRecursive(scope))
    }

    @Test
    fun testInterpretFunctionIndexOfItem() {
        val list = UserList("testList")
        list.addListItem(500.0)
        list.addListItem("two")
        list.addListItem(true)
        list.addListItem(false)
        scope.project!!.addUserList(list)

        // 1. Match number
        var indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            FormulaElement(ElementType.NUMBER, "500.0", null),
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(1.0, indexFunction.interpretRecursive(scope))

        // 2. Match string
        indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            FormulaElement(ElementType.STRING, "two", null),
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(2.0, indexFunction.interpretRecursive(scope))

        // 3. Match literal TRUE
        indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            FormulaElement(ElementType.FUNCTION, Functions.TRUE.name, null),
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(3.0, indexFunction.interpretRecursive(scope))

        // 4. Match computed TRUE (comparison)
        val greaterThan = FormulaElement(
            ElementType.OPERATOR, Operators.GREATER_THAN.name, null,
            FormulaElement(ElementType.NUMBER, "5", null),
            FormulaElement(ElementType.NUMBER, "3", null)
        )
        indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            greaterThan,
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(3.0, indexFunction.interpretRecursive(scope))

        // 5. Match variable TRUE
        val myVar = UserVariable("myVar", 1.0) // 1.0 is TRUE
        scope.project!!.addUserVariable(myVar)
        indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            FormulaElement(ElementType.USER_VARIABLE, "myVar", null),
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(3.0, indexFunction.interpretRecursive(scope))

        // 6. Match literal FALSE
        indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            FormulaElement(ElementType.FUNCTION, Functions.FALSE.name, null),
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(4.0, indexFunction.interpretRecursive(scope))

        // 7. Loose matching (string "500" matches number 500.0)
        indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            FormulaElement(ElementType.STRING, "500", null),
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(1.0, indexFunction.interpretRecursive(scope))

        // 8. Not found
        indexFunction = FormulaElement(
            ElementType.FUNCTION, Functions.INDEX_OF_ITEM.name, null,
            FormulaElement(ElementType.NUMBER, "99", null),
            FormulaElement(ElementType.USER_LIST, "testList", null)
        )
        assertEquals(0.0, indexFunction.interpretRecursive(scope))
    }
}
