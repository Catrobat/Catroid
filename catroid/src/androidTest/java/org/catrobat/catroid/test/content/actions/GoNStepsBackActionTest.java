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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GoNStepsBackActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final int STEPS = 13;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private final Formula steps = new Formula(STEPS);
	private Project project;
	private Sprite background;
	private Sprite sprite;
	private Sprite sprite2;
	private Sprite sprite3;

	@Before
	public void setUp() throws Exception {
		project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		Group parentGroup = new Group();

		background = new SingleSprite("background");
		parentGroup.addActor(background.look);
		sprite = new SingleSprite("penActor");
		parentGroup.addActor(sprite.look);
		project.getDefaultScene().addSprite(sprite);
		sprite2 = new SingleSprite("embroideryActor");
		parentGroup.addActor(sprite2.look);
		project.getDefaultScene().addSprite(sprite2);
		sprite3 = new SingleSprite("testSprite");
		parentGroup.addActor(sprite3.look);
		project.getDefaultScene().addSprite(sprite3);

		ProjectManager.getInstance().setProject(project);
	}

	@Test
	public void testSteps() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		Group parentGroup = new Group();

		for (int i = 0; i < 20; i++) {
			Sprite spriteBefore = new SingleSprite("before" + i);
			parentGroup.addActor(spriteBefore.look);
			project.getDefaultScene().addSprite(spriteBefore);
		}
		Sprite sprite = new SingleSprite("testSprite");
		parentGroup.addActor(sprite.look);
		project.getDefaultScene().addSprite(sprite);
		assertEquals(20, sprite.look.getZIndex());

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getDefaultScene().getSpriteList());

		int oldPosition = sprite.look.getZIndex();

		sprite.getActionFactory().createGoNStepsBackAction(sprite, steps).act(1.0f);
		assertEquals((oldPosition - STEPS), sprite.look.getZIndex());

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getDefaultScene().getSpriteList());
		oldPosition = sprite.look.getZIndex();

		sprite.getActionFactory().createGoNStepsBackAction(sprite, new Formula(-STEPS)).act(1.0f);
		assertEquals((oldPosition + STEPS), sprite.look.getZIndex());
		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getDefaultScene().getSpriteList());
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
			assertTrue(zIndexFound);
		}
	}

	@Test
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGoNStepsBackAction(null, steps);
		exception.expect(NullPointerException.class);
		action.act(1.0f);
	}

	@Test
	public void testBoundarySteps() {
		Group parentGroup = new Group();

		Sprite background = new SingleSprite("background");
		parentGroup.addActor(background.look);
		assertEquals(0, background.look.getZIndex());

		Sprite sprite = new SingleSprite("PenActor");
		parentGroup.addActor(sprite.look);
		assertEquals(1, sprite.look.getZIndex());

		Sprite sprite2 = new SingleSprite("EmbroideryActor");
		parentGroup.addActor(sprite2.look);
		assertEquals(2, sprite2.look.getZIndex());

		Sprite sprite3 = new SingleSprite("testSprite");
		parentGroup.addActor(sprite3.look);
		assertEquals(Constants.Z_INDEX_FIRST_SPRITE, sprite3.look.getZIndex());

		project.getDefaultScene().addSprite(sprite);
		project.getDefaultScene().addSprite(sprite2);
		ProjectManager.getInstance().setProject(project);

		sprite.getActionFactory().createGoNStepsBackAction(sprite, new Formula(Integer.MAX_VALUE)).act(1.0f);
		assertEquals(1, sprite2.look.getZIndex());

		sprite.getActionFactory().createGoNStepsBackAction(sprite2, new Formula(Integer.MIN_VALUE)).act(1.0f);
		assertEquals(3, sprite2.look.getZIndex());
	}

	@Test
	public void testBrickWithStringFormula() {
		sprite.getActionFactory().createGoNStepsBackAction(sprite3, new Formula(String.valueOf(STEPS))).act(1.0f);
		assertEquals(0, background.look.getZIndex());
		assertEquals(1, sprite.look.getZIndex());
		assertEquals(2, sprite2.look.getZIndex());
		assertEquals(Constants.Z_INDEX_FIRST_SPRITE, sprite3.look.getZIndex());

		sprite.getActionFactory().createGoNStepsBackAction(sprite, new Formula(String.valueOf(NOT_NUMERICAL_STRING))).act(1.0f);
		assertEquals(0, background.look.getZIndex());
		assertEquals(1, sprite.look.getZIndex());
		assertEquals(2, sprite2.look.getZIndex());
		assertEquals(Constants.Z_INDEX_FIRST_SPRITE, sprite3.look.getZIndex());
	}

	@Test
	public void testNullFormula() {
		sprite.getActionFactory().createGoNStepsBackAction(sprite3, null).act(1.0f);
		assertEquals(0, background.look.getZIndex());
		assertEquals(1, sprite.look.getZIndex());
		assertEquals(2, sprite2.look.getZIndex());
		assertEquals(Constants.Z_INDEX_FIRST_SPRITE, sprite3.look.getZIndex());
	}

	@Test
	public void testNotANumberFormula() {
		sprite.getActionFactory().createGoNStepsBackAction(sprite3, new Formula(Double.NaN)).act(1.0f);
		assertEquals(0, background.look.getZIndex());
		assertEquals(1, sprite.look.getZIndex());
		assertEquals(2, sprite2.look.getZIndex());
		assertEquals(Constants.Z_INDEX_FIRST_SPRITE, sprite3.look.getZIndex());
	}
}
