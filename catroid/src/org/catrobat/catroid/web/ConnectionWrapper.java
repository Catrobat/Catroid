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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.catrobat.catroid.common.Constants;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class ConnectionWrapper {

	private final static String TAG = ConnectionWrapper.class.getSimpleName();

	public static final String FTP_USERNAME = "ftp-uploader";
	public static final String FTP_PASSWORD = "cat.ftp.loader";
	public static final int FILE_TYPE = org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;
	public static final String FTP_ENCODING = "UTF-8";

	public static final String TAG_PROGRESS = "currentDownloadProgress";
	public static final String TAG_ENDOFFILE = "endOfFileReached";
	public static final String TAG_UNKNOWN = "unknown";
	public static final String TAG_NOTIFICATION_ID = "notificationId";
	public static final String TAG_PROJECT_NAME = "projectName";
	public static final String TAG_PROJECT_TITLE = "projectTitle";

	private FTPClient ftpClient = new FTPClient();

	public String doFtpPostFileUpload(String urlString, HashMap<String, String> postValues, String fileTag,
			String filePath, ResultReceiver receiver, String httpPostUrl, Integer notificationId) throws IOException,
			WebconnectionException {
		String answer = "";
		try {
			// important to call this before connect
			ftpClient.setControlEncoding(FTP_ENCODING);

			ftpClient.connect(urlString, ServerCalls.FTP_PORT);
			ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

			int replyCode = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(replyCode)) {
				ftpClient.disconnect();
				Log.e(TAG, "FTP server refused to connect");
				throw new WebconnectionException(replyCode, "FTP server refused to connect!");
			}

			ftpClient.setFileType(FILE_TYPE);
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
			ftpClient.enterLocalPassiveMode();

			String fileName = "";
			if (filePath != null) {
				fileName = postValues.get(TAG_PROJECT_TITLE);
				String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase(Locale.ENGLISH);
				FtpProgressInputStream ftpProgressStream = new FtpProgressInputStream(inputStream, receiver,
						notificationId, fileName);
				boolean result = ftpClient.storeFile(fileName + "." + extension, ftpProgressStream);

				if (!result) {
					throw new IOException();
				}
			}

			inputStream.close();
			ftpClient.logout();
			ftpClient.disconnect();

			answer = sendUploadPost(httpPostUrl, postValues, fileTag, filePath);

		} catch (SocketException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, "FTP server refused to connect!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK, "FTP connection problem!");
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return answer;
	}

	private String sendUploadPost(String httpPostUrl, HashMap<String, String> postValues, String fileTag,
			String filePath) throws IOException, WebconnectionException {
		try {
			HttpRequest request = HttpRequest.post(httpPostUrl).form(postValues);
			if (!(request.code() == 200 || request.code() == 201)) {
				throw new WebconnectionException(request.code(), "Error response code should be 200 or 201!");
			}
			return request.body();
		} catch (HttpRequestException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK,
					"Connection could not be established!");
		}
	}

	void updateProgress(ResultReceiver receiver, long progress, boolean endOfFileReached, boolean unknown,
			Integer notificationId, String projectName) {
		//send for every 20 kilobytes read a message to update the progress
		if ((!endOfFileReached)) {
			sendUpdateIntent(receiver, progress, false, unknown, notificationId, projectName);
		} else if (endOfFileReached) {
			sendUpdateIntent(receiver, progress, true, unknown, notificationId, projectName);
		}
	}

	private void sendUpdateIntent(ResultReceiver receiver, long progress, boolean endOfFileReached, boolean unknown,
			Integer notificationId, String projectName) {
		Bundle progressBundle = new Bundle();
		progressBundle.putLong(TAG_PROGRESS, progress);
		progressBundle.putBoolean(TAG_ENDOFFILE, endOfFileReached);
		progressBundle.putBoolean(TAG_UNKNOWN, unknown);
		progressBundle.putInt(TAG_NOTIFICATION_ID, notificationId);
		progressBundle.putString(TAG_PROJECT_NAME, projectName);
		receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, progressBundle);
	}

	public void doHttpPostFileDownload(String urlString, HashMap<String, String> postValues, String filePath,
			ResultReceiver receiver, Integer notificationId, String projectName) throws IOException {
		HttpRequest request = HttpRequest.post(urlString);
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		request.form(postValues).acceptGzipEncoding().receive(file);
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

	/*
	 * public String doHttpPostFileUpload(String urlString, HashMap<String, String> postValues, String fileTag,
	 * String filePath) throws IOException, WebconnectionException {
	 * 
	 * MultiPartFormOutputStream out = buildPost(urlString, postValues);
	 * 
	 * if (filePath != null) {
	 * String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
	 * String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	 * 
	 * out.writeFile(fileTag, mimeType, new File(filePath));
	 * }
	 * out.close();
	 * 
	 * // response code != 2xx -> error
	 * if (urlConnection.getResponseCode() / 100 != 2) {
	 * throw new WebconnectionException(urlConnection.getResponseCode());
	 * }
	 * 
	 * InputStream resultStream = urlConnection.getInputStream();
	 * 
	 * return getString(resultStream);
	 * }
	 */

}
