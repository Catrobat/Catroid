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

import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.catrobat.catroid.ui.SignInActivity
import org.junit.Before
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.action.ViewActions
import org.catrobat.catroid.R
import androidx.test.espresso.assertion.ViewAssertions
import org.junit.Rule
import org.junit.Test

class LoginDialogFragmentTest {
    @Rule @JvmField
    var activityRule: ActivityScenarioRule<SignInActivity> =
    ActivityScenarioRule<SignInActivity>(
        SignInActivity::class.java
    )

    @Before
    fun openLoginDialog() {
        Espresso.onView(ViewMatchers.withText("Login"))
            .perform(ViewActions.click())
    }

    @Test
    fun cancelButtonTest() {
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withText("Cancel")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_login_username))
            .check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withId(R.id.dialog_login_password))
            .check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withText("Cancel")).check(ViewAssertions.doesNotExist())
    }
}
