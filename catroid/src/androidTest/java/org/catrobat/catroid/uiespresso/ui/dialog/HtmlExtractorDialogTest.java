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

package org.catrobat.catroid.uiespresso.ui.dialog;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.not;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class HtmlExtractorDialogTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {
		Script script = UiTestUtils.createProjectAndGetStartScript("HtmlExtractorDialogTest");
		script.addBrick(new ChangeSizeByNBrick(0));
		baseActivityTestRule.launchActivity();

		onBrickAtPosition(1).onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());
		openHtmlExtractor();
	}

	@Test
	public void testHtmlExtractorDialogTitle() {
		onView(withText(R.string.formula_editor_regex_html_extractor_dialog_title)).check(matches(isDisplayed()));
	}

	@Test
	public void testHtmlExtractorDialogKeywordHint() {
		onView(withHint(R.string.keyword_label)).check(matches(isDisplayed()));
	}

	@Test
	public void testHtmlExtractorDialogHtmlHint() {
		onView(withHint(R.string.html_label)).check(matches(isDisplayed()));
	}

	@Test
	public void testHtmlExtractorDialogOkButton() {
		onView(withText(R.string.ok)).check(matches(isDisplayed()));
	}

	@Test
	public void testHtmlExtractorDialogCancelButton() {
		onView(withText(R.string.cancel)).check(matches(isDisplayed()));
	}

	@Test
	public void testHtmlExtractorDialogNotFoundMessage() {
		onView(withHint(R.string.keyword_label)).perform(typeText("Not"));
		onView(withHint(R.string.html_label)).perform(typeText("Found"));
		onView(withText(R.string.ok)).perform(click());

		onView(withText(R.string.formula_editor_function_regex_html_extractor_not_found)).inRoot(withDecorView(not(baseActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
	}

	private void openHtmlExtractor() {
		String regularExpressionAssistant =
				"\t\t\t\t\t" + UiTestUtils.getResourcesString(R.string.formula_editor_function_regex_assistant);
		onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.FUNCTIONS).performSelect(regularExpressionAssistant);
		onView(withText(R.string.formula_editor_regex_html_extractor_dialog_title)).perform(click());
	}

	@Test
	public void testInsertKeywordIntoEditor() {
		String expectedText = "regular expression( '\\Q \\E(.+?)\\Q \\E' , 'I am a panda.' ) ";

		onView(withHint(R.string.keyword_label)).perform(typeText("Word"));
		onView(withHint(R.string.html_label)).perform(typeText("One Word Inserted"));
		onView(withText(R.string.ok)).perform(click());

		onFormulaEditor().checkShows(expectedText);
	}
}
