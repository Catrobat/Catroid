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
package at.tugraz.ist.catroid.test.content.sprite;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;

public class SpriteTest extends AndroidTestCase {

	public void testDefaultConstructor() {
		final String spriteName = "new sprite";
		Sprite sprite = new Sprite(spriteName);
		assertEquals("Unexpected Sprite name", spriteName, sprite.getName());
		assertEquals("Unexpected default x position", 0, sprite.getXPosition());
		assertEquals("Unexpected default y position", 0, sprite.getYPosition());
		assertEquals("Unexpected default z position", 0, sprite.getZPosition());
		assertEquals("Unexpected default size", 100.0, sprite.getSize());
		assertTrue("Unexpected default visibility", sprite.isVisible());
		assertNotNull("Unexpected Sprite costume", sprite.getCostume());
		assertEquals("Script list contains items after constructor", 0, sprite.getNumberOfScripts());
		assertNotNull("Costume was not initialized", sprite.getCostume());
	}

	public void testPositionConstructor() {
		final String spriteName = "new sprite";
		final int xPosition = 100;
		final int yPosition = -500;
		Sprite sprite = new Sprite(spriteName);
		sprite.setXYPosition(xPosition, yPosition);
		assertEquals("Unexpected Sprite name", spriteName, sprite.getName());
		assertEquals("Unexpected x position", xPosition, sprite.getXPosition());
		assertEquals("Unexpected y position", yPosition, sprite.getYPosition());
		assertEquals("Unexpected default z position", 0, sprite.getZPosition());
		assertEquals("Unexpected default size", 100.0, sprite.getSize());
		assertTrue("Unexpected default visibility", sprite.isVisible());
		assertNotNull("Unexpected Sprite costume", sprite.getCostume());
		assertEquals("Script list contains items after constructor", 0, sprite.getNumberOfScripts());
		assertNotNull("Costume was not initialized", sprite.getCostume());

		sprite = new Sprite(spriteName);
		sprite.setXYPosition(Integer.MAX_VALUE, Integer.MIN_VALUE);
		assertEquals("Failed to set Sprite X position to maximum Integer value", Integer.MAX_VALUE,
				sprite.getXPosition());
		assertEquals("Failed to set Sprite Y position to minimum Integer value", Integer.MIN_VALUE,
				sprite.getYPosition());
	}

	public void testSetXYPosition() {
		Sprite sprite = new Sprite("new sprite");
		final int xPosition = 5;
		final int yPosition = 8;

		sprite.setXYPosition(xPosition, yPosition);
		assertEquals("Unexpected xPosition", xPosition, sprite.getXPosition());
		assertEquals("Unexpected yPosition", yPosition, sprite.getYPosition());

		sprite.setXYPosition(Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertEquals("Failed to set Sprite X position to maximum Integer value", Integer.MAX_VALUE,
				sprite.getXPosition());
		assertEquals("Failed to set Sprite Y position to minimum Integer value", Integer.MAX_VALUE,
				sprite.getYPosition());
	}

	public void testSetZPosition() {
		Sprite sprite = new Sprite("new sprite");
		final int zPosition = 6;

		sprite.setZPosition(zPosition);
		assertEquals("Unexpected zPosition", zPosition, sprite.getZPosition());

		sprite.setZPosition(Integer.MAX_VALUE);
		assertEquals("Failed to set Sprite Z position to maximum Integer value", Integer.MAX_VALUE,
				sprite.getZPosition());

		sprite.setZPosition(-zPosition);
		assertEquals("Failed to set z position to negative value", -zPosition, sprite.getZPosition());
	}

	public void testAddScript() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript("firstScript", sprite);
		Script secondScript = new StartScript("secondScript", sprite);
		sprite.addScript(firstScript);
		assertEquals("Script list does not contain script after adding", 1, sprite.getNumberOfScripts());

		sprite.addScript(0, secondScript);
		assertEquals("Script list does not contain script after adding", 2, sprite.getNumberOfScripts());

		assertEquals("Script list does not contain script after adding", 1, sprite.getScriptIndex(firstScript));
		assertEquals("Script list does not contain script after adding", 0, sprite.getScriptIndex(secondScript));

		sprite.removeAllScripts();
		assertEquals("Script list could not be cleared", 0, sprite.getNumberOfScripts());
	}

