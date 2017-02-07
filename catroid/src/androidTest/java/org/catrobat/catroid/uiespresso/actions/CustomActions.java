/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.AllOf.allOf;

public final class CustomActions {
	// Suppress default constructor for noninstantiability
	private CustomActions() {
		throw new AssertionError();
	}

	public static ViewAction wait(final int milliSeconds) {
		return new ViewAction() {
			@Override
			public String getDescription() {
				return "Wait for X milliseconds";
			}

			@Override
			public Matcher<View> getConstraints() {
				return isDisplayed();
			}

			@Override
			public void perform(UiController uiController, View view) {
				uiController.loopMainThreadUntilIdle();
				uiController.loopMainThreadForAtLeast(milliSeconds);
			}
		};
	}

	public static ViewAction typeInValue(final String value) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return allOf(isDisplayed(), withId(R.id.formula_editor_edit_field));
			}

			@Override
			public String getDescription() {
				return "type value in formula editor keyboard";
			}

			@Override
			public void perform(UiController uiController, View view) {
				FormulaEditorEditText formulaEditorEditText = (FormulaEditorEditText) view;

				for (char item : value.toCharArray()) {
					switch (item) {
						case '-':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_minus, "");
							break;
						case '0':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_0, "");
							break;
						case '1':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_1, "");
							break;
						case '2':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_2, "");
							break;
						case '3':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_3, "");
							break;
						case '4':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_4, "");
							break;
						case '5':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_5, "");
							break;
						case '6':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_6, "");
							break;
						case '7':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_7, "");
							break;
						case '8':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_8, "");
							break;
						case '9':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_9, "");
							break;
						case '.':
						case ',':
							formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_decimal_mark, "");
					}
				}
			}
		};
	}
}
