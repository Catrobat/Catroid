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
package org.catrobat.catroid.test.utiltests;

import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class EncodeAndDecodeSpecialCharsTest {

	@Test
	public void testEncodeAndDecodeSpecialCharsForFileSystem() {
		String projectName1 = ".*\"/:<>?\\|%";
		String projectName1Encoded = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName1);
		assertEquals(".%2A%22%2F%3A%3C%3E%3F%5C%7C%25", projectName1Encoded);
		assertEquals(projectName1, FileMetaDataExtractor.decodeSpecialCharsForFileSystem(projectName1Encoded));

		String projectName2 = "../*\"/:<>?\\|";
		String projectName2Encoded = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName2);
		assertEquals("..%2F%2A%22%2F%3A%3C%3E%3F%5C%7C", projectName2Encoded);
		assertEquals(projectName2, FileMetaDataExtractor.decodeSpecialCharsForFileSystem(projectName2Encoded));

		String projectName3 = "./*T?E\"S/T:T<E>S?T\\T\\E|S%";
		String projectName3Encoded = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName3);
		assertEquals(".%2F%2AT%3FE%22S%2FT%3AT%3CE%3ES%3FT%5CT%5CE%7CS%25", projectName3Encoded);
		assertEquals(projectName3, FileMetaDataExtractor.decodeSpecialCharsForFileSystem(projectName3Encoded));

		String projectName4 = ".";
		String projectName4Encoded = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName4);
		assertEquals("%2E", projectName4Encoded);
		assertEquals(projectName4, FileMetaDataExtractor.decodeSpecialCharsForFileSystem(projectName4Encoded));

		String projectName5 = "..";
		String projectName5Encoded = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName5);
		assertEquals("%2E%2E", projectName5Encoded);
		assertEquals(projectName5, FileMetaDataExtractor.decodeSpecialCharsForFileSystem(projectName5Encoded));

		String projectName6 = "../*T?E\"S/T:%22T<E>S?T\\T\\E|S%äö|üß";
		String projectName6Encoded = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(projectName6);
		assertEquals("..%2F%2AT%3FE%22S%2FT%3A%2522T%3CE%3ES%3FT%5CT%5CE%7CS%25äö%7Cüß", projectName6Encoded);
		assertEquals(projectName6, FileMetaDataExtractor.decodeSpecialCharsForFileSystem(projectName6Encoded));
	}
}
