/*
 * FTPUtils
 *
 *  Created on: Sep 1, 2011
 *      Author: Dmytro Baryskyy
 */


package com.parrot.freeflight.utils;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.parrot.ftp.FTPClient;
import com.parrot.ftp.FTPClientStatus;
import com.parrot.ftp.FTPClientStatus.FTPStatus;
import com.parrot.ftp.FTPOperation;
import com.parrot.ftp.FTPProgressListener;


public class FTPUtils 
{
	private static final String TAG = "FTPUtils";

	/**
	 * Returns the contents of remote file. 
	 * @param host - FTP host name or IP address
	 * @param port - port
	 * @param remote - remote file name
	 * @return null if error has occurred or file contents if successful.
	 */
	public static String downloadFile(Context context, String host, int port, String remote)
	{
		FTPClient client = null;
		File tempFile = null;
		
		try {
			client = new FTPClient();

			if (!client.connect(host, port)) {
				Log.w(TAG, "downloadFile failed. Can't connect");
				return null;
			}
			
			tempFile = CacheUtils.createTempFile(context);
			
			if (tempFile == null) {
				Log.w(TAG, "downloadFile failed. Can't connect");
				return null;
			}
				
			if (!client.getSync(remote, tempFile.getAbsolutePath())) {
				return null;
			}
			
			if (!tempFile.exists()) {
				return null;
			}
			
			StringBuffer stringBuffer = CacheUtils.readFromFile(tempFile);
			
			return stringBuffer!=null?stringBuffer.toString():null;
		} finally {
			if (tempFile != null && tempFile.exists()) {
				if (!tempFile.delete()) {
					Log.w(TAG, "Can'd delete temp file " + tempFile.getAbsolutePath());
				}
			}
			
			if (client != null && client.isConnected()) {
				client.disconnect();
			}
		}
	}


	/**
	 * Uploads file to FTP server asynchronously with progress tracking.
	 * @param assets - instance of AssetManager
	 * @param host - host name or IP address.
	 * @param port - port
	 * @param local - local file name in assets folder
	 * @param remote - remote file name
	 * @param listener - instance of ProgressListener. May be null.
	 * @return returns true if success or false if error occurred.
	 */
	public static boolean uploadFile(Context context, String host, int port, String local, String remote, final ProgressListener listener)
	{
		Log.d(TAG, "Uploading file " + local + " to " + host + ":" + port);
		
		AssetManager assets = context.getAssets();
		File tempFile = CacheUtils.createTempFile(context);
		FTPClient client = new FTPClient();
		
		try {
			if (tempFile == null) {
				return false;
			}
			
			if (!CacheUtils.copyFileFromAssetsToStorage(assets, local, tempFile)) {
				Log.e(TAG, "uploadFile() Can't copy file " + local + " to " + tempFile.getAbsolutePath());
				return false;
			}
				
			// Send file over ftp	
			if (!client.connect(host, port)) {
				Log.e(TAG, "uploadFile() Can't connect to " + host + ":" + port);
				return false;
			}
			
			final Object lock = new Object();
			
			client.setProgressListener(new FTPProgressListener() {	
				public void onStatusChanged(FTPStatus status, float progress,
						FTPOperation operation) 
				{
					if (status == FTPStatus.FTP_PROGRESS) {
						listener.onProgress(Math.round(progress));
					} else {
						synchronized (lock) {
							lock.notify();
						}
					}
				}
			});
				
			// Start transfer
			client.put(tempFile.getAbsolutePath(), remote);
			
			// Wait for upload to complete
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Check result
			if (FTPClientStatus.isFailure(client.getReplyStatus())) {
				Log.e(TAG, "uploadFile() Failed to upload file to ftp " + host + ":" + port);
				return false;
			}
	
			return true;	
		} finally {
			// Delete temp file
			if (tempFile != null && tempFile.exists()) {
				if (!tempFile.delete()) {
					Log.w(TAG, "Can't delete file " + tempFile.getAbsolutePath());
				}
			}
			
			// Close FTP connection
			if (client.isConnected()) {
				client.disconnect();
			}
		}
	}

	
	/**
	 * Uploads file to FPT server synchronously.
	 * @param context Context
	 * @param host
	 * @param port
	 * @param local
	 * @param remote
	 * @return
	 */
	public static boolean uploadFileSync(Context context, String host, int port, String local, String remote)
	{
		AssetManager assets = context.getAssets();
		File tempFile = CacheUtils.createTempFile(context);
		FTPClient client = new FTPClient();
		
		try {
			if (tempFile == null) {
				return false;
			}
			
			if (!CacheUtils.copyFileFromAssetsToStorage(assets, local, tempFile)) {
				Log.e(TAG, "uploadFile() Can't copy file " + local + " to " + tempFile.getAbsolutePath());
				return false;
			}
				

			if (!client.connect(host, port)) {
				Log.e(TAG, "uploadFile() Can't connect to " + host + ":" + port);
				return false;
			}
				
			// Start transfer
			boolean result = client.putSync(tempFile.getAbsolutePath(), remote);
			
			// Check result
			if (FTPClientStatus.isFailure(client.getReplyStatus())) {
				Log.e(TAG, "uploadFile() Failed to upload file to ftp " + host + ":" + port);
				return false;
			}
	
			return result;	
		} finally {
			// Delete temp file
			if (tempFile != null && tempFile.exists()) {
				if (!tempFile.delete()) {
					Log.w(TAG, "Can't delete file " + tempFile.getAbsolutePath());
				}
			}
			
			// Close FTP connection
			if (client.isConnected()) {
				client.disconnect();
			}
		}
	}
}



