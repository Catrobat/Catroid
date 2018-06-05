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

package org.catrobat.catroid.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;

import org.catrobat.catroid.R;
import org.catrobat.catroid.transfers.CatroidFacebookCallbackMadnessHandler;
import org.catrobat.catroid.transfers.CatroidGooglePlusCallbackMadnessHandler;
import org.catrobat.catroid.ui.recyclerview.dialog.PrivacyPolicyDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.LoginDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.RegistrationDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.Arrays;
import java.util.Collection;

import static org.catrobat.catroid.transfers.CatroidFacebookCallbackMadnessHandler.FACEBOOK_EMAIL_PERMISSION;
import static org.catrobat.catroid.transfers.CatroidFacebookCallbackMadnessHandler.FACEBOOK_PROFILE_PERMISSION;
import static org.catrobat.catroid.transfers.CatroidGooglePlusCallbackMadnessHandler.REQUEST_CODE_GOOGLE_PLUS_SIGNIN;

public class SignInActivity extends BaseActivity implements SignInCompleteListener {

	private static final String SHARED_PREFERENCES_PRIVACY_POLICY_KEY = "USER_ACCEPTED_PRIVACY_POLICY";

	public static final int LOGIN_SUCCESSFUL_KEY = 23498;
	public static final String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";

	private CallbackManager facebookCallbackManager;

	private CatroidGooglePlusCallbackMadnessHandler catroidGooglePlusCallbackMadnessHandler;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_sign_in);
		setUpFacebookCallbackManager();
		setUpGooglePlus();
	}

	public void setUpFacebookCallbackManager() {
		facebookCallbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(facebookCallbackManager, new CatroidFacebookCallbackMadnessHandler(this));
	}

	public void setUpGooglePlus() {
		catroidGooglePlusCallbackMadnessHandler = new CatroidGooglePlusCallbackMadnessHandler(this);
	}

	@Override
	public void onStop() {
		catroidGooglePlusCallbackMadnessHandler.getGoogleApiClient().disconnect();
		super.onStop();
	}

	public void onButtonClick(final View view) {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isNetworkAvailable = connectivityManager != null &&
				connectivityManager.getActiveNetworkInfo().isConnected();

		if (!isNetworkAvailable) {
			ToastUtil.showError(this, R.string.error_internet_connection);
			return;
		}

		boolean privacyDialogAccepted = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean(SHARED_PREFERENCES_PRIVACY_POLICY_KEY, false);
		if (!privacyDialogAccepted) {
			PrivacyPolicyDialogFragment.PrivacyPolicyListener privacyPolicyListener = new PrivacyPolicyDialogFragment.PrivacyPolicyListener() {
				@Override
				public void onPrivacyPolicyAccepted() {
					onButtonClickForRealThisTime(view);
				}
			};
			new PrivacyPolicyDialogFragment(privacyPolicyListener, true)
					.show(getFragmentManager(), PrivacyPolicyDialogFragment.TAG);
		} else {
			onButtonClickForRealThisTime(view);
		}
	}

	private void onButtonClickForRealThisTime(View view) {
		switch (view.getId()) {
			case R.id.sign_in_login:
				LoginDialogFragment logInDialog = new LoginDialogFragment();
				logInDialog.setSignInCompleteListener(this);
				logInDialog.show(getFragmentManager(), LoginDialogFragment.TAG);
				break;
			case R.id.sign_in_register:
				RegistrationDialogFragment registrationDialog = new RegistrationDialogFragment();
				registrationDialog.setSignInCompleteListener(this);
				registrationDialog.show(getFragmentManager(), RegistrationDialogFragment.TAG);
				break;
			case R.id.sign_in_facebook_login_button:
				Collection<String> permissions = Arrays.asList(FACEBOOK_PROFILE_PERMISSION,	FACEBOOK_EMAIL_PERMISSION);
				LoginManager.getInstance().logInWithReadPermissions(this, permissions);
				break;
			case R.id.sign_in_gplus_login_button:
				catroidGooglePlusCallbackMadnessHandler.getGoogleApiClient().connect();
				Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(catroidGooglePlusCallbackMadnessHandler.getGoogleApiClient());
				startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_PLUS_SIGNIN);
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
		catroidGooglePlusCallbackMadnessHandler.onActivityResult(requestCode, resultCode, data);

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLoginSuccessful(Bundle bundle) {
		Intent intent = new Intent();
		intent.putExtra(LOGIN_SUCCESSFUL, bundle);
		setResult(LOGIN_SUCCESSFUL_KEY , intent);
		finish();
	}

	@Override
	public void onLoginCancel() {
	}
}
