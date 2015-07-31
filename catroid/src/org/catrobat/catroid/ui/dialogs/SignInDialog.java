/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;

public class SignInDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_sign_in";

	private Button loginButton;
	private Button registerButton;
	private LoginButton facebookLoginButton;
	private TextView termsOfUseLinkTextView;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sign_in, null);

		loginButton = (Button) rootView.findViewById(R.id.dialog_sign_in_login);
		registerButton = (Button) rootView.findViewById(R.id.dialog_sign_in_register);
		facebookLoginButton = (LoginButton) rootView.findViewById(R.id.dialog_sign_in_facebook_login_button);
		termsOfUseLinkTextView = (TextView) rootView.findViewById(R.id.register_terms_link);

		String termsOfUseUrl = getString(R.string.about_link_template, Constants.CATROBAT_TERMS_OF_USE_URL,
				getString(R.string.register_pocketcode_terms_of_use_text));
		termsOfUseLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
		termsOfUseLinkTextView.setText(Html.fromHtml(termsOfUseUrl));

		final AlertDialog signInDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
				.setTitle(R.string.sign_in_dialog_title).create();
		signInDialog.setCanceledOnTouchOutside(true);
		signInDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleLoginButtonClick();
			}
		});

		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleRegisterButtonClick();
			}
		});
		facebookLoginButton.setFragment(this);

		return signInDialog;
	}

	private void handleLoginButtonClick() {
		LogInDialog logInDialog = new LogInDialog();
		logInDialog.show(getActivity().getSupportFragmentManager(), LogInDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
	}

	private void handleRegisterButtonClick() {
		RegistrationDialog registrationDialog = new RegistrationDialog();
		registrationDialog.show(getActivity().getSupportFragmentManager(), RegistrationDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("FB", data.toString());
	}
}
