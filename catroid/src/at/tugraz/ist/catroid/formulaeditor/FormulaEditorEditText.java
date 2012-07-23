package at.tugraz.ist.catroid.formulaeditor;

import android.content.Context;
import android.text.Editable;
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

public class FormulaEditorEditText extends EditText implements OnClickListener, OnTouchListener {

	//	private static final BackgroundColorSpan COLOR_EDITING = new BackgroundColorSpan(0xFF00FFFF);
	private static final BackgroundColorSpan COLOR_ERROR = new BackgroundColorSpan(0xFFF00000);
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);
	private static final BackgroundColorSpan COLOR_NORMAL = new BackgroundColorSpan(0xFFFFFFFF);

	public static final String[] GROUP_NUMBERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "." };
	public static final String[] GROUP_OPERATORS = new String[] { "+", "-", "*", "/", "^" };
	public static final String[] GROUP_FUNCTIONS = new String[] { "sin", "cos", "tan", "ln", "log", "pi", "sqrt", "e",
			"rand" };

	public static final int NUMBER = 0;
	public static final int OPERATOR = 1;
	public static final int FUNCTION_SEPERATOR = 2;
	public static final int FUNCTION = 3;
	public static final int BRACKET_CLOSE = 4;

	public CatKeyboardView catKeyboardView;
	private int currentlySelectedElementNumber = 0;
	private int selectionStartIndex = 0;
	private int selectionEndIndex = 0;

	private String currentlySelectedElement = null;
	private int currentlySelectedElementType = 0;
	private boolean editMode = false;
	private Spannable highlightSpan = null;
	private Spannable errorSpan = null;
	private boolean ignoreNextUpdate = false;
	private boolean hasChanges = false;

	//FormulaElement selectedElement;

	public FormulaEditorEditText(Context context) {
		super(context);
		init();
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	private void init() {
		this.setOnClickListener(this);
		this.setOnTouchListener(this);
		this.setLongClickable(false);
		this.setSelectAllOnFocus(false);
		this.setEnabled(false);
		//this.setBackgroundColor(getResources().getColor(R.color.transparent));
		this.setCursorVisible(true);

		//this.setBackgroundResource(0);
		//this.setCursorVisible(false);
		//this.setLines(5);
	}

	public void setFieldActive(String formulaAsText) {
		this.setEnabled(true);
		this.setText(formulaAsText);
		super.setSelection(formulaAsText.length());
		updateSelectionIndices();

	}

	//TODO: On doubleclick the text selection widget pops up... found no way to kill it
	public void trickTextSelectionThingy() {
		//this.setSelected(false);
	}

	public synchronized void updateSelectionIndices() {
		Log.i("info", "update selection");

		if (ignoreNextUpdate) {
			ignoreNextUpdate = false;
			return;
		}

		clearSelectionHighlighting();

		selectionStartIndex = getSelectionStart();
		selectionEndIndex = getSelectionEnd();
		setSelection(selectionStartIndex);
	}

	//highlight the selected word
	public synchronized void doSelectionAndHighlighting() {

		//TODO: Interpreter Test
		Log.i("info", "Formula Interpretation: deactivated"); // + formula.interpret());
		Log.i("info", "do Selection");

		String currentInput = this.getText().toString();
		int cursorPos = this.getSelectionStart();
		Log.i("info", "cursor: " + cursorPos);
		currentlySelectedElementNumber = 0;

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

	//What have we actually selected in the Formula? We might need to add items belonging to the FormulaElement
	private void checkSelectedTextType() {

		editMode = true;
		currentlySelectedElement = getText().subSequence(selectionStartIndex, selectionEndIndex).toString();
		checkAndSetSelectedType();
		Log.i("info", "FEEditText: check selected Type " + currentlySelectedElement + " "
				+ currentlySelectedElementType);
		if (currentlySelectedElementType == FUNCTION) {
			extendSelectionForBracketFromBegin();
			//TODO: extend selection across formula
		} else if (currentlySelectedElementType == BRACKET_CLOSE) {
			extendSelectionForBracketFromEnd();
			//TODO: extend selection across formula
		} else if (currentlySelectedElementType == FUNCTION_SEPERATOR) {
			extendSelectionForFunctionOnSeperator();
		} else {
			extendSelectionForNumber();
		}

		Log.i("info", "FEEditText: check selected Type " + selectionStartIndex + " " + selectionEndIndex);

	}

	public void selectOperator() {
		Log.i("info", "operatore ");
		if (selectionEndIndex + 1 >= getText().length()) {
			return;
		} else {
			selectionEndIndex++;
		}
	}

	public void extendSelectionForBracketFromBegin() {
		int bracketCount = 1;

		if (selectionEndIndex + 1 >= getText().length()) {
			return;
		}
		String text = getText().toString().substring(selectionEndIndex + 1);
		Log.i("info", "extendSelection for function " + text + " ");
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
		extendSelectionForBracketFromBegin();

	}

	public void extendSelectionForBracketFromEnd() {
		Log.i("info", "extendSelection for function from end bracket");
		int bracketCount = 1;
		String text = getText().toString().substring(0, selectionStartIndex);
		Log.i("info", "extendSelection for function from end bracket " + text);
		selectionStartIndex--;
		//int textLen = text.length();
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
			//			if ((text.charAt(selectionStartIndex - 1) == ' ' || text.charAt(selectionStartIndex - 1) == '(')) {
			//				break;
			//			}
			Log.i("info", "CHAR IS: " + text.charAt(selectionStartIndex - 1));
			if ((text.charAt(selectionStartIndex - 1) < 97) || (text.charAt(selectionStartIndex - 1) > 123)) {
				break;
			}
			selectionStartIndex--;
		}

	}

	public void extendSelectionForNumber() {
		Log.i("info", "extendSelection for a number");
		String currentInput = getText().toString();

		while (selectionStartIndex > 0) {
			Log.i("info", "CHAR IS: " + currentInput.charAt(selectionStartIndex - 1));
			if (!(((currentInput.charAt(selectionStartIndex - 1) >= 48) && (currentInput
					.charAt(selectionStartIndex - 1) <= 58)) || (currentInput.charAt(selectionStartIndex - 1) == '.'))) {
				break;
			}
			selectionStartIndex--;
		}

		while (selectionEndIndex < currentInput.length()) {
			Log.i("info", "CHAR IS: " + currentInput.charAt(selectionEndIndex));
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
		//highlightSpan.removeSpan(COLOR_EDITING);
	}

	public void highlightParseError(int firstError) {

		clearSelectionHighlighting();

		errorSpan = this.getText();
		errorSpan.removeSpan(COLOR_ERROR);

		if (errorSpan.length() <= firstError) {
			firstError--;
		}

		selectionStartIndex = firstError;
		selectionEndIndex = firstError + 1;

		errorSpan.setSpan(COLOR_ERROR, firstError, firstError + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

		Log.i("info", "Key pressed: " + catKey.getDisplayLabelString());
		Log.i("info",
				"KeyCode:" + catKey.getKeyCode() + " ScanCode:" + catKey.getScanCode() + " MetaState:"
						+ catKey.getMetaState() + " DisplayLabel:" + catKey.getDisplayLabel());

		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			deleteOneCharAtCurrentPosition();
		} else {
			appendToTextFieldAtCurrentPosition(newElement);
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

		currentlySelectedElementType = NUMBER;

		if (currentlySelectedElement.contains(",")) {
			currentlySelectedElementType = FUNCTION_SEPERATOR;
			return;

		} else if (currentlySelectedElement.contains(")")) {
			currentlySelectedElementType = BRACKET_CLOSE;
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
		Log.i("text", "SetSelection called " + index);
		super.setSelection(index);
	}

	@Override
	public void setSelection(int start, int end) {
		Log.i("text", "SetSelection 2 param called");
		//Standard selection cannot be used for our highlighting, would make it very unpracticable to write things >1 char
		//...maybe we could use standard selection when changing its behaviour in BaseInputConnection
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
	}

	@Override
	public void extendSelection(int index) {
		Log.i("info", "extendSelection");
		updateSelectionIndices();

	};

	public void onClick(View v) {
		updateSelectionIndices();

	}

	public boolean onTouch(View v, MotionEvent motion) {
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
			trickTextSelectionThingy();
			return true;
		}
	});

	@Override
	public boolean onCheckIsTextEditor() {
		return false;
	}
}
