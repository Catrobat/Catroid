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
package org.catrobat.catroid.test.io.devicelistaccessor

import android.content.Context
import org.junit.runner.RunWith
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.io.DeviceListAccessor
import org.junit.Before
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.Throws
import org.catrobat.catroid.io.StorageOperations
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DeviceUserListAccessorNullValueTest {
    private val initialNullValue: List<Any>? = null
    private val expectedValue: List<Any> = ArrayList()
    private val throwAwayValue: List<Any> = ArrayList()
    private var directory: File? = null
    private var userList: UserList? = null
    private var accessor: DeviceListAccessor? = null
    @Before
    fun setUp() {
        directory =
            File(ApplicationProvider.getApplicationContext<Context>().cacheDir, "DeviceLists")
        directory!!.mkdir()
        userList = UserList("UserList", initialNullValue)
        accessor = DeviceListAccessor(directory)
    }

    @Test
    fun saveNullUserListTest() {
        accessor!!.writeUserData(userList)
        userList!!.value = throwAwayValue
        val map: Map<*, *> = accessor!!.readMapFromJson()
        val listValueFromFile = map[userList!!.deviceKey]
        Assert.assertEquals(initialNullValue, listValueFromFile)
    }

    @Test
    fun loadNullUserListTest() {
        val map = HashMap<UUID?, Any?>()
        map[userList!!.deviceKey] = initialNullValue
        accessor!!.writeMapToJson(map)
        userList!!.value = throwAwayValue
        junit.framework.Assert.assertFalse(accessor!!.readUserData(userList))
        Assert.assertEquals(expectedValue, userList!!.value)
    }

    @Test
    fun loadUserListNoJsonFileTest() {
        userList!!.value = throwAwayValue
        junit.framework.Assert.assertFalse(accessor!!.readUserData(userList))
        Assert.assertEquals(expectedValue, userList!!.value)
    }

    @Test
    fun loadUserListJsonFileDoesNotContainKeyTest() {
        val map = HashMap<UUID?, Any?>()
        map[UUID.randomUUID()] = "value"
        accessor!!.writeMapToJson(map)
        userList!!.value = throwAwayValue
        junit.framework.Assert.assertFalse(accessor!!.readUserData(userList))
        Assert.assertEquals(expectedValue, userList!!.value)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        StorageOperations.deleteDir(directory)
    }
}