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

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.util.matchers.UserVariableMatchers.withUserVariableName;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public class BrickVariableSpinnerDataInteractionWrapper extends BrickSpinnerDataInteractionWrapper {
	public BrickVariableSpinnerDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public BrickVariableSpinnerDataInteractionWrapper checkShowsText(String text) {
		dataInteraction.onChildView(withText(text))
				.check(matches(isDisplayed()));
		return new BrickVariableSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickVariableSpinnerDataInteractionWrapper performNewVariableInitial(String variableName) {
		checkShowsText(R.string.brick_variable_spinner_create_new_variable);

		dataInteraction.onChildView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		enterTextOnDialogue(R.id.dialog_formula_editor_data_name_edit_text, variableName);

		return new BrickVariableSpinnerDataInteractionWrapper(dataInteraction);
	}

	public BrickVariableSpinnerDataInteractionWrapper performNewVariable(String variableName) {
		dataInteraction.perform(click());

		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		enterTextOnDialogue(R.id.dialog_formula_editor_data_name_edit_text, variableName);
		// todo: CAT-2359 to fix this:
		checkShowsText(variableName);

		return new BrickVariableSpinnerDataInteractionWrapper(dataInteraction);
	}

	private static void enterTextOnDialogue(int dialogueId, String textToEnter) {
		onView(withId(dialogueId))
				.check(matches(isDisplayed()));
		onView(withId(dialogueId))
				.perform(typeText(textToEnter));
		onView(withId(android.R.id.button1))
				.perform(click());
	}

	public BrickVariableSpinnerDataInteractionWrapper performSelectList(String selection) {
		dataInteraction.perform(click());
		onView(withText(selection))
				.perform(click());

		return new BrickVariableSpinnerDataInteractionWrapper(dataInteraction);
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
