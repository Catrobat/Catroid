/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum ElementType {
		OPERATOR, FUNCTION, NUMBER, SENSOR, USER_VARIABLE, BRACKET
	}

	public static final Double NOT_EXISTING_USER_VARIABLE_INTERPRETATION_VALUE = 0d;

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

	public String getValue() {
		return value;
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

	public Double interpretRecursive(Sprite sprite) {

		Double returnValue = 0d;

		switch (type) {
			case BRACKET:
				returnValue = rightChild.interpretRecursive(sprite);
				break;
			case NUMBER:
				returnValue = Double.parseDouble(value);
				break;
			case OPERATOR:
				Operators operator = Operators.getOperatorByValue(value);
				returnValue = interpretOperator(operator, sprite);
				break;
			case FUNCTION:
				Functions function = Functions.getFunctionByValue(value);
				returnValue = interpretFunction(function, sprite);
				break;
			case SENSOR:
				Sensors sensor = Sensors.getSensorByValue(value);
				if (sensor.isObjectSensor) {
					returnValue = interpretObjectSensor(sensor, sprite);
				} else {
					returnValue = SensorHandler.getSensorValue(sensor);
				}
				break;
			case USER_VARIABLE:
				UserVariablesContainer userVariables = ProjectManager.getInstance().getCurrentProject()
						.getUserVariables();
				UserVariable userVariable = userVariables.getUserVariable(value, sprite);
				if (userVariable == null) {
					returnValue = NOT_EXISTING_USER_VARIABLE_INTERPRETATION_VALUE;
					break;
				}
				returnValue = userVariable.getValue();
				break;

		}

		returnValue = checkDegeneratedDoubleValues(returnValue);

		return returnValue;

	}

	private Double interpretFunction(Functions function, Sprite sprite) {
		Double left = null;
		Double right = null;

		if (leftChild != null) {
			left = leftChild.interpretRecursive(sprite);
		}

		switch (function) {
			case SIN:
				return java.lang.Math.sin(Math.toRadians(left));

			case COS:
				return java.lang.Math.cos(Math.toRadians(left));

			case TAN:
				return java.lang.Math.tan(Math.toRadians(left));

			case LN:
				return java.lang.Math.log(left);

			case LOG:
				return java.lang.Math.log10(left);

			case SQRT:
				return java.lang.Math.sqrt(left);

			case RAND:
				right = rightChild.interpretRecursive(sprite);
				Double minimum = java.lang.Math.min(left, right);
				Double maximum = java.lang.Math.max(left, right);

				Double randomDouble = minimum + (java.lang.Math.random() * (maximum - minimum));

				if (isInteger(minimum) && isInteger(maximum)
						&& !(rightChild.type == ElementType.NUMBER && rightChild.value.contains("."))
						&& !(leftChild.type == ElementType.NUMBER && leftChild.value.contains("."))) {
					Log.i("info", "randomDouble: " + randomDouble);

					if ((Math.abs(randomDouble) - (int) Math.abs(randomDouble)) >= 0.5) {
						return Double.valueOf(randomDouble.intValue()) + 1;
					} else {
						return Double.valueOf(randomDouble.intValue());
					}

				} else {
					return randomDouble;
				}

			case ABS:
				return java.lang.Math.abs(left);

			case ROUND:
				return (double) java.lang.Math.round(left);

			case PI:
				return java.lang.Math.PI;

			case MOD:
				double dividend = left;
				double divisor = rightChild.interpretRecursive(sprite);

				if (dividend == 0 || divisor == 0) {
					return dividend;
				}

				if (divisor > 0) {
					while (dividend < 0) {
						dividend += java.lang.Math.abs(divisor);
					}
				} else {
					if (dividend > 0) {
						return (dividend % divisor) + divisor;
					}
				}

				return dividend % divisor;

			case ARCSIN:
				return java.lang.Math.toDegrees(Math.asin(left));
			case ARCCOS:
				return java.lang.Math.toDegrees(Math.acos(left));
			case ARCTAN:
				return java.lang.Math.toDegrees(Math.atan(left));
			case EXP:
				return java.lang.Math.exp(left);
			case MAX:
				right = rightChild.interpretRecursive(sprite);
				return java.lang.Math.max(left, right);
			case MIN:
				right = rightChild.interpretRecursive(sprite);
				return java.lang.Math.min(left, right);

			case TRUE:
				return 1.0;

			case FALSE:
				return 0.0;

		}

		return 0d;
	}

	private Double interpretOperator(Operators operator, Sprite sprite) {

		if (leftChild != null) {// binary operator
			Double left = leftChild.interpretRecursive(sprite);
			Double right = rightChild.interpretRecursive(sprite);

			switch (operator) {
				case PLUS:
					return left + right;
				case MINUS:
					return left - right;
				case MULT:
					return left * right;
				case DIVIDE:
					return left / right;
				case POW:
					return java.lang.Math.pow(left, right);
				case EQUAL:
					return left.equals(right) ? 1d : 0d; //TODO Double equality, may round first?
				case NOT_EQUAL:
					return left.equals(right) ? 0d : 1d;//TODO Double equality, may round first?
				case GREATER_THAN:
					return left.compareTo(right) > 0 ? 1d : 0d;
				case GREATER_OR_EQUAL:
					return left.compareTo(right) >= 0 ? 1d : 0d;
				case SMALLER_THAN:
					return left.compareTo(right) < 0 ? 1d : 0d;
				case SMALLER_OR_EQUAL:
					return left.compareTo(right) <= 0 ? 1d : 0d;
				case LOGICAL_AND:
					return (left * right) != 0d ? 1d : 0d;
				case LOGICAL_OR:
					return left != 0d || right != 0d ? 1d : 0d;
			}

		} else {//unary operators
			Double right = rightChild.interpretRecursive(sprite);

			switch (operator) {
				case MINUS:
					return -right;
				case LOGICAL_NOT:
					return right == 0d ? 1d : 0d;
			}

		}

		return 0d;
	}

	private Double interpretObjectSensor(Sensors sensor, Sprite sprite) {
		Double returnValue = 0d;
		switch (sensor) {
			case OBJECT_BRIGHTNESS:
				returnValue = (double) sprite.look.getBrightnessInUserInterfaceDimensionUnit();
				break;
			case OBJECT_GHOSTEFFECT:
				returnValue = (double) sprite.look.getTransparencyInUserInterfaceDimensionUnit();
				break;
			case OBJECT_LAYER:
				returnValue = (double) sprite.look.getZIndex();
				break;
			case OBJECT_ROTATION:
				returnValue = (double) sprite.look.getDirectionInUserInterfaceDimensionUnit();
				break;
			case OBJECT_SIZE:
				returnValue = (double) sprite.look.getSizeInUserInterfaceDimensionUnit();
				break;
			case OBJECT_X:
				returnValue = (double) sprite.look.getXInUserInterfaceDimensionUnit();
				break;
			case OBJECT_Y:
				returnValue = (double) sprite.look.getYInUserInterfaceDimensionUnit();
				break;
		}
		return returnValue;
	}

	private Double checkDegeneratedDoubleValues(Double valueToCheck) {
		if (valueToCheck == null) {
			return 1.0;
		}
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

	public void replaceWithSubElement(String operator, FormulaElement rightChild) {

		FormulaElement cloneThis = new FormulaElement(ElementType.OPERATOR, operator, this.getParent(), this,
				rightChild);

		cloneThis.parent.rightChild = cloneThis;
	}

	private boolean isInteger(double value) {

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
		if (type.equals(elementType) 
				|| (leftChild != null && leftChild.containsElement(elementType)) 
				|| (rightChild != null && rightChild.containsElement(elementType))) {
			return true;
		}
		return false;
	}

	public boolean isSingleNumberFormula() {
		if (type == ElementType.OPERATOR) {
			Operators operator = Operators.getOperatorByValue(value);
			if (operator == Operators.MINUS && leftChild == null) {
				return rightChild.isSingleNumberFormula();
			}
			return false;
		} else if (type == ElementType.NUMBER) {
			return true;
		}
		return false;
	}

	@Override
	public FormulaElement clone() {
		FormulaElement leftChildClone = leftChild == null ? null : leftChild.clone();
		FormulaElement rightChildClone = rightChild == null ? null : rightChild.clone();
		return new FormulaElement(type, new String(value == null ? "" : value), null, leftChildClone, rightChildClone);
	}

}
