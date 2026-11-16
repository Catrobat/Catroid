/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(AndroidJUnit4.class)
public class FormulaEditorFragmentHelpUrlTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Project project = UiTestUtils.createDefaultTestProject("FormulaEditorFragmentHelpUrlTest");
		Sprite sprite = project.getDefaultScene().getSprite(TestUtils.DEFAULT_TEST_SPRITE_NAME);
		Script script = sprite.getScript(TestUtils.DEFAULT_TEST_SCRIPT_INDEX);

		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(1), new UserVariable("var"));
		UserVariable userVariable = new UserVariable("Global1");
		project.addUserVariable(userVariable);
		setVariableBrick.setUserVariable(userVariable);

		script.addBrick(setVariableBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		baseActivityTestRule.launchActivity();
	}

	@Test
	public void brickHelpOpensItsPublishedArticle() {
		Intents.init();
		try {
			intending(hasAction(Intent.ACTION_VIEW)).respondWith(new ActivityResult(Activity.RESULT_OK, null));
			onBrickAtPosition(1).perform(click());
			onView(withText(R.string.brick_context_dialog_help)).perform(click());
			intended(allOf(hasAction(Intent.ACTION_VIEW),
					hasData("https://catrobat.org/docs/brickdocumentation/data/set-variable-to/")));
		} finally {
			Intents.release();
		}
	}

	@Test
	public void functionsHideUnavailableHelp() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.FUNCTIONS);
		onView(withContentDescription(R.string.main_menu_help)).check(doesNotExist());
	}

	@Test
	public void logicHidesUnavailableHelp() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.LOGIC);
		onView(withContentDescription(R.string.main_menu_help)).check(doesNotExist());
	}

	@Test
	public void sensorsHideUnavailableHelp() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.DEVICE);
		onView(withContentDescription(R.string.main_menu_help)).check(doesNotExist());
	}

	@Test
	public void objectHidesUnavailableHelp() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_set_variable_edit_text))
				.perform(click());
		onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.OBJECT);
		onView(withContentDescription(R.string.main_menu_help)).check(doesNotExist());
	}
}
