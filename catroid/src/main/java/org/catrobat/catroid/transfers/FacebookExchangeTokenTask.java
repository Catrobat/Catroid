/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

public class FacebookExchangeTokenTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = FacebookExchangeTokenTask.class.getSimpleName();

	private Activity activity;
	private ProgressDialog progressDialog;
	private String clientToken;
	private String mail;
	private String username;
	private String id;
	private String locale;
	private String message;
	private boolean tokenExchanged;
	private OnFacebookExchangeTokenCompleteListener onFacebookExchangeTokenCompleteListener;
	private WebconnectionException exception;

	public FacebookExchangeTokenTask(Activity activity, String clientToken, String mail, String username, String id,
			String locale) {
		this.clientToken = clientToken;
		this.activity = activity;
		this.mail = mail;
		this.username = username;
		this.id = id;
		this.locale = locale;
	}

	public void setOnFacebookExchangeTokenCompleteListener(OnFacebookExchangeTokenCompleteListener listener) {
		onFacebookExchangeTokenCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		String title = activity.getString(R.string.please_wait);
		String message = activity.getString(R.string.loading_facebook_exchange_token);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if (!Utils.isNetworkAvailable(activity)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return false;
			}

			tokenExchanged = ServerCalls.getInstance().facebookExchangeToken(clientToken, id, username, mail, locale);
			return true;
		} catch (WebconnectionException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
			message = webconnectionException.getMessage();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (Utils.checkForNetworkError(exception)) {
			showDialog(R.string.error_internet_connection);
			return;
		}

		if ((!success && exception != null) || activity == null || !tokenExchanged) {
			showDialog(R.string.sign_in_error);
			return;
		}

		if (onFacebookExchangeTokenCompleteListener != null) {
			onFacebookExchangeTokenCompleteListener.onFacebookExchangeTokenComplete(activity);
		}
	}

	private void showDialog(int messageId) {
		if (activity == null) {
			return;
		}
		if (message == null) {
			new CustomAlertDialogBuilder(activity).setTitle(R.string.register_error).setMessage(messageId)
					.setPositiveButton(R.string.ok, null).show();
		} else {
			new CustomAlertDialogBuilder(activity).setTitle(R.string.register_error).setMessage(message)
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnFacebookExchangeTokenCompleteListener {
		void onFacebookExchangeTokenComplete(Activity activity);
	}
}
