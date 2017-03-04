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
import org.catrobat.catroid.transfers.RegistrationTask;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import org.catrobat.catroid.utils.UtilDeviceInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_registration";

	private EditText usernameEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	private EditText passwordConfirmEditText;
	private CheckBox showPasswordCheckBox;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_register, null);

		usernameEditText = (EditText) rootView.findViewById(R.id.dialog_register_username);
		emailEditText = (EditText) rootView.findViewById(R.id.dialog_register_email);
		passwordEditText = (EditText) rootView.findViewById(R.id.dialog_register_password);
		passwordConfirmEditText = (EditText) rootView.findViewById(R.id.dialog_register_password_confirm);
		showPasswordCheckBox = (CheckBox) rootView.findViewById(R.id.dialog_register_checkbox_showpassword);

		usernameEditText.setText("");
		passwordEditText.setText("");
		passwordConfirmEditText.setText("");
		String eMail = UtilDeviceInfo.getUserEmail(getActivity());
		if (eMail != null) {
			emailEditText.setText(eMail);
		}
		showPasswordCheckBox.setChecked(false);

		showPasswordCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (showPasswordCheckBox.isChecked()) {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
					passwordConfirmEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordConfirmEditText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		final AlertDialog registrationDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
				.setTitle(R.string.register).setPositiveButton(R.string.register, null).create();
		registrationDialog.setCanceledOnTouchOutside(true);
		registrationDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		registrationDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
				inputManager.showSoftInput(emailEditText, InputMethodManager.SHOW_IMPLICIT);

				Button registerButton = registrationDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				registerButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handleRegisterButtonClick();
					}
				});
			}
		});

		return registrationDialog;
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.NO_OAUTH_PROVIDER);
		ProjectManager.getInstance().signInFinished(getFragmentManager(), bundle);
	}

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

	private void handleRegisterButtonClick() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		String passwordConfirmation = passwordConfirmEditText.getText().toString();
		String email = emailEditText.getText().toString();
        if(isEmailValid(username))
        {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.register_error)
                    .setMessage(R.string.register_username_email).setPositiveButton(R.string.ok, null).show();
        }
		else if (!password.equals(passwordConfirmation)) {
			new AlertDialog.Builder(getActivity()).setTitle(R.string.register_error)
					.setMessage(R.string.register_password_mismatch).setPositiveButton(R.string.ok, null).show();
		} else {
			RegistrationTask registrationTask = new RegistrationTask(getActivity(), username, password, email);
			registrationTask.setOnRegistrationCompleteListener(this);
			registrationTask.execute();
		}
	}
}
