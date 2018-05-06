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
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.ui.SpriteActivity;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class NewDataDialogTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	private int variableBrickPosition;
	private int listBrickPosition;

	@Before
	public void setUp() throws Exception {
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("newDataDialogTest");

		variableBrickPosition = 1;
		listBrickPosition = 2;

		startScript.addBrick(new SetVariableBrick());
		startScript.addBrick(new AddItemToUserListBrick());
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCreateNewVariable() {
		String normalVarName = "myVar";

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariableInitial(normalVarName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCorrectDialogTitle() {
		clickOnEmptySpinner(variableBrickPosition, R.id.set_variable_spinner);

		onView(withText(R.string.formula_editor_variable_dialog_title))
				.check(matches(isDisplayed()));

		clickCancel();

		clickOnEmptySpinner(listBrickPosition, R.id.add_item_to_userlist_spinner);

		onView(withText(R.string.formula_editor_list_dialog_title))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCreateNewList() {
		String normalListName = "myList";

		onBrickAtPosition(listBrickPosition).onVariableSpinner(R.id.add_item_to_userlist_spinner)
				.performNewVariableInitial(normalListName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testInvalidVarNames() {
		String emptyVarName = "";
		String spaceVarName = " ";

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariableInitial(emptyVarName);

		clickOk();

		checkErrorMessageDisplayed(R.string.name_consists_of_spaces_only);

		onView(withId(R.id.input_edit_text))
				.perform(typeText(spaceVarName));

		clickOk();

		checkErrorMessageDisplayed(R.string.name_consists_of_spaces_only);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testInvalidThenValidVarNameErrorMessage() {
		String spaceVarName = " ";
		String validVarName = "name";

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariableInitial(spaceVarName);

		checkErrorMessageDisplayed(R.string.name_consists_of_spaces_only);

		onView(withId(R.id.input_edit_text))
				.perform(typeText(validVarName));

		clickOk();

		checkVariableShownInSpinner(validVarName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testValidSpecialCharVarNames() {
		String underscoreVarName = "var_var";
		String numberVarName = "var123";
		String validSpecialCharVarName = "var$";
		String slashVarName = "var/";
		String bracketVarName = "var(";
		String curlyBracketVarName = "var{";

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariableInitial(underscoreVarName);
		checkVariableShownInSpinner(underscoreVarName);

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(numberVarName);
		checkVariableShownInSpinner(numberVarName);

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(validSpecialCharVarName);
		checkVariableShownInSpinner(validSpecialCharVarName);

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(slashVarName);
		checkVariableShownInSpinner(slashVarName);

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(bracketVarName);
		checkVariableShownInSpinner(bracketVarName);

		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(curlyBracketVarName);
		checkVariableShownInSpinner(curlyBracketVarName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testCancelVarCreation() {
		clickOnEmptySpinner(variableBrickPosition, R.id.set_variable_spinner);

		clickCancel();

		onBrickAtPosition(variableBrickPosition)
				.checkShowsText(R.string.brick_variable_spinner_create_new_variable);
	}

	private void clickOnEmptySpinner(int brickPosition, int spinnerId) {
		onBrickAtPosition(brickPosition)
				.onVariableSpinner(spinnerId)
				.perform(click());
	}

	private void clickCancel() {
		onView(withText(R.string.cancel))
				.perform(click());
	}

	private void clickOk() {
		onView(withText(R.string.ok))
				.perform(click());
	}

	private void checkErrorMessageDisplayed(int errorMessageId) {
		onView(withText(errorMessageId))
				.check(matches(isDisplayed()));
	}

	private void checkVariableShownInSpinner(String varName) {
		onBrickAtPosition(variableBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.checkShowsText(varName);
	}
}
