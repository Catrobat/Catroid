/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.transfers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.os.AsyncTask;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.dialogs.UploadProjectDialog;
import at.tugraz.ist.catroid.utils.UtilDeviceInfo;
import at.tugraz.ist.catroid.utils.UtilToken;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {
	private Activity activity;
	private ProgressDialog progressDialog;
	private String username;
	private String password;
	private Dialog dialogToRemoveOnSuccess;

	private String message;

	public RegistrationTask(Activity activity, String username, String password, Dialog dialogToRemoveOnSuccess) {
		this.activity = activity;
		this.username = username;
		this.password = password;
		this.dialogToRemoveOnSuccess = dialogToRemoveOnSuccess;
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
				return false;
			}

			String email = "mail";
			String language = UtilDeviceInfo.getUserLanguageCode(activity);
			String country = UtilDeviceInfo.getUserCountryCode(activity);
			String token = UtilToken.calculateToken(username, password);

			return ServerCalls.getInstance().registration(username, password, email, language, country, token);

		} catch (WebconnectionException e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		return false;

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!result) {
			showDialog(R.string.error_internet_connection);
			return;
		}

		if (activity == null) {
			return;
		}
		new UploadProjectDialog(activity).show();
		dialogToRemoveOnSuccess.dismiss();
	}

	private void showDialog(int messageId) {
		if (activity == null) {
			return;
		}
		if (message == null) {
			new Builder(activity).setMessage(messageId).setPositiveButton("OK", null).show();
		} else {
			new Builder(activity).setMessage(message).setPositiveButton("OK", null).show();
		}
	}

}
