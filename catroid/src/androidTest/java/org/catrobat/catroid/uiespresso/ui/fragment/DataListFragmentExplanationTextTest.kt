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

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
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
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper.onActionMode
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataListFragmentExplanationTextTest {
    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val script = UiTestUtils.createProjectAndGetStartScript("DataListFragmentExplanationTextNoObjectTest")
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
    fun addVariableTest() {
        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(isDisplayed()))

        val varName = "Variable"
        onDataList()
            .performAdd(varName)
        onDataList().onVariableAtPosition(0)
            .checkHasName(varName)

        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(not(isDisplayed())))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun addListTest() {
        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(isDisplayed()))

        val listName = "List"
        onDataList()
            .performAdd(listName, FormulaEditorDataListWrapper.ItemType.LIST)
        onDataList()
            .onListAtPosition(0).checkHasName(listName)

        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(not(isDisplayed())))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteVariableTest() {
        val itemName = "item"
        onDataList()
            .performAdd(itemName)

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
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    @Throws(InterruptedException::class)
    fun deleteVariableFromMenuTest() {
        val itemName = "item"
        onDataList()
            .performAdd(itemName)

        onView(withId(R.id.empty_view))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(not(isDisplayed())))

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete))
            .perform(click())
        onDataList().onVariableAtPosition(0)
            .performCheckItemClick()
        onActionMode()
            .performConfirm()
        onView(withId(android.R.id.button1))
            .perform(click())
        onRecyclerView().checkHasNumberOfItems(0)

        onView(withId(R.id.empty_view))
            .check(matches(isDisplayed()))
        onView(withText(R.string.fragment_data_text_description))
            .check(matches(isDisplayed()))
    }
}
