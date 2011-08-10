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

package at.tugraz.ist.catroid.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import at.tugraz.ist.catroid.common.Consts;

public class UtilFile {
	public static final int TYPE_IMAGE_FILE = 0;
	public static final int TYPE_SOUND_FILE = 1;
	private static final String TAG = UtilFile.class.getSimpleName();

	static public List<File> getFilesFromDirectoryByExtension(File directory, String extension) {
		String[] extensions = { extension };
		return getFilesFromDirectoryByExtension(directory, extensions);
	}

	static public List<File> getFilesFromDirectoryByExtension(File directory, final String[] extensions) {
		List<File> filesFound = new ArrayList<File>();
		File[] contents = directory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				for (String extension : extensions) {
					if (pathname.getName().endsWith(extension)) {
						return true;
					}
				}
				return (pathname.isDirectory() && !pathname.getName().equals("gen"));
			}
		});

		for (File file : contents) {
			if (file.isDirectory()) {
				filesFound.addAll(getFilesFromDirectoryByExtension(file, extensions));
			} else {
				filesFound.add(file);
			}
		}

		return filesFound;
	}

	static public long getSizeOfDirectoryInByte(File directory) {
		if (!directory.isDirectory()) {
			return 0;
		}
		File[] contents = directory.listFiles();
		long size = 0;
		for (File file : contents) {
			if (file.isDirectory()) {
				size += getSizeOfDirectoryInByte(file);
			} else {
				size += file.length();
			}
		}
		return size;
	}

	static public String getSizeAsString(File directory) {
		long sizeInKB = UtilFile.getSizeOfDirectoryInByte(directory) / 1024;

		String fileSizeString;
		DecimalFormat decimalFormat = new DecimalFormat("#.00");

		if (sizeInKB > 1048576) {
			fileSizeString = decimalFormat.format(sizeInKB / 1048576) + " GB";
		} else if (sizeInKB > 1024) {
			fileSizeString = decimalFormat.format(sizeInKB / 1024) + " MB";
		} else {
			fileSizeString = Long.toString(sizeInKB) + " KB";
		}
		return fileSizeString;
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
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID));

		try {
			Log.v(TAG, filePath);
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
}
