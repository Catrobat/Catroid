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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.*;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckEmailAvailableTask;
import org.catrobat.catroid.transfers.CheckFacebookTokenTask;
import org.catrobat.catroid.transfers.FacebookExchangeTokenTask;
import org.catrobat.catroid.transfers.FacebookLogInTask;
import org.catrobat.catroid.web.FacebookCalls;
import org.catrobat.catroid.web.GoogleCalls;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInDialog extends DialogFragment implements
		ConnectionCallbacks, OnConnectionFailedListener, FacebookLogInTask.OnFacebookLogInCompleteListener,
		FacebookCalls.OnGetFacebookUserInfoCompleteListener, CheckFacebookTokenTask.OnCheckFacebookTokenCompleteListener, CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener, FacebookExchangeTokenTask.OnFacebookExchangeTokenCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_sign_in";
	private static final int GPLUS_REQUEST_CODE_RESOLVE_ERR = 9000;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

	private Button loginButton;
	private Button registerButton;
	private LoginButton facebookLoginButton;
	private SignInButton gplusLoginButton;
	private TextView termsOfUseLinkTextView;
	private CallbackManager callbackManager;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {

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

		mPlusClient = new PlusClient.Builder(getActivity(), this, this)
				.setScopes(Scopes.PLUS_LOGIN)
				.build();
        //mPlusClient = new GoogleApiClient.Builder(getActivity())
        //        .addConnectionCallbacks(this)
        //        .addOnConnectionFailedListener(this)
        //        .addApi(Plus.API)
        //        .addScope(new Scope(Scopes.PLUS_LOGIN))
        //        .build();
		// Anzuzeigende Statusmeldung, wenn der Verbindungsfehler nicht behoben ist
		mConnectionProgressDialog = new ProgressDialog(getActivity());
		mConnectionProgressDialog.setMessage("Signing in to G+...");

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

		gplusLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.dialog_sign_in_gplus_login_button && !mPlusClient.isConnected()) {
					if (mConnectionResult == null) {
						mConnectionProgressDialog.show();
					} else {
						try {
							mConnectionResult.startResolutionForResult(getActivity(), GPLUS_REQUEST_CODE_RESOLVE_ERR);
						} catch (IntentSender.SendIntentException e) {
							// Versuchen Sie erneut, die Verbindung herzustellen.
							mConnectionResult = null;
							mPlusClient.connect();
						}
					}
				}
			}
		});

		return signInDialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		mPlusClient.disconnect();
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
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GPLUS_REQUEST_CODE_RESOLVE_ERR && resultCode == Activity.RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}

		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConnected(Bundle bundle) {
		// Wir haben alle Verbindungsfehler behoben.
		mConnectionProgressDialog.dismiss();
		String accountName = mPlusClient.getAccountName();
		Toast.makeText(getActivity(), accountName + " is connected.", Toast.LENGTH_LONG).show();
        String accessToken = "";
        GoogleCalls.getInstance().getGoogleToken(getActivity(), accountName);
	}

	@Override
	public void onDisconnected() {
		Log.d(DIALOG_FRAGMENT_TAG, "disconnected");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (mConnectionProgressDialog.isShowing() || true) {
			// Der Nutzer hat bereits auf die Anmeldeschaltfläche geklickt. Starten Sie mit der Behebung
			// von Verbindungsfehlern. Warten Sie, bis onConnected() den
			// Verbindungsdialog geschlossen hat.
			if (connectionResult.hasResolution()) {
				try {
					connectionResult.startResolutionForResult(getActivity(), GPLUS_REQUEST_CODE_RESOLVE_ERR);
				} catch (IntentSender.SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}

		// Speichern Sie die Absicht, damit wir eine Aktivität starten können, wenn der Nutzer auf
		// die Anmeldeschaltfläche klickt.
		mConnectionResult = connectionResult;
	}

	/**
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
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

	@Override
	public void onGetFacebookUserInfoComplete(GraphResponse response) {
		Log.d("FB", "User Info complete");
		JSONObject responseObject = response.getJSONObject();
		try {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			sharedPreferences.edit().putString(Constants.FACEBOOK_ID, responseObject.getString("id")).commit();
			sharedPreferences.edit().putString(Constants.FACEBOOK_USERNAME, responseObject.getString("name")).commit();
			sharedPreferences.edit().putString(Constants.FACEBOOK_LOCALE, responseObject.getString("locale")).commit();
			sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, responseObject.getString("email")).commit();

			CheckFacebookTokenTask checkFacebookTokenTask = new CheckFacebookTokenTask(getActivity(), responseObject.getString("id"));
			checkFacebookTokenTask.setOnCheckFacebookTokenCompleteListener(this);
			checkFacebookTokenTask.execute();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckFacebookTokenComplete(Boolean tokenAvailable) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(tokenAvailable) {
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
					sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL));
			checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this);
			checkEmailAvailableTask.execute();
		}
	}

	@Override
	public void onCheckEmailAvailableComplete(Boolean emailAvailable) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(emailAvailable) {
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
			OAuthUsernameDialog oAuthUsernameDialog = new OAuthUsernameDialog();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.CUR_OAUTH_PROVIDER, Constants.FACEBOOK);
            oAuthUsernameDialog.setArguments(bundle);
			oAuthUsernameDialog.show(getActivity().getSupportFragmentManager(), OAuthUsernameDialog.DIALOG_FRAGMENT_TAG);
			dismiss();
		}
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
        uploadProjectDialog.show(getFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

}
