/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.content.ActionFactory;

import java.util.List;

public class GoNStepsBackActionTest extends AndroidTestCase {

	private final Formula steps = new Formula(17);

	public void testSteps() {
		Project project = new Project(getContext(), "testProject");
		Group parentGroup = new Group();

		for (int i = 0; i < 20; i++) {
			Sprite spriteBefore = new Sprite("before" + i);
			parentGroup.addActor(spriteBefore.look);
			project.addSprite(spriteBefore);
		}
		Sprite sprite = new Sprite("testSprite");
		parentGroup.addActor(sprite.look);
		project.addSprite(sprite);
		assertEquals("Unexpected initial sprite Z position", 20, sprite.look.getZIndex());

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getSpriteList());

		int oldPosition = sprite.look.getZIndex();

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createGoNStepsBackAction(sprite, steps);
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed",
				(oldPosition - steps.interpretInteger(sprite)), sprite.look.getZIndex());

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getSpriteList());
		oldPosition = sprite.look.getZIndex();

		action = factory.createGoNStepsBackAction(sprite, new Formula(-steps.interpretInteger(sprite)));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed",
				(oldPosition + steps.interpretInteger(sprite)), sprite.look.getZIndex());
		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getSpriteList());
	}

	private void checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(List<Sprite> spriteList) {
		int spriteSize = spriteList.size();
		int actualZIndex;

		boolean zIndexFound;

		for (int zIndex = 0; zIndex < spriteSize - 1; zIndex++) {
			zIndexFound = false;
			for (int i = 0; i < spriteSize; i++) {
				actualZIndex = spriteList.get(i).look.getZIndex();
				if (actualZIndex == zIndex) {
					zIndexFound = true;
					break;

				}

			}
			assertTrue("z-indexing not correct. z-index have to be from 0 to n-1 each value only once", zIndexFound);
		}


	}


	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGoNStepsBackAction(null, steps);
		try {
			action.act(1.0f);
			fail("Execution of GoNStepsBackBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown successful", true);
		}
	}

	public void testBoundarySteps() {
		Project project = new Project(getContext(), "testProject");
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

		project.addSprite(sprite);
		project.addSprite(sprite2);
		ProjectManager.getInstance().setProject(project);


		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createGoNStepsBackAction(sprite, new Formula(Integer.MAX_VALUE));

		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("GoNStepsBackBrick execution failed. Z position should be zero.", 1, sprite.look.getZIndex());
		assertEquals("Unexpected sprite Z position", 2, sprite2.look.getZIndex());

		action = factory.createGoNStepsBackAction(sprite, new Formula(Integer.MIN_VALUE));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("An unwanted Integer overflow occured during GoNStepsBackBrick execution.", 2,
				sprite.look.getZIndex());
	}

}
