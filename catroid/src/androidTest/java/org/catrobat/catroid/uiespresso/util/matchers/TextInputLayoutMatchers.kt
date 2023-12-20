/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.util.matchers

import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

fun showsText(expectedText: String): TypeSafeMatcher<View> {
    return object :
        TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) = Unit

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val editText = item.editText ?: return false
            val text = editText.toString()
            return text == expectedText
        }
    }
}

fun showsHintText(expectedHintText: String): TypeSafeMatcher<View> {
    return object :
        TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) = Unit

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val hint = item.hint ?: return false
            val hintText = hint.toString()
            return expectedHintText == hintText
        }
    }
}

fun showsErrorText(expectedErrorText: String): TypeSafeMatcher<View> {
    return object :
        TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) = Unit

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val error = item.error ?: return false
            val errorText = error.toString()
            return expectedErrorText == errorText
        }
    }
}
