package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;

public class MoveNStepsBrickTest extends AndroidTestCase {

	public void testMoveHorizontalForward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		brick.execute();
		assertEquals("Wrong x-position", 10, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 20, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());
	}

	public void testMoveHorizontalBackward() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, -10);

		brick.execute();
		assertEquals("Wrong x-position", -10, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", -20, sprite.getXPosition());
		assertEquals("Wrong y-position", 0, sprite.getYPosition());
	}

	public void testMoveVerticalUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(0);

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", 10, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", 20, sprite.getYPosition());
	}

	public void testMoveVerticalDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(180);

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", -10, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 0, sprite.getXPosition());
		assertEquals("Wrong y-position", -20, sprite.getYPosition());
	}

	public void testMoveDiagonalRightUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(45);

		brick.execute();
		assertEquals("Wrong x-position", 7, sprite.getXPosition());
		assertEquals("Wrong y-position", 7, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 14, sprite.getXPosition());
		assertEquals("Wrong y-position", 14, sprite.getYPosition());
	}

	public void testMoveDiagonalLeftUp() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(-45);

		brick.execute();
		assertEquals("Wrong x-position", -7, sprite.getXPosition());
		assertEquals("Wrong y-position", 7, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", -14, sprite.getXPosition());
		assertEquals("Wrong y-position", 14, sprite.getYPosition());
	}

	public void testMoveDiagonalRightDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(135);

		brick.execute();
		assertEquals("Wrong x-position", 7, sprite.getXPosition());
		assertEquals("Wrong y-position", -7, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", 14, sprite.getXPosition());
		assertEquals("Wrong y-position", -14, sprite.getYPosition());
	}

	public void testMoveDiagonalLeftDown() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(-135);

		brick.execute();
		assertEquals("Wrong x-position", -7, sprite.getXPosition());
		assertEquals("Wrong y-position", -7, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong x-position", -14, sprite.getXPosition());
		assertEquals("Wrong y-position", -14, sprite.getYPosition());
	}

	public void testMoveOther() {
		Sprite sprite = new Sprite("test");
		MoveNStepsBrick brick = new MoveNStepsBrick(sprite, 10);

		sprite.setDirection(80);

		brick.execute();
		assertEquals("Wrong x-position", 10, sprite.getXPosition());
		assertEquals("Wrong y-position", 2, sprite.getYPosition());

		sprite.setDirection(40);
		brick.execute();
		assertEquals("Wrong x-position", 16, sprite.getXPosition());
		assertEquals("Wrong y-position", 10, sprite.getYPosition());

	}

}
