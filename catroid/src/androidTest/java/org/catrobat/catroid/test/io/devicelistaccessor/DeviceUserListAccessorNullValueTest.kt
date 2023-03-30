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

import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.io.DeviceListAccessor;
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

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DeviceUserListAccessorNullValueTest {

	private List<Object> initialNullValue = null;
	private List<Object> expectedValue = new ArrayList<>();
	private List<Object> throwAwayValue = new ArrayList<>();
	private File directory;
	private UserList userList;
	private DeviceListAccessor accessor;

	@Before
	public void setUp() {
		directory = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "DeviceLists");
		directory.mkdir();
		userList = new UserList("UserList", initialNullValue);
		accessor = new DeviceListAccessor(directory);
	}

	@Test
	public void saveNullUserListTest() {
		accessor.writeUserData(userList);
		userList.setValue(throwAwayValue);
		Map map = accessor.readMapFromJson();
		Object listValueFromFile = map.get(userList.getDeviceKey());
		assertEquals(initialNullValue, listValueFromFile);
	}

	@Test
	public void loadNullUserListTest() {
		HashMap<UUID, Object> map = new HashMap<>();
		map.put(userList.getDeviceKey(), initialNullValue);
		accessor.writeMapToJson(map);
		userList.setValue(throwAwayValue);
		assertFalse(accessor.readUserData(userList));
		assertEquals(expectedValue, userList.getValue());
	}

	@Test
	public void loadUserListNoJsonFileTest() {
		userList.setValue(throwAwayValue);
		assertFalse(accessor.readUserData(userList));
		assertEquals(expectedValue, userList.getValue());
	}

	@Test
	public void loadUserListJsonFileDoesNotContainKeyTest() {
		HashMap<UUID, Object> map = new HashMap<>();
		map.put(UUID.randomUUID(), "value");
		accessor.writeMapToJson(map);
		userList.setValue(throwAwayValue);
		assertFalse(accessor.readUserData(userList));
		assertEquals(expectedValue, userList.getValue());
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
	}
}
