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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.google.common.collect.Lists;

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

import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ReplaceItemInUserListActionTest {

	private static final double DOUBLE_VALUE_ITEM_TO_REPLACE_WITH = 4d;

	private Sprite testSprite;
	private UserList userList;
	private ActionFactory actionFactory;

	@Before
	public void setUp() throws Exception {
		actionFactory = new ActionFactory();
		testSprite = new Sprite("testSprite");
		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		ProjectManager.getInstance().setCurrentProject(project);

		List<Object> initializedListValues = Lists.newArrayList(1d, 2d, 3d);
		userList = new UserList("testUserList", initializedListValues);
		project.addUserList(userList);
	}

	@Test
	public void testReplaceNumericalValueInUserList() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(), new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(3d, userList.getValue().get(2));
	}

	@Test
	public void testReplaceNumericalValueInUserListAtLastPosition() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(), new Formula(3), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(1d, userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList.getValue().get(2));
	}

	@Test
	public void testReplaceNumericalValueInUserListOutOfUserListBounds() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(), new Formula(4), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(1d, userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(3d, userList.getValue().get(2));
	}

	@Test
	public void testReplaceItemWithInvalidUserList() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(), new Formula(1), new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), null).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(1d, userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(3d, userList.getValue().get(2));
	}

	@Test
	public void testReplaceNullFormula() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(), new Formula(1), null, userList).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(0d, userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(3d, userList.getValue().get(2));
	}

	@Test
	public void testNotANumberFormula() {
		double valueToReplaceWith = Double.NaN;

		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(), new Formula(1), new Formula(valueToReplaceWith), userList).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(String.valueOf(valueToReplaceWith), userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(3d, userList.getValue().get(2));
	}

	@Test
	public void testReplaceFormulaAtNull() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(), null,
				new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(3d, userList.getValue().get(2));
	}

	@Test
	public void testReplaceFormulaInterpretationException() {
		actionFactory.createReplaceItemInUserListAction(testSprite, new SequenceAction(),
				new Formula("Test"),
				new Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList).act(1f);

		assertEquals(3, userList.getValue().size());
		assertEquals(String.valueOf(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList.getValue().get(0));
		assertEquals(2d, userList.getValue().get(1));
		assertEquals(3d, userList.getValue().get(2));
	}
}
