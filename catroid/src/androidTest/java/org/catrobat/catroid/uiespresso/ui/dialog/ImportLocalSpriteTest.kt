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
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.File

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
    private lateinit var no_dog: Sprite
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
        Espresso.onView(ViewMatchers.withText(projectWithSameGlobals.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(projectToImportTo.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val originalUserListsSize = projectToImportTo.userLists.size
        val originalUserVariableSize = projectToImportTo.userVariables.size
        val originalBroadcastMessagesSize = projectToImportTo.broadcastMessageContainer
            .broadcastMessages.size

        Assert.assertTrue(projectToImportTo.userVariables.contains(global1))
        Assert.assertTrue(projectToImportTo.defaultScene.spriteList[1].userVariables.contains(local1))
        Assert.assertTrue(projectToImportTo.userLists.contains(userListGlobal1))
        Assert.assertEquals(projectToImportTo.defaultScene.spriteList.size, 2)
        Assert.assertEquals(projectToImportTo.defaultScene.spriteList[1], dog)

        Espresso.onView(ViewMatchers.withText(projectToImportTo.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_from_local))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_from_local))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText(projectWithSameGlobals.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        Espresso.onView(ViewMatchers.withText(R.string.new_sprite_dialog_place_visually))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.place_visually_sprite_switch))
            .perform(ViewActions.swipeLeft())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText(R.string.ok))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertAddedSprite(doggo)

        Assert.assertEquals(originalUserListsSize, projectToImportTo.userLists.size)
        Assert.assertFalse(checkForDuplicates(projectToImportTo.userLists))

        Assert.assertEquals(originalUserVariableSize, projectToImportTo.userVariables.size)
        Assert.assertFalse(checkForDuplicates(projectToImportTo.userVariables))

        Assert.assertEquals(originalBroadcastMessagesSize, projectToImportTo
            .broadcastMessageContainer.broadcastMessages.size)
        Assert.assertFalse(checkForDuplicates(projectToImportTo.broadcastMessageContainer.broadcastMessages))
    }

    @Test
    fun abortImportWithConflicts() {
        Espresso.onView(ViewMatchers.withText(projectWithConflicts.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(projectToImportTo.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val originalSpriteSize = projectToImportTo.defaultScene.spriteList.size
        Espresso.onView(ViewMatchers.withText(projectToImportTo.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_from_local))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_from_local))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText(projectWithConflicts.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        Assert.assertEquals(projectToImportTo.defaultScene.spriteList.size, originalSpriteSize)
    }

    @Test
    fun importActorOrObjectTest() {
        Espresso.onView(ViewMatchers.withText(projectToImportFrom.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(projectToImportTo.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(projectToImportFrom.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        Assert.assertEquals(projectToImportFrom.defaultScene.spriteList.size, 2)
        Assert.assertEquals(projectToImportFrom.defaultScene.spriteList[1], cat)

        Espresso.onView(ViewMatchers.withText("cat"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("dog"))
            .check(ViewAssertions.doesNotExist())

        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.pressBack())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        Espresso.onView(ViewMatchers.withText(projectToImportTo.name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(projectToImportTo.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText("dog"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("cat"))
            .check(ViewAssertions.doesNotExist())

        Assert.assertEquals(projectToImportTo.defaultScene.spriteList.size, 2)
        Assert.assertEquals(projectToImportTo.defaultScene.spriteList[1], dog)

        Espresso.onView(ViewMatchers.withId(R.id.button_add))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_from_local))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.dialog_new_look_from_local))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText(projectToImportFrom.name))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText(R.string.new_sprite_dialog_place_visually))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.place_visually_sprite_switch))
            .perform(ViewActions.swipeLeft())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText(R.string.ok))
            .perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Espresso.onView(ViewMatchers.withText("cat"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("dog"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        assertAddedSprite(cat)
    }

    private fun assertAddedSprite(addedSprite: Sprite) {
        projectToImportTo = projectManager.value.currentProject
        val originalScriptListOfCat = addedSprite.scriptList

        Assert.assertEquals(projectToImportTo.defaultScene.spriteList.size, 3)
        Assert.assertEquals(projectToImportTo.defaultScene.spriteList[1].name, dog.name)
        Assert.assertEquals(projectToImportTo.defaultScene.spriteList[2].name, addedSprite.name)
        for ((scriptCounter, script) in projectToImportTo.defaultScene.spriteList[2].scriptList
            .withIndex()) {
            Assert.assertTrue(script.javaClass == originalScriptListOfCat[scriptCounter].javaClass)
            for ((brickCounter, brick) in script.brickList
                .withIndex()) {
                Assert.assertTrue(brick.javaClass == originalScriptListOfCat[scriptCounter]
                    .brickList[brickCounter].javaClass)
            }
        }
        for ((lookCounter, look) in projectToImportTo.defaultScene.spriteList[2].lookList
            .withIndex()) {
            Assert.assertTrue(look.name == cat.lookList[lookCounter].name)
            Assert.assertTrue(look.file == cat.lookList[lookCounter].file)
        }
        for ((variableCounter, variable) in projectToImportTo.defaultScene.spriteList[2]
            .userVariables
            .withIndex()) {
            Assert.assertTrue(variable.name == addedSprite.userVariables[variableCounter].name)
            Assert.assertTrue(variable.value == addedSprite.userVariables[variableCounter].value)
        }
        for ((listCounter, list) in projectToImportTo.defaultScene.spriteList[2].userLists
            .withIndex()) {
            for ((listElementCounter, listElement) in list.value.withIndex()) {
                Assert.assertTrue(
                    listElement.equals(addedSprite.userLists[listCounter].value[listElementCounter]))
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
        no_dog = Sprite("no_dog")
        projectWithConflicts.userVariables.add(global1)
        no_dog.userVariables.add(local1)
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
