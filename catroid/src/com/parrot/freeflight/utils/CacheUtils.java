/*
 * CacheUtils
 *
 *  Created on: Sep 1, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class CacheUtils 
{
	/**
	 * Creates temporary file in external storage, or in case when extrnal storage is not available
	 * temporary file is created in the internal storage.
	 * @param context - Context
	 * @return instance of java.io.File or null if error occured.
	 */
	public static File createTempFile(Context context) 
	{
		if (context == null) 
			throw new IllegalArgumentException();
		
		File saveToDir = context.getExternalCacheDir();
		
		if (saveToDir == null) {
			saveToDir = context.getCacheDir();
		}

		File tempFile = null;
		
		try {
			tempFile = File.createTempFile("parrot", "", saveToDir);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return tempFile;
	}
	
	
	/**
	 * Reads content of the file into string buffer
	 * @param tempFile instance of java.io.File that points to an actual file on the file system.
	 * @return instance of java.lang.StringBuffer
	 */
	public static StringBuffer readFromFile(File tempFile) 
	{
		StringBuffer stringBuffer = new StringBuffer();
		FileInputStream is = null;
	
		try {
			is = new FileInputStream(tempFile);
		
			byte[] buffer = new byte[1024];
			int count = 0;
			
			while ((count = is.read(buffer)) != -1) {
				stringBuffer.append(new String(buffer, 0, count ));			
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return stringBuffer;
	}
	
	
	public static boolean copyFileFromAssetsToStorage(AssetManager assets,
			String name, File dest) 
	{
		boolean result = true;
		InputStream is = null;
		FileOutputStream os = null;
		
		// Copy file to storage
		try {
			is = assets.open(name);
			os = new FileOutputStream(dest);
			
			StreamUtils.copyStream(is, os);
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
}
