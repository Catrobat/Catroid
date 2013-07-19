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
package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ComeToFrontAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.test.utils.TestUtils;

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.Group;

public class ComeToFrontActionTest extends AndroidTestCase {

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject("testProject");
		super.tearDown();
	}

	public void testComeToFront() {
		Project project = new Project(getContext(), "testProject");
		Group parentGroup = new Group();

		Sprite bottomSprite = new Sprite("catroid");
		parentGroup.addActor(bottomSprite.look);
		assertEquals("Unexpected initial z position of bottomSprite", 0, bottomSprite.look.getZIndex());

		Sprite middleSprite = new Sprite("catroid cat");
		parentGroup.addActor(middleSprite.look);
		assertEquals("Unexpected initial z position of middleSprite", 1, middleSprite.look.getZIndex());

		Sprite topSprite = new Sprite("scratch");
		parentGroup.addActor(topSprite.look);
		assertEquals("Unexpected initial z position of topSprite", 2, topSprite.look.getZIndex());

		middleSprite.look.setZIndex(2);
		assertEquals("topSprite z position should now be 2", 2, middleSprite.look.getZIndex());
		project.addSprite(bottomSprite);
		project.addSprite(middleSprite);
		project.addSprite(topSprite);
		ProjectManager.INSTANCE.setProject(project);

		ComeToFrontAction action = ExtendedActions.comeToFront(bottomSprite);
		bottomSprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("bottomSprite z position should now be 2", bottomSprite.look.getZIndex(),
				getZMaxValue(bottomSprite));
	}

	public void testNullSprite() {
		ComeToFrontAction action = ExtendedActions.comeToFront(null);

		try {
			action.act(1.0f);
			fail("Execution of ComeToFrontBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundaries() {
		Project project = new Project(getContext(), "testProject");
		Group parentGroup = new Group();

		Sprite firstSprite = new Sprite("firstSprite");
		parentGroup.addActor(firstSprite.look);
		project.addSprite(firstSprite);

		for (int i = 0; i < 10; i++) {
			Sprite sprite = new Sprite("testSprite" + i);
			parentGroup.addActor(sprite.look);
			sprite.look.setZIndex(Integer.MAX_VALUE);
			project.addSprite(sprite);
		}

		ProjectManager.INSTANCE.setProject(project);

		ComeToFrontAction action = ExtendedActions.comeToFront(firstSprite);
		firstSprite.look.addAction(action);
		action.act(1.0f);

		assertEquals("An Integer overflow occured during ComeToFrontBrick Execution", getZMaxValue(firstSprite),
				firstSprite.look.getZIndex());
	}

	private int getZMaxValue(Sprite sprite) {
		return sprite.look.getParent().getChildren().size - 1;
	}
}
