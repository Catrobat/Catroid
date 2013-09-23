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

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class InternFormulaParser {

	private class InternFormulaParserException extends Exception {

		private static final long serialVersionUID = 1L;

		public InternFormulaParserException(String errorMessage) {
			super(errorMessage);
		}
	}

	public static final int PARSER_OK = -1;
	public static final int PARSER_STACK_OVERFLOW = -2;
	public static final int PARSER_INPUT_SYNTAX_ERROR = -3;
	public static final int PARSER_NO_INPUT = -4;
	private static final int MAXIMUM_TOKENS_TO_PARSE = 1000;

	private List<InternToken> internTokensToParse;
	private int currentTokenParseIndex;
	private int errorTokenIndex;
	private InternToken currentToken;

	public InternFormulaParser(List<InternToken> internTokensToParse) {
		this.internTokensToParse = internTokensToParse;
	}

	private void getNextToken() throws InternFormulaParserException {
		currentTokenParseIndex++;
		currentToken = internTokensToParse.get(currentTokenParseIndex);

	}

	public int getErrorTokenIndex() {
		return errorTokenIndex;
	}

	private FormulaElement findLowerOrEqualPriorityOperatorElement(Operators currentOperator,
			FormulaElement currentElement) {

		FormulaElement returnElement = currentElement.getParent();
		FormulaElement notNullElement = currentElement;
		boolean condition = true;

		while (condition) {
			if (returnElement == null) {
				condition = false;
				returnElement = notNullElement;
			} else {
				Operators parentOperator = Operators.getOperatorByValue(returnElement.getValue());
				int compareOperator = parentOperator.compareOperatorTo(currentOperator);
				if (compareOperator < 0) {
					condition = false;
					returnElement = notNullElement;
				} else {
					notNullElement = returnElement;
					returnElement = returnElement.getParent();
				}
			}
		}
		return returnElement;
	}

	public void handleOperator(String operator, FormulaElement currentElement, FormulaElement newElement) {

		if (currentElement.getParent() == null) {
			new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, currentElement, newElement);
			return;
		}

		Operators parentOperator = Operators.getOperatorByValue(currentElement.getParent().getValue());
		Operators currentOperator = Operators.getOperatorByValue(operator);

		int compareOperator = parentOperator.compareOperatorTo(currentOperator);

		if (compareOperator >= 0) {
			FormulaElement newLeftChild = findLowerOrEqualPriorityOperatorElement(currentOperator, currentElement);
			FormulaElement newParent = newLeftChild.getParent();

			if (newParent != null) {
				newLeftChild.replaceWithSubElement(operator, newElement);
			} else {
				new FormulaElement(FormulaElement.ElementType.OPERATOR, operator, null, newLeftChild, newElement);
			}
		} else {
			currentElement.replaceWithSubElement(operator, newElement);
		}
	}

	private void addEndOfFileToken() {
		InternToken endOfFileParserToken = new InternToken(InternTokenType.PARSER_END_OF_FILE);
		internTokensToParse.add(endOfFileParserToken);
	}

	private void removeEndOfFileToken() {
		internTokensToParse.remove(internTokensToParse.size() - 1);
	}

	public FormulaElement parseFormula() {
		errorTokenIndex = PARSER_OK;
		currentTokenParseIndex = 0;

		if (internTokensToParse == null) {
			errorTokenIndex = PARSER_NO_INPUT;
			return null;
		}
		if (internTokensToParse.size() == 0) {
			errorTokenIndex = PARSER_NO_INPUT;
			return null;
		}
		if (internTokensToParse.size() > MAXIMUM_TOKENS_TO_PARSE) {
			errorTokenIndex = PARSER_STACK_OVERFLOW;
			errorTokenIndex = 0;
			return null;
		}

		try {
			List<InternToken> copyIternTokensToParse = new ArrayList<InternToken>(internTokensToParse);
			if (InternFormulaUtils.applyBracketCorrection(copyIternTokensToParse)) {
				Log.i("info", "applyBracketCorrection-> TRUE");
				internTokensToParse.clear();
				internTokensToParse.addAll(copyIternTokensToParse);
			}
		} catch (EmptyStackException emptyStackException) {
			Log.i("info", "emptyStackException-> TRUE");
		}

		addEndOfFileToken();
		currentToken = internTokensToParse.get(0);
		FormulaElement formulaParseTree = null;

		try {
			formulaParseTree = formula();
		} catch (InternFormulaParserException parseExeption) {
			errorTokenIndex = currentTokenParseIndex;
		}
		removeEndOfFileToken();
		return formulaParseTree;
	}

	private FormulaElement formula() throws InternFormulaParserException {
		FormulaElement termListTree = termList();

		if (currentToken.isEndOfFileToken()) {
			return termListTree;
		}

		throw new InternFormulaParserException("Parse Error");
	}

	private FormulaElement termList() throws InternFormulaParserException {
		FormulaElement currentElement = term();
		FormulaElement loopTermTree;
		String operatorStringValue;

		while (currentToken.isOperator() && !currentToken.getTokenStringValue().equals(Operators.LOGICAL_NOT.name())) {
			operatorStringValue = currentToken.getTokenStringValue();
			getNextToken();
			loopTermTree = term();
			handleOperator(operatorStringValue, currentElement, loopTermTree);
			currentElement = loopTermTree;
		}
		return currentElement.getRoot();
	}

	private FormulaElement term() throws InternFormulaParserException {

		FormulaElement termTree = new FormulaElement(FormulaElement.ElementType.NUMBER, null, null);
		FormulaElement currentElement = termTree;

		if (currentToken.isOperator() && currentToken.getTokenStringValue().equals(Operators.MINUS.name())) {
			currentElement = new FormulaElement(FormulaElement.ElementType.NUMBER, null, termTree, null, null);
			termTree.replaceElement(new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.MINUS.name(),
					null, null, currentElement));
			getNextToken();
		} else if (currentToken.isOperator() && currentToken.getTokenStringValue().equals(Operators.LOGICAL_NOT.name())) {
			currentElement = new FormulaElement(FormulaElement.ElementType.NUMBER, null, termTree, null, null);
			termTree.replaceElement(new FormulaElement(FormulaElement.ElementType.OPERATOR, Operators.LOGICAL_NOT
					.name(), null, null, currentElement));

			getNextToken();
		}

		if (currentToken.isNumber()) {
			currentElement.replaceElement(FormulaElement.ElementType.NUMBER, number());
		} else if (currentToken.isBracketOpen()) {
			getNextToken();
			currentElement.replaceElement(new FormulaElement(FormulaElement.ElementType.BRACKET, null, null, null,
					termList()));
			if (!currentToken.isBracketClose()) {
				throw new InternFormulaParserException("Parse Error");
			}
			getNextToken();
		} else if (currentToken.isFunctionName()) {
			currentElement.replaceElement(function());
		} else if (currentToken.isSensor()) {
			currentElement.replaceElement(sensor());
		} else if (currentToken.isUserVariable()) {
			currentElement.replaceElement(userVariable());
		} else {
			throw new InternFormulaParserException("Parse Error");
		}

		return termTree;
	}

	private FormulaElement userVariable() throws InternFormulaParserException {
		UserVariablesContainer userVariables = ProjectManager.getInstance().getCurrentProject().getUserVariables();

		UserBrick currentBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = currentBrick == null ? -1 : currentBrick.getDefinitionBrick().getUserBrickId();

		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		if (userVariables.getUserVariable(currentToken.getTokenStringValue(), userBrickId, currentSprite) == null) {
			throw new InternFormulaParserException("Parse Error");
		}

		FormulaElement lookTree = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				currentToken.getTokenStringValue(), null);

		getNextToken();
		return lookTree;
	}

	private FormulaElement sensor() throws InternFormulaParserException {
		if (!Sensors.isSensor(currentToken.getTokenStringValue())) {
			throw new InternFormulaParserException("Parse Error");
		}

		FormulaElement sensorTree = new FormulaElement(FormulaElement.ElementType.SENSOR,
				currentToken.getTokenStringValue(), null);
		getNextToken();
		return sensorTree;
	}

	private FormulaElement function() throws InternFormulaParserException {
		FormulaElement functionTree = new FormulaElement(FormulaElement.ElementType.FUNCTION, null, null);

		if (!Functions.isFunction(currentToken.getTokenStringValue())) {
			throw new InternFormulaParserException("Parse Error");
		}

		functionTree = new FormulaElement(FormulaElement.ElementType.FUNCTION, currentToken.getTokenStringValue(), null);
		getNextToken();

		if (currentToken.isFunctionParameterBracketOpen()) {
			getNextToken();
			functionTree.setLeftChild(termList());
			if (currentToken.isFunctionParameterDelimiter()) {
				getNextToken();
				functionTree.setRightChild(termList());
			}
			if (!currentToken.isFunctionParameterBracketClose()) {
				throw new InternFormulaParserException("Parse Error");
			}
			getNextToken();
		}
		return functionTree;
	}

	private String number() throws InternFormulaParserException {
		String numberToCheck = currentToken.getTokenStringValue();

		if (!numberToCheck.matches("(\\d)+(\\.(\\d)+)?")) {
			throw new InternFormulaParserException("Parse Error");
		}

		getNextToken();
		return numberToCheck;
	}
}
