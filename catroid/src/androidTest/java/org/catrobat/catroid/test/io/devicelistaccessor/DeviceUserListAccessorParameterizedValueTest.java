/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.io.DeviceListAccessor;
import org.catrobat.catroid.io.DeviceUserDataAccessor;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.koin.core.module.Module;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(Parameterized.class)
public class DeviceUserListAccessorParameterizedValueTest<T> {

	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Double", Arrays.asList(12.123)},
				{"Negative Double", Arrays.asList(-2312.123)},
				{"String", Arrays.asList("initial String Value")},
				{"Empty String", Arrays.asList("")},
				{"String special char", Arrays.asList("{}\\%&($%(/(/§")},
				{"Boolean", Arrays.asList(true)},
				{"Empty", new ArrayList<>()}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public List<Object> initialValue;

	private List<Object> throwAwayValue = Arrays.asList("Throw Away Value");
	private File directory;
	private UserList userList;
	private DeviceUserDataAccessor accessor;

	@Before
	public void setUp() {
		Context contextMock = Mockito.mock(Context.class);
		CatroidKoinHelperKt.startWithContext(contextMock, dependencyModules);
		directory = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "DeviceLists");
		directory.mkdir();

		userList = new UserList("globalListX", initialValue);
		accessor = new DeviceListAccessor(directory);
	}

	@Test
	public void saveUserList() {
		accessor.writeUserData(userList);
		userList.setValue(throwAwayValue);
		Map map = accessor.readMapFromJson();
		assertEquals(initialValue, map.get(userList.getDeviceKey()));
	}

	@Test
	public void loadUserListTest() {
		Map map = new HashMap<>();
		map.put(userList.getDeviceKey(), userList.getValue());
		accessor.writeMapToJson(map);
		userList.setValue(throwAwayValue);
		assertNotEquals(initialValue, userList.getValue());
		assertTrue(accessor.readUserData(userList));
	}

	@Test
	public void deleteUserListTest() {
		Map<UUID, Object> map = new HashMap<>();
		map.put(userList.getDeviceKey(), initialValue);
		accessor.writeMapToJson(map);
		accessor.removeDeviceValue(userList);
		map = accessor.readMapFromJson();
		assertFalse(map.containsKey(userList.getDeviceKey()));
	}

	@Test
	public void cloneSpriteTest() {
		Project dummyProject = new Project();
		Scene dummyScene = new Scene();
		dummyProject.addScene(dummyScene);
		inject(ProjectManager.class).getValue().setCurrentProject(dummyProject);

		Sprite sprite = new Sprite("sprite");
		sprite.addUserList(userList);

		Sprite clone = new SpriteController().copyForCloneBrick(sprite);
		accessor.writeUserData(userList);
		UserList clonedList = clone.getUserList(userList.getName());
		assertNotSame(userList, clonedList);
		assertEquals(userList.getValue(), clonedList.getValue());
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
		CatroidKoinHelperKt.stop(dependencyModules);
	}
}
