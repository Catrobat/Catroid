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
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertTrue;

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

	@Before
	public void setUp() {
		sprite = new Sprite(SPRITE_NAME);
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

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

	@Test
	public void testShowVariablesVisibilitySameVariableNameAcrossSprites() {
		ActionFactory factory = sprite.getActionFactory();
		Action firstSpriteAction = factory.createShowVariableAction(sprite, new Formula(0), new Formula(0),
				var0);
		factory = secondSprite.getActionFactory();
		Action secondSpriteAction = factory.createShowVariableAction(secondSprite, new Formula(0), new Formula(0),
				var1);
		firstSpriteAction.act(1.0f);
		ProjectManager.getInstance().setCurrentSprite(secondSprite);
		secondSpriteAction.act(1.0f);

		UserVariable variableOfFirstSprite = sprite.getUserVariable(USER_VARIABLE_NAME);
		UserVariable variableOfSecondSprite = sprite.getUserVariable(USER_VARIABLE_NAME);
		assertTrue(variableOfFirstSprite.getVisible());
		assertTrue(variableOfSecondSprite.getVisible());
	}
}
