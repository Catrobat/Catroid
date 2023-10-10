/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.function.ArduinoFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.BinaryFunction;
import org.catrobat.catroid.formulaeditor.function.FormulaFunction;
import org.catrobat.catroid.formulaeditor.function.FunctionProvider;
import org.catrobat.catroid.formulaeditor.function.MathFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.ObjectDetectorFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.RaspiFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.TernaryFunction;
import org.catrobat.catroid.formulaeditor.function.TextBlockFunctionProvider;
import org.catrobat.catroid.formulaeditor.function.TouchFunctionProvider;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.sensing.ColorAtXYDetection;
import org.catrobat.catroid.sensing.ColorCollisionDetection;
import org.catrobat.catroid.sensing.ColorEqualsColor;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;

import static org.catrobat.catroid.formulaeditor.Functions.IF_THEN_ELSE;
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
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_DEFINED_BRICK_INPUT;
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_LIST;
import static org.catrobat.catroid.formulaeditor.InternTokenType.USER_VARIABLE;
import static org.catrobat.catroid.formulaeditor.common.Conversions.FALSE;
import static org.catrobat.catroid.formulaeditor.common.Conversions.TRUE;
import static org.catrobat.catroid.formulaeditor.common.Conversions.booleanToDouble;
import static org.catrobat.catroid.formulaeditor.common.Conversions.convertArgumentToDouble;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.interpretOperatorEqual;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.interpretSensor;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.interpretUserDefinedBrickInput;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.interpretUserList;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.interpretUserVariable;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.isInteger;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.normalizeDegeneratedDoubleValues;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.tryInterpretCollision;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.tryInterpretDoubleValue;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.tryInterpretElementRecursive;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementOperations.tryParseIntFromObject;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementResources.addFunctionResources;
import static org.catrobat.catroid.formulaeditor.common.FormulaElementResources.addSensorsResources;
import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum ElementType {
		OPERATOR, FUNCTION, NUMBER, SENSOR, USER_VARIABLE, USER_LIST, USER_DEFINED_BRICK_INPUT, BRACKET, STRING, COLLISION_FORMULA
	}

	private ElementType type;
	private String value;
	private FormulaElement leftChild = null;
	private FormulaElement rightChild = null;
	public List<FormulaElement> additionalChildren;
	private transient FormulaElement parent;
	private transient Map<Functions, FormulaFunction> formulaFunctions;
	private transient TextBlockFunctionProvider textBlockFunctionProvider;

	protected FormulaElement() {
		textBlockFunctionProvider = new TextBlockFunctionProvider();
		List<FunctionProvider> functionProviders = Arrays.asList(
				new ArduinoFunctionProvider(),
				new RaspiFunctionProvider(),
				new MathFunctionProvider(),
				new TouchFunctionProvider(),
				textBlockFunctionProvider,
				new ObjectDetectorFunctionProvider()
		);

		formulaFunctions = new EnumMap<>(Functions.class);
		initFunctionMap(functionProviders, formulaFunctions);
		additionalChildren = new ArrayList<>();
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

	public FormulaElement(ElementType type, String value, FormulaElement parent, FormulaElement leftChild,
			FormulaElement rightChild, List<FormulaElement> additionalChildren) {
		this(type, value, parent, leftChild, rightChild);
		for (FormulaElement child : additionalChildren) {
			addAdditionalChild(child);
		}
	}

	private void initFunctionMap(List<FunctionProvider> functionProviders, Map<Functions, FormulaFunction> formulaFunctions) {
		for (FunctionProvider functionProvider : functionProviders) {
			functionProvider.addFunctionsToMap(formulaFunctions);
		}

		formulaFunctions.put(Functions.RAND, new BinaryFunction(this::interpretFunctionRand));
		formulaFunctions.put(Functions.IF_THEN_ELSE, new TernaryFunction(this::interpretFunctionIfThenElse));
	}

	public ElementType getElementType() {
		return type;
	}

	public String getValue() {
		return trimTrailingCharacters(value);
	}

	public List<InternToken> getInternTokenList() {
		List<InternToken> tokens = new LinkedList<>();

		switch (type) {
			case BRACKET:
				addBracketTokens(tokens, rightChild);
				break;
			case OPERATOR:
				addOperatorTokens(tokens, value);
				break;
			case FUNCTION:
				addFunctionTokens(tokens, value, leftChild, rightChild);
				break;
			case USER_VARIABLE:
				addToken(tokens, USER_VARIABLE, value);
				break;
			case USER_LIST:
				addToken(tokens, USER_LIST, value);
				break;
			case USER_DEFINED_BRICK_INPUT:
				addToken(tokens, USER_DEFINED_BRICK_INPUT, value);
				break;
			case NUMBER:
				addToken(tokens, NUMBER, trimTrailingCharacters(value));
				break;
			case SENSOR:
				addToken(tokens, SENSOR, value);
				break;
			case STRING:
				addToken(tokens, STRING, value);
				break;
			case COLLISION_FORMULA:
				addToken(tokens, COLLISION_FORMULA, value);
				break;
		}
		return tokens;
	}

	private void addToken(List<InternToken> tokens, InternTokenType tokenType) {
		tokens.add(new InternToken(tokenType));
	}

	private void addToken(List<InternToken> tokens, InternTokenType tokenType, String value) {
		tokens.add(new InternToken(tokenType, value));
	}

	private void addBracketTokens(List<InternToken> internTokenList, FormulaElement element) {
		addToken(internTokenList, BRACKET_OPEN);
		tryAddInternTokens(internTokenList, element);
		addToken(internTokenList, BRACKET_CLOSE);
	}

	private void addOperatorTokens(List<InternToken> tokens, String value) {
		tryAddInternTokens(tokens, leftChild);
		addToken(tokens, OPERATOR, value);
		tryAddInternTokens(tokens, rightChild);
	}

	private void addFunctionTokens(List<InternToken> tokens, String value, FormulaElement leftChild, FormulaElement rightChild) {
		addToken(tokens, FUNCTION_NAME, value);
		boolean functionHasParameters = false;
		if (leftChild != null) {
			addToken(tokens, FUNCTION_PARAMETERS_BRACKET_OPEN);
			functionHasParameters = true;
			tokens.addAll(leftChild.getInternTokenList());
		}
		if (rightChild != null) {
			addToken(tokens, FUNCTION_PARAMETER_DELIMITER);
			tokens.addAll(rightChild.getInternTokenList());
		}
		for (FormulaElement child : additionalChildren) {
			if (child != null) {
				addToken(tokens, FUNCTION_PARAMETER_DELIMITER);
				tokens.addAll(child.getInternTokenList());
			}
		}
		if (functionHasParameters) {
			addToken(tokens, FUNCTION_PARAMETERS_BRACKET_CLOSE);
		}
	}

	private void tryAddInternTokens(List<InternToken> tokens, FormulaElement child) {
		if (child != null) {
			tokens.addAll(child.getInternTokenList());
		}
	}

	public FormulaElement getRoot() {
		FormulaElement root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	public void updateElementByName(String oldName, String newName, ElementType type) {
		tryUpdateElementByName(leftChild, oldName, newName, type);
		tryUpdateElementByName(rightChild, oldName, newName, type);

		for (FormulaElement child : additionalChildren) {
			tryUpdateElementByName(child, oldName, newName, type);
		}

		if (matchesTypeAndName(type, oldName)) {
			value = newName;
		}
	}

	private void tryUpdateElementByName(FormulaElement element, String oldName, String newName,
			ElementType type) {
		if (element != null) {
			element.updateElementByName(oldName, newName, type);
		}
	}

	public final boolean containsSpriteInCollision(String name) {
		if (containsSpriteInCollision(leftChild, name) || containsSpriteInCollision(rightChild, name)) {
			return true;
		}
		for (FormulaElement child : additionalChildren) {
			if (containsSpriteInCollision(child, name)) {
				return true;
			}
		}
		return matchesTypeAndName(ElementType.COLLISION_FORMULA, name);
	}

	private boolean containsSpriteInCollision(FormulaElement element, String name) {
		return element != null && element.containsSpriteInCollision(name);
	}

	public final void insertFlattenForAllUserLists(FormulaElement element, FormulaElement parent) {
		if (element.leftChild != null) {
			insertFlattenForAllUserLists(element.leftChild, element);
		}
		if (element.rightChild != null) {
			insertFlattenForAllUserLists(element.rightChild, element);
		}
		for (FormulaElement child : element.additionalChildren) {
			if (child != null) {
				insertFlattenForAllUserLists(child, element);
			}
		}
		if (element.type == ElementType.USER_LIST && isNotUserListFunction(parent)) {
			insertFlattenBetweenParentAndElement(parent, element);
		}
	}

	public boolean isNotUserListFunction(FormulaElement element) {
		return element == null
				|| element.type != ElementType.FUNCTION
				|| (!element.value.equals(Functions.CONTAINS.name())
				&& !element.value.equals(Functions.NUMBER_OF_ITEMS.name())
				&& !element.value.equals(Functions.LIST_ITEM.name())
				&& !element.value.equals(Functions.INDEX_OF_ITEM.name())
				&& !element.value.equals(Functions.FLATTEN.name()));
	}

	public void insertFlattenBetweenParentAndElement(FormulaElement parent,
			FormulaElement element) {
		FormulaElement flatten = new FormulaElement(ElementType.FUNCTION,
				Functions.FLATTEN.name(), parent);
		insertElementBeforeChildInFormulaTree(parent, element, flatten);
	}

	private void insertElementBeforeChildInFormulaTree(FormulaElement parent, FormulaElement child,
			FormulaElement elementToInsert) {
		if (child == null || elementToInsert == null) {
			return;
		}

		child.parent = elementToInsert;
		elementToInsert.setLeftChild(child);

		if (parent == null) {
			return;
		}

		if (parent.leftChild == child) {
			parent.leftChild = elementToInsert;
		} else if (parent.rightChild == child) {
			parent.rightChild = elementToInsert;
		} else {
			for (int i = 0; i < parent.additionalChildren.size(); i++) {
				if (parent.additionalChildren.get(i) == child) {
					parent.additionalChildren.set(i, elementToInsert);
				}
			}
		}
	}

	private boolean matchesTypeAndName(ElementType queriedType, String name) {
		return type == queriedType && value.equals(name);
	}

	public void updateCollisionFormulaToVersion(Project currentProject) {
		tryUpdateCollisionFormulaToVersion(leftChild, currentProject);
		tryUpdateCollisionFormulaToVersion(rightChild, currentProject);
		for (FormulaElement child : additionalChildren) {
			tryUpdateCollisionFormulaToVersion(child, currentProject);
		}
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

	public Object interpretRecursive(Scope scope) {
		Object rawReturnValue = rawInterpretRecursive(scope);
		return normalizeDegeneratedDoubleValues(rawReturnValue);
	}

	private Object rawInterpretRecursive(Scope scope) {
		ProjectManager projectManager = ProjectManager.getInstance();
		Project currentProject = projectManager != null ? projectManager.getCurrentProject() : null;
		Scene currentlyPlayingScene = projectManager != null ? projectManager.getCurrentlyPlayingScene() : null;
		Scene currentlyEditedScene = projectManager != null ? projectManager.getCurrentlyEditedScene() : null;

		switch (type) {
			case BRACKET:
				if (additionalChildren.size() != 0) {
					return additionalChildren.get(additionalChildren.size() - 1).interpretRecursive(scope);
				}
				return rightChild.interpretRecursive(scope);
			case NUMBER:
			case STRING:
				return value;
			case OPERATOR:
				return tryInterpretOperator(scope, value);
			case FUNCTION:
				Functions function = Functions.getFunctionByValue(value);
				return interpretFunction(function, scope);
			case SENSOR:
				return interpretSensor(scope.getSprite(), currentlyEditedScene, currentProject, value);
			case USER_VARIABLE:
				UserVariable userVariable = UserDataWrapper.getUserVariable(value, scope);
				return interpretUserVariable(userVariable);
			case USER_LIST:
				UserList userList = UserDataWrapper.getUserList(value, scope);
				return interpretUserList(userList);
			case USER_DEFINED_BRICK_INPUT:
				UserData userBrickVariable = UserDataWrapper.getUserDefinedBrickInput(value,
						scope.getSequence());
				return interpretUserDefinedBrickInput(userBrickVariable);
			case COLLISION_FORMULA:
				StageListener stageListener = StageActivity.stageListener;
				return tryInterpretCollision(scope.getSprite().look, value, currentlyPlayingScene,
						stageListener);
		}
		return FALSE;
	}

	@NotNull
	private Object tryInterpretOperator(Scope scope, String value) {
		Operators operator = Operators.getOperatorByValue(value);
		if (operator == null) {
			return false;
		}
		return interpretOperator(operator, scope);
	}

	private Object interpretFunction(Functions function, Scope scope) {
		List<Object> arguments = new ArrayList<>();
		arguments.add(tryInterpretRecursive(leftChild, scope));
		arguments.add(tryInterpretRecursive(rightChild, scope));

		for (FormulaElement child : additionalChildren) {
			arguments.add(tryInterpretRecursive(child, scope));
		}

		switch (function) {
			case LETTER:
				return interpretFunctionLetter(arguments.get(0), arguments.get(1));
			case SUBTEXT:
				return interpretFunctionSubtext(arguments.get(0), arguments.get(1),
						arguments.get(2));
			case LENGTH:
				return interpretFunctionLength(arguments.get(0), scope);
			case JOIN:
				return interpretFunctionJoin(scope, leftChild, rightChild);
			case JOIN3:
				return interpretFunctionJoin3(scope, leftChild, rightChild, additionalChildren);
			case REGEX:
				return tryInterpretFunctionRegex(scope, leftChild, rightChild);
			case LIST_ITEM:
				return interpretFunctionListItem(arguments.get(0), scope);
			case CONTAINS:
				return interpretFunctionContains(arguments.get(1), scope);
			case NUMBER_OF_ITEMS:
				return interpretFunctionNumberOfItems(arguments.get(0), scope);
			case INDEX_OF_ITEM:
				return interpretFunctionIndexOfItem(arguments.get(0), scope);
			case FLATTEN:
				return interpretFunctionFlatten(scope, leftChild);
			case COLLIDES_WITH_COLOR:
				return booleanToDouble(new ColorCollisionDetection(scope, StageActivity.stageListener)
						.tryInterpretFunctionTouchesColor(arguments.get(0)));
			case COLOR_TOUCHES_COLOR:
				return booleanToDouble(new ColorCollisionDetection(scope, StageActivity.stageListener)
						.tryInterpretFunctionColorTouchesColor(arguments.get(0), arguments.get(1)));
			case COLOR_AT_XY:
				return new ColorAtXYDetection(scope, StageActivity.stageListener)
						.tryInterpretFunctionColorAtXY(arguments.get(0), arguments.get(1));
			case TEXT_BLOCK_FROM_CAMERA:
				return textBlockFunctionProvider.interpretFunctionTextBlock(Double.parseDouble(arguments.get(0).toString()));
			case TEXT_BLOCK_LANGUAGE_FROM_CAMERA:
				return textBlockFunctionProvider.interpretFunctionTextBlockLanguage(Double.parseDouble(arguments.get(0).toString()));
			case COLOR_EQUALS_COLOR:
				return booleanToDouble(new ColorEqualsColor().tryInterpretFunctionColorEqualsColor(arguments.get(0), arguments.get(1),
						arguments.get(2)));
			default:
				return interpretFormulaFunction(function, arguments);
		}
	}

	@Nullable
	private Object tryInterpretRecursive(FormulaElement element, Scope scope) {
		if (element == null) {
			return null;
		}
		return element.interpretRecursive(scope);
	}

	private Object interpretFormulaFunction(Functions function, List<Object> arguments) {
		List<Double> argumentsDouble = new ArrayList<>();
		for (Object argument : arguments) {
			argumentsDouble.add(convertArgumentToDouble(argument));
		}
		FormulaFunction formulaFunction = formulaFunctions.get(function);
		if (formulaFunction == null) {
			return FALSE;
		}
		if (argumentsDouble.size() == 2) {
			return formulaFunction.execute(argumentsDouble.get(0), argumentsDouble.get(1));
		}
		if (argumentsDouble.size() == 3 && argumentsDouble.get(0) != null && function == IF_THEN_ELSE) {
			Double ifCondition = argumentsDouble.get(0);
			Object thenPart = (arguments.get(1) instanceof String) ? arguments.get(1) : argumentsDouble.get(1);
			Object elsePart = (arguments.get(2) instanceof String) ? arguments.get(2) : argumentsDouble.get(2);
			return interpretFunctionIfThenElseObject(ifCondition, thenPart, elsePart);
		}
		return formulaFunction.execute(argumentsDouble.get(0), argumentsDouble.get(1), argumentsDouble.get(2));
	}

	private Object interpretFunctionNumberOfItems(Object left, Scope scope) {
		if (leftChild.type == ElementType.USER_LIST) {
			UserList userList = UserDataWrapper.getUserList(leftChild.value, scope);
			return (double) handleNumberOfItemsOfUserListParameter(userList);
		}
		return interpretFunctionLength(left, scope);
	}

	private int handleNumberOfItemsOfUserListParameter(UserList userList) {
		if (userList == null) {
			return 0;
		}

		return userList.getValue().size();
	}

	private Object interpretFunctionContains(Object right, Scope scope) {
		UserList userList = getUserListOfChild(leftChild, scope);
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

	private Object interpretFunctionIndexOfItem(Object left, Scope scope) {
		if (rightChild.getElementType() == ElementType.USER_LIST) {
			UserList userList = UserDataWrapper.getUserList(rightChild.value, scope);
			return (double) (userList.getIndexOf(left) + 1);
		}

		return FALSE;
	}

	private Object interpretFunctionListItem(Object left, Scope scope) {
		if (left == null) {
			return "";
		}

		UserList userList = getUserListOfChild(rightChild, scope);
		if (userList == null) {
			return "";
		}

		int index = tryParseIntFromObject(left) - 1;

		if (index < 0 || index >= userList.getValue().size()) {
			return "";
		}
		return userList.getValue().get(index);
	}

	@Nullable
	private UserList getUserListOfChild(FormulaElement child, Scope scope) {
		if (child.getElementType() != ElementType.USER_LIST) {
			return null;
		}
		return UserDataWrapper.getUserList(child.value, scope);
	}

	private static String interpretFunctionJoin(Scope scope, FormulaElement leftChild,
			FormulaElement rightChild) {
		return interpretFunctionString(leftChild, scope).concat(interpretFunctionString(rightChild,
				scope));
	}

	private static String interpretFunctionJoin3(Scope scope, FormulaElement leftChild,
			FormulaElement rightChild, List<FormulaElement> additionalChildren) {
		return interpretFunctionString(leftChild, scope).concat(interpretFunctionString(rightChild,
				scope).concat(interpretFunctionString(additionalChildren.get(0), scope)));
	}

	private static String interpretFunctionFlatten(Scope scope, FormulaElement leftChild) {
		return interpretFunctionString(leftChild, scope);
	}

	private static String tryInterpretFunctionRegex(Scope scope, FormulaElement leftChild,
			FormulaElement rightChild) {
		try {
			String left = interpretFunctionString(leftChild, scope);
			String right = interpretFunctionString(rightChild, scope);
			return interpretFunctionRegex(left, right);
		} catch (IllegalArgumentException exception) {
			return exception.getLocalizedMessage();
		}
	}

	private static String interpretFunctionRegex(String patternString, String matcherString) {
		Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(matcherString);
		if (matcher.find()) {
			int groupIndex = matcher.groupCount() == 0 ? 0 : 1;
			return matcher.group(groupIndex);
		} else {
			return "";
		}
	}

	private static String interpretFunctionString(FormulaElement child, Scope scope) {
		String parameterInterpretation = "";
		if (child != null) {
			Object objectInterpretation = child.interpretRecursive(scope);
			switch (child.getElementType()) {
				case STRING:
					parameterInterpretation = child.getValue();
					break;
				case NUMBER:
					parameterInterpretation = formatNumberString((String) objectInterpretation);
					break;
				default:
					parameterInterpretation += objectInterpretation;
					parameterInterpretation = trimTrailingCharacters(parameterInterpretation);
			}
		}
		return parameterInterpretation;
	}

	private static String formatNumberString(String numberString) {
		double number = Double.parseDouble(numberString);
		String formattedNumberString = "";
		if (!Double.isNaN(number)) {
			formattedNumberString += isInteger(number) ? (int) number : number;
		}
		return trimTrailingCharacters(formattedNumberString);
	}

	private Object interpretFunctionLength(Object left, Scope scope) {
		if (leftChild == null) {
			return FALSE;
		}
		switch (leftChild.type) {
			case NUMBER:
			case STRING:
				return (double) leftChild.value.length();
			case USER_VARIABLE:
				UserVariable userVariable = UserDataWrapper.getUserVariable(leftChild.value, scope);
				return (double) calculateUserVariableLength(userVariable);
			case USER_LIST:
				UserList userList = UserDataWrapper.getUserList(leftChild.value, scope);
				return calculateUserListLength(userList, left, scope);
			default:
				if (left instanceof Double && ((Double) left).isNaN()) {
					return 0d;
				}
				return (double) (String.valueOf(left)).length();
		}
	}

	private int calculateUserVariableLength(UserVariable userVariable) {
		Object userVariableValue = userVariable.getValue();
		if (userVariableValue instanceof String) {
			return String.valueOf(userVariableValue).length();
		} else {
			if (userVariableValue.toString().equals("true") || userVariableValue.toString().equals("false")) {
				return 1;
			} else if (isInteger((Double) userVariableValue)) {
				return Integer.toString(((Double) userVariableValue).intValue()).length();
			} else {
				return Double.toString(((Double) userVariableValue)).length();
			}
		}
	}

	private double calculateUserListLength(UserList userList, Object left, Scope scope) {
		if (userList == null || userList.getValue().isEmpty()) {
			return FALSE;
		}

		Object interpretedList = leftChild.interpretRecursive(scope);
		if (interpretedList instanceof Double) {
			Double interpretedListDoubleValue = (Double) interpretedList;
			if (interpretedListDoubleValue.isNaN() || interpretedListDoubleValue.isInfinite()) {
				return FALSE;
			}
			return String.valueOf(interpretedListDoubleValue.intValue()).length();
		}
		if (interpretedList instanceof String) {
			return ((String) interpretedList).length();
		}
		if (left instanceof Double && ((Double) left).isNaN()) {
			return FALSE;
		}
		return String.valueOf(left).length();
	}

	private Object interpretFunctionLetter(Object left, Object right) {
		if (left == null || right == null) {
			return "";
		}

		int index = tryParseIntFromObject(left) - 1;
		String stringValueOfRight = String.valueOf(right);

		if (index < 0 || index >= stringValueOfRight.length()) {
			return "";
		}
		return String.valueOf(stringValueOfRight.charAt(index));
	}

	private Object interpretFunctionSubtext(Object leftChild,
			Object rightChild, Object string) {
		if (leftChild == null || rightChild == null) {
			return "";
		}

		int start = tryParseIntFromObject(leftChild) - 1;
		int end = tryParseIntFromObject(rightChild);
		String stringValueOfString = String.valueOf(string);

		if (start < 0 || end < 0 || start > end || end > stringValueOfString.length()) {
			return "";
		}

		return stringValueOfString.substring(start, end);
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

	private Object interpretFunctionIfThenElseObject(Double condition, Object thenValue,
			Object elseValue) {
		if (Double.isNaN(condition)) {
			return Double.NaN;
		}
		if (condition != 0) {
			return thenValue;
		}
		return elseValue;
	}

	private double interpretFunctionIfThenElse(double condition, double thenValue, double elseValue) {
		if (Double.isNaN(condition)) {
			return Double.NaN;
		}
		if (condition != 0) {
			return thenValue;
		}
		return elseValue;
	}

	private static boolean isNumberWithDecimalPoint(FormulaElement element) {
		return element.type == ElementType.NUMBER && element.value.contains(".");
	}

	private double interpretOperator(@NotNull Operators operator, Scope scope) {
		if (leftChild != null) {
			return interpretBinaryOperator(operator, scope);
		} else {
			return interpretUnaryOperator(operator, scope);
		}
	}

	private double interpretUnaryOperator(@NotNull Operators operator, Scope scope) {
		Object rightObject = tryInterpretElementRecursive(rightChild, scope);
		double right = tryInterpretDoubleValue(rightObject);

		switch (operator) {
			case MINUS:
				return -right;
			case LOGICAL_NOT:
				return booleanToDouble(right == FALSE);
			default:
				return FALSE;
		}
	}

	private double interpretBinaryOperator(@NotNull Operators operator, Scope scope) {
		Object leftObject = tryInterpretElementRecursive(leftChild, scope);
		Object rightObject = tryInterpretElementRecursive(rightChild, scope);

		Double leftDouble = tryInterpretDoubleValue(leftObject);
		Double rightDouble = tryInterpretDoubleValue(rightObject);

		BigDecimal left;
		BigDecimal right;
		try {
			left = BigDecimal.valueOf(tryInterpretDoubleValue(leftObject));
		} catch (NumberFormatException e) {
			left = BigDecimal.valueOf(0d);
		}
		try {
			right = BigDecimal.valueOf(tryInterpretDoubleValue(rightObject));
		} catch (NumberFormatException e) {
			right = BigDecimal.valueOf(0d);
		}

		boolean atLeastOneIsNaN = Double.isNaN(leftDouble) || Double.isNaN(rightDouble);

		switch (operator) {
			case PLUS:
				if (atLeastOneIsNaN) {
					return Double.NaN;
				}
				return left.add(right, MathContext.DECIMAL128).doubleValue();
			case MINUS:
				if (atLeastOneIsNaN) {
					return Double.NaN;
				}
				return left.subtract(right, MathContext.DECIMAL128).doubleValue();
			case MULT:
				if (atLeastOneIsNaN) {
					return Double.NaN;
				}
				return left.multiply(right, MathContext.DECIMAL128).doubleValue();
			case DIVIDE:
				if (atLeastOneIsNaN || right.equals(BigDecimal.valueOf(0d))) {
					return Double.NaN;
				}
				return left.divide(right, MathContext.DECIMAL128).doubleValue();
			case POW:
				if (atLeastOneIsNaN) {
					return Double.NaN;
				}
				return Math.pow(left.doubleValue(), right.doubleValue());
			case EQUAL:
				return booleanToDouble(interpretOperatorEqual(leftObject, rightObject));
			case NOT_EQUAL:
				return booleanToDouble(!(interpretOperatorEqual(leftObject, rightObject)));
			case GREATER_THAN:
				return booleanToDouble(leftDouble.compareTo(rightDouble) > 0);
			case GREATER_OR_EQUAL:
				return booleanToDouble(leftDouble.compareTo(rightDouble) >= 0);
			case SMALLER_THAN:
				return booleanToDouble(leftDouble.compareTo(rightDouble) < 0);
			case SMALLER_OR_EQUAL:
				return booleanToDouble(leftDouble.compareTo(rightDouble) <= 0);
			case LOGICAL_AND:
				return booleanToDouble(leftDouble != FALSE && rightDouble != FALSE);
			case LOGICAL_OR:
				return booleanToDouble(leftDouble != FALSE || rightDouble != FALSE);
			default:
				return FALSE;
		}
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

	public void addAdditionalChild(FormulaElement child) {
		additionalChildren.add(child);
		child.parent = this;
	}

	public void replaceElement(FormulaElement current) {
		parent = current.parent;
		leftChild = current.leftChild;
		rightChild = current.rightChild;
		for (int index = 0; index < current.additionalChildren.size(); index++) {
			if (index < additionalChildren.size()) {
				additionalChildren.set(index, current.additionalChildren.get(index));
			} else {
				additionalChildren.add(current.additionalChildren.get(index));
			}
		}
		value = current.value;
		type = current.type;

		if (leftChild != null) {
			leftChild.parent = this;
		}
		if (rightChild != null) {
			rightChild.parent = this;
		}
		for (FormulaElement child : additionalChildren) {
			if (child != null) {
				child.parent = this;
			}
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

	public boolean isBoolean(Scope scope) {
		if (type == ElementType.USER_VARIABLE) {
			return isUserVariableBoolean(scope);
		} else if (type == ElementType.USER_LIST) {
			return isUserListBoolean(scope);
		} else if (type == ElementType.USER_DEFINED_BRICK_INPUT) {
			return isUserDefinedBrickInputBoolean(scope);
		} else {
			return isOtherBooleanFormulaElement();
		}
	}

	private boolean isUserVariableBoolean(Scope scope) {
		UserVariable userVariable = UserDataWrapper.getUserVariable(value, scope);
		return userVariable != null && userVariable.getValue() instanceof Boolean;
	}

	private boolean isUserListBoolean(Scope scope) {
		List<Object> listValues = UserDataWrapper.getUserList(value, scope).getValue();
		if (listValues.size() != 1) {
			return false;
		}
		return listValues.get(0) instanceof Boolean;
	}

	private boolean isUserDefinedBrickInputBoolean(Scope scope) {
		UserData userData = UserDataWrapper.getUserDefinedBrickInput(value, scope.getSequence());
		if (userData != null && userData.getValue() instanceof Formula) {
			return ((Formula) userData.getValue()).getRoot().isBoolean(scope);
		} else {
			return false;
		}
	}

	private boolean isOtherBooleanFormulaElement() {
		return (type == ElementType.FUNCTION
				&& Functions.isBoolean(Functions.getFunctionByValue(value)))
				|| (type == ElementType.SENSOR
				&& Sensors.isBoolean(Sensors.getSensorByValue(value)))
				|| (type == ElementType.OPERATOR
				&& Operators.getOperatorByValue(value).isLogicalOperator)
				|| type == ElementType.COLLISION_FORMULA;
	}

	public boolean containsElement(ElementType elementType) {
		if (type.equals(elementType)
				|| (leftChild != null && leftChild.containsElement(elementType))
				|| (rightChild != null && rightChild.containsElement(elementType))) {
			return true;
		}
		for (FormulaElement child : additionalChildren) {
			if (child != null && child.containsElement(elementType)) {
				return true;
			}
		}
		return false;
	}

	public boolean isNumber() {
		if (type == ElementType.OPERATOR) {
			Operators operator = Operators.getOperatorByValue(value);
			return (operator == Operators.MINUS) && (leftChild == null) && rightChild.isNumber();
		}
		return type == ElementType.NUMBER;
	}

	@Override
	public FormulaElement clone() {
		FormulaElement leftChildClone = tryCloneElement(leftChild);
		FormulaElement rightChildClone = tryCloneElement(rightChild);
		List<FormulaElement> additionalChildrenClones = new ArrayList<>();
		for (FormulaElement child : additionalChildren) {
			additionalChildrenClones.add(tryCloneElement(child));
		}
		String valueClone = value == null ? "" : value;
		return new FormulaElement(type, valueClone, null, leftChildClone, rightChildClone,
				additionalChildrenClones);
	}

	private FormulaElement tryCloneElement(FormulaElement element) {
		return element == null ? null : element.clone();
	}

	public void addRequiredResources(final Set<Integer> requiredResourcesSet) {
		tryAddRequiredResources(requiredResourcesSet, leftChild);
		tryAddRequiredResources(requiredResourcesSet, rightChild);

		for (FormulaElement child : additionalChildren) {
			tryAddRequiredResources(requiredResourcesSet, child);
		}

		switch (type) {
			case FUNCTION:
				addFunctionResources(requiredResourcesSet, Functions.getFunctionByValue(value));
				break;
			case SENSOR:
				addSensorsResources(requiredResourcesSet, Sensors.getSensorByValue(value));
				break;
			case COLLISION_FORMULA:
				requiredResourcesSet.add(Brick.COLLISION);
				break;
			default:
		}
	}

	private void tryAddRequiredResources(Set<Integer> resourceSet, FormulaElement element) {
		if (element != null) {
			element.addRequiredResources(resourceSet);
		}
	}

	public void setValue(String value) {
		this.value = value;
	}
}
