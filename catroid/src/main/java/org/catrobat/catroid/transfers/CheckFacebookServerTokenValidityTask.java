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
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.UtilUi;
import org.catrobat.catroid.web.UtilWebConnection;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class CheckFacebookServerTokenValidityTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = CheckFacebookServerTokenValidityTask.class.getSimpleName();

	private Activity activity;
	private ProgressDialog progressDialog;
	private String id;

	private Boolean requestNewToken;

	private WebconnectionException exception;

	private OnCheckFacebookServerTokenValidityCompleteListener onCheckFacebookServerTokenValidityCompleteListener;

	public CheckFacebookServerTokenValidityTask(Activity activity, String id) {
		this.activity = activity;
		this.id = id;
	}

	public void setOnCheckFacebookServerTokenValidityCompleteListener(OnCheckFacebookServerTokenValidityCompleteListener listener) {
		onCheckFacebookServerTokenValidityCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		String title = activity.getString(R.string.please_wait);
		String message = activity.getString(R.string.loading_check_facebook_token_validity);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if (!UtilWebConnection.isNetworkAvailable(activity)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return false;
			}
			requestNewToken = ServerCalls.getInstance().checkFacebookServerTokenValidity(id);
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

		if (onCheckFacebookServerTokenValidityCompleteListener != null) {
			onCheckFacebookServerTokenValidityCompleteListener.onCheckFacebookServerTokenValidityComplete(requestNewToken, activity);
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

	public interface OnCheckFacebookServerTokenValidityCompleteListener {
		void onCheckFacebookServerTokenValidityComplete(Boolean requestNewToken, Activity activity);
	}
}
