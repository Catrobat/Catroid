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

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.web.CatrobatServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

public class CheckTokenTask extends AsyncTask<String, Void, Boolean[]> {

	private static final String TAG = CheckTokenTask.class.getSimpleName();
	private TokenCheckListener onCheckTokenCompleteListener;

	public CheckTokenTask(TokenCheckListener onCheckTokenCompleteListener) {
		this.onCheckTokenCompleteListener = onCheckTokenCompleteListener;
	}

	@Override
	protected Boolean[] doInBackground(String... arg0) {
		try {
			return new Boolean[]{new CatrobatServerCalls().checkToken(arg0[0], arg0[1], FlavoredConstants.BASE_URL_HTTPS), false};
		} catch (WebconnectionException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return new Boolean[]{false, true};
		}
	}

	@Override
	protected void onPostExecute(Boolean[] b) {
		boolean tokenValid = b[0];
		boolean connectionFailed = b[1];
		onCheckTokenCompleteListener.onTokenCheckComplete(tokenValid, connectionFailed);
	}

	public interface TokenCheckListener {

		void onTokenCheckComplete(boolean tokenValid, boolean connectionFailed);
	}
}
