/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import android.os.ResultReceiver;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

//web status codes are on: https://github.com/Catrobat/Catroweb/blob/master/statusCodes.php

public class ConnectionWrapper {

	private final static String TAG = ConnectionWrapper.class.getSimpleName();

	public static final String TAG_PROGRESS = "currentDownloadProgress";
	public static final String TAG_ENDOFFILE = "endOfFileReached";
	public static final String TAG_UNKNOWN = "unknown";
	public static final String TAG_NOTIFICATION_ID = "notificationId";
	public static final String TAG_PROJECT_NAME = "projectName";
	public static final String TAG_PROJECT_TITLE = "projectTitle";

	public String doHttpsPostFileUpload(String urlString, HashMap<String, String> postValues, String fileTag,
			String filePath, ResultReceiver receiver, Integer notificationId) throws IOException,
			WebconnectionException {

		String answer = "";
		String fileName = postValues.get(TAG_PROJECT_TITLE);

		if (filePath != null) {
			HttpRequest uploadRequest = HttpRequest.post(urlString);

			for (String key : postValues.keySet()) {
				uploadRequest.part(key, postValues.get(key));
			}
			File file = new File(filePath);
			uploadRequest.part(fileTag, fileName, file);

			int responseCode = uploadRequest.code();
			if (!(responseCode == 200 || responseCode == 201)) {
				throw new WebconnectionException(responseCode, "Error response code should be 200 or 201!");
			}
			if (!uploadRequest.ok()) {
				Log.v(TAG, "Upload not succesful");
			}

			answer = uploadRequest.body();
			Log.v(TAG, "Upload response is: " + answer);
		}
		return answer;
	}

	public void doHttpPostFileDownload(String urlString, HashMap<String, String> postValues, String filePath,
			ResultReceiver receiver, Integer notificationId, String projectName) throws IOException {
		HttpRequest request = HttpRequest.post(urlString);
		File file = new File(filePath);
		file.getParentFile().mkdirs();

		request = request.form(postValues).acceptGzipEncoding();
		long fileSize = request.contentLength();
		OutputStream stream = new ProgressBufferedOutputStream(new FileOutputStream(file), request.bufferSize(),
				fileSize, receiver, notificationId, projectName);
		request.receive(stream);
		stream.close();
	}

	public String doHttpPost(String urlString, HashMap<String, String> postValues) throws WebconnectionException {
		try {
			return HttpRequest.post(urlString).form(postValues).body();
		} catch (HttpRequestException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
					"Connection could not be established!");
		}
	}
}
