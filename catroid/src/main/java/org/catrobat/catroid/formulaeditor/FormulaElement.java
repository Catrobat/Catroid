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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
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
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.TouchUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.catrobat.catroid.formulaeditor.InternTokenType.BRACKET_CLOSE;
import static org.catrobat.catroid.formulaeditor.InternTokenType.BRACKET_OPEN;
import static org.catrobat.catroid.formulaeditor.InternTokenType.COLLISION_FORMULA;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_NAME;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN;
import static org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETER_DELIMITER;
import static org.catrobat.catroid.formulaeditor.InternTokenType.NUMBER;
import static org.catrobat.catroid.formulaeditor.InternTokenType.OPERATOR;
import static org.catrobat.catroid.formulaeditor.InternTokenType.SENSOR;
import static org.catrobat.catroid.formulaeditor.InternTokenType.STRING;
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_LIST;
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_VARIABLE;
import static org.catrobat.catroid.formulaeditor.common.Conversion.FALSE;
import static org.catrobat.catroid.formulaeditor.common.Conversion.TRUE;
import static org.catrobat.catroid.formulaeditor.common.Conversion.booleanToDouble;
import static org.catrobat.catroid.formulaeditor.common.Conversion.convertArgumentToDouble;
import static org.catrobat.catroid.sensing.ColorCollisionDetection.interpretFunctionTouchesColor;
import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ElementType {
		OPERATOR, FUNCTION, NUMBER, SENSOR, USER_VARIABLE, USER_LIST, BRACKET, STRING, COLLISION_FORMULA
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

		formulaFunctions = new EnumMap<>(Functions.class);
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
				addBracketTokens(internTokenList, rightChild);
				break;
			case OPERATOR:
				addOperatorTokens(internTokenList, value);
				break;
			case FUNCTION:
				addFunctionTokens(internTokenList, value, leftChild, rightChild);
				break;
			case USER_VARIABLE:
				addToken(internTokenList, USER_VARIABLE, value);
				break;
			case USER_LIST:
				addToken(internTokenList, USER_LIST, value);
				break;
			case NUMBER:
				addToken(internTokenList, NUMBER, trimTrailingCharacters(value));
				break;
			case SENSOR:
				addToken(internTokenList, SENSOR, value);
				break;
			case STRING:
				addToken(internTokenList, STRING, value);
				break;
			case COLLISION_FORMULA:
				addToken(internTokenList, COLLISION_FORMULA, value);
				break;
		}
		return internTokenList;
	}

	private void addToken(List<InternToken> internTokenList, InternTokenType tokenType) {
		internTokenList.add(new InternToken(tokenType));
	}

	private void addToken(List<InternToken> internTokenList, InternTokenType tokenType, String value) {
		internTokenList.add(new InternToken(tokenType, value));
	}

	private void addBracketTokens(List<InternToken> internTokenList, FormulaElement element) {
		addToken(internTokenList, BRACKET_OPEN);
		tryAddInternTokenList(internTokenList, element);
		addToken(internTokenList, BRACKET_CLOSE);
	}

	private void addOperatorTokens(List<InternToken> internTokenList, String value) {
		tryAddInternTokenList(internTokenList, leftChild);
		addToken(internTokenList, OPERATOR, value);
		tryAddInternTokenList(internTokenList, rightChild);
	}

	private void addFunctionTokens(List<InternToken> internTokenList, String value, FormulaElement leftChild, FormulaElement rightChild) {
		addToken(internTokenList, FUNCTION_NAME, value);
		boolean functionHasParameters = false;
		if (leftChild != null) {
			addToken(internTokenList, FUNCTION_PARAMETERS_BRACKET_OPEN);
			functionHasParameters = true;
			internTokenList.addAll(leftChild.getInternTokenList());
		}
		if (rightChild != null) {
			addToken(internTokenList, FUNCTION_PARAMETER_DELIMITER);
			internTokenList.addAll(rightChild.getInternTokenList());
		}
		if (functionHasParameters) {
			addToken(internTokenList, FUNCTION_PARAMETERS_BRACKET_CLOSE);
		}
	}

	private void tryAddInternTokenList(List<InternToken> internTokenList, FormulaElement child) {
		if (child != null) {
			internTokenList.addAll(child.getInternTokenList());
		}
	}

	public FormulaElement getRoot() {
		FormulaElement root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	public void updateVariableReferences(String oldName, String newName) {
		tryUpdateVariableReference(leftChild, oldName, newName);
		tryUpdateVariableReference(rightChild, oldName, newName);
		if (matchesTypeAndName(ElementType.USER_VARIABLE, oldName)) {
			value = newName;
		}
	}

	public void updateListName(String oldName, String newName) {
		tryUpdateVariableReference(leftChild, oldName, newName);
		tryUpdateVariableReference(rightChild, oldName, newName);
		if (matchesTypeAndName(ElementType.USER_LIST, oldName)) {
			value = newName;
		}
	}

	private void tryUpdateVariableReference(FormulaElement element, String oldName, String newName) {
		if (element != null) {
			element.updateVariableReferences(oldName, newName);
		}
	}

	public final boolean containsSpriteInCollision(String name) {
		return containsSpriteInCollision(leftChild, name)
		|| containsSpriteInCollision(rightChild, name)
		|| matchesTypeAndName(ElementType.COLLISION_FORMULA, name);
	}

	private boolean containsSpriteInCollision(FormulaElement element, String name) {
		return element != null && element.containsSpriteInCollision(name);
	}

	public final void updateCollisionFormula(String oldName, String newName) {
		tryUpdateCollisionFormula(leftChild, oldName, newName);
		tryUpdateCollisionFormula(rightChild, oldName, newName);
		if (matchesTypeAndName(ElementType.COLLISION_FORMULA, oldName)) {
			value = newName;
		}
	}

	private boolean matchesTypeAndName(ElementType collisionFormula, String name) {
		return type == collisionFormula && value.equals(name);
	}

	private void tryUpdateCollisionFormula(FormulaElement element, String oldName, String newName) {
		if (element != null) {
			element.updateCollisionFormula(oldName, newName);
		}
	}

	public void updateCollisionFormulaToVersion(Project currentProject) {
		tryUpdateCollisionFormulaToVersion(leftChild, currentProject);
		tryUpdateCollisionFormulaToVersion(rightChild, currentProject);
		if (type == ElementType.COLLISION_FORMULA) {
			String secondSpriteName = CollisionDetection.getSecondSpriteNameFromCollisionFormulaString(value, currentProject);
			if (secondSpriteName != null) {
				value = secondSpriteName;
			}
		}
	}

	private void tryUpdateCollisionFormulaToVersion(FormulaElement element, Project currentProject) {
		if (element != null) {
			element.updateCollisionFormulaToVersion(currentProject);
		}
	}

	public Object interpretRecursive(Sprite sprite) {
		Object rawReturnValue = rawInterpretRecursive(sprite);
		return normalizeDegeneratedDoubleValues(rawReturnValue);
	}

	private Object rawInterpretRecursive(Sprite sprite) {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project currentProject = projectManager.getCurrentProject();
		Scene currentlyPlayingScene = projectManager.getCurrentlyPlayingScene();
		Scene currentlyEditedScene = projectManager.getCurrentlyEditedScene();
		StageListener stageListener = StageActivity.stageListener;

		switch (type) {
			case BRACKET:
				return rightChild.interpretRecursive(sprite);
			case NUMBER:
			case STRING:
				return value;
			case OPERATOR:
				return tryInterpretOperator(sprite, value);
			case FUNCTION:
				return tryInterpretFunction(sprite, currentProject, currentlyPlayingScene);
			case SENSOR:
				return interpretSensor(sprite, currentlyEditedScene, currentProject);
			case USER_VARIABLE:
				UserVariable userVariable = UserDataWrapper.getUserVariable(value, sprite, currentProject);
				return interpretUserVariable(userVariable);
			case USER_LIST:
				UserList userList = UserDataWrapper.getUserList(value, sprite, currentProject);
				return interpretUserList(userList);
			case COLLISION_FORMULA:
				return tryInterpretCollision(sprite.look, value, currentlyPlayingScene, stageListener);
		}
		return FALSE;
	}

	private Object tryInterpretFunction(Sprite sprite, Project currentProject, Scene currentlyPlayingScene) {
		Functions function = Functions.getFunctionByValue(value);
		return interpretFunction(function, sprite, currentProject, currentlyPlayingScene);
	}

	@NotNull
	private Object tryInterpretOperator(Sprite sprite, String value) {
		Operators operator = Operators.getOperatorByValue(value);
		if (operator == null) {
			return false;
		}
		return interpretOperator(operator, sprite);
	}

	private double tryInterpretCollision(Look firstLook, String secondSpriteName, Scene currentlyPlayingScene, StageListener stageListener) {
		try {
			return interpretCollision(firstLook, secondSpriteName, currentlyPlayingScene, stageListener);
		} catch (Exception e) {
			return FALSE;
		}
	}

	private double interpretCollision(Look firstLook, String secondSpriteName, Scene currentlyPlayingScene, StageListener stageListener) {
		Sprite secondSprite = tryFindSprite(currentlyPlayingScene, secondSpriteName);
		if (secondSprite == null) {
			return FALSE;
		} else if (secondSprite instanceof GroupSprite) {
			List<Sprite> spritesFromGroupWithGroupName = GroupSprite.getSpritesFromGroupWithGroupName(secondSpriteName, currentlyPlayingScene.getSpriteList());
			return interpretLookCollision(firstLook, toLooks(spritesFromGroupWithGroupName));
		} else {
			return interpretLookCollision(firstLook, toLooks(getAllClones(secondSprite, stageListener)));
		}
	}

	@Nullable
	private Sprite tryFindSprite(Scene scene, String spriteName) {
		try {
			return scene.getSprite(spriteName);
		} catch (Resources.NotFoundException exception) {
			return null;
		}
	}

	private List<Look> toLooks(List<Sprite> sprites) {
		List<Look> looks = new ArrayList<>(sprites.size());
		for (Sprite sprite : sprites) {
			looks.add(sprite.look);
		}
		return looks;
	}

	private double interpretLookCollision(Look look, List<Look> looks) {
		for (Look secondLook : looks) {
			if (look.equals(secondLook)) {
				continue;
			}

			if (CollisionDetection.checkCollisionBetweenLooks(look, secondLook) == TRUE) {
				return TRUE;
			}
		}

		return FALSE;
	}

	@NotNull
	private List<Sprite> getAllClones(Sprite sprite, StageListener stageListener) {
		List<Sprite> spriteAndClones = new ArrayList<>();
		spriteAndClones.add(sprite);
		if (stageListener != null) {
			spriteAndClones.addAll(stageListener.getAllClonesOfSprite(sprite));
		}
		return spriteAndClones;
	}

	private Object interpretUserList(UserList userList) {
		if (userList == null) {
			return FALSE;
		}

		return interpretUserListValues(userList.getValue());
	}

	private Object interpretUserListValues(List<Object> userListValues) {
		if (userListValues.isEmpty()) {
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

	private static boolean listConsistsOfSingleCharacters(List<String> userListStringValues) {
		for (String userListStringValue : userListStringValues) {
			if (userListStringValue.length() > 1) {
				return false;
			}
		}
		return true;
	}

	private Object interpretUserVariable(UserVariable userVariable) {
		if (userVariable == null) {
			return FALSE;
		}
		return userVariable.getValue();
	}

	private Object interpretSensor(Sprite sprite, Scene currentlyEditedScene, Project currentProject) {
		Sensors sensor = Sensors.getSensorByValue(value);
		if (sensor.isObjectSensor) {
			return interpretObjectSensor(sensor, sprite, currentlyEditedScene, currentProject);
		} else {
			return SensorHandler.getSensorValue(sensor);
		}
	}

	private Object interpretFunction(Functions function, Sprite sprite, Project currentProject, Scene currentlyPlayingScene) {
		Object firstArgument = tryInterpretRecursive(leftChild, sprite);
		Object secondArgument = tryInterpretRecursive(rightChild, sprite);

		switch (function) {
			case LETTER:
				return interpretFunctionLetter(firstArgument, secondArgument);
			case LENGTH:
				return interpretFunctionLength(firstArgument, sprite, currentProject);
			case JOIN:
				return interpretFunctionJoin(sprite);
			case REGEX:
				return interpretFunctionRegex(sprite);
			case LIST_ITEM:
				return interpretFunctionListItem(firstArgument, sprite, currentProject);
			case CONTAINS:
				return interpretFunctionContains(secondArgument, sprite, currentProject);
			case NUMBER_OF_ITEMS:
				return interpretFunctionNumberOfItems(firstArgument, sprite, currentProject);
			case COLLIDES_WITH_COLOR:
				return booleanToDouble(interpretFunctionTouchesColor(firstArgument, sprite, currentProject, currentlyPlayingScene));
			default:
				Double firstArgumentDouble = convertArgumentToDouble(firstArgument);
				Double secondArgumentDouble = convertArgumentToDouble(secondArgument);
				return interpretFormulaFunction(function, firstArgumentDouble, secondArgumentDouble);
		}
	}

	@Nullable
	private Object tryInterpretRecursive(FormulaElement element, Sprite sprite) {
		if (element == null) {
			return null;
		}
		return element.interpretRecursive(sprite);
	}

	private Object interpretFormulaFunction(Functions function, Double firstArgumentDouble, Double secondArgumentDouble) {
		FormulaFunction formulaFunction = formulaFunctions.get(function);
		if (formulaFunction == null) {
			return FALSE;
		}
		return formulaFunction.execute(firstArgumentDouble, secondArgumentDouble);
	}

	private Object interpretFunctionNumberOfItems(Object left, Sprite sprite, Project currentProject) {
		if (leftChild.type == ElementType.USER_LIST) {
			UserList userList = UserDataWrapper.getUserList(leftChild.value, sprite, currentProject);
			return (double) handleNumberOfItemsOfUserListParameter(userList);
		}
		return interpretFunctionLength(left, sprite, currentProject);
	}

	private Object interpretFunctionContains(Object right, Sprite sprite, Project currentProject) {
		UserList userList = getUserListOfChild(leftChild, sprite, currentProject);
		if (userList == null) {
			return FALSE;
		}

		for (Object userListElement : userList.getValue()) {
			if (interpretOperatorEqual(userListElement, right)) {
				return TRUE;
			}
		}

		return FALSE;
	}

	private Object interpretFunctionListItem(Object left, Sprite sprite, Project currentProject) {
		if (left == null) {
			return "";
		}

		UserList userList = getUserListOfChild(rightChild, sprite, currentProject);
		if (userList == null) {
			return "";
		}

		int index = tryParseInt(left) - 1;

		if (index < 0 || index >= userList.getValue().size()) {
			return "";
		}
		return userList.getValue().get(index);
	}

	@Nullable
	private UserList getUserListOfChild(FormulaElement child, Sprite sprite, Project currentProject) {
		if (child.getElementType() != ElementType.USER_LIST) {
			return null;
		}
		return UserDataWrapper.getUserList(child.value, sprite, currentProject);
	}

	private int tryParseInt(String left) {
		try {
			return Double.valueOf(left).intValue();
		} catch (NumberFormatException numberFormatexception) {
			return 0;
		}
	}

	private int tryParseInt(Object left) {
		if (left instanceof String) {
			return tryParseInt((String) left);
		} else {
			return ((Double) left).intValue();
		}
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

	private Object interpretFunctionLength(Object left, Sprite sprite, Project currentProject) {
		if (leftChild == null) {
			return FALSE;
		}
		if (leftChild.type == ElementType.NUMBER || leftChild.type == ElementType.STRING) {
			return (double) leftChild.value.length();
		}
		if (leftChild.type == ElementType.USER_VARIABLE) {
			UserVariable userVariable = UserDataWrapper.getUserVariable(leftChild.value, sprite, currentProject);
			return (double) handleLengthUserVariableParameter(userVariable);
		}
		if (leftChild.type == ElementType.USER_LIST) {
			UserList userList = UserDataWrapper.getUserList(leftChild.value, sprite, currentProject);
			if (userList == null || userList.getValue().isEmpty()) {
				return FALSE;
			}

			Object interpretedList = leftChild.interpretRecursive(sprite);
			if (interpretedList instanceof Double) {
				Double interpretedListDoubleValue = (Double) interpretedList;
				if (interpretedListDoubleValue.isNaN() || interpretedListDoubleValue.isInfinite()) {
					return FALSE;
				}
				return (double) (String.valueOf(interpretedListDoubleValue.intValue())).length();
			}
			if (interpretedList instanceof String) {
				String interpretedListStringValue = (String) interpretedList;
				return (double) interpretedListStringValue.length();
			}
		}
		if (left instanceof Double && ((Double) left).isNaN()) {
			return FALSE;
		}
		return (double) (String.valueOf(left)).length();
	}

	private Object interpretFunctionLetter(Object left, Object right) {
		if (left == null || right == null) {
			return "";
		}

		int index = tryParseInt(left) - 1;
		String stringValueOfRight = String.valueOf(right);

		if (index < 0 || index >= stringValueOfRight.length()) {
			return "";
		}
		return String.valueOf(stringValueOfRight.charAt(index));
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

	private double interpretOperator(@NotNull Operators operator, Sprite sprite) {
		Object rightObject = tryInterpretChildRecursive(sprite, rightChild);
		Double right = tryInterpretDoubleValue(rightObject);

		if (leftChild != null) {
			return interpretBinaryOperator(operator, sprite, rightObject, right);
		} else {
			return interpretUnaryOperator(operator, right);
		}
	}

	private double interpretUnaryOperator(@NotNull Operators operator, Double right) {
		switch (operator) {
			case MINUS:
				return -right;
			case LOGICAL_NOT:
				return booleanToDouble(right == FALSE);
			default:
				return FALSE;
		}
	}

	private double interpretBinaryOperator(@NotNull Operators operator, Sprite sprite, Object rightObject, Double right) {
		Object leftObject = tryInterpretChildRecursive(sprite, leftChild);
		Double left = tryInterpretDoubleValue(leftObject);

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
				return Math.pow(left, right);
			case EQUAL:
				return booleanToDouble(interpretOperatorEqual(leftObject, rightObject));
			case NOT_EQUAL:
				return booleanToDouble(!(interpretOperatorEqual(leftObject, rightObject)));
			case GREATER_THAN:
				return booleanToDouble(left.compareTo(right) > 0);
			case GREATER_OR_EQUAL:
				return booleanToDouble(left.compareTo(right) >= 0);
			case SMALLER_THAN:
				return booleanToDouble(left.compareTo(right) < 0);
			case SMALLER_OR_EQUAL:
				return booleanToDouble(left.compareTo(right) <= 0);
			case LOGICAL_AND:
				return booleanToDouble(left != FALSE && right != FALSE);
			case LOGICAL_OR:
				return booleanToDouble(left != FALSE || right != FALSE);
			default:
				return FALSE;
		}
	}

	private Object tryInterpretChildRecursive(Sprite sprite, FormulaElement child) {
		try {
			return child.interpretRecursive(sprite);
		} catch (NumberFormatException numberFormatException) {
			return Double.NaN;
		}
	}

	private Object interpretObjectSensor(Sensors sensor, Sprite sprite, Scene currentlyEditedScene, Project currentProject) {
		Look look = sprite.look;
		LookData lookData = look.getLookData();
		List<LookData> lookDataList = sprite.getLookList();
		if (lookData == null && !lookDataList.isEmpty()) {
			lookData = lookDataList.get(0);
		}
		switch (sensor) {
			case OBJECT_BRIGHTNESS:
				return (double) look.getBrightnessInUserInterfaceDimensionUnit();
			case OBJECT_COLOR:
				return (double) look.getColorInUserInterfaceDimensionUnit();
			case OBJECT_TRANSPARENCY:
				return (double) look.getTransparencyInUserInterfaceDimensionUnit();
			case OBJECT_LAYER:
				return getLookLayerIndex(sprite, look, currentlyEditedScene.getSpriteList());
			case OBJECT_ROTATION:
				return (double) look.getDirectionInUserInterfaceDimensionUnit();
			case OBJECT_SIZE:
				return (double) look.getSizeInUserInterfaceDimensionUnit();
			case OBJECT_X:
				return (double) look.getXInUserInterfaceDimensionUnit();
			case OBJECT_Y:
				return (double) look.getYInUserInterfaceDimensionUnit();
			case OBJECT_ANGULAR_VELOCITY:
				return (double) look.getAngularVelocityInUserInterfaceDimensionUnit();
			case OBJECT_X_VELOCITY:
				return (double) look.getXVelocityInUserInterfaceDimensionUnit();
			case OBJECT_Y_VELOCITY:
				return (double) look.getYVelocityInUserInterfaceDimensionUnit();
			case OBJECT_DISTANCE_TO:
				return (double) look.getDistanceToTouchPositionInUserInterfaceDimensions();
			case OBJECT_LOOK_NUMBER:
			case OBJECT_BACKGROUND_NUMBER:
				return tryGetLookBackgroundNumber(lookData, lookDataList);
			case OBJECT_LOOK_NAME:
			case OBJECT_BACKGROUND_NAME:
				return getLookBackgroundName(lookData);
			case NFC_TAG_MESSAGE:
				return NfcHandler.getLastNfcTagMessage();
			case NFC_TAG_ID:
				return NfcHandler.getLastNfcTagId();
			case COLLIDES_WITH_EDGE:
				return tryCalculateCollidesWithEdge(look, StageActivity.stageListener, currentProject.getXmlHeader());
			case COLLIDES_WITH_FINGER:
				return calculateCollidesWithFinger(look);
			default:
				return FALSE;
		}
	}

	private double calculateCollidesWithFinger(Look look) {
		return CollisionDetection.collidesWithFinger(look.getCurrentCollisionPolygon(),
				TouchUtil.getCurrentTouchingPoints());
	}

	private double tryCalculateCollidesWithEdge(Look look, StageListener stageListener, XmlHeader xmlHeader) {
		if (stageListener == null || !stageListener.firstFrameDrawn) {
			return FALSE;
		}
		int virtualScreenWidth = xmlHeader.virtualScreenWidth;
		int virtualScreenHeight = xmlHeader.virtualScreenHeight;
		return CollisionDetection.collidesWithEdge(look, virtualScreenWidth, virtualScreenHeight);
	}

	private String getLookBackgroundName(LookData lookData) {
		if (lookData == null) {
			return "";
		}
		return lookData.getName();
	}

	private double tryGetLookBackgroundNumber(LookData lookData, List<LookData> lookDataList) {
		if (lookData == null) {
			return 1;
		}
		return lookDataList.indexOf(lookData) + 1d;
	}

	private double getLookLayerIndex(Sprite sprite, Look look, List<Sprite> spriteList) {
		int lookZIndex = look.getZIndex();
		if (lookZIndex == 0) {
			return 0;
		} else if (lookZIndex < 0) {
			return spriteList.indexOf(sprite);
		} else {
			return (double) lookZIndex - Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS;
		}
	}

	private boolean interpretOperatorEqual(Object left, Object right) {
		String leftString = String.valueOf(left);
		String rightString = String.valueOf(right);
		try {
			double tempLeft = Double.parseDouble(leftString);
			double tempRight = Double.parseDouble(rightString);
			return equalsDoubleIEEE754(tempLeft, tempRight);
		} catch (NumberFormatException numberFormatException) {
			return leftString.equals(rightString);
		}
	}

	private boolean equalsDoubleIEEE754(double left, double right) {
		return Double.isNaN(left) && Double.isNaN(right)
				|| !Double.isNaN(left) && !Double.isNaN(right) && left >= right && left <= right;
	}

	private double tryInterpretDoubleValue(Object object) {
		if (object instanceof String) {
			try {
				return Double.valueOf((String) object);
			} catch (NumberFormatException numberFormatException) {
				return Double.NaN;
			}
		} else {
			return (double) object;
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

	public boolean isUserVariableWithTypeString(UserVariable userVariable) {
		if (type == ElementType.USER_VARIABLE) {
			Object userVariableValue = userVariable.getValue();
			return userVariableValue instanceof String;
		}
		return false;
	}

	private int handleLengthUserVariableParameter(UserVariable userVariable) {
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

	private int handleNumberOfItemsOfUserListParameter(UserList userList) {
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
			addFunctionResources(requiredResourcesSet);
		}
		if (type == ElementType.SENSOR) {
			addSensorsResources(requiredResourcesSet);
		}
		if (type == ElementType.COLLISION_FORMULA) {
			requiredResourcesSet.add(Brick.COLLISION);
		}
	}

	private void addSensorsResources(Set<Integer> requiredResourcesSet) {
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

	private void addFunctionResources(Set<Integer> requiredResourcesSet) {
		Functions functions = Functions.getFunctionByValue(value);
		switch (functions) {
			case ARDUINOANALOG:
			case ARDUINODIGITAL:
				requiredResourcesSet.add(Brick.BLUETOOTH_SENSORS_ARDUINO);
				break;
			case RASPIDIGITAL:
				requiredResourcesSet.add(Brick.SOCKET_RASPI);
				break;
			default:
		}
	}
}
