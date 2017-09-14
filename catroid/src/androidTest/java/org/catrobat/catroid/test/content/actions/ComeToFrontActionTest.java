/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.List;

public class ComeToFrontActionTest extends AndroidTestCase {

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject("testProject");
		super.tearDown();
	}

	public void testComeToFront() {
		Project project = new Project(getContext(), "testProject");
		Group parentGroup = new Group();

		Sprite bottomSprite = new SingleSprite("catroid");
		parentGroup.addActor(bottomSprite.look);
		assertEquals("Unexpected initial z position of bottomSprite", 0, bottomSprite.look.getZIndex());

		Sprite middleSprite = new SingleSprite("catroid cat");
		parentGroup.addActor(middleSprite.look);
		assertEquals("Unexpected initial z position of middleSprite", 1, middleSprite.look.getZIndex());

		Sprite topSprite = new SingleSprite("scratch");
		parentGroup.addActor(topSprite.look);
		assertEquals("Unexpected initial z position of topSprite", 2, topSprite.look.getZIndex());

		project.getDefaultScene().addSprite(bottomSprite);
		project.getDefaultScene().addSprite(middleSprite);
		project.getDefaultScene().addSprite(topSprite);
		ProjectManager.getInstance().setProject(project);

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project);

		ActionFactory factory = middleSprite.getActionFactory();
		Action action = factory.createComeToFrontAction(middleSprite);
		bottomSprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("bottomSprite z position should now be 2", middleSprite.look.getZIndex(),
				getZMaxValue(middleSprite));

		Sprite nextSprite = new SingleSprite("dog");
		parentGroup.addActor(nextSprite.look);
		project.getDefaultScene().addSprite(nextSprite);

		assertEquals("Unexpected initial z position of topSprite", 3, nextSprite.look.getZIndex());

		ActionFactory factory2 = middleSprite.getActionFactory();
		Action action2 = factory2.createComeToFrontAction(bottomSprite);
		bottomSprite.look.addAction(action2);
		action2.act(1.0f);
		assertEquals("bottomSprite z position should now be 3", bottomSprite.look.getZIndex(),
				getZMaxValue(bottomSprite));

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project);
	}

	private void checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(Project project) {
		int spriteSize = project.getDefaultScene().getSpriteList().size();
		int actualZIndex;

		List<Sprite> spriteList = project.getDefaultScene().getSpriteList();
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
		Action action = factory.createComeToFrontAction(null);

		try {
			action.act(1.0f);
			fail("Execution of ComeToFrontBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
		}
	}

	public void testBoundaries() {
		Project project = new Project(getContext(), "testProject");
		Group parentGroup = new Group();

		Sprite firstSprite = new SingleSprite("firstSprite");
		parentGroup.addActor(firstSprite.look);
		project.getDefaultScene().addSprite(firstSprite);

		for (int i = 0; i < 10; i++) {
			Sprite sprite = new SingleSprite("testSprite" + i);
			parentGroup.addActor(sprite.look);
			sprite.look.setZIndex(Integer.MAX_VALUE);
			project.getDefaultScene().addSprite(sprite);
		}

		ProjectManager.getInstance().setProject(project);

		ActionFactory factory = firstSprite.getActionFactory();
		Action action = factory.createComeToFrontAction(firstSprite);
		firstSprite.look.addAction(action);
		action.act(1.0f);

		assertEquals("An Integer overflow occured during ComeToFrontBrick Execution", getZMaxValue(firstSprite),
				firstSprite.look.getZIndex());
	}

	private int getZMaxValue(Sprite sprite) {
		return sprite.look.getParent().getChildren().size - 1;
	}
}
