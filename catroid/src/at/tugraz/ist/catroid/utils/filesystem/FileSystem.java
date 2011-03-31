/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
