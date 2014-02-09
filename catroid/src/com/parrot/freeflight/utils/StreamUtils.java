/*
 * StreamUtils
 *
 *  Created on: Sep 2, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils 
{
	/**
	 * Performs copy of one stream into another.
	 * @param is - input stream
	 * @param os - output stream
	 * @throws IOException
	 */
	public static void copyStream(InputStream is, OutputStream os) throws IOException
	{
		byte[] buffer = new byte[1024];
		int count = 0;
		
		while ((count = is.read(buffer)) != -1) {
			os.write(buffer, 0, count);
		}
		
		os.flush();
	}
}
