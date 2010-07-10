package com.tugraz.android.app.test.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import junit.framework.TestCase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.tugraz.android.app.parser.*;

public class ParserTest extends AndroidTestCase {
	private String testXml =
	"<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"+
	"<stage>"+
	  "<command id=\"0\">"+
	    "<image path=\"bla.jpg\" />"+
	  "</command>"+
	  "<command id=\"200\">"+
	    "5"+
	  "</command>"+
	  "<command id=\"100\">"+
	    "<sound path=\"bla.mp3\" />"+
	  "</command>"+
	"</stage>";
	
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
		List list =null;
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
}
