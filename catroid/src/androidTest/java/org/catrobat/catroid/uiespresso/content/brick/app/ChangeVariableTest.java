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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickCoordinatesProvider;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ChangeVariableTest {
	private ChangeVariableBrick changeVariableBrick;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testCreateNewUserVariableAndDeletion() {
		String userVariableName = "testVariable1";
		String secondUserVariableName = "testVariable2";

		onBrickAtPosition(0)
				.checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(1)
				.checkShowsText(R.string.brick_change_variable);
		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.performNewVariableInitial(userVariableName);
		// fails due to CAT-2359
		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.performNewVariable(secondUserVariableName);

		onBrickAtPosition(1).onFormulaTextField(R.id.brick_change_variable_edit_text)
				.perform(click());
		deleteItemWithTextFromFormulaEditorDataList(userVariableName, true);

		onView(allOf(withText(userVariableName), hasSibling(withText("0.0"))))
				.check(doesNotExist());
		pressBack();
		onView(withText(userVariableName))
				.check(doesNotExist());
	}

	@Test
	public void testCreateUserVariableInFormulaEditor() {
		String userVariableName1 = "Variable1";
		performNewVariableFromFormulaEditor(1, userVariableName1);
		onBrickAtPosition(1)
				.checkShowsText(userVariableName1);
		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.checkShowsVariableNameInAdapter(userVariableName1);
	}

	@Test
	public void testViewInFormulaEditorAfterClone() {
		String userVariableName = "testvariable1";
		String userVariableName2 = "testvariable2";

		performNewVariableFromFormulaEditor(1, userVariableName);

		onBrickAtPosition(1)
				.performCopyBrick();
		performNewVariableFromFormulaEditor(1, userVariableName2);
		onBrickAtPosition(1).performDragNDrop(BrickCoordinatesProvider.DOWN_ONE_POSITION);
		onBrickAtPosition(1).onVariableSpinner(R.id.change_variable_spinner)
				.checkShowsVariableNamesInAdapter(Arrays.asList(userVariableName, userVariableName2));
	}

	public static void deleteItemWithTextFromFormulaEditorDataList(String text, boolean isInUse) {
		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(allOf(withText(text), withId(R.id
				.fragment_formula_editor_datalist_item_name_text_view)))
				.perform(longClick());
		onView(withText(R.string.delete))
				.perform(click());
		if (isInUse) {
			onView(withText(R.string.deletion_alert_yes))
					.perform(click());
		}
		pressBack();
	}

	private void performNewVariableFromFormulaEditor(int brickId, String variableName) {
		onBrickAtPosition(brickId).onFormulaTextField(R.id.brick_change_variable_edit_text)
				.perform(click());
		onView(withId(R.id.formula_editor_keyboard_data))
				.perform(click());
		onView(withId(R.id.button_add))
				.perform(click());
		onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
				.perform(typeText(variableName));
		onView(withText(R.string.ok))
				.perform(click());
		pressBack();
		pressBack();
	}

	public void createProject() {
		Script script = BrickTestUtils
				.createProjectAndGetStartScript("ChangeVariableTest");
		changeVariableBrick = new ChangeVariableBrick(10);
		script.addBrick(changeVariableBrick);
		script.getBrickList().size();
	}
}
