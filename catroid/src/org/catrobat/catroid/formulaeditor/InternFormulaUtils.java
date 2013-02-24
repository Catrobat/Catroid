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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InternFormulaUtils {

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

			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter--;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter++;
			}
			functionInternTokenList.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| nestedFunctionsCounter != 0);

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

			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter--;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter++;
			}
			functionInternTokenList.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
				|| nestedFunctionsCounter != 0);

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

			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter++;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter--;
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

			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter++;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter--;
			}
			functionInternTokenList.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
				|| nestedFunctionsCounter != 0);

		return functionInternTokenList;

	}

	public static List<InternToken> generateTokenListByBracketOpen(List<InternToken> internTokenList,
			int internTokenListIndex) {

		if (internTokenListIndex == internTokenList.size()) {
			return null;
		}

		if (internTokenList.get(internTokenListIndex).getInternTokenType() != InternTokenType.BRACKET_OPEN) {
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

			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_OPEN) {
				nestedBracketsCounter++;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_CLOSE) {
				nestedBracketsCounter--;
			}
			bracketInternTokenListToReturn.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.BRACKET_CLOSE || nestedBracketsCounter != 0);

		return bracketInternTokenListToReturn;

	}

	public static List<InternToken> generateTokenListByBracketClose(List<InternToken> internTokenList,
			int internTokenListIndex) {

		if (internTokenListIndex == internTokenList.size()) {
			return null;
		}

		if (internTokenList.get(internTokenListIndex).getInternTokenType() != InternTokenType.BRACKET_CLOSE) {
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

			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_CLOSE) {
				nestedBracketsCounter++;
			}
			if (tempSearchToken.getInternTokenType() == InternTokenType.BRACKET_OPEN) {
				nestedBracketsCounter--;
			}
			bracketInternTokenListToReturn.add(tempSearchToken);

		} while (tempSearchToken.getInternTokenType() != InternTokenType.BRACKET_OPEN || nestedBracketsCounter != 0);

		Collections.reverse(bracketInternTokenListToReturn);
		return bracketInternTokenListToReturn;
	}

	public static List<List<InternToken>> getFunctionParameterInternTokensAsLists(
			List<InternToken> functionInternTokenList) {

		List<List<InternToken>> functionParameterInternTokenList = new LinkedList<List<InternToken>>();

		if (functionInternTokenList == null) {
			return null;
		}

		if (functionInternTokenList.size() < 4) {
			return null;
		}

		if (functionInternTokenList.get(0).getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}

		if (functionInternTokenList.get(1).getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
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

			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter++;
				currentParameterInternTokenList.add(tempSearchToken);
			} else if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter--;
				if (nestedFunctionsCounter != 0) {
					currentParameterInternTokenList.add(tempSearchToken);
				}
			} else if (nestedFunctionsCounter == 1
					&& tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETER_DELIMITER) {
				functionParameterInternTokenList.add(currentParameterInternTokenList);
				currentParameterInternTokenList = new LinkedList<InternToken>();
			} else {
				currentParameterInternTokenList.add(tempSearchToken);
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
		if (functionList == null) {
			return false;
		}
		if (functionList.size() != internTokenList.size()) {
			return false;
		}

		return true;
	}

	private static InternTokenType getFirstInternTokenType(List<InternToken> internTokens) {
		if (internTokens.size() == 0) {
			return null;
		}

		return internTokens.get(0).getInternTokenType();

	}

	public static boolean isPeriodToken(List<InternToken> internTokens) {

		if (internTokens.size() > 1) {
			return false;
		}

		InternTokenType firstInternTokenType = getFirstInternTokenType(internTokens);

		if (firstInternTokenType == null) {
			return false;
		}

		if (firstInternTokenType == InternTokenType.PERIOD) {
			return true;

		}

		return false;
	}

	public static boolean isFunctionToken(List<InternToken> internTokens) {
		InternTokenType firstInternTokenType = getFirstInternTokenType(internTokens);

		if (firstInternTokenType == null) {
			return false;
		}

		if (firstInternTokenType == InternTokenType.FUNCTION_NAME) {
			return true;

		}

		return false;
	}

	public static boolean isNumberToken(List<InternToken> internTokens) {

		if (internTokens.size() > 1) {
			return false;
		}

		InternTokenType firstInternTokenType = getFirstInternTokenType(internTokens);

		if (firstInternTokenType == null) {
			return false;
		}

		if (firstInternTokenType == InternTokenType.NUMBER) {
			return true;
		}

		return false;
	}

	public static List<InternToken> replaceFunctionByTokens(List<InternToken> functionToReplace,
			List<InternToken> internTokensToReplaceWith) {
	
		if (isFunctionToken(internTokensToReplaceWith)) {
	
			return replaceFunctionButKeepParameters(functionToReplace, internTokensToReplaceWith);
	
		} else {
	
			return internTokensToReplaceWith;
		}
	}

	public static InternToken insertIntoNumberToken(InternToken numberTokenToBeModified, int externNumberOffset,
			String numberToInsert) {
		String numberString = numberTokenToBeModified.getTokenSringValue();
		String leftPart = numberString.substring(0, externNumberOffset);
		String rightPart = numberString.substring(externNumberOffset);
	
		numberTokenToBeModified.setTokenStringValue(leftPart + numberToInsert + rightPart);
	
		return numberTokenToBeModified;
	
	}

	static List<InternToken> replaceFunctionButKeepParameters(List<InternToken> functionToReplace,
			List<InternToken> functionToReplaceWith) {
	
		List<List<InternToken>> keepParameterInternTokenList = getFunctionParameterInternTokensAsLists(functionToReplace);
		List<InternToken> replacedParametersFunction = new LinkedList<InternToken>();
		List<List<InternToken>> originalParameterInternTokenList = getFunctionParameterInternTokensAsLists(functionToReplaceWith);
	
		if (functionToReplace == null || keepParameterInternTokenList == null
				|| originalParameterInternTokenList == null) {
			return functionToReplaceWith;
		}
	
		if (functionToReplace.size() < 4 || functionToReplaceWith.size() < 4) {
			return functionToReplaceWith;
		}
	
		if (functionToReplace.get(0).getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return null;
		}
	
		if (functionToReplace.get(1).getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
			return null;
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
	
		if (functionInternTokenList == null) {
			return 0;
		}
	
		if (functionInternTokenList.size() < 4) {
			return 0;
		}
	
		if (functionInternTokenList.get(0).getInternTokenType() != InternTokenType.FUNCTION_NAME) {
			return 0;
		}
	
		if (functionInternTokenList.get(1).getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
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
	
			if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
				nestedFunctionsCounter++;
			} else if (tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE) {
				nestedFunctionsCounter--;
			} else if (nestedFunctionsCounter == 1
					&& tempSearchToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETER_DELIMITER) {
	
				functionParameterCount++;
	
			}
			searchIndex++;
	
		} while (tempSearchToken.getInternTokenType() != InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
				|| nestedFunctionsCounter != 0);
		return functionParameterCount;
	}

	public static InternToken deleteNumberByOffset(InternToken cursorPositionInternToken, int externNumberOffset) {
	
		String numberString = cursorPositionInternToken.getTokenSringValue();
	
		if (externNumberOffset < 1) {
			return cursorPositionInternToken;
		}
	
		if (externNumberOffset > numberString.length()) {
			externNumberOffset = numberString.length();
		}
	
		String leftPart = numberString.substring(0, externNumberOffset - 1);
		String rightPart = numberString.substring(externNumberOffset);
	
		cursorPositionInternToken.setTokenStringValue(leftPart + rightPart);
	
		if (cursorPositionInternToken.getTokenSringValue().length() == 0) {
			return null;
		}
	
		return cursorPositionInternToken;
	}

}
