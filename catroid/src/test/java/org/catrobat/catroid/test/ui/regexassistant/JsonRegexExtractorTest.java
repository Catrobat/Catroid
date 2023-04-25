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

package org.catrobat.catroid.test.ui.regexassistant;

import org.catrobat.catroid.utils.JsonRegexExtractor;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class JsonRegexExtractorTest {
	@Before
	public void setUp() {
	}
	private JsonRegexExtractor jsonExtractor;
	private String jsonExample = "{\"widget\": {\n"
			+ "    \"debug\": \"on\",\n"
			+ "    \"window\": {\n"
			+ "        \"title\": \"Sample Konfabulator Widget\",\n"
			+ "        \"name\": \"main_window\",\n"
			+ "        \"width\": 500,\n"
			+ "        \"height\": 500\n"
			+ "    },\n"
			+ "    \"image\": { \n"
			+ "        \"src\": \"Images/Sun.png\",\n"
			+ "        \"name\": \"sun1\",\n"
			+ "        \"hOffset\": 250,\n"
			+ "        \"vOffset\": 250,\n"
			+ "        \"alignment\": \"center\"\n"
			+ "    },\n"
			+ "    \"text\": {\n"
			+ "        \"data\": \"Click Here\",\n"
			+ "        \"size\":-36.45e-45,\n"
			+ "        \"style\": \"bold\",\n"
			+ "        \"name\": \"text1\",\n"
			+ "        \"hOffset\": 251,\n"
			+ "        \"vOffset\": 100,\n"
			+ "        \"alignment\": \"center\",\n"
			+ "        \"onMouseUp\": \"sun1.opacity = (sun1.opacity / 100) * 90;\"\n"
			+ "    }\n"
			+ "    \"other recognized json expressions\": {\n"
			+ "        \"logic\" :true ,\n"
			+ "        \"un-nested array\" : [ 45, 17, \"string\", { \"some object\" : \"else\"}, null ] ,\n"
			+ "        \"un-nested object\":{\"x\":0,\"array\":[\"y\",null]},\n"
			+ "        \"escaped\" : \"\\\"hello world\\\"\"\n"
			+ "    }\n"			
			+ "}} ";

	@Test
	public void testParserExpressionNumericalParameter() {
		Pattern regexPattern = Pattern.compile(jsonExtractor.getJsonParserRegex("size"));
		Matcher matcher = regexPattern.matcher(jsonExample);
		matcher.find();
		assertEquals("-36.45e-45", matcher.group(1));
	}

	@Test
	public void testParserExpressionStringParameter() {
		Pattern regexPattern = Pattern.compile(jsonExtractor.getJsonParserRegex("style"));
		Matcher matcher = regexPattern.matcher(jsonExample);
		matcher.find();
		assertEquals("bold", matcher.group(1));
	}

	@Test
	public void testParserExpressionDoubleParameter() {
		Pattern regexPattern = Pattern.compile(jsonExtractor.getJsonParserRegex("vOffset"));
		Matcher matcher = regexPattern.matcher(jsonExample);
		matcher.find();
		assertEquals("250", matcher.group(1));
	}

	@Test
	public void testParserExpressionLogic() {
		Pattern regexPattern = Pattern.compile(jsonExtractor.getJsonParserRegex("logic"));
		Matcher matcher = regexPattern.matcher(jsonExample);
		matcher.find();
		assertEquals("true", matcher.group(1));
	}

	@Test
	public void testParserExpressionArray() {
		Pattern regexPattern = Pattern.compile(jsonExtractor.getJsonParserRegex("un-nested array"));
		Matcher matcher = regexPattern.matcher(jsonExample);
		matcher.find();
		assertEquals("[ 45, 17, \"string\", { \"some object\" : \"else\"}, null ]", matcher.group(1));
	}

	@Test
	public void testParserExpressionObject() {
		Pattern regexPattern = Pattern.compile(jsonExtractor.getJsonParserRegex("un-nested object"));
		Matcher matcher = regexPattern.matcher(jsonExample);
		matcher.find();
		assertEquals("{\"x\":0,\"array\":[\"y\",null]}", matcher.group(1));
	}

	@Test
	public void testParserExpressionStringWithEscapedQuotationMark() {
		Pattern regexPattern = Pattern.compile(jsonExtractor.getJsonParserRegex("escaped"));
		Matcher matcher = regexPattern.matcher(jsonExample);
		matcher.find();
		assertEquals("\\\"hello world\\\"", matcher.group(1));
	}
}
