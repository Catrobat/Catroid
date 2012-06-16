/** 
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
package at.tugraz.ist.catroid.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * @author Sam
 *         This XML parser have methods to read from the header part of a projectCode.xml
 *         file and instantiate a project object
 */
public class SimpleParser extends DefaultHandler {

	private List<String> parsedStrings;
	private String tempVal;
	HeaderTags[] tagIndexes = HeaderTags.values();
	public Boolean newheaderFound = false;

	public List<String> parse(InputStream xmlFileStream) {
		return saxParser(xmlFileStream);
	}

	private List<String> saxParser(InputStream projectCodeXMLStream) {
		parsedStrings = new ArrayList<String>();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		try {
			SAXParser parser = parserFactory.newSAXParser();
			parser.parse(projectCodeXMLStream, this);

		} catch (ParserConfigurationException e) {
			Log.e("SimpleParser.saxparser", "parserConfiguration exception");
			e.printStackTrace();
		} catch (SAXException e) {
			return parsedStrings;
		} catch (IOException e) {
			Log.e("SimpleParser.saxparser", "IO exception");
			e.printStackTrace();
		}

		return parsedStrings;

	}

	@Override
	public void startElement(String uri, String localName, String tagName, Attributes attributes) throws SAXException {
		if (OtherTags.SPRITELIST.getOtherXMLTagString().contains(tagName)) {
			newheaderFound = false;

			throw new SAXException("Header parsing done!");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);

	}

	@Override
	public void endElement(String uri, String localName, String tagName) throws SAXException {
		//		if (OtherTags.CONTENTPROJECT.getOtherXMLTagString().contains(tagName)) {
		//			headerStarted = true;
		//		}
		//		if (headerStarted) {
		//		if (!newheaderFound) {
		//			for (int i = 0; i < tagIndexes.length; i++) {
		//				String currentHeaderTag = tagIndexes[i].getXmlTagString();
		//				if (tagName.equalsIgnoreCase(currentHeaderTag)) {
		//					parsedStrings.add(tempVal);
		//					if (i == tagIndexes.length - 1) {
		//						newheaderFound = true;
		//					}
		//					break;
		//				}
		//			}
		//		} else {
		//			parsedStrings.add(tempVal);
		//		}
		//		//	}

		parsedStrings.add(tempVal);
		if (parsedStrings.size() > 7) {
			newheaderFound = true;
		}
	}

	public String getvalueof(HeaderTags tag, InputStream XMLFile) {
		List<String> parsedValues = this.parse(XMLFile);
		return parsedValues.get(tag.ordinal());

	}

}
