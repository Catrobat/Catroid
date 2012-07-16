/**
+ *  Catroid: An on-device graphical programming language for Android devices
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
package at.tugraz.ist.catroid.content;

import java.io.Serializable;

import android.util.Log;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int ROOT_ELEMENT = 0;
	private FormulaElement root;
	public static final String[] NUMBERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
	public static final String[] OPERATORS = new String[] { "+", "-", "*", "/", "^" };
	public static final String[] FUNCTIONS = new String[] { "sin", "cos", "tan", "ln", "log", "pi", "sqrt", "e", "rand" };

	public Formula() {
		root = new FormulaElement(FormulaElement.ELEMENT_VALUE, "0", null);
	}

	public Formula(String value) {
		root = new FormulaElement(FormulaElement.ELEMENT_VALUE, value, null);
	}

	public FormulaElement findItemByPosition(int position) {
		Log.i("info", "F: Find item by pos " + position);
		if (position == 0) {
			return root;
		}
		MutableInteger searchPosition = new MutableInteger(position - 1);
		return root.getItemByPosition(searchPosition);
	}

	public void insertElementAsParent(FormulaElement parent, String newParentOperator, String newRightChild) {
		root = parent.addTopElement(newParentOperator, newRightChild);
	}

	public void insertElement(FormulaElement parent, String newParentOperator, String newRightChild) {
		parent.replaceWithSubElement(newParentOperator, newRightChild);

	}

	public String updateFormula(String keyboardInput, FormulaElement element) {
		String oldValue = element.getValue();
		Log.i("info", "update TO FORMULA " + oldValue);

		element.replaceWithSubElement(oldValue, keyboardInput, "0");

		return oldValue + " " + keyboardInput + " 0";
	}

	public Double interpret() {

		Log.i("info", root.getTreeString());
		return root.interpretRecursive();
	}

	public String stringRepresentation() {
		return root.getTreeString();
	}

	public String getEditTextRepresentation() {
		return root.getEditTextRepresentation();
	}

	public static boolean isInputMemberOfAGroup(String input, final String[] group) {

		boolean isMember = false;

		for (String item : group) {
			if (item.equals(input)) {
				isMember = true;
			}
		}
		return isMember;

	}

}
