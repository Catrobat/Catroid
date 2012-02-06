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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import at.tugraz.ist.catroid.common.Consts;

/**
 * <code>MultiPartFormOutputStream</code> is used to write
 * "multipart/form-data" to a <code>java.net.URLConnection</code> for
 * POSTing. This is primarily for file uploading to HTTP servers.
 * 
 * @since JDK1.3
 */
public class MultiPartFormOutputStream {
	/**
	 * The line end characters.
	 */
	private static final String NEWLINE = "\r\n";

	/**
	 * The boundary prefix.
	 */
	private static final String PREFIX = "--";

	/**
	 * The output stream to write to.
	 */
	private DataOutputStream out = null;

	/**
	 * The multipart boundary string.
	 */
	private String boundary = null;

	/**
	 * Creates a new <code>MultiPartFormOutputStream</code> object using
	 * the specified output stream and boundary. The boundary is required
	 * to be created before using this method, as described in the
	 * description for the <code>getContentType(String)</code> method.
	 * The boundary is only checked for <code>null</code> or empty string,
	 * but it is recommended to be at least 6 characters. (Or use the
	 * static createBoundary() method to create one.)
	 * 
	 * @param os
	 *            the output stream
	 * @param boundary
	 *            the boundary
	 * @see #createBoundary()
	 * @see #getContentType(String)
	 */
	public MultiPartFormOutputStream(OutputStream os, String boundary) {
		if (os == null) {
			throw new IllegalArgumentException("Output stream is required.");
		}
		if (boundary == null || boundary.length() == 0) {
			throw new IllegalArgumentException("Boundary stream is required.");
		}
		this.out = new DataOutputStream(os);
		this.boundary = boundary;
	}

