package at.tugraz.ist.catroid.utils.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.content.Context;
import android.util.Log;

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
		    fOut = new FileOutputStream(name);
			return fOut;
		} catch (FileNotFoundException e) {
			Log.e("ERROR", e.getMessage());
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
			fIn = new FileInputStream(name);
			return fIn;
		} catch (FileNotFoundException e) {
			Log.e("FILESYSTEM", e.getMessage());
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
		File file = new File(name);
		return file.delete();
	}
	
}
