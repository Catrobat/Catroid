/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

public class ReflectionTest extends TestCase {
	private static final int CLASS_DECLARATION_LINE = 28;

	public void testIdenticalReflectionClassInTestProjects() throws IOException {
		final String[] FILES = { "../catroidTest/src/org/catrobat/catroid/test/utils/Reflection.java",
				"../catroidUiTest/src/org/catrobat/catroid/uitest/util/Reflection.java" };

		Map<File, List<String>> fileContent = new HashMap<File, List<String>>();
		for (String file : FILES) {
			File currentFile = new File(file);
			BufferedReader reader = new BufferedReader(new FileReader(currentFile));

			List<String> lines = new ArrayList<String>();
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			fileContent.put(currentFile, lines);

			reader.close();
		}

		Entry<File, List<String>> previousEntry = null;
		for (Entry<File, List<String>> currentEntry : fileContent.entrySet()) {
			if (previousEntry != null) {
				for (int currentLine = CLASS_DECLARATION_LINE; currentLine < previousEntry.getValue().size(); currentLine++) {
					assertEquals(previousEntry.getKey() + " differse from " + currentEntry.getKey() + " at line "
							+ (currentLine + 1), previousEntry.getValue().get(currentLine), currentEntry.getValue()
							.get(currentLine));
				}
			}

			previousEntry = currentEntry;
		}
	}
}
