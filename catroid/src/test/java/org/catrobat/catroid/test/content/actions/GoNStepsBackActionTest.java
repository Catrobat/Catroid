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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class GoNStepsBackActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final int STEPS = 13;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private final int realSpriteMinLayer = 3;
	private final int backgroundLayer = 0;
	private final int penActorLayer = 1;
	private final int embroideryActorLayer = 2;
	private final Formula steps = new Formula(STEPS);
	private Project project;
	private Sprite background;
	private Sprite penActorSprite;
	private Sprite embroideryActorSprite;
	private Sprite realSprite;

	@Before
	public void setUp() throws Exception {
		project = new Project(MockUtil.mockContextForProject(), "testProject");

		background = new Sprite("background");
		penActorSprite = new Sprite("penActor");
		embroideryActorSprite = new Sprite("embroideryActor");
		realSprite = new Sprite("testSprite");

		project.getDefaultScene().addSprite(penActorSprite);
		project.getDefaultScene().addSprite(embroideryActorSprite);
		project.getDefaultScene().addSprite(realSprite);

		Group parentGroup = new Group();
		parentGroup.addActor(background.look);
		parentGroup.addActor(penActorSprite.look);
		parentGroup.addActor(embroideryActorSprite.look);
		parentGroup.addActor(realSprite.look);

		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testSteps() {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		Group parentGroup = new Group();

		for (int i = 0; i < 20; i++) {
			Sprite spriteBefore = new Sprite("before" + i);
			parentGroup.addActor(spriteBefore.look);
			project.getDefaultScene().addSprite(spriteBefore);
		}
		Sprite sprite = new Sprite("testSprite");
		parentGroup.addActor(sprite.look);
		project.getDefaultScene().addSprite(sprite);
		assertEquals(20, sprite.look.getZIndex());

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getDefaultScene().getSpriteList());

		int oldPosition = sprite.look.getZIndex();

		sprite.getActionFactory().createGoNStepsBackAction(sprite, new SequenceAction(), steps).act(1.0f);
		assertEquals((oldPosition - STEPS), sprite.look.getZIndex());

		checkIfEveryZIndexUsedOnlyOnceFromZeroToNMinus1(project.getDefaultScene().getSpriteList());
		oldPosition = sprite.look.getZIndex();

		sprite.getActionFactory().createGoNStepsBackAction(sprite, new SequenceAction(), new Formula(-STEPS)).act(1.0f);
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

	@Test(expected = NullPointerException.class)
	public void testNullSprite() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createGoNStepsBackAction(null, new SequenceAction(), steps);
		action.act(1.0f);
	}

	@Test
	public void testBoundaryBackground() {
		Sprite sprite1 = new Sprite("TestSprite1");
		Sprite sprite2 = new Sprite("TestSprite2");

		Group parentGroup = new Group();
		parentGroup.addActor(background.look);
		parentGroup.addActor(penActorSprite.look);
		parentGroup.addActor(embroideryActorSprite.look);
		parentGroup.addActor(sprite1.look);
		parentGroup.addActor(sprite2.look);

		project.getDefaultScene().addSprite(sprite1);
		project.getDefaultScene().addSprite(sprite2);
		ProjectManager.getInstance().setCurrentProject(project);

		assertEquals(realSpriteMinLayer, sprite1.look.getZIndex());
		assertEquals(backgroundLayer, background.look.getZIndex());
		assertEquals(penActorLayer, penActorSprite.look.getZIndex());
		assertEquals(embroideryActorLayer, embroideryActorSprite.look.getZIndex());

		sprite2.getActionFactory().createGoNStepsBackAction(sprite2, new SequenceAction(), new Formula(Integer.MAX_VALUE)).act(1.0f);
		assertEquals(realSpriteMinLayer, sprite2.look.getZIndex());
	}

	@Test
	public void testBrickWithStringFormula() {
		realSprite.getActionFactory().createGoNStepsBackAction(realSprite, new SequenceAction(), new Formula(String.valueOf(STEPS))).act(1.0f);
		assertEquals(backgroundLayer, background.look.getZIndex());
		assertEquals(penActorLayer, penActorSprite.look.getZIndex());
		assertEquals(embroideryActorLayer, embroideryActorSprite.look.getZIndex());
		assertEquals(realSpriteMinLayer, realSprite.look.getZIndex());

		penActorSprite.getActionFactory().createGoNStepsBackAction(penActorSprite, new SequenceAction(), new Formula(String.valueOf(NOT_NUMERICAL_STRING))).act(1.0f);
		assertEquals(backgroundLayer, background.look.getZIndex());
		assertEquals(penActorLayer, penActorSprite.look.getZIndex());
		assertEquals(embroideryActorLayer, embroideryActorSprite.look.getZIndex());
		assertEquals(realSpriteMinLayer, realSprite.look.getZIndex());
	}

	@Test
	public void testNullFormula() {
		penActorSprite.getActionFactory().createGoNStepsBackAction(realSprite, new SequenceAction(), null).act(1.0f);
		assertEquals(backgroundLayer, background.look.getZIndex());
		assertEquals(penActorLayer, penActorSprite.look.getZIndex());
		assertEquals(embroideryActorLayer, embroideryActorSprite.look.getZIndex());
		assertEquals(realSpriteMinLayer, realSprite.look.getZIndex());
	}

	@Test
	public void testNotANumberFormula() {
		penActorSprite.getActionFactory().createGoNStepsBackAction(realSprite, new SequenceAction(), new Formula(Double.NaN)).act(1.0f);
		assertEquals(backgroundLayer, background.look.getZIndex());
		assertEquals(penActorLayer, penActorSprite.look.getZIndex());
		assertEquals(embroideryActorLayer, embroideryActorSprite.look.getZIndex());
		assertEquals(realSpriteMinLayer, realSprite.look.getZIndex());
	}

	@Test
	public void testLayerOrder() {
		Random random = new Random();

		realSprite.getActionFactory().createGoNStepsBackAction(realSprite, new SequenceAction(), new Formula(random.nextInt(100))).act(1.0f);
		assertEquals(realSpriteMinLayer, realSprite.look.getZIndex());
	}
}
