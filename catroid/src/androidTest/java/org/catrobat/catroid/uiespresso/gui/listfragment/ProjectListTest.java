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

package org.catrobat.catroid.uiespresso.gui.listfragment;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.data.ProjectInfo;
import org.catrobat.catroid.gui.activity.ProjectListActivity;
import org.catrobat.catroid.projecthandler.ProjectCreator;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ProjectListTest {

	List<ProjectInfo> projects = new ArrayList<>();

	@Rule
	public BaseActivityInstrumentationRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectListActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProjects();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testIfAllProjectsAreDisplayed() {
		for (ProjectInfo project : projects) {
			onView(withText(project.getName())).check(matches(isDisplayed()));
		}
	}

	@Test
	public void testDelete() {
		onView(withId(R.id.fragment)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
		onView(withId(R.id.btnDelete)).perform(click());

		onView(withText(projects.get(0).getName())).check(doesNotExist());
	}

	@Test
	public void testCopy() {
		onView(withId(R.id.fragment)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
		onView(withId(R.id.btnCopy)).perform(click());

		onView(withText(projects.get(0).getName())).check(matches(isDisplayed()));

		String expectedNameOfCopy = projects.get(0).getName() + " (1)";
		assertEquals(expectedNameOfCopy, projects.get(3).getName());
		onView(withText(expectedNameOfCopy)).check(matches(isDisplayed()));
	}

	private void createProjects() throws Exception {
		projects.add(ProjectCreator.createDefaultProject("Project 0", InstrumentationRegistry.getTargetContext()));
		projects.add(ProjectCreator.createDefaultProject("Project 1", InstrumentationRegistry.getTargetContext()));
		projects.add(ProjectCreator.createDefaultProject("Project 2", InstrumentationRegistry.getTargetContext()));
		projects.add(ProjectCreator.createDefaultProject("Project 3", InstrumentationRegistry.getTargetContext()));
	}
}
