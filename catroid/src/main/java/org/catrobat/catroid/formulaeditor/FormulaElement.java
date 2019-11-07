/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.function.ArduinoFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.BinaryFunction;
import org.catrobat.catroid.formulaeditor.function.FormulaFunction;
import org.catrobat.catroid.formulaeditor.function.FunctionProvider;
import org.catrobat.catroid.formulaeditor.function.MathFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.RaspiFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.TouchFunctionProvider;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.stage.StageActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.catrobat.catroid.formulaeditor.common.Conversion.FALSE;
import static org.catrobat.catroid.formulaeditor.common.Conversion.convertArgumentToDouble;
import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ElementType {
		OPERATOR, FUNCTION, NUMBER, SENSOR, USER_VARIABLE, USER_LIST, BRACKET, STRING, COLLISION_FORMULA;
	}

	private ElementType type;
	private String value;
	private FormulaElement leftChild = null;
	private FormulaElement rightChild = null;
	private transient FormulaElement parent;
	private transient List<FunctionProvider> functionProviders;
	private transient Map<Functions, FormulaFunction> formulaFunctions;

	protected FormulaElement() {
		functionProviders = Arrays.asList(new ArduinoFunctionProvider(), new RaspiFunctionProvider(),
				new MathFunctionProvider(), new TouchFunctionProvider());

		formulaFunctions = new HashMap<>();
		initFunctionMap(formulaFunctions);
	}

	public FormulaElement(ElementType type, String value, FormulaElement parent) {
		this();
		this.type = type;
		this.value = value;
		this.parent = parent;
	}

	public FormulaElement(ElementType type, String value, FormulaElement parent, FormulaElement leftChild,
			FormulaElement rightChild) {
		this(type, value, parent);
		this.leftChild = leftChild;
		this.rightChild = rightChild;

		if (leftChild != null) {
			this.leftChild.parent = this;
		}
		if (rightChild != null) {
			this.rightChild.parent = this;
		}
	}

	private void initFunctionMap(Map<Functions, FormulaFunction> formulaFunctions) {
		for (FunctionProvider functionProvider : functionProviders) {
			functionProvider.addFunctionsToMap(formulaFunctions);
		}

		formulaFunctions.put(Functions.RAND, new BinaryFunction(this::interpretFunctionRand));
	}

	public ElementType getElementType() {
		return type;
	}

	public String getValue() {
		return trimTrailingCharacters(value);
	}

	public List<InternToken> getInternTokenList() {
		List<InternToken> internTokenList = new LinkedList<>();

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
				internTokenList.add(new InternToken(InternTokenType.OPERATOR, value));
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
				internTokenList.add(new InternToken(InternTokenType.USER_VARIABLE, value));
				break;
			case USER_LIST:
				internTokenList.add(new InternToken(InternTokenType.USER_LIST, value));
				break;
			case NUMBER:
				internTokenList.add(new InternToken(InternTokenType.NUMBER, trimTrailingCharacters(value)));
				break;
			case SENSOR:
				internTokenList.add(new InternToken(InternTokenType.SENSOR, value));
				break;
			case STRING:
				internTokenList.add(new InternToken(InternTokenType.STRING, value));
				break;
			case COLLISION_FORMULA:
				internTokenList.add(new InternToken(InternTokenType.COLLISION_FORMULA, value));
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

	public void updateListName(String oldName, String newName) {
		if (leftChild != null) {
			leftChild.updateVariableReferences(oldName, newName);
		}
		if (rightChild != null) {
			rightChild.updateVariableReferences(oldName, newName);
		}
		if (type == ElementType.USER_LIST && value.equals(oldName)) {
			value = newName;
		}
	}

	public void getVariableAndListNames(List<String> variables, List<String> lists) {
		if (leftChild != null) {
			leftChild.getVariableAndListNames(variables, lists);
		}
		if (rightChild != null) {
			rightChild.getVariableAndListNames(variables, lists);
		}
		if (type == ElementType.USER_VARIABLE && !variables.contains(value)) {
			variables.add(value);
		}
		if (type == ElementType.USER_LIST && !lists.contains(value)) {
			lists.add(value);
		}
	}

	public boolean containsSpriteInCollision(String name) {
		boolean contained = false;
		if (leftChild != null) {
			contained |= leftChild.containsSpriteInCollision(name);
		}
		if (rightChild != null) {
			contained |= rightChild.containsSpriteInCollision(name);
		}
		if (type == ElementType.COLLISION_FORMULA && value.equals(name)) {
			contained = true;
		}
		return contained;
	}

	public void updateCollisionFormula(String oldName, String newName) {

		if (leftChild != null) {
			leftChild.updateCollisionFormula(oldName, newName);
		}
		if (rightChild != null) {
			rightChild.updateCollisionFormula(oldName, newName);
		}
		if (type == ElementType.COLLISION_FORMULA && value.equals(oldName)) {
			value = newName;
		}
	}

	public void updateCollisionFormulaToVersion() {
		if (leftChild != null) {
			leftChild.updateCollisionFormulaToVersion();
		}
		if (rightChild != null) {
			rightChild.updateCollisionFormulaToVersion();
		}
		if (type == ElementType.COLLISION_FORMULA) {
			String secondSpriteName = CollisionDetection.getSecondSpriteNameFromCollisionFormulaString(value);
			if (secondSpriteName != null) {
				value = secondSpriteName;
			}
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
			case COLLISION_FORMULA:
				try {
					returnValue = interpretCollision(sprite, value);
				} catch (Exception exception) {
					returnValue = 0d;
				}
		}
		return normalizeDegeneratedDoubleValues(returnValue);
	}

	private Object interpretCollision(Sprite firstSprite, String formula) {

		String secondSpriteName = formula;
		Sprite secondSprite;
		try {
			secondSprite = ProjectManager.getInstance().getCurrentlyPlayingScene().getSprite(secondSpriteName);
		} catch (Resources.NotFoundException exception) {
			return 0d;
		}
		Look firstLook = firstSprite.look;
		Look secondLook;
		if (secondSprite instanceof GroupSprite) {
			List<Sprite> groupSprites = GroupSprite.getSpritesFromGroupWithGroupName(secondSpriteName);
			for (Sprite sprite : groupSprites) {
				secondLook = sprite.look;
				if (CollisionDetection.checkCollisionBetweenLooks(firstLook, secondLook) == 1d) {
					return 1d;
				}
			}
			return 0d;
		}

		List<Sprite> spriteAndClones = new ArrayList<>();
		spriteAndClones.add(secondSprite);
		if (StageActivity.stageListener != null) {
			spriteAndClones.addAll(StageActivity.stageListener.getAllClonesOfSprite(secondSprite));
		}

		for (Sprite sprite : spriteAndClones) {
			secondLook = sprite.look;
			if (firstLook.equals(secondLook)) {
				continue;
			}

			if (CollisionDetection.checkCollisionBetweenLooks(firstLook, secondLook) == 1d) {
				return 1d;
			}
		}

		return 0d;
	}

	private Object interpretUserList(Sprite sprite) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		UserList userList = UserDataWrapper.getUserList(value, sprite, currentProject);
		if (userList == null) {
			return FALSE;
		}

		List<Object> userListValues = userList.getValue();

		if (userListValues.size() == 0) {
			return "";
		} else if (userListValues.size() == 1) {
			return userListValues.get(0);
		} else {
			return interpretMultipleItemsUserList(userListValues);
		}
	}

	private static Object interpretMultipleItemsUserList(List<Object> userListValues) {
		List<String> userListStringValues = new ArrayList<>();

		for (Object listValue : userListValues) {
			if (listValue instanceof Double) {
				Double doubleValueOfListItem = (Double) listValue;
				userListStringValues.add(trimTrailingCharacters(String.valueOf(doubleValueOfListItem.intValue())));
			} else if (listValue instanceof String) {
				String stringValueOfListItem = (String) listValue;
				userListStringValues.add(trimTrailingCharacters(stringValueOfListItem));
			}
		}

		StringBuilder stringBuilder = new StringBuilder(userListStringValues.size());
		String separator = listConsistsOfSingleCharacters(userListStringValues) ? "" : " ";
		for (String userListStringValue : userListStringValues) {
			stringBuilder.append(trimTrailingCharacters(userListStringValue));
			stringBuilder.append(separator);
		}

		return stringBuilder.toString().trim();
	}

	private static Boolean listConsistsOfSingleCharacters(List<String> userListStringValues) {
		for (String userListStringValue : userListStringValues) {
			if (userListStringValue.length() > 1) {
				return false;
			}
		}
		return true;
	}

	private Object interpretUserVariable(Sprite sprite) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		UserVariable userVariable = UserDataWrapper.getUserVariable(value, sprite, currentProject);
		if (userVariable == null) {
			return FALSE;
		}
		return userVariable.getValue();
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
		Object firstArgument = interpretChild(leftChild, sprite);
		Object secondArgument = interpretChild(rightChild, sprite);

		switch (function) {
			case LETTER:
				return interpretFunctionLetter(firstArgument, secondArgument);
			case LENGTH:
				return interpretFunctionLength(firstArgument, sprite);
			case JOIN:
				return interpretFunctionJoin(sprite);
			case REGEX:
				return interpretFunctionRegex(sprite);
			case LIST_ITEM:
				return interpretFunctionListItem(firstArgument, sprite);
			case CONTAINS:
				return interpretFunctionContains(secondArgument, sprite);
			case NUMBER_OF_ITEMS:
				return interpretFunctionNumberOfItems(firstArgument, sprite);
			default:
				Double firstArgumentDouble = convertArgumentToDouble(firstArgument);
				Double secondArgumentDouble = convertArgumentToDouble(secondArgument);
				return interpretFormulaFunction(function, firstArgumentDouble, secondArgumentDouble);
		}
	}

	@Nullable
	private Object interpretChild(FormulaElement child, Sprite sprite) {
		if (child != null) {
			return child.interpretRecursive(sprite);
		} else {
			return null;
		}
	}

	private Object interpretFormulaFunction(Functions function, Double firstArgumentDouble, Double secondArgumentDouble) {
		FormulaFunction formulaFunction = formulaFunctions.get(function);
		if (formulaFunction == null) {
			return 0d;
		} else {
			return formulaFunction.execute(firstArgumentDouble, secondArgumentDouble);
		}
	}

	private Object interpretFunctionNumberOfItems(Object left, Sprite sprite) {
		if (leftChild.type == ElementType.USER_LIST) {
			return (double) handleNumberOfItemsOfUserListParameter(sprite);
		}
		return interpretFunctionLength(left, sprite);
	}

	private Object interpretFunctionContains(Object right, Sprite sprite) {
		if (leftChild.getElementType() == ElementType.USER_LIST) {
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			UserList userList = UserDataWrapper.getUserList(leftChild.value, sprite, currentProject);

			if (userList == null) {
				return 0d;
			}

			for (Object userListElement : userList.getValue()) {
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
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			userList = UserDataWrapper.getUserList(rightChild.value, sprite, currentProject);
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
				//This is expected
			}
		} else if (left != null) {
			index = ((Double) left).intValue();
		} else {
			return "";
		}

		index--;

		if (index < 0) {
			return "";
		} else if (index >= userList.getValue().size()) {
			return "";
		}

		return userList.getValue().get(index);
	}

	private Object interpretFunctionJoin(Sprite sprite) {
		return trimTrailingCharacters(interpretInterpretFunctionStringParameter(leftChild, sprite))
				+ trimTrailingCharacters(interpretInterpretFunctionStringParameter(rightChild, sprite));
	}

	private Object interpretFunctionRegex(Sprite sprite) {
		try {
			Pattern pattern = Pattern.compile(
					trimTrailingCharacters(interpretInterpretFunctionStringParameter(leftChild, sprite)),
					Pattern.DOTALL | Pattern.MULTILINE);

			Matcher matcher = pattern.matcher(
					trimTrailingCharacters(interpretInterpretFunctionStringParameter(rightChild, sprite)));

			if (matcher.find()) {
				if (matcher.groupCount() == 0) {
					return matcher.group(0);
				} else {
					return matcher.group(1);
				}
			} else {
				return "";
			}
		} catch (IllegalArgumentException exception) {
			return exception.getLocalizedMessage();
		}
	}

	private String interpretInterpretFunctionStringParameter(FormulaElement child, Sprite sprite) {
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
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			UserList userList = UserDataWrapper.getUserList(leftChild.value, sprite, currentProject);
			if (userList == null) {
				return 0d;
			}
			if (userList.getValue().size() == 0) {
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

	private Object interpretFunctionLetter(Object left, Object right) {
		int index = 0;
		if (left instanceof String) {
			try {
				Double doubleValueOfLeftChild = Double.valueOf((String) left);
				index = doubleValueOfLeftChild.intValue();
			} catch (NumberFormatException numberFormatexception) {
				//This is expected
			}
		} else if (left != null) {
			index = ((Double) left).intValue();
		} else {
			return "";
		}

		index--;

		if (index < 0) {
			return "";
		} else if (right == null || index >= String.valueOf(right).length()) {
			return "";
		}
		return String.valueOf(String.valueOf(right).charAt(index));
	}

	private double interpretFunctionRand(double from, double to) {
		double low = Math.min(from, to);
		double high = Math.max(from, to);

		if (low == high) {
			return low;
		}

		if (isInteger(low) && isInteger(high)
				&& !isNumberWithDecimalPoint(leftChild) && !isNumberWithDecimalPoint(rightChild)) {
			return Math.floor(Math.random() * ((high + 1) - low)) + low;
		} else {
			return (Math.random() * (high - low)) + low;
		}
	}

	private static boolean isNumberWithDecimalPoint(FormulaElement element) {
		return element.type == ElementType.NUMBER && element.value.contains(".");
	}

	private Object interpretOperator(Operators operator, Sprite sprite) {

		if (leftChild != null) { // binary operator
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
		} else { //unary operators
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
		LookData lookData = sprite.look.getLookData();
		List<LookData> lookDataList = sprite.getLookList();
		if (lookData == null && lookDataList.size() > 0) {
			lookData = lookDataList.get(0);
		}
		switch (sensor) {
			case OBJECT_BRIGHTNESS:
				returnValue = (double) sprite.look.getBrightnessInUserInterfaceDimensionUnit();
				break;
			case OBJECT_COLOR:
				returnValue = (double) sprite.look.getColorInUserInterfaceDimensionUnit();
				break;
			case OBJECT_TRANSPARENCY:
				returnValue = (double) sprite.look.getTransparencyInUserInterfaceDimensionUnit();
				break;
			case OBJECT_LAYER:
				if (sprite.look.getZIndex() < 0) {
					returnValue = (double) ProjectManager.getInstance().getCurrentlyEditedScene()
							.getSpriteList().indexOf(sprite);
				} else if (sprite.look.getZIndex() == 0) {
					returnValue = 0d;
				} else {
					returnValue = (double) sprite.look.getZIndex() - Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS;
				}
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
			case OBJECT_ANGULAR_VELOCITY:
				returnValue = (double) sprite.look.getAngularVelocityInUserInterfaceDimensionUnit();
				break;
			case OBJECT_X_VELOCITY:
				returnValue = (double) sprite.look.getXVelocityInUserInterfaceDimensionUnit();
				break;
			case OBJECT_Y_VELOCITY:
				returnValue = (double) sprite.look.getYVelocityInUserInterfaceDimensionUnit();
				break;
			case OBJECT_LOOK_NUMBER:
			case OBJECT_BACKGROUND_NUMBER:
				returnValue = 1.0d + ((lookData != null) ? lookDataList.indexOf(lookData) : 0);
				break;
			case OBJECT_LOOK_NAME:
			case OBJECT_BACKGROUND_NAME:
				returnValue = (lookData != null) ? lookData.getName() : "";
				break;
			case OBJECT_DISTANCE_TO:
				returnValue = (double) sprite.look.getDistanceToTouchPositionInUserInterfaceDimensions();
				break;
			case NFC_TAG_MESSAGE:
				returnValue = NfcHandler.getLastNfcTagMessage();
				break;
			case NFC_TAG_ID:
				returnValue = NfcHandler.getLastNfcTagId();
				break;
			case COLLIDES_WITH_EDGE:
				if (StageActivity.stageListener != null) {
					returnValue = StageActivity.stageListener.firstFrameDrawn ? CollisionDetection.collidesWithEdge(sprite
							.look) : 0d;
				} else {
					returnValue = 0d;
				}
				break;
			case COLLIDES_WITH_FINGER:
				returnValue = CollisionDetection.collidesWithFinger(sprite.look);
				break;
		}
		return returnValue;
	}

	private Double interpretOperatorEqual(Object left, Object right) {
		try {
			Double tempLeft = Double.valueOf(String.valueOf(left));
			Double tempRight = Double.valueOf(String.valueOf(right));
			int compareResult = getCompareResult(tempLeft, tempRight);
			if (compareResult == 0) {
				return 1d;
			}
			return 0d;
		} catch (NumberFormatException numberFormatException) {
			int compareResult = String.valueOf(left).compareTo(String.valueOf(right));
			if (compareResult == 0) {
				return 1d;
			}
			return 0d;
		}
	}

	private int getCompareResult(Double left, Double right) {
		int compareResult;
		if (left == 0 || right == 0) {
			compareResult = ((Double) Math.abs(left)).compareTo(Math.abs(right));
		} else {
			compareResult = left.compareTo(right);
		}
		return compareResult;
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

		FormulaElement cloneThis = new FormulaElement(ElementType.OPERATOR, operator, this.getParent());

		cloneThis.leftChild = this;
		cloneThis.rightChild = rightChild;
		cloneThis.leftChild.parent = cloneThis;
		cloneThis.parent.rightChild = cloneThis;
	}

	private boolean isInteger(double value) {
		return !Double.isInfinite(value) && !Double.isNaN(value) && value == Math.rint(value);
	}

	public boolean isLogicalOperator() {
		return (type == ElementType.OPERATOR) && Operators.getOperatorByValue(value).isLogicalOperator;
	}

	public boolean containsElement(ElementType elementType) {
		return (type.equals(elementType)
				|| (leftChild != null && leftChild.containsElement(elementType))
				|| (rightChild != null && rightChild.containsElement(elementType)));
	}

	public boolean isUserVariableWithTypeString(Sprite sprite) {
		if (type == ElementType.USER_VARIABLE) {
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			UserVariable userVariable = UserDataWrapper.getUserVariable(value, sprite, currentProject);
			Object userVariableValue = userVariable.getValue();
			return userVariableValue instanceof String;
		}
		return false;
	}

	private int handleLengthUserVariableParameter(Sprite sprite) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		UserVariable userVariable = UserDataWrapper.getUserVariable(leftChild.value, sprite, currentProject);
		Object userVariableValue = userVariable.getValue();
		if (userVariableValue instanceof String) {
			return String.valueOf(userVariableValue).length();
		} else {
			if (isInteger((Double) userVariableValue)) {
				return Integer.toString(((Double) userVariableValue).intValue()).length();
			} else {
				return Double.toString(((Double) userVariableValue)).length();
			}
		}
	}

	private int handleNumberOfItemsOfUserListParameter(Sprite sprite) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		UserList userList = UserDataWrapper.getUserList(leftChild.value, sprite, currentProject);
		if (userList == null) {
			return 0;
		}

		return userList.getValue().size();
	}

	boolean isNumber() {
		if (type == ElementType.OPERATOR) {
			Operators operator = Operators.getOperatorByValue(value);
			return (operator == Operators.MINUS) && (leftChild == null) && rightChild.isNumber();
		}
		return type == ElementType.NUMBER;
	}

	@Override
	public FormulaElement clone() {
		FormulaElement leftChildClone = leftChild == null ? null : leftChild.clone();
		FormulaElement rightChildClone = rightChild == null ? null : rightChild.clone();
		return new FormulaElement(type, value == null ? "" : value, null, leftChildClone, rightChildClone);
	}

	public void addRequiredResources(final Set<Integer> requiredResourcesSet) {
		if (leftChild != null) {
			leftChild.addRequiredResources(requiredResourcesSet);
		}
		if (rightChild != null) {
			rightChild.addRequiredResources(requiredResourcesSet);
		}
		if (type == ElementType.FUNCTION) {
			Functions functions = Functions.getFunctionByValue(value);
			switch (functions) {
				case ARDUINOANALOG:
				case ARDUINODIGITAL:
					requiredResourcesSet.add(Brick.BLUETOOTH_SENSORS_ARDUINO);
					break;
				case RASPIDIGITAL:
					requiredResourcesSet.add(Brick.SOCKET_RASPI);
					break;
			}
		}
		if (type == ElementType.SENSOR) {
			Sensors sensor = Sensors.getSensorByValue(value);
			switch (sensor) {
				case X_ACCELERATION:
				case Y_ACCELERATION:
				case Z_ACCELERATION:
					requiredResourcesSet.add(Brick.SENSOR_ACCELERATION);
					break;

				case X_INCLINATION:
				case Y_INCLINATION:
					requiredResourcesSet.add(Brick.SENSOR_INCLINATION);
					break;

				case COMPASS_DIRECTION:
					requiredResourcesSet.add(Brick.SENSOR_COMPASS);
					break;

				case LATITUDE:
				case LONGITUDE:
				case LOCATION_ACCURACY:
				case ALTITUDE:
					requiredResourcesSet.add(Brick.SENSOR_GPS);
					break;

				case FACE_DETECTED:
				case FACE_SIZE:
				case FACE_X_POSITION:
				case FACE_Y_POSITION:
					requiredResourcesSet.add(Brick.FACE_DETECTION);
					break;

				case NXT_SENSOR_1:
				case NXT_SENSOR_2:
				case NXT_SENSOR_3:
				case NXT_SENSOR_4:
					requiredResourcesSet.add(Brick.BLUETOOTH_LEGO_NXT);
					break;

				case EV3_SENSOR_1:
				case EV3_SENSOR_2:
				case EV3_SENSOR_3:
				case EV3_SENSOR_4:
					requiredResourcesSet.add(Brick.BLUETOOTH_LEGO_EV3);
					break;

				case PHIRO_FRONT_LEFT:
				case PHIRO_FRONT_RIGHT:
				case PHIRO_SIDE_LEFT:
				case PHIRO_SIDE_RIGHT:
				case PHIRO_BOTTOM_LEFT:
				case PHIRO_BOTTOM_RIGHT:
					requiredResourcesSet.add(Brick.BLUETOOTH_PHIRO);
					break;

				case DRONE_BATTERY_STATUS:
				case DRONE_CAMERA_READY:
				case DRONE_EMERGENCY_STATE:
				case DRONE_FLYING:
				case DRONE_INITIALIZED:
				case DRONE_NUM_FRAMES:
				case DRONE_RECORD_READY:
				case DRONE_RECORDING:
				case DRONE_USB_ACTIVE:
				case DRONE_USB_REMAINING_TIME:
					requiredResourcesSet.add(Brick.ARDRONE_SUPPORT);
					break;

				case NFC_TAG_MESSAGE:
				case NFC_TAG_ID:
					requiredResourcesSet.add(Brick.NFC_ADAPTER);
					break;

				case COLLIDES_WITH_EDGE:
					requiredResourcesSet.add(Brick.COLLISION);
					break;
				case COLLIDES_WITH_FINGER:
					requiredResourcesSet.add(Brick.COLLISION);
					break;

				case GAMEPAD_A_PRESSED:
				case GAMEPAD_B_PRESSED:
				case GAMEPAD_DOWN_PRESSED:
				case GAMEPAD_UP_PRESSED:
				case GAMEPAD_LEFT_PRESSED:
				case GAMEPAD_RIGHT_PRESSED:
					requiredResourcesSet.add(Brick.CAST_REQUIRED);
					break;

				case LOUDNESS:
					requiredResourcesSet.add(Brick.MICROPHONE);
					break;
				default:
			}
		}
		if (type == ElementType.COLLISION_FORMULA) {
			requiredResourcesSet.add(Brick.COLLISION);
		}
	}
}
