/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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
import org.catrobat.catroid.transfers.RegistrationData;
import org.catrobat.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.web.ServerCalls;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class PasswordRecoveryDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_password_recovery";

	private EditText usernameEditText;
	private EditText emailEditText;
	private Button recoverPassword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_login, container);

		usernameEditText = (EditText) rootView.findViewById(R.id.username);
		emailEditText = (EditText) rootView.findViewById(R.id.email);
		recoverPassword = (Button) rootView.findViewById(R.id.password_recovery_button);

		usernameEditText.setText(RegistrationData.INSTANCE.getUserName());
		emailEditText.setText(UtilDeviceInfo.getUserEmail(getActivity()));

		recoverPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handlePasswordRecoveryButtonClick();
			}
		});

		getDialog().setTitle(R.string.password_recovery_dialog_title);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		getDialog().setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
				inputManager.showSoftInput(emailEditText, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		return rootView;
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();
	}

	private void handlePasswordRecoveryButtonClick() {
		//TODO: send HTTP request
		String username = usernameEditText.getText().toString();
		String email = emailEditText.getText().toString();
		ServerCalls.getInstance().recoverPassword(username, email);

		//String username = usernameEditText.getText().toString();
		//String baseUrl = ServerCalls.useTestUrl ? ServerCalls.BASE_URL_TEST_HTTP : ServerCalls.BASE_URL_HTTP;
		//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + PASSWORD_FORGOTTEN_PATH + username));
		//getActivity().startActivity(browserIntent);
	}
}
