/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.FormulaEditor;

import android.test.AndroidTestCase;
import android.util.Log;
import at.tugraz.ist.catroid.content.Formula;
import at.tugraz.ist.catroid.content.FormulaElement;

public class DataStructureTest extends AndroidTestCase {

	private static final String PLUS = "+";
	private static final String MINUS = "-";
	private static final String MULT = "*";
	private static final String DIVIDE = "/";

	private static final String TEST_VALUE = "234";
	private static final String TEST_VALUE1 = "923";
	private static final String TEST_VALUE2 = "345";

	private static final String[] testAddToValueInput = new String[] { "1", "2", "3", "4", "5" };
	private static final String[] testAddToValueOutput = new String[] { "1", "12", "123", "1234", "12345" };

	private static final String[] testDeleteLastCharacterInValueInput = new String[] { "123456789", "123",
			"234234.234", "0.234" };
	private static final String[] testDeleteLastCharacterInValueOutput = new String[] { "12345678", "12", "234234.23",
			"0.23" };

	public void testRoot() {
		Formula formula = new Formula();
		FormulaElement root = formula.findItemByPosition(0);

		assertEquals("Root Element is not as expected", "root", root.getValue());
		assertEquals("Root Element is not ELEMENT_REPLACED_BY_CHILDREN", FormulaElement.ELEMENT_REPLACED_BY_CHILDREN,
				root.getType());

		assertEquals("Children Amount is not as expected", 1, root.getNumberOfRecursiveChildren());
	}

	public void testRootValue() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		String childValue = formula.findItemByPosition(1).getValue();

