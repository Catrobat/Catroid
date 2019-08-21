/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ProjectActivityTest {

	private static final String PROJECT_NAME = "projectName";

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class,
			ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), PROJECT_NAME);
		Sprite firstSprite = new SingleSprite("firstSprite");
		project.getDefaultScene().addSprite(firstSprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testLastUsedProjectPreference() {
		getDefaultSharedPreferences().edit()
				.remove(Constants.PREF_PROJECTNAME_KEY)
				.apply();

		baseActivityTestRule.launchActivity();
		baseActivityTestRule.getActivity().finish();
		InstrumentationRegistry.getInstrumentation().waitForIdleSync();

		String prefProjectName = getDefaultSharedPreferences()
				.getString(Constants.PREF_PROJECTNAME_KEY, null);
		assertEquals(PROJECT_NAME, prefProjectName);
	}

	private SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
	}
}
