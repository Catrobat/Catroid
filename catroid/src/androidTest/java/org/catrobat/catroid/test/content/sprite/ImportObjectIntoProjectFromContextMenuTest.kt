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
package org.catrobat.catroid.test.content.sprite

import android.net.Uri
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.runner.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.merge.ImportProjectHelper
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.utils.Utils.checkForDuplicates
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent
import java.io.File

@RunWith(AndroidJUnit4::class)
class ImportObjectIntoProjectFromContextMenuTest {
    var project: Project? = null
    var importedProject: Project? = null
    var importName = "IMPORTED"
    var defaultProjectName = "defaultProject"
    var uri: Uri? = null
    var spriteToBeImported: Sprite? = null
    private lateinit var scriptForVisualPlacement: Script
    val TAG: String = ImportObjectIntoProjectTest::class.java.simpleName
    private val projectManager = KoinJavaComponent.inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule: FragmentActivityTestRule<ProjectActivity> = FragmentActivityTestRule(
        ProjectActivity::class.java,
        ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Throws(Exception::class)
    @Before
    fun setUp() {
        try {
            Constants.MEDIA_LIBRARY_CACHE_DIR.mkdirs()
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }

        project = DefaultProjectHandler
            .createAndSaveDefaultProject(
                defaultProjectName,
                ApplicationProvider.getApplicationContext(),
                false
            )

        importedProject = DefaultProjectHandler
            .createAndSaveDefaultProject(
                importName,
                ApplicationProvider.getApplicationContext(),
                false
            )

        initProjectVars()

        XstreamSerializer.getInstance().saveProject(importedProject)

        val projectZip = File(
            Constants.MEDIA_LIBRARY_CACHE_DIR,
            importedProject?.name + Constants.CATROBAT_EXTENSION
        )

        ZipArchiver().zip(projectZip, importedProject?.directory?.listFiles())
        uri = Uri.fromFile(projectZip)

        projectManager.value.currentProject = project
        projectManager.value.currentSprite = project!!.defaultScene.spriteList[1]
        Intents.init()
        baseActivityTestRule.launchActivity()
    }

    fun initProjectVars() {
        spriteToBeImported = importedProject!!.defaultScene.spriteList[1]

        spriteToBeImported!!.userVariables.add(UserVariable("localVariable1"))
        spriteToBeImported!!.userVariables.add(UserVariable("localVariable2"))
        spriteToBeImported!!.userLists.add(UserList("localList1"))
        spriteToBeImported!!.userLists.add(UserList("localList2"))

        importedProject!!.addUserVariable(UserVariable("globalVariable1", 1))
        importedProject!!.addUserVariable(UserVariable("globalVariable2", 2))
        importedProject!!.addUserList(UserList("globalListe1"))
        importedProject!!.addUserList(UserList("globalListe2"))

        scriptForVisualPlacement = StartScript().apply {
            addBrick(PlaceAtBrick(100, 200))
        }
    }

    @After
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(defaultProjectName, importName)
    }

