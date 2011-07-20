package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;

public class PointInDirectionBrickTest extends AndroidTestCase {

	public void testPointRight() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 90);

		brick.execute();
		assertEquals("Wrong direction", 90, sprite.getDirection(), 1e-3);
	}

	public void testPointLeft() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, -90);

		brick.execute();
		assertEquals("Wrong direction", -90, sprite.getDirection(), 1e-3);
	}

	public void testPointUp() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 0);

		brick.execute();
		assertEquals("Wrong direction", 0, sprite.getDirection(), 1e-3);
	}

	public void testPointDown() {
		Sprite sprite = new Sprite("test");
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 180);

		brick.execute();
		assertEquals("Wrong direction", 180, sprite.getDirection(), 1e-3);
	}

	public void testRotateAndPoint() {
		Sprite sprite = new Sprite("test");
		sprite.setDirection(-42);
		PointInDirectionBrick brick = new PointInDirectionBrick(sprite, 90);

		brick.execute();
		assertEquals("Wrong direction", 90, sprite.getDirection(), 1e-3);
	}

}
