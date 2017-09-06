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
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class ShowTextBrickTest {
	private int setBrickPosition;
	private int showBrickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		setBrickPosition = 1;
		showBrickPosition = 2;

		Script script = BrickTestUtils.createProjectAndGetStartScript("showTextBrickTest1");
		script.addBrick(new SetVariableBrick());
		script.addBrick(new ShowTextBrick());

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testShowVariableBrick() {
		final String variableName = "testVarible";
		final int intToChange = 42;
		final int positionX = 30;
		final int positionY = 40;

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(setBrickPosition).checkShowsText(R.string.brick_set_variable);
		onBrickAtPosition(showBrickPosition).checkShowsText(R.string.brick_show_variable);

		onView(withId(R.id.set_variable_spinner))
				.perform(click());
		onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
				.perform(typeText(variableName));
		onView(withText(R.string.ok))
				.perform(click());

		onBrickAtPosition(setBrickPosition).onFormulaTextField(R.id.brick_set_variable_edit_text)
				.performEnterNumber(intToChange)
				.checkShowsNumber(intToChange);

		onView(withId(R.id.show_variable_spinner))
				.perform(click());
		onView(withText(variableName))
				.perform(click());

		onBrickAtPosition(showBrickPosition).onFormulaTextField(R.id.brick_show_variable_edit_text_x)
				.performEnterNumber(positionX)
				.checkShowsNumber(positionX);

		onBrickAtPosition(showBrickPosition).onFormulaTextField(R.id.brick_show_variable_edit_text_y)
				.performEnterNumber(positionY)
				.checkShowsNumber(positionY);
	}
}
