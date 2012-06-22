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
	protected void setUp() throws Exception {
		NativeAppActivity.setContext(getInstrumentation().getContext());
	}

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
	}

	public void testParseHeader() {
		HeaderTagsParser parser = new HeaderTagsParser();

		InputStream xmlFileStream = null;

		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}

		Map<String, String> values = parser.parseHeader(xmlFileStream);
		assertEquals("androidVersion tag not parsed", values.get(HeaderTags.ANDROIDVERSION.getXmlTagString()), "10");
		assertEquals("catroidVersionCode tag not parsed", values.get(HeaderTags.CATROIDVERSIONCODE.getXmlTagString()),
				"8");
		assertEquals("catroidVersionName tag not parsed", values.get(HeaderTags.CATROIDVERSIONNAME.getXmlTagString()),
				"0.5.6a");
		assertEquals("deviceName tag not parsed", values.get(HeaderTags.DEVICENAME.getXmlTagString()), "HTC Desire");
		assertEquals("ProjectName tag not parsed", values.get(HeaderTags.PROJECTNAME.getXmlTagString()), "testProject");
		assertEquals("screenHeight tag not parsed", values.get(HeaderTags.SCREENHEIGHT.getXmlTagString()), "800");
		assertEquals("screenWidth tag not parsed", values.get(HeaderTags.SCREENWIDTH.getXmlTagString()), "480");

	}

	public void testFindingElement() {

		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}
		HeaderTagsParser parser = new HeaderTagsParser();

		String value = parser.getvalueof(HeaderTags.ANDROIDVERSION, xmlFileStream);
		assertEquals("The returned value does not match", "10", value);

	}

	public void testParserNewHeaderTag() {
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = NativeAppActivity.getContext().getAssets().open("test_project_new_header.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}
		HeaderTagsParser parser = new HeaderTagsParser();

		Map<String, String> values = parser.parseHeader(xmlFileStream);

		assertEquals("the full headers not added. still have" + values.size() + "values", values.size(), 8);
		assertEquals("androidVersion tag not parsed", values.get(HeaderTags.ANDROIDVERSION.getXmlTagString()), "10");
		assertEquals("catroidVersionCode tag not parsed", values.get(HeaderTags.CATROIDVERSIONCODE.getXmlTagString()),
				"8");
		assertEquals("catroidVersionName tag not parsed", values.get(HeaderTags.CATROIDVERSIONNAME.getXmlTagString()),
				"0.5.6a");
		assertEquals("deviceName tag not parsed", values.get(HeaderTags.DEVICENAME.getXmlTagString()), "HTC Desire");
		assertEquals("ProjectName tag not parsed", values.get(HeaderTags.PROJECTNAME.getXmlTagString()), "testProject");
		assertEquals("dummy tag not parsed", values.get("dummy"), "headerValue");
		assertEquals("screenHeight tag not parsed", values.get(HeaderTags.SCREENHEIGHT.getXmlTagString()), "800");
		assertEquals("screenWidth tag not parsed", values.get(HeaderTags.SCREENWIDTH.getXmlTagString()), "480");
	}
}
