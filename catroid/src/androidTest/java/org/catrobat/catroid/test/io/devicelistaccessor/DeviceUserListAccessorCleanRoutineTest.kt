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
package org.catrobat.catroid.test.io.devicelistaccessor;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.io.DeviceListAccessor;
import org.catrobat.catroid.io.DeviceUserDataAccessor;
import org.catrobat.catroid.io.StorageOperations;
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
import java.util.UUID;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeviceUserListAccessorCleanRoutineTest {
	private Project project;
	private File directory;
	private Scene scene1;
	private Scene scene2;
	private Sprite sprite1;
	private Sprite sprite2;
	private UserList sprite1UserList = new UserList("Sprite1_List");
	private UserList sprite2UserList = new UserList("Sprite2_List");
	private UserList globalUserList = new UserList("Global_List");
	private DeviceUserDataAccessor accessor;

	@Before
	public void setUp() throws IOException {
		project = createProject();

		ArrayList<UserList> allLists = new ArrayList<>();

		sprite1.getUserLists().add(sprite1UserList);
		allLists.addAll(sprite1.getUserLists());

		sprite2.getUserLists().add(sprite2UserList);
		allLists.addAll(sprite2.getUserLists());

		project.getUserLists().add(globalUserList);
		allLists.addAll(project.getUserLists());

		accessor = new DeviceListAccessor(directory);
		Map<UUID, List<Object>> map = new HashMap<>();

		for (UserList userList : allLists) {
			map.put(userList.getDeviceKey(), userList.getValue());
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
	public void deleteGlobalListsTest() {
		project.getUserLists().clear();
		accessor.cleanUpDeletedUserData(project);
		Map map = accessor.readMapFromJson();
		assertFalse(map.containsKey(globalUserList.getDeviceKey()));
		assertTrue(map.containsKey(sprite1UserList.getDeviceKey()));
		assertTrue(map.containsKey(sprite2UserList.getDeviceKey()));
	}

	@Test
	public void deleteSceneListsTest() {
		project.removeScene(scene1);
		accessor.cleanUpDeletedUserData(project);
		Map map = accessor.readMapFromJson();
		assertTrue(map.containsKey(globalUserList.getDeviceKey()));
		assertFalse(map.containsKey(sprite1UserList.getDeviceKey()));
		assertTrue(map.containsKey(sprite2UserList.getDeviceKey()));
	}

	@Test
	public void deleteSpriteListsTest() {
		sprite2.getUserLists().clear();
		accessor.cleanUpDeletedUserData(project);
		Map map = accessor.readMapFromJson();
		assertTrue(map.containsKey(globalUserList.getDeviceKey()));
		assertTrue(map.containsKey(sprite1UserList.getDeviceKey()));
		assertFalse(map.containsKey(sprite2UserList.getDeviceKey()));
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
	}
}
