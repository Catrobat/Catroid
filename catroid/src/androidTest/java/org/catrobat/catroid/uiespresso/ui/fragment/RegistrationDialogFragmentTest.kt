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

package org.catrobat.catroid.uiespresso.ui.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.SignInActivity
import org.catrobat.catroid.uiespresso.util.actions.replaceEditText
import org.catrobat.catroid.uiespresso.util.matchers.showsErrorText
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@Category(AppUi::class, Smoke::class)
@RunWith(AndroidJUnit4::class)
class RegistrationDialogFragmentTest {
    @Rule
    @JvmField
    var activityRule = BaseActivityTestRule(
        SignInActivity::class.java, false, true
    )

    @Before
    fun setUp() {
        onView(withId(R.id.sign_in_register)).perform(click())
    }

    @Test
    fun testRegisterWithInvalidUsername() {
        onView(withId(R.id.dialog_register_username))
            .perform(replaceEditText("!In#valid?"))
        onView(withId(R.id.dialog_register_username))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_invalid_username)
                    )
                )
            )
    }

    @Test
    fun testRegisterWithInvalidUsernameStartWith() {
        onView(withId(R.id.dialog_register_username))
            .perform(replaceEditText("_Username"))
        onView(withId(R.id.dialog_register_username))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_username_start_with)
                    )
                )
            )
    }

    @Test
    fun testRegisterWithEmailAsUsername() {
        onView(withId(R.id.dialog_register_username))
            .perform(replaceEditText("email@test.at"))
        onView(withId(R.id.dialog_register_username))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_username_as_email)
                    )
                )
            )
    }

    @Test
    fun testRegisterWithInvalidEmail() {
        onView(withId(R.id.dialog_register_email))
            .perform(replaceEditText("invalidEmail"))
        onView(withId(R.id.dialog_register_email))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_invalid_email_format)
                    )
                )
            )
    }

    @Test
    fun testRegisterWithTooShortPassword() {
        onView(withId(R.id.dialog_register_password))
            .perform(replaceEditText("short"))
        onView(withId(R.id.dialog_register_password))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_password_at_least_6_characters)
                    )
                )
            )
    }

    @Test
    fun testRegisterWithPasswordsNotMatching() {
        onView(withId(R.id.dialog_register_password))
            .perform(replaceEditText("password1"))
        onView(withId(R.id.dialog_register_password_confirm))
            .perform(replaceEditText("password2"))
        onView(withId(R.id.dialog_register_password_confirm))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_passwords_mismatch)
                    )
                )
            )
    }

    @Test
    fun testInputsContainOnlyWhitespaces() {
        onView(withId(R.id.dialog_register_username))
            .perform(replaceEditText(""))
        onView(withId(R.id.dialog_register_email))
            .perform(replaceEditText(" "))
        onView(withId(R.id.dialog_register_password))
            .perform(replaceEditText(""))
        onView(withId(R.id.dialog_register_password_confirm))
            .perform(replaceEditText(""))

        onView(withId(R.id.dialog_register_username))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_empty_username)
                    )
                )
            )

        onView(withId(R.id.dialog_register_email))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_invalid_email_format)
                    )
                )
            )
        onView(withId(R.id.dialog_register_password))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_empty_password)
                    )
                )
            )
        onView(withId(R.id.dialog_register_password_confirm))
            .check(
                matches(
                    showsErrorText(
                        activityRule.activity
                            .getString(R.string.error_register_empty_confirm_password)
                    )
                )
            )

        onView(allOf(withText(R.string.register), isClickable())).check(matches(not(isEnabled())))
    }
}
