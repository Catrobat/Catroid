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
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.test.utils.TestUtils;

import android.test.AndroidTestCase;
import android.view.View;

public class ComeToFrontActionTest extends AndroidTestCase {

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject("testProject");
		super.tearDown();
	}

	public void testComeToFront() {
		Project project = new Project(getContext(), "testProject");

		Sprite bottomSprite = new Sprite("catroid");
		assertEquals("Unexpected initial z position of bottomSprite", 0, bottomSprite.look.zPosition);

		Sprite topSprite = new Sprite("scratch");
		assertEquals("Unexpected initial z position of topSprite", 0, topSprite.look.zPosition);

		topSprite.look.zPosition = 2;
		assertEquals("topSprite z position should now be 2", 2, topSprite.look.zPosition);
		project.addSprite(bottomSprite);
		project.addSprite(topSprite);
		ProjectManager.getInstance().setProject(project);

		ComeToFrontAction action = ExtendedActions.comeToFront(bottomSprite);
		bottomSprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("bottomSprite z position should now be 3", bottomSprite.look.zPosition, 3);
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

		Sprite sprite = new Sprite("testSprite");
		sprite.look.zPosition = Integer.MAX_VALUE;

		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);

		ComeToFrontAction action = ExtendedActions.comeToFront(sprite);
		sprite.look.addAction(action);
		action.act(1.0f);

		assertEquals("An Integer overflow occured during ComeToFrontBrick Execution", Integer.MAX_VALUE,
				sprite.look.zPosition);
	}

	public void testGetView() {
		ProjectManager.getInstance().setProject(new Project(getContext(), "testProject"));
		ComeToFrontBrick brick = new ComeToFrontBrick(new Sprite("testSprite"));
		View view = brick.getView(getContext(), 1, null);
		assertNotNull("getView returned null", view);
	}
}
