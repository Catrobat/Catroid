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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class DeleteTestUserTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = DeleteTestUserTask.class.getSimpleName();

	private Context context;

	private WebconnectionException exception;

	private OnDeleteTestUserCompleteListener onDeleteTestUserCompleteListener;

	public DeleteTestUserTask(Context context) {
		this.context = context;
	}

	public void setOnDeleteTestUserCompleteListener(OnDeleteTestUserCompleteListener listener) {
		onDeleteTestUserCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if (!Utils.isNetworkAvailable(context)) {
				exception = new WebconnectionException(WebconnectionException.ERROR_NETWORK, "Network not available!");
				return false;
			}

			return ServerCalls.getInstance().deleteTestUserAccountsOnServer();
		} catch (WebconnectionException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
			exception = webconnectionException;
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean deleted) {
		super.onPostExecute(deleted);

		if (Utils.checkForNetworkError(exception)) {
			showDialog(R.string.error_internet_connection);
			return;
		}

		if (onDeleteTestUserCompleteListener != null) {
			onDeleteTestUserCompleteListener.onDeleteTestUserComplete(deleted);
		}
	}

	private void showDialog(int messageId) {
		if (context == null) {
			return;
		}
		if (exception.getMessage() == null) {
			new CustomAlertDialogBuilder(context).setMessage(messageId).setPositiveButton(R.string.ok, null)
					.show();
		} else {
			new CustomAlertDialogBuilder(context).setMessage(exception.getMessage())
					.setPositiveButton(R.string.ok, null).show();
		}
	}

	public interface OnDeleteTestUserCompleteListener {
		void onDeleteTestUserComplete(Boolean deleted);
	}
}
