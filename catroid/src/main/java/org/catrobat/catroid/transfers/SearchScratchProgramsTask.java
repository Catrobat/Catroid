/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.os.Handler;
import android.util.Log;

import com.google.common.base.Preconditions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchSearchResult;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;

public class SearchScratchProgramsTask extends AsyncTask<String, Void, ScratchSearchResult> {

	public interface SearchScratchProgramsTaskDelegate {
		void onPreExecute();
		void onPostExecute(ScratchSearchResult result);
	}

	private static final String TAG = SearchScratchProgramsTask.class.getSimpleName();

	private Context context;
	private Handler handler;
	private SearchScratchProgramsTaskDelegate delegate = null;
	private ScratchDataFetcher fetcher = null;

	public SearchScratchProgramsTask setContext(final Context context) {
		this.context = context;
		this.handler = new Handler(context.getMainLooper());
		return this;
	}

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
		} catch (InterruptedIOException exception) {
			Log.i(TAG, "Task has been cancelled in the meanwhile!");
			return null;
		}
	}

	public ScratchSearchResult fetchProgramList(String query) throws InterruptedIOException {
		// exponential backoff
		final int minTimeout = Constants.SCRATCH_HTTP_REQUEST_MIN_TIMEOUT;
		final int maxNumRetries = Constants.SCRATCH_HTTP_REQUEST_MAX_NUM_OF_RETRIES;

		int delay;

		for (int attempt = 0; attempt <= maxNumRetries; attempt++) {
			if (isCancelled()) {
				Log.i(TAG, "Task has been cancelled in the meanwhile!");
				return null;
			}

			try {
				if (query != null) {
					return fetcher.scratchSearch(query, 20, 0);
				}
				return fetcher.fetchDefaultScratchPrograms();
			} catch (WebconnectionException e) {
				Log.d(TAG, e.getLocalizedMessage() + "\n" + e.getStackTrace());
				delay = minTimeout + (int) (minTimeout * Math.random() * (attempt + 1));
				Log.i(TAG, "Retry #" + (attempt + 1) + " to search for scratch programs scheduled in "
						+ delay + " ms due to " + e.getLocalizedMessage());
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ex) {
					Log.e(TAG, ex.getMessage());
				}
			}
		}
		Log.w(TAG, "Maximum number of " + (maxNumRetries + 1) + " attempts exceeded! Server not reachable?!");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ToastUtil.showError(context, context.getString(R.string.error_request_timeout));
			}
		});
		return null;
	}

	@Override
	protected void onPostExecute(ScratchSearchResult result) {
		super.onPostExecute(result);
		if (delegate != null && !isCancelled()) {
			delegate.onPostExecute(result);
		}
	}

	private void runOnUiThread(Runnable r) {
		handler.post(r);
	}
}
