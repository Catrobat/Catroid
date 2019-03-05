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

package org.catrobat.catroid.uiespresso.content.brick.utils;

import android.support.test.espresso.DataInteraction;
import android.widget.EditText;

import org.catrobat.catroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

public class BroadcastBrickSpinnerDataInteractionWrapper extends BrickSpinnerDataInteractionWrapper {
	public BroadcastBrickSpinnerDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public BroadcastBrickSpinnerDataInteractionWrapper createNewBroadcastMessage(String message) {
		perform(click());
		onView(withText(R.string.new_option))
				.perform(click());
		onView(allOf(withId(R.id.input_edit_text), isDisplayed(), instanceOf(EditText.class)))
				.perform(typeText(message), closeSoftKeyboard());

		onView(withId(android.R.id.button1))
				.perform(click());

		return new BroadcastBrickSpinnerDataInteractionWrapper(dataInteraction);
	}
}
