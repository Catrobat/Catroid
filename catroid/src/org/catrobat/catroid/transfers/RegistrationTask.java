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
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.utils.UtilToken;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {

	private Context context;
	private ProgressDialog progressDialog;
	private String username;
	private String password;

	private String message;
	private boolean userRegistered;

	private OnRegistrationCompleteListener onRegistrationCompleteListener;

	public RegistrationTask(Context activity, String username, String password) {
		this.context = activity;
		this.username = username;
		this.password = password;
	}

	public void setOnRegistrationCompleteListener(OnRegistrationCompleteListener listener) {
		onRegistrationCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.loading);
		progressDialog = ProgressDialog.show(context, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if (!Utils.isNetworkAvailable(context)) {
				return false;
			}
			String email = UtilDeviceInfo.getUserEmail(context);
			String language = UtilDeviceInfo.getUserLanguageCode(context);
			String country = RegistrationData.INSTANCE.getCountryCode();
			String token = UtilToken.calculateToken(username, password);
			String gender = RegistrationData.INSTANCE.getGender();
			String birthdayMonth = RegistrationData.INSTANCE.getBirthdayMonth();
			String birthdayYear = RegistrationData.INSTANCE.getBirthdayYear();
			String city = RegistrationData.INSTANCE.getCity();

			userRegistered = ServerCalls.getInstance().registerOrCheckToken(username, password, email, language,
					country, token, gender, birthdayMonth, birthdayYear, city);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			sharedPreferences.edit().putString(Constants.TOKEN, token).commit();

			return true;

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

		if (context == null) {
			return;
		}

		if (userRegistered) {
			Toast.makeText(context, R.string.new_user_registered, Toast.LENGTH_SHORT).show();
		}

		if (onRegistrationCompleteListener != null) {
			onRegistrationCompleteListener.onRegistrationComplete();
		}
	}

	private void showDialog(int messageId) {
		if (context == null) {
			return;
		}
		if (message == null) {
			new Builder(context).setTitle(R.string.register_error).setMessage(messageId).setPositiveButton("OK", null)
					.show();
		} else {
			new Builder(context).setTitle(R.string.register_error).setMessage(message).setPositiveButton("OK", null)
					.show();
		}
	}

	public interface OnRegistrationCompleteListener {

		public void onRegistrationComplete();

	}
}
