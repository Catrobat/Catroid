/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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
package org.catrobat.catroid.test.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.catrobat.catroid.test.utils.Utils;

public class AssertionErrorMessageTest extends TestCase {
	private static final String[] DIRECTORIES = { ".", "../catroid", "../catroidTest", "../catroidUiTest", };

	private static final String OPENING_BRACKET = "\\(";
	private static final String CLOSING_BRACKET = "\\)";
	private static final String WHITESPACES = "(\\s)*";
	private static final String ANYTHING = ".*";
	private static final String STRING_LITERAL = "\"[^\"]*\"";
	private static final String STRING_LITERAL_NOT_EMPTY = "\"[^\"]+\"";
	private static final String COMMENT = "/\\*[^\\*/]\\*/";
	private static final String NON_STRING_NON_COMMA = "[^\",]";
	private static final String METHOD_CALL = "[a-zA-Z0-9_]+" + OPENING_BRACKET + ".*" + CLOSING_BRACKET;
	private static final String PARAMETER = "(" + STRING_LITERAL + "|" + METHOD_CALL + "|" + NON_STRING_NON_COMMA
			+ ")+";
	private static final String ASSERT_MESSAGE = PARAMETER.replace(STRING_LITERAL, STRING_LITERAL_NOT_EMPTY);
	private static final String NOT_A_NUMBER = "([^(\\d\\.\\-)])";
	private static final String COMMA = ",";
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
	private String regexAssertDoesntStartWithNumber;
	private String regexIsCompleteCommand;
	private StringBuffer errorMessages;
	private boolean errorFound;

	public AssertionErrorMessageTest() {
		/*
		 * All JUnit assert commands, along with the number of parameters IF an error message is included,
		 * taken from http://www.junit.org/apidocs/org/junit/Assert.html and
		 * http://www.junit.org/apidocs/junit/framework/Assert.html as of JUnit version 4.9b2
		 */
		assertMethods = new ArrayList<AssertionErrorMessageTest.AssertMethod>();
		assertMethods.add(new AssertMethod("assertArrayEquals", 3));
		assertMethods.add(new AssertMethod("assertEquals", 3));
		assertMethods.add(new AssertMethod("assertEquals", 4));
		assertMethods.add(new AssertMethod("assertFalse", 2));
		assertMethods.add(new AssertMethod("assertNotNull", 2));
		assertMethods.add(new AssertMethod("assertNotSame", 3));
		assertMethods.add(new AssertMethod("assertNull", 2));
		assertMethods.add(new AssertMethod("assertSame", 3));
		assertMethods.add(new AssertMethod("assertThat", 3));
		assertMethods.add(new AssertMethod("assertTrue", 2));
		assertMethods.add(new AssertMethod("fail", 1));

		// Build regular expressions to check if a String is an assert method
		regexIsAssertMethod = "";
		for (int i = 0; i < assertMethods.size(); i++) {
			regexIsAssertMethod += "(" + WHITESPACES + assertMethods.get(i).getCommandName() + ANYTHING + ")";
			if (i < assertMethods.size() - 1) {
				regexIsAssertMethod += "|";
			}
		}

		//  Build regular expression to check if an assert method contains an valid error message
		regexAssertContainsErrorMessage = "";
		for (int i = 0; i < assertMethods.size(); i++) {
			regexAssertContainsErrorMessage += "(" + WHITESPACES + assertMethods.get(i).getCommandName()
					+ OPENING_BRACKET;

			regexAssertContainsErrorMessage += ASSERT_MESSAGE;
			for (int parameterCount = 1; parameterCount < assertMethods.get(i).getNumberOfParameters(); parameterCount++) {
				regexAssertContainsErrorMessage += COMMA + PARAMETER;
			}
			regexAssertContainsErrorMessage += CLOSING_BRACKET + ANYTHING + ")";

			if (i < assertMethods.size() - 1) {
				regexAssertContainsErrorMessage += "|";
			}
		}

		//  Build regular expression to check if an assert message starts with a number
		regexAssertDoesntStartWithNumber = "";
		for (int i = 0; i < assertMethods.size(); i++) {
			regexAssertDoesntStartWithNumber += "(" + WHITESPACES + assertMethods.get(i).getCommandName()
					+ OPENING_BRACKET + NOT_A_NUMBER + ANYTHING + ")";

			if (i < assertMethods.size() - 1) {
				regexAssertDoesntStartWithNumber += "|";
			}
		}

		// Build regular expression to check if a command is complete (i.e. not one line of a multi-line command)
		regexIsCompleteCommand = "(" + STRING_LITERAL + "|" + COMMENT + "|[^;]" + ")*;" + WHITESPACES + "("
				+ LINE_COMMENT + ")?";
	}

