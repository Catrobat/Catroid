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

import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;

public class GetTagsTask extends AsyncTask<String, Void, String> {

    private AsyncResponse onTagsResponseListener;
    private Context context;

    public void setOnTagsResponseListener(AsyncResponse listener) {
        onTagsResponseListener = listener;
    }

    public interface AsyncResponse {
        void onTagsReceived(String tags);
    }

    public GetTagsTask(Context activity) {
        this.context = activity;
    }

    @Override
    protected String doInBackground(String... arg0) {
        if (!Utils.isNetworkAvailable(context)) {
            return "No network";
        }
        String response = ServerCalls.getInstance().getTagsRequest(UtilDeviceInfo.getUserLanguageCode());
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (onTagsResponseListener != null) {
            onTagsResponseListener.onTagsReceived(result);
        }
    }

}
