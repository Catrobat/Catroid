package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;

public class WhenBrickTest extends AndroidTestCase {

	public void testWhenBrick() {
		int testPosition = 100;

		Sprite sprite = new Sprite("new sprite");
		WhenScript whenScript = new WhenScript("script", sprite);
		whenScript.setAction(1);
		Brick placeAtBrick = new PlaceAtBrick(sprite, testPosition, testPosition);
		whenScript.addBrick(placeAtBrick);
		sprite.addScript(whenScript);
		sprite.startWhenScripts(whenScript.getAction());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("Simple broadcast failed", testPosition, sprite.getXPosition());
	}

}