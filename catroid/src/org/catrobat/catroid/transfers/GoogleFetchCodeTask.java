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
package org.catrobat.catroid.transfers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

public class GoogleFetchCodeTask extends AsyncTask<Void, Void, String> {

	private static final String TAG = GoogleFetchCodeTask.class.getSimpleName();
	private static final Integer RESULT_CODE_AUTH_CODE = 1;

	private static String SERVER_CLIENT_ID = "427226922034-r016ige5kb30q9vflqbt1h0i3arng8u1.apps.googleusercontent.com";

	private SignInDialog signInDialog;
	private Context context;
	private ProgressDialog progressDialog;
	private String accountName;
	private String message;
	private String token;
	private boolean userPermissionNeededForAuthCode = false;
	private OnGoogleFetchCodeCompleteListener onGoogleFetchCodeCompleteListener;
	private boolean codePermissionRequested = false;

	public GoogleFetchCodeTask(Context context, String accountName, SignInDialog signInDialog) {
		this.context = context;
		this.accountName = accountName;
		this.signInDialog = signInDialog;
	}

	public void setOnGoogleFetchCodeCompleteListener(OnGoogleFetchCodeCompleteListener listener) {
		onGoogleFetchCodeCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.loading_google_fetch_code);
		progressDialog = ProgressDialog.show(context, title, message);
	}

	@Override
	protected String doInBackground(Void... arg0) {
		if (!Utils.isNetworkAvailable(context)) {
			return "";
		}

		/*
		ID Token:
		String accountName = Plus.AccountApi.getAccountName(googleApiClient);
		//Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.

		try {
			return GoogleAuthUtil.getToken(context, accountName, scopes);
			//} catch (UserRecoverableAuthException userRecoverableException) {
			//	userRecoverableException.printStackTrace();
			// GooglePlayServices.apk is either old, disabled, or not present
			// so we need to show the user some UI in the activity to recover.
			//context.handleException(userRecoverableException);
		} catch (GoogleAuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/

		Bundle authPreferences = new Bundle();
		//authPreferences.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES,
		//		"");
		String scopesString = Scopes.PROFILE;
		scopesString = "profile email";
		String scope = "oauth2:server:client_id:" + SERVER_CLIENT_ID + ":api_scope:" + scopesString;
		String code = null;
		//authPreferences.putBoolean(GoogleAuthUtil.KEY_SUPPRESS_PROGRESS_SCREEN, true);

		try {
			//Just invalidate previously obtained code on Google's server in case when a problem occurred and a new
			//code is needed. Otherwise, for 10 minutes the same (already redeemed) code will be returned.
			String previousCode = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants
					.REQUEST_GOOGLE_CODE, Constants.NO_TOKEN);
			if (!previousCode.equals(Constants.NO_TOKEN)) {
				GoogleAuthUtil.clearToken(context, previousCode);
			}

			code = GoogleAuthUtil.getToken(
					context,
					accountName,
					scope,
					authPreferences
			);

			PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constants.REQUEST_GOOGLE_CODE,
					code).commit();
		} catch (IOException transientEx) {
			// network or server error, the call is expected to succeed if you try again later.
			// Don't attempt to call again immediately - the request is likely to
			// fail, you'll hit quotas or back-off.
			Log.d(TAG, "Encountered an IOException while trying to login to Google+."
					+ " We'll need to try again at a later time.");
		} catch (UserRecoverableAuthException e) {
			userPermissionNeededForAuthCode = true;
			// Requesting an authorization code will always throw
			// UserRecoverableAuthException on the first call to GoogleAuthUtil.getToken
			// because the user must consent to offline access to their data.  After
			// consent is granted control is returned to your activity in onActivityResult
			// and the second call to GoogleAuthUtil.getToken will succeed.
			if (!codePermissionRequested) {
				codePermissionRequested = true;
				signInDialog.startActivityForResult(e.getIntent(), RESULT_CODE_AUTH_CODE);
				//context.startActivityForResult(e.getIntent(), RESULT_CODE_AUTH_CODE);
			}
		} catch (GoogleAuthException authEx) {
			// Failure. The call is not expected to ever succeed so it should not be
			// retried.
			Log.e(TAG, "Unable to authenticate to Google+.", authEx);
		}

		return code;
	}

	@Override
	protected void onPostExecute(String code) {
		super.onPostExecute(code);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (code == null) {
			if (!userPermissionNeededForAuthCode) {
				showDialog(R.string.error_internet_connection);
			}
			return;
		}

		if (context == null) {
			return;
		}

		if (onGoogleFetchCodeCompleteListener != null) {
			onGoogleFetchCodeCompleteListener.onGoogleFetchCodeComplete(code);
		}
	}

	private void showDialog(int messageId) {
		if (context == null) {
			return;
		}
		if (message == null) {
			new CustomAlertDialogBuilder(context).setTitle(R.string.register_error).setMessage(messageId)
					.setPositiveButton(R.string.ok, null).show();
		} else {
			new CustomAlertDialogBuilder(context).setTitle(R.string.register_error).setMessage(message)
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnGoogleFetchCodeCompleteListener {
		void onGoogleFetchCodeComplete(String code);
	}
}
