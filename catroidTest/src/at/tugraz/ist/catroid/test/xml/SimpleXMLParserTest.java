/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import android.content.Context;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.xml.parser.HeaderTags;
import at.tugraz.ist.catroid.xml.parser.HeaderTagsParser;
import at.tugraz.ist.catroid.xml.parser.ParseException;

public class SimpleXMLParserTest extends InstrumentationTestCase {

	Context androidContext;

	@Override
	protected void setUp() throws Exception {
		androidContext = getInstrumentation().getContext();
	}

	@Override
	protected void tearDown() throws Exception {
		androidContext = null;
	}

	public void testParseHeader() {
		HeaderTagsParser parser = new HeaderTagsParser();

		InputStream xmlFileStream = null;

		try {
			xmlFileStream = androidContext.getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}

		Map<String, String> values = null;
		try {
			values = parser.parseHeader(xmlFileStream);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Exception when parsing the headers");
		}
		assertNotNull("Values are null", values);
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
			xmlFileStream = androidContext.getAssets().open("test_project.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}
		HeaderTagsParser parser = new HeaderTagsParser();

		String value = null;
		try {
			value = parser.getvalueof(HeaderTags.ANDROIDVERSION, xmlFileStream);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Exception when parsing");
		}
		assertNotNull("Values are null", value);
		assertEquals("The returned value does not match", "10", value);

	}

	public void testParserNewHeaderTag() {
		InputStream xmlFileStream = null;
		try {
			xmlFileStream = androidContext.getAssets().open("test_project_new_header.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}
		HeaderTagsParser parser = new HeaderTagsParser();

		Map<String, String> values = null;
		try {
			values = parser.parseHeader(xmlFileStream);
		} catch (ParseException e) {
			e.printStackTrace();
			fail("Exception When parsing");
		}

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
