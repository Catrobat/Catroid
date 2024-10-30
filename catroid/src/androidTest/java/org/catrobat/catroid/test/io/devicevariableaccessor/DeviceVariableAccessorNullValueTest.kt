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
package org.catrobat.catroid.test.io.devicevariableaccessor

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.DeviceVariableAccessor
import org.catrobat.catroid.io.StorageOperations
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DeviceVariableAccessorNullValueTest {
    private val initialNullValue: Any? = null
    private val expectedValue = 0.0
    private val throwAwayValue = Any()
    private lateinit var directory: File
    private lateinit var userVariable: UserVariable
    private lateinit var accessor: DeviceVariableAccessor
    @Before
    fun setUp() {
        directory =
            File(ApplicationProvider.getApplicationContext<Context>().cacheDir, "DeviceValues")
        directory.mkdir()
        userVariable = UserVariable("UserVariable", initialNullValue)
        accessor = DeviceVariableAccessor(directory)
    }

    @Test
    fun saveNullUserVariableTest() {
        accessor.writeUserData(userVariable)
        userVariable.value = throwAwayValue
        val map: Map<*, *> = accessor.readMapFromJson()
        val variableValueFromFile = map[userVariable.deviceKey]
        Assert.assertEquals(initialNullValue, variableValueFromFile)
    }

    @Test
    @Throws(IOException::class)
    fun loadNullUserVariableTest() {
        val map = HashMap<UUID?, Any?>()
        map[userVariable.deviceKey] = initialNullValue
        accessor.writeMapToJson(map)
        userVariable.value = throwAwayValue
        junit.framework.Assert.assertFalse(accessor.readUserData(userVariable))
        Assert.assertEquals(expectedValue, userVariable.value)
    }

    @Test
    fun loadUserVariableNoJsonFileTest() {
        userVariable.value = throwAwayValue
        junit.framework.Assert.assertFalse(accessor.readUserData(userVariable))
        Assert.assertEquals(expectedValue, userVariable.value)
    }

    @Test
    fun loadUserVariableJsonFileDoesNotContainKeyTest() {
        val map = HashMap<UUID?, Any?>()
        map[UUID.randomUUID()] = "value"
        accessor.writeMapToJson(map)
        userVariable.value = throwAwayValue
        junit.framework.Assert.assertFalse(accessor.readUserData(userVariable))
        Assert.assertEquals(expectedValue, userVariable.value)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        StorageOperations.deleteDir(directory)
    }
}