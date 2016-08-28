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

package org.catrobat.catroid.scratchconverter.protocol;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.koushikdutta.async.http.WebSocket;

import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.Client.ConvertCallback;
import org.catrobat.catroid.scratchconverter.Client.DownloadFinishedCallback;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys.JsonDataKeys;
import org.catrobat.catroid.scratchconverter.protocol.message.Message.CategoryType;
import org.catrobat.catroid.scratchconverter.protocol.message.base.BaseMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebSocketMessageListener implements MessageListener, WebSocket.StringCallback {

	private static final String TAG = WebSocketMessageListener.class.getSimpleName();

	private BaseMessageHandler baseMessageHandler;
	private Map<Long, JobHandler> jobHandlers;

	public WebSocketMessageListener() {
		this.baseMessageHandler = null;
		this.jobHandlers = Collections.synchronizedMap(new LinkedHashMap<Long, JobHandler>());
	}

	public void setBaseMessageHandler(final BaseMessageHandler baseMessageHandler) {
		this.baseMessageHandler = baseMessageHandler;
	}

	@Override
	public synchronized void onStringAvailable(String s) {
		try {
			if (s == null) {
				return;
			}

			Log.d(TAG, "Receiving new message: " + s);
			JSONObject jsonMessage = new JSONObject(s);
			if (jsonMessage.length() == 0) {
				return;
			}

			final int categoryID = jsonMessage.getInt(JsonKeys.CATEGORY.toString());
			final CategoryType categoryType = CategoryType.valueOf(categoryID);

			switch (categoryType) {
				case BASE:
					baseMessageHandler.onBaseMessage(BaseMessage.fromJson(jsonMessage));
					break;

				case JOB:
					final JSONObject jsonData = jsonMessage.getJSONObject(JsonKeys.DATA.toString());
					final long jobID = jsonData.getLong(JsonDataKeys.JOB_ID.toString());
					JobHandler jobHandler = jobHandlers.get(jobID);
					if (jobHandler != null) {
						jobHandler.onJobMessage(JobMessage.fromJson(jsonMessage));
					} else {
						Log.w(TAG, "No JobHandler registered for job with ID: " + jobID);
					}
					break;

				default:
					Log.w(TAG, "Message of unsupported category-type " + categoryType + " received");
					break;
			}
		} catch (JSONException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}

	@Override
	public boolean isJobInProgress(long jobID) {
		final JobHandler jobHandler = jobHandlers.get(jobID);
		if (jobHandler == null) {
			return false;
		}
		return jobHandler.isInProgress();
	}

	@Override
	public void onUserCanceledConversion(long jobID) {
		final JobHandler jobHandler = jobHandlers.get(jobID);
		if (jobHandler == null) {
			return;
		}
		jobHandler.onUserCanceledConversion();
	}

	@Override
	public int getNumberOfJobsInProgress() {
		int numberOfJobsInProgress = 0;
		for (final JobHandler jobHandler : jobHandlers.values()) {
			if (jobHandler.isInProgress()) {
				++numberOfJobsInProgress;
			}
		}
		return numberOfJobsInProgress;
	}

	private JobHandler createOrUseExistingJobHandlerForJobIfPossible(final Job job, final boolean force,
			final Client.ConvertCallback convertCallback) {
		JobHandler jobHandler = jobHandlers.get(job.getJobID());
		if (jobHandler != null) {
			Log.d(TAG, "JobHandler for jobID " + job.getJobID() + " already exists!");

			if (!force && jobHandler.isInProgress()) {
				return null;
			}

			jobHandler.setCallback(convertCallback);
		} else {
			Log.d(TAG, "Creating new JobHandler for jobID " + job.getJobID());
			jobHandler = new JobHandler(job, convertCallback);
			jobHandlers.put(jobHandler.getJobID(), jobHandler);
		}
		return jobHandler;
	}

	@Override
	public boolean scheduleJob(final Job job, final boolean force, final ConvertCallback convertCallback) {
		final JobHandler jobHandler = createOrUseExistingJobHandlerForJobIfPossible(job, force, convertCallback);
		if (jobHandler == null) {
			return false;
		}

		jobHandler.onJobScheduled();
		return true;
	}

	@Override
	public DownloadFinishedCallback restoreJobIfRunning(Job job, ConvertCallback convertCallback) {
		final JobHandler jobHandler = createOrUseExistingJobHandlerForJobIfPossible(job, true, convertCallback);
		Preconditions.checkState(jobHandler != null);

		if (job.getState() == Job.State.FINISHED && job.getDownloadState() == Job.DownloadState.NOT_DOWNLOADED
				&& job.getDownloadURL() != null) {
			return jobHandler;
		}
		return null;
	}
}
