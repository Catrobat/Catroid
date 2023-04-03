/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.test.ui.regexassistant

import org.catrobat.catroid.utils.JsonRegexExtractor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.regex.Pattern

class JsonRegexExtractorTest {
    @Before
    fun setUp() {
    }

    private val jsonExtractor: JsonRegexExtractor? = null
    private val jsonExample = """{"widget": {
    "debug": "on",
    "window": {
        "title": "Sample Konfabulator Widget",
        "name": "main_window",
        "width": 500,
        "height": 500
    },
    "image": { 
        "src": "Images/Sun.png",
        "name": "sun1",
        "hOffset": 250,
        "vOffset": 250,
        "alignment": "center"
    },
    "text": {
        "data": "Click Here",
        "size":-36.45e-45,
        "style": "bold",
        "name": "text1",
        "hOffset": 251,
        "vOffset": 100,
        "alignment": "center",
        "onMouseUp": "sun1.opacity = (sun1.opacity / 100) * 90;"
    }
    "other recognized json expressions": {
        "logic" :true ,
        "un-nested array" : [ 45, 17, "string", { "some object" : "else"}, null ] ,
        "un-nested object":{"x":0,"array":["y",null]},
        "escaped" : "\"hello world\""
    }
}} """

    @Test
    fun testParserExpressionNumericalParameter() {
        val regexPattern = Pattern.compile(JsonRegexExtractor.getJsonParserRegex("size"))
        val matcher = regexPattern.matcher(jsonExample)
        matcher.find()
        Assert.assertEquals("-36.45e-45", matcher.group(1))
    }

    @Test
    fun testParserExpressionStringParameter() {
        val regexPattern = Pattern.compile(JsonRegexExtractor.getJsonParserRegex("style"))
        val matcher = regexPattern.matcher(jsonExample)
        matcher.find()
        Assert.assertEquals("bold", matcher.group(1))
    }

    @Test
    fun testParserExpressionDoubleParameter() {
        val regexPattern = Pattern.compile(JsonRegexExtractor.getJsonParserRegex("vOffset"))
        val matcher = regexPattern.matcher(jsonExample)
        matcher.find()
        Assert.assertEquals("250", matcher.group(1))
    }

    @Test
    fun testParserExpressionLogic() {
        val regexPattern = Pattern.compile(JsonRegexExtractor.getJsonParserRegex("logic"))
        val matcher = regexPattern.matcher(jsonExample)
        matcher.find()
        Assert.assertEquals("true", matcher.group(1))
    }

    @Test
    fun testParserExpressionArray() {
        val regexPattern = Pattern.compile(JsonRegexExtractor.getJsonParserRegex("un-nested array"))
        val matcher = regexPattern.matcher(jsonExample)
        matcher.find()
        Assert.assertEquals(
            "[ 45, 17, \"string\", { \"some object\" : \"else\"}, null ]",
            matcher.group(1)
        )
    }

    @Test
    fun testParserExpressionObject() {
        val regexPattern =
            Pattern.compile(JsonRegexExtractor.getJsonParserRegex("un-nested object"))
        val matcher = regexPattern.matcher(jsonExample)
        matcher.find()
        Assert.assertEquals("{\"x\":0,\"array\":[\"y\",null]}", matcher.group(1))
    }

    @Test
    fun testParserExpressionStringWithEscapedQuotationMark() {
        val regexPattern = Pattern.compile(JsonRegexExtractor.getJsonParserRegex("escaped"))
        val matcher = regexPattern.matcher(jsonExample)
        matcher.find()
        Assert.assertEquals("\\\"hello world\\\"", matcher.group(1))
    }
}
