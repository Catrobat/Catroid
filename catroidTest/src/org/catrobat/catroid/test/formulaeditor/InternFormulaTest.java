package org.catrobat.catroid.test.formulaeditor;

import java.util.ArrayList;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.junit.After;
import org.junit.Before;

import android.test.InstrumentationTestCase;

public class InternFormulaTest extends InstrumentationTestCase {

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReplaceFunctionByToken() {

		if (Functions.valueOf("sdfsdf") == null) {
			fail();
		}

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();
		int doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, false);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 9, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.id.formula_editor_keyboard_4, getInstrumentation().getTargetContext(), null);
		internFormula.handleKeyInput(R.id.formula_editor_keyboard_2, getInstrumentation().getTargetContext(), null);

		assertNull("Selection found but should not", internFormula.getSelection());

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, false);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 0, internFormula.getSelection().getEndIndex());

	}

	public void testReplaceFunctionButKeepParameters() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.ROUND.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();
		int doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, false);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 9, internFormula.getSelection().getEndIndex());

		internFormula
				.handleKeyInput(R.id.formula_editor_keyboard_random, getInstrumentation().getTargetContext(), null);

		assertEquals("Selection start index not as expected", 2, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 8, internFormula.getSelection().getEndIndex());

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, false);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 11, internFormula.getSelection().getEndIndex());

		internFormula.handleKeyInput(R.string.formula_editor_function_sqrt, getInstrumentation().getTargetContext(),
				null);

		externFormulaString = internFormula.getExternFormulaString();
		doubleClickIndex = externFormulaString.length();

		assertEquals("Selection start index not as expected", 2, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 8, internFormula.getSelection().getEndIndex());

		internFormula.setCursorAndSelection(doubleClickIndex, false);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 9, internFormula.getSelection().getEndIndex());
	}

	public void testSelectBrackets() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.COS.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		internTokens.add(new InternToken(InternTokenType.BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();

		int doubleClickIndex = externFormulaString.length();
		int offsetRight = 0;
		while (offsetRight < 3) {
			internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, false);

			assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
			assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());
			offsetRight++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, false);
		assertEquals("Selection start index not as expected", 1, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());

		doubleClickIndex = 0;
		int offsetLeft = 0;

		while (offsetLeft < 2) {
			internFormula.setCursorAndSelection(doubleClickIndex + offsetLeft, false);

			assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
			assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());
			offsetLeft++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex + offsetLeft, false);
		assertEquals("Selection start index not as expected", 1, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());
	}

	public void testSelectFunctionAndSingleTab() {
		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.RAND.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();

		internFormula.setCursorAndSelection(0, true);
		assertNull("Single Tab before Funtion fail", internFormula.getSelection());

		int doubleClickIndex = externFormulaString.length();
		int offsetRight = 0;
		while (offsetRight < 3) {
			internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, false);

			assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
			assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());
			offsetRight++;
		}
		internFormula.setCursorAndSelection(doubleClickIndex - offsetRight, false);
		assertEquals("Selection start index not as expected", 4, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());

		doubleClickIndex = 0;

		internFormula.setCursorAndSelection(doubleClickIndex, false);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex = getInstrumentation().getTargetContext().getString(R.string.formula_editor_function_rand)
				.length();

		int singleClickIndex = doubleClickIndex;

		internFormula.setCursorAndSelection(singleClickIndex, true);
		assertNull("Single Tab between Function Name and Brackets fail", internFormula.getSelection());

		internFormula.setCursorAndSelection(doubleClickIndex, false);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, false);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex += " 42.42 ".length();

		internFormula.setCursorAndSelection(doubleClickIndex, false);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, false);
		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 5, internFormula.getSelection().getEndIndex());

		doubleClickIndex++;

		internFormula.setCursorAndSelection(doubleClickIndex, false);
		assertEquals("Selection start index not as expected", 4, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 4, internFormula.getSelection().getEndIndex());

	}
}
