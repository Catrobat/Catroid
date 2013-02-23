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

import org.catrobat.catroid.R;

import android.content.Context;
import android.util.Log;

public class InternFormula {

	public static enum CursorTokenPosition {
		LEFT, MIDDLE, RIGHT;
	};

	public static enum CursorTokenPropertiesAfterModification {
		LEFT, RIGHT, SELECT, DO_NOT_MODIFY;
	}

	public static enum TokenSelectionType {
		USER_SELECTION, PARSER_ERROR_SELECTION;
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

	public synchronized void setCursorAndSelection(int externCursorPosition, boolean tokenIsSelected) {
		this.externCursorPosition = externCursorPosition;

		updateInternCursorPosition();

		if (tokenIsSelected) {
			selectCursorPositionInternToken(TokenSelectionType.USER_SELECTION);
		} else {
			internFormulaTokenSelection = null;
		}

	}

	public int getExternSelectionStartIndex() {
		if (internFormulaTokenSelection == null) {
			return -1;
		}

		Integer externSelectionStartIndex = externInternRepresentationMapping
				.getExternTokenStartIndex(internFormulaTokenSelection.getStartIndex());

		if (externSelectionStartIndex == null) {
			return -1;
		}

		return externSelectionStartIndex;
	}

	public int getExternSelectionEndIndex() {
		if (internFormulaTokenSelection == null) {
			return -1;
		}

		Integer externSelectionEndIndex = externInternRepresentationMapping
				.getExternTokenEndIndex(internFormulaTokenSelection.getEndIndex());

		if (externSelectionEndIndex == null) {
			return -1;
		}

		return externSelectionEndIndex;
	}

	public String getExternFormulaString() {
		return externFormulaString;
	}

	private boolean isTokenSelected() {
		if (internFormulaTokenSelection == null) {
			return false;
		} else {
			return true;
		}
	}

	public synchronized void handleKeyInput(int resId, Context context, String userVariableName) {
		Log.i("info", "handleKeyInput:enter");

		List<InternToken> catKeyEventTokenList = new InternFormulaHelper().createInternTokensByCatKeyEvent(resId,
				userVariableName);

		CursorTokenPropertiesAfterModification cursorTokenPropertiesAfterInput = CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		if (resId == R.id.formula_editor_keyboard_delete) {

			cursorTokenPropertiesAfterInput = handleDeletion();

		} else if (isTokenSelected()) {

			cursorTokenPropertiesAfterInput = replaceSelection(catKeyEventTokenList);

		} else if (cursorTokenPosition == null) {

			cursorTokenPropertiesAfterInput = appendToFirstLeftToken(catKeyEventTokenList);

		} else if (cursorTokenPosition == CursorTokenPosition.LEFT) {

			cursorTokenPropertiesAfterInput = insertLeftToCurrentToken(catKeyEventTokenList);

		} else if (cursorTokenPosition == CursorTokenPosition.MIDDLE) {

			cursorTokenPropertiesAfterInput = replaceCursorPositionInternTokenByTokenList(catKeyEventTokenList);

		} else if (cursorTokenPosition == CursorTokenPosition.RIGHT) {

			cursorTokenPropertiesAfterInput = insertRightToCurrentToken(catKeyEventTokenList);

		} else {

			//			appendToFirstLeftToken(catKeyEventTokenList);

		}

		generateExternFormulaStringAndInternExternMapping(context);
		updateExternCursorPosition(cursorTokenPropertiesAfterInput);
		updateInternCursorPosition();

	}

	private void updateInternCursorPosition() {
		Integer cursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition);

		Integer leftCursorPositionTokenIndex = externInternRepresentationMapping
				.getInternTokenByExternIndex(externCursorPosition - 1);

		if (cursorPositionTokenIndex != null) {
			if (leftCursorPositionTokenIndex != null) {
				if (cursorPositionTokenIndex.equals(leftCursorPositionTokenIndex)) {
					cursorTokenPosition = CursorTokenPosition.MIDDLE;
				} else {
					cursorTokenPosition = CursorTokenPosition.LEFT;
				}
			} else {
				cursorTokenPosition = CursorTokenPosition.LEFT;
			}
		} else if (leftCursorPositionTokenIndex != null) {
			cursorTokenPosition = CursorTokenPosition.RIGHT;

		} else {
			cursorTokenPosition = null;
			this.cursorPositionInternToken = null;
			return;
		}

