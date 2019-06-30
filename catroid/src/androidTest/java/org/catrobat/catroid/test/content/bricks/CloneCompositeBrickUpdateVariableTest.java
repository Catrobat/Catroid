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
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
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
public class CloneCompositeBrickUpdateVariableTest {
	private static final UserVariable USER_VARIABLE = new UserVariable("list");
	private static final UserList USER_LIST = new UserList("list", Arrays.asList(new Object[]{"a", "b", "c"}));

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"SetVariableBrick", new SetVariableBrick(new Formula(2), USER_VARIABLE)},
				{"ChangeVariableBrick", new ChangeVariableBrick(new Formula(2), USER_VARIABLE)},
				{"AddItemToUserListBrick", new AddItemToUserListBrick(new Formula(2))},
				{"DeleteItemOfUserListBrick", new DeleteItemOfUserListBrick(new Formula(2))}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Brick brick;

	private Sprite sprite;
	private Script script;

	@Before
	public void setUp() {
		Project project = new Project();
		Scene scene = new Scene();
		sprite = new Sprite("test_sprite");
		scene.addSprite(sprite);
		script = new StartScript();
		sprite.addScript(script);
		sprite.addUserVariable(USER_VARIABLE);
		sprite.addUserList(USER_LIST);

		project.addScene(scene);
		ProjectManager.getInstance().setCurrentProject(project);

		if (brick instanceof UserListBrick) {
			((UserListBrick) brick).setUserList(USER_LIST);
		}
	}

	@Test
	public void testIf() {
		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick();
		ifBrick.addBrickToIfBranch(brick);
		script.addBrick(ifBrick);

		Sprite cloneSprite = new SpriteController().copyForCloneBrick(sprite);
		Brick cloneIfLogicBrick = cloneSprite.getScript(0).getBrick(0);
		checkClonedBrickInNestedBrick((CompositeBrick) cloneIfLogicBrick);
	}

	@Test
	public void testElse() {
		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick();
		ifBrick.addBrickToElseBranch(brick);
		script.addBrick(ifBrick);

		Sprite cloneSprite = new SpriteController().copyForCloneBrick(sprite);
		Brick cloneIfLogicBrick = cloneSprite.getScript(0).getBrick(0);
		checkClonedBrickInNestedBrick((CompositeBrick) cloneIfLogicBrick);
	}

	@Test
	public void testForever() {
		ForeverBrick foreverBrick = new ForeverBrick();
		foreverBrick.addBrick(brick);
		script.addBrick(foreverBrick);

		Sprite cloneSprite = new SpriteController().copyForCloneBrick(sprite);
		Brick cloneForeverBrick = cloneSprite.getScript(0).getBrick(0);
		checkClonedBrickInNestedBrick((CompositeBrick) cloneForeverBrick);
	}

	@Test
	public void testRepeat() {
		RepeatBrick repeatBrick = new RepeatBrick();
		repeatBrick.addBrick(brick);
		script.addBrick(repeatBrick);

		Sprite cloneSprite = new SpriteController().copyForCloneBrick(sprite);
		Brick cloneRepeatBrick = cloneSprite.getScript(0).getBrick(0);
		checkClonedBrickInNestedBrick((CompositeBrick) cloneRepeatBrick);
	}

	@Test
	public void doubleNested() {
		RepeatBrick repeatBrick0 = new RepeatBrick();
		RepeatBrick repeatBrick1 = new RepeatBrick();
		repeatBrick1.addBrick(brick);
		repeatBrick0.addBrick(repeatBrick1);

		script.addBrick(repeatBrick0);
		Sprite cloneSprite = new SpriteController().copyForCloneBrick(sprite);

		Brick cloneRepeatBrick0 = cloneSprite.getScript(0).getBrick(0);
		Brick cloneRepeatBrick1 = ((RepeatBrick) cloneRepeatBrick0).getNestedBricks().get(0);

		checkClonedBrickInNestedBrick((CompositeBrick) cloneRepeatBrick1);
	}

	private void checkClonedBrickInNestedBrick(CompositeBrick compositeBrick) {
		Brick nestedBrick = compositeBrick.getNestedBricks().size() == 0
				? compositeBrick.getSecondaryNestedBricks().get(0)
				: compositeBrick.getNestedBricks().get(0);

		if (nestedBrick instanceof UserVariableBrick) {
			UserVariable variable = ((UserVariableBrick) nestedBrick).getUserVariable();
			assertEquals(variable.getName(), USER_VARIABLE.getName());
			assertEquals(variable.getValue(), USER_VARIABLE.getValue());
			assertNotSame(variable, USER_VARIABLE);
		} else {
			UserList userList = ((UserListBrick) nestedBrick).getUserList();
			assertEquals(userList.getName(), USER_LIST.getName());
			assertEquals(userList.getList(), USER_LIST.getList());
			assertNotSame(userList, USER_LIST);
		}
	}
}
