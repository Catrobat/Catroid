/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;

public class GoNStepsBackBrickTest extends AndroidTestCase {

	private final int steps = 17;

	public void testSteps() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite Z position", 0, sprite.costume.zPosition);

		int oldPosition = sprite.costume.zPosition;

		GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(sprite, steps);
		goNStepsBackBrick.execute();
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed", (oldPosition - steps),
				sprite.costume.zPosition);

		oldPosition = sprite.costume.zPosition;

		goNStepsBackBrick = new GoNStepsBackBrick(sprite, -steps);
		goNStepsBackBrick.execute();
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed", (oldPosition + steps),
				sprite.costume.zPosition);
	}

	public void testNullSprite() {
		GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(null, steps);

		try {
			goNStepsBackBrick.execute();
			fail("Execution of GoNStepsBackBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundarySteps() {
		Sprite sprite = new Sprite("testSprite");

		int oldPosition = sprite.costume.zPosition;

		GoNStepsBackBrick goNStepsBackBrick = new GoNStepsBackBrick(sprite, Integer.MAX_VALUE);
		goNStepsBackBrick.execute();
		assertEquals("GoNStepsBackBrick execution failed. Wrong Z position.", (oldPosition - Integer.MAX_VALUE),
				sprite.costume.zPosition);

		goNStepsBackBrick = new GoNStepsBackBrick(sprite, Integer.MAX_VALUE);
		goNStepsBackBrick.execute();
		goNStepsBackBrick.execute();
		assertEquals("An unwanted Integer underflow occured during GoNStepsBackBrick execution.", Integer.MIN_VALUE,
				sprite.costume.zPosition);

		goNStepsBackBrick = new GoNStepsBackBrick(sprite, Integer.MIN_VALUE);
		goNStepsBackBrick.execute();
		goNStepsBackBrick.execute();
		assertEquals("An unwanted Integer overflow occured during GoNStepsBackBrick execution.", Integer.MAX_VALUE,
				sprite.costume.zPosition);
	}

}
