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
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;

public class LogoutTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = LogoutTask.class.getSimpleName();

	private Context context;
	private String username;

	public LogoutTask(Context activity, String username) {
		this.context = activity;
		this.username = username;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		if (!Utils.isNetworkAvailable(context)) {
			return false;
		}
		ServerCalls.getInstance().logout(username);
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
	}
}
