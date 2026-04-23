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

import android.view.View;
import android.view.ViewConfiguration;

import org.catrobat.catroid.uiespresso.util.matchers.ScriptListMatchers;
import org.hamcrest.Matcher;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.PrecisionDescriber;
import androidx.test.espresso.action.Swiper;
import androidx.test.espresso.matcher.ViewMatchers;

public class DragNDropBrickAction implements ViewAction {
	private Swiper swiper;
	private CoordinatesProvider startCoordinatesProvider;
	private CoordinatesProvider destinationCoordinatesProvider;
	private PrecisionDescriber precisionDescriber;

	public DragNDropBrickAction(Swiper swiper, CoordinatesProvider startCoordinatesProvider,
			CoordinatesProvider destinationCoordinatesProvider, PrecisionDescriber precisionDescriber) {
		this.swiper = swiper;
		this.destinationCoordinatesProvider = destinationCoordinatesProvider;
		this.startCoordinatesProvider = startCoordinatesProvider;
		this.precisionDescriber = precisionDescriber;
	}

	@Override
	public Matcher<View> getConstraints() {
		return ViewMatchers.withParent(ScriptListMatchers.isScriptListView());
	}

	@Override
	public String getDescription() {
		return "Performs a longClick and swipeAction to move a brick in ScriptList";
	}

	@Override
	public void perform(UiController uiController, View view) {
		float[] startCoord = startCoordinatesProvider.calculateCoordinates(view);
		float[] finalCoord = destinationCoordinatesProvider.calculateCoordinates(view);

		view.performLongClick();
		swiper.sendSwipe(uiController, startCoord, finalCoord, precisionDescriber.describePrecision());
		uiController.loopMainThreadForAtLeast(ViewConfiguration.getPressedStateDuration());
	}
}
