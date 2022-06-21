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

package org.catrobat.catroid.test.merge

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.recyclerview.controller.SceneController
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.File

class ImportLocalSpriteToProjectTest {
    private lateinit var multipleSceneProjectToImportFrom: Project
    private lateinit var singleSceneProjectToImportFrom: Project
    private lateinit var projectToImportTo: Project
    private lateinit var projectWithSameGlobals: Project
    private lateinit var projectWithConflicts: Project
    private var global1 = UserVariable("global1")
    private var local1 = UserVariable("local1")
    private var userListGlobal1 = UserList("userListGlobal1")
    private var broadcast1 = "broadcast1"
    private lateinit var cat: Sprite
    private lateinit var dog: Sprite
    private lateinit var doggo: Sprite
    private lateinit var noDog: Sprite
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
        createTestProjects()
        activityTestRule.launchActivity(null)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(
            singleSceneProjectToImportFrom.name,
            projectWithSameGlobals.name,
            projectWithConflicts.name,
            projectToImportTo.name
        )
    }

    @Test
    fun importObjectAndMergeGlobals() {
        addSpriteFromLocalProject(
            doggo,
            projectWithSameGlobals,
            projectWithSameGlobals.defaultScene
        )

        UiTestUtils.uncheckPlaceVisually(activityTestRule.activity)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(isDisplayed())
        ).perform(ViewActions.click())
        MergeTestUtils().assertSuccessfulSpriteImport(
            projectToImportTo, projectWithSameGlobals,
            doggo, projectToImportTo.defaultScene
                .spriteList.last()
        )
    }

    @Test
    fun abortImportWithConflicts() {
        val original = MergeTestUtils().getOriginalProjectData(projectToImportTo)
        addSpriteFromLocalProject(noDog, projectWithConflicts, projectWithConflicts.defaultScene)
        MergeTestUtils().assertRejectedImport(projectToImportTo, original)
    }

    @Test
    fun importActorOrObjectTest() {
        addSpriteFromLocalProject(
            cat,
            singleSceneProjectToImportFrom,
            singleSceneProjectToImportFrom.defaultScene
        )

        UiTestUtils.uncheckPlaceVisually(activityTestRule.activity)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(isDisplayed())
        ).perform(ViewActions.click())

        Espresso.onView(withText("cat"))
            .check(ViewAssertions.matches(isDisplayed()))
        MergeTestUtils().assertSuccessfulSpriteImport(
            projectToImportTo,
            singleSceneProjectToImportFrom, cat,
            projectToImportTo.defaultScene.spriteList
                .last()
        )
    }

    @Test
    fun importLocalSpriteFromMultipleSceneProject() {
        Assert.assertEquals(projectToImportTo.defaultScene.spriteList.size, 2)
        Assert.assertEquals(projectToImportTo.defaultScene.spriteList[1], dog)
        Espresso.onView(withText("cat")).check(ViewAssertions.doesNotExist())
        addSpriteFromLocalProject(
            cat,
            multipleSceneProjectToImportFrom,
            multipleSceneProjectToImportFrom.sceneList[1]
        )

        UiTestUtils.uncheckPlaceVisually(activityTestRule.activity)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog())
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        Espresso.onView(withText("cat")).check(ViewAssertions.matches(isDisplayed()))
        MergeTestUtils().assertSuccessfulSpriteImport(
            projectToImportTo,
            multipleSceneProjectToImportFrom, cat,
            projectToImportTo.defaultScene.spriteList.last()
        )
    }

    private fun addSpriteFromLocalProject(
        spriteToAdd: Sprite,
        projectToAddFrom: Project,
        sceneToAddFrom: Scene
    ) {
        Espresso.onView(withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.dialog_new_look_from_local)).perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(projectToAddFrom.name)).perform(ViewActions.click())

        if (projectToAddFrom.hasMultipleScenes()) {
            Espresso.onView(withText(sceneToAddFrom.name)).perform(ViewActions.click())
        }

        Espresso.onView(withText(activityTestRule.activity.applicationContext.getString(R.string.import_object)))
            .check(ViewAssertions.matches(isDisplayed()))
        Espresso.onView(withText(spriteToAdd.name)).perform(ViewActions.click())
    }

    @Throws(ProjectException::class)
    private fun createTestProjects() {
        singleSceneProjectToImportFrom =
            Project(ApplicationProvider.getApplicationContext(), "projectToImportFrom")
        cat = Sprite("cat")
        cat.addScript(StartScript())
        singleSceneProjectToImportFrom.defaultScene.addSprite(cat)
        saveProjectSerial(
            singleSceneProjectToImportFrom,
            ApplicationProvider.getApplicationContext()
        )

        projectWithSameGlobals =
            Project(ApplicationProvider.getApplicationContext(), "projectWithSameGlobals")
        doggo = Sprite("doggo")
        doggo.addScript(StartScript())
        projectWithSameGlobals.userVariables.add(global1)
        projectWithSameGlobals.userLists.add(userListGlobal1)
        projectWithSameGlobals.broadcastMessageContainer.addBroadcastMessage(broadcast1)
        projectWithSameGlobals.defaultScene.addSprite(doggo)
        saveProjectSerial(projectWithSameGlobals, ApplicationProvider.getApplicationContext())

        multipleSceneProjectToImportFrom =
            Project(ApplicationProvider.getApplicationContext(), "multipleSceneProjectToImportFrom")
        val scene1 = SceneController.newSceneWithBackgroundSprite(
            "scene1",
            "background1",
            multipleSceneProjectToImportFrom
        )
        val scene2 = SceneController.newSceneWithBackgroundSprite(
            "scene2",
            "background2",
            multipleSceneProjectToImportFrom
        )
        scene1.addSprite(cat)
        scene2.addSprite(doggo)
        multipleSceneProjectToImportFrom.sceneList.addAll(listOf(scene1, scene2))
        saveProjectSerial(
            multipleSceneProjectToImportFrom,
            ApplicationProvider.getApplicationContext()
        )

        projectWithConflicts =
            Project(ApplicationProvider.getApplicationContext(), "projectWithConflicts")
        noDog = Sprite("no_dog")
        projectWithConflicts.userVariables.add(local1)
        noDog.userVariables.add(global1)
        projectWithConflicts.defaultScene.addSprite(noDog)
        saveProjectSerial(projectWithConflicts, ApplicationProvider.getApplicationContext())

        projectToImportTo =
            Project(ApplicationProvider.getApplicationContext(), "projectToImportTo")
        projectToImportTo.userVariables.add(global1)
        projectToImportTo.broadcastMessageContainer.addBroadcastMessage(broadcast1)
        projectToImportTo.userLists.add(userListGlobal1)
        dog = Sprite("dog")
        dog.userVariables.add(local1)
        dog.addScript(StartScript())
        dog.scriptList[0].addBrick(BroadcastWaitBrick(broadcast1))
        projectToImportTo.defaultScene.addSprite(dog)
        saveProjectSerial(projectToImportTo, ApplicationProvider.getApplicationContext())
        projectManager.value.loadProject(
            File(
                FlavoredConstants.DEFAULT_ROOT_DIRECTORY,
                "projectToImportTo"
            )
        )

        projectManager.value.currentProject = projectToImportTo
    }
}
