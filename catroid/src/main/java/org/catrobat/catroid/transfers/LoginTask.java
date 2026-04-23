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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.ServerAuthenticator;

import java.lang.ref.WeakReference;

public class LoginTask extends AsyncTask<Void, Void, Void> {

	private static final String TAG = LoginTask.class.getSimpleName();

	private WeakReference<Context> contextWeakReference;
	private ProgressDialog progressDialog;
	private String username;
	private String password;

	private String message;
	private boolean userLoggedIn = false;

	private OnLoginListener onLoginListener;

	public LoginTask(Context activity, String username, String password) {
		this.contextWeakReference = new WeakReference<>(activity);
		this.username = username;
		this.password = password;
	}

	public void setOnLoginListener(OnLoginListener listener) {
		onLoginListener = listener;
	}

	@Override
	protected void onPreExecute() {
		Context context = contextWeakReference.get();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.loading);
		progressDialog = ProgressDialog.show(context, title, message);
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		Context context = contextWeakReference.get();

		if (context == null) {
			return null;
		}

		if (!Utils.checkIsNetworkAvailableAndShowErrorMessage(context)) {
			userLoggedIn = false;
			return null;
		}
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		Log.d(TAG, token);

		ServerAuthenticator authenticator = new ServerAuthenticator(username, password, token,
				CatrobatWebClient.INSTANCE.getClient(),
				FlavoredConstants.BASE_URL_HTTPS,
				sharedPreferences,
				new ServerAuthenticator.TaskListener() {
					@Override
					public void onError(int statusCode, String errorMessage) {
						Log.e(TAG, "StatusCode: " + statusCode + errorMessage);
						message = errorMessage;
						userLoggedIn = false;
					}

					@Override
					public void onSuccess() {
						userLoggedIn = true;
					}
				});
		authenticator.performCatrobatLogin();
		return null;
	}

	@Override
	protected void onPostExecute(Void arg) {
		Context context = contextWeakReference.get();
		if (context == null) {
			return;
		}

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (userLoggedIn) {
			ToastUtil.showSuccess(context, R.string.user_logged_in);
			if (onLoginListener != null) {
				onLoginListener.onLoginComplete();
			}
			return;
		}

		if (message == null) {
			message = context.getString(R.string.sign_in_error);
		}
		onLoginListener.onLoginFailed(message);
		Log.e(TAG, message);
	}

	public interface OnLoginListener {

		void onLoginComplete();

		void onLoginFailed(String msg);
	}
}
