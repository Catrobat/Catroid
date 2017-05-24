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

package org.catrobat.catroid.scratchconverter.protocol;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.images.WebImage;
import com.google.common.base.Preconditions;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ClientException;
import org.catrobat.catroid.scratchconverter.protocol.Job.State;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobAlreadyRunningMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFailedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobFinishedMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobOutputMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobProgressMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobReadyMessage;
import org.catrobat.catroid.scratchconverter.protocol.message.job.JobRunningMessage;

public class JobHandler implements Client.DownloadCallback {

	private static final String TAG = JobHandler.class.getSimpleName();

	private final Job job;
	private Client.ConvertCallback callback;

	public JobHandler(@NonNull final Job job, @NonNull final Client.ConvertCallback callback) {
		Preconditions.checkArgument(job != null);
		this.job = job;
		this.callback = callback;
	}

	public boolean isInProgress() {
		return job.isInProgress();
	}

	public void onJobScheduled() {
		Log.d(TAG, "Setting job as scheduled (jobID: " + job.getJobID() + ")");
		job.setState(State.SCHEDULED);
		callback.onJobScheduled(job);
	}

	@Override
	public void onDownloadStarted(final String url) {
		Log.d(TAG, "Download started - Job ID is: " + job.getJobID());
		job.setDownloadState(Job.DownloadState.DOWNLOADING);
		job.setState(State.FINISHED);
	}

	@Override
	public void onDownloadProgress(short progress, String url) {
	}

	@Override
	public void onDownloadFinished(final String programName, final String url) {
		Log.d(TAG, "Download finished - Resetting job with ID: " + job.getJobID());
		job.setDownloadState(Job.DownloadState.DOWNLOADED);
		job.setState(State.FINISHED);
	}

	@Override
	public void onUserCanceledDownload(final String url) {
		Log.d(TAG, "User canceled download - Resetting job with ID: " + job.getJobID());
		job.setDownloadState(Job.DownloadState.CANCELED);
		job.setState(State.FINISHED);
	}

	public void onUserCanceledConversion() {
		Log.d(TAG, "User canceled conversion - Resetting job with ID: " + job.getJobID());
		job.setState(State.FINISHED);
	}

	public Job getJob() {
		return job;
	}

	public long getJobID() {
		return job.getJobID();
	}

	public void setCallback(@NonNull Client.ConvertCallback callback) {
		this.callback = callback;
	}

	public void onJobMessage(final JobMessage jobMessage) {
		Preconditions.checkArgument(job.getJobID() == jobMessage.getJobID());
		Preconditions.checkState(job.getState().isInProgress());

		switch (job.getState()) {
			case SCHEDULED:
				if (jobMessage instanceof JobReadyMessage) {
					handleJobReadyMessage((JobReadyMessage) jobMessage);
					return;
				} else if (jobMessage instanceof JobAlreadyRunningMessage) {
					handleJobAlreadyRunningMessage((JobAlreadyRunningMessage) jobMessage);
					return;
				} else if (jobMessage instanceof JobFinishedMessage) {
					handleJobFinishedMessage((JobFinishedMessage) jobMessage);
					return;
				} else if (jobMessage instanceof JobFailedMessage) {
					handleJobFailedMessage((JobFailedMessage) jobMessage);
					return;
				}
				break;

			case READY:
				if (jobMessage instanceof JobRunningMessage) {
					handleJobRunningMessage((JobRunningMessage) jobMessage);
					return;
				}
				break;

			case RUNNING:
				if (jobMessage instanceof JobProgressMessage) {
					handleJobProgressMessage((JobProgressMessage) jobMessage);
					return;
				} else if (jobMessage instanceof JobOutputMessage) {
					handleJobOutputMessage((JobOutputMessage) jobMessage);
					return;
				} else if (jobMessage instanceof JobFinishedMessage) {
					handleJobFinishedMessage((JobFinishedMessage) jobMessage);
					return;
				} else if (jobMessage instanceof JobFailedMessage) {
					handleJobFailedMessage((JobFailedMessage) jobMessage);
					return;
				}
				break;
		}

		Log.w(TAG, "Unable to handle message of type in current state " + job.getState());
	}

