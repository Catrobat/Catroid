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
package org.catrobat.catroid.test.content.brick;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;

import android.test.AndroidTestCase;

public class GlideToBrickTest extends AndroidTestCase {

	int xPosition = 100;
	int yPosition = 100;
	int duration = 1000;

	public void testNormalBehavior() throws InterruptedException {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.costume.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.costume.getYPosition());

		GlideToBrick glideToBrick = new GlideToBrick(sprite, xPosition, yPosition, duration);
		glideToBrick.execute();

		Thread.sleep(1100);

		assertEquals("Incorrect sprite x position after GlideToBrick executed", (float) xPosition, sprite.costume
				.getXPosition());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", (float) yPosition, sprite.costume
				.getYPosition());
	}

	public void testNullSprite() {
		GlideToBrick glideToBrick = new GlideToBrick(null, xPosition, yPosition, duration);
		try {
			glideToBrick.execute();
			fail("Execution of GlideToBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		PlaceAtBrick brick = new PlaceAtBrick(sprite, Integer.MAX_VALUE, Integer.MAX_VALUE);
		brick.execute();

		assertEquals("PlaceAtBrick failed to place Sprite at maximum x float value", (float) Integer.MAX_VALUE,
				sprite.costume.getXPosition());
		assertEquals("PlaceAtBrick failed to place Sprite at maximum y float value", (float) Integer.MAX_VALUE,
				sprite.costume.getYPosition());

		brick = new PlaceAtBrick(sprite, Integer.MIN_VALUE, Integer.MIN_VALUE);
		brick.execute();

		assertEquals("PlaceAtBrick failed to place Sprite at minimum x float value", (float) Integer.MIN_VALUE,
				sprite.costume.getXPosition());
		assertEquals("PlaceAtBrick failed to place Sprite at minimum y float value", (float) Integer.MIN_VALUE,
				sprite.costume.getYPosition());
	}

	public void testTime() throws InterruptedException {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript(sprite);
		HideBrick hideBrick = new HideBrick(sprite);
		GlideToBrick glideToBrick = new GlideToBrick(sprite, 0, 0, 1000);
		ShowBrick showBrick = new ShowBrick(sprite);

		script.addBrick(hideBrick);
		script.addBrick(glideToBrick);
		script.addBrick(showBrick);

		sprite.addScript(script);

		sprite.startStartScripts();

		Thread.sleep(250);

		assertFalse("GlideToBrick should not be visible!", sprite.costume.show);

		Thread.sleep(1000);

		assertTrue("GlideToBrick should be visible!", sprite.costume.show);
	}

	public void testPauseResume() throws InterruptedException {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript(sprite);
		HideBrick hideBrick = new HideBrick(sprite);
		GlideToBrick glideToBrick = new GlideToBrick(sprite, 0, 0, 3000);
		ShowBrick showBrick = new ShowBrick(sprite);

		script.addBrick(hideBrick);
		script.addBrick(glideToBrick);
		script.addBrick(showBrick);

		sprite.addScript(script);

		sprite.startStartScripts();

		Thread.sleep(1000);
		assertFalse("Unexpected visibility of test sprite", sprite.costume.show);

		sprite.pause();
		Thread.sleep(200);
		assertFalse("Unexpected visibility of test sprite", sprite.costume.show);

		sprite.resume();
		Thread.sleep(1000);
		assertFalse("Unexpected visibility of testSprite", sprite.costume.show);

		Thread.sleep(1200);
		assertTrue("Unexpected visibility of testSprite", sprite.costume.show);
	}
}
