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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Formula;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.io.CatKeyboard;
import at.tugraz.ist.catroid.io.CatKeyboardView;
import at.tugraz.ist.catroid.io.FormulaEditorEditText;
import at.tugraz.ist.catroid.io.FormulaRepresentation;

public class FormulaEditorDialog extends Dialog implements OnClickListener, OnDismissListener {

	private final Context context;
	private Brick currentBrick;
	private FormulaEditorEditText textArea;
	private int value;
	private Formula formula;

	private CatKeyboardView catKeyboardView;
	private CatKeyboard catKeyboard;
	private ViewFlipper flipView;
	private LinearLayout brickSpace;
	private LinearLayout formulaSpace;
	private int i = 0;

	//private View overlay;
	//	private int selectionStartIndex = 0;
	//	private int selectionEndIndex = 0;

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

		flipView = (ViewFlipper) findViewById(R.id.catflip);
		brickSpace = (LinearLayout) findViewById(R.id.catview);
		formulaSpace = (LinearLayout) findViewById(R.id.datview);
		brickSpace.addView(currentBrick.getView(context, 0, null));

		flipView.setDisplayedChild(1);
		Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_in);
		flipView.setOutAnimation(slideOut);
		Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_out);
		flipView.setInAnimation(slideIn);

		//LinearLayout brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		//brickSpace.addView(currentBrick.getView(context, 0, null));

		setTitle(R.string.dialog_formula_editor_title);
		setCanceledOnTouchOutside(true);

		ImageButton okButton = (ImageButton) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		ImageButton cancelButton = (ImageButton) findViewById(R.id.formula_editor_cancel_button);
		cancelButton.setOnClickListener(this);

		okButton = (ImageButton) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		//		FormulaEditorEditText rolf = (FormulaEditorEditText) findViewById(R.id.testy);
		//		rolf.setFormula(new Formula("0"));
		//		//rolf.setInputType(0);// turn off default input method
		//		rolf.setFormulaEditorDialog(this);

		textArea = (FormulaEditorEditText) findViewById(R.id.formula_editor_edit_field);
		//TODO save in in the brick
		Formula data = new Formula("0");
		textArea.setFormula(data);
		//textArea.setInputType(0);// turn off default input method
		textArea.setFormulaEditorDialog(this);

		CatKeyboard catKeyboard = null;
		if (Locale.getDefault().getDisplayLanguage().contentEquals(Locale.GERMAN.getDisplayLanguage())) {
			catKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_de);
			//			Log.i("info", "FormulaEditorDialog.onCreate() - DisplayLanguage is DE");
		} else if (Locale.getDefault().getDisplayLanguage().contentEquals(Locale.ENGLISH.getDisplayLanguage())) {
			catKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols_eng);
			//			Log.i("info", "FormulaEditorDialog.onCreate() - DisplayLanguage is ENG");
		}

		Log.i("info", "DisplayLanguage: " + Locale.getDefault().getDisplayLanguage());

		catKeyboardView = (CatKeyboardView) findViewById(R.id.keyboardcat);
		catKeyboardView.setKeyboard(catKeyboard);
		catKeyboardView.setEditText(textArea);
		textArea.catKeyboardView = catKeyboardView;

		//catKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols);
		//		textArea.catKeyboardView = (CatKeyboardView) findViewById(R.id.keyboardcat);
		//		textArea.catKeyboardView.setKeyboard(catKeyboard);

	}

	public void updateGraphicRepresentation(FormulaRepresentation formula) {

		View v = formula.getView(context, this);
		formulaSpace.removeAllViews();
		formulaSpace.addView(v);
		flipView.setDisplayedChild(0);

	}

	public void setInputFocusAndText(String text) {

	}

	public int getReturnValue() {
		return value;
	}

	public void onClick(View v) {

		ProjectManager projectManager = ProjectManager.getInstance();

		switch (v.getId()) {
			case R.id.formula_editor_ok_button:

				flipView.setDisplayedChild(1);

				break;

			case R.id.formula_editor_cancel_button:
				dismiss();
				break;

			case R.id.formula_editor_back_button:
				dismiss();
				break;
			default:
				Log.i("info", "Got some crazy click here!");
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
				this.dismiss();

		}

		return textArea.catKeyboardView.onKeyDown(keyCode, event);

	}

}