	private void handleJobReadyMessage(@NonNull final JobReadyMessage jobReadyMessage) {
		Preconditions.checkArgument(getJob().getJobID() == jobReadyMessage.getJobID());
		Preconditions.checkState(job.getState() == State.SCHEDULED);

		job.setState(Job.State.READY);
		callback.onConversionReady(job);
	}

	private void handleJobAlreadyRunningMessage(@NonNull final JobAlreadyRunningMessage jobAlreadyRunningMessage) {
		Preconditions.checkArgument(getJob().getJobID() == jobAlreadyRunningMessage.getJobID());
		Preconditions.checkState(job.getState() == State.SCHEDULED);

		job.setState(Job.State.READY);
		final long jobID = jobAlreadyRunningMessage.getJobID();
		final String jobTitle = jobAlreadyRunningMessage.getJobTitle();
		final String jobImageURL = jobAlreadyRunningMessage.getJobImageURL();
		handleJobRunningMessage(new JobRunningMessage(jobID, jobTitle, jobImageURL));
	}

	private void handleJobRunningMessage(@NonNull final JobRunningMessage jobRunningMessage) {
		Preconditions.checkArgument(getJob().getJobID() == jobRunningMessage.getJobID());
		Preconditions.checkState(job.getState() == State.READY);

		job.setTitle(jobRunningMessage.getJobTitle());
		final String jobImageURL = jobRunningMessage.getJobImageURL();
		if (jobImageURL != null) {
			final int[] imageSize = new int[] {Constants.SCRATCH_IMAGE_DEFAULT_WIDTH, Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT};
			job.setImage(new WebImage(Uri.parse(jobImageURL), imageSize[0], imageSize[1]));
		}
		job.setState(Job.State.RUNNING);
		callback.onConversionStart(job);
	}

	private void handleJobProgressMessage(@NonNull final JobProgressMessage jobProgressMessage) {
		Preconditions.checkArgument(getJob().getJobID() == jobProgressMessage.getJobID());
		Preconditions.checkState(job.getState() == State.RUNNING);

		job.setProgress(jobProgressMessage.getProgress());
		callback.onJobProgress(job, jobProgressMessage.getProgress());
	}

	private void handleJobOutputMessage(@NonNull final JobOutputMessage jobOutputMessage) {
		Preconditions.checkArgument(getJob().getJobID() == jobOutputMessage.getJobID());
		Preconditions.checkState(job.getState() == State.RUNNING);

		final String[] lines = jobOutputMessage.getLines();
		for (String line : lines) {
			Log.d(TAG, line);
		}
		callback.onJobOutput(job, lines);
	}

	private void handleJobFinishedMessage(@NonNull final JobFinishedMessage jobFinishedMessage) {
		Preconditions.checkArgument(getJob().getJobID() == jobFinishedMessage.getJobID());
		Preconditions.checkState(job.getState() == State.SCHEDULED || job.getState() == State.RUNNING);

		job.setState(State.FINISHED);
		job.setDownloadURL(jobFinishedMessage.getDownloadURL());
		callback.onConversionFinished(job, this, jobFinishedMessage.getDownloadURL(),
				jobFinishedMessage.getCachedDate());
	}

	private void handleJobFailedMessage(@NonNull final JobFailedMessage jobFailedMessage) {
		Preconditions.checkArgument(getJob().getJobID() == jobFailedMessage.getJobID());
		Preconditions.checkState(job.getState() == State.SCHEDULED || job.getState() == State.RUNNING);

		job.setState(Job.State.FAILED);
		callback.onConversionFailure(job, new ClientException("Job failed - Reason: " + jobFailedMessage.getMessage()));
	}
}
