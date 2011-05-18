/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.test.content.script;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;

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
		comeToFrontBrick = new ComeToFrontBrick(sprite);
	};

	public void testAddBricks() {
		Sprite testSprite = new Sprite("sprite");
		Script script = new StartScript("test", testSprite);
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
		Sprite testSprite = new Sprite("sprite");
		Script script = new StartScript("test", testSprite);
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
		Sprite testSprite = new Sprite("sprite");
		Script script = new StartScript("test", testSprite);
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
		Sprite testSprite = new Sprite("sprite");
		Script script = new StartScript("test", testSprite);
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
		Sprite testSprite = new Sprite("sprite");
		Script script = new StartScript("test", testSprite);
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
		Sprite testSprite = new Sprite("sprite");
		Script script = new StartScript("test", testSprite);
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
