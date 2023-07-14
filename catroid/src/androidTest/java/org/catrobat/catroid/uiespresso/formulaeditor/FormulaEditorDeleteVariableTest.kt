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

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.ItemType
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.ItemScope
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormulaEditorDeleteVariableTest {
    var script: Script? = null

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        script = UiTestUtils.createProjectAndGetStartScript("FormulaEditorDeleteVariableTest")
        script!!.addBrick(ChangeSizeByNBrick(0.0))
        script!!.addBrick(SetXBrick(0))
        baseActivityTestRule.launchActivity()
        onBrickAtPosition(1).onFormulaTextField(R.id.brick_change_size_by_edit_text)
            .perform(click())
        onFormulaEditor().performOpenDataFragment()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteVariableFromMenuTest() {
        val itemName = "item"
        onDataList().performAdd(itemName)
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.delete)).perform(click())
        onDataList().onVariableAtPosition(0).performCheckItemClick()
        ActionModeWrapper.onActionMode().performConfirm()
        onRecyclerView().checkHasNumberOfItems(0)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteLocalVariableTest() {
        val itemName = "item"
        onDataList().performAdd(itemName, ItemType.VARIABLE, ItemScope.LOCAL)
        onDataList().onVariableAtPosition(0).performDelete()
        onRecyclerView().checkHasNumberOfItems(0)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteGlobalVariableTest() {
        val itemName = "item"
        onDataList().performAdd(itemName, ItemType.VARIABLE, ItemScope.GLOBAL)
        onDataList().onVariableAtPosition(0).performDelete()
        onRecyclerView().checkHasNumberOfItems(0)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteLocalVariableWhenInUseTest() {
        val itemName = "item"
        onDataList().performAdd(itemName, ItemType.VARIABLE, ItemScope.LOCAL)

        onDataList().onVariableAtPosition(0).performSelect()
        onView(isRoot()).perform(pressBack())
        onBrickAtPosition(2).onFormulaTextField(R.id.brick_set_x_edit_text).perform(click())
        onFormulaEditor().performOpenDataFragment()
        onDataList().onVariableAtPosition(0).performDelete()

        onView(withText(R.string.deletion_alert_warning)).check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
        onRecyclerView().checkHasNumberOfItems(1)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteGlobalVariableWhenInUseTest() {
        val itemName = "item"
        onDataList().performAdd(itemName, ItemType.VARIABLE, ItemScope.GLOBAL)

        onDataList().onVariableAtPosition(0).performSelect()
        onView(isRoot()).perform(pressBack())
        onBrickAtPosition(2).onFormulaTextField(R.id.brick_set_x_edit_text).perform(click())
        onFormulaEditor().performOpenDataFragment()
        onDataList().onVariableAtPosition(0).performDelete()

        onView(withText(R.string.deletion_alert_warning)).check(matches(isDisplayed()))
        onView(withText(R.string.ok)).perform(click())
        onRecyclerView().checkHasNumberOfItems(1)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteLocalVariableWhenInDropDownTest() {
        script!!.addBrick(SetVariableBrick(0.0))
        val itemName = "item"
        onDataList().performAdd(itemName, ItemType.VARIABLE, ItemScope.LOCAL)

        onView(isRoot()).perform(pressBack())
        onView(isRoot()).perform(pressBack())
        onBrickAtPosition(2).onFormulaTextField(R.id.brick_set_x_edit_text).perform(click())
        onFormulaEditor().performOpenDataFragment()
        onDataList().onVariableAtPosition(0).performDelete()

        onView(withText(R.string.deletion_alert_warning)).check(matches(isDisplayed()))
        onView(withText(R.string.no)).check(matches(isDisplayed()))
        onView(withText(R.string.deletion_alert_yes)).check(matches(isDisplayed())).perform(click())
        onRecyclerView().checkHasNumberOfItems(0)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteGlobalVariableWhenInDropDownTest() {
        script!!.addBrick(SetVariableBrick(0.0))
        val itemName = "item"
        onDataList().performAdd(itemName, ItemType.VARIABLE, ItemScope.GLOBAL)

        onView(isRoot()).perform(pressBack())
        onView(isRoot()).perform(pressBack())
        onBrickAtPosition(2).onFormulaTextField(R.id.brick_set_x_edit_text).perform(click())
        onFormulaEditor().performOpenDataFragment()
        onDataList().onVariableAtPosition(0).performDelete()

        onView(withText(R.string.deletion_alert_warning)).check(matches(isDisplayed()))
        onView(withText(R.string.no)).check(matches(isDisplayed()))
        onView(withText(R.string.deletion_alert_yes)).check(matches(isDisplayed())).perform(click())
        onRecyclerView().checkHasNumberOfItems(0)
    }
}
