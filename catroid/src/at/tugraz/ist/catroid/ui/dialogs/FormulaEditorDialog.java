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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;

public class FormulaEditorDialog extends Dialog implements OnClickListener {

	private final Context context;
	private Button okButton;
	private Brick currentBrick;
	private EditText edit;

	public FormulaEditorDialog(Context context, Brick brick) {

		super(context, R.style.dialog_fullscreen);
		currentBrick = brick;
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_formula_editor);
		LinearLayout brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		brickSpace.addView(currentBrick.getEditorView(context));

		setTitle("Editor testyyyy");
		setCanceledOnTouchOutside(true);

		okButton = (Button) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		Button cancelButton = (Button) findViewById(R.id.formula_editor_cancel_button);
		cancelButton.setOnClickListener(this);

		okButton = (Button) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		edit = (EditText) findViewById(R.id.formula_editor_edit_text);
		edit.setOnClickListener(this);
	}

	public void setInputFocusAndText(String text) {
		edit.setText("12344");

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
		}
	}
}
