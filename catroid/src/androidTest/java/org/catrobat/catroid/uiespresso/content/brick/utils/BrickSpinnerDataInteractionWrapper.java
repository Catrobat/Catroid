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

import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickSpinnerMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.NameableItemMatchers;
import org.catrobat.catroid.uiespresso.util.wrappers.DataInteractionWrapper;

import java.util.ArrayList;
import java.util.List;

import androidx.test.espresso.DataInteraction;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

	public BrickSpinnerDataInteractionWrapper checkStringValuesAvailable(List<String> stringValues) {
		dataInteraction.check(matches(BrickSpinnerMatchers.withStringValues(stringValues)));
		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper checkStringIdValuesAvailable(List<Integer> stringResourceIdValues) {
		List<String> stringValues = new ArrayList<>();
		for (Integer stringResourceId : stringResourceIdValues) {
			stringValues.add(UiTestUtils.getResourcesString(stringResourceId));
		}
		return checkStringValuesAvailable(stringValues);
	}

	public BrickSpinnerDataInteractionWrapper checkNameableValuesAvailable(List<String> stringValues) {
		dataInteraction.check(matches(BrickSpinnerMatchers.withNameableValues(stringValues)));
		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper performSelectNameable(int selectionStringResourceId) {
		return performSelectNameable(UiTestUtils.getResourcesString(selectionStringResourceId));
	}

	public BrickSpinnerDataInteractionWrapper performSelectNameable(String selection) {
		dataInteraction.perform(click());

		onData(allOf(is(instanceOf(Nameable.class)), NameableItemMatchers.withNameable(selection)))
				.perform(click());

		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper performSelectString(int selectionStringResourceId) {
		return performSelectString(UiTestUtils.getResourcesString(selectionStringResourceId));
	}

	public BrickSpinnerDataInteractionWrapper performSelectString(String selection) {
		dataInteraction.perform(click());

		onData(allOf(is(instanceOf(String.class)), is(selection)))
				.perform(click());

		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}
}
