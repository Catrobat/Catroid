/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import junit.framework.Assert.assertNotNull
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.InternFormulaParser
import org.catrobat.catroid.formulaeditor.InternToken
import org.catrobat.catroid.formulaeditor.InternTokenType
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.test.formulaeditor.FormulaEditorTestUtil.assertEqualsTokenLists
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.LinkedList
import java.util.UUID

@RunWith(JUnit4::class)
class UserDefinedFunctionTest {
    private lateinit var scope: Scope

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val project = Project(MockUtil.mockContextForProject(), "Project")
        scope = Scope(project, project.defaultScene.backgroundSprite, SequenceAction())
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testParseAndConvertUserDefinedFunction() {
        val brickId = UUID.randomUUID().toString()
        val tokens = LinkedList<InternToken>()
        tokens.add(InternToken(InternTokenType.USER_DEFINED_FUNCTION, brickId))
        tokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN, "("))
        tokens.add(InternToken(InternTokenType.NUMBER, "1"))
        tokens.add(InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER, ","))
        tokens.add(InternToken(InternTokenType.NUMBER, "2"))
        tokens.add(InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE, ")"))

        val parser = InternFormulaParser(tokens)
        val tree: FormulaElement = parser.parseFormula(scope)

        assertNotNull(tree)
        assertEquals(ElementType.USER_DEFINED_FUNCTION, tree.elementType)
        assertEquals(brickId, tree.value)

        val convertedTokens = tree.internTokenList
        assertEqualsTokenLists(tokens, convertedTokens)
    }
}
