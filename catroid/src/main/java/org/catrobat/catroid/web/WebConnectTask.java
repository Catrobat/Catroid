/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.web;

import android.os.AsyncTask;

public class WebConnectTask extends AsyncTask<String, Void, Object> {

    public static final String TAG = WebConnectTask.class.getSimpleName();

    private Object webResponse;

    public Object getWebResponse() {
        return webResponse;
    }

    public WebConnectTask() {
    }

    @Override
    protected Object doInBackground(String... strings) {
        try {
            return ServerCalls.getInstance().getRequest(strings[0]);
        } catch (WebconnectionException e) {
            return 503d;
        }
    }

    @Override
    protected void onPostExecute(Object success) {
    }
}
