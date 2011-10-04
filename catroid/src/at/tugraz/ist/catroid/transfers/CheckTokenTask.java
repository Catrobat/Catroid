/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.transfers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.os.AsyncTask;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.dialogs.LoginRegisterDialog;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class CheckTokenTask extends AsyncTask<Void, Void, Boolean> {
	private Activity activity;
	private ProgressDialog progressDialog;
	private String token;

	private WebconnectionException exception;

	public CheckTokenTask(Activity activity, String token) {
		this.activity = activity;
		this.token = token;
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

			return ServerCalls.getInstance().checkToken(token);

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
			new LoginRegisterDialog(activity).show();
			return;
		}

		activity.showDialog(MainMenuActivity.DIALOG_UPLOAD_PROJECT);
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

}
