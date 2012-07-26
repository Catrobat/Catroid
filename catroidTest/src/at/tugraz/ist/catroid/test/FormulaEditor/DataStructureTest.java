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

import java.util.EnumSet;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.formulaeditor.CalcGrammarParser;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;

public class DataStructureTest extends AndroidTestCase {

	public void testParserTreeGenerationFormulas() {
		for (ParserFormulaTestData parserTest : EnumSet.allOf(ParserFormulaTestData.class)) {
			CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(parserTest.getInput());
			FormulaElement parserFormulaElement = parser.parseFormula();

			assertNotNull("Formula is not parsed correctly: " + parserTest.getInput() + "=", parserFormulaElement);

			assertEquals("Formula interpretation is not as expected: " + parserTest.getInput() + "=",
					parserTest.getOutput(), parserFormulaElement.interpretRecursive(null));
		}
	}

	public void testParserTreeGenerationInvalidFormulas() {
		for (InvalidParserFormulaTestData parserTest : EnumSet.allOf(InvalidParserFormulaTestData.class)) {
			CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(parserTest.getInput());
			FormulaElement parserFormulaElement = parser.parseFormula();

			assertNull("Invalid formula parsed: " + parserTest.getInput() + "=", parserFormulaElement);
			assertEquals("First error character position is not as expected: " + parserTest.getInput() + "=",
					parserTest.getFirstErrorPosition(), Integer.valueOf(parser.getErrorCharacterPosition()));

		}
	}
}
