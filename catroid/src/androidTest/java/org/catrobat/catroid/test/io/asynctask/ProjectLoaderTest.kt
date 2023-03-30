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
package org.catrobat.catroid.test.io.asynctask

import org.catrobat.catroid.io.asynctask.loadProject
import org.junit.runner.RunWith
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.io.DeviceVariableAccessor
import org.catrobat.catroid.io.DeviceUserDataAccessor
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.io.XstreamSerializer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.DeviceListAccessor
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.Objects
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ProjectLoaderTest {
    private val projectName = "testProject"
    private var project: Project? = null
    private var directory: File? = null
    private var scene1: Scene? = null
    private var scene2: Scene? = null
    private var sprite1: Sprite? = null
    private var sprite2: Sprite? = null
    private val sprite1UserVariable = UserVariable("Sprite1_Variable", 0)
    private val sprite2UserVariable = UserVariable("Sprite2_Variable", 0)
    private val globalUserVariable = UserVariable("Global_Variable", 0)
    private val multiplayerUserVariable = UserVariable("Multiplayer_Variable", 0)
    private val sprite1UserList = UserList("Sprite1_List")
    private val sprite2UserList = UserList("Sprite2_List")
    private val globalUserList = UserList("Global_List")
    private var variableAccessor: DeviceVariableAccessor? = null
    private var userDataAccessor: DeviceUserDataAccessor? = null
    private lateinit var correctLooks: Array<File>
    @Before
    @Throws(IOException::class)
    fun setUp() {
        project = createProject()
        Assert.assertTrue(XstreamSerializer.getInstance().saveProject(project))
        setUpVariables()
        setUpUserLists()
        setUpLooks()
        Assert.assertTrue(XstreamSerializer.getInstance().saveProject(project))
    }

    @Throws(IOException::class)
    private fun createProject(): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        directory = project.directory
        sprite1 = Sprite("__sprite1__")
        sprite2 = Sprite("__sprite3__")
        scene1 = Scene("__scene1__", project)
        scene1!!.addSprite(sprite1)
        scene2 = Scene("__scene2__", project)
        scene2!!.addSprite(sprite2)
        project.addScene(scene1)
        project.addScene(scene2)
        return project
    }

    private fun setUpVariables() {
        sprite1!!.userVariables.add(sprite1UserVariable)
        val allVariables = ArrayList(
            sprite1!!.userVariables
        )
        sprite2!!.userVariables.add(sprite2UserVariable)
        allVariables.addAll(sprite2!!.userVariables)
        project!!.userVariables.add(globalUserVariable)
        allVariables.addAll(project!!.userVariables)
        project!!.multiplayerVariables.add(multiplayerUserVariable)
        allVariables.addAll(project!!.multiplayerVariables)
        variableAccessor = DeviceVariableAccessor(directory)
        val variablesMap: MutableMap<UUID?, Any?> = HashMap()
        for (userVariable in allVariables) {
            variablesMap[userVariable.deviceKey] = userVariable.value
        }
        variableAccessor!!.writeMapToJson(variablesMap)
    }

    private fun setUpUserLists() {
        sprite1!!.userLists.add(sprite1UserList)
        val allLists = ArrayList(sprite1!!.userLists)
        sprite2!!.userLists.add(sprite2UserList)
        allLists.addAll(sprite2!!.userLists)
        project!!.userLists.add(globalUserList)
        allLists.addAll(project!!.userLists)
        userDataAccessor = DeviceListAccessor(directory)
        val map: MutableMap<UUID?, List<Any>?> = HashMap()
        for (userList in allLists) {
            map[userList.deviceKey] = userList.value
        }
        (userDataAccessor as DeviceListAccessor).writeMapToJson(map)
    }

    @Throws(IOException::class)
    private fun setUpLooks() {
        addLookToSprite(sprite1, scene1, "Valid look1")
        addLookToSprite(sprite1, scene1, "Valid look2")
        addUnusedLookToSprite(scene1, "Unused look1")
        addUnusedLookToSprite(scene1, "Unused look2")
    }

    @Throws(IOException::class)
    private fun addLookToSprite(sprite: Sprite?, scene: Scene?, name: String) {
        val imageDirectory = File(scene!!.directory, Constants.IMAGE_DIRECTORY_NAME)
        val lookDataFile = File(imageDirectory, name)
        lookDataFile.createNewFile()
        val lookData = LookData(name, lookDataFile)
        sprite!!.lookList.add(lookData)
        sprite.look.lookData = sprite.lookList.stream().findFirst().get()
        correctLooks = imageDirectory.listFiles()
    }

    @Throws(IOException::class)
    private fun addUnusedLookToSprite(scene: Scene?, name: String) {
        val imageDirectory = File(scene!!.directory, Constants.IMAGE_DIRECTORY_NAME)
        val lookDataFile = File(imageDirectory, name)
        lookDataFile.createNewFile()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
    }

    @Test
    @Throws(IOException::class)
    fun projectLoadTaskTest() {
        // Delete User Variables
        project!!.userVariables.clear()
        sprite1!!.userVariables.clear()
        project!!.removeScene(scene2)
        project!!.multiplayerVariables.clear()
        //Delete User Lists
        project!!.userLists.clear()
        sprite1!!.userLists.clear()
        // Check Look Count (2 used, 2 unused, 1 nomediaOffset)
        val imageDirectoryPre = File(scene1!!.directory, Constants.IMAGE_DIRECTORY_NAME)
        Assert.assertEquals(
            (2 + 2 + 1).toLong(),
            Objects.requireNonNull(imageDirectoryPre.listFiles()).size.toLong()
        )

        //save changes of project
        Assert.assertTrue(XstreamSerializer.getInstance().saveProject(project))
        Assert.assertNotNull(directory)
        Assert.assertTrue(loadProject(directory, ApplicationProvider.getApplicationContext()))

        // Check if User Variables are removed
        val variableMap = variableAccessor!!.readMapFromJson()
        Assert.assertFalse(variableMap.containsKey(globalUserVariable.deviceKey))
        Assert.assertFalse(variableMap.containsKey(multiplayerUserVariable.deviceKey))
        Assert.assertFalse(variableMap.containsKey(sprite1UserVariable.deviceKey))
        Assert.assertFalse(variableMap.containsKey(sprite2UserVariable.deviceKey))
        // Check if User Lists are removed
        val listMap = userDataAccessor!!.readMapFromJson()
        Assert.assertFalse(listMap.containsKey(globalUserList.deviceKey))
        Assert.assertFalse(listMap.containsKey(sprite1UserList.deviceKey))
        Assert.assertFalse(listMap.containsKey(sprite2UserList.deviceKey))
        // Check if Looks are removed and only correct ones remain
        val imageDirectoryPost = File(scene1!!.directory, Constants.IMAGE_DIRECTORY_NAME)
        Assert.assertArrayEquals(correctLooks, imageDirectoryPost.listFiles())
    }

    @Test
    @Throws(IOException::class)
    fun projectInvalidLoadTaskTest() {
        val directory = File("")
        Assert.assertNotNull(directory)
        Assert.assertFalse(loadProject(directory, ApplicationProvider.getApplicationContext()))
    }
}
