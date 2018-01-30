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

import org.catrobat.catroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorDataListMatchers.isDataListView;

public final class FormulaEditorDataListWrapper {

	public enum ItemScope {
		GLOBAL,
		LOCAL
	}

	public enum ItemType {
		VARIABLE,
		LIST
	}

	private FormulaEditorDataListWrapper() {
	}

	public static FormulaEditorDataListWrapper onDataList() {
		onView(isDataListView())
				.check(matches(isDisplayed()));
		return new FormulaEditorDataListWrapper();
	}

	public FormulaEditorDataListWrapper performAdd(String itemName) {
		return performAdd(itemName, ItemType.VARIABLE, ItemScope.GLOBAL);
	}

	public FormulaEditorDataListWrapper performAdd(String itemName, ItemType type) {
		return performAdd(itemName, type, ItemScope.GLOBAL);
	}

	public FormulaEditorDataListWrapper performAdd(String itemName, ItemType type, ItemScope scope) {
		onView(withId(R.id.button_add))
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.perform(typeText(itemName), closeSoftKeyboard());

		switch (scope) {
			case GLOBAL:
				onView(withId(R.id.global))
						.check(matches(isChecked()));
				break;
			case LOCAL:
				onView(withId(R.id.local))
						.perform(click());
				break;
			default:
				break;
		}

		switch (type) {
			case VARIABLE:
				onView(withId(R.id.make_list))
						.check(matches(isNotChecked()));
				break;
			case LIST:
				onView(withId(R.id.make_list))
						.perform(click());
				break;
			default:
				break;
		}

		onView(withId(android.R.id.button1))
				.perform(click());

		return new FormulaEditorDataListWrapper();
	}

	public UserListDataItemRVInteractionWrapper onListAtPosition(int position) {
		return UserListDataItemRVInteractionWrapper.onListAtPosition(position);
	}

	public UserVariableDataItemRVInteractionWrapper onVariableAtPosition(int position) {
		return UserVariableDataItemRVInteractionWrapper.onVariableAtPosition(position);
	}

	public void performClose() {
		pressBack();
		onFormulaEditor();
	}
}
