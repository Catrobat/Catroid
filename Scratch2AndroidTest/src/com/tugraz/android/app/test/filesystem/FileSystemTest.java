package com.tugraz.android.app.test.filesystem;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.tugraz.android.app.filesystem.FileSystem;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;

public class FileSystemTest extends AndroidTestCase {

	final String TEST_STRING = new String("Hello Scratch");
	final String TEST_FILENAME = new String("samplefile.txt");
	
	private FileSystem mFileSystem;
	private Context mCtx;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mFileSystem = new FileSystem();
		try {
			mCtx = getContext().createPackageContext("com.tugraz.android.app", Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			assertFalse(true);
		}
		
		 try {     
             // ##### Write a file to the disk #####
             /* We have to use the openFileOutput()-method
              * the ActivityContext provides, to
              * protect your file from others and
              * This is done for security-reasons.
              * We chose MODE_WORLD_READABLE, because
              *  we have nothing to hide in our file */ 
             FileOutputStream fOut = mCtx.openFileOutput(TEST_FILENAME, Activity.MODE_WORLD_READABLE);
             OutputStreamWriter osw = new OutputStreamWriter(fOut); 

             // Write the string to the file
             osw.write(TEST_STRING);
             /* ensure that everything is
              * really written out and close */
             osw.flush();
             osw.close();
             
		 }catch(Exception ex){assertFalse(true);}

		 
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		mCtx.deleteFile(TEST_FILENAME);
	}

	
	public void testDeleteFile(){
		
        boolean deleted = true;
        mFileSystem.deleteFile(TEST_FILENAME);
		String[] fileNames = mCtx.fileList();
		for(int i = 0; i < fileNames.length; i++){
			if(fileNames[i].equals(TEST_FILENAME))
				deleted = false;
		}
		
		assertTrue(deleted);
		 
	}
	
	public void testCreateOrOpenFile(){
		mCtx.deleteFile(TEST_FILENAME);
		
		mFileSystem.createOrOpenFile(TEST_FILENAME);
		
		boolean available = false;
		String[] fileNames = mCtx.fileList();
		for(int i = 0; i < fileNames.length; i++){
			if(fileNames[i].equals(TEST_FILENAME))
				available = true;
		}
			
			assertTrue(available);
	}
}
