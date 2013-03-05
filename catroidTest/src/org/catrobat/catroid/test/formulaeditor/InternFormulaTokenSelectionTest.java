package org.catrobat.catroid.test.formulaeditor;

import java.util.ArrayList;

import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.formulaeditor.InternFormulaTokenSelection;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.After;
import org.junit.Before;

import android.test.InstrumentationTestCase;

public class InternFormulaTokenSelectionTest extends InstrumentationTestCase {

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

		ArrayList<InternToken> internTokens = new ArrayList<InternToken>();
		internTokens.add(new InternToken(InternTokenType.FUNCTION_NAME, Functions.SIN.functionName));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		internTokens.add(new InternToken(InternTokenType.NUMBER, "42.42"));
		internTokens.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		InternFormula internFormula = new InternFormula(internTokens);
		internFormula.generateExternFormulaStringAndInternExternMapping(getInstrumentation().getTargetContext());
		String externFormulaString = internFormula.getExternFormulaString();
		int doubleClickIndex = externFormulaString.length();

		internFormula.setCursorAndSelection(doubleClickIndex, false);

		assertEquals("Selection start index not as expected", 0, internFormula.getSelection().getStartIndex());
		assertEquals("Selection end index not as expected", 3, internFormula.getSelection().getEndIndex());

		InternFormulaTokenSelection tokenSelection = internFormula.getSelection();
		InternFormulaTokenSelection tokenSelectionDeepCopy = tokenSelection.deepCopy();

		assertTrue("Deep copy of InternFormulaTokenSelection failed", tokenSelection.equals(tokenSelectionDeepCopy));

		Reflection.setPrivateField(tokenSelectionDeepCopy, "tokenSelectionType",
				TokenSelectionType.PARSER_ERROR_SELECTION);
		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(tokenSelection));

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionStart", -1);
		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(tokenSelection));

		tokenSelectionDeepCopy = tokenSelection.deepCopy();
		Reflection.setPrivateField(tokenSelectionDeepCopy, "internTokenSelectionEnd", -1);
		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(tokenSelection));

		assertFalse("Equal error in InternFormulaTokenSelection", tokenSelectionDeepCopy.equals(1));
	}

}
