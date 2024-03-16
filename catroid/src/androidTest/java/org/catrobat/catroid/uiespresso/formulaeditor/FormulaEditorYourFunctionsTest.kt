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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ReportBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.test.BuildConfig
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormulaEditorYourFunctionsTest {
    private var userDefinedBrick: UserDefinedBrick? = null
    private var userDefinedRecBrick: UserDefinedReceiverBrick? = null

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java, SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @After
    @kotlin.jvm.Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(FormulaEditorUserDefinedBrickTest::class.java.simpleName)
    }

    @Before
    @kotlin.jvm.Throws(Exception::class)
    fun setUp() {
        org.junit.Assume.assumeTrue(BuildConfig.FEATURE_USER_REPORTERS_ENABLED)
        createProject()
        baseActivityTestRule.launchActivity()

        onView(withId(R.id.brick_change_size_by_edit_text))
            .perform(click())
    }

    @Test
    @kotlin.jvm.Throws(Throwable::class)
    fun testCheckYourFunctionIsPresent() {
        onView(withId(R.id.formula_editor_keyboard_your_functions))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withText(userDefinedRecBrick?.nameForFormulaEditor))
            .check(matches(isDisplayed()))
            .perform(click())
    }

    private fun createProject() {
        val script = BrickTestUtils.createProjectAndGetStartScript(
            "FormulaEditorAddVariableTest"
        )

        val listUserData = listOf(label, input, secondInput)
        userDefinedBrick = UserDefinedBrick(listUserData)
        userDefinedBrick!!.setCallingBrick(true)
        userDefinedRecBrick = UserDefinedReceiverBrick(userDefinedBrick)

        val changeSizeByNBrick = ChangeSizeByNBrick(2.0)
        script.addBrick(changeSizeByNBrick)

        script.addBrick(userDefinedRecBrick)

        val reportBrick = ReportBrick()
        userDefinedRecBrick!!.script.addBrick(reportBrick)
    }

    companion object {
        private val label = UserDefinedBrickLabel("Label")
        private val input = UserDefinedBrickInput("Input")
        private val secondInput = UserDefinedBrickInput("SecondInput")
    }
}
