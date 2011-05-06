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
package at.tugraz.ist.catroid.test.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.UtilFile;

public class Utils {

	public static final int TYPE_IMAGE_FILE = 0;
	public static final int TYPE_SOUND_FILE = 1;

	/**
	 * saves a file into the project folder
	 * if project == null or "" file will be saved into Catroid folder
	 * 
	 * @param project
	 *            Folder where the file will be saved, this folder should exist
	 * @param name
	 *            Name of the file
	 * @param fileID
	 *            the id of the file --> needs the right context
	 * @param context
	 * @param type
	 *            type of the file: 0 = imagefile, 1 = soundfile
	 * @return the file
	 * @throws IOException
	 */
	public static File saveFileToProject(String project, String name, int fileID, Context context, int type)
			throws IOException {

		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Consts.DEFAULT_ROOT + "/" + name;
		} else {
			switch (type) {
				case TYPE_IMAGE_FILE:
					filePath = Consts.DEFAULT_ROOT + "/" + project + Consts.IMAGE_DIRECTORY + "/" + name;
					break;
				case TYPE_SOUND_FILE:
					filePath = Consts.DEFAULT_ROOT + "/" + project + Consts.SOUND_DIRECTORY + "/" + name;
					break;
				default:
					filePath = Consts.DEFAULT_ROOT + "/" + name;
					break;
			}
		}
		//		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID));
		//
		//		File file = new File(filePath);
		//		file.createNewFile();
		//
		//		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), 1024);
		//		byte[] buffer = new byte[1024];
		//		int length = 0;
		//		while ((length = in.read(buffer)) > 0) {
		//			out.write(buffer, 0, length);
		//		}
		//
		//		in.close();
		//		out.flush();
		//		out.close();

		return createTestMediaFile(filePath, fileID, context);
	}

	public static boolean clearProject(String projectname) {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectname);
		if (directory.exists()) {
			return UtilFile.deleteDirectory(directory);
		}
		return false;
	}

	public static File createTestMediaFile(String filePath, int fileID, Context context) throws IOException {

		File testImage = new File(filePath);

		if (!testImage.exists()) {
			testImage.createNewFile();
		}

		InputStream in = context.getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage));

		byte[] buffer = new byte[1024];
		int length = 0;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}

}
