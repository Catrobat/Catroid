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
import org.catrobat.catroid.content.bricks.TranslateTextFromToBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class FormulaEditorTranslateTextFromToBrickTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject("formulaEditorTranslateTextFromToBrickTest");
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void thirdCreateUserVariableToStoreTranslatedTextTest() {
		onBrickAtPosition(1)
				.onSpinner(R.id.brick_translate_text_spinner)
				.perform(click());
		onView(withText(R.string.new_option))
				.perform(click());

		final String variableName = "Translation";
		onView(withId(R.id.input_edit_text))
				.perform(replaceText(variableName))
				.perform(closeSoftKeyboard());
		onView(withId(R.id.global))
				.perform(click());
		onView(withId(android.R.id.button1))
				.perform(click());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void secondTranslationTextAndLanguageValuesTest() {
		onView(withId(R.id.brick_translate_from_to_edit_text)).perform(click());
		onFormulaEditor().performEnterString("Bienvenidos");
		onView(withId(R.id.brick_translate_from_to_edit_text)).perform(closeSoftKeyboard());

		onView(withId(R.id.brick_translate_from_edit_text)).perform(click());
		onFormulaEditor().performEnterString("es");
		onView(withId(R.id.brick_translate_from_to_edit_text)).perform(closeSoftKeyboard());

		onView(withId(R.id.brick_translate_to_edit_text)).perform(click());
		onFormulaEditor().performEnterString("de");
		onView(withId(R.id.brick_translate_from_to_edit_text)).perform(closeSoftKeyboard());

		pressBack();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void firstTextOnViewTest() {
		onView(withId(R.id.brick_translate_text_from_to_label));
		onView(withText("Translate")).check(matches(isDisplayed()));

		onView(withId(R.id.brick_translate_text_from_label));
		onView(withText("from language")).check(matches(isDisplayed()));

		onView(withId(R.id.brick_translate_text_to_label));
		onView(withText("to")).check(matches(isDisplayed()));

		onView(withId(R.id.brick_translate_text_store_in_variable_label));
		onView(withText("and store result in")).check(matches(isDisplayed()));
	}

	void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript();
		sprite.addScript(startScript);

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		TranslateTextFromToBrick translateTextFromToBrick = new TranslateTextFromToBrick(
				new Formula("Text"), new Formula("en"), new Formula("en"));
		startScript.addBrick(translateTextFromToBrick);

		startScript.addBrick(new WaitBrick(3000));
	}
}
