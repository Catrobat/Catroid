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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;

/**
 * simple dialog for entering text with ok and cancel button will not permit to
 * enter empty strings you have to implement the key listener in the subclass
 */
public abstract class TextDialog {
	protected Activity activity;
	protected EditText input;
	protected Button buttonPositive;
	protected Button buttonNegative;
	protected ProjectManager projectManager;
	public Dialog dialog;

	public TextDialog(Activity activityFromChild, String title, String hint) {
		this.activity = activityFromChild;
		projectManager = ProjectManager.getInstance();

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_text_dialog, null);

		input = (EditText) view.findViewById(R.id.dialog_text_EditText);
		if (hint != null) {
			input.setHint(hint);
		}

		buttonPositive = (Button) view.findViewById(R.id.dialog_text_ok);
		buttonNegative = (Button) view.findViewById(R.id.dialog_text_cancel);

		if (input.getText().toString().length() == 0) {
			buttonPositive.setEnabled(false);
		}

		builder.setView(view);

		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);

		input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		input.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					buttonPositive.setEnabled(false);
				} else {
					buttonPositive.setEnabled(true);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}
}
