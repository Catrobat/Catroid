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
package at.tugraz.ist.catroid.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

	/**
	 * @param xmlFileStream
	 * @return
	 */
	public List<String> parse(InputStream xmlFileStream) {
		return saxParser(xmlFileStream);
	}

	private List<String> stringParser(File XMLFileStream) {
		FileReader fReader = null;
		parsedStrings = new ArrayList<String>();
		try {
			fReader = new FileReader(XMLFileStream);
			BufferedReader bReader = new BufferedReader(fReader);

			String currentLine = "";
			boolean inHeader = true;
			boolean tagCheck = false;

			while (inHeader) {
				currentLine = bReader.readLine();

				if (currentLine.trim().equals(OtherTags.CONTENTPROJECT.getOtherXMLTagString())) {
					tagCheck = true;
				}
				if (tagCheck) {
					if (currentLine.trim().equals(OtherTags.SPRITELIST.getOtherXMLTagString())) {
						inHeader = false;
						tagCheck = false;
					} else {
						for (int i = 0; i < 7; i++) {
							String currentTag = tagIndexes[i].getXmlTagString();
							if (currentLine.contains(currentTag)) {
								parsedStrings.add(currentLine.trim().replace("<" + currentTag + ">", "")
										.replace("</" + currentTag + ">", ""));
							}
						}
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return parsedStrings;

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

	/*
	 * 
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (OtherTags.SPRITELIST.getOtherXMLTagString().contains(qName)) {
			throw new SAXException("Header parsing done!");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		for (int i = 0; i < 7; i++) {
			String currentTag = tagIndexes[i].getXmlTagString();
			if (qName.equalsIgnoreCase(currentTag)) {
				parsedStrings.add(tempVal);
				break;
			}
		}

	}

	/**
	 * @param HeaderTag
	 * @param XMLFile
	 * @return The String value of the headerTag of the given xml file
	 */
	public String getvalueof(HeaderTags tag, InputStream XMLFile) {
		List<String> parsedValues = this.parse(XMLFile);

		return parsedValues.get(tag.ordinal());

	}

}
