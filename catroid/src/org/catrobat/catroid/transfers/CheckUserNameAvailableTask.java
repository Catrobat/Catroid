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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class CheckUserNameAvailableTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = CheckUserNameAvailableTask.class.getSimpleName();

	private Activity activity;
	private ProgressDialog progressDialog;
	private String username;

	private Boolean userNameAvailable;

	private WebconnectionException exception;

	private OnCheckUserNameAvailableCompleteListener onCheckUserNameAvailableCompleteListener;

	public CheckUserNameAvailableTask(Activity activity, String username) {
		this.activity = activity;
		this.username = username;
	}

	public void setOnCheckUserNameAvailableCompleteListener(OnCheckUserNameAvailableCompleteListener listener) {
		onCheckUserNameAvailableCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		String title = activity.getString(R.string.please_wait);
		String message = activity.getString(R.string.loading_check_oauth_username);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if (!Utils.isNetworkAvailable(activity)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return false;
			}

			userNameAvailable = ServerCalls.getInstance().checkUserNameAvailable(username);
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

		if (Utils.checkForNetworkError(success, exception)) {
			showDialog(R.string.error_internet_connection);
			return;
		}

		if (!success && exception != null) {
			showDialog(R.string.sign_in_error);
			return;
		}

		if (onCheckUserNameAvailableCompleteListener != null) {
			onCheckUserNameAvailableCompleteListener.onCheckUserNameAvailableComplete(userNameAvailable, username);
		}
	}

	private void showDialog(int messageId) {
		if (activity == null) {
			return;
		}
		if (exception != null && exception.getMessage() == null) {
			new CustomAlertDialogBuilder(activity).setMessage(messageId).setPositiveButton(R.string.ok, null)
					.show();
		} else {
			new CustomAlertDialogBuilder(activity).setMessage(exception.getMessage())
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnCheckUserNameAvailableCompleteListener {
		void onCheckUserNameAvailableComplete(Boolean userNameAvailable, String username);
	}
}
