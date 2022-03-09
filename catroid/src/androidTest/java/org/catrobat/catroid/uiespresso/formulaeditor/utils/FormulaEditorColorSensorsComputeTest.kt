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
package org.catrobat.catroid.uiespresso.formulaeditor.utils

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.STRING
import org.catrobat.catroid.formulaeditor.Functions.COLLIDES_WITH_COLOR
import org.catrobat.catroid.formulaeditor.Functions.COLOR_AT_XY
import org.catrobat.catroid.formulaeditor.Functions.COLOR_TOUCHES_COLOR
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.java.KoinJavaComponent.inject

@RunWith(Parameterized::class)
class FormulaEditorColorSensorsComputeTest(
    private val name: String,
    private val formulaElement: FormulaElement,
    private val leftChild: FormulaElement?,
    private val rightChild: FormulaElement?,
    private val expectedValue: String
) {
    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    companion object {
        var colorAtXYLeftChild = FormulaElement(NUMBER, "0", null)
        var colorAtXYRightChild = FormulaElement(NUMBER, "0", null)
        var touchesColorLeftChild = FormulaElement(STRING, "#000000", null)
        var touchesColorRightChild = FormulaElement(STRING, "#000000", null)
        var colorAtXY = FormulaElement(FUNCTION, COLOR_AT_XY.name, null)
        var touchesColor = FormulaElement(FUNCTION, COLLIDES_WITH_COLOR.name, null)
        var colorTouchesColor = FormulaElement(FUNCTION, COLOR_TOUCHES_COLOR.name, null)

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("Color at xy", colorAtXY, colorAtXYLeftChild, colorAtXYRightChild, "NaN"),
            arrayOf("Touches Color", touchesColor, touchesColorLeftChild, null, "false"),
            arrayOf(
                "Color touches color",
                colorTouchesColor,
                touchesColorLeftChild,
                touchesColorRightChild,
                "false"
            )
        )
    }

    @Test
    fun testComputingColorSensors() {
        onView(ViewMatchers.withId(R.id.brick_if_begin_edit_text))
            .perform(click())
        FormulaEditorWrapper.onFormulaEditor()
            .performCompute()
        onView(ViewMatchers.withId(R.id.formula_editor_compute_dialog_textview))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedValue)))
        pressBack()
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProjectWithFormulaElements(FormulaEditorColorSensorsComputeTest::class.java.name)
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(FormulaEditorColorSensorsComputeTest::class.java.name)
    }

    private fun createProjectWithFormulaElements(projectName: String?): Project {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        if (leftChild != null) formulaElement.setLeftChild(leftChild)
        if (rightChild != null) formulaElement.setRightChild(rightChild)

        val formula = Formula(formulaElement)

        val ifBrick = IfLogicBeginBrick(formula)
        script.addBrick(ifBrick)
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
        return project
    }
}
