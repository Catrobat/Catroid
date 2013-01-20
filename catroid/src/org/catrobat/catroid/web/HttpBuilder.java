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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.catrobat.catroid.common.Constants;

public class HttpBuilder {

	private DataOutputStream outputStream = null;
	private String boundary = null;

	public HttpBuilder(OutputStream stream, String boundary) {
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
		return Constants.HTTP_BOUNDARY_PREFIX + Long.toString(System.currentTimeMillis(), 16);
	}

	public static URLConnection createConnection(URL url) throws IOException {
		URLConnection urlConnonnection = url.openConnection();
		if (urlConnonnection instanceof HttpURLConnection) {
			HttpURLConnection httpConnection = (HttpURLConnection) urlConnonnection;
			httpConnection.setRequestMethod("POST");
		}
		urlConnonnection.setDoInput(true);
		urlConnonnection.setDoOutput(true);
		urlConnonnection.setUseCaches(false);
		urlConnonnection.setDefaultUseCaches(false);
		return urlConnonnection;
	}

	public String getBoundary() {
		return this.boundary;
	}

	public static String getContentType(String boundary) {
		return "multipart/form-data; boundary=" + boundary;
	}

	public void writeField(String name, String value) throws IOException {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null or empty.");
		}
		if (value == null) {
			value = "";
		}
		// write boundary
		outputStream.writeBytes(Constants.HTTP_PREFIX);
		outputStream.writeBytes(boundary);
		outputStream.writeBytes(Constants.HTTP_NEWLINE);
		// write content header
		outputStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
		outputStream.writeBytes(Constants.HTTP_NEWLINE);
		outputStream.writeBytes(Constants.HTTP_NEWLINE);
		// write content
		outputStream.writeBytes(value);
		outputStream.writeBytes(Constants.HTTP_NEWLINE);
		outputStream.flush();
	}

	public void close() throws IOException {
		// write final boundary
		outputStream.writeBytes(Constants.HTTP_PREFIX);
		outputStream.writeBytes(boundary);
		outputStream.writeBytes(Constants.HTTP_PREFIX);
		outputStream.writeBytes(Constants.HTTP_NEWLINE);
		outputStream.flush();
		outputStream.close();
	}
}
