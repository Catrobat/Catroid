/*
 * FTPClient
 *
 *  Created on: Apr 14, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.ftp;

import java.io.OutputStream;

import android.util.Log;

import com.parrot.ftp.FTPClientStatus.FTPStatus;

public class FTPClient 
{
	private static final String TAG = "FTPClient";
	
	private int connectionHandle; 
	private int ftpStatus;
	
	private boolean busy;
	private FTPOperation currOperation;
	
	private FTPProgressListener listener;
	
	public FTPClient()
	{
		connectionHandle = 0;
		busy = false;
	}
	
	
	/**
	 * Connects to a FTP server using anonymous credentials.
	 * @param ip IP address of the server
	 * @param port FTP port
	 * @return true if connection successful, false otherwise.
	 * @throws IllegalArgumentException when IP is null.
	 */
	public boolean connect(final String ip, final int port)
	{
		return connect(ip, port, "anonymous", "");
	}
	
	
	/**
	 * Connects to a FTP server.
	 * @param ip IP address of the server
	 * @param port FTP port
	 * @param username FTP user name.
	 * @param password FTP password.
	 * @return true if connection successful, false otherwise.
	 * @throws IllegalArgumentException when ip or username or password is null.
	 */
	public boolean connect(final String ip, final int port, final String username, final String password)
	{
		if (ip == null || username == null || password == null)
			throw new IllegalArgumentException();
		
		return ftpConnect(ip, port, username, password);
	}
	
	
	/**
	 * Disconnects from FTP server.
	 * @return true if successful, false otherwise.
	 */
	public boolean disconnect()
	{
		return ftpDisconnect();
	}
	
	
	/**
	 * Puts the local file to the FTP server asynchronously.
	 * @param localFilePath absolute path to the file. Could be retrieved from File.getAbsoluteFilePath()
	 * @param remoteFilePath remote file path.
	 * @return true if successful, false otherwise.
	 */
	public boolean put(String localFilePath, String remoteFilePath)
	{
		if (busy) {
			Log.w(TAG, "Can't put file. FTPClient is busy at the moment. Performing " + currOperation.name());
			return false;
		}
		
		currOperation = FTPOperation.FTP_PUT;
		busy = true;
		
		return ftpPut(localFilePath, remoteFilePath, false);
	}
	
	
	/**
	 * Puts local file to the FTP server synchronously.
	 * @param localFilePath absolute path to the file. Could be retrieved from File.getAbsoluteFilePath()
	 * @param remoteFilePath remote file path.
	 * @return true if successful, false otherwise.
	 */
	public boolean putSync(String localFilePath, String remoteFilePath)
	{
		if (busy) {
			Log.w(TAG, "Can't put file. FTPClient is busy at the moment. Performing " + currOperation.name());
			return false;
		}
		
		currOperation = FTPOperation.FTP_PUT;
		busy = true;
		
		try {
			return ftpPutSync(localFilePath, remoteFilePath, false);
		} finally {
			busy = false;
			currOperation = FTPOperation.FTP_NONE;
		}
	}
	
	
	/**
	 * Downloads a remote file asynchronously.
	 * @param remoteFilePath path to remote file.
	 * @param localFilePath local path where remote file should be saved. 
	 * @return true if successful, false otherwise.
	 */
	public boolean get(String remoteFilePath, String localFilePath)
	{
		if (busy) {
			Log.w(TAG, "Can't get file. FTPClient is busy at the moment. Performing " + currOperation.name());
			return false;
		}
		
		currOperation = FTPOperation.FTP_GET;
		busy = true;
		
		return ftpGet(remoteFilePath, localFilePath, false);
	}
	

	/**
	 * Downloads a remote file synchronously.
	 * @param remoteFilePath path to remote file.
	 * @param localFilePath local path where remote file should be saved. 
	 * @return true if successful, false otherwise.
	 */
	public boolean getSync(String remoteFilePath, String localFilePath)
	{
		if (busy) {
			Log.w(TAG, "Can't get file. FTPClient is busy at the moment. Performing " + currOperation.name());
			return false;
		}
		
		currOperation = FTPOperation.FTP_GET;
		busy = true;
		
		boolean result = ftpGetSync(remoteFilePath, localFilePath, false);
		
		busy = false;
		currOperation = FTPOperation.FTP_NONE;
		
		return result;
	}
	
	
	public void list()
	{
		throw new IllegalStateException("Not implemented");
	}
	
	
	public void remove()
	{
		throw new IllegalStateException("Not implemented");
	}
	
	
	public void rename()
	{
		throw new IllegalStateException("Not implemented");
	}
	
	
	public void cd()
	{
		throw new IllegalStateException("Not implemented");
	}
	
	
	public void pwd()
	{
		throw new IllegalStateException("Not implemented");
	}
	
	
	public void mkdir()
	{
		throw new IllegalStateException("Not implemented");
	}
	
	
	public void rmdir()
	{
		throw new IllegalStateException("Not implemented");
	}
	
	
	public boolean abort()
	{
		boolean result = ftpAbort();
		
		if (result) {
			busy = false;
			currOperation = FTPOperation.FTP_NONE;
		}
		
		return result;
	}
	
	
	/**
	 * Shows whether connection to FTP server is established or not.
	 * @return true if connected, false otherwise.
	 */
	public boolean isConnected()
	{
		return ftpIsConnected();
	}
	
	
	/**
	 * Returns the FTP reply code
	 * @return int
	 */
	public int getReplyCode()
	{
		return this.ftpStatus;
	}
	
	
	/**
	 * Returns the FTP reply status.
	 * @return one of the FTPStatus values.
	 */
	public FTPStatus getReplyStatus()
	{
		return FTPClientStatus.translateStatus(ftpStatus);
	}
	
	
	public boolean retrieveFile(String remote, OutputStream os)
	{
		throw new IllegalStateException("Not implemented");
	}
	

	public void setProgressListener(FTPProgressListener listener)
	{
		this.listener = listener;
	}
	
	/*
	 * This callback is called from native code during upload/download progress.
	 */
	private void callback(int statusId, float progress, String fileList)
	{
		ftpStatus = statusId;
		FTPStatus status = FTPClientStatus.translateStatus(statusId);
		
		if (listener != null) {
			listener.onStatusChanged(status, progress, currOperation);
		}
		
		if (status != FTPStatus.FTP_PROGRESS) {
			busy = false;
			currOperation = FTPOperation.FTP_NONE;
		}
		
		Log.d(TAG, "Status: " + status.name() + ", progress: " + progress);
	}
	
	/*
	 * The implementation of all native methods could be fount at /jni/Stubs/ftp_client_stub.c
	 */
	private native boolean ftpConnect(String ip, int port, String username, String password);
	private native boolean ftpDisconnect();
	private native boolean ftpAbort();
	
	private native boolean ftpPutSync(String localName,  String remoteName, boolean useResume);
	private native boolean ftpPut    (String localName,  String remoteName, boolean useResume);
	private native boolean ftpGet    (String remoteName, String localName,  boolean useResume);
	private native boolean ftpGetSync(String remoteName, String localName,  boolean useResume);
	
	private native boolean ftpIsConnected();
}
