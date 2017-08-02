/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.wrappers.DataInteractionWrapper;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public class BrickSpinnerDataInteractionWrapper extends DataInteractionWrapper {
	public BrickSpinnerDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper checkShowsText(String text) {
		dataInteraction.onChildView(withText(text))
				.check(matches(isDisplayed()));
		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper checkShowsText(int stringResourceId) {
		dataInteraction.onChildView(withText(stringResourceId))
				.check(matches(isDisplayed()));
		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper checkValuesAvailable(List<Integer> stringResourceIdValues) {
		dataInteraction.perform(click());

		for (Integer stringResourceId : stringResourceIdValues) {
			onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(stringResourceId))))
					.check(matches(isDisplayed()));
		}
		pressBack();
		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper performSelect(int selectionStringResourceId) {
		dataInteraction.perform(click());

		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(selectionStringResourceId))))
				.perform(click());

		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper performSelect(String selection) {
		dataInteraction.perform(click());

		onData(allOf(is(instanceOf(String.class)), is(selection)))
				.perform(click());

		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}
}
