/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckEmailAvailableTask;
import org.catrobat.catroid.transfers.CheckUserNameAvailableTask;
import org.catrobat.catroid.transfers.RegistrationTask;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationListener;
import org.catrobat.catroid.ui.ViewUtils;
import org.catrobat.catroid.utils.DeviceSettingsProvider;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class RegistrationDialogFragment extends DialogFragment implements OnRegistrationListener {

	public static final String TAG = RegistrationDialogFragment.class.getSimpleName();

	private TextInputLayout usernameInputLayout;
	private TextInputLayout emailInputLayout;
	private TextInputLayout passwordInputLayout;
	private TextInputLayout confirmPasswordInputLayout;
	private EditText userNameEditText;
	private EditText emailAddressEditText;
	private EditText passwordEditText;
	private EditText confirmPasswordEditText;
	private AlertDialog alertDialog;

	private SignInCompleteListener signInCompleteListener;

	public void setSignInCompleteListener(SignInCompleteListener signInCompleteListener) {
		this.signInCompleteListener = signInCompleteListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_register, null);

		usernameInputLayout = view.findViewById(R.id.dialog_register_username);
		emailInputLayout = view.findViewById(R.id.dialog_register_email);
		passwordInputLayout = view.findViewById(R.id.dialog_register_password);
		confirmPasswordInputLayout = view.findViewById(R.id.dialog_register_password_confirm);

		alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.register)
				.setView(view)
				.setPositiveButton(R.string.register, null)
				.create();

		userNameEditText = usernameInputLayout.getEditText();
		emailAddressEditText = emailInputLayout.getEditText();
		passwordEditText = passwordInputLayout.getEditText();
		confirmPasswordEditText = confirmPasswordInputLayout.getEditText();

		userNameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
				if (s.toString().trim().isEmpty()) {
					usernameInputLayout.setError(getString(R.string.error_register_empty_username));
				} else if (s.toString().trim().contains("@")) {
					usernameInputLayout.setError(getString(R.string.error_register_username_as_email));
				} else if (!s.toString().trim().matches("^[a-zA-Z0-9-_.]*$")) {
					usernameInputLayout.setError(getString(R.string.error_register_invalid_username));
				} else if (s.toString().trim().startsWith("-") || s.toString().startsWith("_") || s.toString().startsWith(".")) {
					usernameInputLayout.setError(getString(R.string.error_register_username_start_with));
				} else {
					usernameInputLayout.setErrorEnabled(false);
				}

				if (!usernameInputLayout.isErrorEnabled()) {
					CheckUserNameAvailableTask checkUserNameAvailableTask = new CheckUserNameAvailableTask(s.toString());
					checkUserNameAvailableTask.setOnCheckUserNameAvailableCompleteListener(new CheckUserNameAvailableTask.OnCheckUserNameAvailableCompleteListener() {
						@Override
						public void onCheckUserNameAvailableComplete(Boolean userNameAvailable, String username) {
							if (userNameAvailable == null) {
								ToastUtil.showError(getActivity(), R.string.error_internet_connection);
							} else if (userNameAvailable) {
								usernameInputLayout.setError(getString(R.string.error_register_username_already_exists));
							}
						}
					});
					checkUserNameAvailableTask.execute();
				}
				handleRegisterBtnStatus();
			}
		});

		emailAddressEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
				if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches() || s.toString().isEmpty()) {
					emailInputLayout.setError(getString(R.string.error_register_invalid_email_format));
				} else {
					emailInputLayout.setErrorEnabled(false);
				}
				if (!emailInputLayout.isErrorEnabled()) {
					CheckEmailAvailableTask checkEmailAvailableTask = new
							CheckEmailAvailableTask(s.toString(), Constants
							.NO_OAUTH_PROVIDER);
					checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(new CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener() {
						@Override
						public void onCheckEmailAvailableComplete(Boolean emailAvailable, String provider) {
							if (emailAvailable == null) {
								ToastUtil.showError(getActivity(), R.string.error_internet_connection);
							} else if (emailAvailable) {
								emailInputLayout.setError(getString(R.string.error_register_email_exists));
							}
						}
					});
					checkEmailAvailableTask.execute();
				}
				handleRegisterBtnStatus();
			}
		});

		passwordEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					passwordInputLayout.setError(getString(R.string.error_register_empty_password));
				} else if (s.toString().length() < 6) {
					passwordInputLayout.setError(getString(R.string.error_register_password_at_least_6_characters));
				} else if (!s.toString().equals(confirmPasswordEditText.getText().toString())) {
					confirmPasswordInputLayout.setError(getString(R.string.error_register_passwords_mismatch));
					passwordInputLayout.setErrorEnabled(false);
				} else {
					passwordInputLayout.setErrorEnabled(false);
					confirmPasswordInputLayout.setErrorEnabled(false);
				}
				handleRegisterBtnStatus();
			}
		});

		confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					confirmPasswordInputLayout.setError(getString(R.string.error_register_empty_confirm_password));
				} else if (s.toString().length() < 6) {
					confirmPasswordInputLayout.setError(getString(R.string.error_register_password_at_least_6_characters));
				} else if (!s.toString().equals(passwordEditText.getText().toString())) {
					confirmPasswordInputLayout.setError(getString(R.string.error_register_passwords_mismatch));
				} else {
					confirmPasswordInputLayout.setErrorEnabled(false);
					passwordInputLayout.setErrorEnabled(false);
				}
				handleRegisterBtnStatus();
			}
		});

		CheckBox showPasswordCheckBox = view.findViewById(R.id.show_password);
		showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
					confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		String eMail = DeviceSettingsProvider.getUserEmail(getActivity());
		if (eMail != null) {
			emailAddressEditText.setText(eMail);
			emailInputLayout.setErrorEnabled(false);
		}

		alertDialog.setOnShowListener(dialog -> {
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(buttonView -> onRegisterButtonClick());
			ViewUtils.showKeyboard(userNameEditText);
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

	@Override
	public void onRegistrationFailed(String msg) {
		confirmPasswordEditText.setError(msg);
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
	}

	private void onRegisterButtonClick() {
		String username = userNameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString();
		String email = emailAddressEditText.getText().toString();

		RegistrationTask registrationTask = new RegistrationTask(getActivity(), username, password, email);
		registrationTask.setOnRegistrationListener(this);
		registrationTask.execute();
	}

	private void handleRegisterBtnStatus() {
		if (alertDialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
			if (!usernameInputLayout.isErrorEnabled() && !emailInputLayout.isErrorEnabled() && !passwordInputLayout
					.isErrorEnabled() && !confirmPasswordInputLayout.isErrorEnabled() && Utils.isNetworkAvailable(getActivity())) {
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
			} else {
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		}
	}
}
