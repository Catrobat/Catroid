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

import android.preference.PreferenceManager
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.collect.Ordering
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys.SORT_VARIABLE_PREFERENCE_KEY
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.recyclerview.adapter.DataListAdapter
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
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
    private var bufferedSortVariablePreference: Boolean = false

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())

        bufferedSortVariablePreference = sharedPreferences
            .getBoolean(SORT_VARIABLE_PREFERENCE_KEY, false)

        sharedPreferences
            .edit()
            .putBoolean(SORT_VARIABLE_PREFERENCE_KEY, false)
            .apply()

        val script = UiTestUtils.createProjectAndGetStartScript("FormulaEditorEditVariableTest")
        script.addBrick(ChangeSizeByNBrick(0.0))
        baseActivityTestRule.launchActivity()

        onBrickAtPosition(0)
            .checkShowsText(R.string.brick_when_started)
        onBrickAtPosition(1)
            .checkShowsText(R.string.brick_change_size_by)
        onBrickAtPosition(1)
            .onChildView(withId(R.id.brick_change_size_by_edit_text))
            .perform(click())
        onFormulaEditor()
            .performOpenDataFragment()
    }

    @After
    fun tearDown() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            .edit()
            .putBoolean(SORT_VARIABLE_PREFERENCE_KEY, bufferedSortVariablePreference)
            .apply()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun dataListSortingTest() {
        onDataList()
            .performAdd(variableName1)
        onDataList()
            .performAdd(variableName2)
        onDataList()
            .performAdd(variableName3)
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.sort))
            .perform(click())
        onView(withId(R.id.recycler_view))
            .check(matches(isSortedLexicographically))
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.undo_sort))
            .perform(click())
        onDataList().onVariableAtPosition(0)
            .checkHasName(variableName1)
        onDataList().onVariableAtPosition(1)
            .checkHasName(variableName2)
        onDataList().onVariableAtPosition(2)
            .checkHasName(variableName3)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deletionOnSortedList() {
        onDataList()
            .performAdd(variableName1)
        onDataList()
            .performAdd(variableName2)
        onDataList()
            .performAdd(variableName3)
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.sort))
            .perform(click())
        onDataList().onVariableAtPosition(0)
            .performDelete()
        onRecyclerView()
            .checkHasNumberOfItems(2)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun renameOnSortedList() {
        onDataList()
            .performAdd(variableName1)
        onDataList()
            .performAdd(variableName2)
        onDataList()
            .performAdd(variableName3)
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.sort))
            .perform(click())
        onDataList().onVariableAtPosition(2)
            .performRename(variableName6)
        onView(withId(R.id.recycler_view))
            .check(matches(isSortedLexicographically))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun differentScopesSortedList() {
        onDataList()
            .performAdd(variableName1)
        onDataList()
            .performAdd(variableName2)
        onDataList()
            .performAdd(variableName3)
        onDataList()
            .performAdd(
                variableName6,
                FormulaEditorDataListWrapper.ItemType.VARIABLE,
                FormulaEditorDataListWrapper.ItemScope.LOCAL
            )
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.sort))
            .perform(click())
        onDataList().onVariableAtPosition(0)
            .checkHasName(variableName2)
        onDataList().onVariableAtPosition(1)
            .checkHasName(variableName3)
        onDataList().onVariableAtPosition(2)
            .checkHasName(variableName1)
        onDataList().onVariableAtPosition(3)
            .checkHasName(variableName6)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun insertionOnEmptySortedList() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.sort))
            .perform(click())
        onDataList()
            .performAdd(variableName1)
        onDataList()
            .performAdd(variableName2)
        onDataList()
            .performAdd(variableName3)
        onView(withId(R.id.recycler_view))
            .check(matches(isSortedLexicographically))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun insertionOnSortedList() {
        onDataList()
            .performAdd(variableName1)
        onDataList()
            .performAdd(variableName2)
        onDataList()
            .performAdd(variableName3)
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.sort))
            .perform(click())
        onDataList()
            .performAdd(variableName4)
        onDataList()
            .performAdd(variableName5)
        onDataList()
            .performAdd(variableName6)
        onView(withId(R.id.recycler_view))
            .check(matches(isSortedLexicographically))
        onDataList()
            .performClose()
    }

    companion object {
        private const val variableName1 = "z"
        private const val variableName2 = "x"
        private const val variableName3 = "y"
        private const val variableName4 = "c"
        private const val variableName5 = "b"
        private const val variableName6 = "a"
        private val isSortedLexicographically: Matcher<View>
            get() = object : TypeSafeMatcher<View>() {
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
