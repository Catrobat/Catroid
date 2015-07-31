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

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public final class InternFormulaUtils {

	// Suppress default constructor for noninstantiability
	private InternFormulaUtils() {
		throw new AssertionError();
	}

	public static List<InternToken> getFunctionByFunctionBracketClose(List<InternToken> internTokenList,
			int functionBracketCloseInternTokenListIndex) {

		if (functionBracketCloseInternTokenListIndex == 0
				|| functionBracketCloseInternTokenListIndex == internTokenList.size()) {
			return null;
		}

		List<InternToken> functionInternTokenList = new LinkedList<InternToken>();
		functionInternTokenList.add(internTokenList.get(functionBracketCloseInternTokenListIndex));

		int functionIndex = functionBracketCloseInternTokenListIndex - 1;
		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			if (functionIndex < 0) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex--;

			switch (tempSearchToken.getInternTokenType()) {
				case FUNCTION_PARAMETERS_BRACKET_OPEN:
					nestedFunctionsCounter--;
					break;

				case FUNCTION_PARAMETERS_BRACKET_CLOSE:
					nestedFunctionsCounter++;
					break;
			}

			functionInternTokenList.add(tempSearchToken);
		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| nestedFunctionsCounter != 0);

		if (functionIndex < 0) {
			return null;
		}
		tempSearchToken = internTokenList.get(functionIndex);

		if (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		functionInternTokenList.add(tempSearchToken);

		Collections.reverse(functionInternTokenList);

		return functionInternTokenList;
	}

	public static List<InternToken> getFunctionByParameterDelimiter(List<InternToken> internTokenList,
			int functionParameterDelimiterInternTokenListIndex) {

		if (functionParameterDelimiterInternTokenListIndex == 0
				|| functionParameterDelimiterInternTokenListIndex == internTokenList.size()) {
			return null;
		}

		List<InternToken> functionInternTokenList = new LinkedList<InternToken>();
		functionInternTokenList.add(internTokenList.get(functionParameterDelimiterInternTokenListIndex));

		int functionIndex = functionParameterDelimiterInternTokenListIndex - 1;
		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			if (functionIndex < 0) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex--;

			switch (tempSearchToken.getInternTokenType()) {
				case FUNCTION_PARAMETERS_BRACKET_OPEN:
					nestedFunctionsCounter--;
					break;

				case FUNCTION_PARAMETERS_BRACKET_CLOSE:
					nestedFunctionsCounter++;
					break;
			}

			functionInternTokenList.add(tempSearchToken);
		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| nestedFunctionsCounter != 0);

		if (functionIndex < 0) {
			return null;
		}
		tempSearchToken = internTokenList.get(functionIndex);

		if (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		functionInternTokenList.add(tempSearchToken);

		Collections.reverse(functionInternTokenList);

		functionIndex = functionParameterDelimiterInternTokenListIndex + 1;
		nestedFunctionsCounter = 1;

		do {
			if (functionIndex >= internTokenList.size()) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex++;

			switch (tempSearchToken.getInternTokenType()) {
				case FUNCTION_PARAMETERS_BRACKET_OPEN:
					nestedFunctionsCounter++;
					break;

				case FUNCTION_PARAMETERS_BRACKET_CLOSE:
					nestedFunctionsCounter--;
					break;
			}

			functionInternTokenList.add(tempSearchToken);
		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
				|| nestedFunctionsCounter != 0);

		return functionInternTokenList;
	}

	public static List<InternToken> getFunctionByFunctionBracketOpen(List<InternToken> internTokenList,
			int functionBracketOpenInternTokenListIndex) {

		if (functionBracketOpenInternTokenListIndex <= 0
				|| functionBracketOpenInternTokenListIndex >= internTokenList.size()) {
			return null;
		}

		InternToken functionNameInternToken = internTokenList.get(functionBracketOpenInternTokenListIndex - 1);

		if (functionNameInternToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		List<InternToken> functionInternTokenList = getFunctionByName(internTokenList,
				functionBracketOpenInternTokenListIndex - 1);

		return functionInternTokenList;
	}

	public static List<InternToken> getFunctionByName(List<InternToken> internTokenList, int functionStartListIndex) {

		InternToken functionNameToken = internTokenList.get(functionStartListIndex);

		List<InternToken> functionInternTokenList = new LinkedList<InternToken>();

		if (functionNameToken.getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		functionInternTokenList.add(functionNameToken);

		int functionIndex = functionStartListIndex + 1;

		if (functionIndex >= internTokenList.size()) {
			return functionInternTokenList;
		}

		InternToken functionStartParameter = internTokenList.get(functionIndex);

		if (!functionStartParameter.isFunctionParameterBracketOpen()) {
			return functionInternTokenList;
		}

		functionInternTokenList.add(functionStartParameter);

		functionIndex++;
		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			if (functionIndex >= internTokenList.size()) {
				return null;
			}
			tempSearchToken = internTokenList.get(functionIndex);
			functionIndex++;

			switch (tempSearchToken.getInternTokenType()) {
				case FUNCTION_PARAMETERS_BRACKET_OPEN:
					nestedFunctionsCounter++;
					break;

				case FUNCTION_PARAMETERS_BRACKET_CLOSE:
					nestedFunctionsCounter--;
					break;
			}

			functionInternTokenList.add(tempSearchToken);
		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
				|| nestedFunctionsCounter != 0);

		return functionInternTokenList;
	}

	public static List<InternToken> generateTokenListByBracketOpen(List<InternToken> internTokenList,
			int internTokenListIndex) {

		if (internTokenListIndex == internTokenList.size()
				|| internTokenList.get(internTokenListIndex).getInternTokenType() != InternTokenType.BRACKET_OPEN) {
			return null;
		}

		List<InternToken> bracketInternTokenListToReturn = new LinkedList<InternToken>();
		bracketInternTokenListToReturn.add(internTokenList.get(internTokenListIndex));

		int bracketsIndex = internTokenListIndex + 1;
		int nestedBracketsCounter = 1;
		InternToken tempSearchToken;

		do {
			if (bracketsIndex >= internTokenList.size()) {
				return null;
			}
			tempSearchToken = internTokenList.get(bracketsIndex);
			bracketsIndex++;

			switch (tempSearchToken.getInternTokenType()) {
				case BRACKET_OPEN:
					nestedBracketsCounter++;
					break;

				case BRACKET_CLOSE:
					nestedBracketsCounter--;
					break;
			}

			bracketInternTokenListToReturn.add(tempSearchToken);
		} while (tempSearchToken.getInternTokenType() != InternTokenType.BRACKET_CLOSE || nestedBracketsCounter != 0);

		return bracketInternTokenListToReturn;
	}

	public static List<InternToken> generateTokenListByBracketClose(List<InternToken> internTokenList,
			int internTokenListIndex) {

		if (internTokenListIndex == internTokenList.size()
				|| internTokenList.get(internTokenListIndex).getInternTokenType() != InternTokenType.BRACKET_CLOSE) {
			return null;
		}

		List<InternToken> bracketInternTokenListToReturn = new LinkedList<InternToken>();
		bracketInternTokenListToReturn.add(internTokenList.get(internTokenListIndex));

		int bracketSearchIndex = internTokenListIndex - 1;
		int nestedBracketsCounter = 1;
		InternToken tempSearchToken;

		do {
			if (bracketSearchIndex < 0) {
				return null;
			}
			tempSearchToken = internTokenList.get(bracketSearchIndex);
			bracketSearchIndex--;

			switch (tempSearchToken.getInternTokenType()) {
				case BRACKET_CLOSE:
					nestedBracketsCounter++;
					break;

				case BRACKET_OPEN:
					nestedBracketsCounter--;
					break;
			}

			bracketInternTokenListToReturn.add(tempSearchToken);
		} while (tempSearchToken.getInternTokenType() != InternTokenType.BRACKET_OPEN || nestedBracketsCounter != 0);

		Collections.reverse(bracketInternTokenListToReturn);
		return bracketInternTokenListToReturn;
	}

	public static List<List<InternToken>> getFunctionParameterInternTokensAsLists(
			List<InternToken> functionInternTokenList) {

		List<List<InternToken>> functionParameterInternTokenList = new LinkedList<List<InternToken>>();

		if (functionInternTokenList == null
				|| functionInternTokenList.size() < 4
				|| functionInternTokenList.get(0).getInternTokenType() != InternTokenType.FUNCTION_NAME
				|| functionInternTokenList.get(1).getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
			return null;
		}

		int searchIndex = 2;
		List<InternToken> currentParameterInternTokenList = new LinkedList<InternToken>();

		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		do {
			if (searchIndex >= functionInternTokenList.size()) {
				return null;
			}

			tempSearchToken = functionInternTokenList.get(searchIndex);

			switch (tempSearchToken.getInternTokenType()) {
				case FUNCTION_PARAMETERS_BRACKET_OPEN:
					nestedFunctionsCounter++;
					currentParameterInternTokenList.add(tempSearchToken);
					break;

				case FUNCTION_PARAMETERS_BRACKET_CLOSE:
					nestedFunctionsCounter--;
					if (nestedFunctionsCounter != 0) {
						currentParameterInternTokenList.add(tempSearchToken);
					}
					break;

				case FUNCTION_PARAMETER_DELIMITER:
					if (nestedFunctionsCounter == 1) {
						functionParameterInternTokenList.add(currentParameterInternTokenList);
						currentParameterInternTokenList = new LinkedList<InternToken>();
						break;
					}

				default:
					currentParameterInternTokenList.add(tempSearchToken);
					break;
			}

			searchIndex++;
		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
				|| nestedFunctionsCounter != 0);

		if (currentParameterInternTokenList.size() > 0) {
			functionParameterInternTokenList.add(currentParameterInternTokenList);
		}

		return functionParameterInternTokenList;
	}

	public static boolean isFunction(List<InternToken> internTokenList) {
		List<InternToken> functionList = getFunctionByName(internTokenList, 0);
		return !(functionList == null || functionList.size() != internTokenList.size());
	}

	private static InternTokenType getFirstInternTokenType(List<InternToken> internTokens) {
		if (internTokens == null || internTokens.size() == 0) {
			return null;
		}

		return internTokens.get(0).getInternTokenType();
	}

	public static boolean isPeriodToken(List<InternToken> internTokens) {

		if (internTokens == null || internTokens.size() != 1) {
			return false;
		}

		InternTokenType firstInternTokenType = internTokens.get(0).getInternTokenType();

		if (firstInternTokenType == InternTokenType.PERIOD) {
			return true;
		}

		return false;
	}

	public static boolean isFunctionToken(List<InternToken> internTokens) {
		InternTokenType firstInternTokenType = getFirstInternTokenType(internTokens);

		if (firstInternTokenType != null && firstInternTokenType == InternTokenType.FUNCTION_NAME) {
			return true;
		}

		return false;
	}

	public static boolean isNumberToken(List<InternToken> internTokens) {
		InternTokenType firstInternTokenType = getFirstInternTokenType(internTokens);

		if (firstInternTokenType != null && internTokens.size() <= 1 && firstInternTokenType == InternTokenType.NUMBER) {
			return true;
		}

		return false;
	}

	public static List<InternToken> replaceFunctionByTokens(List<InternToken> functionToReplace,
			List<InternToken> internTokensToReplaceWith) {

		if (isFunctionToken(internTokensToReplaceWith)) {
			return replaceFunctionButKeepParameters(functionToReplace, internTokensToReplaceWith);
		}

		return internTokensToReplaceWith;
	}

	public static List<InternToken> insertOperatorToNumberToken(InternToken numberTokenToBeModified, int externNumberOffset, InternToken operatorToInsert) {
		List<InternToken> replaceTokenList = new LinkedList<InternToken>();
		String numberString = numberTokenToBeModified.getTokenStringValue();
		String leftPart = numberString.substring(0, externNumberOffset);
		String rightPart = numberString.substring(externNumberOffset);

		InternToken leftNumber = new InternToken(InternTokenType.NUMBER, leftPart);
		replaceTokenList.add(leftNumber);

		replaceTokenList.add(operatorToInsert);

		InternToken rightNumber = new InternToken(InternTokenType.NUMBER, rightPart);
		replaceTokenList.add(rightNumber);

		return replaceTokenList;
	}

	public static InternToken insertIntoNumberToken(InternToken numberTokenToBeModified, int externNumberOffset,
			String numberToInsert) {
		String numberString = numberTokenToBeModified.getTokenStringValue();
		String leftPart = numberString.substring(0, externNumberOffset);
		String rightPart = numberString.substring(externNumberOffset);

		numberTokenToBeModified.setTokenStringValue(leftPart + numberToInsert + rightPart);

		return numberTokenToBeModified;
	}

	public static List<InternToken> replaceFunctionButKeepParameters(List<InternToken> functionToReplace,
			List<InternToken> functionToReplaceWith) {

		List<List<InternToken>> keepParameterInternTokenList = getFunctionParameterInternTokensAsLists(functionToReplace);
		List<InternToken> replacedParametersFunction = new LinkedList<InternToken>();
		List<List<InternToken>> originalParameterInternTokenList = getFunctionParameterInternTokensAsLists(functionToReplaceWith);

		if (functionToReplace == null || keepParameterInternTokenList == null
				|| originalParameterInternTokenList == null) {
			return functionToReplaceWith;
		}

		replacedParametersFunction.add(functionToReplaceWith.get(0));
		replacedParametersFunction.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));

		int functionParameterCount = getFunctionParameterCount(functionToReplaceWith);

		for (int index = 0; index < functionParameterCount; index++) {
			if (index < keepParameterInternTokenList.size() && keepParameterInternTokenList.get(index).size() > 0) {
				replacedParametersFunction.addAll(keepParameterInternTokenList.get(index));
			} else {
				replacedParametersFunction.addAll(originalParameterInternTokenList.get(index));
			}

			if (index < functionParameterCount - 1) {
				replacedParametersFunction.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));
			}
		}

		replacedParametersFunction.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		return replacedParametersFunction;
	}

	static int getFunctionParameterCount(List<InternToken> functionInternTokenList) {

		if (functionInternTokenList == null
				|| functionInternTokenList.size() < 4
				|| functionInternTokenList.get(0).getInternTokenType() != InternTokenType.FUNCTION_NAME
				|| functionInternTokenList.get(1).getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
			return 0;
		}

		int searchIndex = 2;

		InternToken tempSearchToken;
		int nestedFunctionsCounter = 1;

		int functionParameterCount = 1;
		do {
			if (searchIndex >= functionInternTokenList.size()) {
				return 0;
			}

			tempSearchToken = functionInternTokenList.get(searchIndex);

			switch (tempSearchToken.getInternTokenType()) {
				case FUNCTION_PARAMETERS_BRACKET_OPEN:
					nestedFunctionsCounter++;
					break;

				case FUNCTION_PARAMETERS_BRACKET_CLOSE:
					nestedFunctionsCounter--;
					break;

				case FUNCTION_PARAMETER_DELIMITER:
					if (nestedFunctionsCounter == 1) {
						functionParameterCount++;
					}
					break;
			}

			searchIndex++;
		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
				|| nestedFunctionsCounter != 0);
		return functionParameterCount;
	}

	public static InternToken deleteNumberByOffset(InternToken cursorPositionInternToken, int externNumberOffset) {

		String numberString = cursorPositionInternToken.getTokenStringValue();

		if (externNumberOffset < 1) {
			return cursorPositionInternToken;
		}

		if (externNumberOffset > numberString.length()) {
			externNumberOffset = numberString.length();
		}

		String leftPart = numberString.substring(0, externNumberOffset - 1);
		String rightPart = numberString.substring(externNumberOffset);

		cursorPositionInternToken.setTokenStringValue(leftPart + rightPart);

		if (cursorPositionInternToken.getTokenStringValue().isEmpty()) {
			return null;
		}

		return cursorPositionInternToken;
	}

	public static boolean applyBracketCorrection(List<InternToken> internFormula) throws EmptyStackException {

		Stack<InternTokenType> stack = new Stack<InternTokenType>();

		for (int index = 0; index < internFormula.size(); index++) {

			switch (internFormula.get(index).getInternTokenType()) {
				case BRACKET_OPEN:
					stack.push(InternTokenType.BRACKET_OPEN);
					break;

				case FUNCTION_PARAMETERS_BRACKET_OPEN:
					stack.push(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN);
					break;

				case BRACKET_CLOSE:
					if (stack.peek() == InternTokenType.BRACKET_OPEN) {
						stack.pop();
					} else {
						if (swapBrackets(internFormula, index, InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE)) {
							stack.pop();
							continue;
						}
						return false;
					}
					break;

				case FUNCTION_PARAMETERS_BRACKET_CLOSE:
					if (stack.peek() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
						stack.pop();
					} else {
						if (swapBrackets(internFormula, index, InternTokenType.BRACKET_CLOSE)) {
							stack.pop();
							continue;
						}
						return false;
					}
					break;
			}
		}
		return true;
	}

	private static boolean swapBrackets(List<InternToken> internFormula, int firstBracketIndex,
			InternTokenType secondBracket) {
		for (int index = firstBracketIndex + 1; index < internFormula.size(); index++) {
			if (internFormula.get(index).getInternTokenType() == secondBracket) {
				InternToken firstBracket = internFormula.get(firstBracketIndex);
				internFormula.set(firstBracketIndex, internFormula.get(index));
				internFormula.set(index, firstBracket);
				return true;
			}
		}
		return false;
	}
}
