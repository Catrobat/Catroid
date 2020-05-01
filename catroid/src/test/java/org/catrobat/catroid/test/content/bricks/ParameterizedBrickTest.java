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
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ParameterizedBrick;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.utils.UserDataUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ParameterizedBrickTest {

	private UserList userList;
	private UserVariable userVariable;
	private ParameterizedBrick parameterizedBrick;
	private static final String VARIABLE_NAME = "Test";
	private static final String NEW_VARIABLE_NAME = "NewName";

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		userVariable = new UserVariable();
		userList = new UserList();
		Scene scene = new Scene();
		Sprite sprite = new Sprite();
		Script script = new WhenScript();
		parameterizedBrick = new ParameterizedBrick();

		userVariable.setName(VARIABLE_NAME);
		userList.setName(VARIABLE_NAME);

		parameterizedBrick.getUserLists().add(userList);

		project.addUserVariable(userVariable);
		project.addUserList(userList);
		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(script);
		script.addBrick(parameterizedBrick);

		ProjectManager.getInstance().setCurrentProject(project);
	}

	@Test
	public void testRenamingLinkedVariable() {
		UserDataUtil.renameUserData(userVariable, NEW_VARIABLE_NAME);

		assertEquals(userList.getName(), NEW_VARIABLE_NAME);
	}

	@Test
	public void testRenamingLinkedList() {
		UserDataUtil.renameUserData(userList, NEW_VARIABLE_NAME);

		assertEquals(userVariable.getName(), NEW_VARIABLE_NAME);
	}

	@Test
	public void testRemovingLinkedVariable() {
		List<UserData> elements = new ArrayList<>();
		elements.add(userVariable);
		ProjectManager.getInstance().getCurrentProject().deselectElements(elements);

		assertFalse(parameterizedBrick.getUserLists().contains(userList));
	}

	@Test
	public void testRemovingLinkedList() {
		List<UserData> elements = new ArrayList<>();
		elements.add(userList);
		ProjectManager.getInstance().getCurrentProject().deselectElements(elements);

		assertFalse(parameterizedBrick.getUserLists().contains(userList));
	}
}
