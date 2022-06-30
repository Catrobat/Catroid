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
package org.catrobat.catroid.uiespresso.formulaeditor

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.COLLISION_FORMULA
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.OPERATOR
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.USER_VARIABLE
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AdvancedClipboardTest {

    @get:Rule
    val activityTestRule = BaseActivityTestRule(ProjectListActivity::class.java, false, false)

    lateinit var script: Script
    lateinit var sprite: Sprite
    lateinit var project: Project

    @Before
    fun setUp() {
        createProject()
        activityTestRule.launchActivity(null)
    }

    @Test
    fun copyAndPasteToSameSprite() {
        openFormulaEditor()
        onFormulaEditor().performEnterNumber(12_345)
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.copy)).inRoot(isPlatformPopup())
            .perform(click())
        pressBack()
        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup())
            .perform(click())
    }

    @Test
    fun cutAndPasteToSameSprite() {
        openFormulaEditor()
        onFormulaEditor().performEnterNumber(12_345)
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.cut)).inRoot(isPlatformPopup())
            .perform(click())
        pressBack()
        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup())
            .perform(click())
    }

    @Test
    fun copyAndPasteToOtherSprite() {
        createSpriteWithBricks(project, "${spriteName}1")
        XstreamSerializer.getInstance().saveProject(project)

        openFormulaEditor()
        onFormulaEditor().performEnterNumber(12_345)
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.copy)).inRoot(isPlatformPopup())
            .perform(click())

        pressBack()
        pressBack()
        onView(withText("${spriteName}1"))
            .perform(click())

        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup())
            .perform(click())
    }

    @Test
    fun copyAndPasteToOtherProject() {
        val project = createProject("${projectName}1", "${spriteName}1", "${variableName}1")
        XstreamSerializer.getInstance().saveProject(project)

        openFormulaEditor()
        onFormulaEditor().performEnterNumber(12_345)
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.copy)).inRoot(isPlatformPopup())
            .perform(click())

        pressBack()
        pressBack()
        pressBack()

        onView(withText("${projectName}1"))
            .perform(waitFor(isDisplayed(), 5000))
            .perform(click())
        onView(withText("${spriteName}1"))
            .perform(waitFor(isDisplayed(), 5000))
            .perform(click())

        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup()).perform(click())
    }

    @Test
    fun copyAndPasteToOtherSpriteWithMissingVariable() {
        val newSprite = createSpriteWithBricks(project, "${spriteName}1", false)
        XstreamSerializer.getInstance().saveProject(project)

        onView(withText(projectName))
            .perform(click())
        onView(withText(spriteName))
            .perform(click())
        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.copy)).inRoot(isPlatformPopup())
            .perform(click())

        pressBack()
        pressBack()
        onView(withText("${spriteName}1"))
            .perform(click())

        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup())
            .perform(click())

        onFormulaEditor().checkContains(CatroidApplication.getAppContext().getString(R.string.formula_editor_missing_variable))
        Assert.assertEquals(0, newSprite.userVariables.size)
    }

    @Test
    fun copyAndPasteToOtherSpriteWithExistingVariable() {
        val newSprite = createSpriteWithBricks(project, "${spriteName}1")
        XstreamSerializer.getInstance().saveProject(project)

        onView(withText(projectName))
            .perform(click())
        onView(withText(spriteName))
            .perform(click())
        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.copy)).inRoot(isPlatformPopup())
            .perform(click())

        pressBack()
        pressBack()
        onView(withText("${spriteName}1"))
            .perform(click())

        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup())
            .perform(click())

        onFormulaEditor().checkContains(variableName)
        Assert.assertEquals(0, newSprite.userVariables.size)
        Assert.assertEquals(1, project.userVariables.size)
    }

    @Test
    fun copyAndPasteToOtherSpriteWithMissingCollisionSprite() {
        createSpriteWithBricks(project, "${spriteName}1", false)
        sprite.scriptList[0].brickList.removeAt(1)
        sprite.scriptList[0].addBrick(SetXBrick(createFormulaWithCollision("${spriteName}2")))
        XstreamSerializer.getInstance().saveProject(project)

        onView(withText(projectName))
            .perform(click())
        onView(withText(spriteName))
            .perform(click())
        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.copy)).inRoot(isPlatformPopup())
            .perform(click())

        pressBack()
        pressBack()
        onView(withText("${spriteName}1"))
            .perform(click())

        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup())
            .perform(click())

        onFormulaEditor().checkContains(CatroidApplication.getAppContext().getString(R.string.background))
    }

    @Test
    fun copyAndPasteToOtherSpriteWithExistingCollisionSprite() {
        createSpriteWithBricks(project, "${spriteName}1", false)
        sprite.scriptList[0].brickList.removeAt(1)
        sprite.scriptList[0].addBrick(SetXBrick(createFormulaWithCollision("${spriteName}1")))
        XstreamSerializer.getInstance().saveProject(project)

        onView(withText(projectName))
            .perform(click())
        onView(withText(spriteName))
            .perform(click())
        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.copy)).inRoot(isPlatformPopup())
            .perform(click())

        pressBack()
        pressBack()
        onView(withText("${spriteName}1"))
            .perform(click())

        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())
        onView(withId(R.id.paste)).inRoot(isPlatformPopup())
            .perform(click())

        onFormulaEditor().checkContains("${spriteName}1")
    }

    @After
    fun tearDown() {
        activityTestRule.finishActivity()
        TestUtils.deleteProjects(projectName)
        emptyClipboardFile()
    }

    private val projectName: String = AdvancedClipboardTest::class.java.simpleName
    private val spriteName = "testSprite"
    private val variableName = "testVariable"

    fun createProject(
        projectName: String = this.projectName,
        spriteName: String = this.spriteName,
        variableName: String = this.variableName
    ): Project {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        sprite = createSprite(project, spriteName)
        script = StartScript()
        script.addBrick(createSetVariableBrick(sprite, variableName))
        script.addBrick(createSetXBrick(createFormulaWithVariable()))
        sprite.addScript(script)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite

        XstreamSerializer.getInstance().saveProject(project)
        return project
    }

    fun createSprite(currentProject: Project, spriteName: String = this.spriteName): Sprite {
        val sprite = Sprite(spriteName)
        currentProject.defaultScene.addSprite(sprite)
        return sprite
    }

    private fun createSpriteWithBricks(
        currentProject: Project,
        spriteName: String = this.spriteName,
        createVariable: Boolean = true,
        formula: Formula? = null
    ): Sprite {
        val sprite = createSprite(currentProject, spriteName)
        val script = StartScript()
        if (createVariable) {
            script.addBrick(createSetVariableBrick())
            script.addBrick(createSetXBrick(formula))
        } else {
            script.addBrick(createSetXBrick())
        }
        sprite.addScript(script)
        return sprite
    }

    private fun createSetVariableBrick(sprite: Sprite? = null, variableName: String = this.variableName):
        SetVariableBrick {
        val setVariableBrick = SetVariableBrick()
        val userVariable = UserVariable(variableName)
        if (sprite != null) {
            sprite.addUserVariable(userVariable)
        } else {
            project.addUserVariable(userVariable)
        }
        setVariableBrick.userVariable = userVariable
        return setVariableBrick
    }

    private fun createSetXBrick(formula: Formula? = null): SetXBrick {
        return if (formula != null) {
            SetXBrick(formula)
        } else {
            SetXBrick()
        }
    }

    private fun createFormulaWithVariable(variableName: String = this.variableName): Formula {
        val parent = FormulaElement(OPERATOR, "PLUS", null, null, null)
        val leftChild = FormulaElement(USER_VARIABLE, variableName, parent)
        val rightChild = FormulaElement(NUMBER, "1", parent)
        parent.setLeftChild(leftChild)
        parent.setRightChild(rightChild)
        return Formula(parent)
    }

    private fun createFormulaWithCollision(collidingWith: String): Formula =
        Formula(FormulaElement(COLLISION_FORMULA, collidingWith, null))

    private fun emptyClipboardFile() {
        val clipboardDirectory = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, Constants.CLIPBOARD_DIRECTORY_NAME)
        val clipboardFile = File(clipboardDirectory, Constants.CLIPBOARD_JSON_FILE_NAME)
        if (clipboardFile.exists()) {
            clipboardFile.writeText("")
        }
    }

    private fun openFormulaEditor() {
        onView(withText(projectName))
            .perform(click())
        onView(withText(spriteName))
            .perform(click())
        onView(withId(R.id.brick_set_variable_edit_text))
            .perform(click())
    }
}
