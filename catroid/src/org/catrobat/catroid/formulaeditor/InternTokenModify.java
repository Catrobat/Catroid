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

import java.util.LinkedList;
import java.util.List;

public class InternTokenModify {

	public static List<InternToken> replaceFunctionByTokens(List<InternToken> functionToReplace,
			List<InternToken> internTokensToReplaceWith) {

		if (InternToken.isFunctionToken(internTokensToReplaceWith)) {

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

	private static List<InternToken> replaceFunctionButKeepParameters(List<InternToken> functionToReplace,
			List<InternToken> functionToReplaceWith) {

		List<List<InternToken>> keepParameterInternTokenList = InternTokenGroups
				.getFunctionParameterInternTokensAsLists(functionToReplace);
		List<InternToken> replacedParametersFunction = new LinkedList<InternToken>();
		List<List<InternToken>> originalParameterInternTokenList = InternTokenGroups
				.getFunctionParameterInternTokensAsLists(functionToReplaceWith);

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

	private static int getFunctionParameterCount(List<InternToken> functionInternTokenList) {

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
