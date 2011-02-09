package at.tugraz.ist.catroid.test.io;


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
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;


public class StorageHandlerTest extends AndroidTestCase {
	public StageActivity stageActivity = new StageActivity();
	public StorageHandler storageHandler = StorageHandler.getInstance(stageActivity);
	
	
	public void testSerializeProject() {
		Project project = new Project("testProject");
		Sprite testSprite = new Sprite("testSprite");
		Sprite otherSprite = new Sprite("otherSprite");
		Sprite anotherSprite = new Sprite("anotherSprite");
		Sprite yetAnotherSprite = new Sprite("yetAnotherSprite");
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

		project.addSprite(otherSprite);
		project.addSprite(yetAnotherSprite);
		project.addSprite(testSprite);
		project.addSprite(anotherSprite);
		

		storageHandler.saveProject(project);
		
		Project loadedProject = storageHandler.loadProject("testProject");

		ArrayList<Sprite> preSpriteList  = (ArrayList<Sprite>) project.getSpriteList();
		ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getSpriteList();
		
		assertEquals("First sprite does not match after deserialization",  preSpriteList.get(0).getName(), postSpriteList.get(0).getName());
		assertEquals("Second sprite does not match after deserialization", preSpriteList.get(1).getName(), postSpriteList.get(1).getName());
		assertEquals("Third sprite does not match after deserialization",  preSpriteList.get(2).getName(), postSpriteList.get(2).getName());
		assertEquals("Fourth sprite does not match after deserialization", preSpriteList.get(3).getName(), postSpriteList.get(3).getName());
		assertEquals("Fifth sprite does not match after deserialization",  preSpriteList.get(4).getName(), postSpriteList.get(4).getName());
		
		
		
	}
}
