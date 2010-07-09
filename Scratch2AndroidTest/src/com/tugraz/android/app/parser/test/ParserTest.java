package com.tugraz.android.app.parser.test;

import java.util.Vector;

import com.tugraz.android.app.parser.Command;
import com.tugraz.android.app.parser.Parser;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

	private Parser parser;
	
	public ParserTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		parser = new Parser();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParser() {
		fail("Not yet implemented");
	}

	public void testParse() {
		fail("Not yet implemented");
	}

	public void testToXml() {
		Vector<Command> command_list = new Vector<Command>();
		
		Command c = new Command(0, "", 0);
		command_list.add(c);
		
		c = new Command(100, "c:\\sounds\\sound1.wav", 0);
		command_list.add(c);
		
		c = new Command(200, "", 100);
		command_list.add(c);
		
		String result = parser.toXml(command_list);
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><stage><command id=\"0\"><image path=\"\" /></command><command id=\"100\"><sound path=\"c:\\sounds\\sound1.wav\" /></command><command id=\"200\">100</command></stage>";
		assertEquals("constructed list with 3 commands", expected, result);
		
		command_list.clear();
		result = parser.toXml(command_list);
		expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><stage />";
		assertEquals("constructed list without commands", expected, result);
	
	}

}
