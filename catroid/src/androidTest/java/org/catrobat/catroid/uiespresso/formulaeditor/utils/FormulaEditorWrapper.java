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

import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.wrappers.ViewInteractionWrapper;
import org.hamcrest.Matcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import androidx.test.espresso.ViewInteraction;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorCategoryListWrapper.onCategoryList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class FormulaEditorWrapper extends ViewInteractionWrapper {
	public static final Matcher<View> FORMULA_EDITOR_KEYBOARD_MATCHER = withId(R.id.formula_editor_keyboardview);
	public static final Matcher<View> FORMULA_EDITOR_TEXT_FIELD_MATCHER = withId(R.id.formula_editor_edit_field);

	private FormulaEditorWrapper() {
		super(onView(FORMULA_EDITOR_KEYBOARD_MATCHER));
		onView(FORMULA_EDITOR_KEYBOARD_MATCHER)
				.check(matches(isDisplayed()));
		onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER)
				.check(matches(isDisplayed()));
	}

	public static FormulaEditorWrapper onFormulaEditor() {
		return new FormulaEditorWrapper();
	}

	public <V extends Number> FormulaEditorWrapper performEnterNumber(V valueToBeEntered) {
		String value = "";

		if (valueToBeEntered instanceof Float) {
			value = String.format("%f", valueToBeEntered.floatValue());
		} else if (valueToBeEntered instanceof Double) {
			value = String.format("%f", valueToBeEntered.doubleValue());
		} else if (valueToBeEntered instanceof Integer) {
			value = String.format("%d", valueToBeEntered.intValue());
		}

		return performEnterFormula(value);
	}

	public FormulaEditorWrapper performEnterFormula(String formulaString) {
		for (Character item : formulaString.toCharArray()) {
			if (!charToButtonMapping.keySet().contains(item)) {
				throw new IllegalArgumentException("Formula or Number contained illegal character: " + item
						+ " contained in: " + formulaString);
			}
			onView(charToButtonMapping.get(item))
					.perform(click());
		}
		return new FormulaEditorWrapper();
	}

	public FormulaEditorWrapper performEnterString(String stringToBeEntered) {
		onView(Control.TEXT)
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.perform(replaceText(stringToBeEntered));
		onView(withText(R.string.ok))
				.perform(click());
		performCloseFormulaStringWarning();
		return new FormulaEditorWrapper();
	}

	public FormulaEditorWrapper performEnterString(int stringResourceId) {
		onView(Control.TEXT)
				.perform(click());
		onView(withId(R.id.input_edit_text))
				.perform(replaceText(UiTestUtils.getResourcesString(stringResourceId)));
		onView(withText(R.string.ok))
				.perform(click());
		performCloseFormulaStringWarning();
		return new FormulaEditorWrapper();
	}

	public FormulaEditorWrapper checkShows(String expected) {
		onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER)
				.check(matches(withText(equalToIgnoringWhiteSpace(expected))));
		return new FormulaEditorWrapper();
	}

	public FormulaEditorWrapper checkValue(String expected) {
		onView(FORMULA_EDITOR_TEXT_FIELD_MATCHER)
				.check(matches(withText(equalToIgnoringWhiteSpace(expected))));
		return new FormulaEditorWrapper();
	}

	public FormulaEditorCategoryListWrapper performOpenCategory(Matcher<View> category) {
		onView(category)
				.perform(click());
		return onCategoryList();
	}

	public void performCloseFormulaStringWarning() {
		try {
			onView(allOf(withText(R.string.warning),
					withParent(allOf(withId(R.id.title_template),
					withParent(withId(R.id.topPanel)))), isDisplayed()));
			onView(allOf(withText(R.string.warning_formula_recognized),
					withParent(withParent(withId(R.id.scrollView))), isDisplayed()));
			ViewInteraction button = onView(
					allOf(withText(R.string.ok),
					withParent(withParent(withId(R.id.buttonPanel))), isDisplayed()));
			button.perform(click());
		} catch (Exception ignored) { }
	}

	public void performOpenDataFragment() {
		onView(Control.DATA)
				.perform(click());
	}

	public void performClickOn(Matcher<View> matcher) {
		onView(matcher)
				.perform(click());
	}

	public void performBackspace() {
		onView(Control.BACKSPACE)
				.perform(click());
	}

	public void performCompute() {
		onView(Control.COMPUTE)
				.perform(click());
	}

	public void performOpenFunctions() {
		performOpenCategory(Category.FUNCTIONS);
	}

	public void performUndo() {
		onView(ActionMenu.UNDO)
				.perform(click());
	}

	public void performRedo() {
		onView(ActionMenu.REDO)
				.perform(click());
	}

	private static Map<Character, Matcher<View>> charToButtonMapping;
	static {
		Map<Character, Matcher<View>> numpad = new HashMap<>();
		numpad.put('0', NumPad.NUM0);
		numpad.put('1', NumPad.NUM1);
		numpad.put('2', NumPad.NUM2);
		numpad.put('3', NumPad.NUM3);
		numpad.put('4', NumPad.NUM4);
		numpad.put('5', NumPad.NUM5);
		numpad.put('6', NumPad.NUM6);
		numpad.put('7', NumPad.NUM7);
		numpad.put('8', NumPad.NUM8);
		numpad.put('9', NumPad.NUM9);
		numpad.put('.', NumPad.COMMA);
		numpad.put(',', NumPad.COMMA);
		numpad.put('+', Math.PLUS);
		numpad.put('-', Math.MINUS);
		numpad.put('*', Math.MULT);
		numpad.put('/', Math.DIVIDE);
		numpad.put('(', Math.BRACKETOPEN);
		numpad.put(')', Math.BRACKETCLOSE);

		charToButtonMapping = Collections.unmodifiableMap(numpad);
	}

	public static final class NumPad {
		public static final Matcher<View> NUM0 = withId(R.id.formula_editor_keyboard_0);
		public static final Matcher<View> NUM1 = withId(R.id.formula_editor_keyboard_1);
		public static final Matcher<View> NUM2 = withId(R.id.formula_editor_keyboard_2);
		public static final Matcher<View> NUM3 = withId(R.id.formula_editor_keyboard_3);
		public static final Matcher<View> NUM4 = withId(R.id.formula_editor_keyboard_4);
		public static final Matcher<View> NUM5 = withId(R.id.formula_editor_keyboard_5);
		public static final Matcher<View> NUM6 = withId(R.id.formula_editor_keyboard_6);
		public static final Matcher<View> NUM7 = withId(R.id.formula_editor_keyboard_7);
		public static final Matcher<View> NUM8 = withId(R.id.formula_editor_keyboard_8);
		public static final Matcher<View> NUM9 = withId(R.id.formula_editor_keyboard_9);
		public static final Matcher<View> COMMA = withId(R.id.formula_editor_keyboard_decimal_mark);
	}

	public static final class Math {
		public static final Matcher<View> PLUS = withId(R.id.formula_editor_keyboard_plus);
		public static final Matcher<View> MINUS = withId(R.id.formula_editor_keyboard_minus);
		public static final Matcher<View> MULT = withId(R.id.formula_editor_keyboard_mult);
		public static final Matcher<View> DIVIDE = withId(R.id.formula_editor_keyboard_divide);
		public static final Matcher<View> BRACKETOPEN = withId(R.id.formula_editor_keyboard_bracket_open);
		public static final Matcher<View> BRACKETCLOSE = withId(R.id.formula_editor_keyboard_bracket_close);
	}

	public static final class Control {
		public static final Matcher<View> COMPUTE = withId(R.id.formula_editor_keyboard_compute);
		public static final Matcher<View> BACKSPACE = withId(R.id.formula_editor_keyboard_delete);
		public static final Matcher<View> DATA = withId(R.id.formula_editor_keyboard_data);
		public static final Matcher<View> TEXT = withId(R.id.formula_editor_keyboard_string);
		public static final Matcher<View> PROPERTIES = withId(R.id.formula_editor_keyboard_object);
	}

	public static final class Category {
		public static final Matcher<View> OBJECT = withId(R.id.formula_editor_keyboard_object);
		public static final Matcher<View> FUNCTIONS = withId(R.id.formula_editor_keyboard_function);
		public static final Matcher<View> LOGIC = withId(R.id.formula_editor_keyboard_logic);
		public static final Matcher<View> DEVICE = withId(R.id.formula_editor_keyboard_sensors);
	}

	public static final class ActionMenu {
		public static final Matcher<View> UNDO = withId(R.id.menu_undo);
		public static final Matcher<View> REDO = withId(R.id.menu_redo);
	}
}
