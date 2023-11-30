/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.content.Context;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.koin.core.module.Module;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Collections;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

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

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);

	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	private Group parentGroup;

	@Before
	public void setUp() throws Exception {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		project = new Project(context, "testProject");

		background = new Sprite("background");
		penActorSprite = new Sprite("penActor");
		embroideryActorSprite = new Sprite("embroideryActor");
		realSprite = new Sprite("testSprite");

		project.getDefaultScene().addSprite(background);
		project.getDefaultScene().addSprite(penActorSprite);
		project.getDefaultScene().addSprite(embroideryActorSprite);
		project.getDefaultScene().addSprite(realSprite);

		parentGroup = new Group();
		parentGroup.addActor(background.look);
		parentGroup.addActor(penActorSprite.look);
		parentGroup.addActor(embroideryActorSprite.look);
		parentGroup.addActor(realSprite.look);

		projectManager.getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testSteps() {
		validateZIndices(project.getDefaultScene().getSpriteList());

		for (int i = 0; i < 20; i++) {
			Sprite spriteBefore = new Sprite("before" + i);
			parentGroup.addActor(spriteBefore.look);
			project.getDefaultScene().addSprite(spriteBefore);
			validateZIndices(project.getDefaultScene().getSpriteList());
		}
		Sprite sprite = new Sprite("testSprite");
		parentGroup.addActor(sprite.look);
		project.getDefaultScene().addSprite(sprite);
		assertEquals(sprite.look.getZIndex(), project.getDefaultScene().getSpriteList().size() - 1);

		validateZIndices(project.getDefaultScene().getSpriteList());

		int oldPosition = sprite.look.getZIndex();

		sprite.getActionFactory().createGoNStepsBackAction(sprite, new SequenceAction(), steps).act(1.0f);
		assertEquals((oldPosition - STEPS), sprite.look.getZIndex());

		validateZIndices(project.getDefaultScene().getSpriteList());
		oldPosition = sprite.look.getZIndex();

		sprite.getActionFactory().createGoNStepsBackAction(sprite, new SequenceAction(), new Formula(-STEPS)).act(1.0f);
		assertEquals((oldPosition + STEPS), sprite.look.getZIndex());
		validateZIndices(project.getDefaultScene().getSpriteList());
	}

	private void validateZIndices(List<Sprite> spriteList) {
		ArrayList<Integer> zIndices = new ArrayList<>();
		spriteList.forEach(sprite -> zIndices.add(sprite.look.getZIndex()));
		Collections.sort(zIndices);
		assert (zIndices.get(0) >= -1);
		HashSet<Integer> indices = new HashSet<>();
		for (Integer actualIndex : zIndices) {
			assert (!indices.contains(actualIndex));
			indices.add(actualIndex);
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
		projectManager.getValue().setCurrentProject(project);

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

		penActorSprite.getActionFactory().createGoNStepsBackAction(penActorSprite, new SequenceAction(), new Formula(NOT_NUMERICAL_STRING)).act(1.0f);
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
