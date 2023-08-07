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

package org.catrobat.catroid.uiespresso.formulaeditor

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FormulaEditorRevertChangesDialogTest {
    private var brickPosition = 0

    private val VARIABLE_NAME = "TestVariable"
    private val NEW_VARIABLE_NAME = "TestVariableNew"
    private val VARIABLE_VALUE = 5

    var userVariable: UserVariable? = null

    @get:Rule
    var baseActivityTestRule: FragmentActivityTestRule<SpriteActivity> =
        FragmentActivityTestRule<SpriteActivity>(
            SpriteActivity::class.java,
            SpriteActivity.EXTRA_FRAGMENT_POSITION,
            SpriteActivity.FRAGMENT_SCRIPTS
        )

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(FormulaEditorRevertChangesDialogTest::class.java.name)
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        brickPosition = 1
        val script: org.catrobat.catroid.content.Script =
            BrickTestUtils.createProjectAndGetStartScript(
                FormulaEditorRevertChangesDialogTest::class.java.name
            )
        script.addBrick(PlaceAtBrick())
        userVariable =
            UserVariable(VARIABLE_NAME, VARIABLE_VALUE)
        ProjectManager.getInstance().getCurrentProject().addUserVariable(userVariable)
        script.addBrick(SetVariableBrick(Formula(0), userVariable))
        baseActivityTestRule.launchActivity()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testRevertChangesDialogAppears() {
        BrickDataInteractionWrapper.onBrickAtPosition(brickPosition)
            .checkShowsText(R.string.brick_place_at)
        onView(withId(R.id.brick_place_at_edit_text_x))
            .perform(ViewActions.click())
        onView(withText(R.string.brick_context_dialog_formula_edit_brick))
            .perform(ViewActions.click())
        FormulaEditorWrapper.onFormulaEditor()
            .performOpenDataFragment()
        FormulaEditorDataListWrapper.onDataList().onVariableAtPosition(0)
            .performRename(NEW_VARIABLE_NAME)

        FormulaEditorDataListWrapper.onDataList()
            .performClose()

        Espresso.pressBack()
        onView(withText(R.string.formula_editor_discard_changes_dialog_title))
            .inRoot(RootMatchers.isDialog())
            .check(matches(ViewMatchers.isDisplayed()))
    }
    @Category(AppUi::class, Smoke::class)
    @Test
    fun testRevertChangesDialogDoesNotAppear() {

        BrickDataInteractionWrapper.onBrickAtPosition(brickPosition)
            .checkShowsText(R.string.brick_place_at)

        onView(withId(R.id.brick_place_at_edit_text_x))
            .perform(ViewActions.click())
        onView(withText(R.string.brick_context_dialog_formula_edit_brick))
            .perform(ViewActions.click())

        Espresso.pressBack()
        onView(withText(R.string.formula_editor_discard_changes_dialog_title))
            .check(doesNotExist())
    }
}
