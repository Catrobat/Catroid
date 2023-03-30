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
import org.catrobat.catroid.io.DeviceUserDataAccessor
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.io.DeviceListAccessor
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import junit.framework.TestCase
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.junit.After
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DeviceUserListAccessorCleanRoutineTest {
    private var project: Project? = null
    private var directory: File? = null
    private var scene1: Scene? = null
    private var scene2: Scene? = null
    private var sprite1: Sprite? = null
    private var sprite2: Sprite? = null
    private val sprite1UserList = UserList("Sprite1_List")
    private val sprite2UserList = UserList("Sprite2_List")
    private val globalUserList = UserList("Global_List")
    private var accessor: DeviceUserDataAccessor? = null
    @Before
    @Throws(IOException::class)
    fun setUp() {
        project = createProject()
        val allLists = ArrayList<UserList>()
        sprite1!!.userLists.add(sprite1UserList)
        allLists.addAll(sprite1!!.userLists)
        sprite2!!.userLists.add(sprite2UserList)
        allLists.addAll(sprite2!!.userLists)
        project!!.userLists.add(globalUserList)
        allLists.addAll(project!!.userLists)
        accessor = DeviceListAccessor(directory)
        val map: MutableMap<UUID?, List<Any>?> = HashMap()
        for (userList in allLists) {
            map[userList.deviceKey] = userList.value
        }
        (accessor as DeviceListAccessor).writeMapToJson(map)
    }

    private fun createProject(): Project {
        val project = Project()
        directory =
            File(ApplicationProvider.getApplicationContext<Context>().cacheDir, "DeviceValues")
        directory!!.mkdir()
        project.directory = directory
        project.name = "__project__"
        sprite1 = Sprite("__sprite1__")
        sprite2 = Sprite("__sprite3__")
        scene1 = Scene()
        scene1!!.addSprite(sprite1)
        scene2 = Scene()
        scene2!!.addSprite(sprite2)
        project.addScene(scene1)
        project.addScene(scene2)
        return project
    }

    @Test
    fun deleteGlobalListsTest() {
        project!!.userLists.clear()
        accessor!!.cleanUpDeletedUserData(project)
        val map: Map<*, *> = accessor!!.readMapFromJson()
        Assert.assertFalse(map.containsKey(globalUserList.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite1UserList.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite2UserList.deviceKey))
    }

    @Test
    fun deleteSceneListsTest() {
        project!!.removeScene(scene1)
        accessor!!.cleanUpDeletedUserData(project)
        val map: Map<*, *> = accessor!!.readMapFromJson()
        TestCase.assertTrue(map.containsKey(globalUserList.deviceKey))
        Assert.assertFalse(map.containsKey(sprite1UserList.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite2UserList.deviceKey))
    }

    @Test
    fun deleteSpriteListsTest() {
        sprite2!!.userLists.clear()
        accessor!!.cleanUpDeletedUserData(project)
        val map: Map<*, *> = accessor!!.readMapFromJson()
        TestCase.assertTrue(map.containsKey(globalUserList.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite1UserList.deviceKey))
        Assert.assertFalse(map.containsKey(sprite2UserList.deviceKey))
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        StorageOperations.deleteDir(directory)
    }
}
