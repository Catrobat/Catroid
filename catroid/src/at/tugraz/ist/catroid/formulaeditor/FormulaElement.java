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
package at.tugraz.ist.catroid.formulaeditor;

import java.io.Serializable;

import android.util.Log;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int ELEMENT_OPERATOR = 2;
	public static final int ELEMENT_FUNCTION = 3;
	public static final int ELEMENT_VALUE = 4;
	public static final int ELEMENT_SENSOR = 5;
	public static final int ELEMENT_CONSTANT = 6;
	public static final int ELEMENT_VARIABLE = 7;

	//	private static HashMap<String, Integer> variableMap = new HashMap<String, Integer>(); TODO
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
		String result = "";

		switch (type) {
			case ELEMENT_OPERATOR:
				if (leftChild != null) {
					result += leftChild.getEditTextRepresentation();
				}
				result += " " + this.value + " ";
				if (rightChild != null) {
					result += rightChild.getEditTextRepresentation();
				}
				break;
			case ELEMENT_FUNCTION:
				result += this.value + "( ";
				if (leftChild != null) {
					result += leftChild.getEditTextRepresentation();
				}
				if (rightChild != null) {
					result += ", ";
					result += rightChild.getEditTextRepresentation();
				}
				result += ") ";
				break;
			case ELEMENT_VALUE:
				result += this.value + " ";
				break;
		}
		return result;
	}

	public FormulaElement getRoot() {
		FormulaElement root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	public String getTreeString() {
		String text = "";

		text = "(" + type + "/" + value + " ";

		if (leftChild == null && rightChild == null) {
			return text + ") ";
		}

		if (leftChild != null) {
			text += leftChild.getTreeString() + " ";

		} else {
			text += "( )";
		}
		if (rightChild != null) {
			text += rightChild.getTreeString() + " ";
		} else {
			text += "( )";
		}
		return text + ") ";
	}

	public Double interpretRecursive() {

		if (type == ELEMENT_VALUE) {
			return Double.parseDouble(value);
		} else if (type == ELEMENT_OPERATOR) {
			if (leftChild != null) {// binär operator
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
			} else {//unär operators
				Double right = rightChild.interpretRecursive();
				//				if (value.equals("+")) {
				//					return right;
				//				}
				if (value.equals("-")) {
					return -right;
				}

			}
		} else if (type == ELEMENT_FUNCTION) {
			Double left = 0.0d;
			if (leftChild != null) {
				left = leftChild.interpretRecursive();
			}

			if (value.equals("sin")) {
				return java.lang.Math.sin(left);
			}
			if (value.equals("cos")) {
				return java.lang.Math.cos(left);
			}
			if (value.equals("tan")) {
				return java.lang.Math.tan(left);
			}
			if (value.equals("ln")) {
				return java.lang.Math.log1p(left);// TODO check this X_X
			}
			if (value.equals("log")) {
				return java.lang.Math.log(left);
			}
			if (value.equals("sqrt")) {
				return java.lang.Math.sqrt(left);
			}
			if (value.equals("rand")) {
				double min = left;
				double max = rightChild.interpretRecursive();
				return min + (java.lang.Math.random() * (max - min));
			}
			//			if (value.equals("sensor1")) {
			//				return 1.0d;
			//			}
			//			if (value.equals("sensor2")) {
			//				return 2.0d;
			//			}
			//			if (value.equals("sensor3")) {
			//				return 3.0d;
			//			}
			//			if (value.equals("sensor4")) {
			//				return 4.0d;
			//			}
			//			if (value.equals("sensor5")) {
			//				return 5.0d;
			//			}
		} else if (type == ELEMENT_CONSTANT) {
			if (value.equals("pi")) {
				return java.lang.Math.PI;
			}
			if (value.equals("e")) {
				return java.lang.Math.E;
			}
		} else if (type == ELEMENT_VARIABLE) {
			//			TODO ^_^
			return null;
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

		FormulaElement newParent = new FormulaElement(ELEMENT_OPERATOR, newParentOperator, null, this, newRightChild);

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
		this.type = ELEMENT_OPERATOR;
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
