/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.CheckEmailAvailableTask;
import org.catrobat.catroid.transfers.CheckOAuthTokenTask;
import org.catrobat.catroid.transfers.FacebookExchangeTokenTask;
import org.catrobat.catroid.transfers.FacebookLogInTask;
import org.catrobat.catroid.transfers.GetFacebookUserInfoTask;
import org.catrobat.catroid.transfers.GoogleExchangeCodeTask;
import org.catrobat.catroid.transfers.GoogleLogInTask;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;

import java.util.Arrays;
import java.util.Collection;

public class SignInDialog extends DialogFragment implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		GoogleExchangeCodeTask.OnFacebookExchangeCodeCompleteListener,
		GoogleLogInTask.OnGoogleServerLogInCompleteListener,
		FacebookExchangeTokenTask.OnFacebookExchangeTokenCompleteListener,
		FacebookLogInTask.OnFacebookLogInCompleteListener,
		CheckOAuthTokenTask.OnCheckOAuthTokenCompleteListener,
		CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener,
		GetFacebookUserInfoTask.OnGetFacebookUserInfoTaskCompleteListener {
	private static final String TAG = SignInDialog.class.getSimpleName();

	public static final String DIALOG_FRAGMENT_TAG = "dialog_sign_in";
	private static final int GPLUS_REQUEST_CODE_SIGN_IN = 0;
	private static final Integer RESULT_CODE_AUTH_CODE = 1;
	private static final String FACEBOOK_PROFILE_PERMISSION = "public_profile";
	private static final String FACEBOOK_EMAIL_PERMISSION = "email";
	private static final java.lang.String GOOGLE_PLUS_CATROWEB_SERVER_CLIENT_ID = "427226922034-r016ige5kb30q9vflqbt1h0i3arng8u1.apps.googleusercontent.com";

	private ProgressDialog connectionProgressDialog;
	private GoogleApiClient googleApiClient;
	private boolean shouldResolveErrors = false;
	private boolean isResolving = false;
	private boolean triggerGPlusLogin = false;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		if (getActivity() instanceof MainMenuActivity) {
			((MainMenuActivity) getActivity()).setSignInDialog(this);
		} else if (getActivity() instanceof ProjectActivity) {
			((ProjectActivity) getActivity()).setSignInDialog(this);
		}

		initializeGooglePlus();

		View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sign_in, null);

		final Button loginButton = (Button) rootView.findViewById(R.id.dialog_sign_in_login);
		final Button registerButton = (Button) rootView.findViewById(R.id.dialog_sign_in_register);
		final Button facebookLoginButton = (Button) rootView.findViewById(R.id.dialog_sign_in_facebook_login_button);
		Button gplusLoginButton = (Button) rootView.findViewById(R.id.dialog_sign_in_gplus_login_button);
		TextView termsOfUseLinkTextView = (TextView) rootView.findViewById(R.id.register_terms_link);

		facebookLoginButton.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int width = facebookLoginButton.getWidth();
						if (width > 0) {
							loginButton.getLayoutParams().width = width;
							registerButton.getLayoutParams().width = width;
							facebookLoginButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}
					}
				});

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

		LoginManager.getInstance().setLoginBehavior(ServerCalls.getInstance().getLoginBehavior());

		facebookLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utils.isNetworkAvailable(getActivity())) {
					Collection<String> permissions = Arrays.asList(FACEBOOK_PROFILE_PERMISSION, FACEBOOK_EMAIL_PERMISSION);
					LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissions);
				} else {
					Utils.isNetworkAvailable(getActivity(), true);
				}
			}
		});

		gplusLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Utils.isNetworkAvailable(getActivity())) {
					handleGooglePlusLoginButtonClick(view);
				} else {
					Utils.isNetworkAvailable(getActivity(), true);
				}
			}
		});

		signInDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(DIALOG_FRAGMENT_TAG, "onShow() Activity was null!");
					return;
				}
				TextSizeUtil.enlargeViewGroup((ViewGroup) signInDialog.getWindow().getDecorView().getRootView());
			}
		});

		return signInDialog;
	}

	private void initializeGooglePlus() {
		GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.requestServerAuthCode(GOOGLE_PLUS_CATROWEB_SERVER_CLIENT_ID, false)
				.requestIdToken(GOOGLE_PLUS_CATROWEB_SERVER_CLIENT_ID)
				.build();

		googleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
				.build();

		connectionProgressDialog = new ProgressDialog(getActivity());
		connectionProgressDialog.setMessage("Trying to sign in to Google+");
	}

	private void handleGooglePlusLoginButtonClick(View view) {
		if (view.getId() == R.id.dialog_sign_in_gplus_login_button) {
			if (!googleApiClient.isConnected()) {
				shouldResolveErrors = true;
				googleApiClient.connect();
				triggerGPlusLogin = true;
			} else {
				Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
				startActivityForResult(signInIntent, MainMenuActivity.REQUEST_CODE_GOOGLE_PLUS_SIGNIN);
			}
		}
	}

	public void onGoogleLogInComplete(GoogleSignInAccount account) {
		String id = account.getId();
		String personName = account.getDisplayName();
		String email = account.getEmail();
		String locale = UtilDeviceInfo.getUserCountryCode();
		String idToken = account.getIdToken();
		String code = account.getServerAuthCode();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.GOOGLE_ID, id).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_USERNAME, personName).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_EMAIL, email).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_LOCALE, locale).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_ID_TOKEN, idToken).commit();
		sharedPreferences.edit().putString(Constants.GOOGLE_EXCHANGE_CODE, code).commit();

		CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(getActivity(), id, Constants.GOOGLE_PLUS);
		checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
		checkOAuthTokenTask.execute();
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
		if (!Utils.isNetworkAvailable(getActivity(), true)) {
			return;
		}

		LogInDialog logInDialog = new LogInDialog();
		logInDialog.show(getActivity().getFragmentManager(), LogInDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
	}

	private void handleRegisterButtonClick() {
		if (!Utils.isNetworkAvailable(getActivity(), true)) {
			return;
		}

		RegistrationDialog registrationDialog = new RegistrationDialog();
		registrationDialog.show(getActivity().getFragmentManager(), RegistrationDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GPLUS_REQUEST_CODE_SIGN_IN) {
			// If the error resolution was not successful we should not resolve further.
			if (resultCode != Activity.RESULT_OK) {
				shouldResolveErrors = false;
			}
			isResolving = false;
			googleApiClient.connect();
		} else if (requestCode == RESULT_CODE_AUTH_CODE) {
			Log.d(DIALOG_FRAGMENT_TAG, "offline access approved?");
		} else if (requestCode == MainMenuActivity.REQUEST_CODE_GOOGLE_PLUS_SIGNIN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			triggerGPlusLogin(result);
		}
	}

	private void triggerGPlusLogin(GoogleSignInResult result) {
		if (result.isSuccess()) {
			GoogleSignInAccount account = result.getSignInAccount();
			onGoogleLogInComplete(account);
		} else {
			ToastUtil.showError(getActivity(), "There was a problem during Google+ Signin. Status: "
					+ result.getStatus());
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		connectionProgressDialog.dismiss();
		shouldResolveErrors = false;

		if (triggerGPlusLogin) {
			triggerGPlusLogin = false;
			Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
			startActivityForResult(signInIntent, MainMenuActivity.REQUEST_CODE_GOOGLE_PLUS_SIGNIN);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(DIALOG_FRAGMENT_TAG, "onConnectionSuspended:" + i);
		googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Could not connect to Google Play Services.  The user needs to select an account,
		// grant permissions or resolve an error in order to sign in.
		Log.d(DIALOG_FRAGMENT_TAG, "onConnectionFailed:" + connectionResult);

		if (shouldResolveErrors && !isResolving) {
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
				new AlertDialog.Builder(getActivity()).setTitle(R.string.error)
						.setMessage(R.string.sign_in_error).setPositiveButton(R.string.ok, null).show();
			}
		}
	}

	@Override
	public void onCheckOAuthTokenComplete(Boolean tokenAvailable, String provider) {
		if (provider.equals(Constants.FACEBOOK)) {
			checkOAuthTokenFacebookComplete(tokenAvailable);
		} else if (provider.equals(Constants.GOOGLE_PLUS)) {
			checkOAuthTokenGoogleComplete(tokenAvailable);
		}
	}

	private void checkOAuthTokenFacebookComplete(boolean tokenAvailable) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
					sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL), Constants.FACEBOOK);
			checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this);
			checkEmailAvailableTask.execute();
		}
	}

	private void checkOAuthTokenGoogleComplete(boolean tokenAvailable) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (tokenAvailable) {
			GoogleLogInTask googleLogInTask = new GoogleLogInTask(getActivity(),
					sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
					sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
					sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
					sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE));
			googleLogInTask.setOnGoogleServerLogInCompleteListener(this);
			googleLogInTask.execute();
		} else {
			String email = sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL);
			CheckEmailAvailableTask checkEmailAvailableTask = new CheckEmailAvailableTask(getActivity(),
					email, Constants.GOOGLE_PLUS);
			checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this);
			checkEmailAvailableTask.execute();
		}
	}

	@Override
	public void onCheckEmailAvailableComplete(Boolean emailAvailable, String provider) {
		if (provider.equals(Constants.FACEBOOK)) {
			checkEmailAvailableCompleteFacebook(emailAvailable);
		} else if (provider.equals(Constants.GOOGLE_PLUS)) {
			checkEmailAvailableCompleteGoogle(emailAvailable);
		}
	}

	private void checkEmailAvailableCompleteFacebook(boolean emailAvailable) {
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
	}

	private void checkEmailAvailableCompleteGoogle(boolean emailAvailable) {
		if (emailAvailable) {
			exchangeGoogleAuthorizationCode();
		} else {
			showOauthUserNameDialog(Constants.GOOGLE_PLUS);
		}
	}

	public void exchangeGoogleAuthorizationCode() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

	private void showOauthUserNameDialog(String provider) {
		OAuthUsernameDialog oAuthUsernameDialog = new OAuthUsernameDialog();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, provider);
		oAuthUsernameDialog.setArguments(bundle);
		oAuthUsernameDialog.show(getActivity().getFragmentManager(), OAuthUsernameDialog.DIALOG_FRAGMENT_TAG);
		dismiss();
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
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.FACEBOOK);
		ProjectManager.getInstance().signInFinished(getFragmentManager(), bundle);
	}

	@Override
	public void onGoogleServerLogInComplete() {
		dismiss();

		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.GOOGLE_PLUS);
		ProjectManager.getInstance().signInFinished(getFragmentManager(), bundle);
	}

	@Override
	public void onGoogleExchangeCodeComplete() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		GoogleLogInTask googleLogInTask = new GoogleLogInTask(getActivity(),
				sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL),
				sharedPreferences.getString(Constants.GOOGLE_USERNAME, Constants.NO_GOOGLE_USERNAME),
				sharedPreferences.getString(Constants.GOOGLE_ID, Constants.NO_GOOGLE_ID),
				sharedPreferences.getString(Constants.GOOGLE_LOCALE, Constants.NO_GOOGLE_LOCALE));
		googleLogInTask.setOnGoogleServerLogInCompleteListener(this);
		googleLogInTask.execute();
	}

	@Override
	public void onGetFacebookUserInfoTaskComplete(String id, String name, String locale, String email) {
		Log.d(TAG, "FB User Info complete");
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPreferences.edit().putString(Constants.FACEBOOK_ID, id).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_USERNAME, name).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_LOCALE, locale).commit();

		//if user has approved email permission, fb-email address is taken, else device email address
		if (email != null) {
			sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, email).commit();
		} else {
			sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, UtilDeviceInfo.getUserEmail(getActivity()))
					.commit();
		}

		CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(getActivity(), id, Constants.FACEBOOK);
		checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
		checkOAuthTokenTask.execute();
	}

	@Override
	public void forceSignIn() {
		SignInDialog signInDialog = new SignInDialog();
		signInDialog.show(getActivity().getFragmentManager(), SignInDialog.DIALOG_FRAGMENT_TAG);
	}
}
