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
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int ELEMENT_FIRST_VALUE_REPLACED_BY_CHILDREN = -4;
	public static final int ELEMENT_SECOND_VALUE_REPLACED_BY_CHILDREN = -3;
	public static final int SEARCHING_FOR_PARENT_HACK = -2;
	public static final int ELEMENT_REPLACED_BY_CHILDREN = -1;
	public static final int ELEMENT_FUNCTION = 0;
	public static final int ELEMENT_FIRST_VALUE = 1;
	public static final int ELEMENT_OPERATOR = 2;
	public static final int ELEMENT_SECOND_VALUE = 3;

	private int type;
	private String value;
	private List<FormulaElement> children = null;
	private FormulaElement parent = null;

	public FormulaElement(int type, String value, FormulaElement parent) {
		this.type = type;
		this.value = value;
		this.parent = parent;
	}

	public int getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public FormulaElement getChildOfType(int type) {

		if (children == null) {
			Log.i("info", "Get Child null ");
			return null;
		}

		for (FormulaElement item : children) {
			if (item != null) {
				if (item.type == type) {
					return item;
				}
			}
		}
		return null;
	}

	//	public FormulaElement getItemWithId(int searchedId) {
	//		FormulaElement result = null;
	//		if (this.id == searchedId) {
	//			return this;
	//		} else if (children == null) {
	//			return null;
	//		} else {
	//			for (FormulaElement nextChild : children) {
	//				result = nextChild.getItemWithId(searchedId);
	//				if (result != null) {
	//					break;
	//				}
	//			}
	//		}
	//		return result;
	//	}

	//	public List<FormulaElement> getAllChildren(int searchedId) {
	//
	//		List<FormulaElement> result = null;
	//
	//		if (this.id == searchedId) {
	//			return children;
	//		} else if (children == null) {
	//			return null;
	//		} else {
	//			for (FormulaElement nextChild : children) {
	//				result = nextChild.getAllChildren(searchedId);
	//				if (result != null) {
	//					break;
	//				}
	//			}
	//		}
	//		return result;
	//	}

	private void addChild(FormulaElement element) {
		if (children == null) {
			children = new ArrayList<FormulaElement>();
		}
		children.add(element);
	}

	public FormulaElement getItemByPosition(MutableInteger position) {
		FormulaElement result = null;
		if (children == null) {
			if (position.i == 0) {
				return this;
			} else {
				position.i--;
				return null;
			}

		} else {
			for (FormulaElement nextChild : children) {
				result = nextChild.getItemByPosition(position);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public int getNumberOfRecursiveChildren() {
		if (children == null) {
			return 1;

		} else {
			int result = 0;
			for (FormulaElement nextChild : children) {
				result += nextChild.getNumberOfRecursiveChildren();
			}
			return result;
		}
	}

	//	public FormulaElement getParentByPosition(MutableInteger position) {
	//
	//		Log.i("info", "FE: get parent by position: " + position.i);
	//		FormulaElement result = null;
	//		if (children == null) {
	//			Log.i("info", "FE: get parent by position, null " + position.i);
	//			if (position.i == 0) {
	//				return new FormulaElement(SEARCHING_FOR_PARENT_HACK, "", null);
	//			} else {
	//				position.i--;
	//				return null;
	//			}
	//
	//		} else {
	//			for (FormulaElement nextChild : children) {
	//				Log.i("info", "FE: get parent by position, iterating children " + position.i);
	//				result = nextChild.getParentByPosition(position);
	//				if (result != null) {
	//					if (result.type == SEARCHING_FOR_PARENT_HACK) {
	//						result = this;
	//						break;
	//					}
	//				}
	//			}
	//		}
	//		return result;
	//	}

	public void replaceValue(String value) {
		this.value = value;
	}

	public FormulaElement getParent() {
		return parent;
	}

	/**
	 * Represents: Function(value1 operator value2) Note: only values may have further childs!
	 * functionName and operator are always terminal! May be changed to different one though
	 * 
	 * @param functionName
	 *            null if represents no function, function could be: sin, cod, rand,...
	 * @param value1
	 *            first value, usually a number, must never be null!
	 * @param operator
	 *            the operator, +,-,*,/ or , if represents a function. null if one-param function
	 * @param value2
	 *            second value, usually a number, can be null
	 */
	public void replaceWithChildren(String functionName, String value1, String operator, String value2,
			FormulaElement parent) {
		this.value = null;
		if (this.type == ELEMENT_FIRST_VALUE) {
			this.type = ELEMENT_FIRST_VALUE_REPLACED_BY_CHILDREN;
		} else if (this.type == ELEMENT_SECOND_VALUE) {
			this.type = ELEMENT_SECOND_VALUE_REPLACED_BY_CHILDREN;
		} else {
			this.type = ELEMENT_REPLACED_BY_CHILDREN;
		}
		if (children != null) {
			Log.i("info", "Uh-oh, thats bad!");
		}
		if (functionName != null) {
			addChild(new FormulaElement(ELEMENT_FUNCTION, functionName, parent));
		}
		if (value1 != null) {
			addChild(new FormulaElement(ELEMENT_FIRST_VALUE, value1, parent));
		}
		if (operator != null) {
			addChild(new FormulaElement(ELEMENT_OPERATOR, operator, parent));
		}
		if (value2 != null) {
			addChild(new FormulaElement(ELEMENT_SECOND_VALUE, value2, parent));
		}
	}

	@Override
	public String toString() {
		return value;

	}

}
