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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

public class AssertionErrorMessageTest extends TestCase {
	private static final String[] DIRECTORIES = { ".", "../catroid", "../catroidTest", "../catroidUiTest", };
	private static final String OPENING_BRACKET = "\\(";
	private static final String WHITESPACES = "\\s*";
	private static final String STRING_LITERAL = "\"[^\"]*\"";
	private static final String PARAMETER = "(" + STRING_LITERAL + "|[^\",])*";
	private static final String COMMA = ",";
	private static final String CLOSING_BRACKET = "\\)";

	private String[] assertCommands = { "assertTrue", "assertFalse", "assertEquals" };
	private int[] numberOfParameters = { 2, 2, 3 };

	private String[] regularExpressions;

	public AssertionErrorMessageTest() {
		regularExpressions = new String[assertCommands.length];
		for (int i = 0; i < assertCommands.length; i++) {
			String regex = WHITESPACES + assertCommands[i] + OPENING_BRACKET;
			for (int parameterCount = 0; parameterCount < numberOfParameters[i] - 1; parameterCount++) {
				regex += PARAMETER + COMMA;
			}
			regex += PARAMETER + CLOSING_BRACKET + ".*";
			regularExpressions[i] = regex;
		}
	}

	private void traverseDirectory(File directory) throws IOException {
		File[] contents = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (pathname.isDirectory() && !pathname.getName().equals("gen"))
						|| pathname.getName().endsWith(".java");
			}
		});

		for (File file : contents) {
			if (file.isDirectory()) {
				traverseDirectory(file);
			} else {
				assertionErrorMessagesPresentInFile(file);
			}
		}
	}

	public void testRegex() {
		String[] regularExpressions = new String[assertCommands.length];
		for (int i = 0; i < assertCommands.length; i++) {
			String regex = WHITESPACES + assertCommands[i] + OPENING_BRACKET;
			for (int parameterCount = 0; parameterCount < numberOfParameters[i] - 1; parameterCount++) {
				regex += PARAMETER + COMMA;
			}
			regex += PARAMETER + CLOSING_BRACKET + ".*";
			regularExpressions[i] = regex;
		}

		String[] regexMatches = { "assertTrue(\"message\", parameter);", "assertFalse( \"message\", parameter);",
				"assertEquals(\"message\", a, b);", "assertTrue(\"message, with a comma\", value);",
				"assertTrue(name + \" has wrong value, but...\", value);" };

		String[] regexNotMatches = { "assertTrue(parameter);", "assertTrue((name + \"text\").equals(value));",
				"assertTrue(text.equals(\"a,b\"));", "assertEquals(a, b)" };

		for (String matchingRegex : regexMatches) {
			boolean matchFound = false;
			for (String regex : regularExpressions) {
				if (matchingRegex.matches(regex)) {
					matchFound = true;
				}
			}
			assertTrue("No regex matched expression " + matchingRegex, matchFound);
		}

		for (String notMatchingRegex : regexNotMatches) {
			boolean matchFound = false;
			for (String regex : regularExpressions) {
				if (notMatchingRegex.matches(regex)) {
					matchFound = true;
				}
			}
			assertFalse("Expression was matched even though it shouldn't: " + notMatchingRegex, matchFound);
		}
	}

	public void assertionErrorMessagesPresentInFile(File file) throws IOException {
		assertTrue("Could not read file " + file.getAbsolutePath(), file.exists() && file.canRead());

		BufferedReader reader = new BufferedReader(new FileReader(file));

		String currentLine = "";
		int lineNumber = 0;
		while ((currentLine = reader.readLine()) != null) {
			lineNumber++;
			boolean isAssertCommand = false;
			for (String assertCommand : assertCommands) {
				if (currentLine.contains(assertCommand) && !currentLine.contains("\"" + assertCommand)) {
					isAssertCommand = true;
				}
			}
			if (isAssertCommand && currentLine.contains(";")) {
				boolean matchFound = false;
				for (String regex : regularExpressions) {
					if (currentLine.matches(regex)) {
						matchFound = true;
					}
				}
				assertTrue("Assert without message found in file " + file.getAbsolutePath() + " on line " + lineNumber,
						matchFound);
			}
		}

		reader.close();
	}

	public void testAssertionErrorMessagesPresent() throws IOException {
		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			traverseDirectory(directory);
		}
	}
}
