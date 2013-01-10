/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.script;

import java.util.ArrayList;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;

import android.test.AndroidTestCase;

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
		Script script = new StartScript(testSprite);
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
