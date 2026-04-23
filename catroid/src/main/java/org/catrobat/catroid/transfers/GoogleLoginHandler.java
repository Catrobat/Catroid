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

package org.catrobat.catroid.transfers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.recyclerview.dialog.login.OAuthUsernameDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener;
import org.catrobat.catroid.utils.DeviceSettingsProvider;
import org.catrobat.catroid.utils.ToastUtil;

import androidx.appcompat.app.AppCompatActivity;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getClient;
import static com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent;

import static org.catrobat.catroid.web.ServerAuthenticationConstants.GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID;

public class GoogleLoginHandler implements CheckOAuthTokenTask.OnCheckOAuthTokenCompleteListener,
		GoogleLogInTask.OnGoogleServerLogInCompleteListener,
		CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener,
		GoogleExchangeCodeTask.OnGoogleExchangeCodeCompleteListener {

	private AppCompatActivity activity;
	public static final int REQUEST_CODE_GOOGLE_SIGNIN = 100;
	private GoogleSignInClient googleSignInClient;

	@SuppressWarnings("RestrictedApi")
	public GoogleLoginHandler(AppCompatActivity activity) {
		this.activity = activity;

		GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestIdToken(GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID)
				.build();
		googleSignInClient = getClient(this.activity, googleSignInOptions);
	}

	public GoogleSignInClient getGoogleSignInClient() {
		return googleSignInClient;
	}

	@SuppressWarnings("RestrictedApi")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GOOGLE_SIGNIN) {
			Task<GoogleSignInAccount> task = getSignedInAccountFromIntent(data);
			if (task.isSuccessful()) {
				onGoogleLogInComplete(task.getResult());
			} else {
				ToastUtil.showError(activity,
						String.format(activity.getString(R.string.error_google_plus_sign_in), task.getException().getLocalizedMessage().replace(":", "")));
			}
		}
	}

	public void onGoogleLogInComplete(GoogleSignInAccount account) {
		String id = account.getId();
		String personName = account.getDisplayName();
		String email = account.getEmail();
		String locale = DeviceSettingsProvider.getUserCountryCode();
		String idToken = account.getIdToken();
		String code = account.getServerAuthCode();

		PreferenceManager.getDefaultSharedPreferences(activity).edit()
				.putString(Constants.GOOGLE_ID, id)
				.putString(Constants.GOOGLE_USERNAME, personName)
				.putString(Constants.GOOGLE_EMAIL, email)
				.putString(Constants.GOOGLE_LOCALE, locale)
				.putString(Constants.GOOGLE_ID_TOKEN, idToken)
				.putString(Constants.GOOGLE_EXCHANGE_CODE, code)
				.apply();

		CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(activity, id, Constants.GOOGLE_PLUS);
		checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
		checkOAuthTokenTask.execute();
	}

	@Override
	public void onCheckOAuthTokenComplete(Boolean tokenAvailable, String provider) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		if (tokenAvailable) {
			GoogleLogInTask googleLogInTask = new GoogleLogInTask(activity,
					sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
					sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
					sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
					sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE));
			googleLogInTask.setOnGoogleServerLogInCompleteListener(this);
			googleLogInTask.execute();
		} else {
			String email = sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL);
			CheckEmailAvailableTask checkEmailAvailableTask = new CheckEmailAvailableTask(email, Constants.GOOGLE_PLUS);
			checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this);
			checkEmailAvailableTask.execute();
		}
	}

	@Override
	public void onGoogleServerLogInComplete() {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.GOOGLE_PLUS);
		((SignInCompleteListener) activity).onLoginSuccessful(bundle);
	}

	@Override
	public void onCheckEmailAvailableComplete(Boolean emailAvailable, String provider) {
		if (emailAvailable) {
			exchangeGoogleAuthorizationCode();
		} else {
			showOauthUserNameDialog(Constants.GOOGLE_PLUS);
		}
	}

	public void exchangeGoogleAuthorizationCode() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		GoogleExchangeCodeTask googleExchangeCodeTask = new GoogleExchangeCodeTask(activity,
				sharedPreferences.getString(Constants.GOOGLE_EXCHANGE_CODE, Constants.NO_GOOGLE_EXCHANGE_CODE),
				sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
				sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
				sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
				sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE),
				sharedPreferences.getString(Constants.GOOGLE_ID_TOKEN, Constants.NO_GOOGLE_ID_TOKEN));
		googleExchangeCodeTask.setOnGoogleExchangeCodeCompleteListener(this);
		googleExchangeCodeTask.execute();
	}

	private void showOauthUserNameDialog(String provider) {
		OAuthUsernameDialogFragment dialog = new OAuthUsernameDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, provider);
		dialog.setArguments(bundle);
		dialog.setSignInCompleteListener((SignInCompleteListener) activity);
		dialog.show(activity.getSupportFragmentManager(), OAuthUsernameDialogFragment.TAG);
	}

	@Override
	public void onGoogleExchangeCodeComplete() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		GoogleLogInTask googleLogInTask = new GoogleLogInTask(activity,
				sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
				sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
				sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
				sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE));
		googleLogInTask.setOnGoogleServerLogInCompleteListener(this);
		googleLogInTask.execute();
	}
}