	public void testGetScript() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript("firstScript", sprite);
		Script secondScript = new StartScript("secondScript", sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals("Scripts do not match after retrieving", firstScript, sprite.getScript(0));
		assertEquals("Script doo not match after retrieving", secondScript, sprite.getScript(1));
	}

	public void testRemoveAllScripts() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript("firstScript", sprite);
		Script secondScript = new StartScript("secondScript", sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeAllScripts();

		assertEquals("Script list was not cleared", 0, sprite.getNumberOfScripts());
	}

	public void testRemoveScript() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript("firstScript", sprite);
		Script secondScript = new StartScript("secondScript", sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeScript(firstScript);

		assertEquals("Wrong script list size", 1, sprite.getNumberOfScripts());
		assertEquals("Wrong script remained", secondScript, sprite.getScript(0));

	}

	public void testGetScriptIndex() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript("firstScript", sprite);
		Script secondScript = new StartScript("secondScript", sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals("Indexes do not match", 0, sprite.getScriptIndex(firstScript));
		assertEquals("Indexes do not match", 1, sprite.getScriptIndex(secondScript));
	}

	public void testSetSize() {
		Sprite sprite = new Sprite("new sprite");
		final double size = 2.0;
		sprite.setSize(size);
		assertEquals("Unexpected size", size, sprite.getSize());

		final double hugeSize = 10.0e100;
		sprite.setSize(hugeSize);
		assertEquals("Failed to size sprite to a very large size", hugeSize, sprite.getSize());

		final double tinySize = 10.0e-100;
		sprite.setSize(tinySize);
		assertEquals("Failed to size sprite to a very small size", tinySize, sprite.getSize());
	}

