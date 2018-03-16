/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.ui.recyclerview.dialog.login;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.RegistrationTask;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import org.catrobat.catroid.utils.UtilDeviceInfo;

public class RegistrationDialogFragment extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String TAG = RegistrationDialogFragment.class.getSimpleName();

	private TextInputLayout usernameInputLayout;
	private TextInputLayout emailInputLayout;
	private TextInputLayout passwordInputLayout;
	private TextInputLayout confirmPasswordInputLayout;

	private SignInDialog.SignInCompleteListener signInCompleteListener;

	public void setSignInCompleteListener(SignInDialog.SignInCompleteListener signInCompleteListener) {
		this.signInCompleteListener = signInCompleteListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_register, null);

		usernameInputLayout = view.findViewById(R.id.dialog_register_username);
		emailInputLayout = view.findViewById(R.id.dialog_register_email);
		passwordInputLayout = view.findViewById(R.id.dialog_register_password);
		confirmPasswordInputLayout = view.findViewById(R.id.dialog_register_password_confirm);

		CheckBox showPasswordCheckBox = view.findViewById(R.id.show_password);
		showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					passwordInputLayout.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
					confirmPasswordInputLayout.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordInputLayout.getEditText()
							.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					confirmPasswordInputLayout.getEditText()
							.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		String eMail = UtilDeviceInfo.getUserEmail(getActivity());
		if (eMail != null) {
			emailInputLayout.getEditText().setText(eMail);
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.register)
				.setView(view)
				.setPositiveButton(R.string.register, null)
				.create();

		alertDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						onRegisterButtonClick();
					}
				});
			}
		});

		return alertDialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		signInCompleteListener.onLoginCancel();
	}

	@Override
	public void onRegistrationComplete() {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.NO_OAUTH_PROVIDER);
		signInCompleteListener.onLoginSuccessful(bundle);
		dismiss();
	}

	private void onRegisterButtonClick() {
		String username = usernameInputLayout.getEditText().getText().toString().trim();
		String password = passwordInputLayout.getEditText().getText().toString();
		String passwordConfirmation = confirmPasswordInputLayout.getEditText().getText().toString();
		String email = emailInputLayout.getEditText().getText().toString();

		if (password.equals(passwordConfirmation)) {
			RegistrationTask registrationTask = new RegistrationTask(getActivity(), username, password, email);
			registrationTask.setOnRegistrationCompleteListener(this);
			registrationTask.execute();
		} else {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.register_error)
					.setMessage(R.string.register_password_mismatch)
					.setPositiveButton(R.string.ok, null)
					.show();
		}
	}
}
