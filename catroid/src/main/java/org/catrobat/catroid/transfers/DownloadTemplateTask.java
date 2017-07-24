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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.TemplateData;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.IOException;

public class DownloadTemplateTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = DownloadTemplateTask.class.getSimpleName();

	private Context context;
	private final TemplateData templateData;
	private final String zipFileUrl;
	private ProgressDialog progressDialog;
	private OnDownloadTemplateCompleteListener onDownloadTemplateCompleteListener;
	private String message;

	public DownloadTemplateTask(Context context, TemplateData templateData, String zipFileUrl,
			OnDownloadTemplateCompleteListener listener) {
		this.context = context;
		this.templateData = templateData;
		this.zipFileUrl = zipFileUrl;
		onDownloadTemplateCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String progressMessage = context.getString(R.string.status_downloading);
		progressDialog = ProgressDialog.show(context, title, progressMessage);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		if (!Utils.isNetworkAvailable(context)) {
			return false;
		}
		String zipFileString = Utils.buildPathForTemplatesZip(templateData.getName());

		try {
			ServerCalls.getInstance().downloadProject(zipFileUrl, zipFileString, null, null, 0);
		} catch (WebconnectionException | IOException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
			message = webconnectionException.getMessage();
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean networkAvailable) {
		super.onPostExecute(networkAvailable);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!networkAvailable) {
			ToastUtil.showError(context, R.string.error_internet_connection);
			return;
		} else if (message != null) {
			ToastUtil.showError(context, message);
			return;
		}

		onDownloadTemplateCompleteListener.onDownloadTemplateComplete();
	}

	public interface OnDownloadTemplateCompleteListener {
		void onDownloadTemplateComplete();
	}
}
