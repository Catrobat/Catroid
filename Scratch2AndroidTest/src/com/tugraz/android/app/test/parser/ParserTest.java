package com.tugraz.android.app.test.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.tugraz.android.app.BrickDefine;
import com.tugraz.android.app.parser.*;

public class ParserTest extends TestCase {
	private Parser parser;
	
	private String testXml =
	"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"+
	"<stage>"+
	  "<command id=\"1001\">"+
	    "<image path=\"bla.jpg\" />"+
	  "</command>"+
	  "<command id=\"1002\">"+
	    "5"+
	  "</command>"+
	  "<command id=\"2001\">"+
	    "<sound path=\"bla.mp3\" />"+
	  "</command>"+
	"</stage>";
	
	
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

//	public void testParser() {
//		fail("Not yet implemented");
//	}

	public void testParse() throws Throwable{
		Parser parser = new Parser();
		File file = null;
		try {
			file = File.createTempFile("project", "xml");
			if(file.canWrite()){
				OutputStream stream = new FileOutputStream(file);
				stream.write(testXml.getBytes());
				stream.flush();
			}
		}
		catch (IOException e){
			Log.e("ParserTest", "Writing Test XML to file failed");
			e.printStackTrace();
		}
		ArrayList<HashMap<String, String>> list =null;
		try {
			InputStream stream = new FileInputStream(file);
			list = parser.parse(stream);
		}
		catch (FileNotFoundException e){
			Log.e("ParserTest", "Reading from test XML file failed!");
			e.printStackTrace();
		}
		Log.i("ParserTest", testXml);
		String xml = parser.toXml(list);
		
		Log.i("ParserTest", xml);
		assertTrue(xml.equals(testXml));
		
	}
	
	public void testToXml() {
		ArrayList<HashMap<String, String>> brickList = new ArrayList<HashMap<String,String>>();;
	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_ID, "0");
	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.SET_BACKGROUND));
	    map.put(BrickDefine.BRICK_NAME, "blabla");
	    map.put(BrickDefine.BRICK_VALUE, "");
		brickList.add(map);
		
		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_ID, "1");
	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.PLAY_SOUND));
	    map.put(BrickDefine.BRICK_NAME, "blabla");
	    map.put(BrickDefine.BRICK_VALUE, "c:\\sounds\\sound1.wav");
		brickList.add(map);
		
		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_ID, "2");
	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.WAIT));
	    map.put(BrickDefine.BRICK_NAME, "blabla");
	    map.put(BrickDefine.BRICK_VALUE, "100");
		brickList.add(map);
		
		String result = parser.toXml(brickList);
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><stage><command id=\"1001\"><image path=\"\" /></command><command id=\"2001\"><sound path=\"c:\\sounds\\sound1.wav\" /></command><command id=\"1002\">100</command></stage>";
		Log.i("testToXml result", result);
		Log.i("testToXml expected", expected);
		
		assertEquals("constructed list with 3 commands", expected, result);

		
		brickList.clear();
		result = parser.toXml(brickList);
		expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><stage />";
		assertEquals("constructed list without commands", expected, result);
	
	}
}