	/** Test that tests the regular expressions used in the actual test (meta-test :)) */
	public void testRegex() {
		List<String> matchingAsserts = new ArrayList<String>();
		matchingAsserts.add("assertTrue(\"message\", parameter);");
		matchingAsserts.add("assertTrue(iAmAString, parameter);");
		matchingAsserts.add("assertFalse(\"message\", parameter); // comment");
		matchingAsserts.add("assertEquals(\"message\", a, b);");
		matchingAsserts.add("assertTrue(\"message, with a comma\", value);");
		matchingAsserts.add("assertTrue(name + \" has wrong value, but...\", value);");
		matchingAsserts.add("fail(\"epic fail\");");
		matchingAsserts.add("assertTrue(getErrorMessage(a, b, c), value);");
		matchingAsserts.add("assertEquals(\"Foo!\", bar, baz(), 1e-3);");

		for (String matchingAssert : matchingAsserts) {
			assertTrue(
					"Regex didn't match expression " + matchingAssert,
					matchingAssert.matches(regexAssertContainsErrorMessage)
							&& matchingAssert.matches(regexAssertDoesntStartWithNumber));
		}

		List<String> notMatchingAsserts = new ArrayList<String>();
		notMatchingAsserts.add("assertTrue(parameter);");
		notMatchingAsserts.add("assertTrue((name + \"text\").equals(value));");
		notMatchingAsserts.add("assertTrue(text.equals(\"a,b\"));");
		notMatchingAsserts.add("assertTrue(/* Comment; evil */ value);");
		notMatchingAsserts.add("assertTrue(\"\", true)");
		notMatchingAsserts.add("assertEquals(\"a\", \"b\");");
		notMatchingAsserts.add("assertEquals(a, b)");
		notMatchingAsserts.add("assertEquals(1.0, 1.0, 1.0);");
		notMatchingAsserts.add("fail();");

		for (String notMatchingAssert : notMatchingAsserts) {
			assertFalse(
					"Expression was matched even though it shouldn't: " + notMatchingAssert,
					notMatchingAssert.matches(regexAssertContainsErrorMessage)
							&& notMatchingAssert.matches(regexAssertDoesntStartWithNumber));
		}

		List<String> completeCommands = new ArrayList<String>();
		completeCommands.add("method();");
		completeCommands.add("method(param);");
		completeCommands.add("method(\"text;\");");
		for (String completeCommand : completeCommands) {
			assertTrue("Regex for complete commands did not match command: " + completeCommand,
					completeCommand.matches(regexIsCompleteCommand));
		}

		List<String> incompleteCommands = new ArrayList<String>();
		incompleteCommands.add("method()");
		incompleteCommands.add("method(");
		incompleteCommands.add("method(\";\"");
		incompleteCommands.add("method(/*;*/");
		// TODO: Incorporate this one; don't know how to exclude "//" from regex
		// incompleteCommands.add("method(//;");
		for (String incompleteCommand : incompleteCommands) {
			assertFalse("Regex for complete commands matched incomplete command: " + incompleteCommand,
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
					lineNumber++;
				}
				currentLine.replace("\n", "");

				if (!currentLine.matches(regexAssertContainsErrorMessage)
						|| !currentLine.matches(regexAssertDoesntStartWithNumber)) {
					errorFound = true;
					errorMessages.append(file.getCanonicalPath() + " in line " + lineNumber + "\n");
				}
			}
		}

		reader.close();
	}

	public void testAssertionErrorMessagesPresent() throws IOException {
		errorMessages = new StringBuffer();
		errorFound = false;
		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = Utils.getFilesFromDirectoryByExtension(directory, ".java");
			for (File file : filesToCheck) {
				assertionErrorMessagesPresentInFile(file);
			}
		}
		assertFalse("Assert statements without error messages have been found in the following files: \n"
				+ errorMessages.toString(), errorFound);
	}
}
