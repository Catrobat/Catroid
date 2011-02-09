package at.tugraz.ist.catroid.test.content.project;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;

import com.thoughtworks.xstream.XStream;

public class SerializingTest extends AndroidTestCase {

	public void testSerializeProject() throws FileNotFoundException {
		Project project     = new Project("testProject");
		Sprite firstSprite  = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite  = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript   = new Script();
		Script otherScript  = new Script();
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		PlaceAtBrick placeAtBrick 			= new PlaceAtBrick(secondSprite, 0, 0);
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(secondSprite, 0);
		ComeToFrontBrick comeToFrontBrick   = new ComeToFrontBrick(firstSprite, null);

		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(scaleCostumeBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick);

		firstSprite.getScriptList().add(testScript);
		secondSprite.getScriptList().add(otherScript);

		project.addSprite(secondSprite);
		project.addSprite(fourthSprite);
		project.addSprite(firstSprite);
		project.addSprite(thirdSprite);
		
		
		XStream xstream = new XStream();
		xstream.alias("project", Project.class);
		xstream.alias("sprite",  Sprite.class);
		xstream.alias("script",  Script.class);
		xstream.alias("hideBrick", HideBrick.class);
		xstream.alias("showBrick", ShowBrick.class);
		xstream.alias("scaleCostumeBrick", ScaleCostumeBrick.class);
		xstream.alias("comeToFrontBrick",  ComeToFrontBrick.class);
		xstream.alias("placeAtBrick",      PlaceAtBrick.class);
		
		String xml = xstream.toXML(project);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("/sdcard/text.xml"));
			out.write(xml);
			out.close();
		} catch (IOException e) {

		}	
		
		// TODO: replace with Storagehandler?
		File file = new File("/sdcard/text.xml");
	    FileInputStream     fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream     dis = null;
	      
	    String xmlFile = "";

	    try {
	    	fis = new FileInputStream(file);
	    	bis = new BufferedInputStream(fis);
	    	dis = new DataInputStream(bis);

	    	while (dis.available() != 0) {
		        xmlFile += dis.readLine() + "\n";
		    }
	    	
			System.out.println(xmlFile);
			
			fis.close();
			bis.close();
			dis.close();
			
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		
		Project deserializedProject = (Project)xstream.fromXML(xmlFile);
		
		ArrayList<Sprite> preSpriteList  = (ArrayList<Sprite>) project.getSpriteList();
		ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) deserializedProject.getSpriteList();
		
		assertEquals("First sprite does not match after deserialization",  preSpriteList.get(0).getName(), postSpriteList.get(0).getName());
		assertEquals("Second sprite does not match after deserialization", preSpriteList.get(1).getName(), postSpriteList.get(1).getName());
		assertEquals("Third sprite does not match after deserialization",  preSpriteList.get(2).getName(), postSpriteList.get(2).getName());
		assertEquals("Fourth sprite does not match after deserialization", preSpriteList.get(3).getName(), postSpriteList.get(3).getName());
		assertEquals("Fifth sprite does not match after deserialization",  preSpriteList.get(4).getName(), postSpriteList.get(4).getName());

	}

}
