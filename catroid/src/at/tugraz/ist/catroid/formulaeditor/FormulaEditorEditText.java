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

package at.tugraz.ist.catroid.formulaeditor;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class FormulaEditorEditText extends EditText implements OnClickListener, OnTouchListener {

	//	private static final BackgroundColorSpan COLOR_EDITING = new BackgroundColorSpan(0xFF00FFFF);
	private static final BackgroundColorSpan COLOR_ERROR = new BackgroundColorSpan(0xFFF00000);
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);
	private static final BackgroundColorSpan COLOR_NORMAL = new BackgroundColorSpan(0xFFFFFFFF);

	public static final String[] GROUP_NUMBERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "." };
	public static final String[] GROUP_OPERATORS = new String[] { "+", "-", "*", "/", "^" };
	public static final String[] GROUP_FUNCTIONS = new String[] { "sin", "cos", "tan", "ln", "log", "pi", "sqrt", "e",
			"rand", "abs", "round" };

	public static final int NUMBER = 0;
	public static final int OPERATOR = 1;
	public static final int FUNCTION_SEPERATOR = 2;
	public static final int FUNCTION = 3;
	public static final int BRACKET_CLOSE = 4;
	public static final int BRACKET_OPEN = 5;

	public CatKeyboardView catKeyboardView;
	private int selectionStartIndex = 0;
	private int selectionEndIndex = 0;

	private String currentlySelectedElement = null;
	private int currentlySelectedElementType = 0;
	private boolean editMode = false;
	private Spannable highlightSpan = null;
	private Spannable errorSpan = null;
	private boolean ignoreNextUpdate = false;
	private boolean hasChanges = false;

	FormulaEditorDialog dialog = null;

	public FormulaEditorEditText(Context context) {
		super(context);
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(FormulaEditorDialog dialog, int brickHeight, CatKeyboardView ckv) {
		this.dialog = dialog;
		this.setOnClickListener(this);
		this.setOnTouchListener(this);
		this.setLongClickable(false);
		this.setSelectAllOnFocus(false);
		this.setEnabled(false);
		//this.setBackgroundColor(getResources().getColor(R.color.transparent));
		this.catKeyboardView = ckv;
		//this.setCursorVisible(true);
		//		int inType = getInputType(); // backup the input type
		//		setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); // disable soft input
		//		this.onTouchEvent(motion); // call native handler
		//		this.setInputType(inType); // restore input type
		this.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		if (brickHeight < 100) { //this height seems buggy for some high bricks, still need it...
			this.setLines(7);
		} else if (brickHeight < 200) {
			this.setLines(6);
		} else {
			this.setLines(4);
		}

		//this.setBackgroundResource(0);
		//this.setCursorVisible(false);
		//
	}

	public void setFieldActive(String formulaAsText) {
		this.setEnabled(true);
		this.setText(formulaAsText);
		super.setSelection(formulaAsText.length());
		updateSelectionIndices();

		//this.setCursorVisible(true);

	}

	public synchronized void updateSelectionIndices() {
		//Log.i("info", "update selection indices");

		if (ignoreNextUpdate) {
			ignoreNextUpdate = false;
			return;
		}

		clearSelectionHighlighting();
		editMode = false;

		selectionStartIndex = getSelectionStart();
		selectionEndIndex = getSelectionEnd();
		setSelection(selectionStartIndex);
	}

	public synchronized void doSelectionAndHighlighting() {
		//Log.i("info", "do Selection and highlighting, cursor position: " + cursor pos);
		String currentInput = this.getText().toString();
		int cursorPos = this.getSelectionStart();

		if (currentInput.length() == 0) {
			return;
		}

		selectionStartIndex = cursorPos;
		selectionEndIndex = cursorPos;

		while (selectionStartIndex > 0) {
			//this reads: (char is not 'a'...'z' or 'A'...'Z' or '_'), which is the naming convention for our variables/sensors
			if (((currentInput.charAt(selectionStartIndex - 1) < 97) || (currentInput.charAt(selectionStartIndex - 1) > 123))
					&& ((currentInput.charAt(selectionStartIndex - 1) < 65) || (currentInput
							.charAt(selectionStartIndex - 1) > 91))
					&& (currentInput.charAt(selectionStartIndex - 1) != '_')) {
				if ((currentInput.charAt(selectionStartIndex - 1) == '(')
						|| (currentInput.charAt(selectionStartIndex - 1) == ',')) {
					selectionStartIndex--;
				}
				break;
			}
			selectionStartIndex--;
		}

		while (selectionEndIndex < currentInput.length()) {
			if (((currentInput.charAt(selectionEndIndex) < 97) || (currentInput.charAt(selectionEndIndex) > 123))
					&& ((currentInput.charAt(selectionEndIndex) < 65) || (currentInput.charAt(selectionEndIndex) > 91))
					&& (currentInput.charAt(selectionEndIndex) != '_')) {
				if ((currentInput.charAt(selectionEndIndex) == ')') || (currentInput.charAt(selectionEndIndex) == ',')) {
					selectionEndIndex++;
				}
				break;
			}
			selectionEndIndex++;
		}

		checkSelectedTextType();
		highlightSelection();

	}

	private void checkSelectedTextType() {

		editMode = true;
		currentlySelectedElement = getText().subSequence(selectionStartIndex, selectionEndIndex).toString();
		checkAndSetSelectedType();
		//Log.i("info", "FEEditText: check selected Type " + currentlySelectedElement + " "
		//		+ currentlySelectedElementType);
		if (currentlySelectedElementType == FUNCTION) {
			extendSelectionForFunction();
			//TODO: extend selection across formula
		} else if (currentlySelectedElementType == BRACKET_CLOSE) {
			extendSelectionForBracketFromEnd();
			//TODO: extend selection across formula
		} else if (currentlySelectedElementType == BRACKET_OPEN) {
			extendSelectionForBracketFromBegin();
			//TODO: extend selection across formula
		} else if (currentlySelectedElementType == FUNCTION_SEPERATOR) {
			extendSelectionForFunctionOnSeperator();
		} else {
			extendSelectionForNumber();
		}

		//Log.i("info", "FEEditText: check selected Type " + selectionStartIndex + " " + selectionEndIndex);

	}

	//	public void selectOperator() {
	//		Log.i("info", "operator ");
	//		if (selectionEndIndex + 1 >= getText().length()) {
	//			return;
	//		} else {
	//			selectionEndIndex++;
	//		}
	//	}

	public void extendSelectionForFunction() {
		int bracketCount = 1;

		if (selectionEndIndex + 1 >= getText().length()) {
			return;
		}
		String text = getText().toString().substring(selectionEndIndex + 1);
		//Log.i("info", "extendSelection for function " + text + " ");
		int textLen = text.length();
		int i = 0;
		while (i < textLen && bracketCount > 0) {
			if (text.charAt(i) == '(') {
				bracketCount++;
			} else if (text.charAt(i) == ')') {
				bracketCount--;
			}
			selectionEndIndex++;
			i++;
		}

		//if (selectionEndIndex < textLen && text.charAt(i - 1) == ')') {
		selectionEndIndex++;

	}

	public void extendSelectionForFunctionOnSeperator() {
		extendSelectionForBracketFromEnd();
		extendSelectionForFunction();

	}

	public void extendSelectionForBracketFromEnd() {
		//Log.i("info", "extendSelection from close bracket");
		int bracketCount = 1;
		String text = getText().toString().substring(0, selectionStartIndex);
		//Log.i("info", "extendSelection for function from end bracket " + text);
		selectionStartIndex--;

		while (selectionStartIndex > 0) {
			if (text.charAt(selectionStartIndex) == '(') {
				bracketCount--;
			} else if (text.charAt(selectionStartIndex) == ')') {
				bracketCount++;
			}
			if (bracketCount == 0) {
				break;
			}

			selectionStartIndex--;
		}

		while (selectionStartIndex > 0) {
			//Log.i("info", "CHAR IS: " + text.charAt(selectionStartIndex - 1));
			if ((text.charAt(selectionStartIndex - 1) < 97) || (text.charAt(selectionStartIndex - 1) > 123)) {
				break;
			}
			selectionStartIndex--;
		}

	}

	public void extendSelectionForBracketFromBegin() {
		//Log.i("info", "extendSelection from begin bracket");
		extendSelectionForFunction();
		Editable text = getText();

		while (selectionStartIndex > 0) {

			//Log.i("info", "CHAR IS: " + text.charAt(selectionStartIndex - 1));
			if ((text.charAt(selectionStartIndex - 1) < 97) || (text.charAt(selectionStartIndex - 1) > 123)) {
				break;
			}
			selectionStartIndex--;
		}

	}

	public void extendSelectionForNumber() {
		//Log.i("info", "extendSelection for a number");
		String currentInput = getText().toString();

		while (selectionStartIndex > 0) {
			//Log.i("info", "CHAR IS: " + currentInput.charAt(selectionStartIndex - 1));
			if (!(((currentInput.charAt(selectionStartIndex - 1) >= 48) && (currentInput
					.charAt(selectionStartIndex - 1) <= 58)) || (currentInput.charAt(selectionStartIndex - 1) == '.'))) {
				break;
			}
			selectionStartIndex--;
		}

		while (selectionEndIndex < currentInput.length()) {
			if (!(((currentInput.charAt(selectionEndIndex) >= 48) && (currentInput.charAt(selectionEndIndex) <= 58)) || (currentInput
					.charAt(selectionEndIndex) == '.'))) {
				break;
			}
			selectionEndIndex++;
		}

	}

	//	public void extendSelection(int left, int right) {
	//		Log.i("info", "extendSelection" + left + " " + right);
	//		String currentInput = getText().toString();
	//		while (right > 0) {
	//			selectionEndIndex = currentInput.indexOf(ELEMENT_SEPERATOR, selectionEndIndex + 1);
	//			right--;
	//			if (selectionEndIndex == -1) {
	//				selectionEndIndex = currentInput.length();
	//			}
	//		}
	//		left = currentlySelectedElementNumber - 1 - left;
	//		int newSelectionStart = 0;
	//		while (left > 0) {
	//			newSelectionStart = currentInput.indexOf(ELEMENT_SEPERATOR, newSelectionStart);
	//			left--;
	//			newSelectionStart++;
	//			if (newSelectionStart == -1) {
	//				newSelectionStart = 0;
	//				break;
	//			}
	//		}
	//		selectionStartIndex = newSelectionStart;
	//
	//	}

	public void highlightSelection() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		//highlightSpan.removeSpan(COLOR_EDITING);

		if (selectionStartIndex < 0) {
			selectionStartIndex = 0;
		}

		if (selectionEndIndex == selectionStartIndex) {
			return;
		}

		highlightSpan.setSpan(COLOR_HIGHLIGHT, selectionStartIndex, selectionEndIndex,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		//		previousSelectionStartIndex = selectionStartIndex;
		//		previousSelectionEndIndex = selectionEndIndex;
	}

	public void clearSelectionHighlighting() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		highlightSpan.removeSpan(COLOR_ERROR);
	}

	public void highlightParseError(int firstError) {

		clearSelectionHighlighting();
		errorSpan = this.getText();
		//Log.i("info", "" + firstError);

		if (errorSpan.length() <= firstError) {
			firstError--;
		}

		selectionStartIndex = firstError;
		selectionEndIndex = firstError + 1;
		setSelection(firstError);

		String text = getText().toString();
		//error at start of function or variable/constant
		if (!(((text.charAt(firstError) < 97) || (text.charAt(firstError) > 123))
				&& ((text.charAt(firstError) < 65) || (text.charAt(firstError) > 91)) && (text.charAt(firstError) != '_'))) {
			doSelectionAndHighlighting();
			//selectionEndIndex++;
		} else if (((text.charAt(firstError) >= 48) && (text.charAt(firstError) <= 58))) {
			doSelectionAndHighlighting();
		}

		editMode = true;

		//		if (selectionEndIndex > getText().length()) {
		//			selectionEndIndex = getText().length;
		//		}

		errorSpan.setSpan(COLOR_ERROR, selectionStartIndex, selectionEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	//	public void highlightSelectionCurrentlyEditing() {
	//		Spannable str = this.getText();
	//
	//		str.setSpan(COLOR_EDITING, selectionStartIndex, selectionEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	//	}

	public void checkAndModifyKeyInput(CatKeyEvent catKey) {
		hasChanges = true;
		String newElement = null;
		if (catKey.getKeyCode() == CatKeyEvent.KEYCODE_COMMA) {
			newElement = ".";
		} else {
			newElement = "" + catKey.getDisplayLabelString();
		}

		clearSelectionHighlighting();

		String text = getText().toString();
		int cursor = this.getSelectionStart();
		if (cursor > 0
				&& cursor < text.length()
				&& !editMode
				&& ((((text.charAt(cursor) >= 97) && (text.charAt(cursor) <= 123))
						|| ((text.charAt(cursor) >= 65) && (text.charAt(cursor) <= 91)) || (text.charAt(cursor) == '_')))
				&& (!(((text.charAt(cursor - 1) < 97) || (text.charAt(cursor - 1) > 123))
						&& ((text.charAt(cursor - 1) < 65) || (text.charAt(cursor - 1) > 91)) && (text
						.charAt(cursor - 1) != '_')))) {
			doSelectionAndHighlighting();
			editMode = true;
			return;
		}

		//		Log.i("info", "Key pressed: " + catKey.getDisplayLabelString());
		//		Log.i("info",
		//				"KeyCode:" + catKey.getKeyCode() + " ScanCode:" + catKey.getScanCode() + " MetaState:"
		//						+ catKey.getMetaState() + " DisplayLabel:" + catKey.getDisplayLabel());

		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			deleteOneCharAtCurrentPosition();
		} else {
			appendToTextFieldAtCurrentPosition(newElement);
		}

		if (getText().length() == 0) {
			dialog.hideOkayButton();
		} else {
			dialog.showOkayButton();
		}

	}

	public void deleteOneCharAtCurrentPosition() {
		Editable text = getText();

		if (selectionEndIndex < 1) {
			return;
		}

		if (editMode) {
			text.replace(selectionStartIndex, selectionEndIndex, "");
			selectionEndIndex = selectionStartIndex;
			editMode = false;
		} else {
			if (text.charAt(selectionEndIndex - 1) == ',') {
				super.setSelection(selectionEndIndex - 1, selectionEndIndex);
				doSelectionAndHighlighting();
				return;
			} else if (text.charAt(selectionEndIndex - 1) == ')') {
				super.setSelection(selectionEndIndex - 1, selectionEndIndex);
				doSelectionAndHighlighting();
				return;
			} else if (text.charAt(selectionEndIndex - 1) == '(') {
				super.setSelection(selectionEndIndex - 1, selectionEndIndex);
				doSelectionAndHighlighting();
				return;
			} else if (((text.charAt(selectionEndIndex - 1) >= 97) && (text.charAt(selectionEndIndex - 1) <= 123))
					|| ((text.charAt(selectionEndIndex - 1) >= 65) && (text.charAt(selectionEndIndex - 1) <= 91))
					|| (text.charAt(selectionEndIndex - 1) == '_')) {
				super.setSelection(selectionEndIndex - 1, selectionEndIndex);
				doSelectionAndHighlighting();
				return;
			}
			text.replace(selectionEndIndex - 1, selectionEndIndex, "");
			selectionEndIndex--;
			selectionStartIndex = selectionEndIndex;
		}

		setText(text);
		super.setSelection(selectionEndIndex);
	}

	private void appendToTextFieldAtCurrentPosition(String newElement) {
		Editable text = getText();
		if (newElement.equals("null")) { //Spacebar!
			newElement = " ";
		}

		if (editMode) {
			text.replace(selectionStartIndex, selectionEndIndex, newElement);
			selectionEndIndex = selectionStartIndex + newElement.length();

			editMode = false;
		} else {

			text.insert(selectionEndIndex, newElement);
			selectionEndIndex += newElement.length();
		}

		setText(text);
		setSelection(selectionEndIndex);
	}

	public boolean getEditMode() {
		return editMode;
	}

	public void checkAndSetSelectedType() {
		Log.i("info", currentlySelectedElement + " start: " + selectionStartIndex + " end: " + selectionEndIndex);
		currentlySelectedElementType = NUMBER;

		if (currentlySelectedElement.contains(",")) {
			currentlySelectedElementType = FUNCTION_SEPERATOR;
			return;

		} else if (currentlySelectedElement.contains(")")) {
			currentlySelectedElementType = BRACKET_CLOSE;
			return;
		} else if (currentlySelectedElement.contains("(")) {
			currentlySelectedElementType = BRACKET_OPEN;
			return;
		}

		for (String item : GROUP_OPERATORS) {
			if (currentlySelectedElement.contains(item)) {
				currentlySelectedElementType = OPERATOR;
				return;
			}
		}
		for (String item : GROUP_FUNCTIONS) {
			if (currentlySelectedElement.startsWith(item)) {
				currentlySelectedElementType = FUNCTION;
				return;
			}
		}

	}

	public boolean hasChanges() {
		return hasChanges;
	}

	public void formulaSaved() {
		hasChanges = false;
		errorSpan = this.getText();
		errorSpan.removeSpan(COLOR_ERROR);
	}

	@Override
	public void setSelection(int index) {
		//Log.i("text", "SetSelection called " + index);
		super.setSelection(index);
	}

	@Override
	public void setSelection(int start, int end) {
		//Standard selection cannot be used for our highlighting, would make it very unpracticable to write things >1 char
		//...maybe we could use standard selection when changing its behaviour in BaseInputConnection
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
	}

	@Override
	public void extendSelection(int index) {
		//Log.i("info", "extendSelection");
		updateSelectionIndices();

	};

	public void onClick(View v) {
		updateSelectionIndices();

	}

	public boolean onTouch(View v, MotionEvent motion) {

		//		int inType = getInputType(); // backup the input type
		//		setInputType(InputType.TYPE_NULL); // disable soft input
		//		this.onTouchEvent(motion); // call native handler
		//		this.setInputType(inType); // restore input type

		if (motion.getAction() == android.view.MotionEvent.ACTION_DOWN) {
			//updateSelectionIndices();
		} else if (motion.getAction() == android.view.MotionEvent.ACTION_UP) {
			//Log.i("info", "Act up");
			//updateSelectionIndices();
		}

		gestureDetector.onTouchEvent(motion);
		return false;
	}

	final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			ignoreNextUpdate = true;
			doSelectionAndHighlighting();
			return true;
		}
	});

	@Override
	public boolean onCheckIsTextEditor() {
		return false;
	}
}
