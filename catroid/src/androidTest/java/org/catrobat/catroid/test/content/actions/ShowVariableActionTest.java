/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class ShowVariableActionTest extends AndroidTestCase {

	private static final String SPRITE_NAME = "Cat";
	private static final UserVariable USER_VARIABLE = new UserVariable("var");
	private Project project;

	public void testShowVariablesVisibilitySameVariableNameAcrossSprites() {
		Sprite sprite = new Sprite(SPRITE_NAME);
		project = new Project(null, "testProject");
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		Sprite secondSprite = sprite.clone();
		project.getDefaultScene().addSprite(secondSprite);

		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		dataContainer.addSpriteUserVariableToSprite(sprite, USER_VARIABLE.getName()).setVisible(false);
		dataContainer.addSpriteUserVariableToSprite(secondSprite, USER_VARIABLE.getName()).setVisible(false);

		ActionFactory factory = sprite.getActionFactory();
		Action firstSpriteAction = factory.createShowVariableAction(sprite, new Formula(0), new Formula(0),
				USER_VARIABLE);
		factory = secondSprite.getActionFactory();
		Action secondSpriteAction = factory.createShowVariableAction(secondSprite, new Formula(0), new Formula(0),
				USER_VARIABLE);
		firstSpriteAction.act(1.0f);
		ProjectManager.getInstance().setCurrentSprite(secondSprite);
		secondSpriteAction.act(1.0f);

		UserVariable variableOfFirstSprite = dataContainer.findUserVariable(USER_VARIABLE.getName(), dataContainer
				.getVariableListForSprite(sprite));
		UserVariable variableOfSecondSprite = dataContainer.findUserVariable(USER_VARIABLE.getName(), dataContainer
				.getVariableListForSprite(secondSprite));
		assertTrue("Uservariable not set visible", variableOfFirstSprite.getVisible());
		assertTrue("Uservariable not set visible", variableOfSecondSprite.getVisible());
	}
}
