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

import android.graphics.Point;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.PrecisionDescriber;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Swiper;
import android.support.test.espresso.util.HumanReadables;
import android.view.View;
import android.view.ViewConfiguration;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

public final class CustomSwipeAction implements ViewAction {

	private static final int MAX_TRIES = 3;
	private static final int MIN_VISIBLE_AREA_PERCENTAGE = 90;

	private final SwipeAction swipeAction;
	private final Swiper swiper;
	private final PrecisionDescriber precisionDescriber;
	private float pos;

	public enum SwipeAction {
		SWIPE_RIGHT,
		SWIPE_LEFT,
		SWIPE_POS
	}

	public static ViewAction swipeToPosition(float relativePosition) {
		return new CustomSwipeAction(Swipe.SLOW, CustomSwipeAction.SwipeAction.SWIPE_POS, Press.FINGER, relativePosition);
	}

	public static ViewAction swipeRightSlow() {
		return new CustomSwipeAction(Swipe.SLOW, CustomSwipeAction.SwipeAction.SWIPE_RIGHT, Press.FINGER);
	}

	public static ViewAction swipeLeftSlow() {
		return new CustomSwipeAction(Swipe.SLOW, CustomSwipeAction.SwipeAction.SWIPE_LEFT, Press.FINGER);
	}

	private CustomSwipeAction(Swiper swiper, SwipeAction swipeAction, PrecisionDescriber precisionDescriber) {
		this.swiper = swiper;
		this.swipeAction = swipeAction;
		this.precisionDescriber = precisionDescriber;
	}

	private CustomSwipeAction(Swiper swiper, SwipeAction swipeAction, PrecisionDescriber precisionDescriber,
			float pos) {
		this.swiper = swiper;
		this.swipeAction = swipeAction;
		this.precisionDescriber = precisionDescriber;
		this.pos = pos;
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
		float paddingLeft = view.getPaddingLeft();
		float paddingRight = view.getPaddingRight();
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
			case SWIPE_POS:
				startCoordinates[0] = elementLocation[0];
				startCoordinates[1] = elementLocation[1] + height / 2;
				float relativePos = pos * (width - paddingLeft - paddingRight);
				endCoordinates[0] = relativePos + elementLocation[0] + paddingLeft;
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
