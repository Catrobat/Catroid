/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.LoginTask;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.web.ServerCalls;

public class LogInDialog extends DialogFragment implements LoginTask.OnLoginCompleteListener {

	public static final String PASSWORD_FORGOTTEN_PATH = "resetting/request";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_login_register";

	private TextInputLayout usernameInputLayout;
	private TextInputLayout passwordInputLayout;
	private CheckBox showPasswordCheckBox;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		@SuppressLint("InflateParams")
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_login, null);

		usernameInputLayout = view.findViewById(R.id.dialog_login_username);
		passwordInputLayout = view.findViewById(R.id.dialog_login_password);
		showPasswordCheckBox = view.findViewById(R.id.show_password);

		showPasswordCheckBox.setChecked(false);

		showPasswordCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (showPasswordCheckBox.isChecked()) {
					passwordInputLayout.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					passwordInputLayout.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType
							.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setView(view)
				.setTitle(R.string.login)
				.setPositiveButton(R.string.login, null)
				.setNeutralButton(R.string.password_forgotten, null)
				.create();

		alertDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handleLoginButtonClick();
					}
				});
				alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handlePasswordForgottenButtonClick();
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
		ProjectManager.getInstance().signInFinished(getActivity(), getFragmentManager(), bundle);
		dismiss();
	}

	private void handleLoginButtonClick() {
		String username = usernameInputLayout.getEditText().getText().toString().replaceAll("\\s", "");
		String password = passwordInputLayout.getEditText().getText().toString();

		LoginTask loginTask = new LoginTask(getActivity(), username, password);
		loginTask.setOnLoginCompleteListener(this);
		loginTask.execute();
	}

	private void handlePasswordForgottenButtonClick() {
		String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTPS : Constants.BASE_URL_HTTPS;
		String url = baseUrl + PASSWORD_FORGOTTEN_PATH;

		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		startActivity(intent);
	}
}
