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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.recyclerview.dialog.login.OAuthUsernameDialogFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.login.SignInCompleteListener;
import org.catrobat.catroid.utils.DeviceSettingsProvider;
import org.catrobat.catroid.utils.ToastUtil;

public class FacebookLoginHandler implements FacebookCallback<LoginResult>,
		GetFacebookUserInfoTask.OnGetFacebookUserInfoTaskCompleteListener,
		CheckOAuthTokenTask.OnCheckOAuthTokenCompleteListener,
		FacebookLogInTask.OnFacebookLogInCompleteListener,
		FacebookExchangeTokenTask.OnFacebookExchangeTokenCompleteListener,
		CheckEmailAvailableTask.OnCheckEmailAvailableCompleteListener {

	private Activity activity;

	public static final String FACEBOOK_PROFILE_PERMISSION = "public_profile";
	public static final String FACEBOOK_EMAIL_PERMISSION = "email";

	public FacebookLoginHandler(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onSuccess(LoginResult loginResult) {
		AccessToken accessToken = loginResult.getAccessToken();
		GetFacebookUserInfoTask getFacebookUserInfoTask = new GetFacebookUserInfoTask(activity, accessToken.getToken(),
				accessToken.getUserId());
		getFacebookUserInfoTask.setOnGetFacebookUserInfoTaskCompleteListener(this);
		getFacebookUserInfoTask.execute();
	}

	@Override
	public void onCancel() {
		((SignInCompleteListener) activity).onLoginCancel();
	}

	@Override
	public void onError(FacebookException error) {
		((SignInCompleteListener) activity).onLoginCancel();
	}

	@Override
	public void onGetFacebookUserInfoTaskComplete(String id, String name, String locale, String email) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		sharedPreferences.edit().putString(Constants.FACEBOOK_ID, id).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_USERNAME, name).commit();
		sharedPreferences.edit().putString(Constants.FACEBOOK_LOCALE, locale).commit();

		if (email != null) {
			sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL, email).commit();
		} else {
			sharedPreferences.edit().putString(Constants.FACEBOOK_EMAIL,
					DeviceSettingsProvider.getUserEmail(activity)).commit();
		}

		CheckOAuthTokenTask checkOAuthTokenTask = new CheckOAuthTokenTask(activity, id, Constants.FACEBOOK);
		checkOAuthTokenTask.setOnCheckOAuthTokenCompleteListener(this);
		checkOAuthTokenTask.execute();
	}

	@Override
	public void forceSignIn() {
		ToastUtil.showError(activity, activity.getString(R.string.error_session_expired));
	}

	@Override
	public void onCheckOAuthTokenComplete(Boolean tokenAvailable, String provider) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		if (tokenAvailable) {
			FacebookLogInTask facebookLogInTask = new FacebookLogInTask(activity,
					sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
					sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
					sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
					sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
			);
			facebookLogInTask.setOnFacebookLogInCompleteListener(this);
			facebookLogInTask.execute();
		} else {
			CheckEmailAvailableTask checkEmailAvailableTask = new CheckEmailAvailableTask(sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL), Constants.FACEBOOK);
			checkEmailAvailableTask.setOnCheckEmailAvailableCompleteListener(this);
			checkEmailAvailableTask.execute();
		}
	}

	@Override
	public void onCheckEmailAvailableComplete(Boolean emailAvailable, String provider) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		if (emailAvailable) {
			FacebookExchangeTokenTask facebookExchangeTokenTask = new FacebookExchangeTokenTask(activity,
					AccessToken.getCurrentAccessToken().getToken(),
					sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
					sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
					sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
					sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
			);
			facebookExchangeTokenTask.setOnFacebookExchangeTokenCompleteListener(this);
			facebookExchangeTokenTask.execute();
		} else {
			OAuthUsernameDialogFragment dialog = new OAuthUsernameDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, provider);
			dialog.setArguments(bundle);
			dialog.setSignInCompleteListener((SignInCompleteListener) activity);
			dialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), OAuthUsernameDialogFragment.TAG);
		}
	}

	@Override
	public void onFacebookLogInComplete() {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.CURRENT_OAUTH_PROVIDER, Constants.FACEBOOK);
		((SignInCompleteListener) activity).onLoginSuccessful(bundle);
	}

	@Override
	public void onFacebookExchangeTokenComplete(Activity activity) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

		FacebookLogInTask facebookLogInTask = new FacebookLogInTask(activity,
				sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL),
				sharedPreferences.getString(Constants.FACEBOOK_USERNAME, Constants.NO_FACEBOOK_USERNAME),
				sharedPreferences.getString(Constants.FACEBOOK_ID, Constants.NO_FACEBOOK_ID),
				sharedPreferences.getString(Constants.FACEBOOK_LOCALE, Constants.NO_FACEBOOK_LOCALE)
		);
		facebookLogInTask.setOnFacebookLogInCompleteListener(this);
		facebookLogInTask.execute();
	}
}
