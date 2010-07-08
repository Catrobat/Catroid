package com.tugraz.android.app.parser;


import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.util.Xml;

public class Parser {
	private DocumentBuilder builder;
	private Document doc;
	
	final static int CMD_SET_BACKGROUND = 000;
	final static int CMD_SET_SOUND = 100;
	final static int CMD_WAIT = 200;

	public Parser() {}
	
	/**
	 * Parses the project file and returns a list of commands
	 * @param stream the input stream to read out
	 * @return a List of Commands
	 */
	public List parse(InputStream stream){
		List list = null;
		try {
			doc = builder.parse(stream);	
		}
		catch (Exception e) {
			//TODO implement
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
				time = Integer.parseInt(commands.item(i).getNodeValue());
			}
			Command command = new Command(id, path, time);
			list.add(command);
			
		}
		return list;
	}

	/**
	 * Writes the command list to an XML file
	 * @param commandList the command list to save
	 * @param path the path where to save the XML file
	 */
	public void writeToXlm(List commandList, String path) {
		//TODO implement
	}
	
	
}



//InputStream in = getResources().openRawResource(R.raw.myXmlFile);
//DocumentBuilder builder = DocumentBuilderFactory
//  .newInstance().newDocumentBuilder();
//Document doc = builder.parse(in, null);
//NodeList animals = doc.getElementsByTagName("animal");
//for (int i=0;i<animals.getLength();i++) {
//  items.add(((Element)animals.item(i)).getAttribute("species"));
//}
//in.close();