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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.xml.HeaderTags;
import at.tugraz.ist.catroid.xml.SimpleParser;

public class SimpleXMLParserTest extends InstrumentationTestCase {

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
	}

	public void testParseHeader() {
		SimpleParser parser = new SimpleParser();
		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;

		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
		}

		List<String> testStrings = parser.parse(xmlFileStream);
		assertEquals("androidVersion tag not parsed", testStrings.get(0), "10");
		assertEquals("catroidVersionCode tag not parsed", testStrings.get(1), "8");
		assertEquals("catroidVersionName tag not parsed", testStrings.get(2), "0.5.6a");
		assertEquals("deviceName tag not parsed", testStrings.get(3), "HTC Desire");
		assertEquals("ProjectName tag not parsed", testStrings.get(4), "testProject");
		assertEquals("screenHeight tag not parsed", testStrings.get(5), "800");
		assertEquals("screenWidth tag not parsed", testStrings.get(6), "480");

	}

	public void testFindingElement() {

		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
		}
		SimpleParser parser = new SimpleParser();

		String value = parser.getvalueof(HeaderTags.ANDROIDVERSION, xmlFileStream);
		assertEquals("The returned value does not match", "10", value);

	}

	public void testParserNewHeaderTag() {
		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project_new_header.xml");
		} catch (IOException e) {

			e.printStackTrace();
		}
		SimpleParser parser = new SimpleParser();

		List<String> values = parser.parse(xmlFileStream);
		boolean testBool = parser.newheaderFound;
		assertEquals("the full headers not added", values.size(), 8);
		//assertEquals("the new header not found", true, testBool);
		assertEquals("androidVersion tag not parsed", values.get(0), "10");
		assertEquals("catroidVersionCode tag not parsed", values.get(1), "8");
		assertEquals("catroidVersionName tag not parsed", values.get(2), "0.5.6a");
		assertEquals("deviceName tag not parsed", values.get(3), "HTC Desire");
		assertEquals("ProjectName tag not parsed", values.get(4), "testProject");
		assertEquals("ProjectName tag not parsed", values.get(5), "headerValue");
		assertEquals("screenHeight tag not parsed", values.get(6), "800");
		assertEquals("screenWidth tag not parsed", values.get(7), "480");
	}
}
