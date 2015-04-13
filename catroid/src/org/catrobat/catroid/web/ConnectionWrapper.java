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

import android.os.ResultReceiver;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import org.catrobat.catroid.utils.StatusBarNotificationManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

//web status codes are on: https://github.com/Catrobat/Catroweb/blob/master/statusCodes.php

public class ConnectionWrapper {

	private static final String TAG = ConnectionWrapper.class.getSimpleName();

	public static final String TAG_PROJECT_TITLE = "projectTitle";

	public String doHttpsPostFileUpload(String urlString, HashMap<String, String> postValues, String fileTag,
			String filePath, ResultReceiver receiver, Integer notificationId) throws IOException,
			WebconnectionException {

		String answer = "";
		String fileName = postValues.get(TAG_PROJECT_TITLE);

		if (filePath != null) {
			try {
				HttpRequest uploadRequest = HttpRequest.post(urlString).chunk(0);

				for (HashMap.Entry<String, String> entry : postValues.entrySet()) {
					uploadRequest.part(entry.getKey(), entry.getValue());
				}
				File file = new File(filePath);
				uploadRequest.part(fileTag, fileName, file);
				int responseCode = uploadRequest.code();
				if (!(responseCode == 200 || responseCode == 201)) {
					throw new WebconnectionException(responseCode, "Error response code should be 200 or 201!");
				}
				if (!uploadRequest.ok()) {
					Log.v(TAG, "Upload not succesful");
					StatusBarNotificationManager.getInstance().cancelNotification(notificationId);
				} else {
					StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationId, 100);
				}

				answer = uploadRequest.body();
				Log.v(TAG, "Upload response is: " + answer);
			} catch (HttpRequestException httpRequestException) {
				Log.e(TAG, Log.getStackTraceString(httpRequestException));
				throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
						"Connection could not be established!");
			}
		}
		return answer;
	}

	public void doHttpPostFileDownload(String urlString, HashMap<String, String> postValues, String filePath,
			ResultReceiver receiver, Integer notificationId) throws IOException, WebconnectionException {
		try {
			HttpRequest request = HttpRequest.post(urlString);
			File file = new File(filePath);
			if (!(file.getParentFile().mkdirs() || file.getParentFile().isDirectory())) {
				throw (new IOException("Folder not created"));
			}

			request = request.form(postValues).acceptGzipEncoding();
			long fileSize = request.contentLength();
			OutputStream stream = new ProgressBufferedOutputStream(new FileOutputStream(file), request.bufferSize(),
					fileSize, receiver, notificationId);
			request.receive(stream);
			stream.close();
		} catch (HttpRequestException httpRequestException) {
			Log.e(TAG, Log.getStackTraceString(httpRequestException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
					"Connection could not be established!");
		}
	}

	public String doHttpPost(String urlString, HashMap<String, String> postValues) throws WebconnectionException {
		try {
			return HttpRequest.post(urlString).form(postValues).body();
		} catch (HttpRequestException httpRequestException) {
			Log.e(TAG, Log.getStackTraceString(httpRequestException));
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
					"Connection could not be established!");
		}
	}
}
