/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.RegistrationTask;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import org.catrobat.catroid.web.ServerCalls;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginRegisterDialog extends DialogFragment implements OnRegistrationCompleteListener {

	private static final String PASSWORD_FORGOTTEN_PATH = "catroid/passwordrecovery?username=";
	public static final String DIALOG_FRAGMENT_TAG = "dialog_login_register";

	private EditText usernameEditText;
	private EditText passwordEditText;
	private TextView termsOfUseLinkTextView;
	private Button loginOrRegister;
	private Button passwordForgotten;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_login_register, container);

		usernameEditText = (EditText) rootView.findViewById(R.id.username);
		passwordEditText = (EditText) rootView.findViewById(R.id.password);
		termsOfUseLinkTextView = (TextView) rootView.findViewById(R.id.register_terms_link);
		loginOrRegister = (Button) rootView.findViewById(R.id.login_register_button);
		passwordForgotten = (Button) rootView.findViewById(R.id.password_forgotten_button);

		String termsOfUseUrl = getString(R.string.about_link_template, getString(R.string.catrobat_terms_of_use),
				getString(R.string.register_catroid_terms_of_use_text));
		termsOfUseLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
		termsOfUseLinkTextView.setText(Html.fromHtml(termsOfUseUrl));

		usernameEditText.setText("");
		passwordEditText.setText("");

		loginOrRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				handleLoginRegisterButtonClick();
			}
		});
		passwordForgotten.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				handlePasswordForgottenButtonClick();
			}
		});

		getDialog().setTitle(R.string.login_register_dialog_title);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		getDialog().setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		return rootView;
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();

		UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
		uploadProjectDialog.show(getFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	private void handleLoginRegisterButtonClick() {
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		RegistrationTask registrationTask = new RegistrationTask(getActivity(), username, password);
		registrationTask.setOnRegistrationCompleteListener(this);
		registrationTask.execute();
	}

	private void handlePasswordForgottenButtonClick() {
		String username = usernameEditText.getText().toString();
		String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTP : ServerCalls.BASE_URL_HTTP;

		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + PASSWORD_FORGOTTEN_PATH + username));
		getActivity().startActivity(browserIntent);
	}
}
