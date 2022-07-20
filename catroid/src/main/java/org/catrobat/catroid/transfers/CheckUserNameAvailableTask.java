/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.catroid.web.CatrobatServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class CheckUserNameAvailableTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = CheckUserNameAvailableTask.class.getSimpleName();
	private String username;

	private Boolean userNameAvailable;

	private OnCheckUserNameAvailableCompleteListener onCheckUserNameAvailableCompleteListener;

	public CheckUserNameAvailableTask(String username) {
		this.username = username;
	}

	public void setOnCheckUserNameAvailableCompleteListener(OnCheckUserNameAvailableCompleteListener listener) {
		onCheckUserNameAvailableCompleteListener = listener;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			userNameAvailable = new CatrobatServerCalls().isUserNameAvailable(username);
			return true;
		} catch (WebconnectionException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (onCheckUserNameAvailableCompleteListener != null) {
			onCheckUserNameAvailableCompleteListener.onCheckUserNameAvailableComplete(userNameAvailable, username);
		}
	}

	public interface OnCheckUserNameAvailableCompleteListener {
		void onCheckUserNameAvailableComplete(Boolean userNameAvailable, String username);
	}
}
