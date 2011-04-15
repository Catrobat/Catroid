/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
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

	protected EditText editText;
	protected EditText referencedEditText;
	protected Context context;

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
		final Button closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
		editText = (EditText) findViewById(R.id.dialogEditText);
		editText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					Toast.makeText(EditDialog.this.context, R.string.notification_no_text_entered, Toast.LENGTH_SHORT)
							.show();
					closeButton.setEnabled(false);
				}
				else if (s.length() == 1 && s.charAt(0) == '.') {
					closeButton.setEnabled(false);
				}
				else {
					closeButton.setEnabled(true);
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

}
