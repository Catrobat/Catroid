/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.test.io.DeviceVariableAccessor;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

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
import java.util.UUID;

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
	private Sprite sprite3;
	private DeviceVariableAccessor accessor;

	@Before
	public void setUp() throws IOException {
		project = createProject();

		ArrayList<UserVariable> allVariables = new ArrayList<>();

		sprite1.getUserVariables().addAll(generateUserVariables("S1_Variable:", 3));
		allVariables.addAll(sprite1.getUserVariables());

		sprite2.getUserVariables().addAll(generateUserVariables("S2_Variable:", 3));
		allVariables.addAll(sprite2.getUserVariables());

		sprite3.getUserVariables().addAll(generateUserVariables("S3_Variable:", 3));
		allVariables.addAll(sprite3.getUserVariables());

		project.getUserVariables().addAll(generateUserVariables("S3_Variable:", 3));
		allVariables.addAll(project.getUserVariables());

		accessor = new DeviceVariableAccessor(directory);
		HashMap<UUID, Object> map = new HashMap<>();

		for (UserVariable userVariable: allVariables)	{
			map.put(userVariable.getDeviceValueKey(), userVariable.getValue());
		}

		accessor.writeMapToJson(map);
	}

	private Project createProject() {
		Project project = new Project();
		directory = new File(InstrumentationRegistry.getTargetContext().getCacheDir(), "DeviceValues");
		directory.mkdir();
		project.setDirectory(directory);
		project.setName("__project__");

		sprite1 = new Sprite("__sprite1__");
		sprite2 = new Sprite("__sprite2__");
		sprite3 = new Sprite("__sprite3__");

		scene1 = new Scene();
		scene1.addSprite(sprite1);
		scene1.addSprite(sprite2);

		scene2 = new Scene();
		scene2.addSprite(sprite3);

		project.addScene(scene1);
		project.addScene(scene2);
		return project;
	}

	private ArrayList<UserVariable> generateUserVariables(String prefix, int number) {
		ArrayList userVariables = new ArrayList();
		for (int index = 0; index < number; index++) {
			userVariables.add(new UserVariable(prefix + index, (double) index));
		}
		return userVariables;
	}

	@Test
	public void deleteGlobalVariablesTest() {
		project.getUserVariables().clear();
		accessor.cleanUpDeletedVariables(project);
		HashMap<UUID, Object> map = accessor.readMapFromJson();
		for (UserVariable userVariable : project.getUserVariables()) {
			assertFalse(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite1.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite2.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite3.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
	}

	@Test
	public void deleteSceneVariablesTest() {
		project.removeScene(scene1);
		accessor.cleanUpDeletedVariables(project);
		HashMap<UUID, Object> map = accessor.readMapFromJson();
		for (UserVariable userVariable : project.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite1.getUserVariables()) {
			assertFalse(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite2.getUserVariables()) {
			assertFalse(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite3.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
	}

	@Test
	public void deleteSpriteVariablesTest() {
		sprite1.getUserVariables().clear();
		accessor.cleanUpDeletedVariables(project);
		HashMap<UUID, Object> map = accessor.readMapFromJson();
		for (UserVariable userVariable : project.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite1.getUserVariables()) {
			assertFalse(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite2.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
		for (UserVariable userVariable : sprite3.getUserVariables()) {
			assertTrue(map.containsKey(userVariable.getDeviceValueKey()));
		}
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
	}
}

