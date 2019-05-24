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
package org.catrobat.catroid.test.io.devicevariableaccessor;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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
import java.util.HashMap;

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DeviceVariableAccessorNullValueTest {

	public Object initialValue = null;

	private Object throwAwayValue = new Object();
	private File directory;
	private UserVariable userVariable;
	private UserVariable localUserVariable;
	private Sprite sprite;
	private DeviceVariableAccessor accessor;

	@Before
	public void setUp() {
		directory = new File(InstrumentationRegistry.getTargetContext().getCacheDir(), "DeviceValues");
		directory.mkdir();

		sprite = new Sprite("_sprite_");
		userVariable = new UserVariable("globalVarX", initialValue);
		localUserVariable = new UserVariable("localVarY", initialValue);
		sprite.addUserVariable(localUserVariable);
		accessor = new DeviceVariableAccessor(directory);
	}

	@Test
	public void saveLocalVariable() throws IOException {
		accessor.writeVariable(localUserVariable);
		localUserVariable.setValue(throwAwayValue);
		HashMap map = accessor.readMapFromJson();
		Object variableValueFromFile = map.get(localUserVariable.getDeviceValueKey());
		assertEquals(initialValue, variableValueFromFile);
	}

	@Test
	public void saveGlobalVariable() throws IOException {
		accessor.writeVariable(userVariable);
		userVariable.setValue(throwAwayValue);
		HashMap map = accessor.readMapFromJson();
		Object variableValueFromFile = map.get(userVariable.getDeviceValueKey());
		assertEquals(initialValue, variableValueFromFile);
	}

	@Test
	public void loadLocalUserVariableTest() throws IOException {
		HashMap map = new HashMap<>();
		map.put(localUserVariable.getDeviceValueKey(), initialValue);
		accessor.writeMapToJson(map);
		assertFalse(accessor.readUserVariableValue(localUserVariable));
		assertEquals(initialValue, localUserVariable.getValue());
	}

	@Test
	public void loadGlobalUserVariableTest() throws IOException {
		HashMap map = new HashMap<>();
		map.put(userVariable.getDeviceValueKey(), initialValue);
		accessor.writeMapToJson(map);
		assertFalse(accessor.readUserVariableValue(userVariable));
		assertEquals(initialValue, userVariable.getValue());
	}

	@After
	public void tearDown() throws IOException {
		StorageOperations.deleteDir(directory);
	}
}
