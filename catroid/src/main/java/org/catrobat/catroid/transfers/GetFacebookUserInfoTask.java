/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.web.UtilWebConnection;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;
import org.json.JSONException;
import org.json.JSONObject;

public class GetFacebookUserInfoTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = GetFacebookUserInfoTask.class.getSimpleName();

	private Activity activity;
	private ProgressDialog progressDialog;
	private String token;
	private final String facebookId;

	private WebconnectionException exception;

	private OnGetFacebookUserInfoTaskCompleteListener onGetFacebookUserInfoTaskCompleteListener;
	private String name;
	private String locale;
	private String email;
	private boolean facebookSessionExpired;

	public GetFacebookUserInfoTask(Activity activity, String token, String facebookId) {
		this.activity = activity;
		this.token = token;
		this.facebookId = facebookId;
	}

	public void setOnGetFacebookUserInfoTaskCompleteListener(OnGetFacebookUserInfoTaskCompleteListener listener) {
		onGetFacebookUserInfoTaskCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		String title = activity.getString(R.string.please_wait);
		String message = activity.getString(R.string.loading_check_facebook_data);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			if (!UtilWebConnection.isNetworkAvailable(activity)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return false;
			}
			JSONObject serverReponse = ServerCalls.getInstance().getFacebookUserInfo(facebookId, token);
			if (serverReponse == null) {
				return false;
			}
			try {
				if (serverReponse.has(Constants.JSON_ERROR_CODE)) {
					int errorCode = serverReponse.getInt(Constants.JSON_ERROR_CODE);
					if (errorCode == Constants.ERROR_CODE_FACEBOOK_SESSION_EXPIRED) {
						facebookSessionExpired = true;
					} else {
						exception = new WebconnectionException(WebconnectionException.ERROR_JSON, serverReponse.toString());
					}
				}
				if (serverReponse.has(Constants.USERNAME)) {
					name = serverReponse.getString(Constants.USERNAME);
				}
				if (serverReponse.has(Constants.EMAIL)) {
					email = serverReponse.getString(Constants.EMAIL);
				}
				if (serverReponse.has(Constants.LOCALE)) {
					locale = serverReponse.getString(Constants.LOCALE);
				}
			} catch (JSONException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}

			return true;
		} catch (WebconnectionException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
			exception = webconnectionException;
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (UtilWebConnection.checkForNetworkError(exception)) {
			UtilUi.showNoWebConnectionDialog(activity);
			return;
		}

		if (!success && exception != null) {
			showDialog(R.string.sign_in_error);
			return;
		}

		if (onGetFacebookUserInfoTaskCompleteListener != null) {
			if (facebookSessionExpired) {
				onGetFacebookUserInfoTaskCompleteListener.forceSignIn();
			} else {
				onGetFacebookUserInfoTaskCompleteListener.onGetFacebookUserInfoTaskComplete(facebookId, name, locale,
						email);
			}
		}
	}

	private void showDialog(int messageId) {
		if (activity == null) {
			return;
		}
		if (exception.getMessage() == null) {
			new CustomAlertDialogBuilder(activity).setMessage(messageId).setPositiveButton(R.string.ok, null)
					.show();
		} else {
			new CustomAlertDialogBuilder(activity).setMessage(exception.getMessage())
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnGetFacebookUserInfoTaskCompleteListener {
		void onGetFacebookUserInfoTaskComplete(String id, String name, String locale, String email);
		void forceSignIn();
	}
}