    @Test
    fun importProjectWithoutConflictsTest() {
        val anySpriteOfProject = project!!.defaultScene.spriteList[1]

        anySpriteOfProject.userVariables.add(UserVariable("localVariable3"))
        anySpriteOfProject.userVariables.add(UserVariable("localVariable4"))
        anySpriteOfProject.userLists.add(UserList("localList3"))
        anySpriteOfProject.userLists.add(UserList("localList4"))

        project!!.addUserVariable(UserVariable("globalVariable3", 1))
        project!!.addUserVariable(UserVariable("globalVariable4", 2))
        project!!.addUserList(UserList("globalList3"))
        project!!.addUserList(UserList("globalList4"))
        XstreamSerializer.getInstance().saveProject(project)

        val oldLocalVariableList = anySpriteOfProject!!.userVariables.size
        val oldLocalListList = anySpriteOfProject.userLists.size
        val oldGlobalVariableList = project!!.userVariables.size
        val oldGlobalListList = project!!.userLists.size
        val oldLookList = anySpriteOfProject.lookList.size
        val oldSoundList = anySpriteOfProject.soundList.size
        val oldScriptList = anySpriteOfProject.scriptList.size

        val importedLocalVariableList = spriteToBeImported!!.userVariables.size
        val importedLocalListList = spriteToBeImported!!.userLists.size
        val importedGlobalVariableList = importedProject!!.userVariables.size
        val importedGlobalListList = importedProject!!.userLists.size
        val importedLookList = spriteToBeImported!!.lookList.size
        val importedSoundList = spriteToBeImported!!.soundList.size
        val importedScriptList = spriteToBeImported!!.scriptList.size

        val currentScene = project!!.defaultScene
        val activity = baseActivityTestRule.activity
        val resolvedFileName = StorageOperations.resolveFileName(activity.contentResolver, uri)
        val lookFileName: String? = resolvedFileName

        val importProjectHelper = ImportProjectHelper(
            lookFileName!!,
            currentScene, activity
        )

        Assert.assertTrue(importProjectHelper.checkForConflicts())

        importProjectHelper.addObjectDataToNewSprite(anySpriteOfProject)

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].userVariables.size,
            importedLocalVariableList + oldLocalVariableList
        )
        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].userLists.size,
            importedLocalListList + oldLocalListList
        )

        Assert.assertEquals(
            project!!.userLists.size,
            importedGlobalListList + oldGlobalListList
        )

        Assert.assertEquals(
            project!!.userVariables.size,
            importedGlobalVariableList + oldGlobalVariableList
        )

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].scriptList.size,
            importedScriptList + oldScriptList
        )

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].soundList.size,
            importedSoundList + oldSoundList
        )

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].lookList.size,
            importedLookList + oldLookList
        )
    }

    @Test
    fun importProjectWithConflictsTest() {
        val anySpriteOfProject = project!!.defaultScene.spriteList[1]

        anySpriteOfProject.userVariables.add(UserVariable("localVariable3"))
        anySpriteOfProject.userVariables.add(UserVariable("localVariable4"))
        anySpriteOfProject.userLists.add(UserList("localList3"))
        anySpriteOfProject.userLists.add(UserList("localList4"))

        project!!.addUserVariable(UserVariable("localVariable2", 1))
        project!!.addUserVariable(UserVariable("globalVariable4", 2))
        project!!.addUserList(UserList("globalList3"))
        project!!.addUserList(UserList("globalList4"))
        XstreamSerializer.getInstance().saveProject(project)

        val oldLocalVariableList = anySpriteOfProject!!.userVariables.size
        val oldLocalListList = anySpriteOfProject.userLists.size
        val oldGlobalVariableList = project!!.userVariables.size
        val oldGlobalListList = project!!.userLists.size
        val oldLookList = anySpriteOfProject.lookList.size
        val oldSoundList = anySpriteOfProject.soundList.size
        val oldScriptList = anySpriteOfProject.scriptList.size

        val currentScene = project!!.defaultScene
        val activity = baseActivityTestRule.activity
        val resolvedFileName = StorageOperations.resolveFileName(activity.getContentResolver(), uri)
        val lookFileName: String? = resolvedFileName

        val importProjectHelper = ImportProjectHelper(
            lookFileName!!,
            currentScene, activity
        )

        if (importProjectHelper.checkForConflicts()) {
            importProjectHelper.addObjectDataToNewSprite(anySpriteOfProject)
        }

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].userVariables.size,
            oldLocalVariableList
        )
        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].userLists.size,
            oldLocalListList
        )

        Assert.assertEquals(
            project!!.userLists.size,
            oldGlobalListList
        )

        Assert.assertEquals(
            project!!.userVariables.size,
            oldGlobalVariableList
        )

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].scriptList.size,
            oldScriptList
        )

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].soundList.size,
            oldSoundList
        )

        Assert.assertEquals(
            project!!.defaultScene.spriteList[1].lookList.size,
            oldLookList
        )
    }

    @Test
    fun uniqueLooksAndSoundsNamesImportedOnceTest() {
        val anySpriteOfProject = project!!.defaultScene.spriteList[1]
        val currentScene = project!!.defaultScene
        val activity = baseActivityTestRule.activity
        val resolvedFileName = StorageOperations.resolveFileName(activity.contentResolver, uri)
        val lookFileName: String? = resolvedFileName

        val importProjectHelper = ImportProjectHelper(
            lookFileName!!,
            currentScene, activity
        )

        Assert.assertTrue(importProjectHelper.checkForConflicts())
        importProjectHelper.addObjectDataToNewSprite(anySpriteOfProject)
        Assert.assertFalse(checkForDuplicates(anySpriteOfProject.lookList as List<Any>?))
        Assert.assertFalse(checkForDuplicates(anySpriteOfProject.soundList as List<Any>?))
    }
}
