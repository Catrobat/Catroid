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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class AssertionErrorMessageTest extends TestCase {
	private static final String[] DIRECTORIES = { ".", "../catroid", "../catroidTest", "../catroidUiTest", };

	// Bits of regular expressions to be used
	private static final String OPENING_BRACKET = "\\(";
	private static final String WHITESPACES = "(\\s)*";
	private static final String ANYTHING = ".*";
	private static final String STRING_LITERAL = "\"[^\"]*\"";
	private static final String NON_STRING_NON_COMMA = "[^\",]";
	private static final String PARAMETER = "(" + STRING_LITERAL + "|" + NON_STRING_NON_COMMA + ")*";
	private static final String COMMA = ",";
	private static final String CLOSING_BRACKET = "\\)";
	private static final String COMMENT = "/\\*[^\\*/]\\*/";
	private static final String LINE_COMMENT = "//.*";

	private class AssertMethod {
		private String commandName;
		private int numberOfParameters;

		public AssertMethod(String commandName, int numberOfParameters) {
			this.commandName = commandName;
			this.numberOfParameters = numberOfParameters;
		}

		public String getCommandName() {
			return commandName;
		}

		public int getNumberOfParameters() {
			return numberOfParameters;
		}
	}

	private List<AssertMethod> assertMethods;
	private String regexIsAssertMethod;
	private String regexAssertContainsErrorMessage;
	private String regexIsCompleteCommand = "(" + STRING_LITERAL + "|" + COMMENT + "|[^;]" + ")*;" + WHITESPACES + "("
			+ LINE_COMMENT + ")?";
	private StringBuffer errorMessages;
	private boolean errorFound;

	public AssertionErrorMessageTest() {
		System.out.println(regexIsCompleteCommand);
		/*
		 * All JUnit assert commands taken from http://www.junit.org/apidocs/org/junit/Assert.html and
		 * http://www.junit.org/apidocs/junit/framework/Assert.html as of JUnit version 4.9b2
		 */
		assertMethods = new ArrayList<AssertionErrorMessageTest.AssertMethod>();
		assertMethods.add(new AssertMethod("assertArrayEquals", 3));
		assertMethods.add(new AssertMethod("assertEquals", 3));
		assertMethods.add(new AssertMethod("assertFalse", 2));
		assertMethods.add(new AssertMethod("assertNotNull", 2));
		assertMethods.add(new AssertMethod("assertNotSame", 3));
		assertMethods.add(new AssertMethod("assertNull", 2));
		assertMethods.add(new AssertMethod("assertSame", 3));
		assertMethods.add(new AssertMethod("assertThat", 3));
		assertMethods.add(new AssertMethod("assertTrue", 2));
		assertMethods.add(new AssertMethod("fail", 1));

		// Build regular expressions to check if a String is an assert method and if it contains an error message
		regexIsAssertMethod = "";
		regexAssertContainsErrorMessage = "";
		for (int i = 0; i < assertMethods.size(); i++) {
			regexIsAssertMethod += "(" + WHITESPACES + assertMethods.get(i).getCommandName() + ANYTHING + ")";
			regexAssertContainsErrorMessage += "(" + WHITESPACES + assertMethods.get(i).getCommandName()
					+ OPENING_BRACKET;
			for (int parameterCount = 0; parameterCount < assertMethods.get(i).getNumberOfParameters() - 1; parameterCount++) {
				regexAssertContainsErrorMessage += PARAMETER + COMMA;
			}
			regexAssertContainsErrorMessage += PARAMETER + CLOSING_BRACKET + ANYTHING + ")";
			if (i < assertMethods.size() - 1) {
				regexIsAssertMethod += "|";
				regexAssertContainsErrorMessage += "|";
			}
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
		String[] regexMatches = { "assertTrue(\"message\", parameter);", "assertFalse( \"message\", parameter);",
				"assertEquals(\"message\", a, b);", "assertTrue(\"message, with a comma\", value);",
				"assertTrue(name + \" has wrong value, but...\", value);" };

		for (String matchingRegex : regexMatches) {
			assertTrue("No regex matched expression " + matchingRegex,
					matchingRegex.matches(regexAssertContainsErrorMessage));
		}

		String[] regexNotMatches = { "assertTrue(parameter);", "assertTrue((name + \"text\").equals(value));",
				"assertTrue(text.equals(\"a,b\"));", "assertEquals(a, b)" };

		for (String notMatchingRegex : regexNotMatches) {
			assertFalse("Expression was matched even though it shouldn't: " + notMatchingRegex,
					notMatchingRegex.matches(regexAssertContainsErrorMessage));
		}

		String[] completeCommands = { "method();", "method(param);", "method(\"text;\");" };
		for (String completeCommand : completeCommands) {
			assertTrue("Regex for complete commands did not match " + completeCommand,
					completeCommand.matches(regexIsCompleteCommand));
		}

		String[] incompleteCommands = { "method()", "method(", "method(\";\"", "method(/*;*/" };
		for (String incompleteCommand : incompleteCommands) {
			assertFalse("Regex for complete commands matched incomplete command " + incompleteCommand,
					incompleteCommand.matches(regexIsCompleteCommand));
		}
	}

	public void assertionErrorMessagesPresentInFile(File file) throws IOException {
		assertTrue("Could not read file " + file.getAbsolutePath(), file.exists() && file.canRead());

		BufferedReader reader = new BufferedReader(new FileReader(file));

		String currentLine = "";
		int lineNumber = 0;
		while ((currentLine = reader.readLine()) != null) {
			lineNumber++;
			if (currentLine.matches(regexIsAssertMethod)) {
				while (!currentLine.matches(regexIsCompleteCommand)) {
					currentLine += reader.readLine();
					currentLine.replace("\n", "");
					lineNumber++;
				}

				if (!currentLine.matches(regexAssertContainsErrorMessage)) {
					errorFound = true;
					errorMessages.append(file.getAbsolutePath() + " on line " + lineNumber + "\n");
				}
			}
		}

		reader.close();
	}

	public void testAssertionErrorMessagesPresent() throws IOException {
		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			errorMessages = new StringBuffer();
			errorFound = false;

			traverseDirectory(directory);

			assertFalse("Assert statements without error messages have been found in the following files:\n"
					+ errorMessages.toString(), errorFound);
		}
	}
}
