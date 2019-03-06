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
package org.catrobat.catroid.test.common;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.defaultprojectcreators.DefaultProjectCreator;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.asynctask.ProjectCopyTask;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeviceVariableAccessorTest {

	UserVariable userVariable;
	UserVariable localUserVariable;
	Sprite sprite;
	Project project;
	Context context;

	ArrayList<String> cleanupList = new ArrayList<>();

	@Before
	public void setUp() throws IOException {
		context = InstrumentationRegistry.getTargetContext();
		project = createSimpleProject("projectX");
		sprite = new Sprite("_sprite_");
		project.getDefaultScene().addSprite(sprite);
		userVariable = new UserVariable("X", 10.0);
		localUserVariable = new UserVariable("Y", 20.0);

		ProjectManager.getInstance().setCurrentProject(project);
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();
		dataContainer.addUserVariable(sprite, localUserVariable);
		dataContainer.addUserVariable(userVariable);
		XstreamSerializer.getInstance().saveProject(project);
		cleanupList.add(project.getName());
	}

	@Test
	public void saveUserVariableTest() throws IOException, ClassNotFoundException {
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(project.getName());

		Object savedVar = userVariable.getValue();
		Object localSavedVar = localUserVariable.getValue();
		accessor.writeVariable(userVariable);
		accessor.writeVariable(localUserVariable);
		userVariable.setValue("false");
		localUserVariable.setValue("false");
		assertTrue(accessor.readUserVariableValue(userVariable));
		assertTrue(accessor.readUserVariableValue(localUserVariable));
		assertEquals(savedVar, userVariable.getValue());
		assertEquals(localSavedVar, localUserVariable.getValue());
	}

	@Test
	public void saveProjectTest() throws IOException, LoadingProjectException, ClassNotFoundException {
		UserVariable userVariable1 = project.getDefaultScene().getDataContainer().getProjectUserVariable(userVariable
				.getName());
		userVariable1.setValue("false");
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(project.getName());
		accessor.writeVariable(userVariable1);
		XstreamSerializer.getInstance().saveProject(project);
		Project project1 = XstreamSerializer.getInstance().loadProject(project.getName(), context);

		UserVariable result = project1.getDefaultScene().getDataContainer().getProjectUserVariable(userVariable.getName());
		accessor = new DeviceVariableAccessor(project1.getName());
		accessor.readUserVariableValue(result);

		assertEquals(result.getDeviceValueFileName(), userVariable1.getDeviceValueFileName());
		assertEquals(result.getValue(), userVariable1.getValue());
	}

	@Test
	public void copyProjectTest() throws IOException, ClassNotFoundException, LoadingProjectException {
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(project.getName());
		accessor.writeVariable(userVariable);
		XstreamSerializer.getInstance().saveProject(project);

		Project project2 = new Project(context, "projectX2");
		ProjectCopyTask task = new ProjectCopyTask(null);
		task.task(project.getName(), project2.getName());

		project2 = XstreamSerializer.getInstance().loadProject(project2.getName(), context);
		DataContainer container = project2.getDefaultScene().getDataContainer();
		container.getUserVariable(null, userVariable.getName()).setValue("false");

		UserVariable userVariableProject2 = container.getUserVariable(null, userVariable.getName());
		accessor = new DeviceVariableAccessor(project2.getName());
		accessor.readUserVariableValue(userVariableProject2);

		cleanupList.add(project2.getName());
		assertEquals(userVariable.getDeviceValueFileName(), userVariableProject2.getDeviceValueFileName());
		assertEquals(userVariable.getValue(), userVariableProject2.getValue());
	}

	@Test
	public void renameProjectTest() throws IOException, ClassNotFoundException {
		String name = project.getName();
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(project.getName());
		Object savedVariable = userVariable.getValue();
		accessor.writeVariable(userVariable);
		XstreamSerializer.getInstance().saveProject(project);

		userVariable.setValue("false");
		project.setName("false");
		accessor = new DeviceVariableAccessor(project.getName());
		accessor.readUserVariableValue(userVariable);

		cleanupList.add("false");
		assertEquals(savedVariable, userVariable.getValue());
		project.setName(name);
	}

	@Test
	public void deleteLocaleVariableTest() throws IOException, ClassNotFoundException {
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(project.getName());
		accessor.writeVariable(localUserVariable);
		accessor.writeVariable(userVariable);
		accessor.deleteAllLocalVariables(ProjectManager.getInstance().getCurrentlyEditedScene(), sprite);

		assertFalse(accessor.readUserVariableValue(localUserVariable));
		assertTrue(accessor.readUserVariableValue(userVariable));
	}

	@Test
	public void cloneSpriteTest() throws IOException, ClassNotFoundException {
		DataContainer container = project.getDefaultScene().getDataContainer();
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(project.getName());
		Sprite clone = sprite.cloneForCloneBrick();

		accessor.writeVariable(localUserVariable);

		UserVariable clonedVar = container.getUserVariable(clone, localUserVariable.getName());
		clonedVar.setValue("false");
		assertTrue(accessor.readUserVariableValue(clonedVar));
		assertEquals(localUserVariable.getValue(), clonedVar.getValue());
	}

	@Test
	public void deleteUserVariableTest() throws IOException, ClassNotFoundException {
		DeviceVariableAccessor accessor = new DeviceVariableAccessor(project.getName());
		accessor.writeVariable(userVariable);
		assertTrue(accessor.readUserVariableValue(userVariable));
		accessor.removeFile(userVariable);
		assertFalse(accessor.readUserVariableValue(userVariable));
	}

	@After
	public void tearDown() throws IOException {
		for (String name: cleanupList) {
			TestUtils.deleteProjects(name);
			new DeviceVariableAccessor(name).deleteAllVariables();
		}
	}

	private Project createSimpleProject(String name) throws IOException {
		DefaultProjectCreator creator = new DefaultProjectCreator();
		Project project = creator.createDefaultProject(name, context, true);
		ProjectManager.getInstance().setCurrentProject(project);
		return project;
	}
}
