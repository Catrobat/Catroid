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
package org.catrobat.catroid.test.io.devicevariableaccessor;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.io.StorageOperations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeviceVariableAccessorCleanRoutineTest {
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
	private DeviceVariableAccessor accessor;

	@Before
	public void setUp() throws IOException {
		project = createProject();

		ArrayList<UserVariable> allVariables = new ArrayList<>();

		sprite1.getUserVariables().add(sprite1UserVariable);
		allVariables.addAll(sprite1.getUserVariables());

		sprite2.getUserVariables().add(sprite2UserVariable);
		allVariables.addAll(sprite2.getUserVariables());

		project.getUserVariables().add(globalUserVariable);
		allVariables.addAll(project.getUserVariables());

		project.getMultiplayerVariables().add(multiplayerUserVariable);
		allVariables.addAll(project.getMultiplayerVariables());

		accessor = new DeviceVariableAccessor(directory);
		Map<UUID, Object> map = new HashMap<>();

		for (UserVariable userVariable : allVariables) {
			map.put(userVariable.getDeviceKey(), userVariable.getValue());
		}
		accessor.writeMapToJson(map);
	}

	private Project createProject() {
		Project project = new Project();
		directory = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "DeviceValues");
		directory.mkdir();
		project.setDirectory(directory);
		project.setName("__project__");

		sprite1 = new Sprite("__sprite1__");
		sprite2 = new Sprite("__sprite3__");

		scene1 = new Scene();
		scene1.addSprite(sprite1);

		scene2 = new Scene();
		scene2.addSprite(sprite2);

		project.addScene(scene1);
		project.addScene(scene2);
		return project;
	}

	@Test
	public void deleteGlobalVariablesTest() {
		project.getUserVariables().clear();
		accessor.cleanUpDeletedUserData(project);
		Map<UUID, Object> map = accessor.readMapFromJson();
		assertFalse(map.containsKey(globalUserVariable.getDeviceKey()));
		assertTrue(map.containsKey(multiplayerUserVariable.getDeviceKey()));
		assertTrue(map.containsKey(sprite1UserVariable.getDeviceKey()));
		assertTrue(map.containsKey(sprite2UserVariable.getDeviceKey()));
	}

	@Test
	public void deleteSceneVariablesTest() {
		project.removeScene(scene1);
		accessor.cleanUpDeletedUserData(project);
		Map<UUID, Object> map = accessor.readMapFromJson();
		assertTrue(map.containsKey(globalUserVariable.getDeviceKey()));
		assertTrue(map.containsKey(multiplayerUserVariable.getDeviceKey()));
		assertFalse(map.containsKey(sprite1UserVariable.getDeviceKey()));
		assertTrue(map.containsKey(sprite2UserVariable.getDeviceKey()));
	}

	@Test
	public void deleteSpriteVariablesTest() {
		sprite2.getUserVariables().clear();
		accessor.cleanUpDeletedUserData(project);
		Map<UUID, Object> map = accessor.readMapFromJson();
		assertTrue(map.containsKey(globalUserVariable.getDeviceKey()));
		assertTrue(map.containsKey(multiplayerUserVariable.getDeviceKey()));
		assertTrue(map.containsKey(sprite1UserVariable.getDeviceKey()));
		assertFalse(map.containsKey(sprite2UserVariable.getDeviceKey()));
	}

	@Test
	public void deleteMultiplayerVariablesTest() {
		project.getMultiplayerVariables().clear();
		accessor.cleanUpDeletedUserData(project);
		Map<UUID, Object> map = accessor.readMapFromJson();
		assertTrue(map.containsKey(globalUserVariable.getDeviceKey()));
		assertFalse(map.containsKey(multiplayerUserVariable.getDeviceKey()));
		assertTrue(map.containsKey(sprite1UserVariable.getDeviceKey()));
		assertTrue(map.containsKey(sprite2UserVariable.getDeviceKey()));
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
	}
}
