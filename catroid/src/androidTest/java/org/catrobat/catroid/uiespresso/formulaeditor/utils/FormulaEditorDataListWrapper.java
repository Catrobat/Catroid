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
package org.catrobat.catroid.uiespresso.formulaeditor.utils;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.wrappers.ViewInteractionWrapper;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorDataListMatchers.isDataListView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public final class FormulaEditorDataListWrapper extends ViewInteractionWrapper {

	public enum ItemScope {
		GLOBAL,
		LOCAL,
		MULTIPLAYER
	}

	public enum ItemType {
		VARIABLE,
		LIST
	}

	private FormulaEditorDataListWrapper() {
		super(onView(isDataListView()));
		onView(isDataListView())
				.check(matches(isDisplayed()));
	}

	public static FormulaEditorDataListWrapper onDataList() {
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
				.perform(replaceText(itemName), closeSoftKeyboard());

		switch (scope) {
			case GLOBAL:
				onView(withId(R.id.global))
						.check(matches(isChecked()));
				break;
			case LOCAL:
				onView(withId(R.id.local))
						.perform(click());
				break;
			case MULTIPLAYER:
				onView(withId(R.id.multiplayer))
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

	public void performCancel(String itemName) {
		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(R.id.input_edit_text))
				.perform(replaceText(itemName), closeSoftKeyboard());

		onView(withId(android.R.id.button2))
				.perform(click());
	}
}
