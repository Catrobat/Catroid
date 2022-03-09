/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ThinkSayBubbleAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.stage.ShowBubbleActor;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.koin.core.module.Module;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GdxNativesLoader.class, ShowBubbleActor.class, ThinkSayBubbleAction.class})
public class ThinkSayBubbleActionTest {
	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	Context contextMock = MockUtil.mockContextForProject(dependencyModules);
	AndroidStringProvider androidStringProviderMock = new AndroidStringProvider(contextMock);

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(GdxNativesLoader.class);
		PowerMockito.whenNew(ShowBubbleActor.class).withAnyArguments()
				.thenReturn(Mockito.mock(ShowBubbleActor.class));
		StageActivity.stageListener = Mockito.mock(StageListener.class);

		Project project = new Project(contextMock, "Project");
		projectManager.getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		StageActivity.stageListener = null;
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testCreateBubbleActor() throws Exception {
		Formula emptyText = new Formula(
				new FormulaElement(FormulaElement.ElementType.STRING, "", null));
		Formula normalText = new Formula(
				new FormulaElement(FormulaElement.ElementType.STRING, "not empty", null));
		ActionFactory factory = new ActionFactory();

		ThinkSayBubbleAction thinkActionEmptyText =
				(ThinkSayBubbleAction) factory.createThinkSayBubbleAction(
						Mockito.mock(Sprite.class), Mockito.mock(SequenceAction.class),
						androidStringProviderMock, emptyText, Constants.THINK_BRICK);
		ThinkSayBubbleAction thinkActionNormalText =
				(ThinkSayBubbleAction) factory.createThinkSayBubbleAction(
						Mockito.mock(Sprite.class), Mockito.mock(SequenceAction.class),
						androidStringProviderMock, normalText, Constants.THINK_BRICK);

		assertNull(thinkActionEmptyText.createBubbleActor(androidStringProviderMock));
		assertNotNull(thinkActionNormalText.createBubbleActor(androidStringProviderMock));
	}

	@Test
	public void testBasicThinkSayBubble() throws InterpretationException {
		Mockito.when(StageActivity.stageListener.getBubbleActorForSprite(Mockito.any(Sprite.class)))
				.thenReturn(null);

		Sprite sprite = Mockito.mock(Sprite.class);
		Formula text = Mockito.mock(Formula.class);
		ShowBubbleActor actor = Mockito.mock(ShowBubbleActor.class);
		ActionFactory factory = new ActionFactory();
		ThinkSayBubbleAction thinkAction =
				(ThinkSayBubbleAction) factory.createThinkSayBubbleAction(sprite,
						new SequenceAction(), androidStringProviderMock, text, Constants.THINK_BRICK);
		thinkAction = Mockito.spy(thinkAction);
		Mockito.doReturn(actor).when(thinkAction).createBubbleActor(androidStringProviderMock);

		thinkAction.act(1f);

		Mockito.verify(StageActivity.stageListener, Mockito.times(1))
				.setBubbleActorForSprite(sprite, actor);
		Mockito.verify(StageActivity.stageListener, Mockito.never())
				.removeBubbleActorForSprite(sprite);
	}

	@Test
	public void testRemoveThinkSayBubble() throws InterpretationException {
		Mockito.when(StageActivity.stageListener.getBubbleActorForSprite(Mockito.any(Sprite.class)))
				.thenReturn(null);
		Sprite sprite = Mockito.mock(Sprite.class);
		Formula text = Mockito.mock(Formula.class);
		ShowBubbleActor actor = Mockito.mock(ShowBubbleActor.class);

		ActionFactory factory = new ActionFactory();
		ThinkSayBubbleAction thinkAction =
				(ThinkSayBubbleAction) factory.createThinkSayBubbleAction(sprite,
						new SequenceAction(), androidStringProviderMock, text, Constants.THINK_BRICK);
		ThinkSayBubbleAction thinkActionWithoutText =
				(ThinkSayBubbleAction) factory.createThinkSayBubbleAction(sprite,
						new SequenceAction(), androidStringProviderMock, text, Constants.THINK_BRICK);
		thinkAction = Mockito.spy(thinkAction);
		thinkActionWithoutText = Mockito.spy(thinkActionWithoutText);
		Mockito.doReturn(actor)
				.when(thinkAction).createBubbleActor(androidStringProviderMock);
		Mockito.doReturn(null)
				.when(thinkActionWithoutText).createBubbleActor(androidStringProviderMock);

		thinkAction.act(1f);
		Mockito.when(StageActivity.stageListener.getBubbleActorForSprite(Mockito.any(Sprite.class)))
				.thenReturn(actor);
		thinkActionWithoutText.act(1f);

		InOrder inOrder = Mockito.inOrder(StageActivity.stageListener);
		inOrder.verify(StageActivity.stageListener, Mockito.times(1))
				.setBubbleActorForSprite(sprite, actor);
		inOrder.verify(StageActivity.stageListener, Mockito.times(1))
				.removeBubbleActorForSprite(sprite);
	}
}
