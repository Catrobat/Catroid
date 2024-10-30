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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectListFragment;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SORT_PROJECTS_PREFERENCE_KEY;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SortProjectsTest {

	@Rule
	public BaseActivityTestRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectListActivity.class, true, false);

	private static final String PROJECT_A = "A";
	private static final String PROJECT_B = "B";
	private static final String PROJECT_C = "C";
	private static final String PROJECT_LOWER_B = "b";

	@Before
	public void setUp() throws Exception {
		createProject(PROJECT_B);
		createProject(PROJECT_A);
		createProject(PROJECT_LOWER_B);
		createProject(PROJECT_C);

		getDefaultSharedPreferences().edit()
				.putBoolean(SORT_PROJECTS_PREFERENCE_KEY, false)
				.apply();

		baseActivityTestRule.launchActivity(null);
	}

	@After
	public void tearDown() throws IOException {
		baseActivityTestRule.deleteAllProjects();

		getDefaultSharedPreferences().edit()
				.remove(SORT_PROJECTS_PREFERENCE_KEY)
				.apply();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void projectsListIsSortedTest() {
		String lastUsedProjectName = getLastUsedProject();

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.sort_projects)).perform(click());

		onRecyclerView().checkHasSortedOrder();

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.unsort_projects)).perform(click());

		onRecyclerView().atPosition(0)
				.perform(click());
		onView(withText(lastUsedProjectName))
				.check(matches(isDisplayed()));

		pressBack();

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.sort_projects)).check(matches(isDisplayed()));
	}

	private void createProject(String projectName) {
		Project project = UiTestUtils.createDefaultTestProject(projectName);
		XstreamSerializer.getInstance().saveProject(project);
	}

	private SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
	}

	private String getLastUsedProject() {
		List<ProjectData> items = new ArrayList<>();
		ProjectListFragment.getLocalProjectList(items);

		Collections.sort(items, (project1, project2) -> Long.compare(project2.getLastUsed(), project1.getLastUsed()));

		return items.get(0).getName();
	}
}
