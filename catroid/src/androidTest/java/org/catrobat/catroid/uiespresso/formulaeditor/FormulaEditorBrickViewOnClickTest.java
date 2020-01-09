/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorBrickViewOnClickTest {

	private ShowTextBrick showTextBrick;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testIfSpinnersAreDisabled() {
		onBrickAtPosition(1)
				.onFormulaTextField(R.id.brick_show_variable_edit_text_x)
				.perform(click());

		onView(withId(R.id.show_variable_spinner))
				.check(matches(allOf(not(isClickable()), not(isEnabled()), not(isFocusable()))));
	}

	@Test
	public void checkSwitchBetweenBrickFields() {
		onBrickAtPosition(1)
				.onFormulaTextField(R.id.brick_show_variable_edit_text_x)
				.perform(click());

		onFormulaEditor()
				.checkShows(showTextBrick.getFormulaWithBrickField(Brick.BrickField.X_POSITION)
						.getTrimmedFormulaString(ApplicationProvider.getApplicationContext()));

		onView(withId(R.id.brick_show_variable_edit_text_y))
				.perform(click());

		onFormulaEditor()
				.checkShows(showTextBrick.getFormulaWithBrickField(Brick.BrickField.Y_POSITION)
						.getTrimmedFormulaString(ApplicationProvider.getApplicationContext()));
	}

	private void createProject() {
		Script script = BrickTestUtils.createProjectAndGetStartScript(getClass().getSimpleName());
		showTextBrick = new ShowTextBrick(new Formula(100), new Formula(200));
		script.addBrick(showTextBrick);
	}
}
