package at.tugraz.ist.catroid.test.content.project;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class SerializingTest extends AndroidTestCase {

	public void testSerializeProject() throws FileNotFoundException {
		Project project = new Project("testProject");
		Sprite testSprite = new Sprite("testSprite");
		Sprite otherSprite = new Sprite("otherSprite");
		Script testScript = new Script();
		Script otherScript = new Script();
		HideBrick hideBrick = new HideBrick(testSprite);
		ShowBrick showBrick = new ShowBrick(testSprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(otherSprite, 0, 0);
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(otherSprite, 0);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(testSprite, null);

		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(scaleCostumeBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick);

		testSprite.getScriptList().add(testScript);
		otherSprite.getScriptList().add(otherScript);

		project.addSprite(testSprite);
		project.addSprite(otherSprite);
		
		XStream xstream = new XStream();
		xstream.alias("project", Project.class);
		xstream.alias("sprite", Sprite.class);
		xstream.alias("script", Script.class);
		xstream.alias("hideBrick", HideBrick.class);
		xstream.alias("showBrick", ShowBrick.class);
		xstream.alias("scaleCostumeBrick", ScaleCostumeBrick.class);
		xstream.alias("comeToFrontBrick", ComeToFrontBrick.class);
		xstream.alias("placeAtBrick", PlaceAtBrick.class);
		
		String xml = xstream.toXML(project);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("/sdcard/text.xml"));
			out.write(xml);
			out.close();
		} catch (IOException e) {

		}

	}

}
