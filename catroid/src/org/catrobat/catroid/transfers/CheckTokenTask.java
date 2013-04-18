/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.transfers;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class CheckTokenTask extends AsyncTask<Void, Void, Boolean> {
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
		String message = activity.getString(R.string.loading);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if (!Utils.isNetworkAvailable(activity)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK);
				return false;
			}

			return ServerCalls.getInstance().checkToken(token, username);

		} catch (WebconnectionException e) {
			e.printStackTrace();
			exception = e;
		}
		return false;

	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!success && exception.getStatusCode() == WebconnectionException.ERROR_NETWORK) {
			showDialog(R.string.error_internet_connection);
			return;
		}
		if (!success) {
			// token is not valid -> maybe password has changed
			if (onCheckTokenCompleteListener != null) {
				onCheckTokenCompleteListener.onTokenNotValid();
			}

			return;
		}

		if (onCheckTokenCompleteListener != null) {
			onCheckTokenCompleteListener.onCheckTokenSuccess();
		}
	}

	private void showDialog(int messageId) {
		if (activity == null) {
			return;
		}
		if (exception.getMessage() == null) {
			new Builder(activity).setMessage(messageId).setPositiveButton("OK", null).show();
		} else {
			new Builder(activity).setMessage(exception.getMessage()).setPositiveButton("OK", null).show();
		}
	}

	public interface OnCheckTokenCompleteListener {

		public void onTokenNotValid();

		public void onCheckTokenSuccess();

	}
}
