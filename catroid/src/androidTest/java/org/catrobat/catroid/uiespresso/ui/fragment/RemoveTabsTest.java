/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.fragment;

import com.google.android.material.tabs.TabLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.R.id.tab_layout;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_FRAGMENT_POSITION;
import static org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RemoveTabsTest {
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, EXTRA_FRAGMENT_POSITION, FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {
		Script script = UiTestUtils.createProjectAndGetStartScript(
				"RemoveTabsFromSpriteActivityTest");
		script.addBrick(new SetVariableBrick());
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void testRemoveTabsInCategoryFragmentTest() {
		assertTabLayoutIsShown(FRAGMENT_SCRIPTS);
		onView(withId(R.id.button_add)).perform(click());
		assertTabLayoutIsNotShown();
		pressBack();
		assertTabLayoutIsShown(FRAGMENT_SCRIPTS);
	}

	@Test
	public void testRemoveTabsInFormulaEditorFragmentTest() {
		assertTabLayoutIsShown(FRAGMENT_SCRIPTS);
		onView(withId(R.id.brick_set_variable_edit_text)).perform(click());
		assertTabLayoutIsNotShown();
		pressBack();
		assertTabLayoutIsShown(FRAGMENT_SCRIPTS);
	}

	private void assertTabLayoutIsShown(int tabSelected) {
		onIdle();
		TabLayout tabLayout = baseActivityTestRule.getActivity().findViewById(tab_layout);
		assertNotNull(tabLayout);
		assertEquals(tabSelected, tabLayout.getSelectedTabPosition());
	}

	private void assertTabLayoutIsNotShown() {
		Espresso.onIdle();
		assertNull(baseActivityTestRule.getActivity().findViewById(R.id.tab_layout));
	}
}
