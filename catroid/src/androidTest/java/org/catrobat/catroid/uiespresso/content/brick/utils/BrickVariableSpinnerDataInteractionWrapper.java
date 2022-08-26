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

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

import androidx.test.espresso.DataInteraction;

import static org.catrobat.catroid.uiespresso.util.matchers.UserDataItemMatchers.withUserVariableName;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class BrickVariableSpinnerDataInteractionWrapper extends BrickSpinnerDataInteractionWrapper {

	public BrickVariableSpinnerDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public BrickVariableSpinnerDataInteractionWrapper checkShowsText(String text) {
		dataInteraction.onChildView(withText(text))
				.check(matches(isDisplayed()));
		return new BrickVariableSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickVariableSpinnerDataInteractionWrapper performNewVariable(String variableName) {
		dataInteraction.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		enterTextOnDialogue(R.id.input_edit_text, variableName);
		checkShowsText(variableName);

		return new BrickVariableSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickVariableSpinnerDataInteractionWrapper performNewMultiplayerVariable(String variableName) {
		dataInteraction.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		onView(withId(R.id.multiplayer))
				.perform(click());

		enterTextOnDialogue(R.id.input_edit_text, variableName);
		checkShowsText(variableName);

		return new BrickVariableSpinnerDataInteractionWrapper(dataInteraction);
	}

	private static void enterTextOnDialogue(int editTextId, String textToEnter) {
		onView(withId(editTextId))
				.perform(replaceText(textToEnter), closeSoftKeyboard());
		onView(withId(android.R.id.button1))
				.perform(click());
	}

	public BrickSpinnerDataInteractionWrapper checkShowsVariableNamesInAdapter(List<String> variableNames) {
		dataInteraction.perform(click());
		for (String variableName : variableNames) {
			onData(allOf(is(instanceOf(UserVariable.class)), withUserVariableName(variableName)))
					.check(matches(isDisplayed()));
		}
		pressBack();
		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickSpinnerDataInteractionWrapper checkShowsVariableNameInAdapter(String variableName) {
		dataInteraction.perform(click());
		onData(allOf(is(instanceOf(UserVariable.class)), withUserVariableName(variableName)))
				.check(matches(isDisplayed()));
		pressBack();
		return new BrickSpinnerDataInteractionWrapper(dataInteraction);
	}
}
