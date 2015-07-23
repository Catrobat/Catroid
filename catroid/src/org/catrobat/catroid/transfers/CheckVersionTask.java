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

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class CheckVersionTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = CheckVersionTask.class.getSimpleName();

	private FragmentActivity fragmentActivity;
	private ProgressDialog progressDialog;
	private String versionNumberCurrent;
	private String projectName;
	private boolean isVersionSameAsPlayStore = false;
	private int errorCode = 0;
	private static final int INTERNET_EXCEPTION = 1;

	private Exception exception;

	private OnCheckVersionListener onCheckVersionListener;

	public CheckVersionTask(FragmentActivity fragmentActivity, String version, String projectName) {
		this.fragmentActivity = fragmentActivity;
		this.versionNumberCurrent = version;
		this.projectName = projectName;
	}

	public void setOnCheckVersionListener(OnCheckVersionListener listener) {
		onCheckVersionListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (fragmentActivity == null) {
			return;
		}
		String title = fragmentActivity.getString(R.string.please_wait);
		String message = fragmentActivity.getString(R.string.loading);
		progressDialog = ProgressDialog.show(fragmentActivity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			if (!Utils.isNetworkAvailable(fragmentActivity)) {
				errorCode = INTERNET_EXCEPTION;
			}

			String currentContent = "";

			URL url = null;
			URI uri;
			url = new URL(Constants.POCKET_CODE_PLAY_STORE_URL);
			uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			String tempLine;
			while ((tempLine = bufferedreader.readLine()) != null) {
				currentContent += tempLine;
			}

			if (currentContent.contains("softwareVersion\"> " + versionNumberCurrent)) {
				isVersionSameAsPlayStore = true;
			} else {
				isVersionSameAsPlayStore = false;
			}
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
			exception = e;

			progressDialog.dismiss();
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		if (exception != null) {
			int messageId;
			switch (errorCode) {
				case INTERNET_EXCEPTION:
					messageId = R.string.error_internet_connection;
					break;
				default:
					messageId = R.string.error_unknown_error;
			}
			showDialog(messageId);
			return;
		}

		if (isVersionSameAsPlayStore) {
			if (onCheckVersionListener != null) {
				onCheckVersionListener.onVersionValid(fragmentActivity, projectName);
			}
			return;
		} else {
			if (onCheckVersionListener != null) {
				onCheckVersionListener.onVersionInvalid(fragmentActivity);
			}
			return;
		}
	}

	private void showDialog(int messageId) {
		if (fragmentActivity == null) {
			return;
		}
		new CustomAlertDialogBuilder(fragmentActivity).setMessage(messageId).setPositiveButton(R.string.ok, null).show();
	}

	public interface OnCheckVersionListener {

		void onVersionValid(FragmentActivity fragmentActivity, String projectName);

		void onVersionInvalid(FragmentActivity fragmentActivity);
	}
}
