/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;

public class SearchScratchProgramsTask extends AsyncTask<String, Void, ScratchSearchResult> {

	public interface SearchScratchProgramsTaskDelegate {
		void onPreExecute();
		void onPostExecute(ScratchSearchResult result);
	}

	private static final String TAG = SearchScratchProgramsTask.class.getSimpleName();

	private SearchScratchProgramsTaskDelegate delegate = null;
	private ScratchDataFetcher fetcher = null;

	public SearchScratchProgramsTask setDelegate(SearchScratchProgramsTaskDelegate delegate) {
		this.delegate = delegate;
		return this;
	}

	public SearchScratchProgramsTask setFetcher(ScratchDataFetcher fetcher) {
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
	protected ScratchSearchResult doInBackground(String... params) {
		Preconditions.checkArgument(params.length <= 2, "Invalid number of parameters!");
		try {
			return fetchProgramList(params.length > 0 ? params[0] : null);
		} catch (InterruptedIOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return null;
		}
	}

	public ScratchSearchResult fetchProgramList(String query) throws InterruptedIOException {
		final int minTimeout = Constants.SCRATCH_HTTP_REQUEST_MIN_TIMEOUT;
		final int maxNumRetries = Constants.SCRATCH_HTTP_REQUEST_MAX_NUM_OF_RETRIES;

		int delay;

		for (int attempt = 0; attempt <= maxNumRetries; attempt++) {
			if (isCancelled()) {
				return null;
			}
			try {
				if (query != null) {
					return fetcher.scratchSearch(query, 20, 0);
				}
				return fetcher.fetchDefaultScratchPrograms();
			} catch (WebconnectionException e) {
				Log.e(TAG, Log.getStackTraceString(e));
				delay = minTimeout + (int) (minTimeout * Math.random() * (attempt + 1));
				try {
					Thread.sleep(delay);
				} catch (InterruptedException interruptedE) {
					Log.e(TAG, Log.getStackTraceString(interruptedE));
				}
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(ScratchSearchResult result) {
		super.onPostExecute(result);
		if (delegate != null && !isCancelled()) {
			delegate.onPostExecute(result);
		}
	}
}