		switch (cursorTokenPosition) {
			case LEFT:
				this.cursorPositionInternToken = internTokenFormulaList.get(cursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = cursorPositionTokenIndex;
				Log.i("info", "LEFT of " + cursorPositionInternToken.getTokenSringValue());
				break;
			case MIDDLE:
				this.cursorPositionInternToken = internTokenFormulaList.get(cursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = cursorPositionTokenIndex;
				Log.i("info", "SELECTED " + cursorPositionInternToken.getTokenSringValue());
				break;
			case RIGHT:
				this.cursorPositionInternToken = internTokenFormulaList.get(leftCursorPositionTokenIndex);
				this.cursorPositionInternTokenIndex = leftCursorPositionTokenIndex;
				Log.i("info", "RIGHT of " + cursorPositionInternToken.getTokenSringValue());
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
			case SELECT:

				break;
		}

		if (externCursorPosition < 0) {
			externCursorPosition = 0;
		}
	}

	private CursorTokenPropertiesAfterModification replaceSelection(List<InternToken> tokenListToInsert) {

		if (InternToken.isPeriodToken(tokenListToInsert)) {
			tokenListToInsert = new LinkedList<InternToken>();
			tokenListToInsert.add(new InternToken(InternTokenType.NUMBER, "0."));
		}

		int internTokenSelectionStart = internFormulaTokenSelection.getStartIndex();
		int internTokenSelectionEnd = internFormulaTokenSelection.getEndIndex();

		if (internTokenSelectionStart > internTokenSelectionEnd || internTokenSelectionStart < 0
				|| internTokenSelectionEnd < 0) {
			internFormulaTokenSelection = null;

			return setCursorPositionAndSelectionAfterInput(internTokenSelectionStart);
		}

		List<InternToken> tokenListToRemove = new LinkedList<InternToken>();
		for (int tokensToRemove = 0; tokensToRemove <= internTokenSelectionEnd - internTokenSelectionStart; tokensToRemove++) {
			tokenListToRemove.add(internTokenFormulaList.get(internTokenSelectionStart + tokensToRemove));
		}

		if (InternTokenGroups.isFunction(tokenListToRemove)) {
			cursorPositionInternToken = tokenListToRemove.get(0);
			cursorPositionInternTokenIndex = internTokenSelectionStart;
			return replaceCursorPositionInternTokenByTokenList(tokenListToInsert);

		} else {
			replaceInternTokens(tokenListToInsert, internTokenSelectionStart, internTokenSelectionEnd);

			internFormulaTokenSelection = null;

			return setCursorPositionAndSelectionAfterInput(internTokenSelectionStart);
		}

	}

	private void deleteInternTokens(int deleteIndexStart, int deleteIndexEnd) {
		List<InternToken> tokenListToInsert = new LinkedList<InternToken>();
		replaceInternTokens(tokenListToInsert, deleteIndexStart, deleteIndexEnd);
	}

	private void replaceInternTokens(List<InternToken> tokenListToInsert, int replaceIndexStart, int replaceIndexEnd) {
		if (replaceIndexStart > replaceIndexEnd || replaceIndexStart < 0 || replaceIndexEnd < 0) {
			return;
		}

		List<InternToken> tokenListToRemove = new LinkedList<InternToken>();
		for (int tokensToRemove = replaceIndexEnd - replaceIndexStart; tokensToRemove >= 0; tokensToRemove--) {
			tokenListToRemove.add(internTokenFormulaList.remove(replaceIndexStart));
		}

		internTokenFormulaList.addAll(replaceIndexStart, tokenListToInsert);

	}

	private CursorTokenPropertiesAfterModification handleDeletion() {
		if (internFormulaTokenSelection != null) {
			deleteInternTokens(internFormulaTokenSelection.getStartIndex(), internFormulaTokenSelection.getEndIndex());

			cursorPositionInternTokenIndex = internFormulaTokenSelection.getStartIndex();
			cursorPositionInternToken = null;

			internFormulaTokenSelection = null;

			return CursorTokenPropertiesAfterModification.LEFT;

		} else if (cursorTokenPosition == null) {

			InternToken firstLeftInternToken = getFirstLeftInternToken(externCursorPosition);

			if (firstLeftInternToken == null) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			int firstLeftInternTokenIndex = internTokenFormulaList.indexOf(firstLeftInternToken);
			return deleteInternTokenByIndex(firstLeftInternTokenIndex);

		} else if (cursorTokenPosition == CursorTokenPosition.LEFT) {

			InternToken firstLeftInternToken = getFirstLeftInternToken(externCursorPosition - 1);

			if (firstLeftInternToken == null) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			int firstLeftInternTokenIndex = internTokenFormulaList.indexOf(firstLeftInternToken);

			return deleteInternTokenByIndex(firstLeftInternTokenIndex);

		} else if (cursorTokenPosition == CursorTokenPosition.MIDDLE) {

			return deleteInternTokenByIndex(cursorPositionInternTokenIndex);

		} else if (cursorTokenPosition == CursorTokenPosition.RIGHT) {

			return deleteInternTokenByIndex(cursorPositionInternTokenIndex);

		}

		return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

	}

	private CursorTokenPropertiesAfterModification deleteInternTokenByIndex(int internTokenIndex) {

		InternToken tokenToDelete = internTokenFormulaList.get(internTokenIndex);

		switch (tokenToDelete.getInternTokenType()) {
			case NUMBER:
				int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(
						externCursorPosition, internTokenIndex);

				Log.i("info", "Delete number offset = " + externNumberOffset);

				if (externNumberOffset == -1) {
					return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
				}

				InternToken modifiedToken = InternTokenModify.deleteNumberByOffset(tokenToDelete, externNumberOffset);

				if (modifiedToken == null) {
					internTokenFormulaList.remove(internTokenIndex);

					cursorPositionInternTokenIndex = internTokenIndex;
					cursorPositionInternToken = null;
					return CursorTokenPropertiesAfterModification.LEFT;
				}

				externCursorPosition--;
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

			case FUNCTION_NAME:
				List<InternToken> functionInternTokens = InternTokenGroups.getFunctionByName(internTokenFormulaList,
						internTokenIndex);

				if (functionInternTokens.size() == 0) {
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
				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketOpen(internTokenFormulaList,
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
				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketClose(internTokenFormulaList,
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
				functionInternTokens = InternTokenGroups.getFunctionByParameterDelimiter(internTokenFormulaList,
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

		Integer externTokenStartIndex = externInternRepresentationMapping.getExternTokenStartIndex(internTokenIndex);
		if (externTokenStartIndex == null) {
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

		Integer externTokenEndIndex = externInternRepresentationMapping.getExternTokenEndIndex(internTokenIndex);
		if (externTokenEndIndex == null) {
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
				List<InternToken> functionInternTokens = InternTokenGroups.getFunctionByName(internTokenFormulaList,
						cursorPositionInternTokenIndex);

				if (functionInternTokens.size() == 0) {
					return;
				}

				int lastListIndex = functionInternTokens.size() - 1;
				InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);

				int endSelectionIndex = internTokenFormulaList.indexOf(lastFunctionToken);

				internFormulaTokenSelection = new InternFormulaTokenSelection(internTokenSelectionType,
						cursorPositionInternTokenIndex, endSelectionIndex);

				break;
			case FUNCTION_PARAMETERS_BRACKET_OPEN:

				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketOpen(internTokenFormulaList,
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
				functionInternTokens = InternTokenGroups.getFunctionByFunctionBracketClose(internTokenFormulaList,
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
				functionInternTokens = InternTokenGroups.getFunctionByParameterDelimiter(internTokenFormulaList,
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
				List<InternToken> bracketsInternTokens = InternTokenGroups.generateTokenListByBracketOpen(
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

				bracketsInternTokens = InternTokenGroups.generateTokenListByBracketClose(internTokenFormulaList,
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
		Log.i("info", "insertLeftToCurrentToken:enter");

		InternToken firstLeftInternToken = null;
		if (cursorPositionInternTokenIndex > 0) {
			firstLeftInternToken = internTokenFormulaList.get(cursorPositionInternTokenIndex - 1);
		}

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToInsert)) {

			String numberToInsert = internTokensToInsert.get(0).getTokenSringValue();

			InternTokenModify.insertIntoNumberToken(cursorPositionInternToken, 0, numberToInsert);
			externCursorPosition++;

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenSringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			InternTokenModify.insertIntoNumberToken(cursorPositionInternToken, 0, "0.");
			externCursorPosition += 2;

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (firstLeftInternToken != null && firstLeftInternToken.isNumber()
				&& InternToken.isNumberToken(internTokensToInsert)) {

			firstLeftInternToken.appendToTokenStringValue(internTokensToInsert);

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (firstLeftInternToken != null && firstLeftInternToken.isNumber()
				&& InternToken.isPeriodToken(internTokensToInsert)) {

			String numberString = firstLeftInternToken.getTokenSringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			firstLeftInternToken.appendToTokenStringValue(".");

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (cursorPositionInternToken.getInternTokenType() == InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN) {
			return replaceCursorPositionInternTokenByTokenList(internTokensToInsert);
		} else if (InternToken.isPeriodToken(internTokensToInsert)) {
			internTokenFormulaList.add(cursorPositionInternTokenIndex, new InternToken(InternTokenType.NUMBER, "0."));

			cursorPositionInternToken = null;
			return CursorTokenPropertiesAfterModification.RIGHT;
		} else {

			internTokenFormulaList.addAll(cursorPositionInternTokenIndex, internTokensToInsert);

			return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex);
		}
	}

	private CursorTokenPropertiesAfterModification insertRightToCurrentToken(List<InternToken> internTokensToInsert) {
		Log.i("info", "insertRightToCurrentToken:enter");

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToInsert)) {

			cursorPositionInternToken.appendToTokenStringValue(internTokensToInsert);

			return CursorTokenPropertiesAfterModification.RIGHT;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToInsert)) {
			String numberString = cursorPositionInternToken.getTokenSringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}
			cursorPositionInternToken.appendToTokenStringValue(".");

			return CursorTokenPropertiesAfterModification.RIGHT;

		} else if (InternToken.isPeriodToken(internTokensToInsert)) {

			internTokenFormulaList.add(cursorPositionInternTokenIndex + 1,
					new InternToken(InternTokenType.NUMBER, "0."));

			cursorPositionInternToken = null;
			cursorPositionInternTokenIndex = cursorPositionInternTokenIndex + 1;
			return CursorTokenPropertiesAfterModification.RIGHT;
		} else {

			internTokenFormulaList.addAll(cursorPositionInternTokenIndex + 1, internTokensToInsert);

			return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex + 1);
		}
	}

	private CursorTokenPropertiesAfterModification appendToFirstLeftToken(List<InternToken> internTokensToAppend) {

		Log.i("info", "appendToFirstLeftToken:enter");

		InternToken firstLeftToken = getFirstLeftInternToken(externCursorPosition);
		int firstLeftTokenListIndex = internTokenFormulaList.indexOf(firstLeftToken);

		if (firstLeftToken == null) {

			if (InternToken.isPeriodToken(internTokensToAppend)) {
				internTokensToAppend = new LinkedList<InternToken>();
				internTokensToAppend.add(new InternToken(InternTokenType.NUMBER, "0."));
			}
			internTokenFormulaList.addAll(0, internTokensToAppend);

			return setCursorPositionAndSelectionAfterInput(0);

		} else if (firstLeftToken.isNumber() && InternToken.isNumberToken(internTokensToAppend)) {

			firstLeftToken.appendToTokenStringValue(internTokensToAppend);

			externCursorPosition++;
			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (firstLeftToken.isNumber() && InternToken.isPeriodToken(internTokensToAppend)) {
			String numberString = firstLeftToken.getTokenSringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}
			firstLeftToken.appendToTokenStringValue(".");

			externCursorPosition++;
			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (InternToken.isPeriodToken(internTokensToAppend)) {
			internTokenFormulaList.add(firstLeftTokenListIndex + 1, new InternToken(InternTokenType.NUMBER, "0."));

			cursorPositionInternToken = null;
			cursorPositionInternTokenIndex = firstLeftTokenListIndex + 1;
			return CursorTokenPropertiesAfterModification.RIGHT;
		} else {

			internTokenFormulaList.addAll(firstLeftTokenListIndex + 1, internTokensToAppend);

			return setCursorPositionAndSelectionAfterInput(firstLeftTokenListIndex + 1);
		}

	}

	private CursorTokenPropertiesAfterModification setCursorPositionAndSelectionAfterInput(int insertedInternTokenIndex) {
		InternToken insertedInternToken = internTokenFormulaList.get(insertedInternTokenIndex);
		if (insertedInternToken == null) {
			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
		}

		switch (insertedInternToken.getInternTokenType()) {
			case FUNCTION_NAME:
				List<InternToken> functionInternTokenList = InternTokenGroups.getFunctionByName(internTokenFormulaList,
						insertedInternTokenIndex);

				if (functionInternTokenList.size() < 4) {
					cursorPositionInternTokenIndex = insertedInternTokenIndex + functionInternTokenList.size() - 1;
					cursorPositionInternToken = null;
					return CursorTokenPropertiesAfterModification.RIGHT;
				}

				List<List<InternToken>> functionParameters = InternTokenGroups
						.getFunctionParameterInternTokensAsLists(functionInternTokenList);

				if (functionParameters.size() < 1) {
					return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
				}

				List<InternToken> functionFirstParameter = functionParameters.get(0);

				if (functionFirstParameter.size() == 0) {
					cursorPositionInternTokenIndex = insertedInternTokenIndex + 1;
					cursorPositionInternToken = null;
					return CursorTokenPropertiesAfterModification.RIGHT;
				}

				internFormulaTokenSelection = new InternFormulaTokenSelection(TokenSelectionType.USER_SELECTION,
						insertedInternTokenIndex + 2, insertedInternTokenIndex + functionFirstParameter.size() + 1);

				cursorPositionInternTokenIndex = internFormulaTokenSelection.getEndIndex();
				cursorPositionInternToken = null;
				return CursorTokenPropertiesAfterModification.RIGHT;

			default:
				cursorPositionInternTokenIndex = insertedInternTokenIndex;
				cursorPositionInternToken = null;
				return CursorTokenPropertiesAfterModification.RIGHT;
		}
	}

	private CursorTokenPropertiesAfterModification replaceCursorPositionInternTokenByTokenList(
			List<InternToken> internTokensToReplaceWith) {

		Log.i("info", "replaceCursorPositionInternTokenByTokenList:enter");

		if (cursorPositionInternToken.isNumber() && InternToken.isNumberToken(internTokensToReplaceWith)) {

			InternToken numberTokenToInsert = internTokensToReplaceWith.get(0);

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					cursorPositionInternTokenIndex);

			if (externNumberOffset == -1) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			InternTokenModify.insertIntoNumberToken(cursorPositionInternToken, externNumberOffset,
					numberTokenToInsert.getTokenSringValue());

			externCursorPosition++;
			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (cursorPositionInternToken.isNumber() && InternToken.isPeriodToken(internTokensToReplaceWith)) {

			String numberString = cursorPositionInternToken.getTokenSringValue();
			if (numberString.contains(".")) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			int externNumberOffset = externInternRepresentationMapping.getExternTokenStartOffset(externCursorPosition,
					cursorPositionInternTokenIndex);

			if (externNumberOffset == -1) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			InternTokenModify.insertIntoNumberToken(cursorPositionInternToken, externNumberOffset, ".");
			externCursorPosition++;

			return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;

		} else if (cursorPositionInternToken.isFunctionName()) {

			List<InternToken> functionInternTokens = InternTokenGroups.getFunctionByName(internTokenFormulaList,
					cursorPositionInternTokenIndex);

			if (functionInternTokens.size() == 0) {
				return CursorTokenPropertiesAfterModification.DO_NOT_MODIFY;
			}

			int lastListIndex = functionInternTokens.size() - 1;
			InternToken lastFunctionToken = functionInternTokens.get(lastListIndex);
			int endIndexToReplace = internTokenFormulaList.indexOf(lastFunctionToken);

			List<InternToken> tokensToInsert = InternTokenModify.replaceFunctionByTokens(functionInternTokens,
					internTokensToReplaceWith);

			replaceInternTokens(tokensToInsert, cursorPositionInternTokenIndex, endIndexToReplace);

			return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex);

		} else if (InternToken.isPeriodToken(internTokensToReplaceWith)) {
			internTokenFormulaList.add(cursorPositionInternTokenIndex + 1,
					new InternToken(InternTokenType.NUMBER, "0."));

			cursorPositionInternToken = null;
			cursorPositionInternTokenIndex = cursorPositionInternTokenIndex + 1;
			return CursorTokenPropertiesAfterModification.RIGHT;
		} else {

			replaceInternTokens(internTokensToReplaceWith, cursorPositionInternTokenIndex,
					cursorPositionInternTokenIndex);

			return setCursorPositionAndSelectionAfterInput(cursorPositionInternTokenIndex);
		}

	}

	public InternToken getFirstLeftInternToken(int externIndex) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex) != null) {
				int internTokenIndex = externInternRepresentationMapping.getInternTokenByExternIndex(searchIndex);
				InternToken internTokenToReturn = internTokenFormulaList.get(internTokenIndex);
				return internTokenToReturn;
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
		if (internTokenFormulaParser == null) {
			return;
		}

		if (internTokenFormulaList.size() == 0) {
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

}
