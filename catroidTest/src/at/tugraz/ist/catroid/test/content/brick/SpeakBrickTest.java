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
import at.tugraz.ist.catroid.content.bricks.SpeakBrick;

public class SpeakBrickTest extends AndroidTestCase {

	private static final String TEXT1 = "Hey, I just met you, and this is crazy.";
	private static final String TEXT2 = "But here's my number! So call me maybe?";

	public void testSpeak() {
		Sprite sprite = new Sprite("new sprite");
		SpeakBrick speakBrick = new SpeakBrick(sprite, TEXT1);
		assertEquals("Text is not updated after SpeakBrick executed", TEXT1, speakBrick.getText());
		speakBrick = new SpeakBrick(sprite, TEXT2);
		assertEquals("Text is not updated after SpeakBrick executed", TEXT2, speakBrick.getText());
	}

	public void testNullSprite() {
		SpeakBrick speakBrick = new SpeakBrick(null, TEXT1);
		try {
			speakBrick.execute();
			fail("Execution of ShowBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}
}
