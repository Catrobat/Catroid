/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.test.io.asynctask;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceListAccessor;
import org.catrobat.catroid.io.DeviceUserDataAccessor;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.io.asynctask.ProjectLoaderKt.loadProject;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProjectLoaderTest {
	private final String projectName = "testProject";
	private Project project;
	private File directory;
	private Scene scene1;
	private Scene scene2;
	private Sprite sprite1;
	private Sprite sprite2;
	private UserVariable sprite1UserVariable = new UserVariable("Sprite1_Variable", 0);
	private UserVariable sprite2UserVariable = new UserVariable("Sprite2_Variable", 0);
	private UserVariable globalUserVariable = new UserVariable("Global_Variable", 0);
	private UserVariable multiplayerUserVariable = new UserVariable("Multiplayer_Variable", 0);
	private UserList sprite1UserList = new UserList("Sprite1_List");
	private UserList sprite2UserList = new UserList("Sprite2_List");
	private UserList globalUserList = new UserList("Global_List");
	private DeviceVariableAccessor variableAccessor;
	private DeviceUserDataAccessor userDataAccessor;
	private File[] correctLooks;

	@Before
	public void setUp() throws IOException {
		project = createProject();
		assertTrue(XstreamSerializer.getInstance().saveProject(project));

		setUpVariables();
		setUpUserLists();
		setUpLooks();
		assertTrue(XstreamSerializer.getInstance().saveProject(project));
	}

	private Project createProject() throws IOException {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		directory = project.getDirectory();

		sprite1 = new Sprite("__sprite1__");
		sprite2 = new Sprite("__sprite3__");

		scene1 = new Scene("__scene1__", project);
		scene1.addSprite(sprite1);

		scene2 = new Scene("__scene2__", project);
		scene2.addSprite(sprite2);

		project.addScene(scene1);
		project.addScene(scene2);
		return project;
	}

	private void setUpVariables() {
		sprite1.getUserVariables().add(sprite1UserVariable);
		ArrayList<UserVariable> allVariables = new ArrayList<>(sprite1.getUserVariables());

		sprite2.getUserVariables().add(sprite2UserVariable);
		allVariables.addAll(sprite2.getUserVariables());

		project.getUserVariables().add(globalUserVariable);
		allVariables.addAll(project.getUserVariables());

		project.getMultiplayerVariables().add(multiplayerUserVariable);
		allVariables.addAll(project.getMultiplayerVariables());

		variableAccessor = new DeviceVariableAccessor(directory);
		Map<UUID, Object> variablesMap = new HashMap<>();

		for (UserVariable userVariable : allVariables) {
			variablesMap.put(userVariable.getDeviceKey(), userVariable.getValue());
		}
		variableAccessor.writeMapToJson(variablesMap);
	}

	private void setUpUserLists() {
		sprite1.getUserLists().add(sprite1UserList);
		ArrayList<UserList> allLists = new ArrayList<>(sprite1.getUserLists());

		sprite2.getUserLists().add(sprite2UserList);
		allLists.addAll(sprite2.getUserLists());

		project.getUserLists().add(globalUserList);
		allLists.addAll(project.getUserLists());

		userDataAccessor = new DeviceListAccessor(directory);
		Map<UUID, List<Object>> map = new HashMap<>();

		for (UserList userList : allLists) {
			map.put(userList.getDeviceKey(), userList.getValue());
		}
		userDataAccessor.writeMapToJson(map);
	}

	private void setUpLooks() throws IOException {
		addLookToSprite(sprite1, scene1, "Valid look1");
		addLookToSprite(sprite1, scene1, "Valid look2");
		addUnusedLookToSprite(scene1, "Unused look1");
		addUnusedLookToSprite(scene1, "Unused look2");
	}

	private void addLookToSprite(Sprite sprite, Scene scene, String name) throws IOException {
		File imageDirectory = new File(scene.getDirectory(), Constants.IMAGE_DIRECTORY_NAME);
		File lookDataFile = new File(imageDirectory, name);
		lookDataFile.createNewFile();
		LookData lookData = new LookData(name, lookDataFile);
		sprite.getLookList().add(lookData);
		sprite.look.setLookData(sprite.getLookList().stream().findFirst().get());
		correctLooks = imageDirectory.listFiles();
	}

	private void addUnusedLookToSprite(Scene scene, String name) throws IOException {
		File imageDirectory = new File(scene.getDirectory(), Constants.IMAGE_DIRECTORY_NAME);
		File lookDataFile = new File(imageDirectory, name);
		lookDataFile.createNewFile();
	}

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(projectName);
	}

	@Test
	public void projectLoadTaskTest() throws IOException {
		// Delete User Variables
		project.getUserVariables().clear();
		sprite1.getUserVariables().clear();
		project.removeScene(scene2);
		project.getMultiplayerVariables().clear();
		//Delete User Lists
		project.getUserLists().clear();
		sprite1.getUserLists().clear();
		// Check Look Count (2 used, 2 unused, 1 nomediaOffset)
		File imageDirectoryPre = new File(scene1.getDirectory(), Constants.IMAGE_DIRECTORY_NAME);
		assertEquals(2 + 2 + 1, Objects.requireNonNull(imageDirectoryPre.listFiles()).length);

		//save changes of project
		assertTrue(XstreamSerializer.getInstance().saveProject(project));

		assertNotNull(directory);
		assertTrue(loadProject(directory, ApplicationProvider.getApplicationContext()));

		// Check if User Variables are removed
		Map<UUID, Object> variableMap = variableAccessor.readMapFromJson();
		assertFalse(variableMap.containsKey(globalUserVariable.getDeviceKey()));
		assertFalse(variableMap.containsKey(multiplayerUserVariable.getDeviceKey()));
		assertFalse(variableMap.containsKey(sprite1UserVariable.getDeviceKey()));
		assertFalse(variableMap.containsKey(sprite2UserVariable.getDeviceKey()));
		// Check if User Lists are removed
		Map<UUID, Object> listMap = userDataAccessor.readMapFromJson();
		assertFalse(listMap.containsKey(globalUserList.getDeviceKey()));
		assertFalse(listMap.containsKey(sprite1UserList.getDeviceKey()));
		assertFalse(listMap.containsKey(sprite2UserList.getDeviceKey()));
		// Check if Looks are removed and only correct ones remain
		File imageDirectoryPost = new File(scene1.getDirectory(), Constants.IMAGE_DIRECTORY_NAME);
		assertArrayEquals(correctLooks, imageDirectoryPost.listFiles());
	}

	@Test
	public void projectInvalidLoadTaskTest() throws IOException {
		File directory = new File("");
		assertNotNull(directory);
		assertFalse(loadProject(directory, ApplicationProvider.getApplicationContext()));
	}
}
