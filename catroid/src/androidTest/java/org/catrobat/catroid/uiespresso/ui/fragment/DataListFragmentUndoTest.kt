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

package org.catrobat.catroid.uiespresso.ui.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataListFragmentUndoTest {
    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val script = createProjectAndGetStartScript("DataListFragmentUndoTest")
        script.addBrick(ChangeSizeByNBrick(0.0))
        baseActivityTestRule.launchActivity()
        onView(withId(R.id.brick_change_size_by_edit_text))
            .perform(click())
        onFormulaEditor()
            .performOpenDataFragment()
    }

    @Test
    fun emptyViewOnStartTest() {
        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(isDisplayed()))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun undoDeleteVariableTest() {
        val variableName = "item"
        onDataList()
            .performAdd(variableName)

        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(not(isDisplayed())))

        onDataList().onVariableAtPosition(0)
            .performDelete()
        onRecyclerView()
            .checkHasNumberOfItems(0)

        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(isDisplayed()))

        onDataList().performUndo()

        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(not(isDisplayed())))

        onDataList()
            .onVariableAtPosition(0)
            .checkHasName(variableName)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun undoEditVariableTest() {
        val variableName = "item"
        val value1 = "test"
        val value2 = "123"
        onDataList()
            .performAdd(variableName)

        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(not(isDisplayed())))

        onDataList().onVariableAtPosition(0)
            .performEdit(value1)
        onDataList()
            .onVariableAtPosition(0)
            .checkHasValue(value1)

        onDataList().onVariableAtPosition(0)
            .performEdit(value2)
        onDataList()
            .onVariableAtPosition(0)
            .checkHasValue(value2)

        onDataList().performUndo()

        onDataList()
            .onVariableAtPosition(0)
            .checkHasValue(value1)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun undoRenameVariableTest() {
        val variableName = "item"
        val variableName2 = "myVariable"
        onDataList()
            .performAdd(variableName)

        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(not(isDisplayed())))

        onDataList().onVariableAtPosition(0)
            .performRename(variableName2)

        onDataList()
            .onVariableAtPosition(0)
            .checkHasName(variableName2)

        onDataList().performUndo()

        onDataList()
            .onVariableAtPosition(0)
            .checkHasName(variableName)
    }
}
