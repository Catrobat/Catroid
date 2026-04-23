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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckUserNameAvailableTask;
import org.catrobat.catroid.transfers.GoogleExchangeCodeTask;
import org.catrobat.catroid.transfers.GoogleLogInTask;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DialogInputWatcher;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class OAuthUsernameDialogFragment extends DialogFragment implements
		CheckUserNameAvailableTask.OnCheckUserNameAvailableCompleteListener,
		GoogleExchangeCodeTask.OnGoogleExchangeCodeCompleteListener,
		GoogleLogInTask.OnGoogleServerLogInCompleteListener {

	public static final String TAG = OAuthUsernameDialogFragment.class.getSimpleName();

	private TextInputLayout inputLayout;
	private String openAuthProvider;

	private SignInCompleteListener signInCompleteListener;

	public void setSignInCompleteListener(SignInCompleteListener signInCompleteListener) {
		this.signInCompleteListener = signInCompleteListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_sign_in_oauth_username, null);

		inputLayout = view.findViewById(R.id.dialog_signin_oauth_username);

		Bundle bundle = getArguments();
		if (bundle != null) {
			openAuthProvider = bundle.getString(Constants.CURRENT_OAUTH_PROVIDER);
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.sign_in_dialog_title)
				.setView(view)
				.setPositiveButton(R.string.ok, null)
				.create();

		alertDialog.setOnShowListener(dialog -> {
			Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
			buttonPositive.setOnClickListener(view1 -> onPositiveButtonClick());
			buttonPositive.setEnabled(!inputLayout.getEditText().getText().toString().isEmpty());
			DialogInputWatcher inputWatcher = new DialogInputWatcher(inputLayout, buttonPositive, false);
			inputLayout.getEditText().addTextChangedListener(inputWatcher);
		});

		return alertDialog;
	}

	private void onPositiveButtonClick() {
		String username = inputLayout.getEditText().getText().toString().trim();

		if (username.isEmpty()) {
			inputLayout.setError(getString(R.string.signin_choose_username_empty));
		} else {
			CheckUserNameAvailableTask checkUserNameAvailableTask = new CheckUserNameAvailableTask(username);
			checkUserNameAvailableTask.setOnCheckUserNameAvailableCompleteListener(this);
			checkUserNameAvailableTask.execute();
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		signInCompleteListener.onLoginCancel();
	}

	@Override
	public void onCheckUserNameAvailableComplete(Boolean userNameAvailable, String username) {
		if (userNameAvailable) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.error)
					.setMessage(R.string.oauth_username_taken)
					.setPositiveButton(R.string.ok, null)
					.show();
		} else {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (openAuthProvider.equals(Constants.GOOGLE_PLUS)) {
				sharedPreferences.edit()
						.putString(Constants.GOOGLE_USERNAME, username)
						.apply();

				GoogleExchangeCodeTask googleExchangeCodeTask = new GoogleExchangeCodeTask(getActivity(),
						sharedPreferences.getString(Constants.GOOGLE_EXCHANGE_CODE, Constants.NO_GOOGLE_EXCHANGE_CODE),
						sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
						sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
						sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
						sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE),
						sharedPreferences.getString(Constants.GOOGLE_ID_TOKEN, Constants.NO_GOOGLE_ID_TOKEN));
				googleExchangeCodeTask.setOnGoogleExchangeCodeCompleteListener(this);
				googleExchangeCodeTask.execute();
			}
		}
	}

	@Override
	public void onGoogleExchangeCodeComplete() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		GoogleLogInTask googleLogInTask = new GoogleLogInTask(getActivity(),
				sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
				sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
				sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
				sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE)
		);
		googleLogInTask.setOnGoogleServerLogInCompleteListener(this);
		googleLogInTask.execute();
	}

	@Override
	public void onGoogleServerLogInComplete() {
		signInCompleteListener.onLoginSuccessful(null);
		dismiss();
	}
}
