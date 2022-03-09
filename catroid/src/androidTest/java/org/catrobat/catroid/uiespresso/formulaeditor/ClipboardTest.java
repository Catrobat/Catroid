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

package org.catrobat.catroid.uiespresso.formulaeditor;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.WaitForConditionAction.waitFor;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.not;
import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ClipboardTest {

	private static final String PROJECT_NAME = "ClipboardTest";

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	public static Project createProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		SetVariableBrick setVariableBrick = new SetVariableBrick();
		UserVariable userVariable = new UserVariable("testVariable");
		project.addUserVariable(userVariable);
		setVariableBrick.setUserVariable(userVariable);

		script.addBrick(setVariableBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);

		return project;
	}

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void hideCutCopyWhenNothingSelectedTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onFormulaEditor().performBackspace();
		onView(withId(R.id.formula_editor_edit_field)).perform(longClick());
		onView(withId(R.id.copy)).check(doesNotExist());
		onView(withId(R.id.cut)).check(doesNotExist());
	}

	@Test
	public void hidePasteWhenClipboardIsEmptyTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick());
		onView(withId(R.id.copy)).inRoot(isPlatformPopup()).perform(waitFor(isDisplayed(), 5000));
		onView(withId(R.id.copy)).inRoot(isPlatformPopup()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withId(R.id.cut)).inRoot(isPlatformPopup()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withId(R.id.paste)).inRoot(isPlatformPopup()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
	}

	@Test
	public void cutCopyPasteVisibleOnDoubleClickWhenClipboardIsNotEmptyTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onView(withId(R.id.formula_editor_edit_field)).perform(longClick());
		onView(withId(R.id.copy)).inRoot(isPlatformPopup()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withId(R.id.cut)).inRoot(isPlatformPopup()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		onView(withId(R.id.copy)).inRoot(isPlatformPopup()).perform(click());
		onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick());
		onView(withId(R.id.paste)).inRoot(isPlatformPopup()).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@Test
	public void copyAndPasteTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onFormulaEditor().performEnterNumber(12345);
		onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick());
		onView(withId(R.id.copy)).inRoot(isPlatformPopup()).perform(click());
		onFormulaEditor().performBackspace();
		onView(withId(R.id.formula_editor_edit_field)).perform(longClick());
		onView(withId(R.id.paste)).inRoot(isPlatformPopup()).perform(click());
		onFormulaEditor().checkShows("12345");
	}

	@Test
	public void cutAndPasteTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onFormulaEditor().performEnterNumber(12345);
		onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick());
		onView(withId(R.id.cut)).inRoot(isPlatformPopup()).perform(click());
		onFormulaEditor().checkShows("");
		onView(withId(R.id.formula_editor_edit_field)).perform(click());
		onView(withId(R.id.formula_editor_edit_field)).perform(longClick());
		onView(withId(R.id.paste)).inRoot(isPlatformPopup()).perform(click());
		onFormulaEditor().checkShows("12345");
	}

	@Test
	public void checkUndoVisibleAfterCutOperation() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onView(withId(R.id.menu_undo)).check(matches(not(isEnabled())));
		onView(withId(R.id.cut)).inRoot(isPlatformPopup()).perform(click());
		onFormulaEditor().checkShows("");
		onView(withId(R.id.menu_undo)).check(matches(isEnabled()));
	}

	@Test
	public void cutWithoutFirstParameterTest() {
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		onFormulaEditor()
				.performOpenCategory(FormulaEditorWrapper.Category.FUNCTIONS)
				.performSelect("power(2,3)");
		onFormulaEditor().performBackspace();
		onView(withId(R.id.formula_editor_edit_field)).perform(doubleClick());
		onView(withId(R.id.cut)).inRoot(isPlatformPopup()).perform(click());
		onView(withId(R.id.formula_editor_edit_field)).perform(longClick());
		onView(withId(R.id.paste)).inRoot(isPlatformPopup()).perform(click());
		onFormulaEditor().checkValue("power( , 3 )");
	}

	@After
	public void tearDown() throws IOException {
		baseActivityTestRule.finishActivity();
		TestUtils.deleteProjects(PROJECT_NAME);
	}
}
