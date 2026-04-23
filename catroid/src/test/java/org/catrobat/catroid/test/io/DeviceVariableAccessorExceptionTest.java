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
package org.catrobat.catroid.test.io;

import com.google.gson.Gson;

import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.DeviceVariableAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DeviceVariableAccessor.class})
public class DeviceVariableAccessorExceptionTest {

	private File variableFile;
	private DeviceVariableAccessor deviceVariableAccessor;

	@Before
	public void setUp() throws Exception {
		deviceVariableAccessor = new DeviceVariableAccessor(new File("a"));
		variableFile = Mockito.mock(File.class);
		Mockito.when(variableFile.exists()).thenReturn(true);
		deviceVariableAccessor.setDeviceFile(variableFile);
		PowerMockito.whenNew(Gson.class).withAnyArguments().thenThrow(new FileNotFoundException());
	}

	@Test
	public void deleteCorruptedFileOnReadTest() {
		deviceVariableAccessor.readMapFromJson();
		Mockito.verify(variableFile, times(1)).delete();
	}

	@Test
	public void setVariableValue0OnFailedReadTest() {
		UserVariable userVariable = new UserVariable("Variable", new Object());
		deviceVariableAccessor.readUserData(userVariable);
		assertEquals(0.0, userVariable.getValue());
	}
}
