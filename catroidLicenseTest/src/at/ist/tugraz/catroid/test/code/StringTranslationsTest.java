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
package at.ist.tugraz.catroid.test.code;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author pete
 * 
 */
public class StringTranslationsTest extends TestCase {
	private static final String[] LANGUAGES = { "English", "German" };
	private static final String[] LANGUAGE_SUFFIXES = { "", "-de" };

	public void testStringTranslations() throws IOException, ParserConfigurationException, SAXException {
		List<String> allStringNames = new ArrayList<String>(); // Using a List instead of a set to preserve order
		Map<String, List<String>> languageStrings = new HashMap<String, List<String>>();

		boolean missingStrings = false;
		StringBuilder errorMessage = new StringBuilder();

		List<File> stringFiles = new ArrayList<File>();
		for (String languageSuffix : LANGUAGE_SUFFIXES) {
			stringFiles.add(new File("../catroid/res/values" + languageSuffix + "/strings.xml"));
		}

		for (String language : LANGUAGES) {
			List<String> stringNames = new ArrayList<String>();
			languageStrings.put(language, stringNames);
		}

		for (int i = 0; i < LANGUAGES.length; i++) {
			File stringFile = stringFiles.get(i);
			assertNotNull("File is null: " + stringFile.getCanonicalPath());
			if (!stringFile.exists() || !stringFile.canRead()) {
				fail("Could not read file " + stringFile.getCanonicalPath());
			}
			List<String> languageStringNames = languageStrings.get(LANGUAGES[i]);

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(stringFile);
			document.getDocumentElement().normalize();

			NodeList strings = document.getElementsByTagName("string");
			for (int nodeIndex = 0; nodeIndex < strings.getLength(); nodeIndex++) {
				Node node = strings.item(nodeIndex);
				assertTrue("Node is not an element: " + node.toString(), node.getNodeType() == Node.ELEMENT_NODE);
				Element element = (Element) node;
				String elementName = element.getAttribute("name");
				if (!allStringNames.contains(elementName)) {
					allStringNames.add(elementName);
				}
				languageStringNames.add(elementName);
			}
		}

		for (String stringName : allStringNames) {
			for (int i = 0; i < LANGUAGES.length; i++) {
				List<String> languageStringNames = languageStrings.get(LANGUAGES[i]);
				if (!languageStringNames.contains(stringName)) {
					missingStrings = true;
					errorMessage.append("\nString with name " + stringName + " is missing in " + LANGUAGES[i]);
				}
			}
		}

		assertFalse("There are untranslated Strings:" + errorMessage.toString(), missingStrings);
	}
}