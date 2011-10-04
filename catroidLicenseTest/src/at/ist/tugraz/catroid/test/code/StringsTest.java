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
package at.ist.tugraz.catroid.test.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import at.tugraz.ist.catroid.utils.UtilFile;

/**
 * @author pete
 * 
 */
public class StringsTest extends TestCase {
	private static final String[] LANGUAGES = { "English", "German" }; //, "Russian", "Romanian" };
	private static final String[] LANGUAGE_SUFFIXES = { "", "-de" }; //, "-ru", "-ro" };
	private static final String SOURCE_DIRECTORY = "../catroid/src";
	private static final String RESOURCES_DIRECTORY = "../catroid/res";
	private static final String JAVA_STRING_PREFIX = "R.string.";
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

	public void testUnusedStrings() throws SAXException, IOException, ParserConfigurationException {
		StringBuilder errorMessage = new StringBuilder();
		boolean unusedStringsFound = false;

		StringBuilder javaSourceCodeBuilder = new StringBuilder();
		File directory = new File(SOURCE_DIRECTORY);
		assertTrue("Couldn't find directory: " + SOURCE_DIRECTORY, directory.exists() && directory.isDirectory());
		assertTrue("Couldn't read directory: " + SOURCE_DIRECTORY, directory.canRead());

		List<File> filesToCheck = UtilFile.getFilesFromDirectoryByExtension(directory, ".java");
		for (File file : filesToCheck) {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String currentLine = null;
			while ((currentLine = reader.readLine()) != null) {
				javaSourceCodeBuilder.append(currentLine + "\n");
			}
		}
		String javaSourceCode = javaSourceCodeBuilder.toString();

		StringBuilder xmlSourceCodeBuilder = new StringBuilder();
		directory = new File(RESOURCES_DIRECTORY);
		assertTrue("Couldn't find directory: " + RESOURCES_DIRECTORY, directory.exists() && directory.isDirectory());
		assertTrue("Couldn't read directory: " + RESOURCES_DIRECTORY, directory.canRead());

		filesToCheck = UtilFile.getFilesFromDirectoryByExtension(directory, ".xml");
		for (File file : filesToCheck) {
			if (!file.getName().equals("strings.xml")) {
				BufferedReader reader = new BufferedReader(new FileReader(file));

				String currentLine = null;
				while ((currentLine = reader.readLine()) != null) {
					xmlSourceCodeBuilder.append(currentLine + "\n");
				}
			}
		}
		String xmlSourceCode = xmlSourceCodeBuilder.toString();

		List<String> allStringNames = getAllStringNames(); // Using a List instead of a set to preserve order
		Map<String, List<String>> languageStrings = getStringNamesPerLanguage();

		for (String string : allStringNames) {
			String stringReferenceJava = JAVA_STRING_PREFIX + string;
			String stringReferenceXml = XML_STRING_PREFIX + string;
			if (!javaSourceCode.contains(stringReferenceJava) && !xmlSourceCode.contains(stringReferenceXml)) {
				unusedStringsFound = true;

				errorMessage.append("\nString with name " + string + " is unused (found in ");
				for (int i = 0; i < LANGUAGES.length; i++) {
					List<String> languageStringNames = languageStrings.get(LANGUAGES[i]);
					if (languageStringNames.contains(string)) {
						errorMessage.append(LANGUAGES[i] + ", ");
					}
				}
				errorMessage.replace(errorMessage.length() - 2, errorMessage.length(), ").");
			}
		}

		assertFalse("Unused string resources were found:" + errorMessage.toString(), unusedStringsFound);
	}
}
