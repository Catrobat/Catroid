package at.tugraz.ist.s2a.utils.parser;


import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;


import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;

//TODO überlegen, ob stage keinen namen mehr haben soll, da eigener xml-tag oder ob sie sprite sein soll und namen stage hat
//TODO sinnvollere namensbezeichnungen der attribute? vor allem bei sound und image...

public class Parser {
	private DocumentBuilder builder;
	private Document doc;
	private static int mIdCounter = 1;
	// TODO : IDs read and save Ids set in ContentManager
	
	final static int CMD_SET_BACKGROUND = 0;
	final static int CMD_SET_SOUND = 100;
	final static int CMD_WAIT = 200;

	final static String STAGE = "stage";
	final static String OBJECT = "object";
	final static String ID = "id";
	final static String PATH = "path";
	final static String PATH_THUMB = "path_thumb";
	final static String NAME = "name";
	final static String FILE_NAME = "file_name";
	
	final static String PROJECT = "project";
	final static String COMMAND = "command";
	final static String SOUND = "sound";
	final static String IMAGE = "image";
	final static String X = "x";
	final static String Y = "y";
	
	final static String EMPTY_STRING = "";

	public Parser() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			 Log.e("PARSER", e.getMessage());
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			Log.e("PARSER", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> parse(InputStream stream, Context context){
		ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> spritesList = new ArrayList<Pair<String, ArrayList<HashMap<String,String>>>>();
		mIdCounter = 1;
		// TODO : IDs read and save Ids set in ContentManager
		try {
			doc = builder.parse(stream);	
		}
		catch (Exception e) {
			Log.e("Parser", "A parser error occured");
			e.printStackTrace();
		}
		Node stage = doc.getElementsByTagName(STAGE).item(0);
		NodeList sprites = doc.getElementsByTagName(OBJECT);
		
		// first read out stage objects
		NodeList bricks = stage.getChildNodes();
		ArrayList<HashMap<String, String>> sublist = new ArrayList<HashMap<String,String>>();
		for (int i=0; i<bricks.getLength(); i++) {
			int brickType = Integer.parseInt(bricks.item(i).getAttributes().getNamedItem(ID).getNodeValue());
			String value = EMPTY_STRING;
			String value1 = EMPTY_STRING;
			String file_name = EMPTY_STRING;
			String id = "0"; //TODO: default empty
			switch (brickType){
				case BrickDefine.SET_BACKGROUND:
				case BrickDefine.SET_COSTUME:
					value = bricks.item(i).getFirstChild().getAttributes().getNamedItem(PATH).getNodeValue();
					value1 = bricks.item(i).getFirstChild().getAttributes().getNamedItem(PATH_THUMB).getNodeValue();
					Log.d("TEST", value + " " + value1);
					break;
				case BrickDefine.PLAY_SOUND:
				
					value = bricks.item(i).getFirstChild().getAttributes().getNamedItem(PATH).getNodeValue();
					file_name = bricks.item(i).getFirstChild().getAttributes().getNamedItem(FILE_NAME).getNodeValue();
					break;
				case BrickDefine.WAIT:
					value = bricks.item(i).getFirstChild().getNodeValue();
					break;
				case BrickDefine.GO_TO:
					value = bricks.item(i).getFirstChild().getFirstChild().getNodeValue();
					value1 = bricks.item(i).getLastChild().getFirstChild().getNodeValue();
					break;
			}
			HashMap<String, String> map = getBrickMap(id, file_name, value, value1, brickType);
			sublist.add(map);
			// TODO : IDs read and save Ids set in ContentManager
			mIdCounter++;
			
		}
		//insert localized stage name
		spritesList.add(new Pair<String, ArrayList<HashMap<String,String>>>(context.getString(R.string.stage), sublist));
		
		//then read out sprites
		for (int j=0; j<sprites.getLength(); j++){
			bricks = sprites.item(j).getChildNodes();
			sublist = new ArrayList<HashMap<String,String>>();
			String name;
			name = sprites.item(j).getAttributes().getNamedItem(NAME).getNodeValue();
			for (int i=0; i<bricks.getLength(); i++) {
				int brickType = Integer.parseInt(bricks.item(i).getAttributes().getNamedItem(ID).getNodeValue());
				String value = EMPTY_STRING;
				String value1 = EMPTY_STRING;
				String file_name = EMPTY_STRING;
				String id = "0"; //TODO: default empty
				switch (brickType){
				case BrickDefine.SET_BACKGROUND:
				case BrickDefine.SET_COSTUME:
					value = bricks.item(i).getFirstChild().getAttributes().getNamedItem(PATH).getNodeValue();
					value1 = bricks.item(i).getFirstChild().getAttributes().getNamedItem(PATH_THUMB).getNodeValue();
					
					break;
				case BrickDefine.PLAY_SOUND:
				
					value = bricks.item(i).getFirstChild().getAttributes().getNamedItem(PATH).getNodeValue();
					file_name = bricks.item(i).getFirstChild().getAttributes().getNamedItem(FILE_NAME).getNodeValue();
					break;
					case BrickDefine.WAIT:
						value = bricks.item(i).getFirstChild().getNodeValue();
						break;
					case BrickDefine.GO_TO:
						value = bricks.item(i).getFirstChild().getFirstChild().getNodeValue();
						value1 = bricks.item(i).getLastChild().getFirstChild().getNodeValue();
						break;
				}
				HashMap<String, String> map = getBrickMap(id, file_name, value, value1, brickType);
				sublist.add(map);
				// TODO : IDs read and save Ids set in ContentManager
				mIdCounter++;
				
			}
			spritesList.add(new Pair<String, ArrayList<HashMap<String,String>>>(name, sublist));
		}
		
		return spritesList;
	}

	public String toXml(ArrayList<Pair<String, ArrayList<HashMap<String,String>>>> spritesMap) {
		ArrayList<ArrayList<HashMap<String,String>>> spriteBrickList = new ArrayList<ArrayList<HashMap<String,String>>>();
		ArrayList<String> spriteNameList = new ArrayList<String>(); 
		for(int i=0; i<spritesMap.size(); i++){
			spriteBrickList.add(spritesMap.get(i).second);
			spriteNameList.add(spritesMap.get(i).first);
		}
			
		doc = builder.newDocument();
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
    	
		try {
	    	serializer.setOutput(writer);
	    	serializer.startDocument("UTF-8", true);
	    	serializer.startTag(EMPTY_STRING, PROJECT);
		}
		catch (Exception e){
	    	Log.e("Parser","An error occured in toXml 1" + e.getMessage());
	    	e.printStackTrace();
		}
		
		boolean stageRead = false;
		
		for (int j=0; j<spriteBrickList.size(); j++) {
			ArrayList<HashMap<String, String>> sprite = spriteBrickList.get(j);
		    try {
		    	if (!stageRead) //workaround so that stage always is the first element in xml file
	    		{	
		    		//TODO is stage a sprite?
	    			serializer.startTag(EMPTY_STRING, STAGE);
	    			serializer.attribute(EMPTY_STRING, NAME, spriteNameList.get(j));
	    		}
		    	else
		    	{
		    		serializer.startTag(EMPTY_STRING, OBJECT);
		    	    serializer.attribute(EMPTY_STRING, NAME, spriteNameList.get(j));
		    	}
		    	
		    	for (int i=0; i<sprite.size(); i++) {
		    		HashMap<String,String> brick = sprite.get(i);
					switch (Integer.parseInt(brick.get(BrickDefine.BRICK_TYPE))){ //TODO nicht bei jedem durchlauf neue elemente erzeugen sonder nur clonen
					case BrickDefine.SET_BACKGROUND:
						serializer.startTag(EMPTY_STRING, COMMAND);
						serializer.attribute(EMPTY_STRING, ID, Integer.toString(BrickDefine.SET_BACKGROUND));
						serializer.startTag(EMPTY_STRING, IMAGE);
						serializer.attribute(EMPTY_STRING, PATH, brick.get(BrickDefine.BRICK_VALUE));
						serializer.attribute(EMPTY_STRING, PATH_THUMB, brick.get(BrickDefine.BRICK_VALUE_1));
						serializer.endTag(EMPTY_STRING, IMAGE);
						serializer.endTag(EMPTY_STRING, COMMAND);
						break;
					case BrickDefine.PLAY_SOUND:
						serializer.startTag(EMPTY_STRING, COMMAND);
						serializer.attribute(EMPTY_STRING, ID, Integer.toString(BrickDefine.PLAY_SOUND));
						serializer.startTag(EMPTY_STRING, SOUND);
						
						serializer.attribute(EMPTY_STRING, PATH, brick.get(BrickDefine.BRICK_VALUE));
						serializer.attribute(EMPTY_STRING, FILE_NAME, brick.get(BrickDefine.BRICK_NAME));
						serializer.endTag(EMPTY_STRING, SOUND);
						serializer.endTag(EMPTY_STRING, COMMAND);
						break;
					case BrickDefine.WAIT:
						serializer.startTag(EMPTY_STRING, COMMAND);
						serializer.attribute(EMPTY_STRING, ID, Integer.toString(BrickDefine.WAIT));
						serializer.text(brick.get(BrickDefine.BRICK_VALUE));
						serializer.endTag(EMPTY_STRING, COMMAND);
						break;
					case BrickDefine.HIDE:
						serializer.startTag(EMPTY_STRING, COMMAND);
						serializer.attribute(EMPTY_STRING, ID, Integer.toString(BrickDefine.HIDE));
						serializer.endTag(EMPTY_STRING, COMMAND);
						break;
					case BrickDefine.SHOW:
						serializer.startTag(EMPTY_STRING, COMMAND);
						serializer.attribute(EMPTY_STRING, ID, Integer.toString(BrickDefine.SHOW));
						serializer.endTag(EMPTY_STRING, COMMAND);
						break;
					case BrickDefine.SET_COSTUME:
						serializer.startTag(EMPTY_STRING, COMMAND);
						serializer.attribute(EMPTY_STRING, ID, Integer.toString(BrickDefine.SET_COSTUME));
						
						serializer.startTag(EMPTY_STRING, IMAGE);
						serializer.attribute(EMPTY_STRING, PATH, brick.get(BrickDefine.BRICK_VALUE));
						serializer.attribute(EMPTY_STRING, PATH_THUMB, brick.get(BrickDefine.BRICK_VALUE_1));
						serializer.endTag(EMPTY_STRING, IMAGE);
						serializer.endTag(EMPTY_STRING, COMMAND);
						break;
					case BrickDefine.GO_TO:
						serializer.startTag(EMPTY_STRING, COMMAND);
						serializer.attribute(EMPTY_STRING, ID, Integer.toString(BrickDefine.GO_TO));
						serializer.startTag(EMPTY_STRING, X);
						serializer.text(brick.get(BrickDefine.BRICK_VALUE));
						serializer.endTag(EMPTY_STRING, X);
						serializer.startTag(EMPTY_STRING, Y);
						serializer.text(brick.get(BrickDefine.BRICK_VALUE_1));
						serializer.endTag(EMPTY_STRING, Y);
						serializer.endTag(EMPTY_STRING, COMMAND);
						break;
					
					}
		    	}
		    	if (!stageRead) {
		    		//Log.d("TEST", "in not stage");
		    		serializer.endTag(EMPTY_STRING, STAGE);
		    		stageRead = true;
		    	}
		    	else {
		    		//Log.d("TEST", "in end tag");
		    		serializer.endTag(EMPTY_STRING, OBJECT);
		    	}
			}
		    catch (Exception e){
		    	Log.e("Parser","An error occured in toXml 2 "+ e.getMessage());
		    	e.printStackTrace();
		    }
		}

		try{ 
			serializer.endTag(EMPTY_STRING, PROJECT);
			serializer.endDocument();
		}
		catch (Exception e){
			Log.e("Parser","An error occured in toXml 3"+ e.getMessage());
	    	e.printStackTrace();
		}
	    
	    return writer.toString();
		

	}
	
	
	private HashMap<String, String> getBrickMap(String id, String value, String value1, int type) {
		return getBrickMap(id, EMPTY_STRING, value, value1, type);
	}
	
	private HashMap<String, String> getBrickMap(String id, String name, String value, String value1, int type) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(BrickDefine.BRICK_ID, id);
	    map.put(BrickDefine.BRICK_TYPE, String.valueOf(type));
	    map.put(BrickDefine.BRICK_NAME, name);
	    map.put(BrickDefine.BRICK_VALUE, value);
	    map.put(BrickDefine.BRICK_VALUE_1, value1);
		
		return map;
	}
		
}
