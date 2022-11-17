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

package org.catrobat.catroid.uiespresso.ui.actionbar;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper.onActionMode;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ActionModeMergeTest {

	@Rule
	public BaseActivityTestRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectListActivity.class, true, false);

	private String firstProjectName = "firstProjectName";
	private String secondProjectName = "secondProjectName";
	private String thirdProjectName = "thirdProjectName";

	@Before
	public void setUp() throws Exception {
		createProject(firstProjectName);
		createProject(secondProjectName);
		createProject(thirdProjectName);

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void onlyTwoProjectsCheckedTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.merge)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItemClick();

		onRecyclerView().atPosition(1)
				.performCheckItemClick();

		onRecyclerView().atPosition(2)
				.performCheckItemClick();

		onRecyclerView().atPosition(0).onChildView(R.id.checkbox).check(matches(isChecked()));
		onRecyclerView().atPosition(1).onChildView(R.id.checkbox).check(matches(isChecked()));
		onRecyclerView().atPosition(2).onChildView(R.id.checkbox).check(matches(isNotChecked()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void checkMenuButtonTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.merge)).check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void actionModeMergeTitleTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		onView(withText(R.string.merge))
				.perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItemClick();
		onActionMode().checkTitleMatches(UiTestUtils.getResourcesString(R.string.merge) + " 1");

		onRecyclerView().atPosition(1)
				.performCheckItemClick();
		onActionMode().checkTitleMatches(UiTestUtils.getResourcesString(R.string.merge) + " 2");

		onRecyclerView().atPosition(0)
				.performCheckItemClick();
		onActionMode().checkTitleMatches(UiTestUtils.getResourcesString(R.string.merge) + " 1");

		onRecyclerView().atPosition(1)
				.performCheckItemClick();

		onActionMode().checkTitleMatches(UiTestUtils.getResourcesString(R.string.merge) + " 0");
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = new Sprite("firstSprite");

		Script script = new StartScript();
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
	}
}
