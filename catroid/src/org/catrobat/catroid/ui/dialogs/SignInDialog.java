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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.common.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckEmailAvailableTask;
import org.catrobat.catroid.transfers.CheckOAuthTokenTask;
import org.catrobat.catroid.transfers.FacebookExchangeTokenTask;
import org.catrobat.catroid.transfers.FacebookLogInTask;
import org.catrobat.catroid.transfers.GoogleExchangeCodeTask;
import org.catrobat.catroid.transfers.GoogleFetchCodeTask;
import org.catrobat.catroid.transfers.GoogleLogInTask;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.web.FacebookCalls;
import org.catrobat.catroid.web.GoogleCalls;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInDialog extends DialogFragment implements
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FacebookLogInTask
		.OnFacebookLogInCompleteListener, FacebookCalls.OnGetFacebookUserInfoCompleteListener, CheckOAuthTokenTask.OnCheckOAuthTokenCompleteListener, CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener,
		FacebookExchangeTokenTask.OnFacebookExchangeTokenCompleteListener, GoogleLogInTask.OnGoogleLogInCompleteListener, GoogleFetchCodeTask.OnGoogleFetchCodeCompleteListener, GoogleExchangeCodeTask.OnFacebookExchangeCodeCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_sign_in";
	private static final int GPLUS_REQUEST_CODE_SIGN_IN = 0;
	private static final Integer RESULT_CODE_AUTH_CODE = 1;
	private static final String SCOPE_EMAIL = "email";

	private Button loginButton;
	private Button registerButton;
	private LoginButton facebookLoginButton;
	private SignInButton gplusLoginButton;
	private TextView termsOfUseLinkTextView;
	private CallbackManager callbackManager;

	private ProgressDialog connectionProgressDialog;
	private GoogleApiClient googleApiClient;
	private ConnectionResult mConnectionResult;
	/* Should we automatically resolve ConnectionResults when possible? */
	private boolean shouldResolve = false;
	/* Is there a ConnectionResult resolution in progress? */
	private boolean isResolving = false;
	private boolean triggerGPlusLogin = false;

	//https://developers.google.com/identity/sign-in/android/start-integrating

	@Override
	public Dialog onCreateDialog(Bundle bundle) {

		initializeFacebookSdk();
		initializeGoogleSdk();

		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sign_in, null);

		loginButton = (Button) rootView.findViewById(R.id.dialog_sign_in_login);
		registerButton = (Button) rootView.findViewById(R.id.dialog_sign_in_register);
		facebookLoginButton = (LoginButton) rootView.findViewById(R.id.dialog_sign_in_facebook_login_button);
		gplusLoginButton = (SignInButton) rootView.findViewById(R.id.dialog_sign_in_gplus_login_button);
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
		facebookLoginButton.setReadPermissions("email");
		facebookLoginButton.setLoginBehavior(FacebookCalls.getInstance().getLoginBehavior());

		gplusLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleGooglePlusLoginButtonClick(view);
			}
		});

		Bundle arguments = getArguments();
		if(arguments != null) {
			if(arguments.getBoolean(Constants.REQUEST_GOOGLE_CODE)){
				requestGoogleAuthorizationCode();
			}
		}

		return signInDialog;
	}

	private void initializeFacebookSdk() {
		FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		FacebookCalls.getInstance().setOnGetFacebookUserInfoCompleteListener(SignInDialog.this);

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						Toast.makeText(getActivity(), loginResult.toString(), Toast.LENGTH_LONG);
						Log.d("Facebook", loginResult.toString());
						FacebookCalls.getInstance().getFacebookUserInfo(loginResult.getAccessToken());
					}

					@Override
					public void onCancel() {
						// App code
						Log.d("Facebook", "cancel");
					}

					@Override
					public void onError(FacebookException exception) {
						// App code
						Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG);
						Log.d("Facebook", exception.getMessage());
					}
				});
	}

	private void initializeGoogleSdk() {

		googleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(new Scope(Scopes.PROFILE)) //nötig?
				.addScope(new Scope(SCOPE_EMAIL))
				.build();

		// Anzuzeigende Statusmeldung, wenn der Verbindungsfehler nicht behoben ist
		connectionProgressDialog = new ProgressDialog(getActivity());
		connectionProgressDialog.setMessage("Signing in to G+...");
	}

	private void handleGooglePlusLoginButtonClick(View view) {
		if (view.getId() == R.id.dialog_sign_in_gplus_login_button) {
			if (!googleApiClient.isConnected()) {
				// User clicked the sign-in button, so begin the sign-in process and automatically
				// attempt to resolve any errors that occur.
				shouldResolve = true;
				googleApiClient.connect();
				triggerGPlusLogin = true;
			} else {
				triggerGPlusLogin();
			}
		}
	}

	private void triggerGPlusLogin() {
		if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null && Plus.AccountApi.getAccountName(googleApiClient) != null) {
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
			String id = currentPerson.getId();
			String personName = currentPerson.getDisplayName();
			String email = Plus.AccountApi.getAccountName(googleApiClient);
			String locale = currentPerson.getLanguage();

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			sharedPreferences.edit().putString(Constants.GOOGLE_ID, id).commit();
			sharedPreferences.edit().putString(Constants.GOOGLE_USERNAME, personName).commit();
			sharedPreferences.edit().putString(Constants.GOOGLE_EMAIL, email).commit();
			sharedPreferences.edit().putString(Constants.GOOGLE_LOCALE, locale).commit();

			CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(getActivity(), id, Constants.GOOGLE_PLUS);
			checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
			checkOAuthTokenTask.execute();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		googleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		googleApiClient.disconnect();
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

	public void requestGoogleAuthorizationCode() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String email = sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL);
		GoogleCalls.getInstance().getGoogleAuthorizationCode(getActivity(), email, this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GPLUS_REQUEST_CODE_SIGN_IN) {
			// If the error resolution was not successful we should not resolve further.
			if (resultCode != Activity.RESULT_OK) {
				shouldResolve = false;
			}
			isResolving = false;
			googleApiClient.connect();
		} else if (requestCode == RESULT_CODE_AUTH_CODE) {
			Log.d(DIALOG_FRAGMENT_TAG, "offline access approved?");
			requestGoogleAuthorizationCode();
		}

		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConnected(Bundle bundle) {
		connectionProgressDialog.dismiss();
		shouldResolve = false;
		if(triggerGPlusLogin) {
			triggerGPlusLogin = false;
			triggerGPlusLogin();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(DIALOG_FRAGMENT_TAG, "onConnectionSuspended:" + i);
		googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		//if (connectionProgressDialog.isShowing() || true) {
		// Could not connect to Google Play Services.  The user needs to select an account,
		// grant permissions or resolve an error in order to sign in. Refer to the javadoc for
		// ConnectionResult to see possible error codes.
		Log.d(DIALOG_FRAGMENT_TAG, "onConnectionFailed:" + connectionResult);

		if (shouldResolve && !isResolving) {
			if (connectionResult.hasResolution()) {
				try {
					connectionResult.startResolutionForResult(getActivity(), GPLUS_REQUEST_CODE_SIGN_IN);
					isResolving = true;
				} catch (IntentSender.SendIntentException e) {
					Log.e(DIALOG_FRAGMENT_TAG, "Could not resolve ConnectionResult.", e);
					isResolving = false;
					googleApiClient.connect();
				}
			} else {
				// Could not resolve the connection result, show the user an
				// error dialog.
				new AlertDialog.Builder(getActivity()).setTitle(R.string.error)
						.setMessage(R.string.sign_in_error).setPositiveButton(R.string.ok, null).show();
			}
		} /*else {
			// Show the signed-out UI
			showSignedOutUI();
		}*/
	}

	/**
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
	/*
	public void handleException(final Exception e) {
		// Because this call comes from the AsyncTask, we must ensure that the following
		// code instead executes on the UI thread.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (e instanceof GooglePlayServicesAvailabilityException) {
					// The Google Play services APK is old, disabled, or not present.
					// Show a dialog created by Google Play services that allows
					// the user to update the APK
					int statusCode = ((GooglePlayServicesAvailabilityException) e)
							.getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
							SignInDialog.this.getActivity(),
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					// Unable to authenticate, such as when the user has not yet granted
					// the app access to the account, but the user can fix this.
					// Forward the user to an activity in Google Play services.
					Intent intent = ((UserRecoverableAuthException) e).getIntent();
					startActivityForResult(intent,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}
	*/
	@Override
	public void onGetFacebookUserInfoComplete(GraphResponse response) {
		Log.d("FB", "User Info complete");
		JSONObject responseObject = response.getJSONObject();
		try {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			sharedPreferences.edit().putString(Constants.FACEBOOK_ID, responseObject.getString("id")).commit();
			sharedPreferences.edit().putString(Constants.FACEBOOK_USERNAME, responseObject.getString("name")).commit();
			sharedPreferences.edit().putString(Constants.FACEBOOK_LOCALE, responseObject.getString("locale")).commit();
			//if user has approved email permission, fb-email address is taken, else device email address
			if(responseObject.has("email")) {
				sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, responseObject.getString("email")).commit();
			} else {
				sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, UtilDeviceInfo.getUserEmail(getActivity()))
						.commit();
			}

			CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(getActivity(), responseObject.getString
					("id"), Constants.FACEBOOK);
			checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
			checkOAuthTokenTask.execute();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckOAuthTokenComplete(Boolean tokenAvailable, String provider) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (provider.equals(Constants.FACEBOOK)) {
			if (tokenAvailable) {
				FacebookLogInTask facebookLogInTask = new FacebookLogInTask(getActivity(),
						sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
						sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
						sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
						sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
				);
				facebookLogInTask.setOnFacebookLogInCompleteListener(this);
				facebookLogInTask.execute();
			} else {
				CheckEmailAvailableTask checkEmailAvailableTask = new CheckEmailAvailableTask(getActivity(),
						sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL), provider);
				checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this);
				checkEmailAvailableTask.execute();
			}
		} else if (provider.equals(Constants.GOOGLE_PLUS)) {
			if (tokenAvailable) {
				GoogleLogInTask googleLogInTask = new GoogleLogInTask(getActivity(),
						sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
						sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
						sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
						sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE));
				googleLogInTask.setOnGoogleLogInCompleteListener(this);
				googleLogInTask.execute();
			} else {
				String email = sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL);
				CheckEmailAvailableTask checkEmailAvailableTask = new CheckEmailAvailableTask(getActivity(),
						email, provider);
				checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this);
				checkEmailAvailableTask.execute();
			}
		}
	}

	@Override
	public void onCheckEmailAvailableComplete(Boolean emailAvailable, String provider) {
		if (provider.equals(Constants.FACEBOOK)) {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (emailAvailable) {
				FacebookExchangeTokenTask facebookExchangeTokenTask = new FacebookExchangeTokenTask(getActivity(),
						AccessToken.getCurrentAccessToken().getToken(),
						sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
						sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
						sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
						sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
				);
				facebookExchangeTokenTask.setOnFacebookExchangeTokenCompleteListener(this);
				facebookExchangeTokenTask.execute();
			} else {
				showOauthUserNameDialog(Constants.FACEBOOK);
			}
		} else if (provider.equals(Constants.GOOGLE_PLUS)) {
			if (emailAvailable) {
				requestGoogleAuthorizationCode();
			} else {
				showOauthUserNameDialog(Constants.GOOGLE_PLUS);
			}
		}
	}

	private void showOauthUserNameDialog(String provider) {
		OAuthUsernameDialog oAuthUsernameDialog = new OAuthUsernameDialog();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, provider);
		oAuthUsernameDialog.setArguments(bundle);
		oAuthUsernameDialog.setSignInDialog(this);
		oAuthUsernameDialog.show(getActivity().getSupportFragmentManager(), OAuthUsernameDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
	}

	@Override
	public void onFacebookExchangeTokenComplete() {
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
		UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.FACEBOOK);
		uploadProjectDialog.setArguments(bundle);
		uploadProjectDialog.show(getFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onGoogleLogInComplete() {
		dismiss();
		UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.GOOGLE_PLUS);
		uploadProjectDialog.setArguments(bundle);
		uploadProjectDialog.show(getFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	@Override
	public void onGoogleFetchCodeComplete(String code) {
		Log.d(DIALOG_FRAGMENT_TAG, "code:" + code);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		GoogleExchangeCodeTask googleExchangeCodeTask = new GoogleExchangeCodeTask(getActivity(),
				code,
				sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
				sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
				sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
				sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE));
		googleExchangeCodeTask.setOnGoogleExchangeCodeCompleteListener(this);
		googleExchangeCodeTask.execute();
	}

	@Override
	public void onGoogleExchangeCodeComplete() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		GoogleLogInTask googleLogInTask = new GoogleLogInTask(getActivity(),
				sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
				sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
				sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
				sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE));
		googleLogInTask.setOnGoogleLogInCompleteListener(this);
		googleLogInTask.execute();
	}
}
