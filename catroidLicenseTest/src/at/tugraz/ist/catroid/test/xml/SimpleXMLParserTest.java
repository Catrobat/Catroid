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
import java.util.List;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.xml.HeaderTags;
import at.tugraz.ist.catroid.xml.SimpleParser;

/**
 * @author Samitha
 * 
 */
public class SimpleXMLParserTest extends TestCase {

	public void testParseHeader() {
		SimpleParser parser = new SimpleParser();
		File testStandardXML = new File("res/catroidXMLsToValidate/standardProjekt.xml");
		File testComplexXML = new File("res/catroidXMLsToValidate/complexProject.xml");

		List<String> testStrings = parser.Parse(testStandardXML);
		assertEquals("androidVersion tag not parsed", testStrings.get(0), "10");
		assertEquals("catroidVersionCode tag not parsed", testStrings.get(1), "10");
		assertEquals("catroidVersionName tag not parsed", testStrings.get(2), "0.5.6a");
		assertEquals("deviceName tag not parsed", testStrings.get(3), "GT-I9100");
		assertEquals("ProjectName tag not parsed", testStrings.get(4), "standardProjekt");
		assertEquals("screenHeight tag not parsed", testStrings.get(5), "800");
		assertEquals("screenWidth tag not parsed", testStrings.get(6), "480");

		testStrings = parser.Parse(testComplexXML);
		assertEquals("androidVersion tag not parsed", testStrings.get(0), "10");
		assertEquals("catroidVersionCode tag not parsed", testStrings.get(1), "308");
		assertEquals("catroidVersionName tag not parsed", testStrings.get(2), "0.5.308");
		assertEquals("deviceName tag not parsed", testStrings.get(3), "HTC Sensation Z710e");
		assertEquals("ProjectName tag not parsed", testStrings.get(4), "fruit catcher 2");
		assertEquals("screenHeight tag not parsed", testStrings.get(5), "800");
		assertEquals("screenWidth tag not parsed", testStrings.get(6), "480");

	}

	public void testFindingElement() {
		SimpleParser parser = new SimpleParser();
		File testStandardXML = new File("res/catroidXMLsToValidate/standardProjekt.xml");
		String value = parser.getvalueof(HeaderTags.ANDROIDVERSION, testStandardXML);
		assertEquals("The returned value does not match", "10", value);

	}

	public void testSAXparserStoppingAfterHeader() {

	}
}
