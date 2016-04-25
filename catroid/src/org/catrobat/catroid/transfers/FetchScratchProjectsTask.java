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

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Preconditions;

import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;

public class FetchScratchProjectsTask extends AsyncTask<String, Void, ScratchSearchResult> {

    private static final String TAG = FetchScratchProjectsTask.class.getSimpleName();
    private static final int MAX_NUM_OF_RETRIES = 2;
    private static final int MIN_TIMEOUT = 1_000; // in ms

    public interface ScratchProjectListTaskDelegate {
        void onPreExecute();
        void onPostExecute(ScratchSearchResult result);
    }

    private ScratchProjectListTaskDelegate delegate = null;

    public FetchScratchProjectsTask setDelegate(ScratchProjectListTaskDelegate delegate) {
        this.delegate = delegate;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (delegate != null) {
            delegate.onPreExecute();
        }
    }

    @Override
    protected ScratchSearchResult doInBackground(String... params) {
        Preconditions.checkArgument(params.length <= 2, "Invalid number of parameters!");
        try {
            return fetchProjectList(params.length > 0 ? params[0] : null);
        } catch (InterruptedIOException exception) {
            Log.d(TAG, "CANCELLED!");
            Log.d(TAG, "test: " + isCancelled());
            return null;
        }
    }

    public ScratchSearchResult fetchProjectList(String query) throws InterruptedIOException {
        // exponential backoff
        int delay;
        for (int attempt = 0; attempt <= MAX_NUM_OF_RETRIES; attempt++) {
            if (isCancelled()) {
                return null;
            }
            try {
                if (query != null) {
                    ServerCalls.ScratchSearchSortType sortType = ServerCalls.ScratchSearchSortType.RELEVANCE;
                    return ServerCalls.getInstance().scratchSearch(query, sortType, 20, 0);
                }
                return ServerCalls.getInstance().fetchDefaultScratchProjects();
            } catch (WebconnectionException e) {
                Log.d(TAG, e.getLocalizedMessage() + "\n" +  e.getStackTrace());
                delay = MIN_TIMEOUT + (int) (MIN_TIMEOUT * Math.random() * (attempt + 1));
                Log.i(TAG, "Retry #" + (attempt+1) + " to fetch scratch project list scheduled in "
                        + delay + " ms due to " + e.getLocalizedMessage());
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e1) {}
            }
        }
        Log.w(TAG, "Maximum number of " + (MAX_NUM_OF_RETRIES + 1)
                + " attempts exceeded! Server not reachable?!");
        return null;
    }

    @Override
    protected void onPostExecute(ScratchSearchResult result) {
        super.onPostExecute(result);
        if (delegate != null && ! isCancelled()) {
            delegate.onPostExecute(result);
        }
    }

}
