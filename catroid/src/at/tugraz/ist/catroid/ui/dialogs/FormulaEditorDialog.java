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

import java.util.Locale;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.formulaeditor.CalcGrammarLexer;
import at.tugraz.ist.catroid.formulaeditor.CalcGrammarParser;
import at.tugraz.ist.catroid.formulaeditor.CatKeyboardView;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaEditorEditText;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;

public class FormulaEditorDialog extends Dialog implements OnClickListener, OnDismissListener, OnGestureListener {

	private final Context context;
	private Brick currentBrick;
	private FormulaEditorEditText textArea;
	private int value;
	private Formula formula = null;
	private CatKeyboardView catKeyboardView;
	private LinearLayout brickSpace;
	private View brickView;

	//private GestureDetector gestureDetector = null;

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

		ImageButton okButton = (ImageButton) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		ImageButton cancelButton = (ImageButton) findViewById(R.id.formula_editor_cancel_button);
		cancelButton.setOnClickListener(this);

		ImageButton backButton = (ImageButton) findViewById(R.id.formula_editor_back_button);
		backButton.setOnClickListener(this);

		//		FormulaEditorEditText rolf = (FormulaEditorEditText) findViewById(R.id.testy);
		//		rolf.setFormula(new Formula("0"));
		//		//rolf.setInputType(0);// turn off default input method
		//		rolf.setFormulaEditorDialog(this);

		textArea = (FormulaEditorEditText) findViewById(R.id.formula_editor_edit_field);
		//TODO save in in the brick
		//Formula data = new Formula("0");
		//textArea.setFormula(data);
		//textArea.setInputType(0);// turn off default input method: oncheckEditor() does this now

		Log.i("info", "DisplayLanguage: " + Locale.getDefault().getDisplayLanguage());

		catKeyboardView = (CatKeyboardView) findViewById(R.id.keyboardcat);
		catKeyboardView.setEditText(textArea);
		textArea.catKeyboardView = catKeyboardView;
	}

	//	public void updateGraphicalRepresentation(FormulaRepresentation formula) {
	//		Log.i("info", "zwoosh!!!");
	//		if (formula == null) {
	//			formulaSpace.removeAllViews();
	//			formulaSpace.addView(theBricksView);
	//			flipView.setDisplayedChild(1);
	//			return;
	//		} else {
	//			View v = formula.getView(context, this);
	//			formulaSpace.removeAllViews();
	//			formulaSpace.addView(v);
	//			flipView.setDisplayedChild(0);
	//		}
	//	}

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
		CharStream cs = new ANTLRStringStream(formulaToParse);
		CalcGrammarLexer lexer = new CalcGrammarLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcGrammarParser parser = new CalcGrammarParser(tokens);

		try {
			FormulaElement parserFormulaElement = parser.formula();

			Log.i("info", "formula: " + formula.getEditTextRepresentation());
			Log.i("info", "getParserErrorCount: " + parser.getParserErrorCount());
			if (parser.getParserErrorMessages() != null) {
				Toast.makeText(context, R.string.formula_editor_parse_fail, Toast.LENGTH_SHORT).show();
				for (String err : parser.getParserErrorMessages()) {
					String[] temp = err.substring(12).split(" ");
					Log.i("info", "" + Integer.parseInt(temp[0]));
					return Integer.parseInt(temp[0]);
				}

			} else {
				formula.setRoot(parserFormulaElement);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//			Log.i("info", "index: " + e.index);
			//			Log.i("info", "line: " + e.line);
			//			Log.i("info", "charPositionInLine: " + e.charPositionInLine);
			//			Log.i("info", "message: " + e.getMessage());
			Toast.makeText(context, R.string.formula_editor_parse_fail, Toast.LENGTH_SHORT).show();
			return -2;
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
					//Log.i("info", "Inteperetation of Formular:" + this.formula.interpret());
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("info", "FormulaEditorDialog.onKeyDown(), keyCode:" + String.valueOf(keyCode));
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (textArea.hasChanges()) {
					Toast.makeText(context, R.string.formula_editor_changes_discarded, Toast.LENGTH_SHORT).show();
				}
				this.dismiss();

		}

		return textArea.catKeyboardView.onKeyDown(keyCode, event);

	}

	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		Log.i("info", "FLING!");
		return true;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
