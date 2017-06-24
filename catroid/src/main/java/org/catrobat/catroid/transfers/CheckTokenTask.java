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

public class CheckTokenTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = CheckTokenTask.class.getSimpleName();

	private Activity activity;
	private ProgressDialog progressDialog;
	private String token;
	private String username;

	private WebconnectionException exception;

	private OnCheckTokenCompleteListener onCheckTokenCompleteListener;

	public CheckTokenTask(Activity activity, String token, String username) {
		this.activity = activity;
		this.token = token;
		this.username = username;
	}

	public void setOnCheckTokenCompleteListener(OnCheckTokenCompleteListener listener) {
		onCheckTokenCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		String title = activity.getString(R.string.please_wait);
		String message = activity.getString(R.string.loading_check_token);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if (!UtilWebConnection.isNetworkAvailable(activity)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return false;
			}

			return ServerCalls.getInstance().checkToken(token, username);
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

		if (!success && UtilWebConnection.checkForNetworkError(exception)) {
			UtilUi.showNoWebConnectionDialog(activity);
			return;
		}
		if (!success) {
			// token is not valid -> maybe password has changed
			if (onCheckTokenCompleteListener != null) {
				onCheckTokenCompleteListener.onTokenNotValid(activity);
			}

			return;
		}

		if (onCheckTokenCompleteListener != null) {
			onCheckTokenCompleteListener.onCheckTokenSuccess(activity);
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

	public interface OnCheckTokenCompleteListener {

		void onTokenNotValid(Activity activity);

		void onCheckTokenSuccess(Activity activity);
	}
}
