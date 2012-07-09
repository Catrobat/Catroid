package at.tugraz.ist.catroid.io;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import at.tugraz.ist.catroid.content.Formula;
import at.tugraz.ist.catroid.content.FormulaElement;

public class FormulaEditorEditText extends EditText implements OnClickListener, OnTouchListener {

	private static final String ELEMENT_SEPERATOR = " ";
	private int currentlySelectedElementNumber = 0;
	private int selectionStartIndex = 0;
	private int selectionEndIndex = 0;
	private int previousSelectionStartIndex = 0;
	private int previousSelectionEndIndex = 0;
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);
	private static final BackgroundColorSpan COLOR_NORMAL = new BackgroundColorSpan(0xFFFFFFFF);
	private Formula formula;

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
		this.setBackgroundResource(0);
		this.addTextChangedListener(new FormulaEditorTextWatcher(this));
		this.setCursorVisible(false);

		this.setText("0");
	}

	//TODO: On doubleclick the text selection widget pops up... found no way to kill it
	public void trickTextSelectionThingy() {
		//this.setSelected(false);
	}

	//highlight the selected word
	public synchronized void updateSelectionIndices() {

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
		checkSelectedTextType();
		this.setSelection();

	}

	//What have we actually selected in the Formula? We might need to add items belonging to the FormulaElement
	private void checkSelectedTextType() {
		FormulaElement selectedElement = formula.findItemByPosition(currentlySelectedElementNumber);
		Log.i("info", "FEEditText: check selected Type ");
		FormulaElement parentElement = null;
		switch (selectedElement.getType()) {
			case FormulaElement.ELEMENT_FIRST_VALUE:
			case FormulaElement.ELEMENT_SECOND_VALUE:
				break;
			case FormulaElement.ELEMENT_FUNCTION:
				break;
			case FormulaElement.ELEMENT_OPERATOR:
				Log.i("info", "Search pos for child " + currentlySelectedElementNumber);
				//selectedElement = formula.getParentOfItemByPosition(currentlySelectedElementNumber);
				parentElement = selectedElement.getParent();
				FormulaElement el1 = parentElement
						.getChildOfType(FormulaElement.ELEMENT_FIRST_VALUE_REPLACED_BY_CHILDREN);
				FormulaElement el2 = parentElement
						.getChildOfType(FormulaElement.ELEMENT_SECOND_VALUE_REPLACED_BY_CHILDREN);
				int childCount1 = 1;
				int childCount2 = 1;

				if (el1 != null) {
					childCount1 = el1.getNumberOfRecursiveChildren();
				}
				if (el2 != null) {
					childCount2 = el2.getNumberOfRecursiveChildren();
				}

				extendSelection(childCount1, childCount2);
				break;
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

	public void setSelection() {
		Spannable str = this.getText();

		str.setSpan(COLOR_NORMAL, previousSelectionStartIndex, previousSelectionEndIndex,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		str.setSpan(COLOR_HIGHLIGHT, selectionStartIndex, selectionEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		previousSelectionStartIndex = selectionStartIndex;
		previousSelectionEndIndex = selectionEndIndex;
	}

	public void replaceSelection(String newElement) {

		//Log.i("info", newElement);
		if (formula == null) {
			return;
		}

		//FormulaElement parent = formula.findItemByPosition(currentlySelectedElementNumber);
		String textOutput = formula.addToFormula(newElement, currentlySelectedElementNumber);
		if (textOutput == "") {
			return;
		}

		Editable text = getText();
		Log.i("info", selectionStartIndex + " " + selectionStartIndex + "Text: " + text);

		//text.delete(selectionStartIndex, selectionEndIndex + 1);
		//text.insert(selectionStartIndex, textOutput);
		text.replace(selectionStartIndex, selectionEndIndex + 1, textOutput);
		setText(text);

	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Formula getFormula() {
		return formula;
	}

	@Override
	public void setSelection(int index) {
		Log.i("text", "SetSelection called");
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
			updateSelectionIndices();
		} else if (motion.getAction() == android.view.MotionEvent.ACTION_UP) {
			updateSelectionIndices();
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

	//	@Override
	//	public void selectAll() {
	//	};

	//	@Override
	//	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
	//		//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
	//		//			return false;
	//		//		}
	//		Log.i("info", "" + keyCode);
	//		return super.dispatchKeyEvent(event);
	//	}

	//	@Override
	//	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
	//		outAttrs.actionLabel = null;
	//		outAttrs.label = "Test text";
	//		outAttrs.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
	//		outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
	//
	//		return new FormulaEditorInputConnection(this, true);
	//	}

	//	@Override
	//	public boolean onCheckIsTextEditor() {
	//		return true;
	//	}
}
