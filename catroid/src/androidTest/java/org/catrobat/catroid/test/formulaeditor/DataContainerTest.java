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
package org.catrobat.catroid.test.formulaeditor;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DataContainerTest {

	@Test
	public void testAddProjectVariablesWithSameName() {

		String projectName = "project";
		String varName = "var0";

		UserVariable var = new UserVariable(varName);

		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);

		DataContainer dataContainer = new DataContainer(project);
		assertTrue(dataContainer.addUserVariable(var));
		assertFalse(dataContainer.addUserVariable(var));
	}

	@Test
	public void testGetVariables() {

		String projectName = "project";
		String spriteName = "sprite";
		String gVarName = "gVar";
		String lVarName = "lVar";

		UserVariable gVar = new UserVariable(gVarName);
		UserVariable lVar = new UserVariable(lVarName);

		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new Sprite(spriteName);
		project.getDefaultScene().addSprite(sprite);

		DataContainer dataContainer = new DataContainer(project);
		assertTrue(dataContainer.addUserVariable(gVar));
		assertTrue(dataContainer.addUserVariable(sprite, lVar));

		assertEquals(gVar, dataContainer.getUserVariable(sprite, gVarName));
		assertEquals(lVar, dataContainer.getUserVariable(sprite, lVarName));

		assertEquals(1, dataContainer.getProjectUserVariables().size());
		assertEquals(1, dataContainer.getSpriteUserVariables(sprite).size());

		assertEquals(gVar, dataContainer.getProjectUserVariable(gVarName));
		assertNull(dataContainer.getProjectUserVariable(lVarName));
	}

	@Test
	public void testGetLists() {

		String projectName = "project";
		String spriteName = "sprite";
		String gListName = "gList";
		String lListName = "lList";

		UserList gList = new UserList(gListName);
		UserList lList = new UserList(lListName);

		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new Sprite(spriteName);
		project.getDefaultScene().addSprite(sprite);

		DataContainer dataContainer = new DataContainer(project);
		assertTrue(dataContainer.addUserList(gList));
		assertTrue(dataContainer.addUserList(sprite, lList));

		assertEquals(gList, dataContainer.getUserList(sprite, gListName));
		assertEquals(lList, dataContainer.getUserList(sprite, lListName));

		assertEquals(1, dataContainer.getProjectUserLists().size());
		assertEquals(1, dataContainer.getSpriteUserLists(sprite).size());

		assertEquals(gList, dataContainer.getUserList(gListName));
		assertNull(dataContainer.getUserList(lListName));
	}
}
