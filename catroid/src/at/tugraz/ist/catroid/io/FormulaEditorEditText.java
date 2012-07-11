package at.tugraz.ist.catroid.io;

import android.R;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
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
import at.tugraz.ist.catroid.content.Formula;
import at.tugraz.ist.catroid.content.FormulaElement;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class FormulaEditorEditText extends EditText implements OnClickListener, OnTouchListener {

	private static final BackgroundColorSpan COLOR_EDITING = new BackgroundColorSpan(0xFF00FFFF);
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);
	private static final BackgroundColorSpan COLOR_NORMAL = new BackgroundColorSpan(0xFFFFFFFF);
	private static final String ELEMENT_SEPERATOR = " ";
	public CatKeyboardView catKeyboardView;
	private int currentlySelectedElementNumber = 0;
	private int previouslySelectedElementNumber = 0;
	private int selectionStartIndex = 0;
	private int selectionEndIndex = 0;
	private int previousSelectionStartIndex = 0;
	private int previousSelectionEndIndex = 0;
	private int operatorSelectionIndex = 0;
	private Formula formula = null;
	private FormulaElement currentlySelectedFormulaElement = null;
	private boolean editMode = false;
	//private String valueToBeEdited = "";
	private Spannable highlightSpan = null;
	private FormulaEditorDialog formulaEditorDialog = null;

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
		this.setBackgroundColor(getResources().getColor(R.color.transparent));
		//this.setBackgroundResource(0);
		//this.setCursorVisible(false);
		//this.setLines(5);

		this.setText("0");
	}

	public void setFormulaEditorDialog(FormulaEditorDialog dialog) {
		formulaEditorDialog = dialog;
	}

	//TODO: On doubleclick the text selection widget pops up... found no way to kill it
	public void trickTextSelectionThingy() {
		//this.setSelected(false);
	}

	//highlight the selected word
	public synchronized void updateSelectionIndices() {

		//TODO: Interpreter Test
		Log.i("info", "Formula Interpretation: " + formula.interpret());
		Log.i("info", "updateSelection");

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
		Log.i("info", "Selected element: " + currentlySelectedElementNumber);
		operatorSelectionIndex = selectionStartIndex;

		checkSelectedTextType();

		previouslySelectedElementNumber = currentlySelectedElementNumber;

		this.highlightSelection();

	}

	private void updateGraphicalRepresentation(int left, int right) {
		FormulaRepresentation fr = null;

		if (currentlySelectedFormulaElement.getType() == FormulaElement.ELEMENT_FIRST_VALUE
				|| currentlySelectedFormulaElement.getType() == FormulaElement.ELEMENT_SECOND_VALUE) {
			fr = new FormulaRepresentation(currentlySelectedFormulaElement.getValue());
		} else {
			fr = new FormulaRepresentation(null, left + " Elements", currentlySelectedFormulaElement.getValue(), right
					+ " Elements");
		}
		formulaEditorDialog.updateGraphicRepresentation(fr);
	}

	//What have we actually selected in the Formula? We might need to add items belonging to the FormulaElement
	private void checkSelectedTextType() {

		//		if (editMode == true) {
		//			selectedElement.replaceValue(valueToBeEdited);
		//			editMode = false;
		//			valueToBeEdited = "";
		//		}
		editMode = true;

		currentlySelectedFormulaElement = formula.findItemByPosition(currentlySelectedElementNumber);
		Log.i("info", "FEEditText: check selected Type ");
		FormulaElement parentElement = null;
		int childCount1 = 1;
		int childCount2 = 1;

		switch (currentlySelectedFormulaElement.getType()) {
		//TODO: once keyboard is implemented, set the keys that should be available for our rules
			case FormulaElement.ELEMENT_FIRST_VALUE:
			case FormulaElement.ELEMENT_SECOND_VALUE:
				break;
			case FormulaElement.ELEMENT_FUNCTION:
				break;
			case FormulaElement.ELEMENT_OPERATOR:
				Log.i("info", "Search pos for child " + currentlySelectedElementNumber);
				//selectedElement = formula.getParentOfItemByPosition(currentlySelectedElementNumber);
				parentElement = currentlySelectedFormulaElement.getParent();
				FormulaElement el1 = parentElement
						.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE_REPLACED_BY_CHILDREN);
				FormulaElement el2 = parentElement
						.getChildOfType(FormulaElement.ELEMENT_SECOND_VALUE_REPLACED_BY_CHILDREN);

				if (el1 != null) {
					childCount1 = el1.getNumberOfRecursiveChildren();
				}
				if (el2 != null) {
					childCount2 = el2.getNumberOfRecursiveChildren();
				}

				extendSelection(childCount1, childCount2);
				break;
		}

		if (previouslySelectedElementNumber != currentlySelectedElementNumber) {
			updateGraphicalRepresentation(childCount1, childCount2);
		}

	}

	public void selectNewlyAddedOperator() {
		currentlySelectedElementNumber++;
		selectionStartIndex = selectionEndIndex + 1;
		selectionEndIndex += 2;
		extendSelection(1, 1);
		highlightSelection();
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

		//if (previousSelectionEndIndex > str.length()) {
		//	previousSelectionEndIndex = str.length();
		//}
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		highlightSpan.removeSpan(COLOR_EDITING);
		//highlightSpan.setSpan(COLOR_NORMAL, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//		str.setSpan(COLOR_NORMAL, previousSelectionStartIndex, previousSelectionEndIndex,
		//				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		highlightSpan.setSpan(COLOR_HIGHLIGHT, selectionStartIndex, selectionEndIndex,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		previousSelectionStartIndex = selectionStartIndex;
		previousSelectionEndIndex = selectionEndIndex;
	}

	public void highlightSelectionCurrentlyEditing() {
		Spannable str = this.getText();

		str.setSpan(COLOR_EDITING, selectionStartIndex, selectionEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	//TextWatcher tells us what it has just replaced, we still have to make sure its represented correctly for and in the formula
	//	public void checkAndModifyKeyInput(char lol) {
	//		String newElement = "" + lol;
	//		Log.i("info", "fooooo" + newElement + "val: " + valueToBeEdited);
	//		if (formula == null) {
	//			return;
	//		}
	//		boolean newElementIsOperator = Formula.isInputMemberOfAGroup(newElement, Formula.OPERATORS);
	//
	//		if (newElementIsOperator
	//				&& (selectedElement.getType() == FormulaElement.ELEMENT_FIRST_VALUE || selectedElement.getType() == FormulaElement.ELEMENT_SECOND_VALUE)) {
	//			replaceNumberWithSubElement(newElement);
	//		} else if (newElementIsOperator && (selectedElement.getType() == FormulaElement.ELEMENT_OPERATOR)) {
	//			replaceOperatorByOperator(newElement);
	//		} else if (lol == Keyboard.KEYCODE_DELETE && (selectedElement.getType() == FormulaElement.ELEMENT_OPERATOR)) {
	//			deleteElementAndChildren();
	//		} else {
	//			replaceNumberByNumber(newElement);
	//		}
	//
	//	}

	public void checkAndModifyKeyInput(CatKeyEvent catKey) {
		String newElement = "" + catKey.getNumber();
		Log.i("info", "Key pressed: " + newElement);
		if (formula == null) {
			return;
		}
		if (catKey.getNumber() == CatKeyEvent.KEYCODE_ENTER) {
			updateSelectionIndices();
			return;
		}

		boolean newElementIsOperator = Formula.isInputMemberOfAGroup(newElement, Formula.OPERATORS);
		boolean newElementIsNumber = Formula.isInputMemberOfAGroup(newElement, Formula.NUMBERS);
		boolean newElementIsFunction = Formula.isInputMemberOfAGroup(newElement, Formula.FUNCTIONS);

		if ((currentlySelectedFormulaElement.getType() == FormulaElement.ELEMENT_FIRST_VALUE || currentlySelectedFormulaElement
				.getType() == FormulaElement.ELEMENT_SECOND_VALUE)) {
			if (newElementIsOperator) {
				replaceValueBySubElement(newElement);
			} else if (newElementIsNumber) {
				replaceValueByNumber(newElement);
			} else if (newElementIsFunction) {
				//TODO ...
			} else {
				specialKeyPressOnNumber(catKey);
			}
		} else if ((currentlySelectedFormulaElement.getType() == FormulaElement.ELEMENT_OPERATOR)) {
			if (newElementIsOperator) {
				replaceOperatorByOperator(newElement);
			} else if (newElementIsNumber) {
				replaceElementHierarchyByNumber(newElement);
			} else if (newElementIsFunction) {
				//TODO ...
			} else {
				specialKeyPressOnOperator(catKey);
			}
		} else if ((currentlySelectedFormulaElement.getType() == FormulaElement.ELEMENT_FUNCTION)) {
			//TODO Make it work for functions
			if (newElementIsOperator) {

			} else if (newElementIsNumber) {

			} else if (newElementIsFunction) {

			} else {

			}
		} else {
			replaceValueByNumber(newElement);
		}

	}

	public void specialKeyPressOnNumber(CatKeyEvent catKey) {
		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			editMode = false;
			Editable text = getText();
			if (selectionEndIndex > selectionStartIndex + 1) {
				text.replace(selectionEndIndex - 1, selectionEndIndex, "");
				currentlySelectedFormulaElement.deleteLastCharacterInValue();
				setText(text);
				selectionEndIndex--;

			} else {
				currentlySelectedFormulaElement.replaceValue("0");
				text.replace(selectionEndIndex - 1, selectionEndIndex, "0");
				setText(text);
			}

			highlightSelectionCurrentlyEditing();
		}
	}

	public void specialKeyPressOnOperator(CatKeyEvent catKey) {
		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			String value = currentlySelectedFormulaElement.getParent().getFirstChildValue();
			replaceElementHierarchyByNumber(value);
			selectionEndIndex = selectionStartIndex + value.length();
			highlightSelection();
			editMode = true;

		}
	}

	private void appendToTextFieldAtCurrentPosition(String newElement) {
		Editable text = getText();
		text.insert(selectionEndIndex, newElement);
		setText(text);
		selectionEndIndex++;
	}

	public void replaceElementHierarchyByNumber(String newElement) {
		Log.i("info", "replace tree!");

		if (editMode) {
			currentlySelectedFormulaElement = currentlySelectedFormulaElement.getParent().makeMeALeaf(newElement);
			Editable text = getText();
			text.replace(selectionStartIndex, selectionEndIndex, newElement);
			setText(text);
			selectionEndIndex = selectionStartIndex + 1;
			editMode = false;
		} else {
			currentlySelectedFormulaElement.addToValue(newElement);
			appendToTextFieldAtCurrentPosition(newElement);
		}

		highlightSelectionCurrentlyEditing();
	}

	public void replaceValueByNumber(String newElement) {
		Log.i("info", "replace num by num");

		if (editMode) {
			currentlySelectedFormulaElement.replaceValue(newElement);
			Editable text = getText();
			text.replace(selectionStartIndex, selectionEndIndex, newElement);
			setText(text);
			selectionEndIndex = selectionStartIndex + 1;
			editMode = false;
		} else {
			currentlySelectedFormulaElement.addToValue(newElement);
			appendToTextFieldAtCurrentPosition(newElement);
		}

		highlightSelectionCurrentlyEditing();
	}

	public void replaceOperatorByOperator(String newElement) {
		Log.i("info", "replace op by op");

		Editable text = getText();
		text.replace(operatorSelectionIndex, operatorSelectionIndex + 1, newElement);
		setText(text);
		currentlySelectedFormulaElement.replaceValue(newElement);
	}

	public void replaceValueBySubElement(String newElement) {
		Log.i("info", "replace num by sub el");
		String textOutput = formula.addToFormula(newElement, currentlySelectedFormulaElement);
		if (textOutput == "") {
			return;
		}
		Editable text = getText();
		text.replace(selectionStartIndex, selectionEndIndex, textOutput);
		setText(text);
		//selectNewlyAddedOperator();
		setSelection(selectionStartIndex + 2);
		updateSelectionIndices();
	}

	public void setPossibleInput(int type) {
		//TODO: ensure only the inputtype specified in type is displayed on keyboard
		//	if ((type & INPUT_TYPE_NUMBERS) > 0) ...we allow input of values etc. 
	}

	//	public void deleteSelectionIfNeeded() {
	//		if (deleteElementOnInsert && editMode == true && valueToBeEdited == "") {
	//			deleteElementOnInsert = false;
	//			Editable text = getText();
	//			Log.i("info", "replace: " + selectionStartIndex + " to " + selectionEndIndex);
	//			text.replace(selectionStartIndex, selectionEndIndex, "");
	//			selectionEndIndex = selectionStartIndex;
	//			setText(text);
	//		}
	//	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Formula getFormula() {
		return formula;
	}

	public boolean getEditMode() {
		return editMode;
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
		if (catKeyboardView.getVisibility() == KeyboardView.GONE) {
			catKeyboardView.setEnabled(true);
			catKeyboardView.setVisibility(KeyboardView.VISIBLE);
		}
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
			trickTextSelectionThingy();
			return true;
		}
	});

	@Override
	public boolean onCheckIsTextEditor() {
		return false;
	}
}
