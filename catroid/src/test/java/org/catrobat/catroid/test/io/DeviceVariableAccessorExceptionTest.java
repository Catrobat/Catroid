/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class DeviceVariableAccessorExceptionTest {

	private File variableFile;
	private DeviceVariableAccessor deviceVariableAccessor;

	private MockedConstruction<Gson> gsonMock;

	@Before
	public void setUp() {
		deviceVariableAccessor = new DeviceVariableAccessor(new File("a"));
		variableFile = Mockito.mock(File.class);
		when(variableFile.exists()).thenReturn(true);
		when(variableFile.getPath()).thenReturn("dummy.json");
		deviceVariableAccessor.setDeviceFile(variableFile);

		gsonMock = Mockito.mockConstruction(Gson.class, (mock, context) ->
			when(mock.fromJson(anyString(), any()))
					.thenThrow(new RuntimeException(new FileNotFoundException()))
		);
	}

	@After
	public void tearDown() {
		gsonMock.close();
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
