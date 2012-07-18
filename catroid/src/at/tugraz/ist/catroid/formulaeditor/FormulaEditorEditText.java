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
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class FormulaEditorEditText extends EditText implements OnClickListener, OnTouchListener {

	private static final BackgroundColorSpan COLOR_EDITING = new BackgroundColorSpan(0xFF00FFFF);
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);
	private static final BackgroundColorSpan COLOR_NORMAL = new BackgroundColorSpan(0xFFFFFFFF);

	public static final String[] GROUP_NUMBERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
	public static final String[] GROUP_OPERATORS = new String[] { "+", "-", "*", "/", "^" };
	public static final String[] GROUP_FUNCTIONS = new String[] { "sin", "cos", "tan", "ln", "log", "pi", "sqrt", "e",
			"rand" };

	public static final int NUMBER = 0;
	public static final int OPERATOR = 1;
	public static final int FUNCTION_SEPERATOR = 2;
	public static final int FUNCTION = 3;
	public static final int BRACKET_CLOSE = 4;

	private static final String ELEMENT_SEPERATOR = " ";
	public CatKeyboardView catKeyboardView;
	private int currentlySelectedElementNumber = 0;
	private int previouslySelectedElementNumber = 0;
	private int selectionStartIndex = 0;
	private int selectionEndIndex = 0;
	private int previousSelectionStartIndex = 0;
	private int previousSelectionEndIndex = 0;

	private String currentlySelectedElement = null;
	private int currentlySelectedElementType = 0;
	private boolean editMode = false;
	private Spannable highlightSpan = null;
	private FormulaEditorDialog formulaEditorDialog = null;
	private boolean ignoreNextUpdate = false;

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
		updateSelectionIndices();

	}

	public void setFormulaEditorDialog(FormulaEditorDialog dialog) {
		formulaEditorDialog = dialog;
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

		selectionEndIndex = currentInput.indexOf(ELEMENT_SEPERATOR, cursorPos);
		if (selectionEndIndex == -1) {
			selectionEndIndex = currentInput.length();
		}

		int tempIndex = 0;

		while (tempIndex < selectionEndIndex) {
			currentlySelectedElementNumber++;
			selectionStartIndex = tempIndex;
			tempIndex = currentInput.indexOf(ELEMENT_SEPERATOR, tempIndex);
			if (tempIndex == -1) {
				break;
			}
			tempIndex++;
		}
		if (cursorPos == 0) {
			selectionStartIndex = 0;
		}

		//Log.i("info", "start index: " + selectionStartIndex);
		//Log.i("info", "end index: " + selectionEndIndex);
		//Log.i("info", "Selected element: " + currentlySelectedElementNumber);
		//operatorSelectionIndex = selectionStartIndex;

		checkSelectedTextType();

		previouslySelectedElementNumber = currentlySelectedElementNumber;

		highlightSelection();

	}

	//What have we actually selected in the Formula? We might need to add items belonging to the FormulaElement
	private void checkSelectedTextType() {

		editMode = true;

		currentlySelectedElement = getText().subSequence(selectionStartIndex, selectionEndIndex).toString();
		Log.i("info", "FEEditText: check selected Type " + currentlySelectedElementType);
		checkAndSetSelectedType();
		if (currentlySelectedElementType == FUNCTION) {
			extendSelectionForBracketFromBegin();
			//TODO: extend selection across formula
		} else if (currentlySelectedElementType == BRACKET_CLOSE) {
			extendSelectionForBracketFromEnd();
			//TODO: extend selection across formula
		} else if (currentlySelectedElementType == FUNCTION_SEPERATOR) {
			extendSelectionForFunctionOnSeperator();
			return;
		}

	}

	public void extendSelectionForBracketFromBegin() {
		Log.i("info", "extendSelection for function");
		int bracketCount = 1;
		String text = getText().toString().substring(selectionEndIndex);
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

	}

	public void extendSelectionForFunctionOnSeperator() {
		extendSelectionForBracketFromEnd();
		extendSelectionForBracketFromBegin();

	}

	public void extendSelectionForBracketFromEnd() {
		Log.i("info", "extendSelection for function from end bracket");
		int bracketCount = 1;
		String text = getText().toString().substring(0, selectionStartIndex);
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
			if ((text.charAt(selectionStartIndex - 1) == ' ' || text.charAt(selectionStartIndex - 1) == '(')) {
				break;
			}
			selectionStartIndex--;
		}

	}

	public void extendSelection(int left, int right) {
		Log.i("info", "extendSelection" + left + " " + right);
		String currentInput = getText().toString();
		while (right > 0) {
			selectionEndIndex = currentInput.indexOf(ELEMENT_SEPERATOR, selectionEndIndex + 1);
			right--;
			if (selectionEndIndex == -1) {
				selectionEndIndex = currentInput.length();
			}
		}
		left = currentlySelectedElementNumber - 1 - left;
		int newSelectionStart = 0;
		while (left > 0) {
			newSelectionStart = currentInput.indexOf(ELEMENT_SEPERATOR, newSelectionStart);
			left--;
			newSelectionStart++;
			if (newSelectionStart == -1) {
				newSelectionStart = 0;
				break;
			}
		}
		selectionStartIndex = newSelectionStart;

	}

	public void highlightSelection() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		highlightSpan.removeSpan(COLOR_EDITING);

		highlightSpan.setSpan(COLOR_HIGHLIGHT, selectionStartIndex, selectionEndIndex,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		//		previousSelectionStartIndex = selectionStartIndex;
		//		previousSelectionEndIndex = selectionEndIndex;
	}

	public void clearSelectionHighlighting() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		highlightSpan.removeSpan(COLOR_EDITING);
	}

	public void highlightSelectionCurrentlyEditing() {
		Spannable str = this.getText();

		str.setSpan(COLOR_EDITING, selectionStartIndex, selectionEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public void checkAndModifyKeyInput(CatKeyEvent catKey) {
		String newElement = "" + catKey.getDisplayLabelString();
		Log.i("info", "Key pressed: " + catKey.getDisplayLabelString());
		Log.i("info",
				"KeyCode:" + catKey.getKeyCode() + " ScanCode:" + catKey.getScanCode() + " MetaState:"
						+ catKey.getMetaState() + " DisplayLabel:" + catKey.getDisplayLabel());

		if (catKey.getNumber() == CatKeyEvent.KEYCODE_ENTER) {
			updateSelectionIndices();
			return;
		}

		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			deleteOneCharAtCurrentPosition();
		} else {
			appendToTextFieldAtCurrentPosition(newElement);
		}

		//		if (currentlySelectedElementType == NUMBER) {
		//			if (newElementIsOperator) { //|| newElementIsFunction) { TODO
		//				replaceValueBySubElement(newElement);
		//			} else if (newElementIsNumber) {
		//				replaceValueByValue(newElement);
		//			} else {
		//				specialKeyPressOnValue(catKey);
		//			}
		//		} else if ((currentlySelectedElement.getType() == FormulaElement.ELEMENT_OP_OR_FCT)) {
		//			if (newElementIsOperator) {//|| newElementIsFunction) { TODO
		//				replaceSubElementBySubElement(newElement);
		//			} else if (newElementIsNumber) {
		//				replaceSubElementByValue(newElement);
		//			} else {
		//				specialKeyPressOnSubElement(catKey);
		//			}
		//		} else {
		//			//PANIC!
		//			//replaceValueByValue(newElement);
		//		}

	}

	//	public void specialKeyPressOnValue(CatKeyEvent catKey) {
	//
	//		editMode = false;
	//		Editable text = getText();
	//
	//		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
	//
	//			if (selectionEndIndex > selectionStartIndex + 1) {
	//				text.replace(selectionEndIndex - 1, selectionEndIndex, "");
	//				currentlySelectedElement.deleteLastCharacterInValue();
	//				setText(text);
	//				selectionEndIndex--;
	//
	//			} else {
	//				currentlySelectedElement.replaceValue("0");
	//				text.replace(selectionEndIndex - 1, selectionEndIndex, "0");
	//				setText(text);
	//			}
	//
	//		} else if (catKey.getKeyCode() == KeyEvent.KEYCODE_PERIOD || catKey.getKeyCode() == KeyEvent.KEYCODE_COMMA) {
	//			String sign = "."; //TODO Do we want representation for , as comma?
	//
	//			if (currentlySelectedElement.addCommaIfPossible()) {
	//
	//				text.insert(selectionEndIndex, "" + sign);
	//				setText(text);
	//				selectionEndIndex++;
	//			}
	//		}
	//
	//		highlightSelectionCurrentlyEditing();
	//	}
	//
	//	public void specialKeyPressOnSubElement(CatKeyEvent catKey) {
	//		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
	//			String value = currentlySelectedElement.getParent().getFirstChildValue();
	//			replaceSubElementByValue(value);
	//			selectionEndIndex = selectionStartIndex + value.length();
	//			highlightSelection();
	//			editMode = true;
	//
	//		}
	//	}

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
			//			Log.i("info", "Sel end: " + text.charAt(selectionEndIndex - 1) + text.charAt(selectionStartIndex - 1)); throughs exception if cursor is at beginning of formular and you press DEL button
			if (text.charAt(selectionEndIndex - 1) == ',') {
				return;
			} else if (text.charAt(selectionEndIndex - 1) == ')') {
				doSelectionAndHighlighting();
				return;
			} else if (text.charAt(selectionEndIndex - 1) == '(') {
				doSelectionAndHighlighting();
				return;
			}
			text.replace(selectionEndIndex - 1, selectionEndIndex, "");
			selectionEndIndex--;
			selectionStartIndex = selectionEndIndex;
		}

		setText(text);
		setSelection(selectionEndIndex);
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

	public void setPossibleInput(int type) {
		//TODO: ensure only the inputtype specified in type is displayed on keyboard
		//	if ((type & INPUT_TYPE_NUMBERS) > 0) ...we allow input of values etc. 
	}

	//	public Formula setNewFormulaAndReturnOldFormula(Formula formula) {
	//		Formula old = this.formula;
	//		this.formula = formula;
	//		this.setEnabled(true);
	//		String formulaAsText = formula.getEditTextRepresentation();
	//		formulaAsText = formulaAsText.substring(0, formulaAsText.length() - 1);
	//		this.setText(formulaAsText);
	//		updateSelectionIndices();
	//		return old;
	//	}

	public boolean getEditMode() {
		return editMode;
	}

	public boolean checkIsOperator(String text) {
		for (String item : GROUP_OPERATORS) {
			if (item.equals(text)) {
				currentlySelectedElementType = OPERATOR;
				return true;
			}
		}
		return false;
	}

	public void checkAndSetSelectedType() {

		currentlySelectedElementType = NUMBER;

		if (currentlySelectedElement.startsWith(",")) {
			currentlySelectedElementType = FUNCTION_SEPERATOR;

		} else if (currentlySelectedElement.startsWith(")")) {
			currentlySelectedElementType = BRACKET_CLOSE;
		}

		for (String item : GROUP_OPERATORS) {
			if (currentlySelectedElement.startsWith(item)) {
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
