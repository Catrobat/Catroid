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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.TapAtAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.MockUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.koin.core.module.Module;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.koin.java.KoinJavaComponent.inject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class TapAtActionTest {

	private static final float X_POSITION = 111f;
	private static final float Y_POSITION = 333f;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	Formula xPosition = new Formula(X_POSITION);
	Formula yPosition = new Formula(Y_POSITION);
	Formula notNumericalString = new Formula(NOT_NUMERICAL_STRING);
	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	Stage stageMock;

	@Before
	public void setUp() {
		mockStatic(GdxNativesLoader.class);

		stageMock = Mockito.mock(Stage.class);

		StageActivity.stageListener = Mockito.mock(StageListener.class);
		Mockito.when(StageActivity.stageListener.getStage()).thenReturn(stageMock);

		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "Project");
		projectManager.getValue().setCurrentProject(project);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testCreateAction() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createTapAtAction(new Sprite(), new SequenceAction(), xPosition, yPosition);
		assertThat(action, instanceOf(TapAtAction.class));
	}

	@Test
	public void testActionCallsStage() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createTapAtAction(new Sprite(), new SequenceAction(), xPosition, yPosition);

		Vector2 touchCoords = new Vector2();
		touchCoords.set(X_POSITION, Y_POSITION);

		Mockito.when(stageMock.stageToScreenCoordinates(touchCoords)).thenReturn(touchCoords);

		action.act(1.0f);

		InOrder inOrder = Mockito.inOrder(stageMock);
		inOrder.verify(stageMock, times(1)).stageToScreenCoordinates(touchCoords);
		inOrder.verify(stageMock, times(1)).touchDown((int) X_POSITION, (int) Y_POSITION, 0, 0);
		inOrder.verify(stageMock, times(1)).touchUp((int) X_POSITION, (int) Y_POSITION, 0, 0);
	}

	@Test
	public void testXNotValid() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createTapAtAction(new Sprite(), new SequenceAction(), notNumericalString, yPosition);

		Mockito.when(stageMock.stageToScreenCoordinates(any())).thenReturn(new Vector2());

		action.act(1.0f);

		Mockito.verify(stageMock, never()).stageToScreenCoordinates(any());
		Mockito.verify(stageMock, never()).touchDown(anyInt(), anyInt(), anyInt(), anyInt());
		Mockito.verify(stageMock, never()).touchUp(anyInt(), anyInt(), anyInt(), anyInt());
	}

	@Test
	public void testYNotValid() {
		ActionFactory factory = new ActionFactory();
		Action action = factory.createTapAtAction(new Sprite(), new SequenceAction(), xPosition, notNumericalString);

		Mockito.when(stageMock.stageToScreenCoordinates(any())).thenReturn(new Vector2());

		action.act(1.0f);

		Mockito.verify(stageMock, never()).stageToScreenCoordinates(any());
		Mockito.verify(stageMock, never()).touchDown(anyInt(), anyInt(), anyInt(), anyInt());
		Mockito.verify(stageMock, never()).touchUp(anyInt(), anyInt(), anyInt(), anyInt());
	}
}
