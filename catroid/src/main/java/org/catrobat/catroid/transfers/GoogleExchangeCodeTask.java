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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class GoogleExchangeCodeTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = GoogleExchangeCodeTask.class.getSimpleName();

	private Context context;
	private ProgressDialog progressDialog;
	private String code;
	private String mail;
	private String username;
	private String id;
	private String locale;
	private final String idToken;
	private String message;
	private OnGoogleExchangeCodeCompleteListener onGoogleExchangeCodeCompleteListener;
	private WebconnectionException exception;
	private boolean tokenExchanged;

	public GoogleExchangeCodeTask(Activity activity, String code, String mail, String username, String id, String
			locale, String idToken) {
		this.code = code;
		this.context = activity;
		this.mail = mail;
		this.username = username;
		this.id = id;
		this.locale = locale;
		this.idToken = idToken;
	}

	public void setOnGoogleExchangeCodeCompleteListener(OnGoogleExchangeCodeCompleteListener listener) {
		onGoogleExchangeCodeCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.loading_google_exchange_code);
		progressDialog = ProgressDialog.show(context, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if (!Utils.isNetworkAvailable(context)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return false;
			}

			tokenExchanged = new ServerCalls(CatrobatWebClient.INSTANCE.getClient()).googleExchangeCode(code, id, username, mail, locale, idToken);
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

		if ((!success && exception != null) || !tokenExchanged) {
			showDialog(R.string.sign_in_error);
			return;
		}

		if (onGoogleExchangeCodeCompleteListener != null) {
			onGoogleExchangeCodeCompleteListener.onGoogleExchangeCodeComplete();
		}
	}

	private void showDialog(int messageId) {
		if (context == null) {
			return;
		}
		if (message == null) {
			new AlertDialog.Builder(context).setTitle(R.string.register_error).setMessage(messageId)
					.setPositiveButton(R.string.ok, null).show();
		} else {
			new AlertDialog.Builder(context).setTitle(R.string.register_error).setMessage(message)
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnGoogleExchangeCodeCompleteListener {
		void onGoogleExchangeCodeComplete();
	}
}
