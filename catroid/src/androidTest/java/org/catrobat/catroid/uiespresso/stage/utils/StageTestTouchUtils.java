/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.stage.utils;

import android.view.MotionEvent;
import android.view.View;

import org.hamcrest.Matcher;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.MotionEvents;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

public final class StageTestTouchUtils {
	private StageTestTouchUtils() {
		throw new AssertionError();
	}

	private static MotionEvent down = null;
	public static ViewAction touchDown(final float x, final float y) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isDisplayed();
			}

			@Override
			public String getDescription() {
				return "Send touch down event.";
			}

			@Override
			public void perform(UiController uiController, final View view) {
				// Get view absolute position
				int[] location = new int[2];
				view.getLocationOnScreen(location);

				// Offset coordinates by view position
				float[] coordinates = new float[]{x + location[0], y + location[1]};
				float[] precision = new float[]{1f, 1f};

				// Send down event
				down = MotionEvents.sendDown(uiController, coordinates, precision).down;
				uiController.loopMainThreadForAtLeast(200);
			}
		};
	}
	public static ViewAction touchUp(final float x, final float y) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isDisplayed();
			}

			@Override
			public String getDescription() {
				return "Send touch up event.";
			}

			@Override
			public void perform(UiController uiController, final View view) {
				// Get view absolute position
				int[] location = new int[2];
				view.getLocationOnScreen(location);

				// Offset coordinates by view position
				float[] coordinates = new float[]{x + location[0], y + location[1]};

				// Send up event
				MotionEvents.sendUp(uiController, down, coordinates);
				down = null;
			}
		};
	}
}
