/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(AndroidJUnit4.class)
public class CopyGroupSpriteTest {

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
			ProjectActivity.FRAGMENT_SPRITES);

	private final List<String> spriteList = Arrays.asList("groupSprite", "sprite", "standaloneSprite");
	private UniqueNameProvider uniqueNameProvider;

	@Before
	public void setUp() throws Exception {
		createProject(CopyGroupSpriteTest.class.getSimpleName());
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(CopyGroupSpriteTest.class.getSimpleName());
	}

	@Test
	public void copySpriteTest() {
		openCopyAction();
		onRecyclerView().atPosition(3).performCheckItem();
		onView(withId(R.id.confirm)).perform(click());
		onView(withText(uniqueNameProvider.getUniqueName(spriteList.get(1), spriteList))).check(matches(isDisplayed()));
	}

	@Test
	public void copyTwoDifferentSpritesTest() {
		openCopyAction();
		onRecyclerView().atPosition(1).performCheckItem();
		onRecyclerView().atPosition(3).performCheckItem();
		onView(withId(R.id.confirm)).perform(click());
		onView(withText(uniqueNameProvider.getUniqueName(spriteList.get(1), spriteList))).check(matches(isDisplayed()));
		onView(withText(uniqueNameProvider.getUniqueName(spriteList.get(2), spriteList))).check(matches(isDisplayed()));
	}

	private void openCopyAction() {
		onView(withText(spriteList.get(0))).perform(click());
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.copy)).perform(click());
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		uniqueNameProvider = new UniqueNameProvider();

		Sprite standaloneSprite = new Sprite(spriteList.get(2));
		GroupSprite groupSprite = new GroupSprite(spriteList.get(0));
		Sprite sprite = new GroupItemSprite(spriteList.get(1));

		project.getDefaultScene().addSprite(standaloneSprite);
		project.getDefaultScene().addSprite(groupSprite);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}
}
