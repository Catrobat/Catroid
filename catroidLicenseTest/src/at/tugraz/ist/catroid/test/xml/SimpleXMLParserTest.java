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
package at.tugraz.ist.catroid.test.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.xml.HeaderTags;
import at.tugraz.ist.catroid.xml.HeaderTagsParser;

public class SimpleXMLParserTest extends TestCase {

	public void testParseHeader() {
		HeaderTagsParser parser = new HeaderTagsParser();
		File testStandardXML = new File("res/catroidXMLsToValidate/standardProjekt.xml");
		File testComplexXML = new File("res/catroidXMLsToValidate/complexProject.xml");

		InputStream standardFileStream = null;
		InputStream complexFileStream = null;
		try {
			standardFileStream = new FileInputStream(testStandardXML);
			complexFileStream = new FileInputStream(testComplexXML);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		Map<String, String> testStrings = parser.parse(standardFileStream);
		assertEquals("androidVersion tag not parsed", testStrings.get("androidVersion"), "10");
		assertEquals("catroidVersionCode tag not parsed", testStrings.get("catroidVersionCode"), "10");
		assertEquals("catroidVersionName tag not parsed", testStrings.get("catroidVersionName"), "0.5.6a");
		assertEquals("deviceName tag not parsed", testStrings.get("deviceName"), "GT-I9100");
		assertEquals("ProjectName tag not parsed", testStrings.get("projectName"), "standardProjekt");
		assertEquals("screenHeight tag not parsed", testStrings.get("screenHeight"), "800");
		assertEquals("screenWidth tag not parsed", testStrings.get("screenWidth"), "480");

		testStrings = parser.parse(complexFileStream);
		assertEquals("androidVersion tag not parsed", testStrings.get("androidVersion"), "10");
		assertEquals("catroidVersionCode tag not parsed", testStrings.get("catroidVersionCode"), "308");
		assertEquals("catroidVersionName tag not parsed", testStrings.get("catroidVersionName"), "0.5.308");
		assertEquals("deviceName tag not parsed", testStrings.get("deviceName"), "HTC Sensation Z710e");
		assertEquals("ProjectName tag not parsed", testStrings.get("projectName"), "fruit catcher 2");
		assertEquals("screenHeight tag not parsed", testStrings.get("screenHeight"), "800");
		assertEquals("screenWidth tag not parsed", testStrings.get("screenWidth"), "480");

	}

	public void testFindingElement() {
		HeaderTagsParser parser = new HeaderTagsParser();
		File testStandardXML = new File("res/catroidXMLsToValidate/standardProjekt.xml");
		InputStream fileStream = null;
		try {
			fileStream = new FileInputStream(testStandardXML);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		String value = parser.getvalueof(HeaderTags.ANDROIDVERSION, fileStream);
		assertEquals("The returned value does not match", "10", value);

	}

	public void testSAXparserStoppingAfterHeader() {

	}
}
