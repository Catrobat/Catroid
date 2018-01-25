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

package org.catrobat.catroid.uiespresso.formulaeditor.utils;

import android.support.test.espresso.DataInteraction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorDataListMatchers;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.util.matchers.UserDataItemMatchers.withUserVariableName;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

public final class UserVariableDataInteractionWrapper extends
		UserDataItemDataInteractionWrapper<UserVariableDataInteractionWrapper> {
	private UserVariableDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	static UserVariableDataInteractionWrapper onVariableAtPosition(int position) {
		return new UserVariableDataInteractionWrapper(
				onData(is(instanceOf(UserVariable.class)))
						.inAdapterView(FormulaEditorDataListMatchers.isDataListView())
						.atPosition(position));
	}

	static UserVariableDataInteractionWrapper onVariableWithName(String variableName) {
		return new UserVariableDataInteractionWrapper(
				onData(allOf(is(instanceOf(UserVariable.class)), withUserVariableName(variableName)))
						.inAdapterView(FormulaEditorDataListMatchers.isDataListView()));
	}

	public UserVariableDataInteractionWrapper checkHasValue(String value) {
		dataInteraction.onChildView(
				allOf(withId(R.id.fragment_formula_editor_data_list_item_value_text_view), withText(value)))
				.check(matches(isDisplayed()));
		return new UserVariableDataInteractionWrapper(dataInteraction);
	}
}
