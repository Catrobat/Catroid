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
import junit.framework.Assert
import junit.framework.TestCase
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.DeviceVariableAccessor
import org.catrobat.catroid.io.StorageOperations
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DeviceVariableAccessorCleanRoutineTest {
    private lateinit var project: Project
    private lateinit var directory: File
    private lateinit var scene1: Scene
    private lateinit var scene2: Scene
    private lateinit var sprite1: Sprite
    private lateinit var sprite2: Sprite
    private val sprite1UserVariable = UserVariable("Sprite1_Variable", 0)
    private val sprite2UserVariable = UserVariable("Sprite2_Variable", 0)
    private val globalUserVariable = UserVariable("Global_Variable", 0)
    private val multiplayerUserVariable = UserVariable("Multiplayer_Variable", 0)
    private lateinit var accessor: DeviceVariableAccessor
    @Before
    @Throws(IOException::class)
    fun setUp() {
        project = createProject()
        val allVariables = ArrayList<UserVariable>()
        sprite1.userVariables.add(sprite1UserVariable)
        allVariables.addAll(sprite1.userVariables)
        sprite2.userVariables.add(sprite2UserVariable)
        allVariables.addAll(sprite2.userVariables)
        project.userVariables.add(globalUserVariable)
        allVariables.addAll(project.userVariables)
        project.multiplayerVariables.add(multiplayerUserVariable)
        allVariables.addAll(project.multiplayerVariables)
        accessor = DeviceVariableAccessor(directory)
        val map: MutableMap<UUID?, Any?> = HashMap()
        for (userVariable in allVariables) {
            map[userVariable.deviceKey] = userVariable.value
        }
        accessor.writeMapToJson(map)
    }

    private fun createProject(): Project {
        val project = Project()
        directory =
            File(ApplicationProvider.getApplicationContext<Context>().cacheDir, "DeviceValues")
        directory.mkdir()
        project.directory = directory
        project.name = "__project__"
        sprite1 = Sprite("__sprite1__")
        sprite2 = Sprite("__sprite3__")
        scene1 = Scene()
        scene1.addSprite(sprite1)
        scene2 = Scene()
        scene2.addSprite(sprite2)
        project.addScene(scene1)
        project.addScene(scene2)
        return project
    }

    @Test
    fun deleteGlobalVariablesTest() {
        project.userVariables.clear()
        accessor.cleanUpDeletedUserData(project)
        val map = accessor.readMapFromJson()
        Assert.assertFalse(map.containsKey(globalUserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(multiplayerUserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite1UserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite2UserVariable.deviceKey))
    }

    @Test
    fun deleteSceneVariablesTest() {
        project.removeScene(scene1)
        accessor.cleanUpDeletedUserData(project)
        val map = accessor.readMapFromJson()
        TestCase.assertTrue(map.containsKey(globalUserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(multiplayerUserVariable.deviceKey))
        Assert.assertFalse(map.containsKey(sprite1UserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite2UserVariable.deviceKey))
    }

    @Test
    fun deleteSpriteVariablesTest() {
        sprite2.userVariables.clear()
        accessor.cleanUpDeletedUserData(project)
        val map = accessor.readMapFromJson()
        TestCase.assertTrue(map.containsKey(globalUserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(multiplayerUserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite1UserVariable.deviceKey))
        Assert.assertFalse(map.containsKey(sprite2UserVariable.deviceKey))
    }

    @Test
    fun deleteMultiplayerVariablesTest() {
        project.multiplayerVariables.clear()
        accessor.cleanUpDeletedUserData(project)
        val map = accessor.readMapFromJson()
        TestCase.assertTrue(map.containsKey(globalUserVariable.deviceKey))
        Assert.assertFalse(map.containsKey(multiplayerUserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite1UserVariable.deviceKey))
        TestCase.assertTrue(map.containsKey(sprite2UserVariable.deviceKey))
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        StorageOperations.deleteDir(directory)
    }
}