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

package org.catrobat.catroid.uiespresso.formulaeditor;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorRenameVariableTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorRenameVariableTest");
		script.addBrick(new ChangeSizeByNBrick(0));
		baseActivityTestRule.launchActivity(null);
	}

	private static String variableNameOld = "variableOld";
	private static String variableNameNew = "variableNew";
	private static Integer whenBrickPosition = 0;
	private static Integer changeSizeBrickPosition = 1;

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void renameVariable() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(changeSizeBrickPosition).checkShowsText(R.string.brick_change_size_by);

		onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(withId(R.id.button_add))
				.perform(click());
		onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
				.perform(replaceText(variableNameOld), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());

		onView(withText(variableNameOld))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(getUserVariableEditText(variableNameOld))));

		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(withText(variableNameOld))
				.perform(longClick());
		onView(withText(R.string.rename))
				.perform(click());
		onView(withId(R.id.dialog_formula_rename_variable_name_edit_text))
				.perform(replaceText(variableNameNew), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());
		pressBack();

		onView(withId(R.id.formula_editor_edit_field)).check(matches(withText(getUserVariableEditText(variableNameNew))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void renameList() {
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(changeSizeBrickPosition).checkShowsText(R.string.brick_change_size_by);

		onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(withId(R.id.button_add))
				.perform(click());
		onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
				.perform(replaceText(variableNameOld), closeSoftKeyboard());
		onView(withId(R.id.dialog_formula_editor_data_is_list_checkbox))
				.perform(click());
		onView(withText(R.string.ok))
				.perform(click());

		onView(withText(variableNameOld))
				.perform(click());
		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(getUserListEditText(variableNameOld))));

		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(withText(variableNameOld))
				.perform(longClick());
		onView(withText(R.string.rename))
				.perform(click());
		onView(withId(R.id.dialog_formula_rename_variable_name_edit_text))
				.perform(replaceText(variableNameNew), closeSoftKeyboard());
		onView(withText(R.string.ok))
				.perform(click());
		pressBack();

		onView(withId(R.id.formula_editor_edit_field)).check(matches(withText(getUserListEditText(variableNameNew))));
	}

	private String getUserVariableEditText(String variableName) {
		return "\"" + variableName + "\" ";
	}

	private String getUserListEditText(String variableName) {
		return "*" + variableName + "* ";
	}
}
