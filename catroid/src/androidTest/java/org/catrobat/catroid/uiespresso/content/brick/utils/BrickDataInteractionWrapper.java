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
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.uiespresso.util.matchers.ScriptListMatchers;
import org.catrobat.catroid.uiespresso.util.wrappers.DataInteractionWrapper;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AnyOf.anyOf;

public class BrickDataInteractionWrapper extends DataInteractionWrapper {

	public BrickDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public static BrickDataInteractionWrapper onBrickAtPosition(int position) {
		return new BrickDataInteractionWrapper(onData(instanceOf(Brick.class))
				.inAdapterView(ScriptListMatchers.isScriptListView())
				.atPosition(position));
	}

	public BrickDataInteractionWrapper checkShowsText(String text) {
		dataInteraction.onChildView(withText(text))
				.check(matches(isDisplayed()));
		return new BrickDataInteractionWrapper(dataInteraction);
	}

	public BrickDataInteractionWrapper checkShowsText(int stringResourceId) {
		dataInteraction.onChildView(withText(stringResourceId))
				.check(matches(isDisplayed()));
		return new BrickDataInteractionWrapper(dataInteraction);
	}

	public BrickFormulaEditTextDataInteractionWrapper onFormulaTextField(int editTextResourceId) {
		dataInteraction.onChildView(withId(editTextResourceId)).check(matches(instanceOf(TextView.class)));
		return new BrickFormulaEditTextDataInteractionWrapper(
				dataInteraction.onChildView(withId(editTextResourceId)));
	}

	public BrickSpinnerDataInteractionWrapper onSpinner(int spinnerResourceId) {
		dataInteraction.onChildView(withId(spinnerResourceId)).check(matches(instanceOf(Spinner.class)));
		return new BrickSpinnerDataInteractionWrapper(
				dataInteraction.onChildView(withId(spinnerResourceId)));
	}

	public BrickVariableSpinnerDataInteractionWrapper onVariableSpinner(int spinnerResourceId) {
		dataInteraction.onChildView(withId(spinnerResourceId)).check(matches(instanceOf(Spinner.class)));
		return new BrickVariableSpinnerDataInteractionWrapper(
				dataInteraction.onChildView(withId(spinnerResourceId)));
	}

	public void performDragNDrop(CoordinatesProvider destinationCoordinatesProvider) {
		dataInteraction.perform(new DragNDropBrickAction(Swipe.FAST,
				BrickCoordinatesProvider.UPPER_LEFT_CORNER,
				destinationCoordinatesProvider,
				Press.FINGER));
	}

	public void performDeleteBrick() {
		dataInteraction.perform(new GeneralClickAction(Tap.SINGLE,
				BrickCoordinatesProvider.UPPER_LEFT_CORNER,
				Press.FINGER));
		onView(anyOf(withText(R.string.brick_context_dialog_delete_brick),
				withText(R.string.brick_context_dialog_delete_script)))
				.perform(click());
		onView(withText(R.string.yes))
				.perform(click());
	}
}
