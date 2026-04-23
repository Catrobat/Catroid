/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.io;

import org.catrobat.catroid.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipArchiver {

	private static final String DIRECTORY_LEVEL_UP = "../";
	private static final int COMPRESSION_LEVEL = 0;

	public void zip(File archive, File[] files) throws IOException {
		archive.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(archive);
		ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		try {
			zipOutputStream.setLevel(COMPRESSION_LEVEL);
			writeZipEntriesToStream(zipOutputStream, Arrays.asList(files), "");
		} finally {
			zipOutputStream.close();
			fileOutputStream.close();
		}
	}

	private void writeZipEntriesToStream(ZipOutputStream zipOutputStream, List<File> files, String parentDir) throws IOException {
		for (File file : files) {
			if (!file.exists()) {
				throw new FileNotFoundException("File: " + file.getAbsolutePath() + " does NOT exist.");
			}

			if (file.isDirectory()) {
				writeZipEntriesToStream(zipOutputStream, Arrays.asList(file.listFiles()), parentDir
						+ file.getName() + "/");
				continue;
			}

			zipOutputStream.putNextEntry(new ZipEntry(parentDir + file.getName()));

			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				byte[] b = new byte[Constants.BUFFER_8K];
				int len;
				while ((len = fileInputStream.read(b)) != -1) {
					zipOutputStream.write(b, 0, len);
				}
			} finally {
				zipOutputStream.closeEntry();
			}
		}
	}

	public void unzip(File archive, File dstDir) throws IOException {
		InputStream inputStream = new FileInputStream(archive);
		unzip(inputStream, dstDir);
	}

	public void unzip(InputStream is, File dstDir) throws IOException {
		createDirIfNecessary(dstDir);

		try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().contains(DIRECTORY_LEVEL_UP)) {
					continue;
				}
				if (zipEntry.isDirectory()) {
					createDirIfNecessary(new File(dstDir, zipEntry.getName()));
					continue;
				}

				File zipEntryFile = new File(dstDir, zipEntry.getName());
				zipEntryFile.getParentFile().mkdirs();

				try (FileOutputStream fileOutputStream = new FileOutputStream(zipEntryFile)) {
					byte[] b = new byte[Constants.BUFFER_8K];
					int len;
					while ((len = zipInputStream.read(b)) != -1) {
						fileOutputStream.write(b, 0, len);
					}
				}
			}
		}
	}

	private void createDirIfNecessary(File dir) throws IOException {
		if (!dir.exists() && !dir.mkdir()) {
			throw new IOException("Could NOT create Dir: " + dir.getAbsolutePath());
		}
	}
}
