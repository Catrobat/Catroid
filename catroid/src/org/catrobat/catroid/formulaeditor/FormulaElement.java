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

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum ElementType {
		OPERATOR, FUNCTION, NUMBER, SENSOR, USER_VARIABLE, USER_LIST, BRACKET, STRING
	}

	public static final Double NOT_EXISTING_USER_VARIABLE_INTERPRETATION_VALUE = 0d;
	public static final Double NOT_EXISTING_USER_LIST_INTERPRETATION_VALUE = 0d;

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

	public ElementType getElementType() {
		return type;
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
			case USER_LIST:
				internTokenList.add(new InternToken(InternTokenType.USER_LIST, this.value));
			case NUMBER:
				internTokenList.add(new InternToken(InternTokenType.NUMBER, this.value));
				break;
			case SENSOR:
				internTokenList.add(new InternToken(InternTokenType.SENSOR, this.value));
				break;
			case STRING:
				internTokenList.add(new InternToken(InternTokenType.STRING, value));
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

	public void updateVariableReferences(String oldName, String newName, Context context) {
		if (leftChild != null) {
			leftChild.updateVariableReferences(oldName, newName, context);
		}
		if (rightChild != null) {
			rightChild.updateVariableReferences(oldName, newName, context);
		}
		if (type == ElementType.USER_VARIABLE && value.equals(oldName)) {
				value = newName;
		}
	}

	public Object interpretRecursive(Sprite sprite) {

		Object returnValue = 0d;

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
				returnValue = interpretSensor(sprite);
				break;
			case USER_VARIABLE:
				returnValue = interpretUserVariable(sprite);
				break;
			case USER_LIST:
				returnValue = interpretUserList(sprite);
				break;
			case STRING:
				returnValue = interpretString(value);
				break;
		}
		return normalizeDegeneratedDoubleValues(returnValue);
	}

	private Object interpretUserList(Sprite sprite) {
		UserListContainer userLists = ProjectManager.getInstance().getCurrentProject().getUserLists();
		UserList userList = userLists.getUserList(value, sprite);
		if (userList == null) {
			return NOT_EXISTING_USER_LIST_INTERPRETATION_VALUE;
		}

		List<Object> userListValues = userList.getList();

		if (userListValues.size() == 0) {
			return Double.valueOf(0);
		} else if (userListValues.size() == 1) {
			Object userListValue = userListValues.get(0);
			if (userListValue instanceof String) {
				return interpretListString((String) userListValue);

			} else {
				return userListValue;
			}

		} else {
			return interpretMultipleItemsUserList(userListValues);
		}
	}

	private Object interpretMultipleItemsUserList(List<Object> userListValues) {
		List<String> userListStringValues = new ArrayList<String>();
		boolean concatenateWithoutWhitespace = true;

		for (Object listValue : userListValues) {
			if (listValue instanceof Double) {
				Double doubleValueOfListItem = (Double) listValue;
				if (isNumberAIntegerBetweenZeroAndNine(doubleValueOfListItem)) {
					userListStringValues.add(doubleValueOfListItem.intValue() + "");
				} else {
					concatenateWithoutWhitespace = false;
					userListStringValues.add(listValue.toString());
				}
			} else if (listValue instanceof String) {
				String stringValueOfListItem = (String) listValue;
				if (stringValueOfListItem.length() == 1) {
					userListStringValues.add(stringValueOfListItem);
				} else {
					userListStringValues.add(stringValueOfListItem);
					concatenateWithoutWhitespace = false;
				}
			}
		}
		String concatenatedList = "";
		boolean isFirstListItem = true;
		for (String userListStringValue : userListStringValues) {

			if (!concatenateWithoutWhitespace && !isFirstListItem) {
				concatenatedList += " ";
			}
			if (isFirstListItem) {
				isFirstListItem = false;
			}
			concatenatedList += userListStringValue;
		}
		return concatenatedList;
	}

	private boolean isNumberAIntegerBetweenZeroAndNine(Double valueToCheck) {
		for (Double index = 0.0; index <= 9.0; index++) {
			if (valueToCheck.equals(index)) {
				return true;
			}
		}
		return false;
	}

	private Object interpretListString(String userListValue) {
		Double doubleValueOfListItem;
		try {
			doubleValueOfListItem = Double.valueOf(userListValue);
		} catch (NumberFormatException numberFormatException) {
			return userListValue;
		}
		return doubleValueOfListItem;
	}

	private Object interpretUserVariable(Sprite sprite) {
		UserVariablesContainer userVariables = ProjectManager.getInstance().getCurrentProject().getUserVariables();
		UserVariable userVariable = userVariables.getUserVariable(value, sprite);
		if (userVariable == null) {
			return NOT_EXISTING_USER_VARIABLE_INTERPRETATION_VALUE;
		}

		Object userVariableValue = userVariable.getValue();
		if (userVariableValue instanceof String) {
			try {
				return interpretString((String) userVariableValue);
			} catch (NumberFormatException numberFormatException) {
				return userVariableValue;
			}
		} else {
			return userVariableValue;
		}
	}

	private Object interpretSensor(Sprite sprite) {
		Sensors sensor = Sensors.getSensorByValue(value);
		if (sensor.isObjectSensor) {
			return interpretObjectSensor(sensor, sprite);
		} else {
			return SensorHandler.getSensorValue(sensor);
		}
	}

	private Object interpretFunction(Functions function, Sprite sprite) {
		Object left = null;
		Object right = null;

		if (leftChild != null) {
			left = leftChild.interpretRecursive(sprite);
			if (left instanceof String) {
				try {
					doubleValueOfLeftChild = Double.valueOf((String) left);
				} catch (NumberFormatException numberFormatException) {
					Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatException);
				}
			} else {
				doubleValueOfLeftChild = (Double) left;
			}
		}

		if (rightChild != null) {
			right = rightChild.interpretRecursive(sprite);
			if (right instanceof String) {
				try {
					doubleValueOfRightChild = Double.valueOf((String) right);
				} catch (NumberFormatException numberFormatException) {
					Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatException);
				}
			} else {
				doubleValueOfRightChild = (Double) right;
			}
		}

		switch (function) {
			case SIN:
				return left instanceof String ? 0d : java.lang.Math.sin(Math.toRadians((Double) left));
			case COS:
				return left instanceof String ? 0d : java.lang.Math.cos(Math.toRadians((Double) left));
			case TAN:
				return left instanceof String ? 0d : java.lang.Math.tan(Math.toRadians((Double) left));
			case LN:
				return left instanceof String ? 0d : java.lang.Math.log((Double) left);
			case LOG:
				return left instanceof String ? 0d : java.lang.Math.log10((Double) left);
			case SQRT:
				return left instanceof String ? 0d : java.lang.Math.sqrt((Double) left);
			case RAND:
				right = rightChild.interpretRecursive(sprite);
				return interpretFunctionRAND(right, left);
			case ABS:
				return left instanceof String ? 0d : java.lang.Math.abs((Double) left);
			case ROUND:
				return left instanceof String ? 0d : (double) java.lang.Math.round((Double) left);
			case PI:
				return java.lang.Math.PI;
			case MOD:
				right = rightChild.interpretRecursive(sprite);
				return interpretFunctionMOD(right, left);
			case ARCSIN:
				return left instanceof String ? 0d : java.lang.Math.toDegrees(Math.asin((Double) left));
			case ARCCOS:
				return left instanceof String ? 0d : java.lang.Math.toDegrees(Math.acos((Double) left));
			case ARCTAN:
				return left instanceof String ? 0d : java.lang.Math.toDegrees(Math.atan((Double) left));
			case EXP:
				return left instanceof String ? 0d : java.lang.Math.exp((Double) left);
			case MAX:
				right = rightChild.interpretRecursive(sprite);
				return (left instanceof String || right instanceof String) ? 0d : java.lang.Math.max((Double) left,
						(Double) right);
			case MIN:
				right = rightChild.interpretRecursive(sprite);
				return (left instanceof String || right instanceof String) ? 0d : java.lang.Math.min((Double) left,
						(Double) right);
			case TRUE:
				return 1d;
			case FALSE:
				return 0d;
			case LETTER:
				right = rightChild.interpretRecursive(sprite);
				return interpretFunctionLETTER(right, left);
			case LENGTH:
				return interpretFunctionLENGTH(left, sprite);
			case JOIN:
				return interpretFunctionJOIN(sprite);
			case LIST_ITEM:
				return interpretFunctionLISTITEM(left, sprite);
			case CONTAINS:
				return interpretFunctionCONTAINS(right, sprite);
		}
		return 0d;
	}

	private Object interpretFunctionCONTAINS(Object right, Sprite sprite) {
		if (leftChild.getElementType() == ElementType.USER_LIST) {
			UserListContainer userListContainer = ProjectManager.getInstance().getCurrentProject().getUserLists();
			UserList userList = userListContainer.getUserList(leftChild.getValue(), sprite);

			if (userList == null) {
				return 0d;
			}

			for (Object userListElement : userList.getList()) {
				if (interpretOperatorEqual(userListElement, right) == 1d) {
					return 1d;
				}
			}
		}

		return 0d;
	}

	private Object interpretFunctionLISTITEM(Object left, Sprite sprite) {
		UserList userList = null;
		if (rightChild.getElementType() == ElementType.USER_LIST) {
			UserListContainer userLists = ProjectManager.getInstance().getCurrentProject().getUserLists();
			userList = userLists.getUserList(rightChild.getValue(), sprite);
		}

		if (userList == null) {
			return "";
		}

		int index = 0;
		if (left instanceof String) {
			try {
				Double doubleValueOfLeftChild = Double.valueOf((String) left);
				index = doubleValueOfLeftChild.intValue();
			} catch (NumberFormatException numberFormatexception) {
				Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatexception);
			}
		} else {
			index = ((Double) left).intValue();
		}

		index--;

		if (index < 0) {
			return "";
		} else if (index >= userList.getList().size()) {
			return "";
		}

		return userList.getList().get(index);
	}

	private Object interpretFunctionJOIN(Sprite sprite) {
		return interpretFunctionJOINParameter(leftChild, sprite)
				+ interpretFunctionJOINParameter(rightChild, sprite);
	}

	private String interpretFunctionJOINParameter(FormulaElement child, Sprite sprite) {
		String parameterInterpretation = "";
		if (child != null) {
			if (child.getElementType() == ElementType.NUMBER) {
				Double number = ((Double) child.interpretRecursive(sprite));
				if (number.isNaN()) {
					parameterInterpretation = "";
				} else {
					if (isInteger(number)) {
						parameterInterpretation += number.intValue();
					} else {
						parameterInterpretation += number;
					}
				}
			} else if (child.getElementType() == ElementType.STRING) {
				parameterInterpretation = child.value;
			} else if (child.getElementType() != ElementType.STRING) {
				parameterInterpretation += child.interpretRecursive(sprite);
			}
		}
		return parameterInterpretation;
	}

	private Object interpretFunctionLENGTH(Object left, Sprite sprite) {
		if (leftChild == null) {
			return 0d;
		}
		if (leftChild.type == ElementType.NUMBER) {
			return (double) leftChild.value.length();
		}
		if (leftChild.type == ElementType.STRING) {
			return (double) leftChild.value.length();
		}
		if (leftChild.type == ElementType.USER_VARIABLE) {
			return (double) handleLengthUserVariableParameter(sprite);
		}
		if (leftChild.type == ElementType.USER_LIST) {
			return (double) handleLengthUserListParameter(sprite);
		}
		if (left instanceof Double && ((Double) left).isNaN()) {
			return 0d;
		}
		return (double) (String.valueOf(left)).length();
	}

	private Object interpretFunctionLETTER(Object right, Object left) {
		int index = 0;
		//((Double) left).intValue() - 1;
		if (left instanceof String) {
			try {
				Double doubleValueOfLeftChild = Double.valueOf((String) left);
				index = doubleValueOfLeftChild.intValue();
			} catch (NumberFormatException numberFormatexception) {
				Log.d(getClass().getSimpleName(), "Couldn't parse String", numberFormatexception);
			}
		} else {
			index = ((Double) left).intValue();
		}

		index--;

		if (index < 0) {
			return "";
		} else if (index >= String.valueOf(right).length()) {
			return "";
		}
		return String.valueOf(String.valueOf(right).charAt(index));
	}

	private Object interpretFunctionMOD(Object right, Object left) {

		if (left instanceof String || right instanceof String) {
			return 0d;
		}

		double dividend = (Double) left;
		double divisor = (Double) right;

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
	}

	private Object interpretFunctionRAND(Object right, Object left) {
		if (left instanceof String || right instanceof String) {
			return 0d;
		}

		Double minimum = java.lang.Math.min((Double) left, (Double) right);
		Double maximum = java.lang.Math.max((Double) left, (Double) right);

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
	}

	private Object interpretOperator(Operators operator, Sprite sprite) {

		if (leftChild != null) {// binary operator
			Object leftObject;
			Object rightObject;
			try {
				leftObject = leftChild.interpretRecursive(sprite);
			} catch (NumberFormatException numberFormatException) {
				leftObject = Double.valueOf(Double.NaN);
			}

			try {
				rightObject = rightChild.interpretRecursive(sprite);
			} catch (NumberFormatException numberFormatException) {
				rightObject = Double.valueOf(Double.NaN);
			}

			Double left;
			Double right;

			switch (operator) {
				case PLUS:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left + right;
				case MINUS:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left - right;
				case MULT:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left * right;
				case DIVIDE:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left / right;
				case POW:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return java.lang.Math.pow(left, right);
				case EQUAL:
					return interpretOperatorEqual(leftObject, rightObject);
				case NOT_EQUAL:
					return interpretOperatorEqual(leftObject, rightObject) == 1d ? 0d : 1d;
				case GREATER_THAN:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left.compareTo(right) > 0 ? 1d : 0d;
				case GREATER_OR_EQUAL:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left.compareTo(right) >= 0 ? 1d : 0d;
				case SMALLER_THAN:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left.compareTo(right) < 0 ? 1d : 0d;
				case SMALLER_OR_EQUAL:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left.compareTo(right) <= 0 ? 1d : 0d;
				case LOGICAL_AND:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return (left * right) != 0d ? 1d : 0d;
				case LOGICAL_OR:
					left = interpretOperator(leftObject);
					right = interpretOperator(rightObject);
					return left != 0d || right != 0d ? 1d : 0d;
			}

		} else {//unary operators
			Object right = rightChild.interpretRecursive(sprite);

			switch (operator) {
				case MINUS:
					Double result = (Double) right;
					return Double.valueOf(-result.doubleValue());
				case LOGICAL_NOT:
					return (Double) right == 0d ? 1d : 0d;
			}
		}
		return 0d;
	}

	private Object interpretObjectSensor(Sensors sensor, Sprite sprite) {
		Object returnValue = 0d;
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

	private Object interpretString(String value) {

//		if (parent == null && type != ElementType.USER_VARIABLE) {
//			Double anotherValue;
//			try {
//				anotherValue = Double.valueOf(value);
//			} catch (NumberFormatException numberFormatException) {
//				return value;
//			}
//			return anotherValue;
//		}
//
//		if (parent != null) {
//			boolean isParentAFunction = Functions.getFunctionByValue(parent.value) != null;
//			if (isParentAFunction && Functions.getFunctionByValue(parent.value).returnType == ElementType.STRING) {
//				if (Functions.getFunctionByValue(parent.value) == Functions.LETTER && parent.leftChild == this) {
//					try {
//						return Double.valueOf(value);
//					} catch (NumberFormatException numberFormatexception) {
//						return Double.valueOf(0);
//					}
//				}
//				return value;
//			}
//
//			if (isParentAFunction) {
//				try {
//					return Double.valueOf(value);
//				} catch (NumberFormatException numberFormatexception) {
//					return value;
//				}
//			}
//
//			boolean isParentAOperator = Operators.getOperatorByValue(parent.value) != null;
//			if (isParentAOperator
//					&& (Operators.getOperatorByValue(parent.value) == Operators.EQUAL || Operators
//							.getOperatorByValue(parent.value) == Operators.NOT_EQUAL)) {
//				return value;
//			}
//		}

//		if (value.length() == 0) {
//			return Double.valueOf(0.0);
//		}
	}
	private Double interpretOperatorEqual(Object left, Object right) {

		if (left instanceof String && right instanceof String) {
			try {
				return (Double.valueOf((String) left).compareTo(Double.valueOf((String) right))) == 0 ? 1d : 0;
			} catch (NumberFormatException numberFormatException) {
				int compareResult = ((String) left).compareTo((String) right);
				if (compareResult == 0) {
					return 1d;
				}
			}
		}
		if (left instanceof Double && right instanceof String) {
			try {
				int compareResult = ((Double) left).compareTo(Double.valueOf((String) right));
				if (compareResult == 0) {
					return 1d;
				}
			} catch (NumberFormatException numberFormatException) {
				return 0d;
			}
		}
		if (left instanceof String && right instanceof Double) {
			try {
				int compareResult = Double.valueOf((String) left).compareTo((Double) right);
				if (compareResult == 0) {
					return 1d;
				}
			} catch (NumberFormatException numberFormatException) {
				return 0d;
			}
		}
		if (left instanceof Double && right instanceof Double) {
			return (((Double) left).compareTo((Double) right) == 0) ? 1d : 0d;
		}
		return 0d;
	}

	private Double interpretOperator(Object object) {
		if (object instanceof String) {
			try {
				return Double.valueOf((String) object);
			} catch (NumberFormatException numberFormatException) {
				return Double.NaN;
			}
		} else {
			return (Double) object;
		}
	}

	private Object normalizeDegeneratedDoubleValues(Object valueToCheck) {

		if (valueToCheck instanceof String || valueToCheck instanceof Character) {
			return valueToCheck;
		}

		if (valueToCheck == null) {
			return 0.0;
		}

		if (((Double) valueToCheck).doubleValue() == Double.NEGATIVE_INFINITY) {
			return -Double.MAX_VALUE;
		}
		if (((Double) valueToCheck).doubleValue() == Double.POSITIVE_INFINITY) {
			return Double.MAX_VALUE;
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

	public boolean hasFunctionStringReturnType() {
		Functions function = Functions.getFunctionByValue(value);
		if (function == null) {
			return false;
		}
		return function.returnType == ElementType.STRING;
	}

	public boolean containsElement(ElementType elementType) {
		if (type.equals(elementType)
				|| (leftChild != null && leftChild.containsElement(elementType))
				|| (rightChild != null && rightChild.containsElement(elementType))) {
			return true;
		}
		return false;
	}

	public boolean isUserVariableWithTypeString(Sprite sprite) {
		if (type == ElementType.USER_VARIABLE) {
			UserVariablesContainer userVariableContainer = ProjectManager.getInstance().getCurrentProject()
					.getUserVariables();
			UserVariable userVariable = userVariableContainer.getUserVariable(value, sprite);

			Object userVariableValue = userVariable.getValue();
			if (userVariableValue instanceof String) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private int handleLengthUserVariableParameter(Sprite sprite) {
		UserVariablesContainer userVariableContainer = ProjectManager.getInstance().getCurrentProject()
				.getUserVariables();
		UserVariable userVariable = userVariableContainer.getUserVariable(leftChild.value, sprite);

		Object userVariableValue = userVariable.getValue();
		if (userVariableValue instanceof String) {
			return (String.valueOf(userVariableValue)).length();
		} else {
			if (isInteger((Double) userVariableValue)) {
				return Integer.toString(((Double) userVariableValue).intValue()).length();
			} else {
				return Double.toString(((Double) userVariableValue)).length();
			}
		}

	}

	private int handleLengthUserListParameter(Sprite sprite) {
		UserListContainer userListContainer = ProjectManager.getInstance().getCurrentProject()
				.getUserLists();
		UserList userList = userListContainer.getUserList(leftChild.value, sprite);

		if (userList == null) {
			return 0;
		}

		return userList.getList().size();

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

	public int getRequiredResources() {
		int ressources = Brick.NO_RESOURCES;
		if (leftChild != null) {
			ressources |= leftChild.getRequiredResources();
		}
		if (rightChild != null) {
			ressources |= rightChild.getRequiredResources();
		}
		if (type == ElementType.SENSOR) {
			Sensors sensor = Sensors.getSensorByValue(value);
			switch (sensor) {
				case FACE_DETECTED:
				case FACE_SIZE:
				case FACE_X_POSITION:
				case FACE_Y_POSITION:
					ressources |= Brick.FACE_DETECTION;
					break;
				default:

			}
		}
		return ressources;
	}

}
