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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class UtilFile {
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
}
