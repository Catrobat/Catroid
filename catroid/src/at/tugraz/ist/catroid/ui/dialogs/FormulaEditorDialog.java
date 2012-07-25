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
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.formulaeditor.CalcGrammarParser;
import at.tugraz.ist.catroid.formulaeditor.CatKeyboardView;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaEditorEditText;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;

public class FormulaEditorDialog extends Dialog implements OnClickListener, OnDismissListener {

	private final Context context;
	private Brick currentBrick;
	private FormulaEditorEditText textArea;
	private int value;
	private Formula formula = null;
	private CatKeyboardView catKeyboardView;
	private LinearLayout brickSpace;
	private View brickView;
	private Button okButton = null;

	public FormulaEditorDialog(Context context, Brick brick) {

		super(context, R.style.dialog_fullscreen);
		currentBrick = brick;
		this.context = context;
		this.value = 33;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//for function keys
		Log.i("info", "Key: " + event.getKeyCode());

		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

		}
		super.dispatchKeyEvent(event);
		return false;

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_formula_editor);

		brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		brickView = currentBrick.getView(context, 0, null);
		brickSpace.addView(brickView);

		int brickHeight = brickView.getMeasuredHeight();

		//		flipView = (ViewFlipper) findViewById(R.id.catflip);
		//		flipView.setDisplayedChild(1);
		//		Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_in);
		//		flipView.setOutAnimation(slideOut);
		//		Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_out);
		//		flipView.setInAnimation(slideIn);
		//
		//		flipView.setOnTouchListener(new OnTouchListener() {
		//			public boolean onTouch(View v, MotionEvent event) {
		//
		//				gestureDetector.onTouchEvent(event);
		//				return true;
		//			}
		//		});
		//		gestureDetector = new GestureDetector(context, this);
		//LinearLayout brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		//brickSpace.addView(currentBrick.getView(context, 0, null));

		setTitle(R.string.dialog_formula_editor_title);
		setCanceledOnTouchOutside(true);

		okButton = (Button) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		Button cancelButton = (Button) findViewById(R.id.formula_editor_cancel_button);
		cancelButton.setOnClickListener(this);

		Button backButton = (Button) findViewById(R.id.formula_editor_back_button);
		backButton.setOnClickListener(this);

		textArea = (FormulaEditorEditText) findViewById(R.id.formula_editor_edit_field);
		brickSpace.measure(0, 0);
		catKeyboardView = (CatKeyboardView) findViewById(R.id.keyboardcat);
		catKeyboardView.setEditText(textArea);

		textArea.init(this, brickSpace.getMeasuredHeight(), catKeyboardView);
	}

	public void setInputFocusAndFormula(Formula formula) {

		if (formula == this.formula) {
			return;
		} else if (textArea.hasChanges() == true) {
			Toast.makeText(context, R.string.formula_editor_save_first, Toast.LENGTH_SHORT).show();
			return;
		}

		this.formula = formula;
		textArea.setFieldActive(formula.getEditTextRepresentation());

	}

	public int getReturnValue() {
		return value;
	}

	private int parseFormula(String formulaToParse) {
		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(formulaToParse);
		FormulaElement parserFormulaElement = parser.parseFormula();

		if (parserFormulaElement == null) {
			Toast.makeText(context, R.string.formula_editor_parse_fail, Toast.LENGTH_SHORT).show();
			return parser.getErrorCharacterPosition();
		} else {
			formula.setRoot(parserFormulaElement);
		}
		return -1;
	}

	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.formula_editor_ok_button:

				String formulaToParse = textArea.getText().toString();
				int err = parseFormula(formulaToParse);
				if (err == -1) {
					formula.refreshTextField(brickView);
					textArea.formulaSaved();
					Toast.makeText(context, R.string.formula_editor_changes_saved, Toast.LENGTH_SHORT).show();
				} else if (err == -2) {
					//Crashed it like a BOSS! 
					return;
				} else {
					textArea.highlightParseError(err);
				}

				break;

			case R.id.formula_editor_cancel_button:
				textArea.formulaSaved();
				textArea.setFieldActive(formula.getEditTextRepresentation());
				Toast.makeText(context, R.string.formula_editor_changes_discarded, Toast.LENGTH_SHORT).show();
				showOkayButton();
				break;

			case R.id.formula_editor_back_button:
				if (textArea.hasChanges()) {
					Toast.makeText(context, R.string.formula_editor_changes_discarded, Toast.LENGTH_SHORT).show();
				}
				dismiss();
				break;

			default:
				break;

		}
	}

	public void onDismiss(DialogInterface dialog) {
		this.dismiss();
	}

	public void hideOkayButton() {
		okButton.setClickable(false);
	}

	public void showOkayButton() {
		okButton.setClickable(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (textArea.hasChanges()) {
					Toast.makeText(context, R.string.formula_editor_changes_discarded, Toast.LENGTH_SHORT).show();
				}
				this.dismiss();

		}

		return textArea.catKeyboardView.onKeyDown(keyCode, event);

	}

}
