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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.io.NumPad;

public class FormulaEditorDialog extends Dialog implements OnClickListener, OnDismissListener {

	private final Context context;
	private Brick currentBrick;
	private EditText edit;
	private int value;
	NumPad numPad;

	//	EditorInfo ei;

	public FormulaEditorDialog(Context context, Brick brick) {

		super(context, R.style.dialog_fullscreen);
		currentBrick = brick;
		this.context = context;
		this.value = 33;

		//		this.ei = new EditorInfo();
		//		ei.fieldName += "FormulaEditorInfo";

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_formula_editor);
		LinearLayout brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		brickSpace.addView(currentBrick.getView(context, 0, null));

		setTitle(R.string.dialog_formula_editor_title);
		setCanceledOnTouchOutside(true);

		ImageButton okButton = (ImageButton) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		ImageButton cancelButton = (ImageButton) findViewById(R.id.formula_editor_cancel_button);
		cancelButton.setOnClickListener(this);

		okButton = (ImageButton) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		edit = (EditText) findViewById(R.id.formula_editor_edit_text);
		edit.setOnClickListener(this);

		/*
		 * For the rare people amongst us writing their own text editors, you will need to implement
		 * onCreateInputConnection(EditorInfo)
		 * to return a new instance of your own InputConnection interface allowing the IME to interact with your editor.
		 * http://developer.android.com/reference/android/view/inputmethod/InputMethodManager.html
		 */
		//getWindow().set
		//WindowManager.LayoutParams.
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		//		numPad = new NumPad();
		//		numPad.switchInputMethod("NumPad");
		//		ims = new InputMethodService();
		//		ims.switchInputMethod("NumPad");
		//		IBinder token = numPad.getCurrentInputBinding().getConnectionToken();
		imm.showInputMethodPicker();

		//		List<InputMethodInfo> inputMethodInfo = imm.getInputMethodList();
		//
		//		Binder binder = new Binder();
		//
		//		imm.setInputMethod(token, "NumPad");

		//						for(int i =0; i < inputMethodInfo.size(); i++){
		//							inputMethodInfo.get(i)
		//						}

		//imm.setInputMethod(, id)
		//this.show();

		//imm.setInputMethod(token, id);

	}

	public void setInputFocusAndText(String text) {
		edit.setText(text);

	}

	public int getReturnValue() {
		return value;
	}

	public void onClick(View v) {
		ProjectManager projectManager = ProjectManager.getInstance();

		switch (v.getId()) {
			case R.id.formula_editor_ok_button:
				//				final EditText input = new EditText(context);
				//				InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				//				inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
				dismiss();
				break;

			case R.id.formula_editor_cancel_button:
				dismiss();
				break;

			case R.id.formula_editor_back_button:
				dismiss();
				break;
		}
	}

	public void onDismiss(DialogInterface dialog) {
		this.dismiss();
	}

	//	public void onShow(DialogInterface dialog) {
	//		final EditText input = new EditText(context);
	//		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	//		inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
	//	}
}
