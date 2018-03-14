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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListCheckboxWrapper.checkDataFragmentCheckbox;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorDataListWrapper.onDataList;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorUtils.longPressDeleteVariable;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResources;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorDataFragmentVariableTest {
	private Project project;
	private Sprite sprite;
	private Sprite sprite2;
	private String sprite2Name = "testSprite2";
	private String projectName = "FormulaEditorDataFragmentVariableTest";
	private String defaultVarName = "Global1";
	private String localVarName = "Local1";
	private String globalVarName = "Global2";
	private DataContainer dataContainer;
	private int defaultVarPosition = 0;
	private int globalVarPosition = 1;
	private int localVarPosition = 2;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = UiTestUtils.createEmptyProject(projectName);
		sprite = project.getDefaultScene().getSpriteBySpriteName("testSprite");
		dataContainer = project.getDefaultScene().getDataContainer();
		sprite2 = new Sprite(sprite2Name);
		project.getDefaultScene().addSprite(sprite2);

		SetVariableBrick setVariableBrick = new SetVariableBrick();
		UserVariable userVariable = dataContainer.addProjectUserVariable(defaultVarName);
		setVariableBrick.setUserVariable(userVariable);
		sprite.getScript(0).addBrick(setVariableBrick);

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testAddUserVariableAfterStage() {
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();
		pressBack();
		onView(withText(R.string.ok))
				.perform(click());

		onView(withId(R.id.button_play))
				.perform(click());
		pressBack();
		onView(withId(R.id.stage_dialog_button_back))
				.perform(click());

		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();
		onDataList()
				.performAdd(globalVarName);

		onDataList()
				.onVariableAtPosition(globalVarPosition)
				.checkHasName(globalVarName);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCreateUserVariable() {
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.performAdd(globalVarName);

		onDataList()
				.onVariableAtPosition(globalVarPosition)
				.perform(click());

		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText("\"" + globalVarName + "\" ")));
		onView(withId(R.id.formula_editor_keyboard_delete))
				.perform(click());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testDeleteUserVariableWithLongPress(){
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);

		onDataList()
				.performAdd(globalVarName)
				.onVariableAtPosition(globalVarPosition)
				.checkHasName(globalVarName);

		longPressDeleteVariable(globalVarName);
		onView(withText(containsString(globalVarName)))
				.check(doesNotExist());

		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testDeleteUserVariableWithMultipleChoice() {
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);

		onDataList()
				.performAdd(globalVarName, FormulaEditorDataListWrapper.ItemType.VARIABLE,
						FormulaEditorDataListWrapper.ItemScope.GLOBAL)
				.onVariableAtPosition(globalVarPosition)
				.checkHasName(globalVarName);

		onDataList()
				.performAdd(localVarName, FormulaEditorDataListWrapper.ItemType.VARIABLE,
						FormulaEditorDataListWrapper.ItemScope.LOCAL)
				.onVariableAtPosition(localVarPosition)
				.checkHasName(localVarName);

		openContextualActionModeOverflowMenu();
		onView(withText(R.string.delete))
				.perform(click());

		checkDataFragmentCheckbox(globalVarPosition);
		checkDataFragmentCheckbox(localVarPosition);

		onView(withId(R.id.confirm))
				.perform(click());
		onView(withText(R.string.deletion_alert_yes))
				.perform(click());


		onView(allOf(withText(globalVarName),isDescendantOfA(withId(R.id.recycler_view))))
				.check(doesNotExist());
		onView(allOf(withText(localVarName),isDescendantOfA(withId(R.id.recycler_view))))
				.check(doesNotExist());
		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void tesCancelVariableDeletion(){
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);

		onDataList()
				.performAdd(globalVarName, FormulaEditorDataListWrapper.ItemType.VARIABLE,
						FormulaEditorDataListWrapper.ItemScope.GLOBAL)
				.onVariableAtPosition(globalVarPosition)
				.checkHasName(globalVarName);

		onDataList()
				.performAdd(localVarName, FormulaEditorDataListWrapper.ItemType.VARIABLE,
						FormulaEditorDataListWrapper.ItemScope.LOCAL)
				.onVariableAtPosition(localVarPosition)
				.checkHasName(localVarName);

		openContextualActionModeOverflowMenu();
		onView(withText(R.string.delete))
				.perform(click());

		checkDataFragmentCheckbox(globalVarPosition);
		checkDataFragmentCheckbox(localVarPosition);

		pressBack();
		pressBack();

		onFormulaEditor()
				.performOpenDataFragment();

		onView(allOf(withText(defaultVarName),isDescendantOfA(withId(R.id.recycler_view))))
				.check(matches(isDisplayed()));
		onView(allOf(withText(globalVarName),isDescendantOfA(withId(R.id.recycler_view))))
				.check(matches(isDisplayed()));
		onView(allOf(withText(localVarName),isDescendantOfA(withId(R.id.recycler_view))))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCreateUserVariableErrors(){
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);

		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(android.R.id.button1))
				.perform(click());

		onView(withId(R.id.textinput_error))
				.check(matches(allOf(withText(R.string.name_consists_of_spaces_only), isDisplayed())));

		onView(withId(R.id.input_edit_text))
				.perform(typeText(defaultVarName), closeSoftKeyboard());

		onView(withId(R.id.global))
				.perform(click());
		onView(withId(android.R.id.button1))
				.perform(click());

		onView(withId(R.id.textinput_error))
				.check(matches(allOf(withText(R.string.name_already_exists), isDisplayed())));

		onView(withId(android.R.id.button2))
				.perform(click());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testVisibilityOfMenuItems(){
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		openContextualActionModeOverflowMenu();

		onView(withId(R.string.copy))
				.check(doesNotExist());
		onView(withText(R.string.rename))
				.check(doesNotExist());
		onView(withText(R.string.show_details))
				.check(doesNotExist());
		onView(withText(R.string.settings))
				.check(doesNotExist());

		onView(withText(R.string.delete))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testVariableListHeadlines(){
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);

		onDataList()
				.performAdd(localVarName, FormulaEditorDataListWrapper.ItemType.VARIABLE,
						FormulaEditorDataListWrapper.ItemScope.LOCAL);

		onView(withText(getResources().getString(R.string.global_vars_headline)))
				.check(matches(isDisplayed()));
		onView(withText(getResources().getString(R.string.local_vars_headline)))
				.check(matches(isDisplayed()));

		longPressDeleteVariable(defaultVarName);

		onView(withText(getResources().getString(R.string.global_vars_headline)))
				.check(doesNotExist());
		onView(withText(getResources().getString(R.string.local_vars_headline)))
				.check(matches(isDisplayed()));

		longPressDeleteVariable(localVarName);

		onView(withText(getResources().getString(R.string.global_vars_headline)))
				.check(doesNotExist());
		onView(withText(getResources().getString(R.string.local_vars_headline)))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testScopeOfUserVariable(){
		onView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor()
				.performOpenDataFragment();

		onDataList()
				.onVariableAtPosition(defaultVarPosition)
				.checkHasName(defaultVarName);

		onDataList()
				.performAdd(localVarName, FormulaEditorDataListWrapper.ItemType.VARIABLE,
						FormulaEditorDataListWrapper.ItemScope.LOCAL)
				.performAdd(globalVarName, FormulaEditorDataListWrapper.ItemType.VARIABLE,
						FormulaEditorDataListWrapper.ItemScope.GLOBAL);

		assertTrue(dataContainer.existProjectVariableWithName(globalVarName));
		assertFalse(dataContainer.spriteVariableExistsByName(sprite2, localVarName));
	}
}