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

import android.net.Uri;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.protocol.JsonKeys.JsonJobDataKeys;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Job {

	public enum State {
		UNSCHEDULED(-2),
		SCHEDULED(-1),
		READY(0),
		RUNNING(1),
		FINISHED(2),
		FAILED(3);

		private int state;

		private static Map<Integer, State> map = new HashMap<>();
		static {
			for (State legEnum : State.values()) {
				map.put(legEnum.state, legEnum);
			}
		}
		State(final int state) {
			this.state = state;
		}

		public static State valueOf(int state) {
			return map.get(state);
		}

		public boolean isInProgress() {
			return this == SCHEDULED || this == READY || this == RUNNING;
		}

		public int getStateID() {
			return state;
		}
	}

	public enum DownloadState {
		NOT_READY(0),
		READY(1),
		DOWNLOADING(2),
		DOWNLOADED(3),
		CANCELED(4);

		private int downloadState;

		private static Map<Integer, DownloadState> map = new HashMap<>();
		static {
			for (DownloadState legEnum : DownloadState.values()) {
				map.put(legEnum.downloadState, legEnum);
			}
		}
		DownloadState(final int downloadState) {
			this.downloadState = downloadState;
		}

		public static DownloadState valueOf(int downloadState) {
			return map.get(downloadState);
		}

		public int getDownloadStateID() {
			return downloadState;
		}
	}

	private State state;
	private long jobID;
	private String title;
	private WebImage image;
	private short progress;
	private short downloadProgress;
	private DownloadState downloadState;
	private String downloadURL;

	public Job(long jobID, String title, WebImage image) {
		this.state = State.UNSCHEDULED;
		this.jobID = jobID;
		this.title = title;
		this.image = image;
		this.progress = 0;
		this.downloadState = DownloadState.NOT_READY;
		this.downloadURL = null;
	}

	public static Job fromJson(JSONObject data) throws JSONException {
		final State state = State.valueOf(data.getInt(JsonJobDataKeys.STATE.toString()));
		final long jobID = data.getLong(JsonJobDataKeys.JOB_ID.toString());
		final String title = data.getString(JsonJobDataKeys.TITLE.toString());
		final String imageURL = data.isNull(JsonJobDataKeys.IMAGE_URL.toString()) ? null
				: data.getString(JsonJobDataKeys.IMAGE_URL.toString());
		WebImage image = null;
		if (imageURL != null) {
			final int[] imageSize = new int[] { Constants.SCRATCH_IMAGE_DEFAULT_WIDTH, Constants.SCRATCH_IMAGE_DEFAULT_HEIGHT };
			image = new WebImage(Uri.parse(imageURL), imageSize[0], imageSize[1]);
		}
		final short progress = (short) data.getInt(JsonJobDataKeys.PROGRESS.toString());
		final String downloadURL = data.getString(JsonJobDataKeys.DOWNLOAD_URL.toString());
		final Job job = new Job(jobID, title, image);
		job.state = state;
		job.progress = progress;
		job.downloadURL = downloadURL;
		return job;
	}

	public State getState() {
		return state;
	}

	public boolean isInProgress() {
		return state.isInProgress();
	}

	public void setState(State state) {
		this.state = state;
	}

	public long getJobID() {
		return jobID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public short getProgress() {
		return progress;
	}

	public void setProgress(short progress) {
		this.progress = progress;
	}

	public short getDownloadProgress() {
		return downloadProgress;
	}

	public void setDownloadProgress(short downloadProgress) {
		this.downloadProgress = downloadProgress;
	}

	public WebImage getImage() {
		return image;
	}

	public void setImage(WebImage image) {
		this.image = image;
	}

	public DownloadState getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(DownloadState downloadState) {
		this.downloadState = downloadState;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
}
