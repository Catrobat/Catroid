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

public class SayBrickTest extends AndroidTestCase {

	//	public void testNormalBehavior() {
	//		Sprite sprite = new Sprite("testSprite");
	//		SayBrick sayBrick = new SayBrick(sprite);
	//
	//		SpeechBubble speechBubble = (SpeechBubble) TestUtils.getPrivateField("speechBubble", sprite, false);
	//		int defaultSpeechBubblePictureID = (Integer) TestUtils.getPrivateField("speechBubblePictureID", speechBubble,
	//				false);
	//		int defaultSpeechBubblePictureInvID = (Integer) TestUtils.getPrivateField("speechBubblePictureInvID",
	//				speechBubble, false);
	//
	//		float zeroFloatValue = 0;
	//		float speechBubblePicHeight = (Float) TestUtils.getPrivateField("speechBubblePicHeight", speechBubble, false);
	//		float speechBubblePicWidth = (Float) TestUtils.getPrivateField("speechBubblePicWidth", speechBubble, false);
	//		String brickText = (String) TestUtils.getPrivateField("text", sayBrick, false);
	//		String speechBubbleText = (String) TestUtils.getPrivateField("speechBubbleText", speechBubble, false);
	//		Sprite brickSprite = (Sprite) TestUtils.getPrivateField("sprite", sayBrick, false);
	//
	//		assertEquals("Incorrect default speechBubblePicHeight", zeroFloatValue, speechBubblePicHeight);
	//		assertEquals("Incorrect default speechBubblePicWidth", zeroFloatValue, speechBubblePicWidth);
	//		assertEquals("Incorrect default brickText", "", brickText);
	//		assertEquals("Incorrect default speechBubbleText", "", speechBubbleText);
	//		assertEquals("Incorrect sprite in Brick", sprite, brickSprite);
	//		assertEquals("speechBubblePictureID not correct set", 0, defaultSpeechBubblePictureID);
	//		assertEquals("speechBubblePictureInvID not correct set", 0, defaultSpeechBubblePictureInvID);
	//	}
	//
	//	public void testNullSprite() {
	//		SayBrick sayBrick = new SayBrick(null);
	//		try {
	//			sayBrick.execute();
	//			fail("Execution of SayBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
	//		} catch (NullPointerException e) {
	//		}
	//	}
	//
	//	public void testExecuteWithoutText() {
	//		Sprite sprite = new Sprite("testSprite");
	//		SayBrick sayBrick = new SayBrick(sprite);
	//
	//		SpeechBubble speechBubble = (SpeechBubble) TestUtils.getPrivateField("speechBubble", sprite, false);
	//		int defaultSpeechBubblePictureID = (Integer) TestUtils.getPrivateField("speechBubblePictureID", speechBubble,
	//				false);
	//		int defaultSpeechBubblePictureInvID = (Integer) TestUtils.getPrivateField("speechBubblePictureInvID",
	//				speechBubble, false);
	//
	//		sayBrick.execute();
	//
	//		float zeroFloatValue = 0;
	//		float speechBubblePicHeight = (Float) TestUtils.getPrivateField("speechBubblePicHeight", speechBubble, false);
	//		float speechBubblePicWidth = (Float) TestUtils.getPrivateField("speechBubblePicWidth", speechBubble, false);
	//		String brickText = (String) TestUtils.getPrivateField("text", sayBrick, false);
	//		String speechBubbleText = (String) TestUtils.getPrivateField("speechBubbleText", speechBubble, false);
	//		Sprite brickSprite = (Sprite) TestUtils.getPrivateField("sprite", sayBrick, false);
	//		int speechBubblePictureID = (Integer) TestUtils.getPrivateField("speechBubblePictureID", speechBubble, false);
	//		int speechBubblePictureInvID = (Integer) TestUtils.getPrivateField("speechBubblePictureInvID", speechBubble,
	//				false);
	//
	//		assertEquals("Incorrect default speechBubblePicHeight", zeroFloatValue, speechBubblePicHeight);
	//		assertEquals("Incorrect default speechBubblePicWidth", zeroFloatValue, speechBubblePicWidth);
	//		assertEquals("Incorrect default brickText", "", brickText);
	//		assertEquals("Incorrect default speechBubbleText", "", speechBubbleText);
	//		assertEquals("Incorrect sprite in Brick", sprite, brickSprite);
	//		assertFalse("speechBubblePictureID not correct set",
	//				new Integer(defaultSpeechBubblePictureID).equals(speechBubblePictureID));
	//		assertFalse("speechBubblePictureInvID not correct set",
	//				new Integer(defaultSpeechBubblePictureInvID).equals(speechBubblePictureInvID));
	//
	//	}
	//
	//	public void testExecuteWithText() {
	//		Sprite sprite = new Sprite("testSprite");
	//		String inputText = "test123";
	//		SayBrick sayBrick = new SayBrick(sprite, inputText);
	//
	//		SpeechBubble speechBubble = (SpeechBubble) TestUtils.getPrivateField("speechBubble", sprite, false);
	//		int defaultSpeechBubblePictureID = (Integer) TestUtils.getPrivateField("speechBubblePictureID", speechBubble,
	//				false);
	//		int defaultSpeechBubblePictureInvID = (Integer) TestUtils.getPrivateField("speechBubblePictureInvID",
	//				speechBubble, false);
	//		sayBrick.execute();
	//
	//		float speechBubblePicHeight = (Float) TestUtils.getPrivateField("speechBubblePicHeight", speechBubble, false);
	//		float speechBubblePicWidth = (Float) TestUtils.getPrivateField("speechBubblePicWidth", speechBubble, false);
	//		String brickText = (String) TestUtils.getPrivateField("text", sayBrick, false);
	//		String speechBubbleText = (String) TestUtils.getPrivateField("speechBubbleText", speechBubble, false);
	//		Sprite brickSprite = (Sprite) TestUtils.getPrivateField("sprite", sayBrick, false);
	//		float zeroFloatValue = 0;
	//		int speechBubblePictureID = (Integer) TestUtils.getPrivateField("speechBubblePictureID", speechBubble, false);
	//		int speechBubblePictureInvID = (Integer) TestUtils.getPrivateField("speechBubblePictureInvID", speechBubble,
	//				false);
	//
	//		assertEquals("Incorrect default speechBubblePicHeight", zeroFloatValue, speechBubblePicHeight);
	//		assertEquals("Incorrect default speechBubblePicWidth", zeroFloatValue, speechBubblePicWidth);
	//		assertEquals("Incorrect default brickText", inputText, brickText);
	//		assertEquals("Incorrect default speechBubbleText", inputText, speechBubbleText);
	//		assertEquals("Incorrect sprite in Brick", sprite, brickSprite);
	//		assertFalse("speechBubblePictureID not correct set",
	//				new Integer(defaultSpeechBubblePictureID).equals(speechBubblePictureID));
	//		assertFalse("speechBubblePictureInvID not correct set",
	//				new Integer(defaultSpeechBubblePictureInvID).equals(speechBubblePictureInvID));
	//
	//	}

}
