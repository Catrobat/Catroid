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

package org.catrobat.catroid.uiespresso.ui.dialog

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.catrobat.catroid.utils.Utils.checkForDuplicates
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class MergeLocalSpriteTest {
    lateinit var project: Project
    lateinit var sameGlobalsProject: Project
    lateinit var conflictProject: Project
    var sameGlobalsName = "sameGlobals"
    var defaultProjectName = "defaultProject"
    var conflictProjectName = "conflictProject"
    val controller = SpriteController()
    lateinit var originalSprite: Sprite
    lateinit var originalSpriteCopy: Sprite
    private val projectManager = inject(
        ProjectManager::class.java
    )

    @get:Rule
    var activityTestRule = BaseActivityTestRule(
        ProjectActivity::class.java, true, false
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        project = DefaultProjectHandler
            .createAndSaveDefaultProject(
                defaultProjectName,
                ApplicationProvider.getApplicationContext(),
                false
            )

        sameGlobalsProject = DefaultProjectHandler
            .createAndSaveDefaultProject(
                sameGlobalsName,
                ApplicationProvider.getApplicationContext(),
                false
            )

        conflictProject = DefaultProjectHandler
            .createAndSaveDefaultProject(
                conflictProjectName,
                ApplicationProvider.getApplicationContext(),
                false
            )

        initProjectVars()
        projectManager.value.currentProject = project
        projectManager.value.currentSprite = project.defaultScene.spriteList[1]
        originalSpriteCopy = controller.copy(project.defaultScene.spriteList[1], project, project
            .defaultScene)
        originalSpriteCopy.name = project.defaultScene.spriteList[1].name
        activityTestRule.launchActivity(null)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(defaultProjectName, sameGlobalsName, conflictProjectName)
    }

    @Test
    fun importObjectAndMergeGlobals() {
        UiTestUtils.openSpriteActionMenu(projectManager.value.currentSprite.name, false)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(
            allOf(
                withText(R.string.from_local),
                isDisplayed()))
            .perform(ViewActions.click())

        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(
            allOf(
                withText(sameGlobalsProject.name),
                isDisplayed()))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val sprite1: Sprite = sameGlobalsProject.defaultScene!!.spriteList!![1]
        val mergedSprite: Sprite = projectManager.value.currentProject.defaultScene.spriteList[1]

        assertMergedSprite(mergedSprite, sprite1, originalSpriteCopy)
        assertGlobalsMerged(sameGlobalsProject, projectManager.value.currentProject)
    }

    @Test
    fun abortImportWithConflicts() {
        UiTestUtils.openSpriteActionMenu(projectManager.value.currentSprite.name, false)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(allOf(withText(R.string.from_local), isDisplayed()))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(conflictProject.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        val currentSprite: Sprite = projectManager.value.currentProject.defaultScene!!.spriteList!![1]
        assertOriginalIntact(originalSprite, currentSprite)
    }

    @Test
    fun mergeSpriteTest() {
        mergeLocalSprite()
        val mergedSprite: Sprite = projectManager.value.currentProject.defaultScene.spriteList[1]
        assertMergedSprite(mergedSprite, originalSpriteCopy, originalSpriteCopy)
    }

    @Test
    fun mergeSameSpriteMultipleTimesTest() {
        mergeLocalSprite()
        mergeLocalSprite()
        mergeLocalSprite()
        val mergedSprite: Sprite = projectManager.value.currentProject.defaultScene.spriteList[1]
        Assert.assertFalse(checkForDuplicates(mergedSprite.lookList as List<Any>?))
        Assert.assertFalse(checkForDuplicates(mergedSprite.soundList as List<Any>?))
    }

    private fun mergeLocalSprite() {
        UiTestUtils.openSpriteActionMenu(projectManager.value.currentSprite.name, false)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(allOf(withText(R.string.from_local), isDisplayed()))
        Espresso.onView(withText(R.string.from_local))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(project.name))
            .perform(ViewActions.click())
    }

    @Throws(ProjectException::class)
    private fun initProjectVars() {
        project.defaultScene?.spriteList?.removeAt(2)
        project.defaultScene?.spriteList?.removeAt(1)
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())

        // Add conflicts to projects
        val local1 = UserVariable("local1")
        val conflictSprite = conflictProject.defaultScene?.spriteList?.get(1)
        conflictSprite?.userVariables?.add(local1)
        originalSprite = project.defaultScene?.spriteList?.get(1)!!
        originalSprite.userVariables?.add(local1)

        // Add same globals to projects
        val global1 = UserVariable("global1")
        val userListGlobal1 = UserList("userListGlobal1")

        project.userVariables?.add(global1)
        project.userLists?.add(userListGlobal1)
        saveProjectSerial(project, ApplicationProvider.getApplicationContext())

        sameGlobalsProject.userVariables?.add(global1)
        sameGlobalsProject.userLists?.add(userListGlobal1)
        saveProjectSerial(sameGlobalsProject, ApplicationProvider.getApplicationContext())
    }

    private fun assertMergedSprite(mergedSprite: Sprite, sprite1: Sprite, sprite2: Sprite) {
        val mergedScriptList = mergedSprite.scriptList
        val mergedSoundList = mergedSprite.soundList
        val mergedLookList = mergedSprite.lookList

        Assert.assertEquals(sprite1.lookList.size + sprite2.lookList.size,
                            mergedLookList.size)
        Assert.assertEquals(sprite1.scriptList.size + sprite2.scriptList.size,
                            mergedScriptList.size)
        Assert.assertEquals(sprite1.soundList.size + sprite2.soundList.size,
                            mergedSoundList.size)
    }

    private fun assertOriginalIntact(originalSprite: Sprite, currentSprite: Sprite) {
        Assert.assertEquals(originalSprite, currentSprite)
        Assert.assertEquals(originalSprite.soundList, currentSprite.soundList)
        Assert.assertEquals(originalSprite.scriptList, currentSprite.scriptList)
        Assert.assertEquals(originalSprite.lookList, currentSprite.lookList)
        Assert.assertEquals(originalSprite.userVariables, currentSprite.userVariables)
        Assert.assertEquals(originalSprite.userLists, currentSprite.userLists)
    }

    private fun assertGlobalsMerged(project: Project, currentProject: Project) {
        Assert.assertFalse(checkForDuplicates(currentProject.userLists as List<Any>?))
        Assert.assertFalse(checkForDuplicates(currentProject.userVariables as List<Any>?))
        Assert.assertFalse(checkForDuplicates(
            currentProject.broadcastMessageContainer
                .broadcastMessages as List<Any>?
        ))

        project.userLists.forEach { Assert.assertTrue(currentProject.userLists.contains(it)) }
        project.userVariables.forEach { Assert.assertTrue(currentProject.userVariables.contains(it)) }
    }
}
