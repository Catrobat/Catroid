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

import android.util.Log;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int ELEMENT_ROOT = -1;
	public static final int ELEMENT_OP_OR_FCT = 2;
	public static final int ELEMENT_VALUE = 3;

	private int type;
	private String value;
	private FormulaElement leftChild = null;
	private FormulaElement rightChild = null;
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

	public FormulaElement getLeftChild() {
		return leftChild;
	}

	public FormulaElement getRightChild() {
		return rightChild;
	}

	//	public FormulaElement getChildOfType(int type) {
	//
	//		if (children == null) {
	//			Log.i("info", "Get Child null ");
	//			return null;
	//		}
	//
	//		for (FormulaElement item : children) {
	//			if (item != null) {
	//				if (item.type == type) {
	//					return item;
	//				}
	//			}
	//		}
	//		return null;
	//	}

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
		if (leftChild == null) {
			leftChild = element;
		} else {
			rightChild = element;
		}
	}

	public FormulaElement getItemByPosition(MutableInteger position) {
		FormulaElement result = null;
		if (leftChild == null) {
			if (position.i == 0) {
				Log.i("info", "FE found: " + value);
				return this;
			} else {
				position.i--;
			}
		} else {
			result = leftChild.getItemByPosition(position);
			if (result != null) {
				return result;
			}
			if (position.i == 0) {
				result = this;
			} else {
				position.i--;
			}
			if (result == null) {
				result = rightChild.getItemByPosition(position);
			}
		}

		return result;
	}

	public int getNumberOfRecursiveChildren() {
		if (leftChild == null) {
			return 1;

		} else {
			int result = 0;
			result += leftChild.getNumberOfRecursiveChildren();
			result += leftChild.getNumberOfRecursiveChildren();

			return result;
		}
	}

	String getEditTextRepresentation() {
		if (leftChild == null) {
			return value + " ";
		} else {
			String result = "";

			result += leftChild.getEditTextRepresentation();
			result += this.value + " ";
			if (rightChild != null) {
				result += rightChild.getEditTextRepresentation();
			}

			return result;
		}
	}

	public String getTreeString() {
		String text = "";

		if (leftChild == null) {
			text = "(" + type + "/" + value + ")";

		} else {
			text += leftChild.getTreeString();
			text += "(" + type + "/" + value + ")";
			if (rightChild != null) {
				text += rightChild.getTreeString();
			}

		}
		return text;
	}

	public Double interpretRecursive() {

		//		switch (type) {
		//			case ELEMENT_LEFT_CHILD:
		//				return Double.parseDouble(value);
		//
		//			case ELEMENT_RIGHT_CHILD:
		//				return Double.parseDouble(value);
		//
		//			case ELEMENT_FUNCTION:
		//				//TODO: Implement Functions
		//				break;
		//
		//			case ELEMENT_ROOT:
		//			case ELEMENT_OPERATOR_AND_WAS_LEFT_CHILD:
		//			case ELEMENT_OPERATOR_AND_WAS_RIGHT_CHILD:
		//
		//				if (leftChild == null) { //TODO: should not happen!
		//					return 0.0;
		//				}
		//
		//				if (rightChild == null) {
		//					return leftChild.interpretRecursive();
		//				}
		//
		//				//				if (children.size() != 3) {
		//				//					return -1.0;
		//				//				}
		//
		//				double firstElementResult = leftChild.interpretRecursive();
		//				double secondElementResult = rightChild.interpretRecursive();
		//
		//				String operator = value;
		//				Log.e("info", operator);
		//
		//				if (operator.equals("+")) {
		//					return firstElementResult + secondElementResult;
		//				}
		//				if (operator.equals("-")) {
		//					return firstElementResult - secondElementResult;
		//				}
		//				if (operator.equals("*")) {
		//					return firstElementResult * secondElementResult;
		//				}
		//				if (operator.equals("/")) {
		//					return firstElementResult / secondElementResult;
		//				}
		//
		//				break;
		//
		//		}

		return -1.0;

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

	public void addToValue(String value) {
		this.value += value;
	}

	public boolean addCommaIfPossible() {

		if (value.contains(".")) {
			return false;
		}
		this.value += ".";
		return true;
	}

	public void deleteLastCharacterInValue() {
		value = value.substring(0, value.length() - 1);
	}

	public FormulaElement getParent() {
		return parent;
	}

	public void replaceWithChildren(String value1, String operator, String value2) {
		if (getParent() == null) {
			Log.i("info", "WARNING! ROOT ELEMENT BEING REPLACES");
		}

		if (leftChild != null) {
			Log.i("info", "Delete all previous children and replace with new ones: ");
		}

		this.value = operator;
		this.type = ELEMENT_OP_OR_FCT;
		this.leftChild = new FormulaElement(ELEMENT_VALUE, value1, this);
		this.rightChild = new FormulaElement(ELEMENT_VALUE, value2, this);

	}

	public FormulaElement makeMeALeaf(String value) {
		Log.i("info", "Delete all previous children and this becomes a leaf");

		this.value = value;
		this.leftChild = null;
		this.rightChild = null;
		this.type = ELEMENT_VALUE;

		return this;
	}

	public String getFirstChildValue() {
		String result = null;
		if (this.type == ELEMENT_VALUE) {
			result = this.value;
		} else {

			if (leftChild != null) {
				result = leftChild.getFirstChildValue();
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return value;

	}

}
