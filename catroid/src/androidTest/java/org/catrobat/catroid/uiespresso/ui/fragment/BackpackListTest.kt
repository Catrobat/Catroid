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

package org.catrobat.catroid.uiespresso.ui.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY_NAME
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.USER_LIST
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils.deleteProjects
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController.Companion.GLOBAL_USER_VARIABLE
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController.Companion.LOCAL_USER_VARIABLE
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createEmptyProject
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject
import java.io.File

@RunWith(AndroidJUnit4::class)
class BackpackListTest {
    @get: Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )

    val projectManager: ProjectManager by inject(ProjectManager::class.java)
    lateinit var script: Script
    private val projectName: String? = BackpackListTest::class.simpleName
    private val project2Name: String = BackpackListTest::class.simpleName + "1"
    private val groupName: String = "listScript"
    private val spriteName: String = "testSprite"
    private val sprite2Name: String = "testSprite2"
    private val globalListName: String = "globalList"
    private val localListName: String = "localList"
    private val backpackDirectory: File = File(DEFAULT_ROOT_DIRECTORY, BACKPACK_DIRECTORY_NAME)

    private val waitThreshold: Long = 3000

    @Before
    @Throws(Exception::class)
    fun setUp() {
        if (!backpackDirectory.exists()) {
            backpackDirectory.mkdir()
        }

        script = createMixedBrickScript()

        XstreamSerializer.getInstance().saveProject(projectManager.currentProject)

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        deleteProjects(projectName, project2Name)
        BackpackListManager.getInstance().removeItemFromScriptBackPack(groupName)
    }

    @Test
    fun testUnpackScriptInSameSprite() {
        packScriptViaOverflowMenu()

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(click())

        assertEquals(projectManager.currentProject.userLists.size, 1)
        assertEquals(projectManager.currentSprite.userLists.size, 1)

        assertEquals(projectManager.currentProject.userLists[0].name, globalListName)
        assertEquals(projectManager.currentSprite.userLists[0].name, localListName)
    }

    @Test
    fun testUnpackScriptInOtherSprite() {
        onView(withText(projectName))
            .perform(click())

        onView(withText(spriteName))
            .perform(click())

        onView(withText(R.string.brick_when_started))
            .perform(click())

        onView(withText(R.string.backpack_add))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withText(R.string.backpack_add))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(clearText(), replaceText(groupName), closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())

        pressBack()

        val sprite2 = Sprite(sprite2Name)
        projectManager.currentProject.defaultScene.addSprite(sprite2)

        XstreamSerializer.getInstance().saveProject(projectManager.currentProject)

        pressBack()

        onView(withText(sprite2Name))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withText(sprite2Name))
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(waitFor(isDisplayed(), waitThreshold))

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(click())

        assertEquals(projectManager.currentProject.userLists.size, 1)
        assertEquals(projectManager.currentSprite.userLists.size, 1)

        assertEquals(projectManager.currentProject.userLists[0].name, globalListName)
        assertEquals(projectManager.currentSprite.userLists[0].name, localListName)
    }

    @Test
    fun testUnpackScriptInOtherSpriteTestViaOverflowMenu() {
        packScriptViaOverflowMenu()
        projectManager.currentProject.defaultScene.addSprite(Sprite(sprite2Name))
        XstreamSerializer.getInstance().saveProject(projectManager.currentProject)

        pressBack()

        onView(withText(sprite2Name))
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(waitFor(isDisplayed(), waitThreshold))

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(click())

        assertEquals(projectManager.currentProject.userLists.size, 1)
        assertEquals(projectManager.currentSprite.userLists.size, 1)

        assertEquals(projectManager.currentProject.userLists[0].name, globalListName)
        assertEquals(projectManager.currentSprite.userLists[0].name, localListName)
    }

    @Test
    fun testUnpackScriptInOtherProject() {
        packScriptViaOverflowMenu()

        createEmptyProject(project2Name)

        pressBack()
        pressBack()

        onView(withText(project2Name))
            .perform(click())

        onRecyclerView().atPosition(1)
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(click())

        assertEquals(projectManager.currentProject.userLists.size, 1)
        assertEquals(projectManager.currentSprite.userLists.size, 1)

        assertEquals(projectManager.currentProject.userLists[0].name, globalListName)
        assertEquals(projectManager.currentSprite.userLists[0].name, localListName)
    }

    @Test
    fun testDifferentListTypesWithSameName() {
        packScriptViaOverflowMenu()

        val script = createProjectAndGetStartScript(project2Name)
        script.addBrick(addListBrick(globalListName, LOCAL_USER_VARIABLE))
        script.addBrick(addListBrick(localListName, GLOBAL_USER_VARIABLE))

        pressBack()
        pressBack()

        onView(withText(project2Name))
            .perform(click())

        onRecyclerView().atPosition(1)
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(click())

        onRecyclerView().atPosition(0)
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withText(R.string.unpack))
            .perform(click())

        assertEquals(projectManager.currentProject.userLists.size, 2)
        assertEquals(projectManager.currentSprite.userLists.size, 2)

        assertEquals(projectManager.currentProject.userLists[1].name, "$globalListName (1)")
        assertEquals(projectManager.currentSprite.userLists[1].name, "$localListName (1)")
    }

    @FlakyTest
    @Test
    fun testUnpackSpriteInSameProject() {
        packSpriteViaOverflowMenu()

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withText(R.string.unpack))
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.unpack))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withText(R.string.unpack))
            .perform(click())

        onRecyclerView().atPosition(0)
            .performCheckItem()

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withText("$spriteName (1)"))
            .perform(click())

        assertEquals(projectManager.currentProject.userLists.size, 1)
        assertEquals(projectManager.currentSprite.userLists.size, 1)

        assertEquals(projectManager.currentProject.userLists[0].name, globalListName)
        assertEquals(projectManager.currentSprite.userLists[0].name, localListName)
    }

    @FlakyTest
    @Test
    fun testUnpackSpriteInOtherProject() {
        packSpriteViaOverflowMenu()

        val project2 = createEmptyProject(project2Name)
        XstreamSerializer.getInstance().saveProject(project2)

        pressBack()

        onView(withText(project2Name))
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onView(withText(R.string.unpack))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withText(R.string.unpack))
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.unpack))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withText(R.string.unpack))
            .perform(click())

        onRecyclerView().atPosition(0)
            .performCheckItem()

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withText("$spriteName (1)"))
            .perform(click())

        assertEquals(projectManager.currentProject.userLists.size, 1)
        assertEquals(projectManager.currentSprite.userLists.size, 1)

        assertEquals(projectManager.currentProject.userLists[0].name, globalListName)
        assertEquals(projectManager.currentSprite.userLists[0].name, localListName)
    }

    private fun packScriptViaOverflowMenu() {
        onView(withText(projectName))
            .perform(click())

        waitFor(withText(spriteName), waitThreshold)

        onView(withText(spriteName))
            .perform(click())

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onBrickAtPosition(0).onChildView(allOf(withId(R.id.brick_checkbox)))
            .perform(click())

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(clearText(), replaceText(groupName), closeSoftKeyboard())

        onView(withText(R.string.ok))
            .perform(click())

        XstreamSerializer.getInstance().saveProject(projectManager.currentProject)

        pressBack()
    }

    private fun packSpriteViaOverflowMenu() {
        onView(withText(projectName))
            .perform(click())

        waitFor(withText(R.string.background), waitThreshold)

        openActionBarOverflowOrOptionsMenu(baseActivityTestRule.activity)

        onView(withText(R.string.backpack))
            .perform(click())

        onRecyclerView().atPosition(1)
            .performCheckItem()

        onView(withId(R.id.confirm))
            .perform(click())

        XstreamSerializer.getInstance().saveProject(projectManager.currentProject)

        pressBack()
    }

    private fun createComplexFormula(): Formula {
        val leftChild = FormulaElement(USER_LIST, globalListName, null)
        val leftSubChild = FormulaElement(USER_LIST, localListName, null)
        val rightSubChild = FormulaElement(FormulaElement.ElementType.NUMBER, "1", null)
        val rightChild = FormulaElement(FormulaElement.ElementType.OPERATOR, "+", null, leftSubChild, rightSubChild)
        val parent = FormulaElement(FormulaElement.ElementType.OPERATOR, "*", null, leftChild, rightChild)

        return Formula(parent)
    }

    private fun createMixedBrickScript(): Script {
        val script = createProjectAndGetStartScript(projectName)
        projectManager.currentProject.userLists.add(UserList(globalListName))

        script.addBrick(addListBrick(localListName, LOCAL_USER_VARIABLE))
        script.addBrick(SetXBrick(createComplexFormula()))
        return script
    }

    private fun addListBrick(name: String, type: Int, value: Int = 1): AddItemToUserListBrick {
        val list = UserList(name)
        val addItemToUserListBrick = AddItemToUserListBrick(Formula(value))
        addItemToUserListBrick.userList = list

        when (type) {
            GLOBAL_USER_VARIABLE -> projectManager.currentProject.userLists.add(list)
            LOCAL_USER_VARIABLE -> projectManager.currentSprite.userLists.add(list)
        }

        return addItemToUserListBrick
    }
}
