/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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
package org.catrobat.catroid.xml.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class HeaderTagsParser extends DefaultHandler {

	private Map<String, String> parsedStrings;
	private String tempValue;

	public Map<String, String> parseHeader(InputStream xmlFileStream) throws ParseException {
		parsedStrings = new HashMap<String, String>();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		try {
			SAXParser parser = parserFactory.newSAXParser();
			parser.parse(xmlFileStream, this);

		} catch (ParserConfigurationException e) {
			Log.e("SimpleParser.saxparser", "parserConfiguration exception");
			throw new ParseException(e);
		} catch (SAXException e) {
			return parsedStrings;
		} catch (IOException e) {
			Log.e("SimpleParser.saxparser", "IO exception");
			throw new ParseException(e);
		}

		return parsedStrings;
	}

	@Override
	public void startElement(String uri, String localName, String tagName, Attributes attributes) throws SAXException {
		if (HeaderStarterAndEndTags.SPRITELIST.getOtherXMLTagString().contains(tagName)) {
			throw new SAXException("Header parsing done!");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempValue = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String tagName) throws SAXException {
		if (tagName != CatroidXMLConstants.PROJECT_HEADER_NAME) {
			parsedStrings.put(tagName, tempValue);
		}
	}

	public String getvalueof(HeaderTags tag, InputStream XMLFile) throws ParseException {
		Map<String, String> parsedValues = this.parseHeader(XMLFile);
		return parsedValues.get(tag.getXmlTagString());
	}

}
