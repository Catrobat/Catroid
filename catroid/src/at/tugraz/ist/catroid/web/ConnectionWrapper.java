/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.web;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import android.util.Log;
import android.webkit.MimeTypeMap;
import at.tugraz.ist.catroid.common.Consts;

public class ConnectionWrapper {

	private HttpURLConnection urlConnection;
	private final static String TAG = ConnectionWrapper.class.getSimpleName();

	private String getString(InputStream is) {
		if (is == null) {
			return "";
		}
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr, Consts.BUFFER_8K);

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

	public String doHttpPostFileUpload(String urlString, HashMap<String, String> postValues, String fileTag,
			String filePath) throws IOException, WebconnectionException {

		MultiPartFormOutputStream out = buildPost(urlString, postValues);

		if (filePath != null) {
			String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
			String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

			out.writeFile(fileTag, mimeType, new File(filePath));
		}
		out.close();

		// response code != 2xx -> error
		if (urlConnection.getResponseCode() / 100 != 2) {
			throw new WebconnectionException(urlConnection.getResponseCode());
		}

		InputStream resultStream = urlConnection.getInputStream();

		return getString(resultStream);
	}

	public void doHttpPostFileDownload(String urlString, HashMap<String, String> postValues, String filePath)
			throws IOException {
		MultiPartFormOutputStream out = buildPost(urlString, postValues);
		out.close();

		// read response from server
		DataInputStream input = new DataInputStream(urlConnection.getInputStream());

		File file = new File(filePath);
		file.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(file);

		byte[] buffer = new byte[Consts.BUFFER_8K];
		int length = 0;
		while ((length = input.read(buffer)) != -1) {
			fos.write(buffer, 0, length);
		}
		input.close();
		fos.flush();
		fos.close();
	}

	public String doHttpPost(String urlString, HashMap<String, String> postValues) throws IOException {
		MultiPartFormOutputStream out = buildPost(urlString, postValues);
		out.close();

		InputStream resultStream = null;

		Log.i(TAG, "http response code: " + urlConnection.getResponseCode());
		resultStream = urlConnection.getInputStream();

		return getString(resultStream);
	}

	private MultiPartFormOutputStream buildPost(String urlString, HashMap<String, String> postValues)
			throws IOException {
		if (postValues == null) {
			postValues = new HashMap<String, String>();
		}

		URL url = new URL(urlString);

		String boundary = MultiPartFormOutputStream.createBoundary();
		urlConnection = (HttpURLConnection) MultiPartFormOutputStream.createConnection(url);

		urlConnection.setRequestProperty("Accept", "*/*");
		urlConnection.setRequestProperty("Content-Type", MultiPartFormOutputStream.getContentType(boundary));

		urlConnection.setRequestProperty("Connection", "Keep-Alive");
		urlConnection.setRequestProperty("Cache-Control", "no-cache");

		MultiPartFormOutputStream out = new MultiPartFormOutputStream(urlConnection.getOutputStream(), boundary);

		Set<Entry<String, String>> entries = postValues.entrySet();
		for (Entry<String, String> entry : entries) {
			Log.d(TAG, "key: " + entry.getKey() + ", value: " + entry.getValue());
			out.writeField(entry.getKey(), entry.getValue());
		}

		return out;
	}
}
