/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.formulaeditor;

import org.catrobat.catroid.formulaeditor.InternFormula.TokenSelectionType;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class FormulaEditorEditText extends EditText implements OnTouchListener {

	private static final BackgroundColorSpan COLOR_ERROR = new BackgroundColorSpan(0xFFF00000);
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);

	private int absoluteCursorPosition = 0;
	private InternFormula internFormula;

	private Spannable highlightSpan = null;
	private float lineHeight = 0;

	public LinearLayout catKeyboardView;
	private static FormulaEditorHistory history = null;
	private Context context;

	FormulaEditorFragment formulaEditorDialog = null;
	private boolean doNotMoveCursorOnTab = false;

	public FormulaEditorEditText(Context context) {
		super(context);
		this.context = context;
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void init(FormulaEditorFragment dialog, int brickHeight, LinearLayout ckv) {
		this.formulaEditorDialog = dialog;
		this.setOnTouchListener(this);
		this.setLongClickable(false);
		this.setSelectAllOnFocus(false);
		this.catKeyboardView = ckv;
		this.setCursorVisible(false);
	}

	public void enterNewFormula(InternFormulaState internFormulaState) {

		internFormula = internFormulaState.createInternFormulaFromState();
		internFormula.generateExternFormulaStringAndInternExternMapping(context);

		updateTextAndCursorFromInternFormula();

		internFormula.selectWholeFormula();
		highlightSelection();

		if (history == null) {
			history = new FormulaEditorHistory(internFormula.getInternFormulaState());
		} else {
			history.init(internFormula.getInternFormulaState());
		}
	}

	public boolean restoreFieldFromPreviousHistory() {
		InternFormulaState currentState = history.getCurrentState();
		if (currentState != null) {

			internFormula = currentState.createInternFormulaFromState();
			internFormula.generateExternFormulaStringAndInternExternMapping(context);
			updateTextAndCursorFromInternFormula();
		}

		formulaEditorDialog.refreshFormulaPreviewString();

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		absoluteCursorPosition = absoluteCursorPosition > getText().length() ? getText().length()
				: absoluteCursorPosition;

		Layout layout = getLayout();
		if (layout != null) {
			lineHeight = getTextSize() + 5;

			int line = layout.getLineForOffset(absoluteCursorPosition);
			int baseline = layout.getLineBaseline(line);
			int ascent = layout.getLineAscent(line);

			float xCoordinate = layout.getPrimaryHorizontal(absoluteCursorPosition);
			float startYCoordinate = baseline + ascent;
			float endYCoordinate = baseline + ascent + lineHeight;

			canvas.drawLine(xCoordinate, startYCoordinate, xCoordinate, endYCoordinate, getPaint());
		}
	}

	public void highlightSelection() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		highlightSpan.removeSpan(COLOR_ERROR);

		int selectionStartIndex = internFormula.getExternSelectionStartIndex();
		int selectionEndIndex = internFormula.getExternSelectionEndIndex();
		TokenSelectionType selectionType = internFormula.getExternSelectionType();

		Log.i("info", "highlightSelection: start=" + selectionStartIndex + "  end=" + selectionEndIndex);

		if (selectionStartIndex == -1 || selectionEndIndex == -1 || selectionEndIndex == selectionStartIndex
				|| selectionEndIndex > highlightSpan.length()) {
			return;
		}

		if (selectionType == TokenSelectionType.USER_SELECTION) {
			highlightSpan.setSpan(COLOR_HIGHLIGHT, selectionStartIndex, selectionEndIndex,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			highlightSpan.setSpan(COLOR_ERROR, selectionStartIndex, selectionEndIndex,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public void clearSelectionHighlighting() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		highlightSpan.removeSpan(COLOR_ERROR);
	}

	public void setParseErrorCursorAndSelection() {

		internFormula.selectParseErrorTokenAndSetCursor();
		highlightSelection();
		setSelection(absoluteCursorPosition);

	}

	public void handleKeyEvent(int resource, String userVariableName) {

		internFormula.handleKeyInput(resource, context, userVariableName);
		history.push(internFormula.getInternFormulaState());
		updateTextAndCursorFromInternFormula();
		setSelection(absoluteCursorPosition);
		formulaEditorDialog.refreshFormulaPreviewString();
	}

	public boolean hasChanges() {
		return history == null ? false : history.hasUnsavedChanges();
	}

	public void formulaSaved() {
		history.changesSaved();
	}

	public void endEdit() {
		history.clear();
	}

	public void quickSelect() {
		internFormula.selectWholeFormula();
		highlightSelection();
	}

	public boolean undo() {
		if (!history.undoIsPossible()) {
			return false;
		}
		InternFormulaState lastStep = history.backward();
		if (lastStep != null) {

			internFormula = lastStep.createInternFormulaFromState();
			internFormula.generateExternFormulaStringAndInternExternMapping(context);
			updateTextAndCursorFromInternFormula();
		}

		formulaEditorDialog.refreshFormulaPreviewString();
		return true;
	}

	public boolean redo() {
		if (!history.redoIsPossible()) {
			return false;
		}
		InternFormulaState nextStep = history.forward();
		if (nextStep != null) {

			internFormula = nextStep.createInternFormulaFromState();
			internFormula.generateExternFormulaStringAndInternExternMapping(context);
			updateTextAndCursorFromInternFormula();
		}
		formulaEditorDialog.refreshFormulaPreviewString();
		return true;
	}

	@Override
	public void setSelection(int index) {
		//This is only used to get the scrollbar to the right position easily
		super.setSelection(index);
	}

	private void updateTextAndCursorFromInternFormula() {
		String newExternFormulaString = internFormula.getExternFormulaString();
		setText(newExternFormulaString);
		absoluteCursorPosition = internFormula.getExternCursorPosition();
		if (absoluteCursorPosition > getText().length()) {
			absoluteCursorPosition = getText().length();
		}

		highlightSelection();
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// dont want it!
	}

	@Override
	public boolean onTouch(View v, MotionEvent motion) {
		return gestureDetector.onTouchEvent(motion);
	}

	final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onDoubleTap(MotionEvent e) {

			internFormula.setCursorAndSelection(absoluteCursorPosition, true);
			history.updateCurrentSelection(internFormula.getSelection());
			highlightSelection();

			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent motion) {
			Layout layout = getLayout();
			if (layout != null) {

				lineHeight = getTextSize() + 5;
				int yCoordinate = (int) motion.getY();
				int cursorY = 0;
				int cursorXOffset = (int) motion.getX();
				int initialScrollY = getScrollY();
				int firstLineSize = (int) (initialScrollY % lineHeight);
				int numberOfVisbleLines = (int) (getHeight() / lineHeight);

				if (yCoordinate <= lineHeight - firstLineSize) {

					scrollBy(0, (int) (initialScrollY > lineHeight ? -1 * (firstLineSize + lineHeight / 2) : -1
							* firstLineSize));
					cursorY = 0;
				} else if (yCoordinate >= numberOfVisbleLines * lineHeight - lineHeight / 2) {
					if (!(yCoordinate > layout.getLineCount() * lineHeight - getScrollY())) {
						scrollBy(0, (int) (lineHeight - firstLineSize + lineHeight / 2));
						cursorY = numberOfVisbleLines;
					}
				} else {
					for (int i = 1; i <= numberOfVisbleLines; i++) {
						if (yCoordinate <= ((lineHeight - firstLineSize) + i * lineHeight)) {
							cursorY = i;
							break;
						}
					}
				}

				int linesDown = (int) (initialScrollY / lineHeight);

				while (cursorY + linesDown >= layout.getLineCount()) {
					linesDown--;
				}

				int tempCursorPosition = layout.getOffsetForHorizontal(cursorY + linesDown, cursorXOffset);

				if (tempCursorPosition > getText().length()) {
					tempCursorPosition = getText().length();
				}

				if (isDoNotMoveCursorOnTab() == false) {
					absoluteCursorPosition = tempCursorPosition;
				}
				absoluteCursorPosition = absoluteCursorPosition > getText().length() ? getText().length()
						: absoluteCursorPosition;
				setSelection(absoluteCursorPosition);
				postInvalidate();

				InternToken internToken = internFormula.getFirstLeftInternToken(absoluteCursorPosition);

				if (internToken != null) {
					InternTokenType internTokenType = internToken.getInternTokenType();

					if ((internFormula.getFirstLeftInternToken(absoluteCursorPosition - 1) == internToken)
							&& ((internTokenType == InternTokenType.FUNCTION_NAME)
									|| (internTokenType == InternTokenType.SENSOR)
									|| (internTokenType == InternTokenType.USER_VARIABLE) || (internTokenType == InternTokenType.LOOK))) {
						internFormula.setCursorAndSelection(absoluteCursorPosition, true);
					} else {
						internFormula.setCursorAndSelection(absoluteCursorPosition, false);
					}

					highlightSelection();

					history.updateCurrentSelection(internFormula.getSelection());
				}
				formulaEditorDialog.refreshFormulaPreviewString();
			}
			return true;

		}

	});

	@Override
	public boolean onCheckIsTextEditor() {
		return false;
	}

	public InternFormulaParser getFormulaParser() {
		return internFormula.getInternFormulaParser();
	}

	public int getAbsoluteCursorPosition() {
		return absoluteCursorPosition;
	}

	public boolean isDoNotMoveCursorOnTab() {
		return doNotMoveCursorOnTab;
	}

	public void setDoNotMoveCursorOnTab(boolean doNotMoveCursorOnTab) {
		this.doNotMoveCursorOnTab = doNotMoveCursorOnTab;
	}
}
