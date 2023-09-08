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
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.setLanguageSharedPreference
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorCategoryListWrapper.onCategoryList
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class FormulaEditorTouchesActorOrObjectTest {

    private val brickPosition = 1
    val applicationContext = ApplicationProvider.getApplicationContext<android.content.Context>()
    var bufferedPrivacyPolicyPreferenceSetting = 0
    val germanLocale = Locale.forLanguageTag("de")
    val projectName = "FormulaEditorTouchesActorOrObjectTest"
    val spriteName = "testSprite"

    @get:Rule
    var baseActivityTestRule: BaseActivityTestRule<MainMenuActivity> = BaseActivityTestRule(
        MainMenuActivity::class.java, false, false
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        bufferedPrivacyPolicyPreferenceSetting = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
            .getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit().putInt(
                AGREED_TO_PRIVACY_POLICY_VERSION,
                Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
            ).commit()

        setLanguageSharedPreference(applicationContext, "en")

        val script = UiTestUtils.createProjectAndGetStartScript(projectName)
        script.addBrick(ChangeSizeByNBrick(0.0))

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putInt(
                AGREED_TO_PRIVACY_POLICY_VERSION,
                bufferedPrivacyPolicyPreferenceSetting
            )
            .commit()
        SettingsFragment.removeLanguageSharedPreference(applicationContext)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun checkFormulaAfterLanguageChange() {
        onView(withText(applicationContext.getString(R.string.main_menu_programs))).perform(click())
        onView(withText(projectName)).perform(click())
        onView(withText(spriteName)).perform(click())
        onBrickAtPosition(brickPosition).onChildView(
            withId(R.id.brick_change_size_by_edit_text)
        ).perform(click())
        onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Control.PROPERTIES)
        onCategoryList().performSelect(R.string.formula_editor_function_collision)
        onView(withText(R.string.background)).perform(click())

        onFormulaEditor().checkShows(generateFormulaString())

        pressBack()
        pressBack()
        pressBack()
        pressBack()

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext())
        onView(withText(R.string.settings)).perform(click())
        onView(withText(R.string.preference_title_language)).perform(click())
        onData(Matchers.hasToString(germanLocale.getDisplayName(germanLocale))).perform(click())
        onView(withText(applicationContext.getString(R.string.main_menu_programs))).perform(click())
        onView(withText(projectName)).perform(click())
        onView(withText(spriteName)).perform(click())
        onBrickAtPosition(brickPosition).onChildView(
            withId(R.id.brick_change_size_by_edit_text)
        ).perform(click())

        onFormulaEditor().checkShows(generateFormulaString())
    }

    fun generateFormulaString(): String =
        applicationContext.getString(R.string.formula_editor_function_collision) +
            "(" + applicationContext.getString(R.string.background) + ")"
}
