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

import android.content.Context;
import android.util.Log;

import org.catrobat.catroid.R;

import java.util.LinkedList;
import java.util.List;

public class InternFormula {

	public static enum CursorTokenPosition {
		LEFT, MIDDLE, RIGHT
	}

	public static enum CursorTokenPropertiesAfterModification {
		LEFT, RIGHT, SELECT, DO_NOT_MODIFY
	}

	public static enum TokenSelectionType {
		USER_SELECTION, PARSER_ERROR_SELECTION
	}

	private ExternInternRepresentationMapping externInternRepresentationMapping;

	private List<InternToken> internTokenFormulaList;
	private String externFormulaString;

	private InternFormulaTokenSelection internFormulaTokenSelection;

	private int externCursorPosition;

	private InternToken cursorPositionInternToken;
	private int cursorPositionInternTokenIndex;
	private CursorTokenPosition cursorTokenPosition;

	private InternFormulaParser internTokenFormulaParser;

	public InternFormula(List<InternToken> internTokenList) {

		this.internTokenFormulaList = internTokenList;
		this.externFormulaString = null;
		this.externInternRepresentationMapping = new ExternInternRepresentationMapping();
		this.internFormulaTokenSelection = null;
		this.externCursorPosition = 0;
		this.cursorPositionInternTokenIndex = 0;
	}

	public InternFormula(List<InternToken> internTokenList, InternFormulaTokenSelection internFormulaTokenSelection,
			int externCursorPosition) {
		this.internTokenFormulaList = internTokenList;
		this.externFormulaString = null;
		externInternRepresentationMapping = new ExternInternRepresentationMapping();
		this.internFormulaTokenSelection = internFormulaTokenSelection;
		this.externCursorPosition = externCursorPosition;

		updateInternCursorPosition();
	}

	public void setCursorAndSelection(int externCursorPosition, boolean isSelected) {
		this.externCursorPosition = externCursorPosition;

		updateInternCursorPosition();
		internFormulaTokenSelection = null;

		if (isSelected
				|| externInternRepresentationMapping.getInternTokenByExternIndex(externCursorPosition) != ExternInternRepresentationMapping.MAPPING_NOT_FOUND
				&& (getFirstLeftInternToken(externCursorPosition - 1) == cursorPositionInternToken || cursorPositionInternToken
				.isFunctionParameterBracketOpen())
				&& ((cursorPositionInternToken.isFunctionName())
				|| (cursorPositionInternToken.isFunctionParameterBracketOpen() && cursorTokenPosition == CursorTokenPosition.LEFT)
				|| (cursorPositionInternToken.isSensor()) || (cursorPositionInternToken.isUserVariable())
				|| (cursorPositionInternToken.isUserList()) || (cursorPositionInternToken.isString()))) {
			selectCursorPositionInternToken(TokenSelectionType.USER_SELECTION);
		}
	}

	public void handleKeyInput(int resourceId, Context context, String name) {

		List<InternToken> keyInputInternTokenList = new InternFormulaKeyboardAdapter()
				.createInternTokenListByResourceId(resourceId, name);

		CursorTokenPropertiesAfterModification cursorTokenPropertiesAfterInput = CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		if (resourceId == R.id.formula_editor_edit_field_clear || resourceId == R.id.formula_editor_keyboard_delete) {

			cursorTokenPropertiesAfterInput = handleDeletion();
		} else if (isTokenSelected()) {

			cursorTokenPropertiesAfterInput = replaceSelection(keyInputInternTokenList);
		} else if (cursorTokenPosition == null) {

			cursorTokenPropertiesAfterInput = insertRightToCurrentToken(keyInputInternTokenList);
		} else {
			switch (cursorTokenPosition) {
				case LEFT:
					cursorTokenPropertiesAfterInput = insertLeftToCurrentToken(keyInputInternTokenList);
					break;
				case MIDDLE:
					cursorTokenPropertiesAfterInput = replaceCursorPositionInternTokenByTokenList(keyInputInternTokenList);
					break;
				case RIGHT:
					cursorTokenPropertiesAfterInput = insertRightToCurrentToken(keyInputInternTokenList);
					break;
			}
		}

		generateExternFormulaStringAndInternExternMapping(context);
		updateExternCursorPosition(cursorTokenPropertiesAfterInput);
		updateInternCursorPosition();
	}

