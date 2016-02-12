/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.LoginTask;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.web.ServerCalls;

public class LogInDialog extends DialogFragment implements LoginTask.OnLoginCompleteListener {

	public static final String PASSWORD_FORGOTTEN_PATH = "resetting/request";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_login_register";

	private EditText usernameEditText;
	private EditText passwordEditText;
	private CheckBox showPasswordCheckBox;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_login, null);

		usernameEditText = (EditText) rootView.findViewById(R.id.dialog_login_username);
		passwordEditText = (EditText) rootView.findViewById(R.id.dialog_login_password);
		showPasswordCheckBox = (CheckBox) rootView.findViewById(R.id.dialog_login_checkbox_showpassword);

		usernameEditText.setText("");
		passwordEditText.setText("");

		showPasswordCheckBox.setChecked(false);

		showPasswordCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (showPasswordCheckBox.isChecked()) {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		final AlertDialog loginDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
				.setTitle(R.string.login).setPositiveButton(R.string.login, null)
				.setNeutralButton(R.string.password_forgotten, null).create();
		loginDialog.setCanceledOnTouchOutside(true);
		loginDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		loginDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);

				Button loginButton = loginDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				loginButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handleLoginButtonClick();
					}
				});

				Button passwordForgottenButton = loginDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
				passwordForgottenButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handlePasswordForgottenButtonClick();
					}
				});
			}
		});

		return loginDialog;
	}

	@Override
	public void onLoginComplete() {
		dismiss();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.NO_OAUTH_PROVIDER);
		ProjectManager.getInstance().signInFinished(getFragmentManager(), bundle);
	}

	private void handleLoginButtonClick() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		LoginTask loginTask = new LoginTask(getActivity(), username, password);
		loginTask.setOnLoginCompleteListener(this);
		loginTask.execute();
	}

	private void handlePasswordForgottenButtonClick() {
		String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTPS : Constants.BASE_URL_HTTPS;
		String url = baseUrl + PASSWORD_FORGOTTEN_PATH;

		((MainMenuActivity) getActivity()).startWebViewActivity(url);
	}
}
