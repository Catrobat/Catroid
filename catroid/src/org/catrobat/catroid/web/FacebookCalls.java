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
package org.catrobat.catroid.web;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;

import org.json.JSONObject;

public final class FacebookCalls {

	private static final String TAG = FacebookCalls.class.getSimpleName();

	private static final FacebookCalls INSTANCE = new FacebookCalls();

	private static LoginBehavior loginBehavior = LoginBehavior.NATIVE_WITH_FALLBACK;

	private FacebookCalls() {

	}

	public static FacebookCalls getInstance() {
		return INSTANCE;
	}

	private OnGetFacebookUserInfoCompleteListener onGetFacebookUserInfoCompleteListener;

	public interface OnGetFacebookUserInfoCompleteListener {
		void onGetFacebookUserInfoComplete(GraphResponse response);
	}

	public void setOnGetFacebookUserInfoCompleteListener(OnGetFacebookUserInfoCompleteListener listener) {
		onGetFacebookUserInfoCompleteListener = listener;
	}

	public void getFacebookUserInfo(AccessToken accessToken) {
		GraphRequest request = GraphRequest.newMeRequest(
				accessToken,
				new GraphRequest.GraphJSONObjectCallback() {
					@Override
					public void onCompleted(
							JSONObject object,
							GraphResponse response) {
						Log.d("FB", response.toString());
						if (onGetFacebookUserInfoCompleteListener != null) {
							onGetFacebookUserInfoCompleteListener.onGetFacebookUserInfoComplete(response);
						}
					}
				});
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id,name,locale,email");
		request.setParameters(parameters);
		request.executeAsync();
	}

	public LoginBehavior getLoginBehavior() {
		return loginBehavior;
	}

	public void setLoginBehavior(LoginBehavior loginBehavior) {
		FacebookCalls.loginBehavior = loginBehavior;
	}

}
