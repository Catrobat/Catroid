/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.collect.Ordering
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.recyclerview.adapter.DataListAdapter
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class FormulaEditorSortDataListTest {
    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorEditVariableTest")
        script.addBrick(ChangeSizeByNBrick(0.0))
        baseActivityTestRule.launchActivity()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun dataListSortingTest() {
        BrickDataInteractionWrapper.onBrickAtPosition(whenBrickPosition)
            .checkShowsText(R.string.brick_when_started)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .checkShowsText(R.string.brick_change_size_by)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .onChildView(ViewMatchers.withId(R.id.brick_change_size_by_edit_text))
            .perform(ViewActions.click())
        FormulaEditorWrapper.onFormulaEditor()
            .performOpenDataFragment()
        FormulaEditorDataListWrapper.onDataList()
            .performAdd(variableName1)
        FormulaEditorDataListWrapper.onDataList()
            .performAdd(variableName2)
        FormulaEditorDataListWrapper.onDataList()
            .performAdd(variableName3)
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(ViewMatchers.withText(R.string.sort))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view)).check(
            ViewAssertions.matches(
                isSortedLexicographically
            )
        )
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(ViewMatchers.withText(R.string.undo_sort))
            .perform(ViewActions.click())
        FormulaEditorDataListWrapper.onDataList()
            .performClose()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deletionOnSortedList() {
        BrickDataInteractionWrapper.onBrickAtPosition(whenBrickPosition)
            .checkShowsText(R.string.brick_when_started)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .checkShowsText(R.string.brick_change_size_by)
        BrickDataInteractionWrapper.onBrickAtPosition(changeSizeBrickPosition)
            .onChildView(ViewMatchers.withId(R.id.brick_change_size_by_edit_text))
            .perform(ViewActions.click())
        FormulaEditorWrapper.onFormulaEditor()
            .performOpenDataFragment()
        FormulaEditorDataListWrapper.onDataList()
            .performAdd(variableName1)
        FormulaEditorDataListWrapper.onDataList()
            .performAdd(variableName2)
        FormulaEditorDataListWrapper.onDataList()
            .performAdd(variableName3)
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(ViewMatchers.withText(R.string.sort))
            .perform(ViewActions.click())
        FormulaEditorDataListWrapper.onDataList().onVariableAtPosition(0)
            .performDelete()
        RecyclerViewInteractionWrapper.onRecyclerView()
            .checkHasNumberOfItems(2)
    }

    companion object {
        private const val variableName1 = "z"
        private const val variableName2 = "x"
        private const val variableName3 = "y"
        private const val whenBrickPosition = 0
        private const val changeSizeBrickPosition = 1
        private val isSortedLexicographically: Matcher<View>
            private get() = object : TypeSafeMatcher<View>() {
                private val variables: MutableList<String> = ArrayList()
                override fun matchesSafely(item: View): Boolean {
                    val recyclerView = item as RecyclerView
                    val dataListAdapter = recyclerView.adapter as DataListAdapter?
                    variables.clear()
                    variables.addAll(extractDataNames(dataListAdapter!!.items))
                    return Ordering.natural<Comparable<*>>().isOrdered(variables)
                }

                private fun extractDataNames(dataList: List<UserData<*>>): List<String> {
                    val dataNames: MutableList<String> = ArrayList()
                    for (data in dataList) {
                        dataNames.add(data.name)
                    }
                    return dataNames
                }

                override fun describeTo(description: Description) {
                    description.appendText("has items sorted alphabetically: ")
                }
            }
    }
}
