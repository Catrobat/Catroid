/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.api.identity.AuthorizationClient;
import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.AuthorizationResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.recyclerview.dialog.login.OAuthUsernameDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener;
import org.catrobat.catroid.utils.DeviceSettingsProvider;
import org.catrobat.catroid.utils.ToastUtil;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import static org.catrobat.catroid.web.ServerAuthenticationConstants.GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID;

public class GoogleLoginHandler extends BaseActivity implements GoogleLogInTask.OnGoogleServerLogInCompleteListener,
		CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener,
		CheckOAuthTokenTask.OnCheckOAuthTokenCompleteListener,
		GoogleVerifyUserTask.OnGoogleVerifyUserCompleteListener,
		GoogleExchangeCodeTask.OnGoogleExchangeCodeCompleteListener {

	public static final int REQUEST_CODE_GOOGLE_AUTHORIZATION = 111;

	private AppCompatActivity activity;

	@SuppressWarnings("RestrictedApi")
	public GoogleLoginHandler(AppCompatActivity activity) {
		this.activity = activity;
	}

	public void signInWithGoogle() {
		CredentialManager credentialManager = CredentialManager.create(activity);

		StringBuilder hashedNonce = new StringBuilder();
		try {
			String rawNonce = UUID.randomUUID().toString();
			byte[] bytes = rawNonce.getBytes();
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(bytes);

			for (byte b : digest) {
				hashedNonce.append(String.format("%02x", b));
			}
		} catch (Exception e) {
			Log.i("Google", "Creating nonce has failed.");
			e.printStackTrace();
		}

		GetSignInWithGoogleOption googleSignInOption =
				new GetSignInWithGoogleOption.Builder(GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID)
						.setNonce(hashedNonce.toString())
						.build();
		GetCredentialRequest credentialRequest = new GetCredentialRequest.Builder()
				.addCredentialOption(googleSignInOption)
				.build();
		Executor executor = ContextCompat.getMainExecutor(activity);
		CancellationSignal cancellationSignal = new CancellationSignal();
		GoogleLoginHandler googleLoginHandler = this;
		credentialManager.getCredentialAsync(
				activity,
				credentialRequest,
				cancellationSignal,
				executor,
				new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
					@Override
					public void onResult(GetCredentialResponse getCredentialResponse) {
						Credential credential = getCredentialResponse.getCredential();
						GoogleIdTokenCredential tokenCredential =
								GoogleIdTokenCredential.createFrom(credential.getData());

						GoogleVerifyUserTask googleVerifyUserTask = new GoogleVerifyUserTask(tokenCredential);
						googleVerifyUserTask.setOnGoogleVerifyUserCompleteListener(googleLoginHandler);
						googleVerifyUserTask.execute();
					}

					@Override
					public void onError(@NonNull GetCredentialException e) {
						ToastUtil.showError(activity,
								String.format(activity.getString(R.string.error_google_plus_sign_in), e.getLocalizedMessage().replace(":", "")));
						e.printStackTrace();
					}
				}
		);
	}

	@Override
	public void onGoogleVerifyUserComplete(GoogleIdTokenCredential account, String googleEmail) {
		String id = account.getId();
		String personName = account.getDisplayName();
		String locale = DeviceSettingsProvider.getUserCountryCode();
		String idToken = account.getIdToken();

		PreferenceManager.getDefaultSharedPreferences(activity).edit()
				.putString(Constants.GOOGLE_ID, id)
				.putString(Constants.GOOGLE_USERNAME, personName)
				.putString(Constants.GOOGLE_EMAIL, googleEmail)
				.putString(Constants.GOOGLE_LOCALE, locale)
				.putString(Constants.GOOGLE_ID_TOKEN, idToken)
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
	public void onCheckEmailAvailableComplete(Boolean emailAvailable, String provider) {
		if (emailAvailable) {
			authorizeGoogleUser();
		} else {
			showOauthUserNameDialog(Constants.GOOGLE_PLUS);
		}
	}

	public void authorizeGoogleUser() {
		AuthorizationRequest authorizationRequest = new AuthorizationRequest.Builder()
				.requestOfflineAccess(GOOGLE_LOGIN_CATROWEB_SERVER_CLIENT_ID)
				.setRequestedScopes(List.of(
						new Scope("https://www.googleapis.com/auth/userinfo.email")  // Request email scope
				))
				.build();

		AuthorizationClient authorizationClient = Identity.getAuthorizationClient(activity);
		authorizationClient.authorize(authorizationRequest).addOnSuccessListener(authorizationResult -> {
			if (authorizationResult.hasResolution()) {
				try {
					authorizationResult.getPendingIntent().send(REQUEST_CODE_GOOGLE_AUTHORIZATION);
				} catch (Exception e) {
					Log.e("Google", "Failed to start authorization UI: " + e.getLocalizedMessage());
				}
			} else {
				PreferenceManager.getDefaultSharedPreferences(activity).edit()
						.putString(Constants.GOOGLE_EXCHANGE_CODE, authorizationResult.getServerAuthCode())
						.apply();
				exchangeGoogleServerAuthCode();
			}
		}).addOnFailureListener(e -> Log.e("Google", "Authorization failed", e));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_GOOGLE_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
			AuthorizationClient authorizationClient = Identity.getAuthorizationClient(this);
			AuthorizationResult authorizationResult;

			try {
				authorizationResult = authorizationClient.getAuthorizationResultFromIntent(data);
			} catch (ApiException e) {
				throw new RuntimeException(e);
			}

			PreferenceManager.getDefaultSharedPreferences(activity).edit()
					.putString(Constants.GOOGLE_EXCHANGE_CODE, authorizationResult.getServerAuthCode())
					.apply();
			exchangeGoogleServerAuthCode();
		}
	}

	private void exchangeGoogleServerAuthCode() {
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

	@Override
	public void onGoogleServerLogInComplete() {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.GOOGLE_PLUS);
		((SignInCompleteListener) activity).onLoginSuccessful(bundle);
	}

	private void showOauthUserNameDialog(String provider) {
		OAuthUsernameDialogFragment dialog = new OAuthUsernameDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, provider);
		dialog.setArguments(bundle);
		dialog.setSignInCompleteListener((SignInCompleteListener) activity);
		dialog.show(activity.getSupportFragmentManager(), OAuthUsernameDialogFragment.TAG);
	}
}
