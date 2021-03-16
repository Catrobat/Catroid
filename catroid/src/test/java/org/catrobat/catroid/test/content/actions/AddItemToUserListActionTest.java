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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AddItemToUserListActionTest {

	private static final String TEST_USERLIST_NAME = "testUserList";
	private static final double DOUBLE_VALUE_ITEM_TO_ADD = 3.0;

	private Sprite testSprite;
	private Project project;
	private UserList userList;

	private ActionFactory actionFactory;

	@Before
	public void setUp() throws Exception {
		actionFactory = new ActionFactory();
		testSprite = new Sprite("testSprite");
		project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());

		List<Object> initialList = new ArrayList<>();
		initialList.add(1.0);
		initialList.add(2.0);
		userList = new UserList(TEST_USERLIST_NAME, initialList);
		project.addUserList(userList);
	}

	@Test
	public void testAddNumericalValueToUserList() {
		actionFactory.createAddItemToUserListAction(testSprite,
			new SequenceAction(), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList).act(1f);
		Object lastItemOfUserList = userList.getValue().get(userList.getValue().size() - 1);

		assertEquals(3, userList.getValue().size());
		assertEquals("3.0", lastItemOfUserList);
	}

	@Test
	public void testAddItemWithInvalidUserList() {
		actionFactory.createAddItemToUserListAction(testSprite,
			new SequenceAction(), new Formula(DOUBLE_VALUE_ITEM_TO_ADD), null).act(1f);
		assertEquals(2, userList.getValue().size());
	}

	@Test
	public void testAddNullFormula() {
		actionFactory.createAddItemToUserListAction(testSprite, new SequenceAction(), null,
				userList).act(1f);
		Object lastItemOfUserList = userList.getValue().get(userList.getValue().size() - 1);
		assertEquals(0d, lastItemOfUserList);
	}

	@Test
	public void testNotANumberFormula() {
		actionFactory.createAddItemToUserListAction(testSprite, new SequenceAction(),
				new Formula(Double.NaN),
				userList).act(1f);
		Object lastItemOfUserList = userList.getValue().get(userList.getValue().size() - 1);
		assertEquals(String.valueOf(Double.NaN), lastItemOfUserList);
	}
}
