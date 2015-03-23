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
package org.catrobat.catroid.test.utils;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReflectionTest extends TestCase {
	private static final String[] FILES = { "../catroidTest/src/org/catrobat/catroid/test/utils/Reflection.java" };

	private class SmartFileContent {
		private String context;

		public SmartFileContent(String fileString) throws IOException {
			BufferedReader reader;
			File file = new File(fileString);

			reader = new BufferedReader(new FileReader(file));
			boolean readUntilClassReflection = true;
			while (readUntilClassReflection) {
				readUntilClassReflection = !reader.readLine().contains("class Reflection");
			}

			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			context = builder.toString();
			reader.close();
		}

		@Override
		public boolean equals(Object object) {
			if (object.getClass() != this.getClass()) {
				return false;
			}

			SmartFileContent smartFileContent = (SmartFileContent) object;
			return context.compareTo(smartFileContent.context) == 0;
		}

		@Override
		public int hashCode() {
			int result = 37;
			int prime = 41;
			if (context != null) {
				result = prime * result + context.hashCode();
			}
			return result;
		}

	}

	public void testIdenticalReflectionClassInTestProjects() throws IOException {
		List<SmartFileContent> fileContentList = new ArrayList<SmartFileContent>();
		for (String file : FILES) {
			fileContentList.add(new SmartFileContent(file));
		}

		if (fileContentList.size() > 1) {
			SmartFileContent expectedFileContent = fileContentList.get(0);
			for (int index = 1; index < fileContentList.size(); index++) {
				assertEquals("Reflection files differ", expectedFileContent, fileContentList.get(index));
			}
		}
	}
}
