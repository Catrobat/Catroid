/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.hints

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys.DISABLE_HINTS_DIALOG_SHOWN_PREFERENCE_KEY
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_HINTS
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.actions.selectTabAtPosition
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.catrobat.catroid.utils.SnackbarUtil
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject


@RunWith(AndroidJUnit4::class)
class HintsShownInCorrectActivityTest {

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SpriteActivity::class.java, true, false
    )
    val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
    private val projectName: String? = HintsShownInCorrectActivityTest::class.simpleName
    private lateinit var script: Script

    private var savedBooleanValue: Boolean = false
    private lateinit var savedShownHintsList: Set<String>

    private val SNACKBAR_ANIMATION_DURATION: Long = 300

    @Before
    fun setUp() {
        savedBooleanValue = sharedPreferences.getBoolean(SETTINGS_SHOW_HINTS, false)
        savedShownHintsList = sharedPreferences.getStringSet(SnackbarUtil.SHOWN_HINT_LIST, HashSet<String>()) ?: HashSet()

        sharedPreferences.edit()
            .remove(SnackbarUtil.SHOWN_HINT_LIST)
            .putBoolean(DISABLE_HINTS_DIALOG_SHOWN_PREFERENCE_KEY, true)
            .putBoolean(SETTINGS_SHOW_HINTS, true)
            .apply()

        script = UiTestUtils.createProjectAndGetStartScript(projectName)
        script.addBrick(SetXBrick())

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit()
            .putBoolean(SETTINGS_SHOW_HINTS, savedBooleanValue)
            .putStringSet(SnackbarUtil.SHOWN_HINT_LIST, savedShownHintsList)
            .apply()
    }

    @Test
    fun scriptsHintNotShownInFormulaEditorTest() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withId(R.id.brick_set_x_edit_text))
            .perform(click())

        onView(withText(R.string.formula_editor_intro_summary_formula_editor))
            .check(matches(isDisplayed()))

        checkIfSnackBarIsNotDisplayed(R.string.hint_scripts)
    }

    @Test
    fun hintsChangeCorrectlyBeforeDismissTest() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))

        checkIfSnackBarIsDisplayed(R.string.hint_looks)

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SOUNDS))

        checkIfSnackBarIsDisplayed(R.string.hint_sounds)

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS))

        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        checkIfSnackBarIsNotDisplayed(R.string.hint_sounds)

        checkIfSnackBarIsNotDisplayed(R.string.hint_looks)
    }

    @Test
    fun looksHintNotShownInScriptFragment() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_LOOKS))

        checkIfSnackBarIsDisplayed(R.string.hint_looks)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS))

        checkIfSnackBarIsNotDisplayed(R.string.hint_scripts)

        checkIfSnackBarIsNotDisplayed(R.string.hint_looks)
    }

    @Test
    fun soundsHintNotShownInScriptFragment() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SOUNDS))

        checkIfSnackBarIsDisplayed(R.string.hint_sounds)

        onView(withId(R.id.tab_layout))
            .perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS))

        checkIfSnackBarIsNotDisplayed(R.string.hint_scripts)
    }

    @Test
    fun brickCategoriesHintNotShownInScriptFragment() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.button_add))
            .perform(click())

        checkIfSnackBarIsDisplayed(R.string.hint_category)

        pressBack()

        checkIfSnackBarIsNotDisplayed(R.string.hint_category)
    }

    @Test
    fun bricksHintNotShownInScriptFragment() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.button_add))
            .perform(click())

        checkIfSnackBarIsDisplayed(R.string.hint_category)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withText(R.string.category_event))
            .perform(click())

        checkIfSnackBarIsDisplayed(R.string.hint_bricks)

        pressBack()

        pressBack()

        checkIfSnackBarIsNotDisplayed(R.string.hint_bricks)

        checkIfSnackBarIsNotDisplayed(R.string.hint_scripts)
    }

    @Test
    fun bricksHintNotShownInBrickCategoryFragment() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.button_add))
            .perform(click())

        checkIfSnackBarIsDisplayed(R.string.hint_category)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withText(R.string.category_event))
            .perform(click())

        checkIfSnackBarIsDisplayed(R.string.hint_bricks)

        pressBack()

        checkIfSnackBarIsNotDisplayed(R.string.hint_bricks)

        checkIfSnackBarIsNotDisplayed(R.string.hint_category)
    }

    @Test
    fun brickAndCategoryHintsChangeCorrectlyBeforeDismissTest() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.button_add))
            .perform(click())

        checkIfSnackBarIsDisplayed(R.string.hint_category)

        onView(withText(R.string.category_event))
            .perform(click())

        checkIfSnackBarIsDisplayed(R.string.hint_bricks)

        pressBack()

        checkIfSnackBarIsNotDisplayed(R.string.hint_bricks)

        checkIfSnackBarIsDisplayed(R.string.hint_category)
    }

    @Test
    fun bricksHintIsNotShownAfterChoosingBrickTest() {
        checkIfSnackBarIsDisplayed(R.string.hint_scripts)

        onView(withText(R.string.got_it))
            .perform(click())

        onView(withId(R.id.button_add))
            .perform(click())

        onView(withText(R.string.category_motion))
            .perform(scrollTo(), click())

        checkIfSnackBarIsDisplayed(R.string.hint_bricks)

        onView(withText(R.string.brick_set_y))
            .perform(click())

        checkIfSnackBarIsNotDisplayed(R.string.hint_bricks)

        checkIfSnackBarIsNotDisplayed(R.string.hint_category)

        checkIfSnackBarIsNotDisplayed(R.string.hint_scripts)
    }

    fun checkIfSnackBarIsDisplayed(snackBarText: Int)
    {
        Thread.sleep(SNACKBAR_ANIMATION_DURATION)
        onView(withText(snackBarText)).check(matches(isDisplayed()))
        Thread.sleep(50)
    }

    fun checkIfSnackBarIsNotDisplayed(snackBarText: Int)
    {
        Thread.sleep(SNACKBAR_ANIMATION_DURATION)
        onView(withText(snackBarText)).check(doesNotExist())
        Thread.sleep(50)
    }
}
