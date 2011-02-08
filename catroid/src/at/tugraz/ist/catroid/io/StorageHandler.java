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
package at.tugraz.ist.catroid.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.project.Project;

import com.thoughtworks.xstream.XStream;

/**
 * @author Peter Treitler
 *
 */
public class StorageHandler {
	private static final String DIRECTORY_NAME = "catroid";
	private static final String PROJECT_EXTENTION = ".spf";
	
	private static StorageHandler instance;
	private File catroidRoot;
	
	private StorageHandler(final Activity activity) {
		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable  = true;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = false;
		}
		if(!mExternalStorageAvailable) {
			Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(activity.getString(R.string.error));
			builder.setMessage(activity.getString(R.string.error_no_sd_card));
			builder.setNeutralButton(activity.getString(R.string.close), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// finish parent activity
					activity.finish();
				}
			});
			builder.show();
			return;
		}
		
		String catroidPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+DIRECTORY_NAME;
		catroidRoot = new File(catroidPath);

	}
	
	public synchronized static StorageHandler getInstance(Activity activity) {
		if(instance == null)
			instance = new StorageHandler(activity);
		return instance;
	}
	
	public Project loadProject(String projectName) {
		
		try {
			File projectDirectory = new File(catroidRoot.getAbsolutePath()+"/"+projectName);
			if(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {
				InputStream spfFileStream;
				spfFileStream = new FileInputStream(projectDirectory.getAbsolutePath()+"/"+projectName+PROJECT_EXTENTION);
				
				//TODO: initialize xstream
				XStream xstream = new XStream();
				return (Project)xstream.fromXML(spfFileStream);
			} else 
				return null;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
