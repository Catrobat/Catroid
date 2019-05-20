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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeviceVariableAccessorTest {

	private File directory;
	private UserVariable userVariable;
	private UserVariable localUserVariable;
	private Sprite sprite;
	private DeviceVariableAccessor accessor;

	@Before
	public void setUp() {
		Project dummyProject = new Project();
		Scene dummyScene = new Scene();
		dummyProject.addScene(dummyScene);
		ProjectManager.getInstance().setCurrentProject(dummyProject);
		directory = new File(InstrumentationRegistry.getTargetContext().getCacheDir(), "DeviceValues");
		directory.mkdir();
		dummyProject.setDirectory(directory);

		sprite = new Sprite("_sprite_");
		userVariable = new UserVariable("X", 10.0);
		localUserVariable = new UserVariable("Y", 20.0);
		sprite.addUserVariable(localUserVariable);
		accessor = new DeviceVariableAccessor(directory);
	}

	@Test
	public void saveLocalVariable() throws IOException {
		Object localSavedVar = localUserVariable.getValue();
		accessor.writeVariable(localUserVariable);
		localUserVariable.setValue("false");
		HashMap map = accessor.readMapFromJson();
		assertEquals(map.get(localUserVariable.getDeviceValueKey()), localSavedVar);
	}

	@Test
	public void saveGlobalVariable() throws IOException {
		Object localSavedVar = userVariable.getValue();
		accessor.writeVariable(userVariable);
		userVariable.setValue("false");
		HashMap map = accessor.readMapFromJson();
		assertEquals(map.get(userVariable.getDeviceValueKey()), localSavedVar);
	}

	@Test
	public void loadLocalUserVariableTest() throws IOException {
		HashMap map = new HashMap<>();
		localUserVariable.setDeviceValueKey(UUID.randomUUID());
		map.put(localUserVariable.getDeviceValueKey(), "asdf");
		accessor.writeMapToJson(map);
		assertTrue(accessor.readUserVariableValue(localUserVariable));
		assertEquals("asdf", localUserVariable.getValue());
	}

	@Test
	public void loadGlobalUserVariableTest() throws IOException {
		HashMap map = new HashMap<>();
		userVariable.setDeviceValueKey(UUID.randomUUID());
		map.put(userVariable.getDeviceValueKey(), "asdf");
		accessor.writeMapToJson(map);
		assertTrue(accessor.readUserVariableValue(userVariable));
		assertEquals("asdf", userVariable.getValue());
	}

	@Test
	public void deleteLocaleVariableTest() throws IOException {
		accessor.writeVariable(localUserVariable);
		accessor.writeVariable(userVariable);
		accessor.deleteAllLocalVariables(sprite);

		assertFalse(accessor.readUserVariableValue(localUserVariable));
		assertTrue(accessor.readUserVariableValue(userVariable));
	}

	@Test
	public void deleteUserVariableTest() throws IOException {
		accessor.writeVariable(userVariable);
		assertTrue(accessor.readUserVariableValue(userVariable));
		accessor.removeDeviceValue(userVariable);
		assertFalse(accessor.readUserVariableValue(userVariable));
	}

	@Test
	public void cloneSpriteTest() throws IOException {
		Sprite clone = new SpriteController().copyForCloneBrick(sprite);

		accessor.writeVariable(localUserVariable);

		UserVariable clonedVar = clone.getUserVariable(localUserVariable.getName());
		clonedVar.setValue("false");
		assertTrue(accessor.readUserVariableValue(clonedVar));
		assertEquals(localUserVariable.getValue(), clonedVar.getValue());
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
	}
}

