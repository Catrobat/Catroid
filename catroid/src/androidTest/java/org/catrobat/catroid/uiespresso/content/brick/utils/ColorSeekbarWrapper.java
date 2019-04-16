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

import android.support.test.espresso.Espresso;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.wrappers.ViewInteractionWrapper;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public final class ColorSeekbarWrapper extends ViewInteractionWrapper {
	public static final float MAX_COLOR_SEEKBAR_VALUE = 255.0f;
	public static final float MIN_SEEKBAR_VALUE = 0.0f;
	public static final Matcher<View> COLOR_SEEKBAR_MATCHER_ROOT = withId(R.id.rgb_base_layout);

	public static final Matcher<View> COLOR_SEEKBAR_MATCHER_RED = withId(R.id.color_rgb_seekbar_red);
	public static final Matcher<View> COLOR_SEEKBAR_MATCHER_GREEN = withId(R.id.color_rgb_seekbar_green);
	public static final Matcher<View> COLOR_SEEKBAR_MATCHER_BLUE = withId(R.id.color_rgb_seekbar_blue);

	private ColorSeekbarWrapper(Matcher<View> viewMatcher) {
		super(onView(viewMatcher));
	}

	public static ColorSeekbarWrapper onColorSeekbar() {
		return new ColorSeekbarWrapper(COLOR_SEEKBAR_MATCHER_ROOT);
	}

	public ColorSeekbarWrapper performSwipeRedSeekbar(float valueToBeEntered) {
		onView(COLOR_SEEKBAR_MATCHER_RED).perform(CustomSwipeAction.swipeToPosition(valueToBeEntered));
		return this;
	}

	public ColorSeekbarWrapper performSwipeBlueSeekbar(float valueToBeEntered) {
		onView(COLOR_SEEKBAR_MATCHER_BLUE).perform(CustomSwipeAction.swipeToPosition(valueToBeEntered));
		return this;
	}

	public ColorSeekbarWrapper performSwipeGreenSeekbar(float valueToBeEntered) {
		onView(COLOR_SEEKBAR_MATCHER_GREEN).perform(CustomSwipeAction.swipeToPosition(valueToBeEntered));
		return this;
	}
	public void closeAndSaveChanges() {
		Espresso.pressBack();
		onView(withId(android.R.id.button1)).perform(click());
	}

	public void closeAndDiscardChanges() {
		Espresso.pressBack();
		onView(withId(android.R.id.button2)).perform(click());
	}
}
