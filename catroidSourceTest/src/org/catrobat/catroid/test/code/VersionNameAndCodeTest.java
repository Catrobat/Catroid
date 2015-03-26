/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.code;

import junit.framework.TestCase;

import org.catrobat.catroid.test.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionNameAndCodeTest extends TestCase {

	private static final String[] DIRECTORIES = Utils.VERSION_NAME_AND_CODE_TEST_DIRECTORIES;
	private static final String VERSION_CODE_REGEX = ".*android:versionCode=\"(\\d+)\".*";
	private static final String VERSION_NAME_REGEX = ".*android:versionName=\"(\\d+\\.\\d+\\.\\d+[a-z]*)\".*";

	public void testVersionCodeAndNameAreTheSameAcrossProjects() throws IOException {
		Pattern versionCodePattern = Pattern.compile(VERSION_CODE_REGEX);
		Pattern versionNamePattern = Pattern.compile(VERSION_NAME_REGEX);

		HashSet<String> testSet = new HashSet<String>();
		HashMap<String, String> versionInfos = new HashMap<String, String>();

		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			File androidManifest = new File(directoryName + "/AndroidManifest.xml");
			BufferedReader reader = new BufferedReader(new FileReader(androidManifest));
			String line;

			while ((line = reader.readLine()) != null) {
				Matcher matcher = versionCodePattern.matcher(line);
				if (matcher.find()) {
					testSet.add(matcher.group(1));
					versionInfos.put(directory.getName() + " versionCode", matcher.group(1));
				}
				matcher = versionNamePattern.matcher(line);
				if (matcher.find()) {
					testSet.add(matcher.group(1));
					versionInfos.put(directory.getName() + " versionName", matcher.group(1));
				}
			}
			reader.close();
		}

		assertEquals("There was a versionName or versionCode mismatch in one of the AndroidManifest.xml files\n"
				+ versionInfos, 2, testSet.size());
	}
}
