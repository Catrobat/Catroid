package com.tugraz.android.app.filesystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;

/**
 * 
 * @author NikolausKoller
 *	This class provides open/create and delete in private application memory
 */
public class FileSystem {
	
	/**
	 * DONT forget to close the file descriptor
	 * @param name of file
	 * @param ctx Context
	 * @return file descriptor output stream to write and read a file
	 */
	public FileOutputStream createOrOpenFileOutput(String name, Context ctx){
		FileOutputStream fOut;
		try {
			fOut = ctx.openFileOutput(name, Activity.MODE_WORLD_READABLE);
			return fOut;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * DONT forget to close the file descriptor
	 * @param name of file
	 * @param ctx Context
	 * @return file descriptor input stream to write and read a file
	 */
	public FileInputStream createOrOpenFileInput(String name, Context ctx){
		FileInputStream fIn = null;
		try {
			fIn = ctx.openFileInput(name);
			return fIn;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fIn;
	}
	
	/**
	 * 
	 * @param name of file
	 * @param ctx Context
	 * @return success
	 */
	public boolean deleteFile(String name, Context ctx){
		return ctx.deleteFile(name);
	}
	
	

}
