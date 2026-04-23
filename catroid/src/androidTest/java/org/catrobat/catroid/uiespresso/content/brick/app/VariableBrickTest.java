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
package org.catrobat.catroid.uiespresso.content.brick.app;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_FRAGMENT_POSITION;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.not;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class VariableBrickTest {
	private int setBrickPosition;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	@Before
	public void setUp() throws Exception {
		setBrickPosition = 1;
		Script script = UiTestUtils.createProjectAndGetStartScript("variableBricksTest");
		script.addBrick(new SetVariableBrick());
		script.addBrick(new ChangeVariableBrick());
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	@Flaky
	public void testCreatingNewVariable() {
		final String variableName = "TestVariable";
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(variableName)
				.checkShowsText(variableName);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCreatingNewMultiplayerVariable() {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(getApplicationContext(), true);

		final String variableName = "MultiplayerVariable";
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewMultiplayerVariable(variableName)
				.checkShowsText(variableName)
				.checkShowsVariableNameInAdapter(variableName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	@Flaky
	public void testNewVariableCanceling() {
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		closeSoftKeyboard();

		onView(withText(R.string.cancel))
				.perform(click());

		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.checkShowsText(R.string.new_option);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testAfterDeleteBrickVariableStillVisible() {
		final String variableName = "TestVariable";
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(variableName);

		onBrickAtPosition(setBrickPosition)
				.performDeleteBrick();

		onBrickAtPosition(setBrickPosition).checkShowsText(R.string.brick_change_variable);

		onBrickAtPosition(setBrickPosition).onSpinner(R.id.change_variable_spinner)
				.checkShowsText(variableName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testMultiplayerVariableScopeIsVisibleWithEnabledPreference() {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(getApplicationContext(), true);

		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		onView(withId(R.id.multiplayer))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testMultiplayerVariableScopeIsNotVisibleWithDisabledPreference() {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(getApplicationContext(), false);

		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		onView(withId(R.id.multiplayer))
				.check(matches(not(isDisplayed())));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testMultiplayerVariableScopeIsVisibleWithDisabledPreference() {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(getApplicationContext(), false);

		ProjectManager.getInstance().getCurrentProject().addMultiplayerVariable(new UserVariable("oldMultiplayerVariable"));

		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		onView(withId(R.id.multiplayer))
				.check(matches(isDisplayed()));
	}
}