	public void testZeroSize() {
		Sprite sprite = new Sprite("testSprite");

		SetSizeToBrick brick = new SetSizeToBrick(sprite, 0.0);

		try {
			brick.execute();
			fail("Execution of SetSizeToBrick with 0.0 size did not cause a IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testNegativeSize() {
		Sprite sprite = new Sprite("testSprite");

		final double size = -5.0;
		SetSizeToBrick brick = new SetSizeToBrick(sprite, (int) (size * 100));

		try {
			brick.execute();
			fail("Execution of SetSizeToBrick with negative size did not cause a IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testShowAndHide() {
		Sprite sprite = new Sprite("new sprite");
		assertTrue("Unexpected default visibility", sprite.isVisible());
		sprite.hide();
		assertFalse("Sprite still visible after calling hide method", sprite.isVisible());
		sprite.show();
		assertTrue("Sprite not visible after calling show method", sprite.isVisible());
	}

	public void testPauseUnPause() {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript("testScript", testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		ShowBrick showBrick = new ShowBrick(testSprite);

		for (int i = 0; i < 10000; i++) {
			testScript.addBrick(hideBrick);
			testScript.addBrick(showBrick);
		}

		testSprite.addScript(testScript);

		testSprite.startStartScripts();

		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		testSprite.pause();
		assertTrue("Sprite isn't paused", testSprite.isPaused);
		assertTrue("Script isn't paused", testScript.isPaused());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		testSprite.resume();

		assertFalse("Sprite is paused", testSprite.isPaused);
		assertFalse("Script is paused", testScript.isPaused());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("Script hasn't finished", testScript.isFinished());

	}

	public void testSetDirection() {
		Sprite sprite = new Sprite("testSprite");

		sprite.setDirection(90);
		assertEquals("Direction wrong set", 90.0, sprite.getDirection(), 1e-3);

		sprite.setDirection(360);
		assertEquals("Direction wrong set", 0., sprite.getDirection(), 1e-3);

		sprite.setDirection(-360);
		assertEquals("Direction wrong set", 0., sprite.getDirection(), 1e-3);

		sprite.setDirection(540);
		assertEquals("Direction wrong set", 180, sprite.getDirection(), 1e-3);

		sprite.setDirection(-540);
		assertEquals("Direction wrong set", 180, sprite.getDirection(), 1e-3);

		sprite.setDirection(540.2);
		assertEquals("Direction wrong set", -179.8, sprite.getDirection(), 1e-3);

		sprite.setDirection(-540.2);
		assertEquals("Direction wrong set", 179.8, sprite.getDirection(), 1e-3);

		sprite.setDirection(-450.);
		assertEquals("Direction wrong set", -90., sprite.getDirection(), 1e-3);

		sprite.setDirection(198.12);
		assertEquals("Direction wrong set", -161.88, sprite.getDirection(), 1e-3);

		sprite.setDirection(-73.123);
		assertEquals("Direction wrong set", -73.123, sprite.getDirection(), 1e-3);

		sprite.setDirection(-198.12);
		assertEquals("Direction wrong set", 161.88, sprite.getDirection(), 1e-3);

		sprite.setDirection(73.123);
		assertEquals("Direction wrong set", 73.123, sprite.getDirection(), 1e-3);
	}

	public void compareTo() {
		int ZValue = 0;
		int otherZValue = 0;

		Sprite sprite1 = new Sprite("new Sprite");
		Sprite sprite2 = new Sprite("new Sprite");
		Sprite sprite3 = new Sprite("new Sprite");
		Sprite sprite4 = new Sprite("new Sprite");
		sprite1.setZPosition(ZValue);
		sprite2.setZPosition(otherZValue);
		sprite3.setZPosition(Integer.MAX_VALUE);
		sprite4.setZPosition(Integer.MIN_VALUE);

		assertEquals("Sprite1 and Sprite2 is not at the same position.", 0, sprite1.compareTo(sprite2));
		assertEquals("Sprite1 is not behind Sprite2.", Integer.MAX_VALUE, sprite1.compareTo(sprite3));
		assertEquals("Sprite1 is not in front of Sprite2.", Integer.MIN_VALUE, sprite1.compareTo(sprite4));
	}

	public void testSetBrightness() {
		Sprite sprite = new Sprite("new sprite");
		final double brightness = 70;
		sprite.setBrightnessValue(brightness);
		assertEquals("Unexpected brightness", brightness, sprite.getBrightnessValue());

		final double hugeBrightness = 10.0e100;
		sprite.setBrightnessValue(hugeBrightness);
		assertEquals("Failed to set sprite to a very high brightness", hugeBrightness, sprite.getBrightnessValue());

		final double negativeBrightness = -10.0e100;
		sprite.setBrightnessValue(negativeBrightness);
		assertEquals("Failed to set sprite to a negative brightness", negativeBrightness, sprite.getBrightnessValue());
	}

	public void testSetGhostEffect() {
		Sprite sprite = new Sprite("new sprite");
		final double effect = 70;
		sprite.setGhostEffectValue(effect);
		assertEquals("Unexpected effect", effect, sprite.getGhostEffectValue());

		final double hugeEffect = 10.0e100;
		sprite.setGhostEffectValue(hugeEffect);
		assertEquals("Failed to set sprite to a very high transparent", hugeEffect, sprite.getGhostEffectValue());

		final double negativeEffect = -10.0e100;
		try {
			sprite.setGhostEffectValue(negativeEffect);
			assertNotSame("Failed to change negative effect value to positive", negativeEffect,
					sprite.getGhostEffectValue());
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testClearGraphicEffect() {
		Sprite sprite = new Sprite("new sprite");
		final double effect = 70;
		sprite.setGhostEffectValue(effect);
		assertEquals("Unexpected effect", effect, sprite.getGhostEffectValue());

		final double brightness = 70;
		sprite.setBrightnessValue(brightness);
		assertEquals("Unexpected brightness", brightness, sprite.getBrightnessValue());

		sprite.clearGraphicEffect();
		assertEquals("Unexpected brightness", 0.0, sprite.getBrightnessValue());
		assertEquals("Unexpected effect", 0.0, sprite.getGhostEffectValue());
	}
}
