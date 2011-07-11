package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;

public class IfOnEdgeBounceBrickTest extends AndroidTestCase {

	private static final int BOUNCE_LEFT_POS = -(Consts.MAX_REL_COORDINATES + 50);
	private static final int BOUNCE_RIGHT_POS = Consts.MAX_REL_COORDINATES + 50;
	private static final int BOUNCE_DOWN_POS = -(Consts.MAX_REL_COORDINATES + 50);
	private static final int BOUNCE_UP_POS = Consts.MAX_REL_COORDINATES + 50;

	public void testNoBounce() {

		Sprite sprite = new Sprite("testSprite");

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);

		brick.execute();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 0.0, sprite.getDirection(), 1e-3);

	}

	public void testBounceStraight() {

		Sprite sprite = new Sprite("testSprite");
		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);

		sprite.setDirection(0);
		sprite.setXYPosition(BOUNCE_RIGHT_POS, 0);

		brick.execute();
		assertEquals("Wrong X-Position!", Consts.MAX_REL_COORDINATES, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 180.0, sprite.getDirection(), 1e-3);

		sprite.setDirection(180);
		sprite.setXYPosition(BOUNCE_LEFT_POS, 0);

		brick.execute();
		assertEquals("Wrong X-Position!", -Consts.MAX_REL_COORDINATES, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong direction", 0., sprite.getDirection(), 1e-3);

		sprite.setDirection(90);
		sprite.setXYPosition(0, BOUNCE_UP_POS);

		brick.execute();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", Consts.MAX_REL_COORDINATES, sprite.getYPosition());
		assertEquals("Wrong direction", 270.0, sprite.getDirection(), 1e-3);

		sprite.setDirection(270);
		sprite.setXYPosition(0, BOUNCE_DOWN_POS);

		brick.execute();
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", -Consts.MAX_REL_COORDINATES, sprite.getYPosition());
		assertEquals("Wrong direction", 90., sprite.getDirection(), 1e-3);
	}

	public void testZeroSizeSprite() {

	}

}
