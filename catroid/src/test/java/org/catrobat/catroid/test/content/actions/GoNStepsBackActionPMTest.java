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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.koin.core.module.Module;

import kotlin.Lazy;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class GoNStepsBackActionPMTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Project project;
	private Sprite background;
	private Sprite penActorSprite;
	private Sprite embroideryActorSprite;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);

	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		project = new Project(context, "testProject");

		background = new Sprite("background");
		penActorSprite = new Sprite("penActor");
		embroideryActorSprite = new Sprite("embroideryActor");
		Sprite realSprite = new Sprite("testSprite");

		project.getDefaultScene().addSprite(penActorSprite);
		project.getDefaultScene().addSprite(embroideryActorSprite);
		project.getDefaultScene().addSprite(realSprite);

		Group parentGroup = new Group();
		parentGroup.addActor(background.look);
		parentGroup.addActor(penActorSprite.look);
		parentGroup.addActor(embroideryActorSprite.look);
		parentGroup.addActor(realSprite.look);

		projectManager.getValue().setCurrentProject(project);
		PowerMockito.mockStatic(GdxNativesLoader.class);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testBoundaryForeground() {
		final int expectedLayer = 4;
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

		int realSpriteMinLayer = 3;
		assertEquals(realSpriteMinLayer, sprite1.look.getZIndex());
		int backgroundLayer = 0;
		assertEquals(backgroundLayer, background.look.getZIndex());
		int penActorLayer = 1;
		assertEquals(penActorLayer, penActorSprite.look.getZIndex());
		int embroideryActorLayer = 2;
		assertEquals(embroideryActorLayer, embroideryActorSprite.look.getZIndex());

		sprite1.getActionFactory().createGoNStepsBackAction(sprite1, new SequenceAction(), new Formula(Integer.MIN_VALUE)).act(1.0f);
		assertEquals(expectedLayer, sprite1.look.getZIndex());
	}
}
