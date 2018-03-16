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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.LoginTask;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.web.ServerCalls;

public class LoginDialogFragment extends DialogFragment implements LoginTask.OnLoginCompleteListener {

	public static final String TAG = LoginDialogFragment.class.getSimpleName();
	public static final String PASSWORD_FORGOTTEN_PATH = "resetting/request";

	private TextInputLayout usernameInputLayout;
	private TextInputLayout passwordInputLayout;

	private SignInDialog.SignInCompleteListener signInCompleteListener;

	public void setSignInCompleteListener(SignInDialog.SignInCompleteListener signInCompleteListener) {
		this.signInCompleteListener = signInCompleteListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_login, null);

		usernameInputLayout = view.findViewById(R.id.dialog_login_username);
		passwordInputLayout = view.findViewById(R.id.dialog_login_password);

		CheckBox showPasswordCheckBox = view.findViewById(R.id.show_password);
		showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					passwordInputLayout.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordInputLayout.getEditText()
							.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.login)
				.setView(view)
				.setPositiveButton(R.string.login, null)
				.setNeutralButton(R.string.password_forgotten, null)
				.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						onLoginButtonClick();
					}
				});

				alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						onPasswordForgottenButtonClick();
					}
				});
			}
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
	public void onCancel(DialogInterface dialog) {
		signInCompleteListener.onLoginCancel();
	}

	private void onLoginButtonClick() {
		String username = usernameInputLayout.getEditText().getText().toString().replaceAll("\\s", "");
		String password = passwordInputLayout.getEditText().getText().toString();

		LoginTask loginTask = new LoginTask(getActivity(), username, password);
		loginTask.setOnLoginCompleteListener(this);
		loginTask.execute();
	}

	private void onPasswordForgottenButtonClick() {
		String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTPS : Constants.BASE_URL_HTTPS;
		String url = baseUrl + PASSWORD_FORGOTTEN_PATH;

		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		startActivity(intent);
	}
}
