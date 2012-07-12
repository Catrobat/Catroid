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
	public static final String[] NUMBERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ",", "." };
	public static final String[] OPERATORS = new String[] { "+", "-", "*", "/", "^", "," };
	public static final String[] FUNCTIONS = new String[] { "rand" };

	public Formula() {
		root = new FormulaElement(FormulaElement.ELEMENT_REPLACED_BY_CHILDREN, "root", null);
	}

	public Formula(String value) {
		root = new FormulaElement(FormulaElement.ELEMENT_REPLACED_BY_CHILDREN, "root", null);
		root.replaceWithChildren(null, value, null, null);
	}

	//	public void addChildByTextPosition(int position, String functionName, String value1, String operator, String value2) {
	//		FormulaElement parentItem = findItemByPosition(position);
	//		parentItem.replaceWithChildren(functionName, value1, operator, value2);
	//	}

	public FormulaElement findItemByPosition(int position) {
		if (position == 0) {
			return root;
		}
		MutableInteger searchPosition = new MutableInteger(position - 1);
		return root.getItemByPosition(searchPosition);
	}

	//	public FormulaElement getParentOfItemByPosition(int position) {
	//		Log.i("info", "F: Find parent by pos " + position);
	//		MutableInteger searchPosition = new MutableInteger(position);
	//		return root.getParentByPosition(searchPosition);
	//	}

	public String addToFormula(String keyboardInput, FormulaElement parent) {

		String oldValue = parent.getValue();

		if (parent.getType() == FormulaElement.ELEMENT_REPLACED_BY_CHILDREN) {
			oldValue = "0";
		}
		parent.replaceWithChildren(null, oldValue, keyboardInput, "0");

		return oldValue + " " + keyboardInput + " 0";
	}

	public int interpret() {

		Log.i("info", root.getTreeString());
		return root.interpretRecursive();
	}

	public String stringRepresentation() {
		return root.getTreeString();
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
