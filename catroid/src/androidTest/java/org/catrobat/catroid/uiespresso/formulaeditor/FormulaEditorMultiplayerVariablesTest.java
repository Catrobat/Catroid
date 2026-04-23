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

import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_FRAGMENT_POSITION;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.hamcrest.CoreMatchers.not;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class FormulaEditorMultiplayerVariablesTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		Script script =
				UiTestUtils.createProjectAndGetStartScript(FormulaEditorMultiplayerVariablesTest.class.getSimpleName());
		script.addBrick(new ChangeSizeByNBrick(0));

		baseActivityTestRule.launchActivity();

		onView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onFormulaEditor()
				.performOpenDataFragment();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testMultiplayerScopeIsVisibleWithEnabledPreference() {
		testMultiplayerScopeVisibility(true);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testMultiplayerScopeIsNotVisibleWithDisabledPreference() {
		testMultiplayerScopeVisibility(false);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testMultiplayerScopeIsVisibleWithDisabledPreference() {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(getApplicationContext(), false);

		ProjectManager.getInstance().getCurrentProject().addMultiplayerVariable(new UserVariable("oldMultiplayerVariable"));

		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(R.id.multiplayer))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCannotSelectMultiplayerAndListTogether() {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(getApplicationContext(), true);

		onView(withId(R.id.button_add))
				.perform(click());

		closeSoftKeyboard();

		onView(withId(R.id.multiplayer))
				.perform(click());
		onView(withId(R.id.make_list))
				.check(matches(not(isEnabled())));

		onView(withId(R.id.global))
				.perform(click());
		onView(withId(R.id.make_list))
				.perform(click());
		onView(withId(R.id.multiplayer))
				.check(matches(not(isEnabled())));

		onView(withId(R.id.make_list))
				.perform(click());
		onView(withId(R.id.multiplayer))
				.check(matches(isEnabled()));
	}

	private void testMultiplayerScopeVisibility(boolean isMultiplayerEnabled) {
		SettingsFragment.setMultiplayerVariablesPreferenceEnabled(getApplicationContext(), isMultiplayerEnabled);

		onView(withId(R.id.button_add))
				.perform(click());

		Matcher<View> expectedMultiplayerRadioButtonVisibility = isMultiplayerEnabled
				? isDisplayed()
				: not(isDisplayed());

		onView(withId(R.id.multiplayer))
				.check(matches(expectedMultiplayerRadioButtonVisibility));
	}
}
