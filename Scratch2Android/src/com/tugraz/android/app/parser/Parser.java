package com.tugraz.android.app.parser;


import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.tugraz.android.app.BrickDefine;

public class Parser {
	private DocumentBuilder builder;
	private Document doc;
	private static int mIdCounter = 0;
	
	final static int CMD_SET_BACKGROUND = 0;
	final static int CMD_SET_SOUND = 100;
	final static int CMD_WAIT = 200;

	public Parser() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the project file and returns an ArrayList with the bricks
	 * @param stream the input stream to read out
	 * @return a ArrayList of of HashMaps representing the bricks
	 */
	public ArrayList<HashMap<String, String>> parse(InputStream stream){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		try {
			doc = builder.parse(stream);	
		}
		catch (Exception e) {
			Log.e("Parser", "A parser error occured");
			e.printStackTrace();
		}
		NodeList bricks = doc.getElementsByTagName("command");
		for (int i=0; i<bricks.getLength(); i++) {
			int brickType = Integer.parseInt(bricks.item(i).getAttributes().getNamedItem("id").getNodeValue());
			String value = "";
			switch (brickType){
			case BrickDefine.SET_BACKGROUND:
			case BrickDefine.PLAY_SOUND:
				value = bricks.item(i).getFirstChild().getAttributes().getNamedItem("path").getNodeValue();
				break;
			case BrickDefine.WAIT:
				//if (commands.item(i).getNodeValue() != null)
					value = bricks.item(i).getFirstChild().getNodeValue();
			}
			HashMap<String, String> map = getBrickMap(value, brickType);
			list.add(map);
			
		}
		return list;
	}

	/**
	 * Writes the brick list to an XML file
	 * @param an ArrayList of HashMaps containing the bricks
	 */
	public String toXml(ArrayList<HashMap<String,String>> brickList) {
		doc = builder.newDocument(); //TODO eventuell nachher checken ob sich was veraendert hat und nur das aendern
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
	    try {
	    	
	    	serializer.setOutput(writer);
	    	serializer.startDocument("UTF-8", true);
	    	serializer.startTag("", "stage");
	    	for (int i=0; i<brickList.size(); i++) {
	    		HashMap<String,String> brick = brickList.get(i);
	    		
				switch (Integer.parseInt(brick.get(BrickDefine.BRICK_TYPE))){ //TODO nicht bei jedem durchlauf neue elemente erzeugen sonder nur clonen
				case BrickDefine.SET_BACKGROUND:
//					Element bkgCommand = doc.createElement("command");
//					bkgCommand.setAttribute("id", Integer.toString(CMD_SET_BACKGROUND));
//					Element image = doc.createElement("image");
//					image.setAttribute("path", command.path);
//					bkgCommand.appendChild(image);
//					doc.appendChild(bkgCommand);
					serializer.startTag("", "command");
					serializer.attribute("", "id", Integer.toString(BrickDefine.SET_BACKGROUND));
					serializer.startTag("", "image");
					serializer.attribute("", "path", brick.get(BrickDefine.BRICK_VALUE));
					serializer.endTag("", "image");
					serializer.endTag("", "command");
					break;
				case BrickDefine.PLAY_SOUND:
//					Element soundCommand = doc.createElement("command");
//					soundCommand.setAttribute("id", Integer.toString(CMD_SET_SOUND));
//					Element sound = doc.createElement("sound");
//					sound.setAttribute("path", command.path);
//					soundCommand.appendChild(sound);
//					doc.appendChild(soundCommand);
					serializer.startTag("", "command");
					serializer.attribute("", "id", Integer.toString(BrickDefine.PLAY_SOUND));
					serializer.startTag("", "sound");
					serializer.attribute("", "path", brick.get(BrickDefine.BRICK_VALUE));
					serializer.endTag("", "sound");
					serializer.endTag("", "command");
					break;
				case BrickDefine.WAIT:
//					Element waitCommand = doc.createElement("command");
//					waitCommand.setAttribute("id", Integer.toString(CMD_WAIT));
//					waitCommand.setNodeValue(Integer.toString(command.time));
//					doc.appendChild(waitCommand);
					serializer.startTag("", "command");
					serializer.attribute("", "id", Integer.toString(BrickDefine.WAIT));
					serializer.text(brick.get(BrickDefine.BRICK_VALUE));
					serializer.endTag("", "command");
					break;
				}	
	    	}
	    	serializer.endTag("", "stage");
	    	serializer.endDocument();
	    	//return writer.toString();
		}
	    catch (Exception e){
	    	Log.e("Parser","An error occured in toXml");
	    	e.printStackTrace();
	    }
	    
	    return writer.toString();
		

	}
	
	private HashMap<String, String> getBrickMap(String value, int type) {
		return getBrickMap("Name", value, type);
	}
	
	private HashMap<String, String> getBrickMap(String name, String value, int type) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(BrickDefine.BRICK_ID, Integer.toString(mIdCounter));
	    map.put(BrickDefine.BRICK_TYPE, Integer.toString(type));
	    map.put(BrickDefine.BRICK_NAME, name);
	    map.put(BrickDefine.BRICK_VALUE, value);
		
		
		return map;
	}
		
}
