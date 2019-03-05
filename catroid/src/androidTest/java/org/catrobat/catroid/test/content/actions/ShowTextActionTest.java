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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShowTextActionTest {

	private static final String SPRITE_NAME = "Cat";
	private static final String SECOND_SPRITE_NAME = "Dog";
	private static final String USER_VARIABLE_NAME = "var";

	@Test
	public void testShowVariablesVisibilitySameVariableNameAcrossSprites() {
		Sprite sprite = new Sprite(SPRITE_NAME);
		Project project = new Project(InstrumentationRegistry.getTargetContext(), "testProject");
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		Sprite secondSprite = new Sprite(SECOND_SPRITE_NAME);
		project.getDefaultScene().addSprite(secondSprite);

		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		UserVariable var0 = new UserVariable(USER_VARIABLE_NAME);
		var0.setVisible(false);
		dataContainer.addUserVariable(sprite, var0);

		UserVariable var1 = new UserVariable(USER_VARIABLE_NAME);
		var1.setVisible(false);
		dataContainer.addUserVariable(secondSprite, var1);

		ActionFactory factory = sprite.getActionFactory();
		Action firstSpriteAction = factory.createShowVariableAction(sprite, new Formula(0), new Formula(0),
				var0);
		factory = secondSprite.getActionFactory();
		Action secondSpriteAction = factory.createShowVariableAction(secondSprite, new Formula(0), new Formula(0),
				var1);
		firstSpriteAction.act(1.0f);
		ProjectManager.getInstance().setCurrentSprite(secondSprite);
		secondSpriteAction.act(1.0f);

		UserVariable variableOfFirstSprite = dataContainer.getUserVariable(sprite, USER_VARIABLE_NAME);
		UserVariable variableOfSecondSprite = dataContainer.getUserVariable(secondSprite, USER_VARIABLE_NAME);
		assertTrue(variableOfFirstSprite.getVisible());
		assertTrue(variableOfSecondSprite.getVisible());
	}
}
