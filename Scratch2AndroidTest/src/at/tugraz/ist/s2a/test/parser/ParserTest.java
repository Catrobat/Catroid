package at.tugraz.ist.s2a.test.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import junit.framework.TestCase;
import android.test.AndroidTestCase;
import android.util.Log;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.utils.*;
import at.tugraz.ist.s2a.utils.parser.Parser;


public class ParserTest extends AndroidTestCase {
	
	private String testXml =
	"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"+
	"<project>"+
	"<stage name=\"stage\">"+
	  "<command id=\"1001\">"+
	    "<image path=\"bla.jpg\" />"+
	  "</command>"+
	  "<command id=\"1002\">"+
	    "5"+
	  "</command>"+
	  "<command id=\"2001\">"+
	    "<sound path=\"bla.mp3\" />"+
	  "</command>"+
	"</stage>"+
	"<object name=\"sprite\">"+
	  "<command id=\"4003\">"+
	    "<image path=\"bla.jpg\" />"+
	  "</command>"+
	  "<command id=\"3001\">"+
	  	"<x>5</x>"+
	  	"<y>7</y>"+
	  "</command>"+
	  "<command id=\"4001\" />"+
	  "<command id=\"4002\" />"+
	  "<command id=\"4003\">"+
	    "<image path=\"bla.jpg\" />"+
	  "</command>"+
	"</object>"+
	"</project>";
	
	
	public ParserTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

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
		TreeMap<String, ArrayList<HashMap<String, String>>> list =null;
		try {
			InputStream stream = new FileInputStream(file);
			list = parser.parse(stream, this.getContext());
		}
		catch (FileNotFoundException e){
			Log.e("ParserTest", "Reading from test XML file failed!");
			e.printStackTrace();
		}
		//TODO test if first element is stage (name does not need to be 'stage')

		//test some of the data
		assertEquals(3, list.get(list.firstKey()).size());
		assertEquals("1001", list.get(list.firstKey()).get(0).get(BrickDefine.BRICK_TYPE));
		
		assertEquals("bla.jpg", list.get(list.firstKey()).get(0).get(BrickDefine.BRICK_VALUE));
		
		assertEquals("1002", list.get(list.firstKey()).get(1).get(BrickDefine.BRICK_TYPE));
		assertEquals("2001", list.get(list.firstKey()).get(2).get(BrickDefine.BRICK_TYPE));
		
		assertEquals(5, list.get("sprite").size());
		assertEquals("4003", list.get("sprite").get(0).get(BrickDefine.BRICK_TYPE));
		
		assertEquals("3001", list.get("sprite").get(1).get(BrickDefine.BRICK_TYPE));
		assertEquals("5", list.get("sprite").get(1).get(BrickDefine.BRICK_VALUE));
		assertEquals("7", list.get("sprite").get(1).get(BrickDefine.BRICK_VALUE_1));
		assertEquals("4001", list.get("sprite").get(2).get(BrickDefine.BRICK_TYPE));
		assertEquals("4002", list.get("sprite").get(3).get(BrickDefine.BRICK_TYPE));
		assertEquals("4003", list.get("sprite").get(4).get(BrickDefine.BRICK_TYPE));
	}
	
	public void testToXml() {
		//TODO redesign test depending on refactoring the parser
		assert(false);
		
//		Parser parser = new Parser();
//		ArrayList<HashMap<String, String>> brickList = new ArrayList<HashMap<String,String>>();;
//	
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put(BrickDefine.BRICK_ID, "0");
//	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.SET_BACKGROUND));
//	    map.put(BrickDefine.BRICK_NAME, "blabla");
//	    map.put(BrickDefine.BRICK_VALUE, "");
//		brickList.add(map);
//		
//		map = new HashMap<String, String>();
//		map.put(BrickDefine.BRICK_ID, "1");
//	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.PLAY_SOUND));
//	    map.put(BrickDefine.BRICK_NAME, "blabla");
//	    map.put(BrickDefine.BRICK_VALUE, "c:\\sounds\\sound1.wav");
//		brickList.add(map);
//		
//		map = new HashMap<String, String>();
//		map.put(BrickDefine.BRICK_ID, "2");
//	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.WAIT));
//	    map.put(BrickDefine.BRICK_NAME, "blabla");
//	    map.put(BrickDefine.BRICK_VALUE, "100");
//		brickList.add(map);
//		
//		String result = parser.toXml(brickList);
//		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><stage><command id=\"1001\"><image path=\"\" /></command><command id=\"2001\"><sound path=\"c:\\sounds\\sound1.wav\" /></command><command id=\"1002\">100</command></stage>";
//		Log.i("testToXml result", result);
//		Log.i("testToXml expected", expected);
//		
//		assertEquals("constructed list with 3 commands", expected, result);
//
//		
//		brickList.clear();
//		result = parser.toXml(brickList);
//		expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><stage />";
//		assertEquals("constructed list without commands", expected, result);
	
	}
}
