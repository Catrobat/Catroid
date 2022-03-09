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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceUserDataAccessor;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(Parameterized.class)
public class DeviceVariableAccessorParameterizedValueTest<T> {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Double", Double.class, 12.123},
				{"Negative Double", Double.class, -2312.123},
				{"String", String.class, "initial String Value"},
				{"Empty String", String.class, ""},
				{"String special char", String.class, "{}\\%&($%(/(/§"},
				{"Boolean", Boolean.class, true},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<T> clazz;

	@Parameterized.Parameter(2)
	public T initialValue;

	private Object throwAwayValue = new Object();
	private File directory;
	private UserVariable userVariable;
	private DeviceUserDataAccessor accessor;

	@Before
	public void setUp() {
		directory = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "DeviceValues");
		directory.mkdir();

		userVariable = new UserVariable("globalVarX", initialValue);
		accessor = new DeviceVariableAccessor(directory);
	}

	@Test
	public void saveUserVariable() {
		accessor.writeUserData(userVariable);
		userVariable.setValue(throwAwayValue);
		Map map = accessor.readMapFromJson();
		Object variableValueFromFile = map.get(userVariable.getDeviceKey());
		assertEquals(initialValue, variableValueFromFile);
	}

	@Test
	public void loadUserVariableTest() {
		Map map = new HashMap<>();
		map.put(userVariable.getDeviceKey(), initialValue);
		accessor.writeMapToJson(map);
		userVariable.setValue(throwAwayValue);
		assertTrue(accessor.readUserData(userVariable));
		assertEquals(initialValue, userVariable.getValue());
	}

	@Test
	public void deleteUserVariableTest() {
		Map<UUID, Object> map = new HashMap<>();
		map.put(userVariable.getDeviceKey(), initialValue);
		accessor.writeMapToJson(map);
		accessor.removeDeviceValue(userVariable);
		map = accessor.readMapFromJson();
		assertFalse(map.containsKey(userVariable.getDeviceKey()));
	}

	@Test
	public void cloneSpriteTest() {
		Project dummyProject = new Project();
		Scene dummyScene = new Scene();
		dummyProject.addScene(dummyScene);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(dummyProject);

		Sprite sprite = new Sprite("sprite");
		sprite.addUserVariable(userVariable);

		Sprite clone = new SpriteController().copyForCloneBrick(sprite);
		accessor.writeUserData(userVariable);
		UserVariable clonedVar = clone.getUserVariable(userVariable.getName());
		assertNotSame(userVariable, clonedVar);
		clonedVar.setValue(throwAwayValue);
		assertTrue(accessor.readUserData(clonedVar));
		assertEquals(userVariable.getValue(), clonedVar.getValue());
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
	}
}
