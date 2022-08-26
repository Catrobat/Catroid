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

package org.catrobat.catroid.test.formulaeditor;

import android.content.Context;

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class InternFormulaRegexAssistantTest {

	private InternFormula internFormula;

	//doubleClickIndex set to specific index related to the extern string representation
	// described inside the second comment of each test
	@Test
	public void testRegexFunctionIsSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//regular expression ('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testRegexFunctionFirstParamIsSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//regular expression('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("Hello") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testRegexFunctionSecondParamIsSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//regular expression('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("World") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testJoinFunctionIsSelectedWithNoOutsideRegexFunction() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//join('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(-1,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testJoinFunctionIsSelectedWithOutsideRegexFunction() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//regular expression(join('Hello','World'), 'Foobar')
		//null ( null( 'Hello', , 'World' ) , 'Foobar' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "Foobar"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = 7;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(0,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testJoinFunctionFirstParamIsSelectedAndIsNoRegexFunction() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//join('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("Hello") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(-1,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testJoinFunctionSecondParamIsSelectedAndIsNoRegexFunction() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//join('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("World") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(-1,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testJoinFunctionFirstParamIsSelectedAndIsRegexFunction() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//join(regular expression('Hello', 'World'), 'Foobar')
		//null( null( 'Hello' , 'World' ) , 'Foobar' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "Foobar"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = 7;

		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(2,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testJoinFunctionSecondParamIsSelectedAndIsRegexFunction() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//join('Foobar', regular expression('Hello', 'World'))
		//null( 'Foobar' , null( 'Hello' , 'World' ) )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Foobar"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = 18;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		assertEquals(4,
				internFormula.getIndexOfCorrespondingRegularExpression());
	}

	@Test
	public void testRegexFunctionIsSelectedAndFirstParamGetsSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//regular expression ('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		internFormula.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(0);
		assertEquals(2, internFormula.getIndexOfInternTokenSelection());
	}

	@Test
	public void testRegexFunctionFirstParamIsSelectedAndFirstParamGetsSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//regular expression ('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("Hello") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		internFormula.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(0);
		assertEquals(2, internFormula.getIndexOfInternTokenSelection());
	}

	@Test
	public void testRegexFunctionSecondParamIsSelectedAndFirstParamGetsSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//regular expression ('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("World") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		internFormula.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(0);
		assertEquals(2, internFormula.getIndexOfInternTokenSelection());
	}

	@Test
	public void testSecondRegexFunctionSecondParamIsSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//join (regular expression( 'regex1', 'text1'), regular expression ('regex2', 'text2'))
		//null( null( 'regex1' , 'text1' ) , null( 'regex2' , 'text2' ))
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "regex1"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "text1"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));

		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.REGEX.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "regex2"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "text2"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("text2") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		internFormula.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(internFormula.getIndexOfCorrespondingRegularExpression());
		assertEquals(11, internFormula.getIndexOfInternTokenSelection());
	}

	@Test
	public void testNonRegexFunctionSecondParamSelectedStaysSelected() {
		ArrayList<InternToken> internTokens = new ArrayList<>();
		//join ('Hello', 'World')
		//null( 'Hello' , 'World' )
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.JOIN.name()));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.STRING, "Hello"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.STRING, "World"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(Mockito.mock(Context.class));

		int doubleClickIndex = internFormula.getExternFormulaString().indexOf("World") + 1;
		internFormula.setCursorAndSelection(doubleClickIndex, true);

		internFormula.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(-1);
		assertEquals(4, internFormula.getIndexOfInternTokenSelection());
	}
}
