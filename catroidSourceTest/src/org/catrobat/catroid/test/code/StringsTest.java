/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
package org.catrobat.catroid.test.code;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author pete
 * 
 */
public class StringsTest extends TestCase {
	private static final String[] LANGUAGES = { "English", "German" }; //, "Russian", "Romanian" };
	private static final String[] LANGUAGE_SUFFIXES = { "", "-de-rDE" }; //, "-ru", "-ro" };
	private static final String XML_STRING_PREFIX = "@string/";

	private List<File> getStringFiles() throws IOException {
		List<File> stringFiles = new ArrayList<File>();
		for (String languageSuffix : LANGUAGE_SUFFIXES) {
			File stringFile = new File("../catroid/res/values" + languageSuffix + "/strings.xml");
			assertNotNull("File is null: " + stringFile.getCanonicalPath(), stringFile);
			if (!stringFile.exists() || !stringFile.canRead()) {
				fail("Could not read file " + stringFile.getCanonicalPath());
			}
			stringFiles.add(stringFile);
		}
		return stringFiles;
	}

	private NodeList getStrings(File file) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		document.getDocumentElement().normalize();

		return document.getElementsByTagName("string");
	}

	private List<String> getAllStringNames() throws SAXException, IOException, ParserConfigurationException {
		List<String> allStringNames = new ArrayList<String>();
		List<File> stringFiles = getStringFiles();

		for (int i = 0; i < LANGUAGES.length; i++) {
			File stringFile = stringFiles.get(i);

			NodeList strings = getStrings(stringFile);
			for (int nodeIndex = 0; nodeIndex < strings.getLength(); nodeIndex++) {
				Node node = strings.item(nodeIndex);
				assertTrue("Node is not an element: " + node.toString(), node.getNodeType() == Node.ELEMENT_NODE);
				Element element = (Element) node;
				String elementName = element.getAttribute("name");
				if (!allStringNames.contains(elementName)) {
					allStringNames.add(elementName);
				}
			}
		}

		return allStringNames;
	}

	private Map<String, List<String>> getStringNamesPerLanguage() throws SAXException, IOException,
			ParserConfigurationException {
		Map<String, List<String>> languageStrings = new HashMap<String, List<String>>();
		List<File> stringFiles = getStringFiles();

		for (String language : LANGUAGES) {
			List<String> stringNames = new ArrayList<String>();
			languageStrings.put(language, stringNames);
		}

		for (int i = 0; i < LANGUAGES.length; i++) {
			File stringFile = stringFiles.get(i);
			List<String> languageStringNames = languageStrings.get(LANGUAGES[i]);

			NodeList strings = getStrings(stringFile);
			for (int nodeIndex = 0; nodeIndex < strings.getLength(); nodeIndex++) {
				Node node = strings.item(nodeIndex);
				assertTrue("Node is not an element: " + node.toString(), node.getNodeType() == Node.ELEMENT_NODE);
				Element element = (Element) node;
				String elementName = element.getAttribute("name");
				languageStringNames.add(elementName);
			}
		}

		return languageStrings;
	}

	public void testStringTranslations() throws IOException, ParserConfigurationException, SAXException {
		boolean missingStrings = false;
		StringBuilder errorMessage = new StringBuilder();

		List<String> allStringNames = getAllStringNames(); // Using a List instead of a set to preserve order
		Map<String, List<String>> languageStrings = getStringNamesPerLanguage();

		for (String stringName : allStringNames) {
			for (String language : LANGUAGES) {
				List<String> languageStringNames = languageStrings.get(language);
				if (!languageStringNames.contains(stringName)) {
					missingStrings = true;
					errorMessage.append("\nString with name ").append(stringName).append(" is missing in ")
							.append(language);
				}
			}
		}

		assertFalse("There are untranslated Strings:" + errorMessage, missingStrings);
	}

	private List<File> getLayoutXmlFiles() {
		List<File> layoutFiles = new ArrayList<File>();

		File layoutDir = new File("../catroid/res/layout/");
		File[] fileArray = layoutDir.listFiles();
		if (fileArray != null) {
			for (File file : fileArray) {
				if (file.getName().endsWith(".xml")) {
					layoutFiles.add(file);
				}
			}
		}
		return layoutFiles;
	}

	private List<String> getUsedStringsFromLayoutFile(File layoutFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(layoutFile));

		List<String> usedStrings = new ArrayList<String>();
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			String[] split = currentLine.split("\"");

			// the number of " per line must be even
			if (split.length <= 1) {
				continue;
			}
			for (int i = 1; i < split.length; i += 2) {
				if (split[i].startsWith(XML_STRING_PREFIX)) {
					usedStrings.add(split[i].substring(XML_STRING_PREFIX.length()) + '|' + layoutFile.getName());
				}
			}
		}
		reader.close();
		return usedStrings;
	}

	private List<String> getAllStringsUsedInLayoutXMLs() throws IOException {
		List<String> allStringNames = new ArrayList<String>();
		List<File> layoutFiles = getLayoutXmlFiles();

		for (File layoutFile : layoutFiles) {
			List<String> usedStrings = getUsedStringsFromLayoutFile(layoutFile);
			allStringNames.addAll(usedStrings);
		}

		return allStringNames;
	}

	public void testMissingStrings() throws SAXException, IOException, ParserConfigurationException {
		boolean missingStringsFound = false;

		StringBuilder stringXmlSourceCodeBuilder = new StringBuilder();
		File defaultResDirectory = new File("../catroid/res/values/");
		File[] fileArray = defaultResDirectory.listFiles();
		if (fileArray != null) {
			for (File defaultStringFile : fileArray) {
				BufferedReader reader = new BufferedReader(new FileReader(defaultStringFile));
				String currentLine;
				while ((currentLine = reader.readLine()) != null) {
					stringXmlSourceCodeBuilder.append(currentLine);
				}
				reader.close();
			}
		}

		String stringXmlSourceCode = stringXmlSourceCodeBuilder.toString();
		List<String> allStringsUsedInLayoutFiles = getAllStringsUsedInLayoutXMLs();

		Set<String> missingStrings = new HashSet<String>();
		for (String stringPairUsedInXml : allStringsUsedInLayoutFiles) {
			String[] split = stringPairUsedInXml.split("\\|");
			String stringUsedInXml = split[0];
			String layoutFileName = split[1];
			if (!stringXmlSourceCode.contains(stringUsedInXml)) {
				missingStringsFound = true;
				missingStrings.add(stringUsedInXml + " used in the file: " + layoutFileName);
			}
		}
		StringBuilder errorMessageBuilder = new StringBuilder(62);
		for (String missing : missingStrings) {
			errorMessageBuilder.append("\nString with name ").append(missing)
					.append(" is missing in the default resource folder.");
		}
		assertFalse("Missing string resources were found:" + errorMessageBuilder, missingStringsFound);
	}
}
