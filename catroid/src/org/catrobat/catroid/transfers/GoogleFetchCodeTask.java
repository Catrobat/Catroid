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

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.SignInDialog;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;

public class GoogleFetchCodeTask extends AsyncTask<Void, Void, String> {

	private static final String TAG = GoogleFetchCodeTask.class.getSimpleName();

	private static String OAUTH_SCOPE_PREFIX = "oauth2:";

	private SignInDialog signInDialog;
	private ProgressDialog progressDialog;
	private String accountName;
	private String message;
	private String token;
	private OnGoogleLogInCompleteListener onGoogleLogInCompleteListener;

	public GoogleFetchCodeTask(SignInDialog signInDialog, String accountName) {
		this.signInDialog = signInDialog;
		this.accountName = accountName;
	}

	public void setOnGoogleLogInCompleteListener(OnGoogleLogInCompleteListener listener) {
		onGoogleLogInCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (signInDialog == null) {
			return;
		}
		String title = signInDialog.getString(R.string.please_wait);
		String message = signInDialog.getString(R.string.loading);
		progressDialog = ProgressDialog.show(signInDialog.getActivity(), title, message);
	}

	@Override
	protected String doInBackground(Void... arg0) {
		if (!Utils.isNetworkAvailable(signInDialog.getActivity())) {
			return "";
		}

		try {
			token = GoogleAuthUtil.getToken(signInDialog.getActivity(), accountName, OAUTH_SCOPE_PREFIX + Scopes.PLUS_LOGIN);

			return token;
		} catch (UserRecoverableAuthException userRecoverableException) {
			userRecoverableException.printStackTrace();
			// GooglePlayServices.apk is either old, disabled, or not present
			// so we need to show the user some UI in the activity to recover.
			signInDialog.handleException(userRecoverableException);
		} catch (GoogleAuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			return "";
		}

		@Override
		protected void onPostExecute (String result){
			super.onPostExecute(result);

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			if (result.isEmpty()) {
				showDialog(R.string.error_internet_connection);
				return;
			}

			if (signInDialog == null) {
				return;
			}

			if (token != null && !token.isEmpty()) {
				ToastUtil.showSuccess(signInDialog.getActivity(), R.string.user_logged_in);
			}

			if (onGoogleLogInCompleteListener != null) {
				onGoogleLogInCompleteListener.onGoogleLogInComplete();
			}
		}

	private void showDialog(int messageId) {
		if (signInDialog == null) {
			return;
		}
		if (message == null) {
			new CustomAlertDialogBuilder(signInDialog.getActivity()).setTitle(R.string.register_error).setMessage(messageId)
					.setPositiveButton(R.string.ok, null).show();
		} else {
			new CustomAlertDialogBuilder(signInDialog.getActivity()).setTitle(R.string.register_error).setMessage(message)
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnGoogleLogInCompleteListener {
		void onGoogleLogInComplete();
	}
}
