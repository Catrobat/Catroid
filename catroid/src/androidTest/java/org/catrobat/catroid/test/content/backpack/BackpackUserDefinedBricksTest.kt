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

package org.catrobat.catroid.test.content.backpack

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class BackpackUserDefinedBricksTest {
    private val backpackGroupName: String = "group without definition"
    private lateinit var firstSprite: Sprite
    private lateinit var secondSprite: Sprite
    private lateinit var userDefinedBrick: UserDefinedBrick
    private lateinit var userDefinedScript: UserDefinedScript
    private lateinit var startScript: Script
    private var backpackListManager: BackpackListManager? = null
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    @Before
    fun setUp() {
        backpackListManager = BackpackListManager.getInstance()
        TestUtils.clearBackPack(backpackListManager)
        createProject(BackpackUserDefinedBricksTest::class.java.simpleName)
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(BackpackUserDefinedBricksTest::class.java.simpleName)
        TestUtils.clearBackPack(backpackListManager)
    }

    @Test
    fun testCopyingOfUserDefinedBrickWithoutDefinition() {
        val scriptController = ScriptController()
        val bricksToPack: ArrayList<Brick> = ArrayList()
        startScript.addToFlatList(bricksToPack)
        scriptController.pack(backpackGroupName, bricksToPack)
        val scripts = BackpackListManager.getInstance().backpackedScripts[backpackGroupName]
        scripts?.forEach {
            scriptController.unpack(it, secondSprite)
        }

        Assert.assertNotNull(secondSprite.getUserDefinedScript(userDefinedBrick.userDefinedBrickID))
    }

    @Test
    fun testCopyingOfUserDefinedBrickWithDefinition() {
        val scriptController = ScriptController()
        val bricksToPack: ArrayList<Brick> = ArrayList()
        startScript.addToFlatList(bricksToPack)
        userDefinedScript.addToFlatList(bricksToPack)
        scriptController.pack(backpackGroupName, bricksToPack)
        val scripts = BackpackListManager.getInstance().backpackedScripts[backpackGroupName]
        scripts?.forEach {
            scriptController.unpack(it, secondSprite)
        }

        Assert.assertNotNull(secondSprite.getUserDefinedScript(userDefinedBrick.userDefinedBrickID))
        Assert.assertEquals(2, secondSprite.scriptList.size)
    }

    private fun createProject(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        firstSprite = Sprite("firstSprite")
        secondSprite = Sprite("secondSprite")

        startScript = StartScript()
        userDefinedBrick = UserDefinedBrick()
        userDefinedScript = UserDefinedScript(userDefinedBrick.userDefinedBrickID)
        userDefinedScript.scriptBrick = UserDefinedReceiverBrick(userDefinedBrick)
        userDefinedScript.addBrick(SetXBrick())
        firstSprite.addScript(userDefinedScript)
        firstSprite.addUserDefinedBrick(userDefinedBrick)

        startScript.addBrick(userDefinedBrick)
        firstSprite.addScript(startScript)
        project.defaultScene.addSprite(firstSprite)
        project.defaultScene.addSprite(secondSprite)

        projectManager.currentProject = project
        projectManager.currentSprite = firstSprite
    }
}
