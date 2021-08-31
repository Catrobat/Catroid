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
package org.catrobat.catroid.uiespresso.content.messagecontainer

import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ui.SpriteActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import org.catrobat.catroid.R
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.hamcrest.Matchers.allOf
import java.lang.AssertionError

class BroadcastMessageBrickTestUtils private constructor() {
    companion object {
        fun createNewBroadcastMessageOnBrick(
            message: String?,
            brickPosition: Int,
            activity: AppCompatActivity?
        ) {
            if (activity !is SpriteActivity) {
                return
            }
            onBrickAtPosition(brickPosition).performEditBroadcastMessage()

            onView(withId(R.id.button_add)).perform(click())

            onView(withText(R.string.dialog_new_broadcast_message_title))
                .check(matches(isDisplayed()))

            onView(withId(R.id.input_edit_text))
                .perform(clearText()).perform(typeText(message))

            onView(withText(R.string.ok)).perform(click())
        }

        fun editBroadcastMessageOnBrick(
            oldMessage: String?,
            newMessage: String?,
            brickPosition: Int,
            activity: AppCompatActivity?
        ) {
            if (activity !is SpriteActivity) {
                return
            }
            onBrickAtPosition(brickPosition).performEditBroadcastMessage()

            onView(allOf(withParent(withChild(withText(oldMessage))), withId(R.id.settingsButton)))
                .perform(click())

            onView(withText(R.string.rename)).perform(click())

            onView(withId(R.id.input_edit_text))
                .perform(clearText()).perform(typeText(newMessage))

            onView(withText(R.string.ok)).perform(click())

            pressBack()
        }

        fun selectBroadcastMessageOnBrick(
            selectMessage: String,
            brickPosition: Int,
            activity: AppCompatActivity?
        ) {
            if (activity !is SpriteActivity) {
                return
            }
            onBrickAtPosition(brickPosition).performEditBroadcastMessage()

            onView(withText(selectMessage)).perform(click())
        }

        fun checkBroadcastMessageDoesNotExist(
            checkMessage: String,
            brickPosition: Int,
            activity: AppCompatActivity?
        ) {
            if (activity !is SpriteActivity) {
                return
            }
            onBrickAtPosition(brickPosition).performEditBroadcastMessage()

            onView(withText(checkMessage)).check(doesNotExist())

            pressBack()
        }
    }

    init {
        throw AssertionError()
    }
}
