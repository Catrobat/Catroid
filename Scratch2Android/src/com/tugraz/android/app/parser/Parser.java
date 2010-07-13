package com.tugraz.android.app.parser;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class Parser {
	private DocumentBuilder builder;
	private Document doc;
	
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
	 * Parses the project file and returns a list of commands
	 * @param stream the input stream to read out
	 * @return a List of Commands
	 */
	public List parse(InputStream stream){
		List list = new ArrayList();
		try {
			doc = builder.parse(stream);	
		}
		catch (Exception e) {
			Log.e("Parser", "A parser error occured");
			e.printStackTrace();
		}
		NodeList commands = doc.getElementsByTagName("command");
		for (int i=0; i<commands.getLength(); i++) {
			int id = Integer.parseInt(commands.item(i).getAttributes().getNamedItem("id").getNodeValue());
			String path = "";
			int time = 0;
			switch (id){
			case CMD_SET_BACKGROUND:
			case CMD_SET_SOUND:
				path = commands.item(i).getFirstChild().getAttributes().getNamedItem("path").getNodeValue();
				break;
			case CMD_WAIT:
				//if (commands.item(i).getNodeValue() != null)
					time = Integer.parseInt(commands.item(i).getFirstChild().getNodeValue());
			}
			Command command = new Command(id, path, time);
			list.add(command);
			
		}
		return list;
	}

	/**
	 * Writes the command list to an XML file
	 * @param commandList the command list to save
	 */
	public String toXml(List commandList) {
		doc = builder.newDocument(); //TODO eventuell nachher checken ob sich was veraendert hat und nur das aendern
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
	    try {
	    	
	    	serializer.setOutput(writer);
	    	serializer.startDocument("UTF-8", true);
	    	serializer.startTag("", "stage");
	    	for (int i=0; i<commandList.size(); i++) {
				Command command = (Command) commandList.get(i);
				switch (command.commandType){ //TODO nicht bei jedem durchlauf neue elemente erzeugen sonder nur clonen
				case CMD_SET_BACKGROUND:
//					Element bkgCommand = doc.createElement("command");
//					bkgCommand.setAttribute("id", Integer.toString(CMD_SET_BACKGROUND));
//					Element image = doc.createElement("image");
//					image.setAttribute("path", command.path);
//					bkgCommand.appendChild(image);
//					doc.appendChild(bkgCommand);
					serializer.startTag("", "command");
					serializer.attribute("", "id", Integer.toString(CMD_SET_BACKGROUND));
					serializer.startTag("", "image");
					serializer.attribute("", "path", command.path);
					serializer.endTag("", "image");
					serializer.endTag("", "command");
					break;
				case CMD_SET_SOUND:
//					Element soundCommand = doc.createElement("command");
//					soundCommand.setAttribute("id", Integer.toString(CMD_SET_SOUND));
//					Element sound = doc.createElement("sound");
//					sound.setAttribute("path", command.path);
//					soundCommand.appendChild(sound);
//					doc.appendChild(soundCommand);
					serializer.startTag("", "command");
					serializer.attribute("", "id", Integer.toString(CMD_SET_SOUND));
					serializer.startTag("", "sound");
					serializer.attribute("", "path", command.path);
					serializer.endTag("", "sound");
					serializer.endTag("", "command");
					break;
				case CMD_WAIT:
//					Element waitCommand = doc.createElement("command");
//					waitCommand.setAttribute("id", Integer.toString(CMD_WAIT));
//					waitCommand.setNodeValue(Integer.toString(command.time));
//					doc.appendChild(waitCommand);
					serializer.startTag("", "command");
					serializer.attribute("", "id", Integer.toString(CMD_WAIT));
					serializer.text(Integer.toString(command.time));
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
		
}
