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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Formula;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.io.CatKeyboard;
import at.tugraz.ist.catroid.io.CatKeyboardView;
import at.tugraz.ist.catroid.io.FormulaEditorEditText;

public class FormulaEditorDialog extends Dialog implements OnClickListener, OnDismissListener {

	private final Context context;
	private Brick currentBrick;
	private FormulaEditorEditText textArea;
	private int value;
	private Formula formula;

	private CatKeyboardView catKeyboardView;
	private CatKeyboard catKeyboard;

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
		LinearLayout brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		brickSpace.addView(currentBrick.getView(context, 0, null));

		//		overlay = findViewById(R.id.formula_editor_edit_field_overlay);
		//		overlay.setOnClickListener(this);

		setTitle(R.string.dialog_formula_editor_title);
		setCanceledOnTouchOutside(true);

		ImageButton okButton = (ImageButton) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		ImageButton cancelButton = (ImageButton) findViewById(R.id.formula_editor_cancel_button);
		cancelButton.setOnClickListener(this);

		okButton = (ImageButton) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		textArea = (FormulaEditorEditText) findViewById(R.id.formula_editor_edit_field);
		//TODO save in in the brick
		Formula data = new Formula("0");
		textArea.setFormula(data);
		textArea.setInputType(0);// turn off default input method


		catKeyboard = new CatKeyboard(this.getContext(), R.xml.symbols);

		textArea.catKeyboardView = (CatKeyboardView) findViewById(R.id.keyboardcat);
		textArea.catKeyboardView.setKeyboard(catKeyboard);

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return textArea.catKeyboardView.onKeyDown(keyCode, event);

	}

}
