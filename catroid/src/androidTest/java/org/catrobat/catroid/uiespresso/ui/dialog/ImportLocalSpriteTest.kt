/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.lang.Thread.sleep

class ImportLocalSpriteTest {
    private lateinit var projectToImportFrom: Project
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
        ProjectListActivity::class.java, true, false
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
        onView(withText(projectWithSameGlobals.name))
            .check(matches(isDisplayed()))
        onView(withText(projectToImportTo.name))
            .check(matches(isDisplayed()))

        val originalUserListsSize = projectToImportTo.userLists.size
        val originalUserVariableSize = projectToImportTo.userVariables.size
        val originalBroadcastMessagesSize = projectToImportTo.broadcastMessageContainer
            .broadcastMessages.size

        assertTrue(projectToImportTo.userVariables.contains(global1))
        assertTrue(projectToImportTo.defaultScene.spriteList[1].userVariables.contains(local1))
        assertTrue(projectToImportTo.userLists.contains(userListGlobal1))
        assertEquals(projectToImportTo.defaultScene.spriteList.size, 2)
        assertEquals(projectToImportTo.defaultScene.spriteList[1], dog)

        onView(withText(projectToImportTo.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        sleep(1000)
        onView(withId(R.id.button_add))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withId(R.id.dialog_new_look_from_local))
            .check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_from_local))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText(projectWithSameGlobals.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withText(R.string.new_sprite_dialog_place_visually))
            .check(matches(isDisplayed()))
        onView(withId(R.id.place_visually_sprite_switch))
            .perform(swipeLeft())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText(R.string.ok))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertAddedSprite(doggo)

        assertEquals(originalUserListsSize, projectToImportTo.userLists.size)
        assertFalse(checkForDuplicates(projectToImportTo.userLists))

        assertEquals(originalUserVariableSize, projectToImportTo.userVariables.size)
        assertFalse(checkForDuplicates(projectToImportTo.userVariables))

        assertEquals(
            originalBroadcastMessagesSize, projectToImportTo
                .broadcastMessageContainer.broadcastMessages.size
        )
        assertFalse(checkForDuplicates(projectToImportTo.broadcastMessageContainer.broadcastMessages))
    }

    @Test
    fun abortImportWithConflicts() {
        onView(withText(projectWithConflicts.name))
            .check(matches(isDisplayed()))
        onView(withText(projectToImportTo.name))
            .check(matches(isDisplayed()))

        val originalSpriteSize = projectToImportTo.defaultScene.spriteList.size
        onView(withText(projectToImportTo.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withId(R.id.button_add))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withId(R.id.dialog_new_look_from_local))
            .check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_from_local))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText(projectWithConflicts.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertEquals(projectToImportTo.defaultScene.spriteList.size, originalSpriteSize)
    }

    @Test
    fun importConflictsWithAutomaticResolve() {
        onView(withText(projectWithConflicts.name))
            .check(matches(isDisplayed()))
        onView(withText(projectToImportTo.name))
            .check(matches(isDisplayed()))

        onView(withText(projectToImportTo.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        sleep(500)
        onView(withId(R.id.button_add))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withId(R.id.dialog_new_look_from_local))
            .check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_from_local))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText(projectWithConflicts.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun importActorOrObjectTest() {
        onView(withText(projectToImportFrom.name))
            .check(matches(isDisplayed()))
        onView(withText(projectToImportTo.name))
            .check(matches(isDisplayed()))

        onView(withText(projectToImportFrom.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertEquals(projectToImportFrom.defaultScene.spriteList.size, 2)
        assertEquals(projectToImportFrom.defaultScene.spriteList[1], cat)
        sleep(500)
        onView(withText("cat"))
            .check(matches(isDisplayed()))
        onView(withText("dog"))
            .check(doesNotExist())

        onView(ViewMatchers.isRoot()).perform(ViewActions.pressBack())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        onView(withText(projectToImportTo.name))
            .check(matches(isDisplayed()))
        onView(withText(projectToImportTo.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText("dog"))
            .check(matches(isDisplayed()))
        onView(withText("cat"))
            .check(doesNotExist())

        assertEquals(projectToImportTo.defaultScene.spriteList.size, 2)
        assertEquals(projectToImportTo.defaultScene.spriteList[1], dog)

        onView(withId(R.id.button_add))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withId(R.id.dialog_new_look_from_local))
            .check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_look_from_local))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText(projectToImportFrom.name))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText(R.string.new_sprite_dialog_place_visually))
            .check(matches(isDisplayed()))
        sleep(500)
        onView(withId(R.id.place_visually_sprite_switch))
            .perform(swipeLeft())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText(R.string.ok))
            .perform(click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onView(withText("cat"))
            .check(matches(isDisplayed()))
        onView(withText("dog"))
            .check(matches(isDisplayed()))

        assertAddedSprite(cat)
    }

    private fun assertAddedSprite(addedSprite: Sprite) {
        projectToImportTo = projectManager.value.currentProject
        val originalScriptListOfCat = addedSprite.scriptList

        assertEquals(projectToImportTo.defaultScene.spriteList.size, 3)
        assertEquals(projectToImportTo.defaultScene.spriteList[1].name, dog.name)
        assertEquals(projectToImportTo.defaultScene.spriteList[2].name, addedSprite.name)
        for ((scriptCounter, script) in projectToImportTo.defaultScene.spriteList[2].scriptList
            .withIndex()) {
            assertTrue(script.javaClass == originalScriptListOfCat[scriptCounter].javaClass)
            for ((brickCounter, brick) in script.brickList
                .withIndex()) {
                assertTrue(
                    brick.javaClass == originalScriptListOfCat[scriptCounter]
                        .brickList[brickCounter].javaClass
                )
            }
        }
        for ((lookCounter, look) in projectToImportTo.defaultScene.spriteList[2].lookList
            .withIndex()) {
            assertTrue(look.name == cat.lookList[lookCounter].name)
            assertTrue(look.file == cat.lookList[lookCounter].file)
        }
        for ((variableCounter, variable) in projectToImportTo.defaultScene.spriteList[2]
            .userVariables
            .withIndex()) {
            assertTrue(variable.name == addedSprite.userVariables[variableCounter].name)
            assertTrue(variable.value == addedSprite.userVariables[variableCounter].value)
        }
        for ((listCounter, list) in projectToImportTo.defaultScene.spriteList[2].userLists
            .withIndex()) {
            for ((listElementCounter, listElement) in list.value.withIndex()) {
                assertTrue(
                    listElement.equals(addedSprite.userLists[listCounter].value[listElementCounter])
                )
            }
        }
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
        projectWithSameGlobals.userVariables.add(global1)
        projectWithSameGlobals.userLists.add(userListGlobal1)
        projectWithSameGlobals.broadcastMessageContainer.addBroadcastMessage(broadcast1)
        projectWithSameGlobals.defaultScene.addSprite(doggo)
        saveProjectSerial(projectWithSameGlobals, ApplicationProvider.getApplicationContext())

        projectWithConflicts =
            Project(ApplicationProvider.getApplicationContext(), "projectWithConflicts")
        noDog = Sprite("no_dog")
        projectWithConflicts.userVariables.add(global1)
        noDog.userVariables.add(local1)
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
            File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "projectToImportTo")
        )
    }

    private fun checkForDuplicates(any_list: List<Any>): Boolean {
        var prev: Any? = null
        any_list.forEach {
            if (it == prev) {
                return true
            }
            prev = it
        }
        return false
    }
}
