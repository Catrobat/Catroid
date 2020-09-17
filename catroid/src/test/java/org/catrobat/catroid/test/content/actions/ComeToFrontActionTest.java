/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class ComeToFrontActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private String projectName = "testProject";

	@Before
	public void setUp() {
		PowerMockito.mockStatic(GdxNativesLoader.class);
	}

	@Test
	public void testComeToFront() {
		Project project = new Project(MockUtil.mockContextForProject(), projectName);
		Group parentGroup = new Group();

		Sprite bottomSprite = new Sprite("catroid");
		parentGroup.addActor(bottomSprite.look);
		assertEquals(0, bottomSprite.look.getZIndex());

		Sprite middleSprite = new Sprite("catroid cat");
		parentGroup.addActor(middleSprite.look);
		assertEquals(1, middleSprite.look.getZIndex());

		Sprite topSprite = new Sprite("scratch");
		parentGroup.addActor(topSprite.look);
		assertEquals(2, topSprite.look.getZIndex());

		project.getDefaultScene().addSprite(bottomSprite);
		project.getDefaultScene().addSprite(middleSprite);
		project.getDefaultScene().addSprite(topSprite);
		ProjectManager.getInstance().setCurrentProject(project);

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project);

		ActionFactory factory = middleSprite.getActionFactory();
		Action action = factory.createComeToFrontAction(middleSprite);
		action.act(1.0f);
		assertEquals(middleSprite.look.getZIndex(), getZMaxValue(middleSprite));

		Sprite nextSprite = new Sprite("dog");
		parentGroup.addActor(nextSprite.look);
		project.getDefaultScene().addSprite(nextSprite);

		assertEquals(3, nextSprite.look.getZIndex());

		ActionFactory factory2 = middleSprite.getActionFactory();
		Action action2 = factory2.createComeToFrontAction(bottomSprite);
		action2.act(1.0f);
		assertEquals(bottomSprite.look.getZIndex(), getZMaxValue(bottomSprite));

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
			assertTrue(zIndexFound);
		}
	}

	@Test
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createComeToFrontAction(null);

		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}

	@Test
	public void testBoundaries() {
		Project project = new Project(MockUtil.mockContextForProject(), projectName);
		Group parentGroup = new Group();

		Sprite firstSprite = new Sprite("firstSprite");
		parentGroup.addActor(firstSprite.look);
		project.getDefaultScene().addSprite(firstSprite);

		for (int i = 0; i < 10; i++) {
			Sprite sprite = new Sprite("testSprite" + i);
			parentGroup.addActor(sprite.look);
			sprite.look.setZIndex(Integer.MAX_VALUE);
			project.getDefaultScene().addSprite(sprite);
		}

		ProjectManager.getInstance().setCurrentProject(project);

		ActionFactory factory = firstSprite.getActionFactory();
		Action action = factory.createComeToFrontAction(firstSprite);
		action.act(1.0f);

		assertEquals(getZMaxValue(firstSprite), firstSprite.look.getZIndex());
	}

	private int getZMaxValue(Sprite sprite) {
		return sprite.look.getParent().getChildren().size - 1;
	}
}
