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

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import org.catrobat.catroid.common.Constants;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

	public static final String TAG_PROGRESS = "currentDownloadProgress";
	public static final String TAG_ENDOFFILE = "endOfFileReached";
	public static final String TAG_NOTIFICATION_ID = "notificationId";

	private final ResponseBody responseBody;
	private final ResultReceiver receiver;
	private final int notificationId;

	private BufferedSource bufferedSource;

	public ProgressResponseBody(ResponseBody responseBody, ResultReceiver receiver, int notificationId) throws IOException {
		this.responseBody = responseBody;
		this.receiver = receiver;
		this.notificationId = notificationId;
	}

	@Override
	public MediaType contentType() {
		return responseBody.contentType();
	}

	@Override
	public long contentLength() throws IOException {
		return responseBody.contentLength();
	}

	@Override
	public BufferedSource source() throws IOException {
		if (bufferedSource == null) {
			bufferedSource = Okio.buffer(source(responseBody.source()));
		}
		return bufferedSource;
	}

	private Source source(Source source) {
		return new ForwardingSource(source) {
			long totalBytesRead = 0L;

			@Override
			public long read(Buffer sink, long byteCount) throws IOException {
				long bytesRead = super.read(sink, byteCount);
				totalBytesRead += bytesRead != -1 ? bytesRead : 0;
				sendUpdateIntent((100 * totalBytesRead) / contentLength(), bytesRead == -1);
				return bytesRead;
			}
		};
	}

	private void sendUpdateIntent(long progress, boolean endOfFileReached) {
		Bundle progressBundle = new Bundle();
		progressBundle.putLong(TAG_PROGRESS, progress);
		progressBundle.putBoolean(TAG_ENDOFFILE, endOfFileReached);
		progressBundle.putInt(TAG_NOTIFICATION_ID, notificationId);
		receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, progressBundle);
	}
}
