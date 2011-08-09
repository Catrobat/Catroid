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

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.transfers.RegistrationTask;

public class LoginRegisterDialog extends Dialog implements OnClickListener {
	private final Activity activity;

	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginOrRegister;
	private Button passwordForgotten;

	public LoginRegisterDialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_login_register);
		setTitle(R.string.login_register_dialog_title);
		setCanceledOnTouchOutside(true);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		initializeViews();
		initializeListeners();

		//		this.setOnShowListener(new OnShowListener() {
		//			public void onShow(DialogInterface dialog) {
		//				InputMethodManager inputManager = (InputMethodManager) context
		//						.getSystemService(Context.INPUT_METHOD_SERVICE);
		//				inputManager.showSoftInput(projectUploadName, InputMethodManager.SHOW_IMPLICIT);
		//			}
		//		});

	}

	// initialize
	private void initializeViews() {
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		loginOrRegister = (Button) findViewById(R.id.login_register_button);
		passwordForgotten = (Button) findViewById(R.id.password_forgotten_button);
	}

	private void initializeListeners() {
		loginOrRegister.setOnClickListener(this);
		passwordForgotten.setOnClickListener(this);
	}

	private void initializeValues() {
		usernameEditText.setText("");
		passwordEditText.setText("");
	}

	@Override
	public void show() {
		super.show();
		initializeValues();
	}

	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.login_register_button:
				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				new RegistrationTask(activity, username, password, this).execute();

				break;
			case R.id.password_forgotten_button:
				dismiss();
				break;
		}
	}
}
