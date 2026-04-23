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
package org.catrobat.catroid.uiespresso.content.brick.utils;

import android.graphics.Point;
import android.view.View;
import android.view.ViewConfiguration;

import org.hamcrest.Matcher;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.PrecisionDescriber;
import androidx.test.espresso.action.Swiper;
import androidx.test.espresso.util.HumanReadables;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

public final class CustomSwipeAction implements ViewAction {

	private static final int MAX_TRIES = 3;
	private static final int MIN_VISIBLE_AREA_PERCENTAGE = 90;

	private final SwipeAction swipeAction;
	private final Swiper swiper;
	private final PrecisionDescriber precisionDescriber;

	public enum SwipeAction {
		SWIPE_RIGHT,
		SWIPE_LEFT
	}

	public CustomSwipeAction(Swiper swiper, SwipeAction swipeAction, PrecisionDescriber precisionDescriber) {
		this.swiper = swiper;
		this.swipeAction = swipeAction;
		this.precisionDescriber = precisionDescriber;
	}

	@Override
	public Matcher<View> getConstraints() {
		return isDisplayingAtLeast(MIN_VISIBLE_AREA_PERCENTAGE);
	}

	@Override
	public void perform(UiController uiController, View view) {
		float[] startCoordinates = {0, 0};
		float[] endCoordinates = {0, 0};

		calculateCoordinates(view, startCoordinates, endCoordinates);

		float[] precision = precisionDescriber.describePrecision();

		Swiper.Status status = Swiper.Status.FAILURE;

		for (int tries = 0; tries < MAX_TRIES && status != Swiper.Status.SUCCESS; tries++) {
			try {
				status = swiper.sendSwipe(uiController, startCoordinates, endCoordinates, precision);
			} catch (RuntimeException e) {
				throw new PerformException.Builder()
						.withActionDescription(this.getDescription())
						.withViewDescription(HumanReadables.describe(view))
						.withCause(e)
						.build();
			}

			int duration = ViewConfiguration.getPressedStateDuration();

			if (duration > 0) {
				uiController.loopMainThreadForAtLeast(duration);
			}
		}

		if (status == Swiper.Status.FAILURE) {
			throw new PerformException.Builder()
					.withActionDescription(getDescription())
					.withViewDescription(HumanReadables.describe(view))
					.withCause(new RuntimeException(String.format(
							"Couldn't swipe from: %s,%s to: %s,%s precision: %s, %s . Swiper: %s "
									+ "precision describer: %s. Tried %s times",
							startCoordinates[0],
							startCoordinates[1],
							endCoordinates[0],
							endCoordinates[1],
							precision[0],
							precision[1],
							swiper,
							precisionDescriber,
							MAX_TRIES)))
					.build();
		}
	}

	@Override
	public String getDescription() {
		return swiper.toString().toLowerCase() + " swipe";
	}

	private void calculateCoordinates(View view, float[] startCoordinates, float[] endCoordinates) {
		final int[] elementLocation = new int[2];
		view.getLocationOnScreen(elementLocation);
		float height = view.getHeight();
		float width = view.getWidth();
		Point displaySize = new Point();
		view.getDisplay().getSize(displaySize);

		switch (swipeAction) {
			case SWIPE_LEFT:
				startCoordinates[0] = elementLocation[0] + width - 1;
				startCoordinates[1] = elementLocation[1] + height / 2;
				endCoordinates[0] = 0;
				endCoordinates[1] = elementLocation[1] + height / 2;
				break;
			case SWIPE_RIGHT:
				startCoordinates[0] = elementLocation[0];
				startCoordinates[1] = elementLocation[1] + height / 2;
				endCoordinates[0] = displaySize.x;
				endCoordinates[1] = elementLocation[1] + height / 2;
				break;
			default:
				startCoordinates[0] = 0;
				startCoordinates[1] = 0;
				endCoordinates[0] = 0;
				endCoordinates[1] = 0;
				break;
		}
	}
}
