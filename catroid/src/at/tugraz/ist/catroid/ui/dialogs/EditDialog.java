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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;

public class EditDialog extends Dialog {

	protected Button okButton;
	protected Context context;
	protected EditText editText;
	protected EditText addCourseText;
	protected EditText referencedEditText;

	public EditDialog(Context context, EditText referencedEditText) {
		super(context);
		this.context = context;
		this.referencedEditText = referencedEditText;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_edit_text);
		setCanceledOnTouchOutside(true);
		okButton = (Button) findViewById(R.id.dialog_edit_dialog_ok_button);
		editText = (EditText) findViewById(R.id.dialog_edit_dialog_edit_text);
		editText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					Toast.makeText(context, R.string.notification_invalid_text_entered, Toast.LENGTH_SHORT).show();
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

		this.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		super.onCreate(savedInstanceState);
	}

	/**
	 * @return
	 */
	public int getRefernecedEditTextId() {
		// TODO Auto-generated method stub
		return referencedEditText.getId();
	}
}
