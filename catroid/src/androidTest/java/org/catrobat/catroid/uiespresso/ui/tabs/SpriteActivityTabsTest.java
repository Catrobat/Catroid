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

package org.catrobat.catroid.uiespresso.ui.tabs;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.fragment.LookListFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.SoundListFragment;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.fragment.app.Fragment;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.R.id.brick_set_variable_edit_text;
import static org.catrobat.catroid.R.id.tab_layout;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_LOOKS;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SOUNDS;
import static org.catrobat.catroid.uiespresso.util.actions.TabActionsKt.selectTabAtPosition;
import static org.junit.Assert.assertNull;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SpriteActivityTabsTest {

	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {
		Script script = BrickTestUtils.createProjectAndGetStartScript("SpriteActivityTabsTest");
		ProjectManager.getInstance().getCurrentProject().addUserVariable(new UserVariable("X"));
		script.addBrick(new SetVariableBrick());
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testScriptTab() {
		onView(withId(tab_layout)).perform(selectTabAtPosition(FRAGMENT_SCRIPTS));
		assertFragmentIsShown(ScriptFragment.TAG);
	}

	@Test
	public void testLooksTab() {
		onView(withId(tab_layout)).perform(selectTabAtPosition(FRAGMENT_LOOKS));
		assertFragmentIsShown(LookListFragment.TAG);
	}

	@Test
	public void testSoundsTab() {
		onView(withId(tab_layout)).perform(selectTabAtPosition(FRAGMENT_SOUNDS));
		assertFragmentIsShown(SoundListFragment.TAG);
	}

	@Test
	public void testDeleteVariable() {
		onView(withId(tab_layout)).perform(selectTabAtPosition(FRAGMENT_SCRIPTS));
		onView(withId(brick_set_variable_edit_text)).perform(click());
		onView(withId(R.id.formula_editor_keyboard_data)).perform(click());
		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.delete)).perform(click());
		assertTabLayoutIsNotShown();
		pressBack();
		assertTabLayoutIsNotShown();
	}

	private void assertFragmentIsShown(String tag) {
		onIdle();
		Fragment fragment = baseActivityTestRule
				.getActivity()
				.getSupportFragmentManager()
				.findFragmentByTag(tag);

		Assert.assertNotNull(fragment);
		Assert.assertTrue(fragment.isVisible());
	}

	private void assertTabLayoutIsNotShown() {
		onIdle();
		assertNull(baseActivityTestRule.getActivity().findViewById(tab_layout));
	}
}
