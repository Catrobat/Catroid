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
package org.catrobat.catroid.test.io

import com.google.gson.Gson
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.DeviceVariableAccessor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.File
import java.io.FileNotFoundException

@RunWith(PowerMockRunner::class)
@PrepareForTest(DeviceVariableAccessor::class)
class DeviceVariableAccessorExceptionTest {
    private var variableFile: File? = null
    private var deviceVariableAccessor: DeviceVariableAccessor? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        deviceVariableAccessor = DeviceVariableAccessor(File("a"))
        variableFile = Mockito.mock(File::class.java)
        Mockito.`when`(variableFile?.exists()).thenReturn(true)
        deviceVariableAccessor!!.setDeviceFile(variableFile)
        PowerMockito.whenNew(Gson::class.java).withAnyArguments().thenThrow(FileNotFoundException())
    }

    @Test
    fun deleteCorruptedFileOnReadTest() {
        deviceVariableAccessor!!.readMapFromJson()
        Mockito.verify(variableFile, Mockito.times(1))?.delete()
    }

    @Test
    fun setVariableValue0OnFailedReadTest() {
        val userVariable = UserVariable("Variable", Any())
        deviceVariableAccessor!!.readUserData(userVariable)
        Assert.assertEquals(0.0, userVariable.value)
    }
}