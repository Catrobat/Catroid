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
package org.catrobat.catroid.web;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

	public static final String TAG_PROGRESS = "currentDownloadProgress";
	public static final String TAG_ENDOFFILE = "endOfFileReached";
	public static final String TAG_NOTIFICATION_ID = "notificationId";
	public static final String TAG_REQUEST_URL = "requestUrl";

	private final ResponseBody responseBody;
	private BufferedSource bufferedSource;
	private ServerCalls.DownloadProgressCallback progressCallback;

	ProgressResponseBody(ResponseBody responseBody,
			ServerCalls.DownloadProgressCallback progressCallback) {
		this.responseBody = responseBody;
	}

	@Override
	public MediaType contentType() {
		return responseBody.contentType();
	}

	@Override
	public long contentLength() {
		return responseBody.contentLength();
	}

	@Override
	public BufferedSource source() {
		if (bufferedSource == null) {
			bufferedSource = Okio.buffer(source(responseBody.source()));
		}
		return bufferedSource;
	}

	private Source source(Source source) {
		return new ForwardingSource(source) {
			long totalBytesRead = 0L;
			long lastProgress = -1L;

			@Override
			public long read(@NotNull Buffer sink, long byteCount) throws IOException {
				long bytesRead = super.read(sink, byteCount);
				totalBytesRead += bytesRead != -1 ? bytesRead : 0;
				long progress = (100 * totalBytesRead) / contentLength();
				boolean endOfFile = bytesRead == -1;
				if (progress > lastProgress || endOfFile) {
					sendUpdateIntent(progress, endOfFile);
					lastProgress = progress;
				}
				return bytesRead;
			}
		};
	}

	private void sendUpdateIntent(long progress, boolean endOfFileReached) {
		progressCallback.onProgress(progress);
	}
}
