/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.SpeakAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.utils.Reflection;

public class SpeakActionTest extends AndroidTestCase {

	private Sprite sprite;
	private static final String SPEAK = "hello world!";
	private Formula text;
	private Formula text2;
	private Formula textString;

	@Override
	protected void setUp() throws Exception {
		sprite = new Sprite("testSprite");
		text = new Formula(666);
		text2 = new Formula(888.88);
		textString = new Formula(SPEAK);
		super.setUp();
	}

	public void testSpeak() {

		SpeakBrick speakBrick = new SpeakBrick(text);
		SpeakAction action = ExtendedActions.speak(sprite, text);
		Formula textAfterExecution = (Formula) Reflection.getPrivateField(action, "text");

		assertEquals("Text is not updated after SpeakBrick executed", text, speakBrick
				.getFormulaWithBrickField(Brick.BrickField.SPEAK));
		assertEquals("Text is not updated after SpeakBrick executed", text, textAfterExecution);

		speakBrick = new SpeakBrick(text2);
		action = ExtendedActions.speak(sprite, text);
		textAfterExecution = (Formula) Reflection.getPrivateField(action, "text");

		assertEquals("Text is not updated after SpeakBrick executed", text2, speakBrick
				.getFormulaWithBrickField(Brick.BrickField.SPEAK));
		assertEquals("Text is not updated after SpeakBrick executed", text, textAfterExecution);
	}

	public void testNullSprite() {
		SpeakBrick speakBrick = new SpeakBrick(text);
		SpeakAction action = ExtendedActions.speak(sprite, text);

		try {
			action.act(1.0f);
			fail("Execution of ShowBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
		assertEquals("Stored wrong text in speak brick", text, speakBrick
				.getFormulaWithBrickField(Brick.BrickField.SPEAK));
	}

	public void testRequirements() {
		SpeakBrick speakBrick = new SpeakBrick(new Formula(""));
		assertEquals("Wrong required brick resources", Brick.TEXT_TO_SPEECH, speakBrick.getRequiredResources());
	}

	public void testBrickWithStringFormula() {
		SpeakBrick speakBrick = new SpeakBrick(textString);
		SpeakAction action = ExtendedActions.speak(sprite, textString);
		Reflection.invokeMethod(action, "begin");

		assertEquals("Text is not updated after SpeakBrick executed", textString, speakBrick
				.getFormulaWithBrickField(Brick.BrickField.SPEAK));
		assertEquals("Text is not updated after SpeakBrick executed", SPEAK,
				String.valueOf(Reflection.getPrivateField(action, "interpretedText")));
	}

	public void testNullFormula() {
		SpeakAction action = ExtendedActions.speak(sprite, (Formula) null);
		Reflection.invokeMethod(action, "begin");

		assertEquals("Text is not updated after SpeakBrick executed", "",
				String.valueOf(Reflection.getPrivateField(action, "interpretedText")));
	}

	public void testNotANumberFormula() {
		SpeakAction action = ExtendedActions.speak(sprite, new Formula(Double.NaN));
		Reflection.invokeMethod(action, "begin");

		assertEquals("Text is not updated after SpeakBrick executed", "",
				String.valueOf(Reflection.getPrivateField(action, "interpretedText")));
	}
}
