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

package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.RepeatUntilBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(Parameterized.class)
public class CloneTwoPartCompositeBrickUpdateDataTest {
	private static final UserVariable USER_VARIABLE = new UserVariable("variable");
	private static final UserList USER_LIST = new UserList("list", Arrays.asList(new Object[]{"a", "b", "c"}));

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{IfThenLogicBeginBrick.class.getSimpleName(), IfThenLogicBeginBrick.class},
				{ForeverBrick.class.getSimpleName(), ForeverBrick.class},
				{RepeatBrick.class.getSimpleName(), RepeatBrick.class},
				{RepeatUntilBrick.class.getSimpleName(), RepeatUntilBrick.class}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	private Sprite sprite;
	private CompositeBrick compositeBrick;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		Project project = new Project();
		Scene scene = new Scene();
		sprite = new Sprite("test_sprite");
		scene.addSprite(sprite);
		Script script = new StartScript();
		sprite.addScript(script);
		compositeBrick = compositeBrickClass.newInstance();
		script.addBrick(compositeBrick);
		project.addScene(scene);
		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testUpdateUserVariable() {
		sprite.addUserVariable(USER_VARIABLE);
		UserVariableBrickWithFormula variableBrick = new SetVariableBrick(new Formula(0), USER_VARIABLE);
		compositeBrick.getNestedBricks().add(variableBrick);
		Sprite cloneSprite = new SpriteController().copyForCloneBrick(sprite);
		CompositeBrick clonedCompositeBrick = (CompositeBrick) cloneSprite.getScript(0).getBrick(0);
		UserVariableBrickWithFormula clonedUserVariableBrick = (UserVariableBrickWithFormula) clonedCompositeBrick.getNestedBricks().get(0);
		assertEquals(USER_VARIABLE, clonedUserVariableBrick.getUserVariable());
		assertNotSame(USER_VARIABLE, clonedUserVariableBrick.getUserVariable());
	}

	@Test
	public void testUpdateUserList() {
		sprite.addUserList(USER_LIST);
		UserListBrick listBrick = new AddItemToUserListBrick(0.0);
		listBrick.setUserList(USER_LIST);
		compositeBrick.getNestedBricks().add(listBrick);
		Sprite cloneSprite = new SpriteController().copyForCloneBrick(sprite);
		CompositeBrick clonedCompositeBrick = (CompositeBrick) cloneSprite.getScript(0).getBrick(0);
		UserListBrick clonedUserVariableBrick = (UserListBrick) clonedCompositeBrick.getNestedBricks().get(0);
		assertEquals(USER_LIST, clonedUserVariableBrick.getUserList());
		assertNotSame(USER_LIST, clonedUserVariableBrick.getUserList());
	}
}
