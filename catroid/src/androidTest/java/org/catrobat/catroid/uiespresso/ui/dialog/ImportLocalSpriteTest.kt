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
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.merge.MergeTestUtils
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class ImportLocalSpriteTest {
    private lateinit var projectToImportFrom: Project
    private lateinit var projectToImportTo: Project
    private lateinit var projectWithSameGlobals: Project
    private lateinit var projectWithConflicts: Project
    private lateinit var projectWithProjectNameConflict: Project
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
        TestUtils.deleteProjects(projectToImportFrom.name)
        TestUtils.deleteProjects(projectWithSameGlobals.name)
        TestUtils.deleteProjects(projectWithConflicts.name)
        TestUtils.deleteProjects(projectToImportTo.name)
    }

    @Test
    fun importObjectAndMergeGlobals() {
        addSpriteFromLocalProject(projectWithSameGlobals)

        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(isDisplayed())
        ).perform(ViewActions.click())

        MergeTestUtils().assertSuccessfulSpriteImport(
            projectToImportTo, projectWithSameGlobals,
            projectWithSameGlobals.defaultScene
                .spriteList[1], projectToImportTo
                .defaultScene.spriteList.last()
        )
    }

    @Test
    fun abortImportWithConflicts() {
        val original = MergeTestUtils().getOriginalProjectData(projectToImportTo)
        addSpriteFromLocalProject(projectWithConflicts)

        Espresso.onView(withText(R.string.import_conflicting_variables)).check(
            ViewAssertions.matches
                (isDisplayed())
        )
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(
                isDisplayed()
            )
        ).perform(ViewActions.click())

        MergeTestUtils().assertRejectedImport(projectToImportTo, original)
    }

    @Test
    fun abortImportWithProjectNameConflict() {
        val original = MergeTestUtils().getOriginalProjectData(projectToImportTo)

        addSpriteFromLocalProject(projectWithProjectNameConflict)
        Espresso.onView(withText(R.string.import_unresolvable_project_name_reason)).check(
            ViewAssertions.matches
                (isDisplayed())
        )
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(
                isDisplayed()
            )
        ).perform(ViewActions.click())

        MergeTestUtils().assertRejectedImport(projectToImportTo, original)
    }

    @Test
    fun importActorOrObjectTest() {
        addSpriteFromLocalProject(projectToImportFrom)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(isDisplayed())
        ).perform(ViewActions.click())
        Espresso.onView(withId(R.id.confirm)).perform(ViewActions.click())

        Espresso.onView(withText("cat"))
            .check(ViewAssertions.matches(isDisplayed()))
        Espresso.onView(withText("dog"))
            .check(ViewAssertions.matches(isDisplayed()))

        MergeTestUtils().assertSuccessfulSpriteImport(
            projectToImportTo, projectToImportFrom,
            projectToImportTo
                .defaultScene.getSprite("cat"),
            projectToImportTo
                .defaultScene.spriteList.last()
        )
    }

    private fun addSpriteFromLocalProject(
        projectToAddFrom: Project
    ) {
        Espresso.onView(withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.dialog_new_look_from_local)).perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(withText(projectToAddFrom.name)).perform(ViewActions.click())
    }

    @Throws(ProjectException::class)
    private fun createTestProjects() {
        projectToImportFrom =
            Project(ApplicationProvider.getApplicationContext(), "projectToImportFrom")
        cat = Sprite("cat")
        cat.addScript(StartScript())
        projectToImportFrom.defaultScene.addSprite(cat)
        saveProjectSerial(projectToImportFrom, ApplicationProvider.getApplicationContext())

        projectWithSameGlobals =
            Project(ApplicationProvider.getApplicationContext(), "projectWithSameGlobals")
        doggo = Sprite("doggo")
        doggo.addScript(StartScript())
        projectWithSameGlobals.userVariables.add(global1)
        projectWithSameGlobals.userLists.add(userListGlobal1)
        projectWithSameGlobals.broadcastMessageContainer.addBroadcastMessage(broadcast1)
        projectWithSameGlobals.defaultScene.addSprite(doggo)
        saveProjectSerial(projectWithSameGlobals, ApplicationProvider.getApplicationContext())

        projectWithConflicts =
            Project(ApplicationProvider.getApplicationContext(), "projectWithConflicts")
        noDog = Sprite("no_dog")
        projectWithConflicts.userVariables.add(local1)
        noDog.userVariables.add(global1)
        projectWithConflicts.defaultScene.addSprite(noDog)
        saveProjectSerial(projectWithConflicts, ApplicationProvider.getApplicationContext())

        projectWithProjectNameConflict =
            Project(ApplicationProvider.getApplicationContext(), "project.name.conflict")
        noDog.userVariables.add(global1)
        projectWithProjectNameConflict.defaultScene.addSprite(noDog)
        saveProjectSerial(
            projectWithProjectNameConflict, ApplicationProvider
                .getApplicationContext()
        )

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
        projectManager.value.currentProject = projectToImportTo
    }
}
