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

package at.ist.tugraz.catroid.test.license;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class LicenseTest extends TestCase {
	private static final String[] DIRECTORIES = { ".", "../catroid", "../catroidTest", "../catroidUiTest", };

	private ArrayList<String> licenseText;

	public LicenseTest() throws IOException {
		licenseText = new ArrayList<String>();
		File licenseTextFile = new File("res/license_text.txt");
		BufferedReader reader = new BufferedReader(new FileReader(licenseTextFile));

		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.length() > 0)
				licenseText.add(line);
		}
	}

	private void traverseDirectory(File directory) throws IOException {
		File[] contents = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (pathname.isDirectory() && !pathname.getName().equals("gen")) || pathname.getName().endsWith(".java") || pathname.getName().endsWith(".xml");
			}
		});

		for (File file : contents) {
			if (file.isDirectory())
				traverseDirectory(file);
			else
				checkFileForLicense(file);
		}
	}

	private void checkFileForLicense(File file) throws IOException {
		StringBuilder fileContentsBuilder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = reader.readLine()) != null)
			fileContentsBuilder.append(line);

		final String fileContents = fileContentsBuilder.toString();

		int lastPosition = 0;
		for (String licenseTextLine : licenseText) {
			int position = fileContents.indexOf(licenseTextLine);
			assertTrue("License text was not found in file " + file.getPath(), position != -1);
			assertTrue("License text was found in the wrong order in file " + file.getPath(), position > lastPosition);
			lastPosition = position;
		}
	}

	public void testLicensePresentInAllFiles() throws IOException {
		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			traverseDirectory(directory);
		}
	}
}
