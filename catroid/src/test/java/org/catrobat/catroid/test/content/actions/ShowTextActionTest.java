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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.koin.core.module.Module;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static junit.framework.Assert.assertTrue;

import static org.koin.java.KoinJavaComponent.inject;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class ShowTextActionTest {

	private static final String SPRITE_NAME = "Cat";
	private static final String SECOND_SPRITE_NAME = "Dog";
	private static final String USER_VARIABLE_NAME = "var";

	private Sprite sprite;
	private UserVariable var0;
	private UserVariable var1;
	private Sprite secondSprite;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	Context contextMock = MockUtil.mockContextForProject(dependencyModules);
	AndroidStringProvider androidStringProviderMock = new AndroidStringProvider(contextMock);

	@Before
	public void setUp() {
		sprite = new Sprite(SPRITE_NAME);
		Project project = new Project(contextMock, "testProject");
		project.getDefaultScene().addSprite(sprite);
		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentSprite(sprite);

		secondSprite = new Sprite(SECOND_SPRITE_NAME);
		project.getDefaultScene().addSprite(secondSprite);

		var0 = new UserVariable(USER_VARIABLE_NAME);
		var0.setVisible(false);
		sprite.addUserVariable(var0);

		var1 = new UserVariable(USER_VARIABLE_NAME);
		var1.setVisible(false);
		secondSprite.addUserVariable(var1);

		PowerMockito.mockStatic(GdxNativesLoader.class);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testShowVariablesVisibilitySameVariableNameAcrossSprites() {
		ActionFactory factory = sprite.getActionFactory();
		Action firstSpriteAction = factory.createShowVariableAction(sprite, new SequenceAction(),
				new Formula(0), new Formula(0), var0, androidStringProviderMock);
		factory = secondSprite.getActionFactory();
		Action secondSpriteAction = factory.createShowVariableAction(secondSprite, new SequenceAction(),
				new Formula(0), new Formula(0), var1, androidStringProviderMock);
		firstSpriteAction.act(1.0f);
		projectManager.getValue().setCurrentSprite(secondSprite);
		secondSpriteAction.act(1.0f);

		UserVariable variableOfFirstSprite = sprite.getUserVariable(USER_VARIABLE_NAME);
		UserVariable variableOfSecondSprite = sprite.getUserVariable(USER_VARIABLE_NAME);
		assertTrue(variableOfFirstSprite.getVisible());
		assertTrue(variableOfSecondSprite.getVisible());
	}
}
