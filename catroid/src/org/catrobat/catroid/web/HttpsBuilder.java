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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.entity.StringEntity;

class HttpsBuilder {

	private static final String HTTP_NEWLINE = "\r\n";
	private static final String HTTP_PREFIX = "--";
	private static final String HTTP_BOUNDARY_PREFIX = "--------------------";

	private DataOutputStream outputStream;
	private String boundary;

	public HttpsBuilder(OutputStream stream, String boundary) {
		if (stream == null) {
			throw new IllegalArgumentException("Output stream is null");
		}
		if (boundary == null || boundary.length() == 0) {
			throw new IllegalArgumentException("Boundary is null");
		}
		this.outputStream = new DataOutputStream(stream);
		this.boundary = boundary;
	}

	public static String createBoundary() {
		return HTTP_BOUNDARY_PREFIX + Long.toString(System.currentTimeMillis(), 16);
	}

	public static URLConnection createConnection(URL url) throws IOException {
		URLConnection urlConnection = url.openConnection();
		if (urlConnection instanceof HttpsURLConnection) {
			HttpsURLConnection httpConnection = (HttpsURLConnection) urlConnection;
			httpConnection.setRequestMethod("POST");
		}
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setDefaultUseCaches(false);
		return urlConnection;
	}

	public static String getContentType(String boundary) {
		return "multipart/form-data; boundary=" + boundary;
	}

	public void writeField(String name, String value) throws IOException {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null or empty.");
		}
		if (value == null) {
			value = "";
		}
		// write boundary
		outputStream.writeBytes(HTTP_PREFIX);
		outputStream.writeBytes(boundary);
		outputStream.writeBytes(HTTP_NEWLINE);
		// write content header
		outputStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
		outputStream.writeBytes(HTTP_NEWLINE);
		outputStream.writeBytes(HTTP_NEWLINE);
		// write content
		StringEntity valueEntity = new StringEntity(value, "UTF-8");
		valueEntity.writeTo(outputStream);

		outputStream.writeBytes(HTTP_NEWLINE);
		outputStream.flush();
	}

	public void close() throws IOException {
		// write final boundary
		outputStream.writeBytes(HTTP_PREFIX);
		outputStream.writeBytes(boundary);
		outputStream.writeBytes(HTTP_PREFIX);
		outputStream.writeBytes(HTTP_NEWLINE);
		outputStream.flush();
		outputStream.close();
	}
}
