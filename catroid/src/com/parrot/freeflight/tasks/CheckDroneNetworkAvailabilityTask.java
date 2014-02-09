package com.parrot.freeflight.tasks;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parrot.freeflight.drone.DroneConfig;
import com.parrot.freeflight.utils.CacheUtils;
import com.parrot.ftp.FTPClient;

public class CheckDroneNetworkAvailabilityTask extends AsyncTask<Context, Integer, Boolean> {

	private static final String TAG = "CheckDroneNetworkAvailability";
	private FTPClient ftpClient = null;
	
	@Override
	protected Boolean doInBackground(Context... params) 
	{
		Context context = params[0];
		String host = DroneConfig.getHost();
        int port = DroneConfig.getFtpPort();
        
        try {
    		if (!InetAddress.getByName(host).isReachable(2000)) {
                return Boolean.FALSE;
            }
        } catch (UnknownHostException e) {       
            return Boolean.FALSE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
		
		if (isCancelled()) {
			return Boolean.FALSE;
		}
		
		File tempFile = null;
		String content = null;
		
		try {
		    
			ftpClient = new FTPClient();
			
			if (!ftpClient.connect(host, port) || isCancelled()) {
				Log.w(TAG, "downloadFile failed. Can't connect");
				return Boolean.FALSE;
			}
			
			tempFile = CacheUtils.createTempFile(context);
			
			if (tempFile == null || isCancelled()) {
				Log.w(TAG, "downloadFile failed. Can't connect");
				return Boolean.FALSE;
			}
				
			if (isCancelled() || !ftpClient.getSync("version.txt", tempFile.getAbsolutePath())) {
				return Boolean.FALSE;
			}
			
			if (!tempFile.exists() || isCancelled()) {
				return  Boolean.FALSE;
			}
			
			if (!isCancelled()) {
			    StringBuffer stringBuffer = CacheUtils.readFromFile(tempFile);			
			    content = stringBuffer!=null?stringBuffer.toString():null;
			}
			
			if (content != null)
				return Boolean.TRUE;
			else 
				return Boolean.FALSE;
			
        } finally {
			if (tempFile != null && tempFile.exists()) {
				if (!tempFile.delete()) {
					Log.w(TAG, "Can't delete temp file " + tempFile.getAbsolutePath());
				}
			}
			
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.disconnect();
				ftpClient = null;
			}
		}	
	}
	
	
	public void cancelAnyFtpOperation()
	{
	    cancel(true);
	    
	    if (ftpClient != null) {
	        ftpClient.abort();
	    }
	}
	
}
