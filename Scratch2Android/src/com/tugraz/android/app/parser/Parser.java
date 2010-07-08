package com.tugraz.android.app.parser;


import java.io.InputStream;

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
	
	public void parse(InputStream stream){
		try {
			doc = builder.parse(stream);
			NodeList animals = doc.getElementsByTagName("animals");
		}
		catch (Exception e) {
			//TODO implement
		}
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