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
package org.catrobat.catroid.pocketcode.test.content.actions;

import org.catrobat.catroid.pocketcode.content.Sprite;
import org.catrobat.catroid.pocketcode.content.actions.ExtendedActions;
import org.catrobat.catroid.pocketcode.content.actions.GoNStepsBackAction;
import org.catrobat.catroid.pocketcode.formulaeditor.Formula;

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.Group;

public class GoNStepsBackActionTest extends AndroidTestCase {

	private final Formula steps = new Formula(17);

	public void testSteps() {
		Group parentGroup = new Group();
		for (int i = 0; i < 20; i++) {
			Sprite spriteBefore = new Sprite("before" + i);
			parentGroup.addActor(spriteBefore.look);
		}
		Sprite sprite = new Sprite("testSprite");
		parentGroup.addActor(sprite.look);
		assertEquals("Unexpected initial sprite Z position", 20, sprite.look.getZIndex());

		int oldPosition = sprite.look.getZIndex();

		GoNStepsBackAction action = ExtendedActions.goNStepsBack(sprite, steps);
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed",
				(oldPosition - steps.interpretInteger(sprite)), sprite.look.getZIndex());

		oldPosition = sprite.look.getZIndex();

		action = ExtendedActions.goNStepsBack(sprite, new Formula(-steps.interpretInteger(sprite)));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed",
				(oldPosition + steps.interpretInteger(sprite)), sprite.look.getZIndex());
	}

	public void testNullSprite() {
		GoNStepsBackAction action = ExtendedActions.goNStepsBack(null, steps);
		try {
			action.act(1.0f);
			fail("Execution of GoNStepsBackBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundarySteps() {
		Group parentGroup = new Group();

		Sprite background = new Sprite("background");
		parentGroup.addActor(background.look);
		assertEquals("Unexpected initial sprite Z position", 0, background.look.getZIndex());

		Sprite sprite = new Sprite("testSprite");
		parentGroup.addActor(sprite.look);
		assertEquals("Unexpected initial sprite Z position", 1, sprite.look.getZIndex());

		Sprite sprite2 = new Sprite("testSprite2");
		parentGroup.addActor(sprite2.look);
		assertEquals("Unexpected initial sprite Z position", 2, sprite2.look.getZIndex());

		GoNStepsBackAction action = ExtendedActions.goNStepsBack(sprite, new Formula(Integer.MAX_VALUE));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("GoNStepsBackBrick execution failed. Z position should be zero.", 1, sprite.look.getZIndex());
		assertEquals("Unexpected sprite Z position", 2, sprite2.look.getZIndex());

		action = ExtendedActions.goNStepsBack(sprite, new Formula(Integer.MIN_VALUE));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("An unwanted Integer overflow occured during GoNStepsBackBrick execution.", 2,
				sprite.look.getZIndex());
	}

}
