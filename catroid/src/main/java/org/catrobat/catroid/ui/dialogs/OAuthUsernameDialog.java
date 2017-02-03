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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckUserNameAvailableTask;
import org.catrobat.catroid.transfers.FacebookExchangeTokenTask;
import org.catrobat.catroid.transfers.FacebookLogInTask;
import org.catrobat.catroid.transfers.GoogleExchangeCodeTask;
import org.catrobat.catroid.transfers.GoogleLogInTask;

public class OAuthUsernameDialog extends DialogFragment implements CheckUserNameAvailableTask.OnCheckUserNameAvailableCompleteListener, FacebookExchangeTokenTask.OnFacebookExchangeTokenCompleteListener, FacebookLogInTask.OnFacebookLogInCompleteListener, GoogleExchangeCodeTask.OnFacebookExchangeCodeCompleteListener, GoogleLogInTask.OnGoogleServerLogInCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_oauth_username";

	private EditText usernameEditText;
	private String openAuthProvider;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sign_in_oauth_username, null);

		usernameEditText = (EditText) rootView.findViewById(R.id.dialog_signin_oauth_username);
		usernameEditText.setText("");

		Bundle bundle = getArguments();
		if (bundle != null) {
			openAuthProvider = bundle.getString(Constants.CURRENT_OAUTH_PROVIDER);
		}

		final AlertDialog chooseUsernameDialog = new AlertDialog.Builder(getActivity()).setView(rootView)
				.setTitle(R.string.sign_in_dialog_title).setPositiveButton(R.string.ok, null).create();
		chooseUsernameDialog.setCanceledOnTouchOutside(true);
		chooseUsernameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		chooseUsernameDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);

				Button confirmUsernameButton = chooseUsernameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				confirmUsernameButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handleConfirmUsernameButtonClick();
					}
				});
			}
		});

		return chooseUsernameDialog;
	}

	private void handleConfirmUsernameButtonClick() {
		String username = usernameEditText.getText().toString().trim();

		if (username.isEmpty()) {
			new AlertDialog.Builder(getActivity()).setTitle(R.string.error)
					.setMessage(R.string.signin_choose_username_empty).setPositiveButton(R.string.ok, null).show();
		} else {
			CheckUserNameAvailableTask checkUserNameAvailableTask = new CheckUserNameAvailableTask(getActivity(), username);
			checkUserNameAvailableTask.setOnCheckUserNameAvailableCompleteListener(this);
			checkUserNameAvailableTask.execute();
		}
	}

	@Override
	public void onCheckUserNameAvailableComplete(Boolean userNameAvailable, String username) {
		if (userNameAvailable) {
			new CustomAlertDialogBuilder(getActivity()).setTitle(R.string.error).setMessage(R.string.oauth_username_taken)
					.setPositiveButton(R.string.ok, null).show();
		} else {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (openAuthProvider.equals(Constants.FACEBOOK)) {
				sharedPreferences.edit().putString(Constants.FACEBOOK_USERNAME, username).commit();
				FacebookExchangeTokenTask facebookExchangeTokenTask = new FacebookExchangeTokenTask(getActivity(),
						AccessToken.getCurrentAccessToken().getToken(),
						sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
						sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
						sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
						sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
				);
				facebookExchangeTokenTask.setOnFacebookExchangeTokenCompleteListener(this);
				facebookExchangeTokenTask.execute();
			} else if (openAuthProvider.equals(Constants.GOOGLE_PLUS)) {
				sharedPreferences.edit().putString(Constants.GOOGLE_USERNAME, username).commit();

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
	public void onFacebookExchangeTokenComplete(Activity activity) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		FacebookLogInTask facebookLogInTask = new FacebookLogInTask(getActivity(),
				sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
				sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
				sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
				sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
		);
		facebookLogInTask.setOnFacebookLogInCompleteListener(this);
		facebookLogInTask.execute();
	}

	@Override
	public void onFacebookLogInComplete() {
		dismiss();
		ProjectManager.getInstance().signInFinished(getFragmentManager(), null);
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
		dismiss();
		ProjectManager.getInstance().signInFinished(getFragmentManager(), null);
	}
}
