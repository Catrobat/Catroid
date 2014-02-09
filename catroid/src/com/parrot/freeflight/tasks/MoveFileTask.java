package com.parrot.freeflight.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.AsyncTask;
import android.util.Log;

public class MoveFileTask extends AsyncTask<File, Integer, Boolean>
{
    private static final String TAG = MoveFileTask.class.getSimpleName();
    private File result;

    @Override
    protected Boolean doInBackground(File... params)
    {
        if (params.length < 2) {
            throw new IllegalArgumentException("Not enough parameters. Shoud have source and destination files");
        }
        
        File source = params[0];
        File destination = params[1];
        
        Log.d(TAG, "Moving file "  + source.getAbsolutePath() + " to " + destination.getAbsolutePath());
        
        if (!source.renameTo(destination)) {
            // Can't rename file. Trying to copy it.
            Log.d(TAG, "Moving of file failed. Copying...");
            try {
                Boolean result = copyFile(source, destination);
                
                if (result.equals(Boolean.TRUE)) {
                    Log.d(TAG, "Copying of the file " + source.getAbsolutePath() + " completed with success.");
                } else {
                    Log.w(TAG, "Copying of the file " + source.getAbsolutePath() + " to " + destination.getAbsolutePath() + " failed");
                }
                
                return result; 
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            result = destination;
            
            source.getParentFile().delete();
            
            Log.d(TAG,  "File moved successfully");
        }
        
        return true;
    }


    private Boolean copyFile(File source, File destination) throws IOException
    {
        InputStream is = null;
        OutputStream os = null;
        boolean success = false;
     
        try {
            // Make sure the Pictures directory exists.
            destination.createNewFile();
            
            is = new FileInputStream(source);
            os = new FileOutputStream(destination);
           
            long fileSize = source.length();
            long progress = 0;
            
            byte[] buffer = new byte[512];
            int read = -1;
            
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
               os.write(buffer, 0, read);
               progress += read;
               
               publishProgress((int)((double)progress / (double)fileSize) * 100);
               
               if (isCancelled()) {
                   Log.d(TAG, "Copy of the file was canceled");
                   break;
               }
            }

            if (!isCancelled()) {
                success = true;
            }
            
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + destination, e);
            success = false;
        } finally {      
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                    
                    if (success && !isCancelled()) {
                        if (!source.delete()) {
                            Log.w(TAG, "Cant delete file" + source.getAbsolutePath());
                        }
                        
                        result = destination;
                    } else if (!success && isCancelled()) {
                        if (destination.exists()) {
                            destination.delete();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        
        return success;
    }
    
    
    public File getResultFile()
    {
        return result;
    }
}
