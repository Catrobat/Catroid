/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import at.tugraz.ist.catroid.common.Consts;

public class UtilFile {
	public static final int TYPE_IMAGE_FILE = 0;
	public static final int TYPE_SOUND_FILE = 1;

	static private long getSizeOfFileOrDirectoryInByte(File fileOrDirectory) {
		if (!fileOrDirectory.exists()) {
			return 0;
		}
		if (fileOrDirectory.isFile()) {
			return fileOrDirectory.length();
		}

		File[] contents = fileOrDirectory.listFiles();
		long size = 0;
		for (File file : contents) {
			size += file.isDirectory() ? getSizeOfFileOrDirectoryInByte(file) : file.length();
		}
		return size;
	}

	static public String getSizeAsString(File fileOrDirectory) {
		final int UNIT = 1024;
		long bytes = UtilFile.getSizeOfFileOrDirectoryInByte(fileOrDirectory);

		if (bytes < UNIT) {
			return bytes + " Byte";
		}

		/*
		 * Logarithm of "bytes" to base "unit"
		 * log(a) / log(b) == logarithm of a to the base of b
		 */
		int exponent = (int) (Math.log(bytes) / Math.log(UNIT));
		char prefix = ("KMGTPE").charAt(exponent - 1);

		return String.format("%.1f %sB", bytes / Math.pow(UNIT, exponent), prefix);
	}

	static public boolean clearDirectory(File path) {
		if (path.exists()) {
			File[] filesInDirectory = path.listFiles();
			for (File file : filesInDirectory) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return true;
	}

	static public boolean deleteDirectory(File path) {
		clearDirectory(path);
		return (path.delete());
	}

	public static File saveFileToProject(String project, String name, int fileID, Context context, int type) {

		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Utils.buildProjectPath(name);
		} else {
			switch (type) {
				case TYPE_IMAGE_FILE:
					filePath = Utils.buildPath(Utils.buildProjectPath(project), Consts.IMAGE_DIRECTORY, name);
					break;
				case TYPE_SOUND_FILE:
					filePath = Utils.buildPath(Utils.buildProjectPath(project), Consts.SOUND_DIRECTORY, name);
					break;
				default:
					filePath = Utils.buildProjectPath(name);
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID),
				Consts.BUFFER_8K);

		try {
			File file = new File(filePath);
			file.getParentFile().mkdirs();
			file.createNewFile();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Consts.BUFFER_8K);
			byte[] buffer = new byte[Consts.BUFFER_8K];
			int length = 0;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			out.close();

			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * returns a list of strings of all projectnames in the catroid folder
	 */
	public static List<String> getProjectNames(File directory) {
		List<String> projectList = new ArrayList<String>();
		File[] sdFileList = directory.listFiles();
		for (File file : sdFileList) {
			if (file.isDirectory()) {
				projectList.add(file.getName());
			}
		}
		return projectList;
	}

	public static List<File> getProjectFiles(File directory) {
		List<File> projectList = new ArrayList<File>();
		File[] sdFileList = directory.listFiles();
		FilenameFilter filenameFilter = new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.contentEquals(Consts.PROJECTCODE_NAME);
			}
		};
		for (File file : sdFileList) {
			if (file.isDirectory() && file.list(filenameFilter).length != 0) {
				projectList.add(file);
			}
		}
		return projectList;
	}

}
