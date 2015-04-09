/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.arduino.Arduino;

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
				break;
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

	public void updateVariableReferences(String oldName, String newName) {
		if (leftChild != null) {
			leftChild.updateVariableReferences(oldName, newName);
		}
		if (rightChild != null) {
			rightChild.updateVariableReferences(oldName, newName);
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
				returnValue = value;
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
				returnValue = value;
				break;
		}
		return normalizeDegeneratedDoubleValues(returnValue);
	}

	private Object interpretUserList(Sprite sprite) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		UserList userList = dataContainer.getUserList(value, sprite);
		if (userList == null) {
			return NOT_EXISTING_USER_LIST_INTERPRETATION_VALUE;
		}

		List<Object> userListValues = userList.getList();

		if (userListValues.size() == 0) {
			return 0d;
		} else if (userListValues.size() == 1) {
			Object userListValue = userListValues.get(0);
			if (userListValue instanceof String) {
				return userListValue;

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

	private Object interpretUserVariable(Sprite sprite) {
		DataContainer userVariables = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		UserVariable userVariable = userVariables.getUserVariable(value, sprite);
		if (userVariable == null) {
			return NOT_EXISTING_USER_VARIABLE_INTERPRETATION_VALUE;
		}

		Object userVariableValue = userVariable.getValue();
		if (userVariableValue instanceof String) {
			return userVariableValue;
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

		Double doubleValueOfLeftChild = null;
		Double doubleValueOfRightChild = null;

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
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.sin(Math.toRadians(doubleValueOfLeftChild));
			case COS:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.cos(Math.toRadians(doubleValueOfLeftChild));
			case TAN:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.tan(Math.toRadians(doubleValueOfLeftChild));
			case LN:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.log(doubleValueOfLeftChild);
			case LOG:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.log10(doubleValueOfLeftChild);
			case SQRT:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.sqrt(doubleValueOfLeftChild);
			case RAND:
				return (doubleValueOfLeftChild == null || doubleValueOfRightChild == null) ? 0d : interpretFunctionRand(doubleValueOfLeftChild, doubleValueOfRightChild);
			case ABS:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.abs(doubleValueOfLeftChild);
			case ROUND:
				return doubleValueOfLeftChild == null ? 0d : (double) java.lang.Math.round(doubleValueOfLeftChild);
			case PI:
				return java.lang.Math.PI;
			case MOD:
				return (doubleValueOfLeftChild == null || doubleValueOfRightChild == null) ? 0d : interpretFunctionMod(doubleValueOfLeftChild, doubleValueOfRightChild);
			case ARCSIN:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.toDegrees(Math.asin(doubleValueOfLeftChild));
			case ARCCOS:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.toDegrees(Math.acos(doubleValueOfLeftChild));
			case ARCTAN:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.toDegrees(Math.atan(doubleValueOfLeftChild));
			case EXP:
				return doubleValueOfLeftChild == null ? 0d : java.lang.Math.exp(doubleValueOfLeftChild);
			case MAX:
				return (doubleValueOfLeftChild == null || doubleValueOfRightChild == null) ? 0d : java.lang.Math.max(doubleValueOfLeftChild,
						doubleValueOfRightChild);
			case MIN:
				return (doubleValueOfLeftChild == null || doubleValueOfRightChild == null) ? 0d : java.lang.Math.min(doubleValueOfLeftChild,
						doubleValueOfRightChild);
			case TRUE:
				return 1d;
			case FALSE:
				return 0d;
			case LETTER:
				return interpretFunctionLetter(right, left);
			case LENGTH:
				return interpretFunctionLength(left, sprite);
			case JOIN:
				return interpretFunctionJoin(sprite);
			case ARDUINODIGITAL:
				Arduino arduinoDigital = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).getDevice(BluetoothDevice.ARDUINO);
				return arduinoDigital.getDigitalArduinoPin(left.toString());
			case ARDUINOANALOG:
				return arduinoAnalog.getAnalogArduinoPin(left.toString());
		}

		case LIST_ITEM:
				return interpretFunctionListItem(left, sprite);
			case CONTAINS:
				return interpretFunctionContains(right, sprite);
			case NUMBER_OF_ITEMS:
				return interpretFunctionNumberOfItems(left, sprite);
		}
		return 0d;
	}

	private Object interpretFunctionNumberOfItems(Object left, Sprite sprite) {
		if (leftChild.type == ElementType.USER_LIST) {
			return (double) handleNumberOfItemsOfUserListParameter(sprite);
		}
		return interpretFunctionLength(left, sprite);
	}

	private Object interpretFunctionContains(Object right, Sprite sprite) {
		if (leftChild.getElementType() == ElementType.USER_LIST) {
			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			UserList userList = dataContainer.getUserList(leftChild.getValue(), sprite);

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

	private Object interpretFunctionListItem(Object left, Sprite sprite) {
		UserList userList = null;
		if (rightChild.getElementType() == ElementType.USER_LIST) {
			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			userList = dataContainer.getUserList(rightChild.getValue(), sprite);
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


	private Object interpretFunctionJoin(Sprite sprite) {
		return interpretInterpretFunctionJoinParameter(leftChild, sprite)
				+ interpretInterpretFunctionJoinParameter(rightChild, sprite);
	}

	private String interpretInterpretFunctionJoinParameter(FormulaElement child, Sprite sprite) {
		String parameterInterpretation = "";
		if (child != null) {
			if (child.getElementType() == ElementType.NUMBER) {
				Double number = Double.valueOf((String) child.interpretRecursive(sprite));
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
			} else {
				parameterInterpretation += child.interpretRecursive(sprite);
			}
		}
		return parameterInterpretation;
	}

	private Object interpretFunctionLength(Object left, Sprite sprite) {
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
			DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
			UserList userList = dataContainer.getUserList(leftChild.getValue(), sprite);
			if (userList == null) {
				return 0d;
			}
			if (userList.getList().size() == 0) {
				return 0d;
			}

			Object interpretedList = leftChild.interpretRecursive(sprite);
			if (interpretedList instanceof Double) {
				Double interpretedListDoubleValue = (Double) interpretedList;
				if (interpretedListDoubleValue.isNaN() || interpretedListDoubleValue.isInfinite()) {
					return 0d;
				}
				return (double) (String.valueOf(interpretedListDoubleValue.intValue())).length();
			}
			if (interpretedList instanceof String) {
				String interpretedListStringValue = (String) interpretedList;
				return (double) interpretedListStringValue.length();
			}
		}
		if (left instanceof Double && ((Double) left).isNaN()) {
			return 0d;
		}
		return (double) (String.valueOf(left)).length();
	}

	private Object interpretFunctionLetter(Object right, Object left) {
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
		} else if (index >= String.valueOf(right).length()) {
			return "";
		}
		return String.valueOf(String.valueOf(right).charAt(index));
	}

	private Object interpretFunctionMod(Object left, Object right) {

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

	private Object interpretFunctionRand(Object right, Object left) {
		Double minimum = java.lang.Math.min((Double) left, (Double) right);
		Double maximum = java.lang.Math.max((Double) left, (Double) right);

		Double randomDouble = minimum + (java.lang.Math.random() * (maximum - minimum));

		if (isInteger(minimum) && isInteger(maximum)
				&& !(rightChild.type == ElementType.NUMBER && rightChild.value.contains("."))
				&& !(leftChild.type == ElementType.NUMBER && leftChild.value.contains("."))) {
			if ((Math.abs(randomDouble) - (int) Math.abs(randomDouble)) >= 0.5) {
				return (double) randomDouble.intValue() + 1;
			} else {
				return (double) randomDouble.intValue();
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
				leftObject = Double.NaN;
			}

			try {
				rightObject = rightChild.interpretRecursive(sprite);
			} catch (NumberFormatException numberFormatException) {
				rightObject = Double.NaN;
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
			Object rightObject;
			try {
				rightObject = rightChild.interpretRecursive(sprite);
			} catch (NumberFormatException numberFormatException) {
				rightObject = Double.NaN;
			}

			switch (operator) {
				case MINUS:
					Double result = interpretOperator(rightObject);
					return -result;
				case LOGICAL_NOT:
					return interpretOperator(rightObject) == 0d ? 1d : 0d;
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
			case OBJECT_TRANSPARENCY:
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

		if ((Double) valueToCheck == Double.NEGATIVE_INFINITY) {
			return -Double.MAX_VALUE;
		}
		if ((Double) valueToCheck == Double.POSITIVE_INFINITY) {
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

	public FormulaElement getLeftChild() {
		return leftChild;
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
		return ((Math.abs(value) - (int) Math.abs(value)) < Double.MIN_VALUE);
	}

	public boolean isLogicalOperator() {
		return (type == ElementType.OPERATOR) && Operators.getOperatorByValue(value).isLogicalOperator;
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
			DataContainer userVariableContainer = ProjectManager.getInstance().getCurrentProject()
					.getDataContainer();
			UserVariable userVariable = userVariableContainer.getUserVariable(value, sprite);
			Object userVariableValue = userVariable.getValue();
			return userVariableValue instanceof String;
		}
		return false;
	}

	private int handleLengthUserVariableParameter(Sprite sprite) {
		DataContainer userVariableContainer = ProjectManager.getInstance().getCurrentProject()
				.getDataContainer();
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

	private int handleNumberOfItemsOfUserListParameter(Sprite sprite) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject()
				.getDataContainer();
		UserList userList = dataContainer.getUserList(leftChild.value, sprite);

		if (userList == null) {
			return 0;
		}

		return userList.getList().size();
	}

	public boolean isSingleNumberFormula() {
		if (type == ElementType.OPERATOR) {
			Operators operator = Operators.getOperatorByValue(value);
			return (operator == Operators.MINUS) && (leftChild == null) && rightChild.isSingleNumberFormula();
		} else if (type == ElementType.NUMBER) {
			return true;
		}
		return false;
	}

	@Override
	public FormulaElement clone() {
		FormulaElement leftChildClone = leftChild == null ? null : leftChild.clone();
		FormulaElement rightChildClone = rightChild == null ? null : rightChild.clone();
		return new FormulaElement(type, value == null ? "" : value, null, leftChildClone, rightChildClone);
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
