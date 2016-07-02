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
import android.os.Looper;
import android.util.Log;

import com.google.common.base.Preconditions;

import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;

public class FetchScratchProjectDetailsTask extends AsyncTask<Long, Void, ScratchProjectData> {

	public interface ScratchProjectListTaskDelegate {
		void onPreExecute();
		void onPostExecute(ScratchProjectData projectData);
	}

	public interface ScratchProjectDataFetcher {
		ScratchProjectData fetchScratchProjectDetails(final long projectID) throws WebconnectionException, InterruptedIOException;
		ScratchSearchResult fetchDefaultScratchProjects() throws WebconnectionException, InterruptedIOException;
		ScratchSearchResult scratchSearch(final String query, final ServerCalls.ScratchSearchSortType sortType,
				final int numberOfItems, final int page) throws WebconnectionException, InterruptedIOException;
	}

	private static final String TAG = FetchScratchProjectDetailsTask.class.getSimpleName();
	private static final int MAX_NUM_OF_RETRIES = 2;
	private static final int MIN_TIMEOUT = 1_000; // in ms

	private ScratchProjectListTaskDelegate delegate = null;
	private ScratchProjectDataFetcher fetcher = null;

	public FetchScratchProjectDetailsTask setDelegate(ScratchProjectListTaskDelegate delegate) {
		this.delegate = delegate;
		return this;
	}

	public FetchScratchProjectDetailsTask setFetcher(ScratchProjectDataFetcher fetcher) {
		this.fetcher = fetcher;
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
	protected ScratchProjectData doInBackground(Long... params) {
		Preconditions.checkArgument(params.length == 1, "No project ID given!");
		final long projectID = params[0];
		Preconditions.checkArgument(projectID > 0, "Invalid project ID given!");
		try {
			return fetchProjectData(projectID);
		} catch (InterruptedIOException exception) {
			Log.i(TAG, "Task has been cancelled in the meanwhile!");
			return null;
		}
	}

	public ScratchProjectData fetchProjectData(final long projectID) throws InterruptedIOException {
		// exponential backoff
		int delay;
		for (int attempt = 0; attempt <= MAX_NUM_OF_RETRIES; attempt++) {
			if (isCancelled()) {
				return null;
			}
			try {
				return fetcher.fetchScratchProjectDetails(projectID);
			} catch (WebconnectionException e) {
				Log.d(TAG, e.getLocalizedMessage() + "\n" + e.getStackTrace());
				delay = MIN_TIMEOUT + (int) (MIN_TIMEOUT * Math.random() * (attempt + 1));
				Log.i(TAG, "Retry #" + (attempt + 1) + " to fetch scratch project list scheduled in "
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
	protected void onPostExecute(ScratchProjectData projectData) {
		super.onPostExecute(projectData);
		if (delegate != null && !isCancelled()) {
			delegate.onPostExecute(projectData);
		}
	}
}
