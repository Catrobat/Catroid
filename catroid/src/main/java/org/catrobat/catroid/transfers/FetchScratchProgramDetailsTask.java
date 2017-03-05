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
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.WebScratchProgramException;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.InterruptedIOException;

public class FetchScratchProgramDetailsTask extends AsyncTask<Long, Void, ScratchProgramData> {

	public interface ScratchProgramListTaskDelegate {
		void onPreExecute();
		void onPostExecute(ScratchProgramData programData);
	}

	private static final String TAG = FetchScratchProgramDetailsTask.class.getSimpleName();

	private Context context;
	private Handler handler;
	private ScratchProgramListTaskDelegate delegate = null;
	private ScratchDataFetcher fetcher = null;

	public FetchScratchProgramDetailsTask setContext(final Context context) {
		this.context = context;
		this.handler = new Handler(context.getMainLooper());
		return this;
	}

	public FetchScratchProgramDetailsTask setDelegate(ScratchProgramListTaskDelegate delegate) {
		this.delegate = delegate;
		return this;
	}

	public FetchScratchProgramDetailsTask setFetcher(ScratchDataFetcher fetcher) {
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
	protected ScratchProgramData doInBackground(Long... params) {
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

	public ScratchProgramData fetchProjectData(final long projectID) throws InterruptedIOException {
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
				return fetcher.fetchScratchProgramDetails(projectID);
			} catch (WebScratchProgramException e) {
				String userErrorMessage = context.getString(R.string.error_scratch_program_not_accessible_any_more);
				if (e.getStatusCode() == WebScratchProgramException.ERROR_PROGRAM_NOT_ACCESSIBLE) {
					userErrorMessage = context.getString(R.string.error_scratch_program_not_accessible_any_more);
				}

				final String finalUserErrorMessage = userErrorMessage;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ToastUtil.showError(context, finalUserErrorMessage);
					}
				});

				return null;
			} catch (WebconnectionException e) {
				Log.e(TAG, e.getMessage() + "\n" + e.getStackTrace());
				delay = minTimeout + (int) (minTimeout * Math.random() * (attempt + 1));
				Log.i(TAG, "Retry #" + (attempt + 1) + " to fetch scratch project list scheduled in "
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
	protected void onPostExecute(ScratchProgramData programData) {
		super.onPostExecute(programData);
		if (delegate != null && !isCancelled()) {
			delegate.onPostExecute(programData);
		}
	}

	private void runOnUiThread(Runnable r) {
		handler.post(r);
	}
}
