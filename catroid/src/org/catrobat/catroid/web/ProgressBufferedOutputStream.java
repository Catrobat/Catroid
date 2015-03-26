/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.catrobat.catroid.common.Constants;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProgressBufferedOutputStream extends BufferedOutputStream {

	public static final String TAG_PROGRESS = "currentDownloadProgress";
	public static final String TAG_ENDOFFILE = "endOfFileReached";
	public static final String TAG_NOTIFICATION_ID = "notificationId";

	private static final String TAG = ProgressBufferedOutputStream.class.getSimpleName();

	private long fileSize;
	private long currentFileStatus;
	private ResultReceiver receiver;
	private Integer notificationId;

	public ProgressBufferedOutputStream(OutputStream out, int size, long fileSize, ResultReceiver receiver,
			Integer notificationId) throws IOException {
		super(out, size);

		this.fileSize = fileSize;
		this.receiver = receiver;
		this.notificationId = notificationId;
		currentFileStatus = 0;
	}

	@Override
	public void write(int b) {
		Log.wtf(TAG, "this write method isn't supported");
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);

		currentFileStatus += len;
		Log.v(TAG, "download status: " + currentFileStatus + "/" + fileSize);
		sendUpdateIntent((100 * currentFileStatus) / fileSize, false);
	}

	@Override
	public void close() throws IOException {
		super.close();

		sendUpdateIntent(100, true);
	}

	private void sendUpdateIntent(long progress, boolean endOfFileReached) {
		Bundle progressBundle = new Bundle();
		progressBundle.putLong(TAG_PROGRESS, progress);
		progressBundle.putBoolean(TAG_ENDOFFILE, endOfFileReached);
		progressBundle.putInt(TAG_NOTIFICATION_ID, notificationId);
		receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, progressBundle);
	}

}
