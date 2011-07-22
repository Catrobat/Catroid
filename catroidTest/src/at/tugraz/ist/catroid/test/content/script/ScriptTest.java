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
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;

public class ScriptTest extends AndroidTestCase {

	private Sprite sprite;
	private HideBrick hideBrick;
	private ShowBrick showBrick;
	private PlaceAtBrick placeAtBrick;
	private ArrayList<Brick> brickList;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		hideBrick = new HideBrick(sprite);
		showBrick = new ShowBrick(sprite);
		placeAtBrick = new PlaceAtBrick(sprite, 0, 0);
	};

	public void testAddBricks() {
		Sprite testSprite = new Sprite("sprite");
		Script script = new StartScript("test", testSprite);
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);

		brickList = script.getBrickList();

		assertEquals("Wrong size of brick list", 3, brickList.size());
		assertEquals("hideBrick is not at index 0", 0, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2, brickList.indexOf(placeAtBrick));
	}
}