	/**
	 * Writes an boolean field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, boolean value) throws IOException {
		writeField(name, new Boolean(value).toString());
	}

	/**
	 * Writes an double field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, double value) throws IOException {
		writeField(name, Double.toString(value));
	}

	/**
	 * Writes an float field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, float value) throws IOException {
		writeField(name, Float.toString(value));
	}

	/**
	 * Writes an long field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, long value) throws IOException {
		writeField(name, Long.toString(value));
	}

	/**
	 * Writes an int field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, int value) throws IOException {
		writeField(name, Integer.toString(value));
	}

	/**
	 * Writes an short field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, short value) throws IOException {
		writeField(name, Short.toString(value));
	}

	/**
	 * Writes an char field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, char value) throws IOException {
		writeField(name, new Character(value).toString());
	}

	/**
	 * Writes an string field value. If the value is null, an empty string
	 * is sent ("").
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, String value) throws IOException {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null or empty.");
		}
		if (value == null) {
			value = "";
		}
		/*
		 * --boundary\r\n
		 * Content-Disposition: form-data; name="<fieldName>"\r\n
		 * \r\n
		 * <value>\r\n
		 */
		// write boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(NEWLINE);
		// write content header
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
		out.writeBytes(NEWLINE);
		out.writeBytes(NEWLINE);
		// write content
		out.writeBytes(value);
		out.writeBytes(NEWLINE);
		out.flush();
	}

	/**
	 * Writes a file's contents. If the file is null, does not exists, or
	 * is a directory, a <code>java.lang.IllegalArgumentException</code> will be thrown.
	 * 
	 * @param name
	 *            the field name
	 * @param mimeType
	 *            the file content type (optional, recommended)
	 * @param file
	 *            the file (the file must exist)
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeFile(String name, String mimeType, File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null.");
		}
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist.");
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("File cannot be a directory.");
		}
		writeFile(name, mimeType, file.getCanonicalPath(), new FileInputStream(file));
	}

	/**
	 * Writes a input stream's contents. If the input stream is null, a <code>java.lang.IllegalArgumentException</code>
	 * will be thrown.
	 * 
	 * @param name
	 *            the field name
	 * @param mimeType
	 *            the file content type (optional, recommended)
	 * @param fileName
	 *            the file name (required)
	 * @param is
	 *            the input stream
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeFile(String name, String mimeType, String fileName, InputStream is) throws IOException {
		if (is == null) {
			throw new IllegalArgumentException("Input stream cannot be null.");
		}
		if (fileName == null || fileName.length() == 0) {
			throw new IllegalArgumentException("File name cannot be null or empty.");
		}
		/*
		 * --boundary\r\n
		 * Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
		 * Content-Type: <mime-type>\r\n
		 * \r\n
		 * <file-data>\r\n
		 */
		// write boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(NEWLINE);
		// write content header
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"");
		out.writeBytes(NEWLINE);
		if (mimeType != null) {
			out.writeBytes("Content-Type: " + mimeType);
			out.writeBytes(NEWLINE);
		}
		out.writeBytes(NEWLINE);
		// write content
		byte[] data = new byte[Consts.BUFFER_8K];
		int length = 0;
		while ((length = is.read(data, 0, data.length)) != -1) {
			out.write(data, 0, length);
		}
		// close input stream, but ignore any possible exception for it
		try {
			is.close();
		} catch (Exception e) {
		}
		out.writeBytes(NEWLINE);
		out.flush();
	}

	/**
	 * Writes the given bytes. The bytes are assumed to be the contents
	 * of a file, and will be sent as such. If the data is null, a <code>java.lang.IllegalArgumentException</code> will
	 * be thrown.
	 * 
	 * @param name
	 *            the field name
	 * @param mimeType
	 *            the file content type (optional, recommended)
	 * @param fileName
	 *            the file name (required)
	 * @param data
	 *            the file data
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeFile(String name, String mimeType, String fileName, byte[] data) throws IOException {
		if (data == null) {
			throw new IllegalArgumentException("Data cannot be null.");
		}
		if (fileName == null || fileName.length() == 0) {
			throw new IllegalArgumentException("File name cannot be null or empty.");
		}
		/*
		 * --boundary\r\n
		 * Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
		 * Content-Type: <mime-type>\r\n
		 * \r\n
		 * <file-data>\r\n
		 */
		// write boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(NEWLINE);
		// write content header
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"");
		out.writeBytes(NEWLINE);
		if (mimeType != null) {
			out.writeBytes("Content-Type: " + mimeType);
			out.writeBytes(NEWLINE);
		}
		out.writeBytes(NEWLINE);
		// write content
		out.write(data, 0, data.length);
		out.writeBytes(NEWLINE);
		out.flush();
	}

	/**
	 * Flushes the stream. Actually, this method does nothing, as the only
	 * write methods are highly specialized and automatically flush.
	 * 
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void flush() throws IOException {
		// out.flush();
	}

	/**
	 * Closes the stream. <br />
	 * <br />
	 * <b>NOTE:</b> This method <b>MUST</b> be called to finalize the
	 * multipart stream.
	 * 
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void close() throws IOException {
		// write final boundary
		out.writeBytes(PREFIX);
		out.writeBytes(boundary);
		out.writeBytes(PREFIX);
		out.writeBytes(NEWLINE);
		out.flush();
		out.close();
	}

	/**
	 * Gets the multipart boundary string being used by this stream.
	 * 
	 * @return the boundary
	 */
	public String getBoundary() {
		return this.boundary;
	}

	/**
	 * Creates a new <code>java.net.URLConnection</code> object from the
	 * specified <code>java.net.URL</code>. This is a convenience method
	 * which will set the <code>doInput</code>, <code>doOutput</code>, <code>useCaches</code> and
	 * <code>defaultUseCaches</code> fields to
	 * the appropriate settings in the correct order.
	 * 
	 * @return a <code>java.net.URLConnection</code> object for the URL
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public static URLConnection createConnection(URL url) throws IOException {
		URLConnection urlConn = url.openConnection();
		if (urlConn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) urlConn;
			httpConn.setRequestMethod("POST");
		}
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setDefaultUseCaches(false);
		return urlConn;
	}

	/**
	 * Creates a multipart boundary string by concatenating 20 hyphens (-)
	 * and the hexadecimal (base-16) representation of the current time in
	 * milliseconds.
	 * 
	 * @return a multipart boundary string
	 * @see #getContentType(String)
	 */
	public static String createBoundary() {
		return "--------------------" + Long.toString(System.currentTimeMillis(), 16);
	}

	/**
	 * Gets the content type string suitable for the <code>java.net.URLConnection</code> which includes the multipart
	 * boundary string. <br />
	 * <br />
	 * This method is static because, due to the nature of the <code>java.net.URLConnection</code> class, once the
	 * output stream
	 * for the connection is acquired, it's too late to set the content
	 * type (or any other request parameter). So one has to create a
	 * multipart boundary string first before using this class, such as
	 * with the <code>createBoundary()</code> method.
	 * 
	 * @param boundary
	 *            the boundary string
	 * @return the content type string
	 * @see #createBoundary()
	 */
	public static String getContentType(String boundary) {
		return "multipart/form-data; boundary=" + boundary;
	}
}