	public void updateVariableReferences(String oldName, String newName, Context context) {
		for (InternToken internToken : internTokenFormulaList) {
			internToken.updateVariableReferences(oldName, newName);
		}
		generateExternFormulaStringAndInternExternMapping(context);
	}

	public void removeVariableReferences(String name, Context context) {
		LinkedList<InternToken> toRemove = new LinkedList<InternToken>();
		for (InternToken internToken : internTokenFormulaList) {
			if (internToken.isUserVariable(name)) {
				toRemove.add(internToken);
			}
		}
		for (InternToken internToken : toRemove) {
			internTokenFormulaList.remove(internToken);
		}
		generateExternFormulaStringAndInternExternMapping(context);
	}

	public void updateInternCursorPosition() {
		int cursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition);

		int leftCursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition - 1);

		int leftleftCursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition - 2);

		if (cursorPositionTokenIndex != ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
			if (leftCursorPositionTokenIndex != ExternInternRepresentationMapping.MAPPING_NOT_FOUND
					&& cursorPositionTokenIndex == leftCursorPositionTokenIndex) {
				cursorTokenPosition = CursorTokenPosition.MIDDLE;
			} else {
				cursorTokenPosition = CursorTokenPosition.LEFT;
			}
		} else if (leftCursorPositionTokenIndex != ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
			cursorTokenPosition = CursorTokenPosition.RIGHT;
		} else if (leftleftCursorPositionTokenIndex != ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
			cursorTokenPosition = CursorTokenPosition.RIGHT;
			leftCursorPositionTokenIndex = leftleftCursorPositionTokenIndex;
		} else {

			cursorTokenPosition = null;
			this.cursorPositionInternToken = null;
			return;
		}

		switch (cursorTokenPosition) {
			case LEFT:
				this.cursorPositionInternToken = internTokenFormulaList.get(cursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = cursorPositionTokenIndex;
				break;
			case MIDDLE:
				this.cursorPositionInternToken = internTokenFormulaList.get(cursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = cursorPositionTokenIndex;
				break;
			case RIGHT:
				this.cursorPositionInternToken = internTokenFormulaList.get(leftCursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = leftCursorPositionTokenIndex;
				break;
		}
	}

	private void updateExternCursorPosition(CursorTokenPropertiesAfterModification cursorTokenPropertiesAfterInput) {
		switch (cursorTokenPropertiesAfterInput) {
			case LEFT:
				setExternCursorPositionLeftTo(cursorPositionInternTokenIndex);
				break;
			case RIGHT:
				setExternCursorPositionRightTo(cursorPositionInternTokenIndex);
				break;
		}
	}

	private CursorTokenPropertiesAfterModification replaceSelection(List<InternToken> tokenListToInsert) {

		if (InternFormulaUtils.isPeriodToken(tokenListToInsert)) {
			tokenListToInsert = new LinkedList<InternToken>();
			tokenListToInsert.add(new InternToken(InternTokenType.NUMBER, "0."));
		}

		int internTokenSelectionStart = internFormulaTokenSelection.getStartIndex();
		int internTokenSelectionEnd = internFormulaTokenSelection.getEndIndex();

		if (internTokenSelectionStart > internTokenSelectionEnd || internTokenSelectionStart < 0
				|| internTokenSelectionEnd < 0) {

			internFormulaTokenSelection = null;
			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		List<InternToken> tokenListToRemove = new LinkedList<InternToken>();
		for (int tokensToRemove = 0; tokensToRemove <= internTokenSelectionEnd - internTokenSelectionStart; tokensToRemove++) {
			tokenListToRemove.add(internTokenFormulaList.get(internTokenSelectionStart + tokensToRemove));
		}

		if (InternFormulaUtils.isFunction(tokenListToRemove)) {
			cursorPositionInternToken = tokenListToRemove.get(0);
			cursorPositionInternTokenIndex = internTokenSelectionStart;
			return replaceCursorPositionInternTokenByTokenList(tokenListToInsert);
		} else {
			replaceInternTokens(tokenListToInsert, internTokenSelectionStart, internTokenSelectionEnd);

			return setCursorPositionAndSelectionAfterInput(internTokenSelectionStart);
		}
	}

	private void deleteInternTokens(int deleteIndexStart, int deleteIndexEnd) {
		List<InternToken> tokenListToInsert = new LinkedList<InternToken>();
		replaceInternTokens(tokenListToInsert, deleteIndexStart, deleteIndexEnd);
	}

	private void replaceInternTokens(List<InternToken> tokenListToInsert, int replaceIndexStart, int replaceIndexEnd) {
		for (int tokensToRemove = replaceIndexEnd - replaceIndexStart; tokensToRemove >= 0; tokensToRemove--) {
			internTokenFormulaList.remove(replaceIndexStart);
		}
		internTokenFormulaList.addAll(replaceIndexStart, tokenListToInsert);
	}

	private CursorTokenPropertiesAfterModification handleDeletion() {
		CursorTokenPropertiesAfterModification cursorTokenPropertiesAfterModification = CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		if (internFormulaTokenSelection != null) {
			deleteInternTokens(internFormulaTokenSelection.getStartIndex(), internFormulaTokenSelection.getEndIndex());

			cursorPositionInternTokenIndex = internFormulaTokenSelection.getStartIndex();
			cursorPositionInternToken = null;

			internFormulaTokenSelection = null;

			cursorTokenPropertiesAfterModification = CursorTokenPropertiesAfterModification.LEFT;
		} else {
			switch (cursorTokenPosition) {
				case LEFT:
					InternToken firstLeftInternToken = getFirstLeftInternToken(externCursorPosition - 1);
					if (firstLeftInternToken == null) {
						cursorTokenPropertiesAfterModification = CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
					} else {

						int firstLeftInternTokenIndex = internTokenFormulaList.indexOf(firstLeftInternToken);

						cursorTokenPropertiesAfterModification = deleteInternTokenByIndex(firstLeftInternTokenIndex);
					}
					break;

				case MIDDLE:
					cursorTokenPropertiesAfterModification = deleteInternTokenByIndex(cursorPositionInternTokenIndex);
					break;

				case RIGHT:
					cursorTokenPropertiesAfterModification = deleteInternTokenByIndex(cursorPositionInternTokenIndex);
					break;
			}
		}

		return cursorTokenPropertiesAfterModification;
	}

	private CursorTokenPropertiesAfterModification deleteInternTokenByIndex(int internTokenIndex) {

		InternToken tokenToDelete = internTokenFormulaList.get(internTokenIndex);

		switch (tokenToDelete.getInternTokenType()) {
			case NUMBER:
				int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(
						externCursorPosition, internTokenIndex);

				if (externNumberOffset == -1) {
					return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
				}

				InternToken modifiedToken = InternFormulaUtils.deleteNumberByOffset(tokenToDelete, externNumberOffset);

				if (modifiedToken == null) {
					internTokenFormulaList.remove(internTokenIndex);

					cursorPositionInternTokenIndex = internTokenIndex;
					cursorPositionInternToken = null;
					return CursorTokenPropertiesAfterModification.LEFT;
				}

				externCursorPosition--;
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

			case FUNCTION_NAME:
				List<InternToken> functionInternTokens = InternFormulaUtils.getFunctionByName(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
				}

				int lastListIndex = functionInternTokens.size() - 1;
				InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
				int endIndexToDelete = internTokenFormulaList.indexOf(lastFunctionToken);

				deleteInternTokens(internTokenIndex, endIndexToDelete);
				setExternCursorPositionLeftTo(internTokenIndex);

				cursorPositionInternTokenIndex = internTokenIndex;
				cursorPositionInternToken = null;

				return CursorTokenPropertiesAfterModification.LEFT;

			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				functionInternTokens = InternFormulaUtils.getFunctionByFunctionBracketOpen(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
				}

				int functionInternTokensLastIndex = functionInternTokens.size() - 1;

				int startDeletionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endIndexToDelete = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				deleteInternTokens(startDeletionIndex, endIndexToDelete);

				cursorPositionInternTokenIndex = startDeletionIndex;
				cursorPositionInternToken = null;
				return CursorTokenPropertiesAfterModification.LEFT;

			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				functionInternTokens = InternFormulaUtils.getFunctionByFunctionBracketClose(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startDeletionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endIndexToDelete = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				deleteInternTokens(startDeletionIndex, endIndexToDelete);

				cursorPositionInternTokenIndex = startDeletionIndex;
				cursorPositionInternToken = null;
				return CursorTokenPropertiesAfterModification.LEFT;

			case FUNCTION_PARAMETER_DELIMITER:
				functionInternTokens = InternFormulaUtils.getFunctionByParameterDelimiter(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startDeletionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endIndexToDelete = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				deleteInternTokens(startDeletionIndex, endIndexToDelete);

				cursorPositionInternTokenIndex = startDeletionIndex;
				cursorPositionInternToken = null;
				return CursorTokenPropertiesAfterModification.LEFT;

			default:
				deleteInternTokens(internTokenIndex, internTokenIndex);

				cursorPositionInternTokenIndex = internTokenIndex;
				cursorPositionInternToken = null;
				return CursorTokenPropertiesAfterModification.LEFT;
		}
	}

	private void setExternCursorPositionLeftTo(int internTokenIndex) {
		if (internTokenFormulaList.size() < 1) {
			externCursorPosition = 1;
			return;
		}
		if (internTokenIndex >= internTokenFormulaList.size()) {
			setExternCursorPositionRightTo(internTokenFormulaList.size() - 1);
			return;
		}

		int externTokenStartIndex = externInternRepresentationMapping.getExternTokenStartIndex(internTokenIndex);
		if (externTokenStartIndex == ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
			return;
		}

		externCursorPosition = externTokenStartIndex;
		cursorTokenPosition = CursorTokenPosition.LEFT;
	}

	private void setExternCursorPositionRightTo(int internTokenIndex) {

		if (internTokenFormulaList.size() < 1) {
			return;
		}
		if (internTokenIndex >= internTokenFormulaList.size()) {
			internTokenIndex = internTokenFormulaList.size() - 1;
		}

		int externTokenEndIndex = externInternRepresentationMapping.getExternTokenEndIndex(internTokenIndex);
		if (externTokenEndIndex == ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
			return;
		}

		externCursorPosition = externTokenEndIndex;
		cursorTokenPosition = CursorTokenPosition.RIGHT;
	}

	public void generateExternFormulaStringAndInternExternMapping(Context context) {
		InternToExternGenerator internToExternGenerator = new InternToExternGenerator(context);

		internToExternGenerator.generateExternStringAndMapping(internTokenFormulaList);
		externFormulaString = internToExternGenerator.getGeneratedExternFormulaString();
		externInternRepresentationMapping = internToExternGenerator.getGeneratedExternInternRepresentationMapping();
	}

	private void selectCursorPositionInternToken(TokenSelectionType internTokenSelectionType) {

		internFormulaTokenSelection = null;
		if (cursorPositionInternToken == null) {
			return;
		}

		switch (cursorPositionInternToken.getInternTokenType()) {
			case FUNCTION_NAME:
				List<InternToken> functionInternTokens = InternFormulaUtils.getFunctionByName(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				int lastListIndex = functionInternTokens.size() - 1;
				InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);

				int endSelectionIndex = internTokenFormulaList.indexOf(lastFunctionToken);

				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						cursorPositionInternTokenIndex, endSelectionIndex);
				break;

			case FUNCTION_PARAMETERS_BRACKET_OPEN:
				functionInternTokens = InternFormulaUtils.getFunctionByFunctionBracketOpen(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				int functionInternTokensLastIndex = functionInternTokens.size() - 1;

				int startSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						startSelectionIndex, endSelectionIndex);
				break;

			case FUNCTION_PARAMETERS_BRACKET_CLOSE:
				functionInternTokens = InternFormulaUtils.getFunctionByFunctionBracketClose(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						startSelectionIndex, endSelectionIndex);
				break;

			case FUNCTION_PARAMETER_DELIMITER:
				functionInternTokens = InternFormulaUtils.getFunctionByParameterDelimiter(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens == null || functionInternTokens.size() == 0) {
					return;
				}

				functionInternTokensLastIndex = functionInternTokens.size() - 1;

				startSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(functionInternTokens
						.get(functionInternTokensLastIndex));

				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						startSelectionIndex, endSelectionIndex);
				break;

			case BRACKET_OPEN:
				List<InternToken> bracketsInternTokens = InternFormulaUtils.generateTokenListByBracketOpen(
						internTokenFormulaList, cursorPositionInternTokenIndex);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				int bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startSelectionIndex = cursorPositionInternTokenIndex;
				endSelectionIndex = internTokenFormulaList.indexOf(bracketsInternTokens
						.get(bracketsInternTokensLastIndex));

				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						startSelectionIndex, endSelectionIndex);
				break;

			case BRACKET_CLOSE:
				bracketsInternTokens = InternFormulaUtils.generateTokenListByBracketClose(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (bracketsInternTokens == null || bracketsInternTokens.size() == 0) {
					return;
				}

				bracketsInternTokensLastIndex = bracketsInternTokens.size() - 1;

				startSelectionIndex = internTokenFormulaList.indexOf(bracketsInternTokens.get(0));
				endSelectionIndex = internTokenFormulaList.indexOf(bracketsInternTokens
						.get(bracketsInternTokensLastIndex));

				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						startSelectionIndex, endSelectionIndex);
				break;

			default:
				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						cursorPositionInternTokenIndex, cursorPositionInternTokenIndex);
				break;
		}
	}

	private CursorTokenPropertiesAfterModification insertLeftToCurrentToken(List<InternToken> internTokensToInsert) {

		InternToken firstLeftInternToken = null;
		if (cursorPositionInternTokenIndex > 0) {
			firstLeftInternToken = internTokenFormulaList.get(cursorPositionInternTokenIndex - 1);
		}

		if (cursorPositionInternToken.isNumber() && InternFormulaUtils.isNumberToken(internTokensToInsert)) {

			String numberToInsert = internTokensToInsert.get(0).getTokenStringValue();

			InternFormulaUtils.insertIntoNumberToken(cursorPositionInternToken, 0, numberToInsert);
			externCursorPosition++;

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		if (cursorPositionInternToken.isNumber() && InternFormulaUtils.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenStringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			InternFormulaUtils.insertIntoNumberToken(cursorPositionInternToken, 0, "0.");
			externCursorPosition += 2;

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		if (firstLeftInternToken != null && firstLeftInternToken.isNumber()
				&& InternFormulaUtils.isNumberToken(internTokensToInsert)) {

			firstLeftInternToken.appendToTokenStringValue(internTokensToInsert);

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		if (firstLeftInternToken != null && firstLeftInternToken.isNumber()
				&& InternFormulaUtils.isPeriodToken(internTokensToInsert)) {

			String numberString = firstLeftInternToken.getTokenStringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			firstLeftInternToken.appendToTokenStringValue(".");

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		if (InternFormulaUtils.isPeriodToken(internTokensToInsert)) {
			internTokenFormulaList.add(cursorPositionInternTokenIndex, new InternToken(InternTokenType.NUMBER, "0."));

			cursorPositionInternToken = null;
			return CursorTokenPropertiesAfterModification.RIGHT;
		}

		internTokenFormulaList.addAll(cursorPositionInternTokenIndex, internTokensToInsert);
		return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex);
	}

	private CursorTokenPropertiesAfterModification insertRightToCurrentToken(List<InternToken> internTokensToInsert) {

		if (cursorPositionInternToken == null) {

			if (InternFormulaUtils.isPeriodToken(internTokensToInsert)) {
				internTokensToInsert = new LinkedList<InternToken>();
				internTokensToInsert.add(new InternToken(InternTokenType.NUMBER, "0."));
			}
			internTokenFormulaList.addAll(0, internTokensToInsert);

			return setCursorPositionAndSelectionAfterInput(0);
		}

		if (cursorPositionInternToken.isNumber() && InternFormulaUtils.isNumberToken(internTokensToInsert)) {

			cursorPositionInternToken.appendToTokenStringValue(internTokensToInsert);

			return CursorTokenPropertiesAfterModification.RIGHT;
		}

		if (cursorPositionInternToken.isNumber() && InternFormulaUtils.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenStringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}
			cursorPositionInternToken.appendToTokenStringValue(".");

			return CursorTokenPropertiesAfterModification.RIGHT;
		}

		if (InternFormulaUtils.isPeriodToken(internTokensToInsert)) {

			internTokenFormulaList.add(cursorPositionInternTokenIndex + 1,
					new InternToken(InternTokenType.NUMBER, "0."));

			cursorPositionInternToken = null;
			cursorPositionInternTokenIndex = cursorPositionInternTokenIndex + 1;
			return CursorTokenPropertiesAfterModification.RIGHT;
		}

		internTokenFormulaList.addAll(cursorPositionInternTokenIndex + 1, internTokensToInsert);
		return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex + 1);
	}

	private CursorTokenPropertiesAfterModification setCursorPositionAndSelectionAfterInput(int insertedInternTokenIndex) {
		InternToken insertedInternToken = internTokenFormulaList.get(insertedInternTokenIndex);

		switch (insertedInternToken.getInternTokenType()) {
			case FUNCTION_NAME:
				List<InternToken> functionInternTokenList = InternFormulaUtils.getFunctionByName(
						internTokenFormulaList, insertedInternTokenIndex);

				if (functionInternTokenList.size() < 4) {
					cursorPositionInternTokenIndex = insertedInternTokenIndex + functionInternTokenList.size() - 1;
					cursorPositionInternToken = null;
					return CursorTokenPropertiesAfterModification.RIGHT;
				}

				List<List<InternToken>> functionParameters = InternFormulaUtils
						.getFunctionParameterInternTokensAsLists(functionInternTokenList);

				List<InternToken> functionFirstParameter = functionParameters.get(0);

				internFormulaTokenSelection = new InternFormulaTokenSelection(TokenSelectionType.USER_SELECTION,
						insertedInternTokenIndex + 2, insertedInternTokenIndex + functionFirstParameter.size() + 1);

				cursorPositionInternTokenIndex = internFormulaTokenSelection.getEndIndex();
				cursorPositionInternToken = null;
				return CursorTokenPropertiesAfterModification.RIGHT;

			default:
				cursorPositionInternTokenIndex = insertedInternTokenIndex;
				cursorPositionInternToken = null;
				internFormulaTokenSelection = null;
				return CursorTokenPropertiesAfterModification.RIGHT;
		}
	}

	private CursorTokenPropertiesAfterModification replaceCursorPositionInternTokenByTokenList(
			List<InternToken> internTokensToReplaceWith) {

		Log.i("info", "replaceCursorPositionInternTokenByTokenList:enter");

		if (cursorPositionInternToken.isNumber() && internTokensToReplaceWith.size() == 1
				&& internTokensToReplaceWith.get(0).isOperator()) {

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					cursorPositionInternTokenIndex);
			List<InternToken> replaceList = InternFormulaUtils.insertOperatorToNumberToken(cursorPositionInternToken, externNumberOffset, internTokensToReplaceWith.get(0));
			replaceInternTokens(replaceList, cursorPositionInternTokenIndex, cursorPositionInternTokenIndex);

			return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex);
		}

		if (cursorPositionInternToken.isNumber() && InternFormulaUtils.isNumberToken(internTokensToReplaceWith)) {

			InternToken numberTokenToInsert = internTokensToReplaceWith.get(0);

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					cursorPositionInternTokenIndex);

			if (externNumberOffset == -1) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			InternFormulaUtils.insertIntoNumberToken(cursorPositionInternToken, externNumberOffset,
					numberTokenToInsert.getTokenStringValue());

			externCursorPosition++;
			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		if (cursorPositionInternToken.isNumber() && InternFormulaUtils.isPeriodToken(internTokensToReplaceWith)) {

			String numberString = cursorPositionInternToken.getTokenStringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					cursorPositionInternTokenIndex);

			if (externNumberOffset == -1) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			InternFormulaUtils.insertIntoNumberToken(cursorPositionInternToken, externNumberOffset, ".");
			externCursorPosition++;

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		if (cursorPositionInternToken.isFunctionName()) {

			List<InternToken> functionInternTokens = InternFormulaUtils.getFunctionByName(internTokenFormulaList,
					cursorPositionInternTokenIndex);

			if (functionInternTokens == null) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			int lastListIndex = functionInternTokens.size() - 1;
			InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
			int endIndexToReplace = internTokenFormulaList.indexOf(lastFunctionToken);

			List<InternToken> tokensToInsert = InternFormulaUtils.replaceFunctionByTokens(functionInternTokens,
					internTokensToReplaceWith);

			replaceInternTokens(tokensToInsert, cursorPositionInternTokenIndex, endIndexToReplace);

			return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex);
		}

		if (InternFormulaUtils.isPeriodToken(internTokensToReplaceWith)) {
			internTokenFormulaList.add(cursorPositionInternTokenIndex + 1,
					new InternToken(InternTokenType.NUMBER, "0."));

			cursorPositionInternToken = null;
			cursorPositionInternTokenIndex = cursorPositionInternTokenIndex + 1;
			return CursorTokenPropertiesAfterModification.RIGHT;
		}

		replaceInternTokens(internTokensToReplaceWith, cursorPositionInternTokenIndex, cursorPositionInternTokenIndex);

		return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex);
	}

	public InternToken getFirstLeftInternToken(int externIndex) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex) != ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
				int internTokenIndex = externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex);
				return internTokenFormulaList.get(internTokenIndex);
			}
		}
		return null;
	}

	public int getExternCursorPosition() {
		return this.externCursorPosition;
	}

	public InternFormulaParser getInternFormulaParser() {
		internTokenFormulaParser = new InternFormulaParser(internTokenFormulaList);
		return internTokenFormulaParser;
	}

	public void selectParseErrorTokenAndSetCursor() {
		if (internTokenFormulaParser == null || internTokenFormulaList.size() == 0) {
			return;
		}

		int internErrorTokenIndex = internTokenFormulaParser.getErrorTokenIndex();

		if (internErrorTokenIndex < 0) {
			return;
		}

		if (internErrorTokenIndex >= internTokenFormulaList.size()) {
			internErrorTokenIndex = internTokenFormulaList.size() - 1;
		}

		setExternCursorPositionRightTo(internErrorTokenIndex);
		cursorPositionInternTokenIndex = internErrorTokenIndex;
		cursorPositionInternToken = internTokenFormulaList.get(cursorPositionInternTokenIndex);
		selectCursorPositionInternToken(TokenSelectionType.PARSER_ERROR_SELECTION);
	}

	public TokenSelectionType getExternSelectionType() {
		if (!isTokenSelected()) {
			return null;
		}

		return internFormulaTokenSelection.getTokenSelectionType();
	}

	public void selectWholeFormula() {

		if (internTokenFormulaList.size() == 0) {
			return;
		}

		internFormulaTokenSelection = new InternFormulaTokenSelection(TokenSelectionType.USER_SELECTION, 0,
				internTokenFormulaList.size() - 1);
	}

	public InternFormulaState getInternFormulaState() {

		List<InternToken> deepCopyOfInternTokenFormula = new LinkedList<InternToken>();
		InternFormulaTokenSelection deepCopyOfInternFormulaTokenSelection = null;

		for (InternToken tokenToCopy : internTokenFormulaList) {
			deepCopyOfInternTokenFormula.add(tokenToCopy.deepCopy());
		}

		if (isTokenSelected()) {
			deepCopyOfInternFormulaTokenSelection = internFormulaTokenSelection.deepCopy();
		}

		return new InternFormulaState(deepCopyOfInternTokenFormula, deepCopyOfInternFormulaTokenSelection,
				externCursorPosition);
	}

	public InternFormulaTokenSelection getSelection() {
		return internFormulaTokenSelection;
	}

	public int getExternSelectionStartIndex() {
		if (internFormulaTokenSelection == null) {
			return -1;
		}

		int externSelectionStartIndex = externInternRepresentationMapping
				.getExternTokenStartIndex(internFormulaTokenSelection.getStartIndex());

		if (externSelectionStartIndex == ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
			return -1;
		}

		return externSelectionStartIndex;
	}

	public int getExternSelectionEndIndex() {
		if (internFormulaTokenSelection == null) {
			return -1;
		}

		int externSelectionEndIndex = externInternRepresentationMapping
				.getExternTokenEndIndex(internFormulaTokenSelection.getEndIndex());

		if (externSelectionEndIndex == ExternInternRepresentationMapping.MAPPING_NOT_FOUND) {
			return -1;
		}

		return externSelectionEndIndex;
	}

	public String getExternFormulaString() {
		return externFormulaString;
	}

	private boolean isTokenSelected() {
		return internFormulaTokenSelection != null;
	}

	public boolean isThereSomethingToDelete() {
		if (internFormulaTokenSelection != null) {
			return true;
		}
		return !(cursorTokenPosition == null
				|| (cursorTokenPosition == CursorTokenPosition.LEFT && getFirstLeftInternToken(externCursorPosition - 1) == null));
	}
}
