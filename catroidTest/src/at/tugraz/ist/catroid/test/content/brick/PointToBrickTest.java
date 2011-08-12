package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;

public class PointToBrickTest extends AndroidTestCase {

	public void testPointTo() {

		Sprite sprite2 = new Sprite("cat2");
		Script startScript2 = new StartScript("script2", sprite2);
		PlaceAtBrick placeAt2 = new PlaceAtBrick(sprite2, -400, -300);
		SetSizeToBrick size2 = new SetSizeToBrick(sprite2, 20.0);
		startScript2.addBrick(placeAt2);
		startScript2.addBrick(size2);
		sprite2.addScript(startScript2);
		sprite2.startStartScripts();

		Sprite sprite1 = new Sprite("cat1");
		Script startScript1 = new StartScript("script1", sprite1);
		PlaceAtBrick placeAt1 = new PlaceAtBrick(sprite1, 300, 400);
		SetSizeToBrick size1 = new SetSizeToBrick(sprite1, 20.0);
		startScript1.addBrick(placeAt1);
		startScript1.addBrick(size1);
		PointToBrick pointToBrick = new PointToBrick(sprite1, sprite2);
		startScript1.addBrick(pointToBrick);
		sprite1.addScript(startScript1);
		sprite1.startStartScripts();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("Wrong direction", -135.0, sprite1.getDirection(), 1e-3);
	}

}