		assertEquals("Root Element is not as expected", "root", root.getValue());
		assertEquals("Root Element is not ELEMENT_REPLACED_BY_CHILDREN", FormulaElement.ELEMENT_REPLACED_BY_CHILDREN,
				root.getType());
		assertEquals("Children Amount is not as expected", 1, root.getNumberOfRecursiveChildren());
		assertEquals("Children Value is not as expected", TEST_VALUE, childValue);
	}

	public void testReplaceNumberByOperator() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(PLUS, root);
		root = formula.findItemByPosition(0);
		String firstElementValue = formula.findItemByPosition(1).getValue();
		String secondElementValue = formula.findItemByPosition(3).getValue();
		String operator = formula.findItemByPosition(2).getValue();

		assertEquals("Root Element is not as expected", "root", root.getValue());
		assertEquals("First Element is not as expected", "0", firstElementValue);
		assertEquals("Second Element is not as expected", "0", secondElementValue);
		assertEquals("Operator is not as expected", PLUS, operator);
	}

	public void testGetChildOfType() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(PLUS, root);
		formula.findItemByPosition(1).replaceValue(TEST_VALUE);

		root = formula.findItemByPosition(0);
		String firstElementValue = formula.findItemByPosition(1).getValue();
		String secondElementValue = formula.findItemByPosition(3).getValue();
		String operator = formula.findItemByPosition(2).getValue();
		FormulaElement firstElement = root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE);

		assertEquals("Root Element is not as expected", "root", root.getValue());
		assertEquals("First Element is not as expected", TEST_VALUE, firstElementValue);
		assertEquals("Second Element is not as expected", "0", secondElementValue);
		assertEquals("Operator is not as expected", PLUS, operator);
		assertEquals("getChildOfType is not working as expected", firstElement, formula.findItemByPosition(1));
	}

	public void testType() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(PLUS, root);
		formula.findItemByPosition(1).replaceValue(TEST_VALUE);
		formula.addToFormula(PLUS, formula.findItemByPosition(1));

		assertEquals("Type is not as expected", FormulaElement.ELEMENT_REPLACED_BY_CHILDREN, formula
				.findItemByPosition(0).getType());
		assertEquals("Type is not as expected", FormulaElement.ELEMENT_FIRST_VALUE, formula.findItemByPosition(1)
				.getType());
		assertEquals("Type is not as expected", FormulaElement.ELEMENT_OPERATOR, formula.findItemByPosition(2)
				.getType());
		assertEquals("Type is not as expected", FormulaElement.ELEMENT_SECOND_VALUE, formula.findItemByPosition(3)
				.getType());
		assertEquals("Type is not as expected", FormulaElement.ELEMENT_OPERATOR, formula.findItemByPosition(4)
				.getType());
		assertEquals("Type is not as expected", FormulaElement.ELEMENT_SECOND_VALUE, formula.findItemByPosition(5)
				.getType());
	}

	public void testValue() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(PLUS, root);
		formula.findItemByPosition(1).replaceValue(TEST_VALUE);
		formula.addToFormula(PLUS, formula.findItemByPosition(1));
		formula.findItemByPosition(1).replaceValue(TEST_VALUE);
		formula.findItemByPosition(3).replaceValue(TEST_VALUE1);
		formula.findItemByPosition(5).replaceValue(TEST_VALUE2);

		assertEquals("Value is not as expected", TEST_VALUE, formula.findItemByPosition(1).getValue());
		assertEquals("Value is not as expected", TEST_VALUE1, formula.findItemByPosition(3).getValue());
		assertEquals("Value is not as expected", TEST_VALUE2, formula.findItemByPosition(5).getValue());
	}

	public void testGetNumberOfRecursiveChildren() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);

		assertEquals("Number of Recursive Children is not as expected", 1, root.getNumberOfRecursiveChildren());

		formula.addToFormula(PLUS, root);

		assertEquals("Number of Recursive Children is not as expected", 3, root.getNumberOfRecursiveChildren());

		formula.addToFormula(PLUS, root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE));

		assertEquals("Number of Recursive Children is not as expected", 5, formula.findItemByPosition(0)
				.getNumberOfRecursiveChildren());
		assertEquals("Number of Recursive Children is not as expected", 1, formula.findItemByPosition(1)
				.getNumberOfRecursiveChildren());
		assertEquals("Number of Recursive Children is not as expected", 1, formula.findItemByPosition(2)
				.getNumberOfRecursiveChildren());
		assertEquals("Number of Recursive Children is not as expected", 1, formula.findItemByPosition(3)
				.getNumberOfRecursiveChildren());
		assertEquals("Number of Recursive Children is not as expected", 1, formula.findItemByPosition(4)
				.getNumberOfRecursiveChildren());
		assertEquals("Number of Recursive Children is not as expected", 1, formula.findItemByPosition(5)
				.getNumberOfRecursiveChildren());
		assertEquals("Number of Recursive Children is not as expected", 3,
				root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE_REPLACED_BY_CHILDREN)
						.getNumberOfRecursiveChildren());

	}

	public void testInterpreterAddition() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(PLUS, root);
		formula.addToFormula(PLUS, root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE));
		formula.findItemByPosition(1).replaceValue(TEST_VALUE);
		formula.findItemByPosition(3).replaceValue(TEST_VALUE1);
		formula.findItemByPosition(5).replaceValue(TEST_VALUE2);

		assertEquals("Interpreter result is not as expected", 1502.0, formula.interpret());
	}

	public void testInterpreterSubstraction() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(MINUS, root);
		formula.addToFormula(MINUS, root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE));
		formula.findItemByPosition(1).replaceValue(TEST_VALUE1);
		formula.findItemByPosition(3).replaceValue(TEST_VALUE);
		formula.findItemByPosition(5).replaceValue(TEST_VALUE2);

		assertEquals("Interpreter result is not as expected", 344.0, formula.interpret());
	}

	public void testInterpreterMultiplication() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(MULT, root);
		formula.addToFormula(MULT, root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE));
		formula.findItemByPosition(1).replaceValue(TEST_VALUE1);
		formula.findItemByPosition(3).replaceValue(TEST_VALUE);
		formula.findItemByPosition(5).replaceValue(TEST_VALUE2);

		assertEquals("Interpreter result is not as expected", 74513790.0, formula.interpret());
	}

	public void testInterpreterDivide() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(DIVIDE, root);
		formula.addToFormula(DIVIDE, root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE));
		formula.findItemByPosition(1).replaceValue(TEST_VALUE1);
		formula.findItemByPosition(3).replaceValue(TEST_VALUE);
		formula.findItemByPosition(5).replaceValue(TEST_VALUE2);

		//		assertEquals("Interpreter result is not as expected", 0.0, formula.interpret()); TODO: update to Double
	}

	public void testInterpreterMultPlus() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(MULT, root);
		formula.addToFormula(PLUS, root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE));
		formula.findItemByPosition(1).replaceValue(TEST_VALUE);
		formula.findItemByPosition(3).replaceValue(TEST_VALUE1);
		formula.findItemByPosition(5).replaceValue(TEST_VALUE2);

		assertEquals("Interpreter result is not as expected", 399165.0, formula.interpret());
	}

	public void testInterpreterMultDivide() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement root = formula.findItemByPosition(0);
		formula.addToFormula(DIVIDE, root);
		formula.addToFormula(MULT, root.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE));
		formula.findItemByPosition(1).replaceValue(TEST_VALUE);
		formula.findItemByPosition(3).replaceValue(TEST_VALUE1);
		formula.findItemByPosition(5).replaceValue("215982");

		Log.i("info", "FORMULA testInterpreterMultDivide: " + formula.stringRepresentation());

		assertEquals("Interpreter result is not as expected", 1.0, formula.interpret());
	}

	public void testaddToValue() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement firstElement = formula.findItemByPosition(1);

		firstElement.replaceValue(testAddToValueInput[0]);

		for (int index = 1; index < testAddToValueInput.length; index++) {
			firstElement.addToValue(testAddToValueInput[index]);

			assertEquals("Added value is not as expected", testAddToValueOutput[index], firstElement.getValue());
		}

	}

	public void testDeleteLastCharacterInValue() {
		Formula formula = new Formula(TEST_VALUE);
		FormulaElement firstElement = formula.findItemByPosition(1);

		for (int index = 0; index < testDeleteLastCharacterInValueInput.length; index++) {
			firstElement.replaceValue(testDeleteLastCharacterInValueInput[index]);
			firstElement.deleteLastCharacterInValue();

			assertEquals("Deleted value is not as expected", testDeleteLastCharacterInValueOutput[index],
					firstElement.getValue());
		}

	}
}
