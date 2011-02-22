package at.tugraz.ist.catroid.test.content.script;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.gui.Brick;
import at.tugraz.ist.catroid.content.brick.gui.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.gui.HideBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.gui.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.gui.ShowBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class ScriptTest extends AndroidTestCase {
	private Sprite sprite;
	private HideBrick hideBrick;
	private ShowBrick showBrick;
	private PlaceAtBrick placeAtBrick;
	private ScaleCostumeBrick scaleCostumeBrick;
	private ComeToFrontBrick comeToFrontBrick;
	private ArrayList<Brick> brickList;
	
	//pause/resume and Brick.execute functionality tested in WaitBrickTest.java 

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		hideBrick = new HideBrick(sprite);
		showBrick = new ShowBrick(sprite);
		placeAtBrick = new PlaceAtBrick(sprite, 0, 0);
		scaleCostumeBrick = new ScaleCostumeBrick(sprite, 0);
		comeToFrontBrick = new ComeToFrontBrick(sprite, null);
	};

	public void testAddBricks() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);

		brickList = script.getBrickList();

		assertEquals("Wrong size of brick list", 3, brickList.size());
		assertEquals("hideBrick is not at index 0", 0,
				brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1,
				brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2,
				brickList.indexOf(placeAtBrick));
	}

	public void testMoveTopBrickDown() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(hideBrick, 1);

		brickList = script.getBrickList();

		assertEquals("hideBrick is not at index 1", 1,
				brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 0", 0,
				brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2,
				brickList.indexOf(placeAtBrick));
	}

	public void testMoveTopBrickUp() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(hideBrick, -1);

		brickList = script.getBrickList();

		assertEquals("hideBrick was moved up even though it was the first brick in the list", 0,
				brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1,
				brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2,
				brickList.indexOf(placeAtBrick));
	}

	public void testMoveBottomBrickUp() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(placeAtBrick, -1);

		brickList = script.getBrickList();

		assertEquals("hideBrick is not at index 0", 0,
				brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 2", 2,
				brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 1", 1,
				brickList.indexOf(placeAtBrick));
	}

	public void testMoveBottomBrickDown() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(placeAtBrick, 1);

		brickList = script.getBrickList();

		assertEquals("hideBrick is not at index 0", 0,
				brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1,
				brickList.indexOf(showBrick));
		assertEquals("placeAtBrick was moved down even though it was the last brick in the list", 2,
				brickList.indexOf(placeAtBrick));
	}

	public void testMoveBrick() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.addBrick(scaleCostumeBrick);
		script.addBrick(comeToFrontBrick);
		script.moveBrickBySteps(scaleCostumeBrick, -2);

		brickList = script.getBrickList();

		assertEquals("hideBrick is not at index 0", 0,
				brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 2", 2,
				brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 3", 3,
				brickList.indexOf(placeAtBrick));
		assertEquals("scaleCostumeBrick is not at index 1", 1,
				brickList.indexOf(scaleCostumeBrick));
		assertEquals("comeToFrontBrick is not at index 4", 4,
				brickList.indexOf(comeToFrontBrick));
	}

}
