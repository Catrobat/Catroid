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

package org.catrobat.catroid.transfers;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.recyclerview.dialog.login.OAuthUsernameDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener;
import org.catrobat.catroid.utils.DeviceSettingsProvider;
import org.catrobat.catroid.utils.ToastUtil;

public class GooglePlusLoginHandler implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		CheckOAuthTokenTask.OnCheckOAuthTokenCompleteListener,
		GoogleLogInTask.OnGoogleServerLogInCompleteListener,
		CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener,
		GoogleExchangeCodeTask.OnFacebookExchangeCodeCompleteListener {

	private AppCompatActivity activity;
	public static final int GPLUS_REQUEST_CODE_SIGN_IN = 0;
	public static final int REQUEST_CODE_GOOGLE_PLUS_SIGNIN = 100;
	public static final int STATUS_CODE_SIGN_IN_CURRENTLY_IN_PROGRESS = 12502;
	private GoogleApiClient googleApiClient;
	private static final String GOOGLE_PLUS_CATROWEB_SERVER_CLIENT_ID = "427226922034-r016ige5kb30q9vflqbt1h0i3arng8u1.apps.googleusercontent.com";

	public GooglePlusLoginHandler(AppCompatActivity activity) {
		this.activity = activity;

		GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestServerAuthCode(GOOGLE_PLUS_CATROWEB_SERVER_CLIENT_ID, false)
				.requestIdToken(GOOGLE_PLUS_CATROWEB_SERVER_CLIENT_ID)
				.build();

		googleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
				.build();
	}

	public GoogleApiClient getGoogleApiClient() {
		return googleApiClient;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GPLUS_REQUEST_CODE_SIGN_IN) {
			googleApiClient.connect();
		} else if (requestCode == REQUEST_CODE_GOOGLE_PLUS_SIGNIN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			triggerGPlusLogin(result);
		}
	}

	private void triggerGPlusLogin(GoogleSignInResult result) {
		if (result.isSuccess()) {
			GoogleSignInAccount account = result.getSignInAccount();
			onGoogleLogInComplete(account);
		} else {
			switch (result.getStatus().getStatusCode()) {
				case STATUS_CODE_SIGN_IN_CURRENTLY_IN_PROGRESS:
					break;

				default:
					ToastUtil.showError(activity, activity.getString(R.string.error_google_plus_sign_in,
							Integer.toString(result.getStatus().getStatusCode())));
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

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		sharedPreferences.edit().putString(Constants.GOOGLE_ID, id).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_USERNAME, personName).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_EMAIL, email).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_LOCALE, locale).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_ID_TOKEN, idToken).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_EXCHANGE_CODE, code).commit();

		CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(activity, id, Constants.GOOGLE_PLUS);
		checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
		checkOAuthTokenTask.execute();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
		activity.startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_PLUS_SIGNIN);
	}

	@Override
	public void onConnectionSuspended(int i) {
		googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(activity, GPLUS_REQUEST_CODE_SIGN_IN);
			} catch (IntentSender.SendIntentException e) {
				googleApiClient.connect();
			}
		} else {
			new AlertDialog.Builder(activity)
					.setTitle(R.string.error)
					.setMessage(R.string.sign_in_error)
					.setPositiveButton(R.string.ok, null)
					.show();
		}
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
