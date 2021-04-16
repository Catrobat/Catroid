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

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class GoNStepsBackActionPMTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private final int realSpriteMinLayer = 3;
	private final int backgroundLayer = 0;
	private final int penActorLayer = 1;
	private final int embroideryActorLayer = 2;
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
		PowerMockito.mockStatic(GdxNativesLoader.class);
	}

	@Test
	public void testBoudaryForeground() {
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
		ProjectManager.getInstance().setCurrentProject(project);

		assertEquals(realSpriteMinLayer, sprite1.look.getZIndex());
		assertEquals(backgroundLayer, background.look.getZIndex());
		assertEquals(penActorLayer, penActorSprite.look.getZIndex());
		assertEquals(embroideryActorLayer, embroideryActorSprite.look.getZIndex());

		sprite1.getActionFactory().createGoNStepsBackAction(sprite1, new SequenceAction(), new Formula(Integer.MIN_VALUE)).act(1.0f);
		assertEquals(expectedLayer, sprite1.look.getZIndex());
	}
}
