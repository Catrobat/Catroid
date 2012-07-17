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

	public FormulaElement(int type, String value, FormulaElement parent, FormulaElement leftChild,
			FormulaElement rightChild) {
		this.type = type;
		this.value = value;
		this.parent = parent;
		this.leftChild = leftChild;
		this.rightChild = rightChild;

		if (leftChild != null) {
			this.leftChild.parent = this;
		}
		if (rightChild != null) {
			this.rightChild.parent = this;
		}

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

	//	private void addChild(FormulaElement element) {
	//		if (leftChild == null) {
	//			leftChild = element;
	//		} else {
	//			rightChild = element;
	//		}
	//	}

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

		if (type == ELEMENT_VALUE) {
			return Double.parseDouble(value);
		} else {
			Double left = leftChild.interpretRecursive();
			Double right = rightChild.interpretRecursive();

			if (value.equals("+")) {
				return left + right;
			}
			if (value.equals("-")) {
				return left - right;
			}
			if (value.equals("*")) {
				return left * right;
			}
			if (value.equals("/")) {
				return left / right;
			}
		}

		return null;

	}

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

	public void setRightChild(FormulaElement rightChild) {
		this.rightChild = rightChild;
		this.rightChild.parent = this;
	}

	//-------------------------------------------------------------------

	public void replaceElement(FormulaElement current) {
		parent = current.parent;
		leftChild = current.leftChild;
		rightChild = current.rightChild;
		value = current.value;
		type = current.type;

		if (leftChild != null) {
			leftChild.parent = this;
		}
		if (rightChild != null) {
			rightChild.parent = this;
		}
	}

	public void replaceElement(int type, String value) {
		this.value = value;
		this.type = type;
	}

	public void replaceElement(int type, String value, FormulaElement parent, FormulaElement leftChild,
			FormulaElement rightChild) {
		this.value = value;
		this.type = type;
		this.parent = parent;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	public void replaceElement(int type, String value, FormulaElement leftChild, FormulaElement rightChild) {
		this.value = value;
		this.type = type;
		this.leftChild = leftChild;
		if (this.leftChild != null) {
			this.leftChild.parent = this;
		}
		this.rightChild = rightChild;
		if (rightChild != null) {
			this.rightChild.parent = this;
		}
	}

	//-------------------------------------------------------------------

	public FormulaElement addTopElement(String newParentOperator, FormulaElement newRightChild) {
		Log.i("info", "replaceWithTopElement");

		FormulaElement newParent = new FormulaElement(ELEMENT_OP_OR_FCT, newParentOperator, null, this, newRightChild);

		return newParent;
	}

	public void replaceWithSubElement(String operator, FormulaElement rightChild) {
		Log.i("info", "replaceWithSubElement");

		FormulaElement cloneThis = new FormulaElement(this.type, operator, this.getParent(), this, rightChild);

		cloneThis.parent.rightChild = cloneThis;
	}

	public void replaceWithSubElement(String leftChild, String operator, String rightChild) {
		if (getParent() == null) {
			Log.i("info", "WARNING! ROOT ELEMENT BEING REPLACES");
		}

		if (leftChild != null) {
			Log.i("info", "Delete all previous children and replace with new ones: ");
		}

		this.value = operator;
		this.type = ELEMENT_OP_OR_FCT;
		this.leftChild = new FormulaElement(ELEMENT_VALUE, leftChild, this);
		this.rightChild = new FormulaElement(ELEMENT_VALUE, rightChild, this);

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
