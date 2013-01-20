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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.catrobat.catroid.common.Constants;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class ConnectionWrapper {

	private final static String TAG = ConnectionWrapper.class.getSimpleName();
	private static final Integer DATA_STREAM_UPDATE_SIZE = 1024 * 16; //16 KB
	private HttpURLConnection urlConnection;

	public static final String FTP_USERNAME = "ftp-uploader";
	public static final String FTP_PASSWORD = "cat.ftp.loader";
	public static final int FILE_TYPE = org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;
	private FTPClient ftpClient = new FTPClient();

	public String doFtpPostFileUpload(String urlString, HashMap<String, String> postValues, String fileTag,
			String filePath, ResultReceiver receiver, String httpPostUrl, Integer notificationId) throws IOException,
			WebconnectionException {
		String answer = "";
		try {
			ftpClient.connect(urlString, ServerCalls.FTP_PORT);
			ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

			int replyCode = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(replyCode)) {
				ftpClient.disconnect();
				Log.e(TAG, "FTP server refused to connect");
				throw new WebconnectionException(replyCode);
			}

			ftpClient.setFileType(FILE_TYPE);
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
			ftpClient.enterLocalPassiveMode();

			String fileName = "";
			if (filePath != null) {
				fileName = postValues.get("projectTitle");
				String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
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
		} catch (IOException e) {
			e.printStackTrace();
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

		HttpBuilder httpBuilder = buildPost(httpPostUrl, postValues);

		httpBuilder.close();

		// response code != 2xx -> error
		urlConnection.getResponseCode();
		if (urlConnection.getResponseCode() / 100 != 2) {
			throw new WebconnectionException(urlConnection.getResponseCode());
		}

		InputStream resultStream = urlConnection.getInputStream();
		String resultString = getString(resultStream);
		Log.v(TAG, resultString);
		return resultString;
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
		progressBundle.putLong("currentDownloadProgress", progress);
		progressBundle.putBoolean("endOfFileReached", endOfFileReached);
		progressBundle.putBoolean("unknown", unknown);
		progressBundle.putInt("notificationId", notificationId);
		progressBundle.putString("projectName", projectName);
		receiver.send(Constants.UPDATE_DOWNLOAD_PROGRESS, progressBundle);
	}

	public void doHttpPostFileDownload(String urlString, HashMap<String, String> postValues, String filePath,
			ResultReceiver receiver, Integer notificationId, String projectName) throws IOException {
		HttpBuilder httpBuilder = buildPost(urlString, postValues);
		httpBuilder.close();

		URL downloadUrl = new URL(urlString);
		urlConnection = (HttpURLConnection) downloadUrl.openConnection();
		urlConnection.connect();
		int fileLength = urlConnection.getContentLength();

		//read response from server
		InputStream input = new BufferedInputStream(urlConnection.getInputStream());
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		OutputStream fos = new FileOutputStream(file);

		byte[] buffer = new byte[Constants.BUFFER_8K];
		int count = 0;
		long bytesWritten = 0;
		while ((count = input.read(buffer)) != -1) {
			bytesWritten += count;
			if (fileLength != -1) {
				if ((bytesWritten % DATA_STREAM_UPDATE_SIZE) == 0) {
					long progress = bytesWritten * 100 / fileLength;
					updateProgress(receiver, progress, false, false, notificationId, projectName);
				}
			} else {
				//progress unknown
				updateProgress(receiver, 0, false, true, notificationId, projectName);
			}
			fos.write(buffer, 0, count);
		}
		//publish last progress (100% at EOF):
		updateProgress(receiver, 100, true, false, notificationId, projectName);

		input.close();
		fos.flush();
		fos.close();
	}

	private String getString(InputStream is) {
		if (is == null) {
			return "";
		}
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr, Constants.BUFFER_8K);

			String line;
			String response = "";
			while ((line = br.readLine()) != null) {
				response += line;
			}
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public String doHttpPost(String urlString, HashMap<String, String> postValues) throws IOException {
		HttpBuilder httpBuilder = buildPost(urlString, postValues);
		//HttpBuilder out = buildPost(urlString, postValues);
		httpBuilder.close();

		InputStream resultStream = null;

		Log.i(TAG, "http response code: " + urlConnection.getResponseCode());
		resultStream = urlConnection.getInputStream();

		return getString(resultStream);
	}

	private HttpBuilder buildPost(String urlString, HashMap<String, String> postValues) throws IOException {
		if (postValues == null) {
			postValues = new HashMap<String, String>();
		}

		URL url = new URL(urlString);

		String boundary = HttpBuilder.createBoundary();
		urlConnection = (HttpURLConnection) HttpBuilder.createConnection(url);

		urlConnection.setRequestProperty("Accept", "*/*");
		urlConnection.setRequestProperty("Content-Type", HttpBuilder.getContentType(boundary));

		urlConnection.setRequestProperty("Connection", "Keep-Alive");
		urlConnection.setRequestProperty("Cache-Control", "no-cache");

		HttpBuilder httpBuilder = new HttpBuilder(urlConnection.getOutputStream(), boundary);

		Set<Entry<String, String>> entries = postValues.entrySet();
		for (Entry<String, String> entry : entries) {
			Log.d(TAG, "key: " + entry.getKey() + ", value: " + entry.getValue());
			httpBuilder.writeField(entry.getKey(), entry.getValue());
		}

		return httpBuilder;
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
