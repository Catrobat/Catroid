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
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;

public class GlideToBrickTest extends AndroidTestCase {

	int xPosition = 100;
	int yPosition = 100;
	int duration = 1000;

	public void testNormalBehavior() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite x position", 0f, sprite.costume.getXPosition());
		assertEquals("Unexpected initial sprite y position", 0f, sprite.costume.getYPosition());

		GlideToBrick glideToBrick = new GlideToBrick(sprite, xPosition, yPosition, duration);
		glideToBrick.execute();

		try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals("Incorrect sprite x position after GlideToBrick executed", (float) xPosition,
				sprite.costume.getXPosition());
		assertEquals("Incorrect sprite y position after GlideToBrick executed", (float) yPosition,
				sprite.costume.getYPosition());
	}

	public void testNullSprite() {
		GlideToBrick glideToBrick = new GlideToBrick(null, xPosition, yPosition, duration);
		try {
			glideToBrick.execute();
			fail("Execution of GlideToBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testBoundaryPositions() {
		Sprite sprite = new Sprite("testSprite");

		PlaceAtBrick brick = new PlaceAtBrick(sprite, (int) Float.MAX_VALUE, (int) Float.MAX_VALUE);
		brick.execute();

		assertEquals("PlaceAtBrick failed to place Sprite at maximum x float value", (int) Float.MAX_VALUE,
				(int) sprite.costume.getXPosition());
		assertEquals("PlaceAtBrick failed to place Sprite at maximum y float value", (int) Float.MAX_VALUE,
				(int) sprite.costume.getYPosition());

		brick = new PlaceAtBrick(sprite, (int) Float.MIN_VALUE, (int) Float.MIN_VALUE);
		brick.execute();

		assertEquals("PlaceAtBrick failed to place Sprite at minimum x float value", (int) Float.MIN_VALUE,
				(int) sprite.costume.getXPosition());
		assertEquals("PlaceAtBrick failed to place Sprite at minimum y float value", (int) Float.MIN_VALUE,
				(int) sprite.costume.getYPosition());
	}

	public void testTime() {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript("testScript", sprite);
		HideBrick hideBrick = new HideBrick(sprite);
		GlideToBrick glideToBrick = new GlideToBrick(sprite, 0, 0, 1000);
		ShowBrick showBrick = new ShowBrick(sprite);

		script.addBrick(hideBrick);
		script.addBrick(glideToBrick);
		script.addBrick(showBrick);

		sprite.addScript(script);

		sprite.startStartScripts();

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse("GlideToBrick should not be visible!", sprite.costume.show);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue("GlideToBrick should be visible!", sprite.costume.show);
	}

	public void testPauseResume() {
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript("testScript", sprite);
		HideBrick hideBrick = new HideBrick(sprite);
		GlideToBrick glideToBrick = new GlideToBrick(sprite, 0, 0, 3000);
		ShowBrick showBrick = new ShowBrick(sprite);

		script.addBrick(hideBrick);
		script.addBrick(glideToBrick);
		script.addBrick(showBrick);

		sprite.addScript(script);

		sprite.startStartScripts();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse("Unexpected visibility of test sprite", sprite.costume.show);

		sprite.pause();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse("Unexpected visibility of test sprite", sprite.costume.show);

		sprite.resume();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertFalse("Unexpected visibility of testSprite", sprite.costume.show);

		try {
			Thread.sleep(1200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("Unexpected visibility of testSprite", sprite.costume.show);
	}
}
