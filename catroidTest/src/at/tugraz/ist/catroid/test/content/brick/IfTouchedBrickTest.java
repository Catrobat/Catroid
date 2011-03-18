package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;

public class IfTouchedBrickTest extends AndroidTestCase {

	Script touchScript = new Script();

	public void testIfTouch() {

		Sprite sprite = new Sprite("new sprite");
		assertFalse("Unexpected default value", touchScript.isTouchScript());
		IfTouchedBrick ifTouchedBrick = new IfTouchedBrick(sprite, touchScript);
		ifTouchedBrick.execute();
		assertTrue(
				"The default value for the script is true after IfTouchedBrick execute.",
				touchScript.isTouchScript());
	}

	public void testNullSprite() {
		IfTouchedBrick ifTouchedBrick = new IfTouchedBrick(null, touchScript);

		ifTouchedBrick.execute();
		/*nothing happen because there is no sprite*/
	}

}
