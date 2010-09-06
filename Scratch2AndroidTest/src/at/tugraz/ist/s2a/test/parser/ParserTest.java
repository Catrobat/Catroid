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
import android.util.Pair;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.utils.*;
import at.tugraz.ist.s2a.utils.parser.Parser;




public class ParserTest extends AndroidTestCase {
	
	private String testXml =
	"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"+
	"<project>"+
	"<stage name=\"stage\">"+
	  "<command id=\""+ BrickDefine.SET_BACKGROUND+"\">"+
	    "<image path=\"bla.jpg\" path_thumb=\"bla.jpg\"/>"+
	  "</command>"+
	  "<command id=\""+ BrickDefine.WAIT+"\">"+
	    "5"+
	  "</command>"+
	  "<command id=\""+ BrickDefine.PLAY_SOUND+"\">"+
	    "<sound path=\"bla.mp3\" file_name=\"bla\" />"+
	  "</command>"+
	"</stage>"+
	"<object name=\"sprite\">"+
	  "<command id=\""+ BrickDefine.SET_COSTUME+"\">"+
	    "<image path=\"bla.jpg\" path_thumb=\"bla.jpg\" />"+
	  "</command>"+
	  "<command id=\""+ BrickDefine.GO_TO+"\">"+
	  	"<x>5</x>"+
	  	"<y>7</y>"+
	  "</command>"+
	  "<command id=\""+ BrickDefine.HIDE+"\" />"+
	  "<command id=\""+ BrickDefine.SHOW+"\" />"+
	  "<command id=\""+ BrickDefine.SET_COSTUME+"\">"+
	    "<image path=\"bla.jpg\" path_thumb=\"bla.jpg\" />"+
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
		ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> list =null;
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
		assertEquals(3, list.get(0).second.size());
		assertEquals(String.valueOf(BrickDefine.SET_BACKGROUND), list.get(0).second.get(0).get(BrickDefine.BRICK_TYPE));
		
		assertEquals("bla.jpg", list.get(0).second.get(0).get(BrickDefine.BRICK_VALUE));
		
		assertEquals(String.valueOf(BrickDefine.WAIT), list.get(0).second.get(1).get(BrickDefine.BRICK_TYPE));
		assertEquals(String.valueOf(BrickDefine.PLAY_SOUND), list.get(0).second.get(2).get(BrickDefine.BRICK_TYPE));
		
		assertEquals(5, list.get(1).second.size());
		assertEquals(String.valueOf(BrickDefine.SET_COSTUME), list.get(1).second.get(0).get(BrickDefine.BRICK_TYPE));
		
		assertEquals(String.valueOf(BrickDefine.GO_TO), list.get(1).second.get(1).get(BrickDefine.BRICK_TYPE));
		assertEquals("5", list.get(1).second.get(1).get(BrickDefine.BRICK_VALUE));
		assertEquals("7", list.get(1).second.get(1).get(BrickDefine.BRICK_VALUE_1));
		assertEquals(String.valueOf(BrickDefine.HIDE), list.get(1).second.get(2).get(BrickDefine.BRICK_TYPE));
		assertEquals(String.valueOf(BrickDefine.SHOW), list.get(1).second.get(3).get(BrickDefine.BRICK_TYPE));
		assertEquals(String.valueOf(BrickDefine.SET_COSTUME), list.get(1).second.get(4).get(BrickDefine.BRICK_TYPE));
		
		Log.i("ParserTest", "the name of the first element: "+list.get(0).first);
		assertEquals("Stage", list.get(0).first); //TODO name für stage ganz wegloeschen?

		
	}
	
	public void testToXml() {

		Parser parser = new Parser();
		ArrayList<HashMap<String, String>> brickList = new ArrayList<HashMap<String,String>>();;
	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_ID, "0");
	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.SET_BACKGROUND));
	    map.put(BrickDefine.BRICK_NAME, "blabla");
	    map.put(BrickDefine.BRICK_VALUE, "bla.jpg");
	    map.put(BrickDefine.BRICK_VALUE_1, "bla.jpg");
		brickList.add(map);
		
		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_ID, "1");
	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.PLAY_SOUND));
	    map.put(BrickDefine.BRICK_NAME, "sound1");
	    map.put(BrickDefine.BRICK_VALUE, "sound1.mp3");
		brickList.add(map);
		
		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_ID, "2");
	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(BrickDefine.WAIT));
	    map.put(BrickDefine.BRICK_NAME, "blabla");
	    map.put(BrickDefine.BRICK_VALUE, "100");
		brickList.add(map);
		
		ArrayList<Pair<String, ArrayList<HashMap<String,String>>>> spritesMap = new ArrayList<Pair<String, ArrayList<HashMap<String,String>>>>();
		spritesMap.add(new Pair<String, ArrayList<HashMap<String,String>>>("Stage", brickList));
		String result = parser.toXml(spritesMap);
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><project><stage name=\"Stage\"><command id=\""+ BrickDefine.SET_BACKGROUND+"\"><image path=\"bla.jpg\" path_thumb=\"bla.jpg\" /></command><command id=\""+ BrickDefine.PLAY_SOUND+"\"><sound path=\"sound1.mp3\" file_name=\"sound1\" /></command><command id=\""+ BrickDefine.WAIT+"\">100</command></stage></project>";
		Log.i("testToXml result", result);
		Log.i("testToXml expected", expected);
		
		assertEquals("constructed list with 3 commands", expected, result);

		
		brickList.clear();
		spritesMap.clear();
		spritesMap.add(new Pair<String, ArrayList<HashMap<String,String>>>("Stage", brickList));
		
		result = parser.toXml(spritesMap);
		expected = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><project><stage name=\"Stage\" /></project>";
		assertEquals("constructed list without commands", expected, result);
	
	}
}
