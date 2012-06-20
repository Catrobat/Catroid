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
import java.util.Map;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.xml.HeaderTags;
import at.tugraz.ist.catroid.xml.HeaderTagsParser;

public class SimpleXMLParserTest extends InstrumentationTestCase {

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
	}

	public void testParseHeader() {
		HeaderTagsParser parser = new HeaderTagsParser();
		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;

		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
		}

		Map<String, String> testStrings = parser.parse(xmlFileStream);
		assertEquals("androidVersion tag not parsed", testStrings.get("androidVersion"), "10");
		assertEquals("catroidVersionCode tag not parsed", testStrings.get("catroidVersionCode"), "8");
		assertEquals("catroidVersionName tag not parsed", testStrings.get("catroidVersionName"), "0.5.6a");
		assertEquals("deviceName tag not parsed", testStrings.get("deviceName"), "HTC Desire");
		assertEquals("ProjectName tag not parsed", testStrings.get("projectName"), "testProject");
		assertEquals("screenHeight tag not parsed", testStrings.get("screenHeight"), "800");
		assertEquals("screenWidth tag not parsed", testStrings.get("screenWidth"), "480");

	}

	public void testFindingElement() {

		NativeAppActivity.setContext(getInstrumentation().getContext());
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
		}
		HeaderTagsParser parser = new HeaderTagsParser();

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
		HeaderTagsParser parser = new HeaderTagsParser();

		Map<String, String> values = parser.parse(xmlFileStream);

		assertEquals("the full headers not added. still have" + values.size() + "values", values.size(), 8);
		//assertEquals("the new header not found", true, testBool);
		assertEquals("androidVersion tag not parsed", values.get("androidVersion"), "10");
		assertEquals("catroidVersionCode tag not parsed", values.get("catroidVersionCode"), "8");
		assertEquals("catroidVersionName tag not parsed", values.get("catroidVersionName"), "0.5.6a");
		assertEquals("deviceName tag not parsed", values.get("deviceName"), "HTC Desire");
		assertEquals("ProjectName tag not parsed", values.get("projectName"), "testProject");
		assertEquals("dummy tag not parsed", values.get("dummy"), "headerValue");
		assertEquals("screenHeight tag not parsed", values.get("screenHeight"), "800");
		assertEquals("screenWidth tag not parsed", values.get("screenWidth"), "480");
	}
}
