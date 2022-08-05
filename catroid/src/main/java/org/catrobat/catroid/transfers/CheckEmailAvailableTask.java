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

public class CheckEmailAvailableTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = CheckEmailAvailableTask.class.getSimpleName();

	private String email;
	private String provider;

	private Boolean emailAvailable;

	private OnCheckEmailAvailableCompleteListener onCheckEmailAvailableCompleteListener;

	public CheckEmailAvailableTask(String email, String provider) {
		this.email = email;
		this.provider = provider;
	}

	public void setOnCheckEmailAvailableCompleteListener(OnCheckEmailAvailableCompleteListener listener) {
		onCheckEmailAvailableCompleteListener = listener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			emailAvailable = new CatrobatServerCalls().isEMailAvailable(email);
			return true;
		} catch (WebconnectionException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (onCheckEmailAvailableCompleteListener != null) {
			onCheckEmailAvailableCompleteListener.onCheckEmailAvailableComplete(emailAvailable, provider);
		}
	}

	public interface OnCheckEmailAvailableCompleteListener {
		void onCheckEmailAvailableComplete(Boolean emailAvailable, String provider);
	}
}
