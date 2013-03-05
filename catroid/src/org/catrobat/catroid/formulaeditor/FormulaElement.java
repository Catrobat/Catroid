/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import android.util.Log;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum ElementType {
		OPERATOR, FUNCTION, NUMBER, SENSOR, USER_VARIABLE, BRACKET
	}

	private ElementType type;
	private String value;
	private FormulaElement leftChild = null;
	private FormulaElement rightChild = null;
	private transient FormulaElement parent = null;

	public FormulaElement(ElementType type, String value, FormulaElement parent) {
		this.type = type;
		this.value = value;
		this.parent = parent;
	}

	public FormulaElement(ElementType type, String value, FormulaElement parent, FormulaElement leftChild,
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

	public ElementType getType() {
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

	public List<InternToken> getInternTokenList() {
		List<InternToken> internTokenList = new LinkedList<InternToken>();

		switch (type) {
			case BRACKET:
				internTokenList.add(new InternToken(InternTokenType.BRACKET_OPEN));
				if (rightChild != null) {
					internTokenList.addAll(rightChild.getInternTokenList());
				}
				internTokenList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
				break;
			case OPERATOR:
				if (leftChild != null) {
					internTokenList.addAll(leftChild.getInternTokenList());
				}
				internTokenList.add(new InternToken(InternTokenType.OPERATOR, this.value));
				if (rightChild != null) {
					internTokenList.addAll(rightChild.getInternTokenList());
				}
				break;
			case FUNCTION:
				internTokenList.add(new InternToken(InternTokenType.FUNCTION_NAME, value));
				boolean functionHasParameters = false;
				if (leftChild != null) {
					internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
					functionHasParameters = true;
					internTokenList.addAll(leftChild.getInternTokenList());
				}
				if (rightChild != null) {
					internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
					internTokenList.addAll(rightChild.getInternTokenList());
				}
				if (functionHasParameters) {
					internTokenList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
				}
				break;
			case USER_VARIABLE:
				internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, this.value));
				break;
			case NUMBER:
				internTokenList.add(new InternToken(InternTokenType.NUMBER, this.value));
				break;
			case SENSOR:
				internTokenList.add(new InternToken(InternTokenType.SENSOR, this.value));
				break;
		}
		return internTokenList;
	}

	public FormulaElement getRoot() {
		FormulaElement root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	public Double interpretRecursive() {

		Double returnValue = 0d;

		if (type == ElementType.BRACKET) {
			returnValue = rightChild.interpretRecursive();
		}
		if (type == ElementType.NUMBER) {
			returnValue = Double.parseDouble(value);
		} else if (type == ElementType.OPERATOR) {
			if (leftChild != null) {// binär operator
				Double left = leftChild.interpretRecursive();
				Double right = rightChild.interpretRecursive();

				if (value.equals(Operators.PLUS.operatorName)) {
					returnValue = left + right;
				}
				if (value.equals(Operators.MINUS.operatorName)) {
					returnValue = left - right;
				}
				if (value.equals(Operators.MULT.operatorName)) {
					returnValue = left * right;
				}
				if (value.equals(Operators.DIVIDE.operatorName)) {

					returnValue = left / right;
				}
				if (value.equals(Operators.POW.operatorName)) {
					returnValue = java.lang.Math.pow(left, right);
				}
				if (value.equals(Operators.EQUAL.operatorName)) {
					returnValue = left.equals(right) ? 1d : 0d; //TODO Double equality, may round first?
				}
				if (value.equals(Operators.NOT_EQUAL.operatorName)) {
					returnValue = left.equals(right) ? 0d : 1d;//TODO Double equality, may round first?
				}
				if (value.equals(Operators.GREATER_THAN.operatorName)) {
					returnValue = left.compareTo(right) > 0 ? 1d : 0d;
				}
				if (value.equals(Operators.GREATER_OR_EQUAL.operatorName)) {
					returnValue = left.compareTo(right) >= 0 ? 1d : 0d;
				}
				if (value.equals(Operators.SMALLER_THAN.operatorName)) {
					returnValue = left.compareTo(right) < 0 ? 1d : 0d;
				}
				if (value.equals(Operators.SMALLER_OR_EQUAL.operatorName)) {
					returnValue = left.compareTo(right) <= 0 ? 1d : 0d;
				}
				if (value.equals(Operators.LOGICAL_AND.operatorName)) {
					returnValue = (left * right) != 0d ? 1d : 0d;
				}
				if (value.equals(Operators.LOGICAL_OR.operatorName)) {
					returnValue = left != 0d || right != 0d ? 1d : 0d;
				}
			} else {//unary operators
				Double right = rightChild.interpretRecursive();
				//				if (value.equals("+")) {
				//					return right;
				//				}
				if (value.equals(Operators.MINUS.operatorName)) {
					returnValue = -right;
				}
				if (value.equals(Operators.LOGICAL_NOT.operatorName)) {
					returnValue = right == 0d ? 1d : 0d;
				}

			}
		} else if (type == ElementType.FUNCTION) {
			Double left = 0.0d;
			if (leftChild != null) {
				left = leftChild.interpretRecursive();
			}

			if (value.equals(Functions.SIN.functionName)) {
				returnValue = java.lang.Math.sin(Math.toRadians(left));
			}
			if (value.equals(Functions.COS.functionName)) {
				returnValue = java.lang.Math.cos(Math.toRadians(left));
			}
			if (value.equals(Functions.TAN.functionName)) {
				returnValue = java.lang.Math.tan(Math.toRadians(left));
			}
			if (value.equals(Functions.LN.functionName)) {
				returnValue = java.lang.Math.log(left);
			}
			if (value.equals(Functions.LOG.functionName)) {
				returnValue = java.lang.Math.log10(left);
			}
			if (value.equals(Functions.SQRT.functionName)) {
				returnValue = java.lang.Math.sqrt(left);
			}
			if (value.equals(Functions.RAND.functionName)) {

				Double right = rightChild.interpretRecursive();
				Double minimum;
				Double maximum;

				if (right > left) {
					minimum = left;
					maximum = right;
				} else {
					minimum = right;
					maximum = left;
				}

				Double randomDouble = minimum + (java.lang.Math.random() * (maximum - minimum));

				if (isInteger(minimum) && isInteger(maximum)
						&& !(rightChild.type == ElementType.NUMBER && rightChild.value.contains("."))
						&& !(leftChild.type == ElementType.NUMBER && leftChild.value.contains("."))) {
					Log.i("info", "randomDouble: " + randomDouble);

					if ((Math.abs(randomDouble) - (int) Math.abs(randomDouble)) >= 0.5) {
						returnValue = Double.valueOf(randomDouble.intValue()) + 1;
					} else {
						returnValue = Double.valueOf(randomDouble.intValue());
					}

				} else {
					returnValue = randomDouble;
				}

			}
			if (value.equals(Functions.ABS.functionName)) {
				returnValue = java.lang.Math.abs(left);
			}
			if (value.equals(Functions.ROUND.functionName)) {
				returnValue = (double) java.lang.Math.round(left);
			}
			if (value.equals(Functions.PI.functionName)) {
				returnValue = java.lang.Math.PI;
			}
		} else if (type == ElementType.SENSOR) {
			returnValue = SensorHandler.getSensorValue(value);
		} else if (type == ElementType.USER_VARIABLE) {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			String spriteName = currentSprite == null ? "" : currentSprite.getName();
			String threadName = Thread.currentThread().getName();
			if (threadName.startsWith(Sprite.SCRIPT_THREAD_NAME_PREFIX)) {
				spriteName = Thread.currentThread().getName().substring(Sprite.SCRIPT_THREAD_NAME_PREFIX.length()); //TODO do not save in Thread
			}
			UserVariablesContainer userVariables = ProjectManager.getInstance().getCurrentProject().getUserVariables();
			UserVariable userVariable = userVariables.getUserVariable(value, spriteName);
			if (userVariable == null) {
				return 0d; //TODO handle case, when user-variable does not exist
			}

			return userVariable.getValue();
		}

		returnValue = checkDegeneratedDoubleValues(returnValue);

		return returnValue;

	}

	private Double checkDegeneratedDoubleValues(Double valueToCheck) {
		if (valueToCheck.doubleValue() == Double.NEGATIVE_INFINITY) {
			return -Double.MAX_VALUE;
		}
		if (valueToCheck.doubleValue() == Double.POSITIVE_INFINITY) {
			return Double.MAX_VALUE;
		}
		if (valueToCheck.isNaN()) {
			return 1.0;
		}

		return valueToCheck;
	}

	public FormulaElement getParent() {
		return parent;
	}

	public void setRightChild(FormulaElement rightChild) {
		this.rightChild = rightChild;
		this.rightChild.parent = this;
	}

	public void setLeftChild(FormulaElement leftChild) {
		this.leftChild = leftChild;
		this.leftChild.parent = this;
	}

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

	public void replaceElement(ElementType type, String value) {
		this.value = value;
		this.type = type;
	}

	public void replaceElement(ElementType type, String value, FormulaElement leftChild, FormulaElement rightChild) {
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

	public void replaceWithSubElement(String operator, FormulaElement rightChild) {

		FormulaElement cloneThis = new FormulaElement(ElementType.OPERATOR, operator, this.getParent(), this,
				rightChild);

		cloneThis.parent.rightChild = cloneThis;
	}

	@Override
	public String toString() {
		return value;

	}

	//TODO just for debugging -> remove
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

	private boolean isInteger(double value) {
		//		Log.i("info", "isInteger().value=" + value + "(int) value= " + (int) value);

		if ((Math.abs(value) - (int) Math.abs(value) < Double.MIN_VALUE)) {
			return true;
		}

		return false;
	}

	public boolean isLogicalOperator() {
		if (type == ElementType.OPERATOR) {
			return Operators.getOperatorByValue(value).isLogicalOperator;
		}
		return false;
	}

	public boolean containsElement(ElementType elementType) {
		if (type.equals(elementType)) {
			return true;
		}

		if (leftChild != null && leftChild.containsElement(elementType)) {
			return true;
		}

		if (rightChild != null && rightChild.containsElement(elementType)) {
			return true;
		}
		return false;
	}

}
