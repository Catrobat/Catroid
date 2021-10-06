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
package org.catrobat.catroid.uiespresso.intents.loginfragment

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.SignInActivity
import org.catrobat.catroid.uiespresso.util.actions.replaceEditText
import org.catrobat.catroid.uiespresso.util.matchers.showsErrorText
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginDialogFragmentTest {
    @Rule
    @JvmField
    var activityRule: BaseActivityTestRule<SignInActivity> =
        BaseActivityTestRule<SignInActivity>(
            SignInActivity::class.java
        )

    @Before
    fun openLoginDialog() {
        onView(withText("Login"))
            .perform(click())
    }

    @Test
    fun cancelButtonTest() {
        closeSoftKeyboard()
        onView(withText("Cancel"))
            .perform(click())
        onView(withId(R.id.dialog_login_username))
            .check(doesNotExist())
        onView(withId(R.id.dialog_login_password))
            .check(doesNotExist())
        onView(withText("Cancel"))
            .check(doesNotExist())
    }

    @Test
    fun testLoginWithInvalidUsername() {
        onView(withId(R.id.dialog_login_username))
            .perform(replaceEditText("!In#valid?"))
        onView(withId(R.id.dialog_login_username))
            .check(matches(showsErrorText(activityRule.activity
                            .getString(R.string.error_register_invalid_username))))
    }

    @Test
    fun testLoginWithTooShortPassword() {
        onView(withId(R.id.dialog_login_password))
            .perform(replaceEditText("short"))
        onView(withId(R.id.dialog_login_password))
            .check(matches(showsErrorText(activityRule.activity
                            .getString(R.string.error_register_password_at_least_6_characters))))
    }

    @Test
    fun testInputsContainOnlyWhitespaces() {
        onView(withId(R.id.dialog_login_username))
            .perform(replaceEditText(""))
        onView(withId(R.id.dialog_login_password))
            .perform(replaceEditText(""))

        onView(withId(R.id.dialog_login_username))
            .check(matches(showsErrorText(activityRule.activity
                            .getString(R.string.error_register_empty_username))))
        onView(withId(R.id.dialog_login_password))
            .check(matches(showsErrorText(activityRule.activity
                            .getString(R.string.error_register_empty_password))))

        onView(allOf(withText(R.string.login), isClickable()))
            .check(matches(not(isEnabled())))
    }
}
