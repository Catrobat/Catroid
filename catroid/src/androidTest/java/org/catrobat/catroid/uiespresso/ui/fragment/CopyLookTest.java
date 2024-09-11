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

package org.catrobat.catroid.uiespresso.ui.fragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.ui.fragment.actionutils.ActionUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CopyLookTest {

	final ProjectManager projectManager = inject(ProjectManager.class).getValue();

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_LOOKS);

	private String testLookName1 = "testLook";
	private String testLookName2 = "testLook2";
	private String toBeCopiedLookName1 = "testLook (1)";

	@Before
	public void setUp() {
		createProject();
	}

	@After
	public void tearDown() {
		baseActivityTestRule.getActivity().finish();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void copyLookOneElementListTest() throws IOException {
		ActionUtils.addLook(projectManager, testLookName1);
		baseActivityTestRule.launchActivity();

		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
		onView(withText(R.string.copy)).perform(click());

		onView(withText(toBeCopiedLookName1))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void copyLookMultipleElementsListTest() throws IOException {
		ActionUtils.addLook(projectManager, testLookName1);
		ActionUtils.addLook(projectManager, testLookName2);
		baseActivityTestRule.launchActivity();

		openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
		onView(withText(R.string.copy)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItemClick();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(toBeCopiedLookName1))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void selectFragmentToCopyTest() {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
		onView(withText(R.string.copy)).perform(click());

		onRecyclerView().atPosition(0).perform(click());
		onRecyclerView().atPosition(0).performCheckItemCheck();
	}

	private void createProject() {
		String projectName = "copyLookFragmentTest";
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);
	}
}
