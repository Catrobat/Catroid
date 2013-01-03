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
package org.catrobat.catroid.test.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.catrobat.catroid.xml.parser.HeaderTags;
import org.catrobat.catroid.xml.parser.HeaderTagsParser;
import org.catrobat.catroid.xml.parser.ParseException;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class SimpleXMLParserTest extends InstrumentationTestCase {

	Context androidContext;

	@Override
	protected void setUp() throws Exception {
		androidContext = getInstrumentation().getContext();
	}

	@Override
	protected void tearDown() throws Exception {
		androidContext = null;
		super.tearDown();
	}

	public void testParseHeader() {
		HeaderTagsParser parser = new HeaderTagsParser();

		InputStream xmlFileStream = null;

		try {
			xmlFileStream = androidContext.getAssets().open("standardProject.xml");
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
		assertEquals("androidVersion tag not parsed", values.get(HeaderTags.PLATFORMVERSION.getXmlTagString()), "10");
		assertEquals("catroidVersionCode tag not parsed",
				values.get(HeaderTags.APPLICATIONBUILDNUMBER.getXmlTagString()), "980");
		assertEquals("catroidVersionName tag not parsed", values.get(HeaderTags.APPLICATIONVERSION.getXmlTagString()),
				"0.7.0beta");
		assertEquals("deviceName tag not parsed", values.get(HeaderTags.DEVICENAME.getXmlTagString()), "GT-I9100");
		assertEquals("ProjectName tag not parsed", values.get(HeaderTags.PROGRAMNAME.getXmlTagString()),
				"standardProjekt");
		assertEquals("screenHeight tag not parsed", values.get(HeaderTags.SCREENHEIGHT.getXmlTagString()), "800");
		assertEquals("screenWidth tag not parsed", values.get(HeaderTags.SCREENWIDTH.getXmlTagString()), "480");

	}

	public void testFindingElement() {

		InputStream xmlFileStream = null;
		try {
			xmlFileStream = androidContext.getAssets().open("standardProject.xml");
		} catch (IOException e) {

			e.printStackTrace();
			fail("Exception caught at getting filestream");
		}
		HeaderTagsParser parser = new HeaderTagsParser();

		String value = null;
		try {
			value = parser.getvalueof(HeaderTags.PLATFORMVERSION, xmlFileStream);
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
			xmlFileStream = androidContext.getAssets().open("standardProject.xml");
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

		//assertEquals("the full headers not added. still have " + values.size() + " values ", values.size(), 10);
		assertEquals("androidVersion tag not parsed", values.get(HeaderTags.PLATFORMVERSION.getXmlTagString()), "10");
		assertEquals("catroidVersionCode tag not parsed",
				values.get(HeaderTags.APPLICATIONBUILDNUMBER.getXmlTagString()), "980");
		assertEquals("catroidVersionName tag not parsed", values.get(HeaderTags.APPLICATIONVERSION.getXmlTagString()),
				"0.7.0beta");
		assertEquals("deviceName tag not parsed", values.get(HeaderTags.DEVICENAME.getXmlTagString()), "GT-I9100");
		assertEquals("ProjectName tag not parsed", values.get(HeaderTags.PROGRAMNAME.getXmlTagString()),
				"standardProjekt");
		assertEquals("screenHeight tag not parsed", values.get(HeaderTags.SCREENHEIGHT.getXmlTagString()), "800");
		assertEquals("screenWidth tag not parsed", values.get(HeaderTags.SCREENWIDTH.getXmlTagString()), "480");
	}
}
