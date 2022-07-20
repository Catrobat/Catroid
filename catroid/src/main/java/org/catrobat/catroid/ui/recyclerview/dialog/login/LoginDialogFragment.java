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
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.transfers.LoginTask;
import org.catrobat.catroid.ui.ViewUtils;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.web.ServerCalls;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LoginDialogFragment extends DialogFragment implements LoginTask.OnLoginListener {

	public static final String TAG = LoginDialogFragment.class.getSimpleName();
	public static final String PASSWORD_FORGOTTEN_PATH = "reset-password";

	private TextInputLayout usernameInputLayout;
	private TextInputLayout passwordInputLayout;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private AlertDialog alertDialog;

	private SignInCompleteListener signInCompleteListener;

	public void setSignInCompleteListener(SignInCompleteListener signInCompleteListener) {
		this.signInCompleteListener = signInCompleteListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_login, null);

		usernameInputLayout = view.findViewById(R.id.dialog_login_username);
		passwordInputLayout = view.findViewById(R.id.dialog_login_password);
		usernameEditText = usernameInputLayout.getEditText();
		passwordEditText = passwordInputLayout.getEditText();

		CheckBox showPasswordCheckBox = view.findViewById(R.id.show_password);
		showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.login)
				.setView(view)
				.setPositiveButton(R.string.login, null)
				.setNegativeButton(R.string.cancel, null)
				.setNeutralButton(R.string.reset_password, null)
				.setCancelable(true)
				.create();

		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					usernameInputLayout.setError(getString(R.string.error_register_empty_username));
				} else if (!s.toString().trim().matches("^[a-zA-Z0-9-_.]*$")) {
					usernameInputLayout.setError(getString(R.string.error_register_invalid_username));
				} else {
					usernameInputLayout.setErrorEnabled(false);
				}
				handleLoginBtnStatus();
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
				} else {
					passwordInputLayout.setErrorEnabled(false);
				}
				handleLoginBtnStatus();
			}
		});

		alertDialog.setOnShowListener(dialog -> {
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(buttonView -> onLoginButtonClick());
			alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(buttonView -> onPasswordForgottenButtonClick());

			ViewUtils.showKeyboard(usernameEditText);
		});

		return alertDialog;
	}

	@Override
	public void onLoginComplete() {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.NO_OAUTH_PROVIDER);
		signInCompleteListener.onLoginSuccessful(bundle);
		dismiss();
	}

	@Override
	public void onLoginFailed(String msg) {
		passwordEditText.setError(msg);
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		signInCompleteListener.onLoginCancel();
	}

	private void onLoginButtonClick() {
		String username = usernameEditText.getText().toString().replaceAll("\\s", "");
		String password = passwordEditText.getText().toString();
		LoginTask loginTask = new LoginTask(getActivity(), username, password);
		loginTask.setOnLoginListener(this);
		loginTask.execute();
	}

	private void onPasswordForgottenButtonClick() {
		String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTPS : FlavoredConstants.BASE_URL_HTTPS;
		String url = baseUrl + PASSWORD_FORGOTTEN_PATH;

		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		startActivity(intent);
	}

	private void handleLoginBtnStatus() {
		if (alertDialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
			if (!usernameInputLayout.isErrorEnabled() && !passwordInputLayout.isErrorEnabled()) {
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
			} else {
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			}
		}
	}
}
