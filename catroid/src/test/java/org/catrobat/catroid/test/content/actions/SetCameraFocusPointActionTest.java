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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.stage.CameraPositioner;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.koin.core.module.Module;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GdxNativesLoader.class})
public class SetCameraFocusPointActionTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Sprite sprite;

	final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() {
		String projectName = "testProject";
		Project project = new Project(MockUtil.mockContextForProject(dependencyModules), projectName);
		sprite = new Sprite("sprite");
		sprite.addScript(new WhenScript());
		project.getDefaultScene().addSprite(sprite);
		inject(ProjectManager.class).getValue().setCurrentProject(project);

		PowerMockito.mockStatic(GdxNativesLoader.class);
		StageActivity.stageListener = Mockito.mock(StageListener.class);

		int virtualWidth = project.getXmlHeader().virtualScreenWidth;
		int virtualHeight = project.getXmlHeader().virtualScreenHeight;

		int virtualWidthHalf = virtualWidth / 2;
		int virtualHeightHalf = virtualHeight / 2;

		OrthographicCamera camera = new OrthographicCamera();
		StageActivity.stageListener.cameraPositioner = new CameraPositioner(camera, virtualHeightHalf, virtualWidthHalf);
	}

	@Test
	public void testSetCameraFocusPoint() {
		Float horizontalValue = 15f;
		Float verticalValue = 10f;
		ActionFactory factory = sprite.getActionFactory();
		Formula horizontal = new Formula(horizontalValue);
		Formula vertical = new Formula(verticalValue);
		ScriptSequenceAction sequenceAction = new ScriptSequenceAction(sprite.getScript(0));
		Action action = factory.createSetCameraFocusPointAction(sprite, sequenceAction, horizontal, vertical);
		action.act(1.0f);

		assertEquals(StageActivity.stageListener.cameraPositioner.getHorizontalFlex(), horizontalValue);
		assertEquals(StageActivity.stageListener.cameraPositioner.getVerticalFlex(), verticalValue);
	}
}
