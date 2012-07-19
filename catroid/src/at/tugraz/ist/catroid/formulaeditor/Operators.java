/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.formulaeditor;

import java.util.EnumSet;

public enum Operators {
	PLUS("+", 0), MINUS("-", 0), MULT("*", 1), DIVIDE("/", 1), MOD("%", 1), POW("^", 2);

	private final String value;
	private final Integer priority;

	Operators(String value, Integer priority) {
		this.value = value;
		this.priority = priority;
	}

	public int compareOperatorTo(Operators op) {
		int returnVa = 0;
		if (priority > op.priority) {
			returnVa = 1;
		}
		if (priority == op.priority) {
			returnVa = 0;
		}
		if (priority < op.priority) {
			returnVa = -1;
		}

		return returnVa;
	}

	public static Operators getOperatorByValue(String value) {
		for (Operators op : EnumSet.allOf(Operators.class)) {
			if (op.value.equals(value)) {
				return op;
			}
		}
		return null;
	}

}
