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

package org.catrobat.catroid.uiespresso.formulaeditor

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormulaEditorBluetoothConnectionTest {

    @Rule
    @JvmField
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val script = createProjectAndGetStartScript("FormulaEditorBluetoothConnectionTest")
        script.addBrick(ChangeSizeByNBrick(0.0))

        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())

        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_SHOW_ARDUINO_BRICKS, true)
            .commit()

        baseActivityTestRule.launchActivity()
        Espresso.onView(withId(R.id.brick_change_size_by_edit_text))
            .perform(ViewActions.click())
        FormulaEditorWrapper.onFormulaEditor()
            .performOpenCategory(FormulaEditorWrapper.Category.DEVICE)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun arduinoDoesNotCrashTest() {
        Espresso.onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, ViewActions.click()))
        Espresso.onView(FormulaEditorWrapper.Control.COMPUTE)
            .perform(ViewActions.click())
    }
}